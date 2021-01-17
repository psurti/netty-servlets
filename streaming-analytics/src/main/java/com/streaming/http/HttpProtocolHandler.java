package com.streaming.http;

import static io.netty.handler.codec.http.HttpHeaders.Names.CACHE_CONTROL;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static io.netty.handler.codec.http.HttpHeaders.Names.PRAGMA;
import static io.netty.handler.codec.http.HttpHeaders.Names.SERVER;
import static io.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.streaming.servlet.ServletWebApp;
import com.streaming.utils.ThreadLocals;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.ServerCookieEncoder;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable
public class HttpProtocolHandler extends IdleStateHandler {

	private static final Logger logger = LogManager.getLogger(HttpProtocolHandler.class);

    /** Buffer that stores the response content */
    private final StringBuilder buf = ThreadLocals.stringBuilder.get();

	private final ServletWebApp webApp;

    public HttpProtocolHandler(ServletWebApp webApp) {
		super(20000, 20000, 20000);
		this.webApp = webApp;
	}

    @Override
    public void channelActive(ChannelHandlerContext ctx)
            throws Exception {
        logger.info("Opening new channel: {}", ctx.channel());
        // add channel to sharedchannelgroup in webapp
        ctx.fireChannelActive();
    }

    @Override
    public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) {
        logger.info("Closing idle channel: {}", ctx.channel());
        ctx.channel().close();
    }

	@Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException, ServletException {

    	if (msg instanceof HttpRequest) {
            HttpRequest req  = (HttpRequest) msg;
            String uri = req.getUri();

            if (HttpHeaders.is100ContinueExpected(req)) {
                send100Continue(ctx);
            }

            ServletRequest httpReq = buildServletRequest(req);

            // Build the response object.
        	FullHttpResponse resp = new DefaultFullHttpResponse(
        			HTTP_1_1, req.getDecoderResult().isSuccess()? OK : HttpResponseStatus.BAD_REQUEST,
        					Unpooled.copiedBuffer(buf.toString(), CharsetUtil.UTF_8));


            ServletResponse httpResp = buildServletResponse(resp);

            webApp.run(uri, httpReq, httpResp);

            writeResponse(req, resp, ctx);

        } else {
        	ctx.fireChannelRead(msg);
        }
    }

    private ServletResponse buildServletResponse(FullHttpResponse resp) {
    	return new NettyHttpServletResponse(resp);
	}

	private ServletRequest buildServletRequest(HttpRequest req) {
    	return new NettyHttpServletRequest(req);
	}


    private boolean writeResponse(HttpRequest request, FullHttpResponse response,
    		ChannelHandlerContext ctx) {
    	// Decide whether to close the connection or not.
    	boolean keepAlive = HttpHeaders.isKeepAlive(request);

    	response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");

    	if (keepAlive) {
    		// Add 'Content-Length' header only for a keep-alive connection.
    		response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
    		// Add keep alive header as per:
    		// - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
    		response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
    	}

    	// Encode the cookie.
    	String cookieString = request.headers().get(COOKIE);
    	if (cookieString != null) {
    		Set<Cookie> cookies = CookieDecoder.decode(cookieString);
    		if (!cookies.isEmpty()) {
    			// Reset the cookies if necessary.
    			for (Cookie cookie: cookies) {
    				response.headers().add(SET_COOKIE, ServerCookieEncoder.encode(cookie));
    			}
    		}
    	} else {
    		// Browser sent no cookie.  Add some.
    		response.headers().add(SET_COOKIE, ServerCookieEncoder.encode("key1", "value1"));
    		response.headers().add(SET_COOKIE, ServerCookieEncoder.encode("key2", "value2"));
    	}

    	// Write the response.
    	ctx.write(response);

    	return keepAlive;
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
        ctx.write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	  logger.error("Unexpected exception from downstream.", cause);

          Channel ch = ctx.channel();
          if (cause instanceof IllegalArgumentException) {
              ch.close();
          } else {
              if (cause instanceof TooLongFrameException) {
                  sendError(ctx, BAD_REQUEST);
                  return;
              }

              if (ch.isActive()) {
                  sendError(ctx, INTERNAL_SERVER_ERROR);
              }

          }
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        String text = "Failure: " + status.toString() + "\r\n";
        ByteBuf byteBuf = Unpooled.buffer();
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeBytes(bytes);

        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, byteBuf);
        HttpHeaders headers = response.headers();

        headers.add(CONTENT_TYPE, "text/plain;charset=utf-8");
        headers.add(CACHE_CONTROL, "no-cache");
        headers.add(PRAGMA, "No-cache");
        headers.add(SERVER, "Netty Server");
        headers.add(CONTENT_LENGTH, byteBuf.readableBytes());
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
