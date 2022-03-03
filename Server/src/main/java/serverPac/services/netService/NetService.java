package serverPac.services.netService;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NetService {
    SocketChannel commandChannel;
    SocketChannel carrierChannel;

    public void startCommServer(int commandPort) {
        Thread commServer = new Thread(() -> {
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            try {
                ServerBootstrap boot = new ServerBootstrap();
                boot.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                commandChannel = socketChannel;
                                socketChannel.pipeline().addLast(
                                        new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                        new ObjectEncoder(),
                                        new ServerHandler());
                                        //new StringDecoder(),
                                        //new StringEncoder(),
                                        //new ServerHandler());
                            }
                        });

                ChannelFuture f = boot.bind(commandPort).sync();
                System.out.println("Сервер запущен");
                f.channel().closeFuture().sync();
            } catch (Exception e) {
                System.out.println("SWW with command server");
                e.printStackTrace();
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        });
        commServer.start();
    }

    /*
    public void startCarrierServer(int carrierPort) {
        Thread carrierServer = new Thread(() -> {
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            try {
                ServerBootstrap boot = new ServerBootstrap();
                boot.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                carrierChannel = socketChannel;
                                ChannelPipeline pLine = socketChannel.pipeline();
                                pLine.addLast(new HttpRequestDecoder());
                                pLine.addLast(new HttpResponseEncoder());
                                pLine.addLast(new HttpServerHandler());
                            }
                        });

                ChannelFuture f = boot.bind(carrierPort).sync();
                f.channel().closeFuture().sync();
            } catch (Exception e) {
                System.out.println("SWW with carrier server");
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        });
        carrierServer.start();
    }

     */

}
