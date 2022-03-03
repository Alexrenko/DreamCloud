package clientPac.Services.netService;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NetService {
    SocketChannel commChannel;
    SocketChannel carrierChannel;

    public SocketChannel getCommChannel() {
        return commChannel;
    }

    public SocketChannel getCarrierChannel() {
        return carrierChannel;
    }

    public void sendCommand(CommandMessage command) {
        commChannel.writeAndFlush(command);
        //commChannel.writeAndFlush("Проверочная строка");
    }

    public void startCommChannel(String host, int port) {
        Thread commChannelThread  = new Thread(() -> {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                commChannel = socketChannel;
                                socketChannel.pipeline().addLast(
                                        new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                        new ObjectEncoder(),
                                        new ClientHandler());
                                        //new StringDecoder(),
                                        //new StringEncoder());
                            }
                        });
                ChannelFuture future = b.connect(host, port).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                System.out.println("SWW with command channel");
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        });
        commChannelThread.setDaemon(true);
        commChannelThread.start();
    }

    /*
    public void startCarrierChannel(String host, int port) {
        Thread CarrierChannelThread  = new Thread(() -> {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                carrierChannel = socketChannel;
                                socketChannel.pipeline().addLast(
                                        new StringDecoder(),
                                        new StringEncoder());
                            }
                        });
                ChannelFuture future = b.connect(host, port).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                System.out.println("SWW with carrier channel");
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        });
        CarrierChannelThread.setDaemon(true);
        CarrierChannelThread.start();
    }

     */

}
