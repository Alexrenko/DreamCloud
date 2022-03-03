package clientPac.Services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;

public class FileService {
    private ObservableList<String> fileNameList;
    private boolean isPossibleToCreate;
    private ObservableList<File> currentFileList;
    private File currentDirectory;

    public FileService() {
        fileNameList = FXCollections.observableArrayList();
        currentFileList = FXCollections.observableArrayList();
        isPossibleToCreate = false;
    }

    public ObservableList<String> getFileNameList() {
        return fileNameList;
    }

    public void showRoots() {
        File[] localDiscs = File.listRoots();
        isPossibleToCreate = false;
        setCurrentDirectory(null);
        currentFileList.clear();
        fileNameList.clear();
        currentFileList.addAll(Arrays.asList(localDiscs));
        for(File file : currentFileList) {
            fileNameList.add(file.getAbsolutePath());
        }
    }

    public boolean isPossibleToCreate() {
        return isPossibleToCreate;
    }

    public void setCurrentDirectory(File directory) {
        currentDirectory = directory;
    }

    public String getNameCurrentDirectory() {
        if (currentDirectory != null)
            return currentDirectory.getAbsolutePath();
        return "";
    }

    public void createDirectory(String name) {
        Path pathNewFolder = Paths.get(currentDirectory.getAbsolutePath() + "\\" + name);
        int count = 1;
        while (true) {
            if (!Files.exists(pathNewFolder)) {
                try {
                    Files.createDirectory(pathNewFolder);
                    break;
                } catch (IOException e) {
                    System.out.println("SWW during creating folder");
                }
            } else {
                count++;
                pathNewFolder = Paths.get(currentDirectory.getAbsolutePath() + "\\" + name + " (" + count + ")");
            }
        }
        updateCurrentFileList(currentDirectory.toPath());
        updateFileNameList(currentFileList);
    }

    public void openDirectory(int index){
        if (index != -1) {
            Path path = Paths.get(currentFileList.get(index).getAbsolutePath());
            if (Files.exists(path) && Files.isDirectory(path)) {
                updateCurrentFileList(path);
                updateFileNameList(currentFileList);
                setCurrentDirectory(path.toFile());
                isPossibleToCreate = true;
            }
        }
    }

    public void backToUpDirectory() {
        if (currentDirectory != null) {
            if (currentDirectory.getParentFile() != null) {
                Path path = Paths.get(currentDirectory.getParentFile().getAbsolutePath());
                updateCurrentFileList(path);
                updateFileNameList(currentFileList);
                setCurrentDirectory(path.toFile());
            } else
                showRoots();
        }
    }

    public void updateCurrentFileList(Path path) {
        try {
            DirectoryStream<Path> paths = Files.newDirectoryStream(path);
            currentFileList.clear();
            for(Path currentPath : paths)
                currentFileList.add(currentPath.toFile());
            currentFileList.sort(new byDirectoryComparator());
        } catch (IOException e) {
            System.out.println("SWW during openDirectory in Model");
        }
    }

    public void updateFileNameList(ObservableList<File> files) {
        fileNameList.clear();
        for(File file : files)
            fileNameList.add(file.getName());
    }

    private static class byDirectoryComparator implements Comparator<File> {
        @Override
        public int compare(File first, File second) {
            boolean isDirectoryFirst = Files.isDirectory(first.toPath());
            boolean isDirectorySecond = Files.isDirectory(second.toPath());

            return isDirectoryFirst == isDirectorySecond ?
                    first.compareTo(second) : isDirectoryFirst ?
                    -1 : 1;
        }
    }

}
