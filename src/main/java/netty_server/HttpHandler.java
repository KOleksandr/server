package netty_server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.LOCATION;
import io.netty.handler.codec.http.HttpResponseStatus;
import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.util.CharsetUtil;
import status.db.DBHandler;
import status.db.RequestDAO;
import status.db.entity.IpRequestCount;
import status.db.entity.Redirections;
/**
 *
 * @author KOleksander
 */
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final status.db.entity.Request requestEntity;
    private String getUri;
    private String address;
    private final RequestDAO dao;
    public HttpHandler(status.db.entity.Request request) throws InterruptedException {
        this.dao = DBHandler.factoryInstance();
        requestEntity = request;
        
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        long timeStart = System.currentTimeMillis();
        getUri = request.getUri();
        address = transformIp(ctx.channel().remoteAddress().toString());
        Server.newConnection();
        requestEntity.setUri(getUri);
        requestEntity.setAddress(address);
        
        if (getUri.equals("/hello")) {
            sendHello(ctx, 10000);
        } else if (getUri.startsWith("/redirect?url=")) {
            sendRedirect(ctx, extractUrl(getUri));
        } else if (getUri.equals("/status")) {
            sendStatus(ctx, dao);
        } else {
            sendError(ctx, FOUND);
        }
        Server.removeConnection();
        requestEntity.setSpeed((requestEntity.getReceived_bytes() + requestEntity.getSend_bytes())/((double)(System.currentTimeMillis() - timeStart)));
        dao.request(requestEntity);
        dao.closeConnection();
        DBHandler.end();
        
    }
    
    public static String transformIp(String ipWithPort) {
        ipWithPort = ipWithPort.substring(0, ipWithPort.lastIndexOf(":"));
        return ipWithPort;
    }
    
    public static String extractUrl(String url) {
        url = url.substring(url.indexOf("=") + 1, url.length());
        return url;
    }
    
    private static void sendHello(ChannelHandlerContext ctx, int time) throws InterruptedException {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8"); 
        StringBuilder buf = new StringBuilder();
            buf.append("<!DOCTYPE html>\r\n");
            buf.append("<html><head><title>");
            buf.append("/hello");
            buf.append("</title></head><body>\r\n");
            buf.append("<h1>Hello world!<h1>\r\n");
            buf.append("</ul></body></html>\r\n");
        ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
        response.content().writeBytes(buffer);
        buffer.release();
        Thread.sleep(time);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    
    private static void sendRedirect(ChannelHandlerContext ctx, String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
        response.headers().set(LOCATION, newUri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        
    }
    
    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer("Failure url: " + status + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    
    private static void sendStatus(ChannelHandlerContext ctx, RequestDAO dao) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");
        StringBuilder buf = new StringBuilder();
            buf.append("<!DOCTYPE html>\r\n");
            buf.append("<html><head><title>");
            buf.append("/status");
            buf.append("</title></head>\r\n");
            buf.append("<body>\n" +
    "        <div>\n" +
    "            <h3>Общее количество запросов: " + dao.countAllRequest() + "</h3>\n" +
    "            <h3>Количество уникальных запросов (по одному на IP): " + dao.countUniqueRequestsByIp() + "</h3>\n" +
    "            <br/>\n" +
    "            <h3>Счетчик запросов на каждый IP</h3>\n" +
    "            <table border=\"1\">\n" +
    "                <thead>\n" +
    "                    <tr>\n" +
    "                        <th>IP:</th>\n" +
    "                        <th>Количество запросов:</th>\n" +
    "                        <th>Время последнего запроса:</th>\n" +
    "                    </tr>\n" +
    "                </thead>\n" +
    "                <tbody>\n");
        for (IpRequestCount it :dao.getIpRequestCounter()) {
            buf.append("<tr>\n" +
    "                        <td>" + it.getAddress() + "</td>\n" +
    "                        <td>" + it.getRequests() + "</td>\n" +
    "                        <td>" + it.getTimestamp() + "</td>\n" +
    "                    </tr>\n");
        }
            buf.append("</tbody>\n" +
    "                \n" +
    "            </table>\n" +
    "            \n" +
    "            \n" +
    "            <h3>Количество переадресаций по url'ам</h3>\n" +
    "            <table border=\"1\">\n" +
    "                <thead>\n" +
    "                    <tr>\n" +
    "                        <th>URL:</th>\n" +
    "                        <th>Количество переадресаций:</th>\n" +
    "                    </tr>\n" +
    "                </thead>\n" +
    "                <tbody>\n");
        for (Redirections it :dao.getRedirections()) {
            buf.append("<tr>\n" +
    "                        <td>" + it.getRedirectUrl() + "</td>\n" +
    "                        <td>" + it.getCountRedirect() + "</td>\n" +
    "                       </tr>\n");
        }
        buf.append("</tbody>\n" +
    "            </table>\n" +
    "            <h3>Количество соединений, открытых в данный момент: " + Server.getCurrentConnection() + "</h3>\n" +
    "            <br/>\n" +
    "            <h3>16 последних обработанных соединений</h3>\n" +
    "            <table border=\"1\">\n" +
    "                <thead>\n" +
    "                    <tr>\n" +
    "                        <th>Src_ip:</th>\n" +
    "                        <th>URI:</th>\n" +
    "                        <th>Timestamp:</th>\n" +
    "                        <th>Send_bytes:</th>\n" +
    "                        <th>Received_bytes:</th>\n" +
    "                        <th>Speed (kBytes/sec):</th>\n" +
    "                    </tr>\n" +
    "                </thead>\n" +
    "                <tbody>\n");
        for (status.db.entity.Request it :dao.getLastNumberLog(16)) {
            buf.append("<tr>\n" +
    "                        <th> " + it.getAddress() + " </th>\n" +
    "                        <th>" + it.getUri() + "</th>\n" +
    "                        <th>" + it.getTimestamp() + "</th>\n" +
    "                        <th>" + it.getSend_bytes() + "</th>\n" +
    "                        <th>" + it.getReceived_bytes() + "</th>\n" +
    "                        <th>" + it.getSpeed() + " kByte/s</th>\n" +
    "                       </tr>\n");
        }
            buf.append("</tbody>\n" +
    "            </table>\n" +
    "        </div>\n" +
    "    </body></html>\r\n");
            ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
            response.content().writeBytes(buffer);
        buffer.release();
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}






















  
  