package view;

import model.BrowserTab;
import utils.DB;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import model.DownloadTab;
import model.HistoricoTab;

/**
 * FXML Controller class
 *
 * @author Rodrigo
 */
public class BrowserController implements Initializable {

    public static int tabCount = 0;
    public static BrowserController instance;

    @FXML
    public TextField tfURL;
    @FXML
    public TabPane tabPane;
    @FXML
    private AnchorPane anchor;
    @FXML
    public Button bBack;
    @FXML
    public Button bForw;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        final Tab addTab = new Tab("");
        Image i = new Image("/view/img/add-icon.png");
        ImageView imageView = new ImageView(i);
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        addTab.setGraphic(imageView);
        addTab.setClosable(false);
        tabPane.getTabs().add(addTab);

        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> arg0, Tab arg1, Tab mostRecentlySelectedTab) {
                if (tabPane.getTabs().size() == 1) {
                    Platform.exit();
                } else if (tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex()).getContent() instanceof WebView) {
                    tfURL.setText(((WebView) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex()).getContent()).getEngine().getLocation());
                } else if (tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex()).getText().equals("")) {
                    addTab(null);
                }
            }
        });

        addTab(DB.getHome());

        tfURL.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {
                Platform.runLater(() -> {
                    if (tfURL.isFocused() && tfURL.getText() != null && !tfURL.getText().isEmpty()) {
                        tfURL.selectAll();
                    }
                });
            }
        });

        Platform.runLater(() -> {
            anchor.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.F5), () -> {
                refresh(null);
            });

            anchor.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE), () -> {
                WebView wv = (WebView) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex()).getContent();
                wv.getEngine().getLoadWorker().cancel();
            });

            anchor.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.F6), () -> {
                Platform.runLater(() -> {
                    tfURL.requestFocus();
                    tfURL.selectAll();
                });
            });

            anchor.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN), () -> {
                addTab(null);
            });
        });
    }

    @FXML
    private void goURL(ActionEvent event) {
        String url = tfURL.getText().trim();
        loadURL(url);
    }

    public WebView addTab(String url) {
        return addTab(null, url);
    }

    public WebView addTab(Integer index, String url) {
        final BrowserTab wTab = new BrowserTab("Nova Aba");

        tabPane.getTabs().add(index == null ? tabPane.getTabs().size() - 1 : index, wTab);
        tabPane.getSelectionModel().select(wTab);

        if (url != null) {
            loadURL(url);
        }
        tabCount++;

        Platform.runLater(() -> {
            tfURL.requestFocus();
        });
        return wTab.getWebView();
    }

    @FXML
    private void goBack(ActionEvent event) {
        WebView wv = (WebView) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex()).getContent();
        wv.getEngine().getHistory().go(-1);
    }

    @FXML
    private void goForward(ActionEvent event) {
        WebView wv = (WebView) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex()).getContent();
        wv.getEngine().getHistory().go(1);
    }

    @FXML
    private void goHome(ActionEvent event) {
        WebView wv = (WebView) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex()).getContent();
        wv.getEngine().load(DB.getHome());
    }

    @FXML
    private void refresh(ActionEvent event) {
        BrowserTab tab = (BrowserTab) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex());
        tab.refresh();
    }

    public void loadURL(String url) {
        url = url.matches("https?:\\/\\/.*") ? url
                : "http://" + url;

        Tab tab = tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex());
        if (tab instanceof BrowserTab) {
            BrowserTab bTab = (BrowserTab) tab;
            bTab.loadURL(url);
        }
    }

    @FXML
    private void tfUrlKey(KeyEvent event) {
        if (event.getCode().equals(KeyCode.F5)) {
            refresh(null);
        } else if (event.getCode().equals(KeyCode.ENTER) && event.isControlDown()) {
            tfURL.setText(tfURL.getText() + ".com.br");
            String url = tfURL.getText().trim();
            loadURL(url);
        }
    }

    @FXML
    private void openHist(ActionEvent event) {
        try {
            for (Tab t : tabPane.getTabs()) {
                if (t instanceof HistoricoTab) {
                    tabPane.getSelectionModel().select(t);
                    return;
                }
            }

            HistoricoTab ht = HistoricoTab.getInstance();
            tabPane.getTabs().add(tabPane.getTabs().size() - 1, ht);
            tabPane.getSelectionModel().select(ht);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @FXML
    public void openDowns(ActionEvent event) {
        try {
            for (Tab t : tabPane.getTabs()) {
                if (t instanceof DownloadTab) {
                    tabPane.getSelectionModel().select(t);
                    return;
                }
            }

            DownloadTab dt = DownloadTab.getInstance();
            tabPane.getTabs().add(tabPane.getTabs().size() - 1, dt);
            tabPane.getSelectionModel().select(dt);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
