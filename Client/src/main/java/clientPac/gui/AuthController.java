package clientPac.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import clientPac.Model;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.ResourceBundle;

public class AuthController implements Initializable {

    Model model;

    @FXML
    public VBox authBaseBox;
    public Button signInButton;
    public TextField textFieldLogin;
    public TextField textFieldPass;

    @FXML
    public TextField leftTextField;
    public ListView<String> leftList;

    private boolean possibleToCreate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFieldLogin.setPromptText("Логин");
        textFieldLogin.setFocusTraversable(false);
        textFieldPass.setPromptText("Пароль");
        textFieldPass.setFocusTraversable(false);
        model = Model.getModel();

    }

    public void clickCloseLabel(MouseEvent mouseEvent) {
        Stage stage = (Stage) authBaseBox.getScene().getWindow();
        stage.close();

        //todo
        //Добавить сворачивание окна
        //Сделать перемещение окна мышкой
        //Добавить картинки для кнопки "закрыть" и лого
    }

    public void clickSignInButton(ActionEvent actionEvent) throws Exception {
        URL mainSceneURL;
        Parent main;

        //Подключаемся к серверу
        if (model.getNetService().getCommChannel() == null) {
            model.getNetService().startCommChannel();
            while(model.getNetService().getCommChannel() == null) {
                Thread.sleep(100);
            }
        }

        //Проходим авторизацию
        for (int i = 0; i < 8; i++) {
            model.sendAuth(textFieldLogin.getText(), textFieldPass.getText());
            Thread.sleep(250);
            if(model.isAuthorized())
                break;
            else {
                //выводим сообщение
            }
        }

        //Если авторизация пройдена
        if (model.isAuthorized()) {

            //запускаем транспортный канал связи
            model.getNetService().startCarrierChannel();

            //Подключаем FXML файл основного окна
            try {
                mainSceneURL = getClass().getResource("/mainScene.fxml");
                if (mainSceneURL == null) throw new RuntimeException("Нет доступа к файлу mainScene.fxml");
                main = FXMLLoader.load(mainSceneURL);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("SWW during FXML loading");
            }

            //запускаем основное окно программы
            Stage newStage = new Stage();
            newStage.setWidth(800);
            newStage.setHeight(600);
            newStage.setTitle("DreamCloud");
            Scene mainScene = new Scene(main);
            newStage.setScene(mainScene);
            newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {

                }
            });
            newStage.show();
            Stage stage = (Stage) authBaseBox.getScene().getWindow();
            stage.close();
        }

    }

    public void clickParamLabel(MouseEvent mouseEvent) {

        //открываем окно параметров
        try {
            URL paramSceneURL = getClass().getResource("/paramScene.fxml");
            openUndecoratedWindow(paramSceneURL);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Что-то пошло не так при открытии окна параметров");
        }

        //закрываем окно авторизации
        closeAuthWindow();
    }

    public void clickRegLabel(MouseEvent mouseEvent) {

        //открываем окно регистрации
        try {
            URL regSceneURL = getClass().getResource("/regScene.fxml");
            openUndecoratedWindow(regSceneURL);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Что-то пошло не так при открытии окна регистрации");
        }

        //закрываем окно авторизации
        closeAuthWindow();
    }

    private void openUndecoratedWindow(URL sceneURL) throws IOException {
        Parent parent;

        //Подключаем FXML файл
        if (sceneURL == null) throw new RuntimeException("Нет доступа к FXML файлу");
        parent = FXMLLoader.load(sceneURL);

        //запускаем окно
        Stage stage = new Stage();
        stage.setWidth(290);
        stage.setHeight(270);
        stage.setTitle("DreamCloud");
        stage.setResizable(false);
        Scene regScene = new Scene(parent);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(regScene);
        stage.show();
    }

    private void closeAuthWindow() {
        Stage authStage = (Stage) authBaseBox.getScene().getWindow();
        authStage.close();
    }

}
