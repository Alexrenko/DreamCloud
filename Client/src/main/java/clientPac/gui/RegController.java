package clientPac.gui;

import clientPac.Model;
import clientPac.gui.timers.LocalDirectoriesTimer;
import clientPac.gui.timers.RegTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.Command;
import utils.Message;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;

public class RegController implements Initializable {

    @FXML
    public VBox regBaseBox;
    public TextField textFieldPass, textFieldLogin;
    public Label infoLabel;

    Model model;
    String newLogin = "";
    String newPass = "";
    Timer regLabelTimer;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = Model.getModel();
        //запускаем таймер обновления информационного сообщения
        regLabelTimer = new Timer();
        regLabelTimer.schedule(new RegTimer(infoLabel), 0, 250);
    }

    public void clickSendButton(ActionEvent actionEvent) {

        //подключаемся к серверу
        model.getNetService().startCommChannel();
        while(model.getNetService().getCommChannel() == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //отправляем запрос на регистрацию нового клиента
        newLogin = textFieldLogin.getText();
        newPass = textFieldPass.getText();
        Message msg = new Message(Command.REG, newLogin, newPass);
        model.getNetService().sendCommand(msg);

    }

    public void clickCloseLabel(MouseEvent mouseEvent) {
        Parent auth;
        URL authSceneURL;

        //открываем окно авторизации
        try {
            authSceneURL = getClass().getResource("/authScene.fxml");
            if (authSceneURL == null) throw new RuntimeException("Нет доступа к файлу authScene.fxml");
            auth = FXMLLoader.load(authSceneURL);
        } catch (IOException e) {
            throw new RuntimeException("SWW during FXML loading");
        }

        Stage primaryStage = new Stage();
        primaryStage.setWidth(290);
        primaryStage.setHeight(270);
        primaryStage.setTitle("DreamCloud");
        primaryStage.setResizable(false);
        Scene authScene = new Scene(auth);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(authScene);
        primaryStage.show();

        //закрываем окно регистрации
        Stage stage = (Stage) regBaseBox.getScene().getWindow();
        stage.close();
    }

}
