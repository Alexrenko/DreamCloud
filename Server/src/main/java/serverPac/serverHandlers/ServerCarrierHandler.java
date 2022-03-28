package serverPac.serverHandlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import serverPac.Client;
import serverPac.Model;
import utils.Message;
import utils.TransportMessage;

public class ServerCarrierHandler extends ChannelInboundHandlerAdapter {
    Model model = Model.getModel();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Готов к приему файлов");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("что-то пришло в канал передачи файлов");
        Message message = null;
        if(msg instanceof Message) {
            message = (Message) msg;
            Client client = model.getClient(message.getLogin());

            switch (message.getCommand()) {
                case POST_CARRIER:
                    System.out.println("Пришла команда " + message.getCommand());
                    client.setCarrierChannel(ctx);
                    Thread.sleep(100);
                    break;
                case POST_FILE:
                    System.out.println("Пришла команда " + message.getCommand());
                    TransportMessage tMsg = (TransportMessage) message;
                    model.downloadFile(tMsg, client);
                    break;
            }
        }
    }
}
