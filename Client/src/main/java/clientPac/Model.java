package clientPac;

import clientPac.Services.FileService;
import clientPac.Services.NetService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import utils.Command;
import utils.Message;
import utils.TransportMessage;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


public class Model {

    private static Model model;
    private NetService netService = new NetService();
    private FileService fileService = new FileService();
    private boolean isAuthorized = false;
    private String login;
    private String password;

    private ObservableList<String> netNameList = FXCollections.observableArrayList();
    private String currentDirectory;

    private CountDownLatch countDownLatch;
    Color regLabelColor = Color.GREEN;
    String regLabelText = "";

    private Model() {}

    public static Model getModel() {

        if (model == null)
            model = new Model();
        return model;
    }

    public void sendAuth(String login, String password) {
        netService.sendCommand(new Message(Command.AUTH, login, password));
        this.login = login;
        this.password = password;
    }

    public ObservableList<String> getNetNameList() {
        ObservableList<String> copy = FXCollections.observableArrayList();
        copy.addAll(netNameList);
        return copy;
    }

    public NetService getNetService() {
        return netService;
    }

    public FileService getFileService() {
        return fileService;
    }

    public void updateNetNameList(ArrayList<String> list) {
        netNameList.clear();
        netNameList.addAll(list);
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public void addToNetNameList(String s) {
        netNameList.add(s);
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void sendCarrierChannel() {
        netService.sendCargo(new Message(Command.POST_CARRIER, login, password));
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void setCountDownLatch() {
        if (countDownLatch != null) {
            countDownLatch.countDown();
            System.out.println("Защелка сброшена");
        }
    }

    public Color getRegLabelColor() {
        return regLabelColor;
    }

    public void setRegLabelColor(Color regLabelColor) {
        this.regLabelColor = regLabelColor;
    }

    public String getRegLabelText() {
        return regLabelText;
    }

    public void setRegLabelText(String regLabelText) {
        this.regLabelText = regLabelText;
    }

    public void sendFile(int selectedIndex) {
        try {
            Path path = fileService.getCurrentFileList().get(selectedIndex);
            String fileName = path.getFileName().toString();
            int partSize = 1000000;             //размер одного фрагмента
            long fileSize = Files.size(path);   //размер файла

            if (fileSize < partSize)
                sendShortFile(path, fileName, partSize);
            else
                sendLongFile(path, fileName, partSize, fileSize);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendShortFile(Path path, String fileName, int piece) throws IOException {
        byte[] data = Files.readAllBytes(path);
        netService.sendCargo(new TransportMessage(
                Command.POST_FILE, login, password, //комманда и аутентификация
                data,                               //очередная часть файла
                1,                                  //количество частей файла
                0,                                  //счетчик частей файла
                piece,                              //величина одной части
                data.length,                        //количество байт последней части
                fileName                            //имя файла
        ));
    }

    private void sendLongFile(Path path, String fileName, int partSize, long fileSize)
            throws IOException, InterruptedException
    {
        RandomAccessFile raf = new RandomAccessFile(path.toString(), "r");
        byte[] data;                            //байтовый массив
        int parts = (int)fileSize/partSize;     //количество полных частей
        if (fileSize % partSize != 0) parts++;  //увеличиваем количество частей, если есть остаток
        int lastPiece = 0;                      //количество байт в последней части
        System.out.println("Размер файла: " + fileSize);
        System.out.println("Количество частей " + parts);
        System.out.println("");

        for (int i = 0; i < parts; i++) {

            System.out.println("Count: " + i);
            //рассчитываем последнюю часть файла
            lastPiece = (int)(fileSize - (parts - 1) * partSize);

            //если это последняя часть файла, то создаем байтовый массив подходящего размера
            boolean isLastPart = (i == parts - 1) && (fileSize % partSize != 0);
            //data = isLastPart ? new byte[lastPiece] : new byte[partSize];
            if (isLastPart) {
                data = new byte[lastPiece];
                System.out.println("Пришел последний кусок");
                System.out.println("lastPiece: " + lastPiece);
            } else {
                data = new byte[partSize];
            }

            //счимываем часть файла
            raf.readFully(data, 0, data.length);

            //отправляем часть файла на сервер
            netService.sendCargo(new TransportMessage(
                    Command.POST_FILE, login, password, //команда и аутентификация
                    data,                               //очередная часть файла
                    parts,                              //количество частей файла
                    i,                                  //счетчик частей файла
                    partSize,                           //величина одной части
                    lastPiece,                          //количество байт последней части
                    fileName                            //имя файла
            ));

            //Ждем ответа от сервера об окончании записи фрагмента
            countDownLatch = new CountDownLatch(1);
            countDownLatch.await();
        }
        raf.close();
    }

    public void getRootFromServer() {
        netService.sendCommand(new Message(Command.GET_ROOT, login, password));
    }

    public void getFileNameListFromServer() {
        netService.sendCommand(new Message(Command.GET_FILENAME_LIST, login, password));
    }

    public void openDirectoryFromServer(int selectedIndex) {
        netService.sendCommand(new Message(Command.OPEN_DIRECTORY, selectedIndex, login, password));
    }

    public void upDirectoryFromServer() {
        netService.sendCommand(new Message(Command.UP_DIRECTORY, login, password));
    }


    public void downloadFileFromServer(int selectedIndex) {
        Message message = new Message(Command.DOWNLOAD_FILE, selectedIndex, login, password);
        netService.sendCommand(message);
    }

    public void getDownloadFileFromServer(TransportMessage msg) throws IOException {

        //создаем путь для записи
        String strPath = fileService.getCurrentDirectory().toString();
        String strFullPath = strPath + "\\" + msg.getFileName();
        Path path = Paths.get(strFullPath);

        //если это первый пакет передачи, то создаем файл
        //если существует, то удаляем и создаем заново
        if (msg.getCount() == 0 ) {
            if (Files.exists(path))
                Files.delete(path);
            Files.write(path, msg.getBytes(), StandardOpenOption.CREATE);
            Message message = new Message(Command.GET_NEXT_PART, login, password);
            netService.sendCommand(message);
            netService.sendCommand(new Message(Command.TEST, login, password));

        //если это не первый пакет, то просто записываем и запрашиваем следующую часть
        } else {
            Files.write(path, msg.getBytes(), StandardOpenOption.APPEND);;
            netService.sendCommand(new Message(Command.GET_NEXT_PART, login, password));
        }
        //обновляем локальный файл-лист после копирования файла
        if (msg.getCount() == msg.getTotalAmount() - 1) {
            Path currentPath = fileService.getCurrentDirectory().toPath();
            if (currentPath.toString().equals(strPath)) {
                fileService.updateLists();
            }
        }
    }
}
