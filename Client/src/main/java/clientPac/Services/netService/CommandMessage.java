package clientPac.Services.netService;

import java.io.Serializable;

public class CommandMessage implements Serializable {
    private CommandList command;
    private String login;
    private String password;
    private boolean isAuthorized;

    public CommandMessage(CommandList command) {
        this.command = command;
    }

    public CommandMessage(CommandList command, String login, String password) {
        this.command = command;
        this.login = login;
        this.password = password;
    }

    public CommandList getCommand() {
        return command;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }
}
