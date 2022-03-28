package clientPac.gui.timers;

import clientPac.Model;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.util.TimerTask;

public class RegTimer extends TimerTask {

    Label infoLabel;
    Model model;

    public RegTimer(Label infoLabel) {
        this.infoLabel = infoLabel;
        model = Model.getModel();
    }

    @Override
    public void run() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                infoLabel.setText(model.getRegLabelText());
                infoLabel.setTextFill(model.getRegLabelColor());
            }
        });
    }
}
