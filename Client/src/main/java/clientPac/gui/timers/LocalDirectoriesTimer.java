package clientPac.gui.timers;

import clientPac.Model;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.TimerTask;

public class LocalDirectoriesTimer extends TimerTask {
    ListView<String> leftList;
    TextField leftCurDirField;
    Model model = Model.getModel();

    public LocalDirectoriesTimer(ListView<String> leftList, TextField leftCurDirField) {
        this.leftList = leftList;
        this.leftCurDirField = leftCurDirField;
    }

    @Override
    public void run() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ObservableList<String> observableList = FXCollections.observableArrayList();
                observableList.addAll(model.getFileService().getFileNameList());
                leftList.setItems(observableList);
            }
        });
    }
}
