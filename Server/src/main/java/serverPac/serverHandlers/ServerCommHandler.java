package serverPac.serverHandlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import serverPac.Client;
import serverPac.Model;
import utils.Command;
import utils.Message;

import javax.swing.*;

public class ServerCommHandler extends ChannelInboundHandlerAdapter {
    Model model = Model.getModel();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            System.out.println("Что-то пришло в канал передачи команд");
            Message message = null;
            Client client = null;

            if(msg instanceof Message)
                message = (Message) msg;

            switch (message.getCommand()) {
                case REG:
                    //запрос на регистрацию
                    System.out.println("Пришла комманда " + message.getCommand());
                    String newLogin = message.getLogin();
                    String newPassword = message.getPassword();

                    //если такой логин уже занят, то отправляем сообщение "логин занят"
                    boolean isTaken = model.getAuthService().isRegistered(newLogin);
                    if (isTaken)
                        model.getNetService().sendCommand(ctx, new Message(Command.LOGIN_TAKEN));

                    //если нет, то регистрируем нового клиента и отправляем сообщение "REG_OK"
                    else {
                        boolean isRegister = model.toRegister(newLogin, newPassword);
                        if (isRegister)
                            model.getNetService().sendCommand(ctx, new Message(Command.REG_OK));
                    }
                    break;
                case AUTH:
                    //проверяем логин и пароль, если подходят, то авторизуем клиента
                    System.out.println("Пришла комманда " + message.getCommand());
                    if (model.getAuthService().checkAuth(message.getLogin(), message.getPassword())) {
                        model.authorize(new Client(ctx, message.getLogin(), message.getPassword()));
                    }
                    break;
                case GET_FILENAME_LIST:
                    //отправить список файлов клиенту
                    System.out.println("Пришла комманда " + message.getCommand());
                    client = model.getClient(message.getLogin());
                    model.sendFileNameList(client);
                    break;
                case OPEN_DIRECTORY:
                    //открыть директорию и отправить список файлов клиенту
                    System.out.println("Пришла комманда " + message.getCommand());
                    client = model.getClient(message.getLogin());
                    model.openDirectory(client, message.getLineNumber());
                    model.sendFileNameList(client);
                    break;
                case UP_DIRECTORY:
                    //перейти выше и отправить список файлов клиенту
                    System.out.println("Пришла комманда " + message.getCommand());
                    client = model.getClient(message.getLogin());
                    model.upDirectory(client);
                    model.sendFileNameList(client);
                    break;
                case TEST:
                    System.out.println("TEST TEST TEST");
                    break;
                case DOWNLOAD_FILE:
                    //отправить файл клиенту
                    System.out.println("Пришла комманда " + message.getCommand());
                    client = model.getClient(message.getLogin());
                    model.sendFile(client, message.getLineNumber());
                    break;
                case GET_NEXT_PART:
                    System.out.println("Пришла комманда " + message.getCommand());
                    model.setCountDownLatch();
                    break;
                case DISCONNECT:
                    System.out.println("Пришла комманда " + message.getCommand());

                    break;
            }
        }
        finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
