package clientPac.handlers;

import clientPac.Model;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import utils.Message;
import utils.TransportMessage;

public class ClientCarrierHandler extends ChannelInboundHandlerAdapter {
    Model model = Model.getModel();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        model.sendCarrierChannel();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            System.out.println("Что-то пришло в канал передачи файлов клиента");
            //Пришло сообщение с файлом
            Message message = null;
            TransportMessage tMsg = null;

            if (msg instanceof TransportMessage) {
                System.out.println("Пришло транспортное сообщение");
                tMsg = (TransportMessage) msg;
                switch (tMsg.getCommand()) {
                    case FILE_FROM_SERVER:
                        model.getDownloadFileFromServer(tMsg);
                        break;
                }

                //Пришло информационное сообщение
            } else if (msg instanceof Message) {
                System.out.println("Пришло информационное сообщение");
                message = (Message) msg;
                switch (message.getCommand()) {
                    case FILE_FROM_SERVER:

                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
