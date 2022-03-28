package clientPac.gui.timers;

import clientPac.Model;
import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.TimerTask;

public class ServerDirectoriesTimer extends TimerTask {
    ListView<String> rightList;
    TextField rightCurDirField;
    Model model = Model.getModel();

    public ServerDirectoriesTimer(ListView<String> rightList, TextField rightCurDirField) {
        this.rightList = rightList;
        this.rightCurDirField = rightCurDirField;
    }

    @Override
    public void run() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                rightList.setItems(model.getNetNameList());
                rightCurDirField.setText(model.getCurrentDirectory());
            }
        });
    }
}
