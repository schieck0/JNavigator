package model;

import java.io.File;
import utils.DB;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import view.BrowserController;

public class BrowserTab extends Tab {

    private WebView wv;
    private String url;
    private boolean hist = false;
    private boolean active = true;

    public BrowserTab(String titulo) {
        super(titulo);

        wv = new WebView();

        wv.getEngine().getLoadWorker().stateProperty().addListener(
                new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED && wv.getEngine().getTitle() != null) {
                    setGraphic(null);
                    setText(wv.getEngine().getTitle());
                    if (!hist) {
                        DB.addHist(new ItemHist(LocalDateTime.now(), url));
                        hist = true;
                    }

                    if (isSelected() && wv.getEngine().getHistory().getEntries().size() > 1) {
                        if (wv.getEngine().getHistory().getCurrentIndex() == 0) {
                            BrowserController.instance.bBack.setDisable(true);
                        } else {
                            BrowserController.instance.bBack.setDisable(false);
                        }

                        if (wv.getEngine().getHistory().getEntries().size() - 1 == wv.getEngine().getHistory().getCurrentIndex()) {
                            BrowserController.instance.bForw.setDisable(true);
                        } else {
                            BrowserController.instance.bForw.setDisable(false);
                        }
                    } else {
                        BrowserController.instance.bBack.setDisable(true);
                        BrowserController.instance.bForw.setDisable(true);
                    }

                    Document doc = wv.getEngine().getDocument();
                    NodeList heads = doc.getElementsByTagName("head");
                    if (heads.getLength() > 0) {
                        Element head = (Element) heads.item(0);
                        NodeList links = head.getElementsByTagName("link");
                        if (links.getLength() > 0) {
                            for (int i = 0; i < links.getLength(); i++) {
                                if (links.item(i).getAttributes().getNamedItem("rel") != null
                                        && links.item(i).getAttributes().getNamedItem("rel").getNodeValue().equals("shortcut icon")) {
                                    try {
                                        String faviconUrl = links.item(i).getAttributes().getNamedItem("href").getNodeValue();
                                        URL locationURL = new URL(wv.getEngine().getLocation());
                                        if (faviconUrl.startsWith("//")) {
                                        } else if (faviconUrl.startsWith("/")) {
                                            faviconUrl = locationURL.getHost() + (locationURL.getPort() > 0 ? (":" + locationURL.getPort()) : "") + faviconUrl;
                                        }

                                        if (!faviconUrl.startsWith("http")) {
                                            faviconUrl = locationURL.getProtocol() + "://" + faviconUrl;
                                        }

                                        URL url = new URL(faviconUrl);
                                        InputStream in = url.openStream();
                                        Image favicon = new Image(in);
                                        ImageView iv = new ImageView(favicon);
                                        setGraphic(iv);
                                    } catch (MalformedURLException ex) {
                                        ex.printStackTrace();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                } else if (newState == Worker.State.RUNNING) {
                    Image i = new Image("/view/img/infinity.gif");
                    ImageView imageView = new ImageView(i);
                    imageView.setFitHeight(16);
                    imageView.setFitWidth(16);
                    setGraphic(imageView);

                    if (wv.getEngine().getTitle() != null) {
                        setText(wv.getEngine().getTitle());
                    } else {
                        setText("Loading...");
                    }
                } else if (newState == Worker.State.FAILED) {
                    setGraphic(null);
                    setText("Failed");
                } else if (newState == Worker.State.CANCELLED) {
                    setGraphic(null);
                    setText("Cancelled");
                }
            }
        });

        wv.getEngine().locationProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (isSelected()) {
                    url = newValue;
                    BrowserController.instance.tfURL.setText(newValue);
                    hist = false;
                }
                //download monitor
                if (active) {
                    try {
                        URL url = new URL(newValue);
                        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                        httpConn.setRequestMethod("HEAD");
                        httpConn.setAllowUserInteraction(false);
                        httpConn.connect();
                        
                        String contentType = httpConn.getContentType();
                        if (contentType != null && contentType.startsWith("application")) {
                            String fName = FilenameUtils.getName(newValue);
                            if (fName.contains("?")) {
                                fName = fName.split("\\?")[0];
                            }
                            FileChooser fc = new FileChooser();
                            fc.setInitialFileName(fName);
                            File f = fc.showSaveDialog(null);
                            if (f != null) {
                                Download d = new Download();
                                d.setNome(fName);
                                d.setDestino(f);
                                d.setUrl(newValue);

                                d.start();
                                BrowserController.instance.openDowns(null);
                            }
                            wv.getEngine().getLoadWorker().cancel();
                            active = false;
                        } else if (contentType != null && !contentType.startsWith("text/html")) {
                            System.err.println("N RECONHECIDO: " + contentType);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        //abrir em nova guia
        wv.getEngine().setCreatePopupHandler((PopupFeatures config) -> {
            WebView webView = BrowserController.instance.addTab(BrowserController.instance.tabPane.getSelectionModel().getSelectedIndex() + 1, null);
            return webView.getEngine();
        });

        wv.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton().equals(MouseButton.MIDDLE)) {
                event.consume();//n funciona
                //BUG: https://bugs.openjdk.java.net/browse/JDK-8089927
            }
        });

        setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                BrowserController.tabCount--;
            }
        });

        setContent(wv);
    }

    public WebView getWebView() {
        return wv;
    }

    public WebEngine getEngine() {
        return wv.getEngine();
    }

    public void loadURL(String url) {
        wv.getEngine().load(url);
        this.url = url;
    }

    public void refresh() {
        wv.getEngine().reload();
    }

}
