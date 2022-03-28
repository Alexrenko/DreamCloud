package clientPac.handlers;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import clientPac.Model;
import io.netty.util.ReferenceCountUtil;
import javafx.scene.paint.Color;
import utils.Message;

public class ClientCommHandler extends ChannelInboundHandlerAdapter {

    Model model;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        model = Model.getModel();
        System.out.println("Установлено соединение с сервером");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        try {
            Message message = null;
            if (msg instanceof Message) {
                message = (Message) msg;
                switch (message.getCommand()) {
                    case REG_OK:
                        System.out.println("Пришла комманда " + message.getCommand());
                        model.setRegLabelColor(Color.GREEN);
                        model.setRegLabelText("Логин зарегистрирован");
                        break;
                    case LOGIN_TAKEN:
                        model.setRegLabelColor(Color.RED);
                        model.setRegLabelText("Логин занят");
                        System.out.println("Пришла комманда " + message.getCommand());
                        break;
                    case AUTH_OK:
                        System.out.println("Пришла комманда " + message.getCommand());
                        model.setAuthorized(true);
                        model.getFileNameListFromServer();
                        break;
                    case HELLO:
                        System.out.println("Пришла комманда " + message.getCommand());
                        break;
                    case POST_FILENAME_LIST:
                        System.out.println("Пришла комманда " + message.getCommand());
                        model.updateNetNameList(message.getFileNameList());
                        model.setCurrentDirectory(message.getCurrentDirectory());
                        break;
                    case GET_NEXT_PART:
                        System.out.println("Пришла комманда " + message.getCommand());
                        model.setCountDownLatch();
                        break;
                    case COMMAND2:
                        System.out.println("Command 2");
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
