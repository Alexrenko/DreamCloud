package utils;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {

    private Command command;
    private String login;
    private String password;
    private ArrayList<String> fileNameList;
    private String currentDirectory;
    private int lineNumber;
    private Cargo cargo;

    public Message(Command command) {
        this.command = command;
    }

    public Message(Command command, String login, String password) {
        this.command = command;
        this.login = login;
        this.password = password;
    }

    public Message(Command command, int number, String login, String password) {
        this.command = command;
        this.lineNumber = number;
        this.login = login;
        this.password = password;
    }

    public Message(Command command, ArrayList<String> fileNameList, String currentDirectory ) {
        this.command = command;
        this.fileNameList = fileNameList;
        this.currentDirectory = currentDirectory;
    }

    public Command getCommand() {
        return command;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<String> getFileNameList() {
        return fileNameList;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setFileNameList(ArrayList<String> list) {
        this.fileNameList = list;
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public Cargo getCargo() {
        return cargo;
    }
}