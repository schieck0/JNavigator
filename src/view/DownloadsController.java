package view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import model.Download;

/**
 * FXML Controller class
 *
 * @author Rodrigo
 */
public class DownloadsController implements Initializable {

    public static DownloadsController instance;
    
    @FXML
    public TableView<Download> table;
    @FXML
    private AnchorPane anchor;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        
        TableColumn<Download, Button> pauseCol = new TableColumn<>("#");
        pauseCol.setCellValueFactory((TableColumn.CellDataFeatures<Download, Button> p) -> new ObservableValueBase<Button>() {
            @Override
            public Button getValue() {
                return p.getValue().getPauseButton();
            }
        });
        pauseCol.setMaxWidth(50);
        table.getColumns().add(pauseCol);
        
        TableColumn<Download, String> nomeCol = new TableColumn<>("Nome");
        nomeCol.setCellValueFactory((TableColumn.CellDataFeatures<Download, String> p) -> new ObservableValueBase<String>() {
            @Override
            public String getValue() {
                return p.getValue().getNome();
            }
        });
        nomeCol.setPrefWidth(300);
        table.getColumns().add(nomeCol);

        TableColumn<Download, String> tamCol = new TableColumn<>("Tamanho");
        tamCol.setCellValueFactory((TableColumn.CellDataFeatures<Download, String> p) -> new ObservableValueBase<String>() {
            @Override
            public String getValue() {
                return p.getValue().getTamanho();
            }
        });
        tamCol.setResizable(false);
        tamCol.setMinWidth(80);
        tamCol.setMaxWidth(80);
        table.getColumns().add(tamCol);
        
        TableColumn<Download, String> velCol = new TableColumn<>("Velocidade");
        velCol.setCellValueFactory((TableColumn.CellDataFeatures<Download, String> p) -> new ObservableValueBase<String>() {
            @Override
            public String getValue() {
                return p.getValue().getVeloc();
            }
        });
        velCol.setResizable(false);
        velCol.setMinWidth(80);
        velCol.setMaxWidth(80);
        table.getColumns().add(velCol);
        
        TableColumn<Download, String> timeCol = new TableColumn<>("Tempo Restante");
        timeCol.setCellValueFactory((TableColumn.CellDataFeatures<Download, String> p) -> new ObservableValueBase<String>() {
            @Override
            public String getValue() {
                return p.getValue().getTempoRest();
            }
        });
        timeCol.setResizable(false);
        timeCol.setMinWidth(100);
        timeCol.setMaxWidth(100);
        table.getColumns().add(timeCol);

        TableColumn<Download, String> progressCol = new TableColumn<>("Progresso");
        progressCol.setCellValueFactory((TableColumn.CellDataFeatures<Download, String> p) -> new ObservableValueBase<String>() {
            @Override
            public String getValue() {
                return p.getValue().getPercent() + "%";
            }
        });
        progressCol.setResizable(false);
        progressCol.setMinWidth(80);
        progressCol.setMaxWidth(80);
        table.getColumns().add(progressCol);

        TableColumn<Download, String> urlCol = new TableColumn<>("URL");
        urlCol.setCellValueFactory((TableColumn.CellDataFeatures<Download, String> p) -> new ObservableValueBase<String>() {
            @Override
            public String getValue() {
                return p.getValue().getUrl();
            }
        });
        urlCol.setMinWidth(500);
        table.getColumns().add(urlCol);

        table.setItems(FXCollections.observableArrayList(Download.DOWNLOADS));
    }
    
    public static class HBoxCell extends HBox {
          Label label = new Label();
          Button button = new Button();

          HBoxCell(String labelText, String buttonText) {
               super();

               label.setText(labelText);
               label.setMaxWidth(Double.MAX_VALUE);
               HBox.setHgrow(label, Priority.ALWAYS);

               button.setText(buttonText);

               this.getChildren().addAll(label, button);
          }
     }

}
