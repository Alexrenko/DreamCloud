import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class AuthController implements Initializable {

    @FXML
    public VBox authBaseBox;
    public Button signInButton;
    public TextField textFieldLogin;
    public TextField textFieldPass;

    Model model = new Model();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFieldLogin.setPromptText("Логин");
        textFieldLogin.setFocusTraversable(false);

        textFieldPass.setPromptText("Пароль");
        textFieldPass.setFocusTraversable(false);
    }

    // События окна авторизации

    public void clickCloseLabel(MouseEvent mouseEvent) {
        Stage stage = (Stage) authBaseBox.getScene().getWindow();
        stage.close();

        //todo
        //Добавить сворачивание окна
        //Сделать перемещение окна мышкой
        //Добавить картинки для кнопки "закрыть" и лого
    }

    public void clickSignInButton(ActionEvent actionEvent) throws Exception {
        URL mainSceneURL = Objects.requireNonNull(getClass().getResource("/mainScene.fxml"));
        Parent main = FXMLLoader.load(mainSceneURL);

        Stage newStage = new Stage();
        newStage.setWidth(800);
        newStage.setHeight(600);
        newStage.setTitle("DreamCloud");

        Scene mainScene = new Scene(main);
        newStage.setScene(mainScene);
        newStage.show();

        Stage stage = (Stage) authBaseBox.getScene().getWindow();
        stage.close();

    }

    public void clickParamLabel(MouseEvent mouseEvent) {

    }
}
