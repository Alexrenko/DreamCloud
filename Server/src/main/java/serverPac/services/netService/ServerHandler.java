package serverPac.services.netService;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Готов к приему команд от клиента");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            System.out.println("что-то пришло");
            CommandMessage command = null;
            if(msg instanceof CommandMessage) command = (CommandMessage) msg;
            System.out.println("Получилось!");
            if (command.getCommand() == CommandList.AUTH)
                System.out.println("Пришел запрос на авторизацию");
        }
        finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
