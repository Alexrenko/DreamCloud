package clientPac;

import clientPac.Services.FileService;
import clientPac.Services.netService.CommandList;
import clientPac.Services.netService.CommandMessage;
import clientPac.Services.netService.NetService;
import io.netty.channel.ChannelHandlerContext;
import javafx.collections.ObservableList;

import java.io.File;
import java.nio.file.Path;

public class Model {

    private static Model model;
    NetService netService = new NetService();
    FileService fileService = new FileService();
    private boolean isAuthorized = false;

    private Model() {}

    public static Model getModel() {
        if (model == null)
            model = new Model();
        return model;
    }

    public NetService getNetService() {
        return netService;
    }

    public FileService getFileService() {
        return fileService;
    }

    public void authorization(String login, String password) {
        netService.sendCommand(new CommandMessage(CommandList.AUTH, login, password));
        //netService.sendCommand(new CommandMessage(CommandList.AUTH, "login", "password"));
        //System.out.println("Отправлен запрос на авторизацию. Логин: " + login + ", Пароль: " + password);
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public ObservableList<String> getFileNameList() {
        return fileService.getFileNameList();
    }

    public boolean isPossibleToCreate() {
        return fileService.isPossibleToCreate();
    }

    public void showRoots() {
        fileService.showRoots();
    }

    public void setCurrentDirectory(File directory) {
        fileService.setCurrentDirectory(directory);
    }

    public String getNameCurrentDirectory() {
        return fileService.getNameCurrentDirectory();
    }

    public void createDirectory(String name) {
        fileService.createDirectory(name);
    }

    public void openDirectory(int index){
        fileService.openDirectory(index);
    }

    public void backToUpDirectory() {
        fileService.backToUpDirectory();
    }

    public void updateCurrentFileList(Path path) {
        fileService.updateCurrentFileList(path);
    }

    public void updateFileNameList(ObservableList<File> files) {
        fileService.updateFileNameList(files);
    }

    public void startChannels(String host, int commandPORT, int carrierPORT) {
        netService.startCommChannel(host, commandPORT);

        //netService.startCarrierChannel(host, carrierPORT);
        //netService.sendCommand(CommandList.COMMAND1);
    }

}
