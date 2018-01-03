/**
 *
 */
package com.streaming;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
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

	private final SslContext sslCtx;
	private final boolean detectSsl;

	/**
	 * Constructor
	 */
	public MultiProtocolServerHandler(SslContext sslCtx) {
		this(sslCtx, true);
	}

	/**
	 * Constructor with parameters
	 *
	 * @param sslCtx
	 * @param detectSsl
	 */
	private MultiProtocolServerHandler(SslContext sslCtx, boolean detectSsl) {
		this.sslCtx = sslCtx;
		this.detectSsl = detectSsl;
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

			boolean isHttp = isHttp(magic1, magic2);
			System.out.println( "Detected HTTP:" + isHttp);
			if (isHttp)
				switchToHttp(ctx);


			//Read the data
			System.out.println("");
			while(in.isReadable()) {
				System.out.print((char)in.readByte());
			}
			System.out.println("Done.");

			/*
            System.out.println( "response:");
            for (int i = 0; i < 100; i++ )  {
            	System.out.print( (char)magic);
            }
            */
		}

		in.clear();
		ctx.close();

	}

	/**
	 * enable ssl
	 *
	 * @param ctx
	 */
	private void enableSsl(ChannelHandlerContext ctx) {
        ChannelPipeline p = ctx.pipeline();
        p.addLast("ssl", sslCtx.newHandler(ctx.alloc()));
        p.addLast("mpHandler", new MultiProtocolServerHandler(sslCtx, false));
        p.remove(this);
    }

	/**
	 * is Ssl
	 * @param buf
	 * @return
	 */

	private boolean isSsl(ByteBuf buf) {
		if (detectSsl) {
			return SslHandler.isEncrypted(buf);
		}
		return false;
	}

    private static boolean isHttp(int magic1, int magic2) {
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



    private void switchToHttp(ChannelHandlerContext ctx) {
        ChannelPipeline p = ctx.pipeline();
        p.addLast("decoder", new HttpRequestDecoder());
        p.addLast("encoder", new HttpResponseEncoder());
        p.addLast("deflater", new HttpContentCompressor());
        p.addLast("handler", new HttpSnoopServerHandler());
        p.remove(this);

    }



}
