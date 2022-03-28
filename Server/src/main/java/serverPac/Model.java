package serverPac;

import com.sun.javaws.IconUtil;
import io.netty.channel.ChannelHandlerContext;
import serverPac.services.AuthService;
import serverPac.services.NetService;
import utils.Command;
import utils.Message;
import utils.TransportMessage;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class Model {
    private static Model model;
    private NetService netService = new NetService();
    private AuthService authService = new AuthService();
    private static final ArrayList<Client> authorizedClients = new ArrayList<>();
    private CountDownLatch serverCountDownLatch;
    private static Path cloudPath;
    private String storeName = "\\cloudStore";

    private Model() {
        //создаем корневую директорию для хранения файлов клиентов, если такая не существует
        String strPath = this.getClass().getClassLoader().getResource("").toExternalForm();
        Path path = Paths.get(strPath.substring(6));
        cloudPath = Paths.get(path.getParent().getParent().toString(), storeName);
        try {
            if (!Files.exists(cloudPath)) {
                Files.createDirectory(cloudPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Model getModel() {
        if (model == null)
            model = new Model();
        return model;
    }

    public static Path getCloudPath() {
        return cloudPath;
    }

    public NetService getNetService() {
        return netService;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public void setCountDownLatch() {
        System.out.println(serverCountDownLatch);
        if (serverCountDownLatch != null) {
            serverCountDownLatch.countDown();
            System.out.println("Защелка снята");
        }
    }

    public Client getClient(String login) {
        for(Client client : authorizedClients) {
            if (login.equals(client.getLogin())) {
                return client;
            }
        }
        return null;
    }

    public void startChannels(int commandPORT, int carrierPORT) {
        netService.startCommServer(commandPORT);
        netService.startCarrierServer(carrierPORT);
    }

    public void authorize(Client client) {
        try {
            client.setRoot(Paths.get(cloudPath.toString(),"\\", client.getLogin()));
            client.setCurrentDirectory(client.getRoot());
            DirectoryStream<Path> paths = Files.newDirectoryStream(client.getRoot());
            client.setPathList(paths);
            client.setFileNameList(client.getPathList());
            authorizedClients.add(client);
            netService.sendCommand(client.getCommChannel(), new Message(Command.AUTH_OK));
            System.out.println("Клиент " + client.getLogin() + " авторизован");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFileNameList(Client client) {
        Message message = new Message(
                Command.POST_FILENAME_LIST,
                client.getFileNameList(),
                client.getCurrentDirectory().toString()
        );
        netService.sendCommand(client.getCommChannel(), message);
    }

    public void openDirectory(Client client, int line) {
        try {
            Path openedPath = client.getPathList().get(line);
            if (Files.exists(openedPath) && Files.isDirectory(openedPath)) {
                DirectoryStream<Path> paths = Files.newDirectoryStream(openedPath);
                client.setPathList(paths);
                client.setFileNameList(client.getPathList());
                client.setCurrentDirectory(openedPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void upDirectory(Client client) {
        try {
            Path parentPath = client.getCurrentDirectory().getParent();
            String currentDirectory = client.getCurrentDirectory().toString();
            if (!currentDirectory.toString().equals(client.getRoot().toString())) {
                DirectoryStream<Path> paths = Files.newDirectoryStream(parentPath);
                client.setPathList(paths);
                client.setFileNameList(client.getPathList());
                client.setCurrentDirectory(parentPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadFile(TransportMessage message, Client client) throws IOException {
        //создаем путь для записи
        String strPath = client.getCurrentDirectory().toString();
        strPath += "\\" + message.getFileName();
        Path path = Paths.get(strPath);
        //если это первый пакет передачи
        if (message.getCount() == 0 ) {
            //если файл существует, то удаляем и создаем заново
            if (Files.exists(path))
                Files.delete(path);
            Files.write(path, message.getBytes(), StandardOpenOption.CREATE);
            model.getNetService().sendCommand(client.getCommChannel(), new Message(Command.GET_NEXT_PART));
            //если это не первый пакет
        } else {
            Files.write(path, message.getBytes(), StandardOpenOption.APPEND);
            model.getNetService().sendCommand(client.getCommChannel(), new Message(Command.GET_NEXT_PART));
        }
        //обновляем файл-лист клиента после копирования файла
        if (message.getCount() == message.getTotalAmount() - 1) {
            Path currentPath = client.getCurrentDirectory();
            DirectoryStream<Path> paths = Files.newDirectoryStream(currentPath);
            client.setPathList(paths);
            client.setFileNameList(client.getPathList());
            model.sendFileNameList(client);
        }
    }

    public void sendFile(Client client, int line) {
        try {
            Path path = client.getPathList().get(line);
            int partSize = 1000000;             //размер одного фрагмента
            long fileSize = Files.size(path);   //размер файла
            String fileName = path.getFileName().toString();

            if (fileSize < partSize)
                sendShortFile(client.getCarrierChannel(), path, fileName, partSize);
            else
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendLongFile(client.getCarrierChannel(), path, fileName, partSize, fileSize);
                    }
                }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendShortFile(ChannelHandlerContext ctx, Path path, String fileName, int piece)
            throws IOException
    {
        byte[] data = Files.readAllBytes(path);
        netService.sendCommand(ctx, new TransportMessage(
                Command.FILE_FROM_SERVER,           //комманда и аутентификация
                data,                               //очередная часть файла
                1,                                  //количество частей файла
                0,                                  //счетчик частей файла
                piece,                              //величина одной части
                data.length,                        //количество байт последней части
                fileName                            //имя файла
        ));
    }

    private void sendLongFile(ChannelHandlerContext ctx, Path path, String fileName, int partSize, long fileSize)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RandomAccessFile raf = new RandomAccessFile(path.toString(), "r");
                    byte[] data;                            //байтовый массив
                    int parts = (int)fileSize/partSize;     //количество полных частей
                    if (fileSize % partSize != 0) parts++;  //увеличиваем количество частей, если есть остаток
                    int lastPiece = 0;                      //количество байт в последней части

                    for (int i = 0; i < parts; i++) {

                        //рассчитываем последнюю часть файла
                        lastPiece = (int)(fileSize - (parts - 1) * partSize);

                        //создаем байтовый массив подходящего размера
                        boolean isLastPart = (i == parts - 1) && (fileSize % partSize != 0);
                        data = isLastPart ? new byte[lastPiece] : new byte[partSize];

                        //счимываем часть файла
                        raf.readFully(data, 0, data.length);

                        //отправляем часть файла на сервер
                        TransportMessage tMsg = new TransportMessage(
                                Command.FILE_FROM_SERVER,           //команда и аутентификация
                                data,                               //очередная часть файла
                                parts,                              //количество частей файла
                                i,                                  //счетчик частей файла
                                partSize,                           //величина одной части
                                lastPiece,                          //количество байт последней части
                                fileName                            //имя файла
                        );
                        netService.sendCommand(ctx, tMsg);

                        //Ждем ответа от сервера об окончании записи фрагмента
                        try {
                            serverCountDownLatch = new CountDownLatch(1);
                            serverCountDownLatch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void disconnectClient(Client client) {
        authorizedClients.removeIf(cl -> client.getLogin().equals(cl.getLogin()));

    }

    public boolean toRegister(String login, String password) {

        try {
            //добавляем нового клиента в базу данных
            authService.registerNewClient(login, password);

            //добавляем новый каталог в базу данных
            //String path = "\\" + login;
            //authService.registerNewDirectory(login, path);

            //создаем каталог клиента
            Path path = Paths.get(getCloudPath().toString(), "\\", login);
            if (!Files.exists(path)) {
                Files.createDirectory(path);
                System.out.println("Создан каталог: " + path);
            }

            return true;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
