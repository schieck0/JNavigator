package model;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import view.StartApp;

public class HistoricoTab extends Tab {

    private static HistoricoTab instance;

    private HistoricoTab() throws IOException {
        super("Histórico");
        AnchorPane anchorPane = (AnchorPane)FXMLLoader.load(StartApp.class.getResource("historico.fxml"));
        setContent(anchorPane);
    }

    public static HistoricoTab getInstance() throws IOException {
        if (instance == null) {
            instance = new HistoricoTab();
        }
        return instance;
    }

}
