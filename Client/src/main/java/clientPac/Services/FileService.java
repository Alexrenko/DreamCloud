package clientPac.Services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class FileService {
    //private ObservableList<String> fileNameList;
    private ArrayList<String> fileNameList;
    private boolean isPossibleToCreate;
    //private ObservableList<File> currentFileList;
    ArrayList<Path> currentFileList;
    private File currentDirectory;

    public FileService() {
        //fileNameList = FXCollections.observableArrayList();
        fileNameList = new ArrayList<>();
        //currentFileList = FXCollections.observableArrayList();
        currentFileList = new ArrayList<>();
        isPossibleToCreate = false;
    }

    public void showRoots() {
        File[] Discs = File.listRoots();
        ArrayList<Path> localDiscs = new ArrayList<>();
        for(File file : Discs) {
            localDiscs.add(file.toPath());
        }
        isPossibleToCreate = false;
        setCurrentDirectory(null);
        currentFileList.clear();
        fileNameList.clear();
        currentFileList.addAll(localDiscs);
        for(Path path : currentFileList) {
            fileNameList.add(path.getRoot().toString());
        }
    }

    public boolean isPossibleToCreate() {
        return isPossibleToCreate;
    }

    public void setCurrentDirectory(File directory) {
        currentDirectory = directory;
    }

    public File getCurrentDirectory() {
        return currentDirectory;
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
            Path path = currentFileList.get(index);
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

    public void updateLists() {
        updateCurrentFileList(currentDirectory.toPath());
        updateFileNameList(currentFileList);
    }

    public ArrayList<Path> getCurrentFileList() {
        return currentFileList;
    }

    public void updateCurrentFileList(Path path) {
        try {
            DirectoryStream<Path> paths = Files.newDirectoryStream(path);
            currentFileList.clear();
            for(Path currentPath : paths)
                currentFileList.add(currentPath);
            currentFileList.sort(new byDirectoryComparator());
        } catch (IOException e) {
            System.out.println("SWW during openDirectory in Model");
        }
    }

    //public ObservableList<String> getFileNameList() {
    //    return fileNameList;
    //}

    public ArrayList<String> getFileNameList() {
        return fileNameList;
    }

    public void updateFileNameList(ArrayList<Path> paths) {
        fileNameList.clear();

        for(Path path : paths)
            fileNameList.add(path.getFileName().toString());
    }

    private static class byDirectoryComparator implements Comparator<Path> {

        @Override
        public int compare(Path first, Path second) {
            boolean isDirectoryFirst = Files.isDirectory(first);
            boolean isDirectorySecond = Files.isDirectory(second);

            return isDirectoryFirst == isDirectorySecond ?
                    first.compareTo(second) : isDirectoryFirst ?
                    -1 : 1;
        }
    }

}
