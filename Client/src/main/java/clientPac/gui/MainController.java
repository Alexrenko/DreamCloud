package clientPac.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import clientPac.Model;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    Model model;

    @FXML
    public TextField leftTextField;
    public ListView<String> leftList;
    public Button leftBackButton, leftCreateButton;;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = Model.getModel();
        model.showRoots();
        leftList.setItems(model.getFileService().getFileNameList());
        leftCreateButton.setDisable(true);
    }

    public void clickLeftList(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().name().equals("PRIMARY") && mouseEvent.getClickCount() == 2) {
            int selectedIndex = leftList.getSelectionModel().getSelectedIndex();
            model.getFileService().openDirectory(selectedIndex);
            leftList.setItems(model.getFileService().getFileNameList());
            leftTextField.setText(model.getFileService().getNameCurrentDirectory());
        }
        if (model.isPossibleToCreate())
            leftCreateButton.setDisable(false);
    }

    public void clickLeftBackButton(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().name().equals("PRIMARY")) {
            model.backToUpDirectory();
            leftList.setItems(model.getFileNameList());
            leftTextField.setText(model.getNameCurrentDirectory());
        }
        if (!model.isPossibleToCreate())
            leftCreateButton.setDisable(true);
    }

    public void clickLeftCreateButton(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().name().equals("PRIMARY")) {
            model.createDirectory("New folder");
        }
    }

}
