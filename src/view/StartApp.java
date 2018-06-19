package view;

import utils.DB;
import java.util.Optional;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Rodrigo
 */
public class StartApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        DB.read();
        Parent root = FXMLLoader.load(getClass().getResource("browser.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("JNavigator");
        stage.getIcons().add(new Image("/view/img/Browser-icon.png"));
        stage.setMaximized(true);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (BrowserController.tabCount > 1) {
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Fechar Navegador");
                    alert.setHeaderText("Existem várias abas abertas!");
                    alert.setContentText("Fechar todas as abas?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        DB.store();
                    } else {
                        event.consume();
                    }
                }
                DB.store();
                BrowserController.instance.tabPane.getTabs().clear();
            }
        });

        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
