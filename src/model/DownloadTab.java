package model;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import view.StartApp;

public class DownloadTab extends Tab {

    private static DownloadTab instance;

    private DownloadTab() throws IOException {
        super("Downloads");
        AnchorPane anchorPane = (AnchorPane)FXMLLoader.load(StartApp.class.getResource("downloads.fxml"));
        setContent(anchorPane);
    }

    public static DownloadTab getInstance() throws IOException {
        if (instance == null) {
            instance = new DownloadTab();
        }
        return instance;
    }

}
