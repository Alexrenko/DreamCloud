package HTTP;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpServerHandler extends SimpleChannelInboundHandler {

    private HttpRequest request;
    StringBuilder responseData = new StringBuilder();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        if (o instanceof HttpRequest) {
            request = (HttpRequest) o;
            if (HttpUtil.is100ContinueExpected(request)) {
                //отправляем ответ со статусом "продолжить"
                writeResponse(channelHandlerContext);
            }
            responseData.setLength(0);
            //обрабатываем запрос
            responseData.append(RequestUtils.formatParams(request));
        }
        //responseData.append(RequestUtils.evaluateDecoderResult(request));
        if (o instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) o;
            responseData.append(RequestUtils.formatBody(httpContent));
            //responseData.append(RequestUtils.evaluateDecoderResult(request));

            if (o instanceof LastHttpContent) {
                LastHttpContent trailer = (LastHttpContent) o;
                responseData.append(RequestUtils.prepareLastResponse(request, trailer));
                writeResponse(channelHandlerContext, trailer, responseData);
            }
        }
    }

    private void writeResponse(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE,
                Unpooled.EMPTY_BUFFER);
        ctx.write(response);
    }

    private void writeResponse(ChannelHandlerContext ctx, LastHttpContent trailer, StringBuilder responseData) {
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1,
                ((HttpObject) trailer).decoderResult().isSuccess() ? OK : BAD_REQUEST,
                Unpooled.copiedBuffer(responseData.toString(), CharsetUtil.UTF_8));

        httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        if (keepAlive) {
            httpResponse.headers().setInt(HttpHeaderNames.CONTENT_LENGTH,
                    httpResponse.content().readableBytes());
            httpResponse.headers().set(HttpHeaderNames.CONNECTION,
                    HttpHeaderValues.KEEP_ALIVE);
        }
        ctx.write(httpResponse);

        if (!keepAlive) {
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
