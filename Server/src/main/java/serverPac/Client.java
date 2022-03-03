package serverPac;

import io.netty.channel.socket.SocketChannel;

public class Client {
    private String login;
    private String password;
    private SocketChannel commChannel;
    private SocketChannel carrierChannel;

    public Client(String login, String password, SocketChannel commChannel) {
        this.login = login;
        this.password = password;
        this.commChannel = commChannel;
    }

    public void setCarrierChannel(SocketChannel carrierChannel) {
        this.carrierChannel = carrierChannel;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public SocketChannel getCommChannel() {
        return commChannel;
    }

    public SocketChannel getCarrierChannel() {
        return carrierChannel;
    }
}