package view;

import model.ItemHist;
import utils.DB;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Rodrigo
 */
public class HistoricoController implements Initializable {

    @FXML
    private TableView<ItemHist> table;
    @FXML
    private AnchorPane anchor;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        TableColumn<ItemHist, String> dateCol = new TableColumn<>("Data/Hora");
        dateCol.setCellValueFactory((CellDataFeatures<ItemHist, String> p) -> new ObservableValueBase<String>() {
            @Override
            public String getValue() {
                return p.getValue().getFormattedDate();
            }
        });
        dateCol.setResizable(false);
        dateCol.setPrefWidth(150);
        dateCol.setSortType(TableColumn.SortType.DESCENDING);
        table.getColumns().add(dateCol);

        TableColumn<ItemHist, String> urlCol = new TableColumn<>("URL");
        urlCol.setCellValueFactory((CellDataFeatures<ItemHist, String> p) -> new ObservableValueBase<String>() {
            @Override
            public String getValue() {
                return p.getValue().getUrl();
            }
        });
        urlCol.setResizable(true);
        urlCol.setMinWidth(350);
        table.getColumns().add(urlCol);

        table.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    BrowserController.instance.addTab(table.getSelectionModel().getSelectedItem().getUrl());
                }
            }
        });

        table.setItems(FXCollections.observableArrayList(DB.getHist()));
        
        Platform.runLater(() -> {
            anchor.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.DELETE, KeyCombination.CONTROL_DOWN), () -> {
                DB.getHist().clear();
                table.setItems(FXCollections.observableArrayList(DB.getHist()));
                DB.store();
            });
        });
    }

}
