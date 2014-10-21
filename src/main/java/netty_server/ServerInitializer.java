package netty_server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import java.util.List;



/**
 *
 * @author KOleksander
 */
public class ServerInitializer extends ChannelInitializer<Channel> {  

    status.db.entity.Request request;

    @Override
    protected void initChannel(Channel ch) throws Exception {
        
        request = new status.db.entity.Request();
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("decoder", new HttpRequestDecoder(){
            
            @Override
            protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
                int readable = actualReadableBytes();
                super.decode(ctx, buffer, out);
                request.setReceived_bytes(readable);
            }
            
        });
        pipeline.addLast("encoder", new HttpResponseEncoder() {

            @Override
            protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
                super.encode(ctx, msg, out);
                int writable = 0;
                for (Object object : out) {
                    ByteBuf buf = (ByteBuf)object;
                    writable += buf.readableBytes();
                }
                request.setSend_bytes(writable);
            }
            
        });
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpObjectAggregator(64 * 1024));
        pipeline.addLast(new HttpHandler(request));
        
    } 

    
}