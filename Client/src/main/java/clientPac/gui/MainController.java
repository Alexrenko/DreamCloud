package clientPac.gui;

import clientPac.gui.timers.LocalDirectoriesTimer;
import clientPac.gui.timers.ServerDirectoriesTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import clientPac.Model;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Timer;

public class MainController implements Initializable {

    Model model;
    Timer localDirTimer, serverDirTimer;

    @FXML
    public TextField leftCurDirField, rightCurDirField;
    public ListView<String> leftList, rightList;
    public Button leftBackButton, leftCreateButton;;
    public Button sendButton, downloadButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = Model.getModel();
        model.getFileService().showRoots();
        updateLeftList();
        leftCreateButton.setDisable(true);
        //запускаем обновление локального дерева каталогов
        localDirTimer = new Timer();
        localDirTimer.schedule(new LocalDirectoriesTimer(leftList, leftCurDirField), 0, 250);
        //запускаем обновление дерева каталогов с сервера
        serverDirTimer = new Timer();
        serverDirTimer.schedule(new ServerDirectoriesTimer(rightList, rightCurDirField), 0, 250);
    }

    public void updateLeftList() {
        ObservableList<String> observableList = FXCollections.observableArrayList();
        observableList.addAll(model.getFileService().getFileNameList());
        leftList.setItems(observableList);
    }

    public void updateLeftTextField() {
        leftCurDirField.setText(model.getFileService().getNameCurrentDirectory());
    }

    public void clickLeftList(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().name().equals("PRIMARY") && mouseEvent.getClickCount() == 2) {
            int selectedIndex = leftList.getSelectionModel().getSelectedIndex();
            model.getFileService().openDirectory(selectedIndex);
            updateLeftList();
            updateLeftTextField();
        }
        if (model.getFileService().isPossibleToCreate())
            leftCreateButton.setDisable(false);
    }

    public void clickLeftBackButton(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().name().equals("PRIMARY")) {
            model.getFileService().backToUpDirectory();
            updateLeftList();
            updateLeftTextField();
        }
        if (!model.getFileService().isPossibleToCreate())
            leftCreateButton.setDisable(true);
    }

    public void clickLeftCreateButton(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().name().equals("PRIMARY")) {
            model.getFileService().createDirectory("New folder");
        }
    }

    public void clickRightList(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().name().equals("PRIMARY") && mouseEvent.getClickCount() == 2) {
            int selectedIndex = rightList.getSelectionModel().getSelectedIndex();
            System.out.println("Нажато окно, индекс: " + selectedIndex);
            if (selectedIndex != -1)
                model.openDirectoryFromServer(selectedIndex);
        }
    }

    public void clickRightBackButton(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().name().equals("PRIMARY")) {
            model.upDirectoryFromServer();
        }
    }

    public void clickSendButton(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().name().equals("PRIMARY")) {
            int selectedIndex = leftList.getSelectionModel().getSelectedIndex();
            model.sendFile(selectedIndex);
        }
    }

    public void clickDownloadButton(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().name().equals("PRIMARY")) {
            int selectedIndex = rightList.getSelectionModel().getSelectedIndex();
            model.downloadFileFromServer(selectedIndex);
        }
    }
}
