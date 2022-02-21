import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class Client extends Application {

    public static void main(String[] args) {
        Client.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Parent auth;
        URL authSceneURL;
        String styleSource;

        //Подключаем файлы authScene.fxml и auth.css

        try {
            authSceneURL = getClass().getResource("/authScene.fxml");
            if (authSceneURL == null) throw new RuntimeException("Нет доступа к файлу authScene.fxml");
            styleSource = Objects.requireNonNull(getClass().getResource("/auth.css")).toExternalForm();
            auth = FXMLLoader.load(authSceneURL);
        } catch (NullPointerException e) {
            throw new RuntimeException("Нет доступа к файлу auth.css");
        } catch (IOException e) {
            throw new RuntimeException("SWW during FXML loading");
        }

        //Запускаем окно авторизации

        primaryStage.setWidth(290);
        primaryStage.setHeight(270);
        primaryStage.setTitle("DreamCloud");
        primaryStage.setResizable(false);
        Scene authScene = new Scene(auth);
        authScene.getStylesheets().add(styleSource);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(authScene);
        primaryStage.show();
    }
}
