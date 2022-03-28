package serverPac;

import io.netty.channel.ChannelHandlerContext;

import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private String login;
    private String password;
    private ChannelHandlerContext commChannel;
    private ChannelHandlerContext carrierChannel;
    private ArrayList<String> fileNameList;
    private ArrayList<Path> pathList;
    private Path currentDirectory;
    private Path root;

    public Client(ChannelHandlerContext commChannel, String login, String password) {
        this.commChannel = commChannel;
        this.login = login;
        this.password = password;
        fileNameList = new ArrayList<>();
        pathList = new ArrayList<>();
    }

    public ChannelHandlerContext getCommChannel() {
        return commChannel;
    }

    public ChannelHandlerContext getCarrierChannel() {
        return carrierChannel;
    }

    public void setCarrierChannel(ChannelHandlerContext carrierChannel) {
        this.carrierChannel = carrierChannel;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Path getRoot() {
        return root;
    }

    public void setRoot(Path path) {
        this.root = path;
    }

    public Path getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(Path currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public ArrayList<String> getFileNameList() {
        return fileNameList;
    }

    public void setFileNameList(List<Path> list) {
        fileNameList.clear();
        for(Path path : list)
            fileNameList.add(path.getFileName().toString());
    }

    public void addToFileNameList(String fileName) {
        fileNameList.add(fileName);
    }

    public void clearFileNameList() {
        fileNameList.clear();
    }

    public ArrayList<Path> getPathList() {
        return pathList;
    }

    public void setPathList(DirectoryStream<Path> paths) {
        clearPathList();
        for(Path path : paths)
            pathList.add(path);
    }

    public void addToPathList(Path path) {
        pathList.add(path);
    }

    public void clearPathList() {
        pathList.clear();
    }

}