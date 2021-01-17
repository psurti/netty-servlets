/**
 *
 */
package com.streaming;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.streaming.http.HttpProtocolHandler;
import com.streaming.servlet.ServletWebApp;
import com.streaming.utils.ThreadLocals;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

/**
 * @author psurti
 *
 */
public class MultiProtocolServerHandler extends ByteToMessageDecoder {

	private static final Logger log = LogManager.getLogger(MultiProtocolServerHandler.class);
	private ThreadLocal<StringBuilder> strBuilder =  new ThreadLocal<StringBuilder>() {
		@Override
		protected StringBuilder initialValue() {
			return new StringBuilder();
		}

		@Override
		public StringBuilder get() {
			StringBuilder b = super.get();
			b.setLength(0); // clear/reset the buffer
			return b;
		}

	};

	private final SslContext sslCtx;

	private final boolean detectSsl;
	private final boolean detectGzip;
	private final ServletWebApp webApp;

	/**
	 * Constructor
	 */
	public MultiProtocolServerHandler(SslContext sslCtx, ServletWebApp webApp) {
		this(sslCtx, webApp, true, true);
	}

	/**
	 * Constructor with parameters
	 *
	 * @param sslCtx
	 * @param detectSsl
	 */
	private MultiProtocolServerHandler(SslContext sslCtx, ServletWebApp webApp, boolean detectSsl, boolean detectGzip) {
		this.sslCtx = sslCtx;
		this.detectSsl = detectSsl;
		this.detectGzip = detectGzip;
		this.webApp = webApp;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
			throws Exception {
		if (in.readableBytes() < 5) {
			return;
		}


		if (isSsl(in)) {
			enableSsl(ctx);
		} else {
			//auto-detect protocol
			int idx = in.readerIndex();
			final int magic1 = in.getUnsignedByte(idx);
			final int magic2 = in.getUnsignedByte(idx + 1);
			final int magic3 = in.getUnsignedByte(idx + 2);
			final int magic4 = in.getUnsignedByte(idx + 3);
			byte[] data = new byte[] { (byte)magic1, (byte)magic2, (byte)magic3, (byte)magic4 };


			/*
			 * Detect Protocol:
			 *  isHttpProtocol()
			 *  isJMSProtocol()
			 *  isSocketProtocol():default
			 */
			if (isGzip(magic1, magic2)) {
				enableGzip(ctx);
			} else if (isHttp(magic1, magic2)) {
				log.info( "Detected HTTP");
				switchToHttp(ctx);
			} else if (isJavaSerializable(data)) {
				log.info( "Detected Java Object");
				switchToJavaCall(ctx);
			} else {
				log.info( "Unknown format" );
				dump(in);
			}
		}

		in.clear();
		ctx.close();

	}

	private void dump(ByteBuf in) {
		log.info("");
		StringBuilder sb = strBuilder.get();

		//Read the data
		while(in.isReadable()) {
			sb.append((char)in.readByte());
		}
		log.info(sb.toString());
		log.info("Done.");
		/**
        System.out.println( "response:")
        for (int i = 0; i < 100; i++ )  {*
        	\\System.out.print( (char)magic;
        }
		*/
	}

	/**
	 * enable ssl
	 *
	 * @param ctx
	 */
	private void enableSsl(ChannelHandlerContext ctx) {
        ChannelPipeline p = ctx.pipeline();
        p.addLast("ssl", sslCtx.newHandler(ctx.alloc())); //out-bound
        p.addLast("mpHandler", new MultiProtocolServerHandler(sslCtx, webApp, false, detectGzip)); //in-bound
        p.remove(this);
    }

	private void enableGzip(ChannelHandlerContext ctx) {
		ChannelPipeline p = ctx.pipeline();
		p.addLast("gzipdeflater", ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP)); //out-bound
		p.addLast("gzipinflater", ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP)); //in-bound
		p.addLast("unificationB", new MultiProtocolServerHandler(sslCtx, webApp, detectSsl, false)); //in-bound
		p.remove(this);
	}

	private void switchToHttp(ChannelHandlerContext ctx) {
		ChannelPipeline p = ctx.pipeline();
		p.addLast("decoder", new HttpRequestDecoder()); //in-bound adapter
		p.addLast("encoder", new HttpResponseEncoder()); //out-bound adapter
		p.addLast("deflater", new HttpContentCompressor());//in-bound AND out-bound adapter
		//p.addLast("handler", new HttpSnoopServerHandler(webApp)); //in-bound
		p.addLast("handler", new HttpProtocolHandler(webApp)); //in-bound
		p.remove(this);

	}

	private void switchToJavaCall(ChannelHandlerContext ctx) {
		ChannelPipeline p = ctx.pipeline();

		//p.addLast("handler", new HttpJavaServerHandler());
		p.remove(this);
	}

	private boolean isGzip(int magic1, int magic2) {
		if (detectGzip) {
			return magic1 == 31 && magic2 == 139;
		}
		return false;
	}

	private boolean isSsl(ByteBuf buf) {
		if (detectSsl) {
			return SslHandler.isEncrypted(buf);
		}
		return false;
	}

    private boolean isHttp(int magic1, int magic2) {
        return
            magic1 == 'G' && magic2 == 'E' || // GET
            magic1 == 'P' && magic2 == 'O' || // POST
            magic1 == 'P' && magic2 == 'U' || // PUT
            magic1 == 'H' && magic2 == 'E' || // HEAD
            magic1 == 'O' && magic2 == 'P' || // OPTIONS
            magic1 == 'P' && magic2 == 'A' || // PATCH
            magic1 == 'D' && magic2 == 'E' || // DELETE
            magic1 == 'T' && magic2 == 'R' || // TRACE
            magic1 == 'C' && magic2 == 'O';   // CONNECT
    }

	private boolean isJavaSerializable(byte[] data) {
    	char[] ret = toHex(data, 2);
    	if (ret == null || ret.length < 4) return false;
    	return (ret[0] == 'a' && ret[1] == 'c' && ret[2] == 'e' && ret[3] == 'd');
    }

    private static final String digits = "0123456789abcdef";
	private char[] toHex(byte[] data, int length){
		length = (length > 0) ? length : data.length;
		StringBuilder buf = ThreadLocals.stringBuilder.get();
		for (int i = 0; i != length; i++)	{
			int v = data[i] & 0xff;
			buf.append(digits.charAt(v >> 4));
			buf.append(digits.charAt(v & 0xf));
		}
		return buf.toString().toCharArray();
	}

}
