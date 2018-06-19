package model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import view.DownloadsController;

public class Download {

    public static final List<Download> DOWNLOADS = new ArrayList<>();

    private String nome;
    private String url;
    private File destino;
    private String tamanho;
    private long timeRemaining;
    private Button pauseButton;

    private long contentLength;
    private double downloadRate;
    private long timeStarted;
    private double bytesRead;
    private boolean downloading;
    private Thread downloadThread;

    public Download() {
        pauseButton = new Button();
        pauseButton.setGraphic(new ImageView(new Image("/view/img/Pause-icon.png")));

        pauseButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                downloading = false;
            }
        });
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public File getDestino() {
        return destino;
    }

    public void setDestino(File destino) {
        this.destino = destino;
    }

    public int getPercent() {
        return (int) ((bytesRead / contentLength) * 100);
    }

    public String getTamanho() {
        return tamanho;
    }

    public String getVeloc() {
        return (int) downloadRate + " kbp/s";
    }

    public String getTempoRest() {
        int seconds = (int) (timeRemaining / 1000) % 60;
        int minutes = (int) ((timeRemaining / (1000 * 60)) % 60);
        int hours = (int) ((timeRemaining / (1000 * 60 * 60)));

        DecimalFormat df = new DecimalFormat("00");

        return df.format(hours) + ":" + df.format(minutes) + ":" + df.format(seconds);
    }

    public Button getPauseButton() {
        return pauseButton;
    }

    public void start() {
        downloadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                DOWNLOADS.add(Download.this);
                try {
                    HttpURLConnection httpConn = (HttpURLConnection) new URL(url).openConnection();
                    contentLength = Long.parseLong(httpConn.getHeaderField("Content-Length"));
                    tamanho = getHumanContentLenght(contentLength);
                    url = httpConn.getURL().toString();

                    BufferedInputStream in = new BufferedInputStream(httpConn.getInputStream());
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destino));
                    byte[] buffer = new byte[4096]; // 4kB

                    timeStarted = System.currentTimeMillis();
                    downloading = true;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (downloading) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException ex) {
                                }
                                if (DownloadsController.instance != null) {
                                    DownloadsController.instance.table.getItems().set(DOWNLOADS.indexOf(Download.this), Download.this);
                                }
                            }
                        }
                    }).start();

                    long timeElapsed;
                    bytesRead = 0;
                    while (downloading) {
                        int n = in.read(buffer);
                        if (n == -1) {
                            downloading = false;
                        } else {
                            out.write(buffer, 0, n);
                            bytesRead += n;

                            timeElapsed = System.currentTimeMillis() - timeStarted;
                            downloadRate = (bytesRead / 1000.0) / (timeElapsed / 1000.0);
                            timeRemaining = (long) (1000 * ((contentLength - bytesRead) / (downloadRate * 1000)));
                        }
                    }
                    pauseButton.setGraphic(new ImageView(new Image("/view/img/Play-icon.png")));
                    in.close();
                    out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        downloadThread.start();
    }

    public static String getHumanContentLenght(long bytes) throws IOException {
        DecimalFormat df = new DecimalFormat("###,###.00");
        double tam = bytes;
        String s = "b";

        if (tam / 1024 > 1) {
            tam = tam / 1024;
            s = "kb";
        }
        if (tam / 1024 > 1) {
            tam = tam / 1024;
            s = "Mb";
        }
        if (tam / 1024 > 1) {
            tam = tam / 1024;
            s = "Gb";
        }

        return df.format(tam) + " " + s;
    }

}
