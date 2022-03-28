package clientPac;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Timer;

public class ClientApp extends Application implements Initializable {

    public static void main(String[] args) {
        ClientApp.launch(args);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void start(Stage primaryStage) {
        Parent auth;
        URL authSceneURL;

        //Подключаем authScene.fxml

        try {
            authSceneURL = getClass().getResource("/authScene.fxml");
            if (authSceneURL == null) throw new RuntimeException("Нет доступа к файлу authScene.fxml");
            auth = FXMLLoader.load(authSceneURL);
        } catch (IOException e) {
            throw new RuntimeException("SWW during FXML loading");
        }

        //Запускаем окно авторизации

        primaryStage.setWidth(290);
        primaryStage.setHeight(270);
        primaryStage.setTitle("DreamCloud");
        primaryStage.setResizable(false);
        Scene authScene = new Scene(auth);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(authScene);
        primaryStage.show();
    }
}
