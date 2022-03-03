package clientPac.Services.netService;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import clientPac.Model;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    Model model;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        model = Model.getModel();
        System.out.println("Установлено соединение с сервером");
        model.getNetService().sendCommand(new CommandMessage(CommandList.AUTH, "login", "password"));
        //Object obj = new Object();
        //ctx.writeAndFlush(obj);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //CommandMessage inboundCommand = null;
        //if (msg instanceof CommandMessage)
        //    inboundCommand = (CommandMessage) msg;
        //if (inboundCommand.getCommand() == CommandList.AUTH) {
        //    model.setAuthorized(inboundCommand.isAuthorized());
        //}
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
