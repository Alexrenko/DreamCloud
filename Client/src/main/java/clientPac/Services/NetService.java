package clientPac.Services;

import clientPac.handlers.ClientCarrierHandler;
import clientPac.handlers.ClientCommHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import utils.Command;
import utils.Message;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class NetService {
    private SocketChannel commChannel;
    private SocketChannel carrierChannel;
    private String host;
    private int commPort;
    private int carrierPort;

    public NetService() {
        //Инициализируем путь к конфигурационному файлу
        String strPath = getClass().getResource("/").getPath().substring(1);
        strPath += "config.cfg";
        Path cfgPath = Paths.get(strPath);

        //если config.cfg не существует, то создаем с параметрами по умолчанию
        try {
            if (!Files.exists(cfgPath)) {
                String parameters = String.format("%s%n%s%n%s", "127.0.0.1", "8188", "8189");
                Files.write(cfgPath, parameters.getBytes(), StandardOpenOption.CREATE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Считываем параметры подключения из конфигурационного файла
        try {
            List<String> param = Files.readAllLines(cfgPath);
            host = param.get(0);
            commPort = Integer.parseInt(param.get(1));
            carrierPort = Integer.parseInt(param.get(2));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public SocketChannel getCommChannel() {
        return commChannel;
    }

    public SocketChannel getCarrierChannel() {
        return carrierChannel;
    }

    public void sendCommand(Message msg) {
        commChannel.writeAndFlush(msg);
        System.out.println("Отправлена команда " + msg.getCommand());
    }

    public void sendCargo(Message msg) {
        carrierChannel.writeAndFlush(msg);
        System.out.println("Отправлена команда " + msg.getCommand());
    }

    public void startCommChannel() {
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
                                        new ObjectEncoder(),
                                        new ObjectDecoder(50 * 1024 * 1024, ClassResolvers.cacheDisabled(null)),
                                        new ClientCommHandler());
                            }
                        });
                ChannelFuture future = b.connect(host, commPort).sync();
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


    public void startCarrierChannel() {
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
                                        new ObjectEncoder(),
                                        new ObjectDecoder(50 * 1024 * 1024, ClassResolvers.cacheDisabled(null)),
                                        new ClientCarrierHandler());
                            }
                        });
                ChannelFuture future = b.connect(host, carrierPort).sync();
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

    public void disconnect() {
        sendCommand(new Message(Command.DISCONNECT));
    }

}
