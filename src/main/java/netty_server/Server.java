package netty_server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.net.InetSocketAddress;


/**
 *
 * @author KOleksander
 */
public class Server {

    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Channel channel;
    private static int currentConnections = 0;
    private static final int SERVER_PORT = 8787;
    
    
    
    
    public static void main(String[] args) throws Exception {
        final Server server = new Server();
        ChannelFuture future = server.start(new InetSocketAddress(SERVER_PORT)); 
        System.out.println("Sever established...");
            Runtime.getRuntime().addShutdownHook(new Thread() { 
                @Override 
                public void run() { 
                    server.destroy(); 
                } 
            }); 
        future.channel().closeFuture().syncUninterruptibly(); 
    }
    
    

    public ChannelFuture start(InetSocketAddress address) {
        ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class) 
                .childHandler(createInitializer());
            ChannelFuture future = bootstrap.bind(address);
        future.syncUninterruptibly(); 
        channel = future.channel();
        return future;
    }
    
    public static void newConnection() {
        currentConnections++;
    }
    
    public static void removeConnection() {
        currentConnections--;
    }
    
    public static long getCurrentConnection() {
        return currentConnections;
    }
    
    public void destroy() {
        if (channel != null) { 
            channel.close(); 
        } 
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully(); 
    } 
    
    protected ChannelInitializer<Channel> createInitializer() {
        return new ServerInitializer();
    } 

    
    
}
