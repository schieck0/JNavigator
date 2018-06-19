package utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Download;

public class Downloader {
    
    private ActionListener actionListener;
    private double bytesRead;
    private double contentLength;
    private long timeStarted;
    private boolean downloading;

    public void addProgressListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public File downloadFile(String source, File dest) throws IOException {
        bytesRead = 0;
        timeStarted = 0;

        URLConnection conn = new URL(source).openConnection();
        conn.setReadTimeout(1800000);
        contentLength = conn.getContentLength();
        BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
        File tmp = null;
        if (dest != null) {
            if (dest.isDirectory()) {
                tmp = new File(dest, new File(source).getName());
            } else {
                tmp = dest;
            }
        } else {
            tmp = new File(new File(System.getProperty("user.dir")), new File(source).getName());
        }
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tmp));
        byte[] buffer = new byte[4096]; // 4kB
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(1);
        nf.setGroupingUsed(true);

        timeStarted = System.currentTimeMillis();
        downloading = true;

        if (actionListener != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Map<String, String> map = new HashMap<String, String>();
                    while (downloading) {
                        try {
                            Thread.sleep(1000);

                            long timeElapsed = System.currentTimeMillis() - timeStarted;
                            double downloadRate = (bytesRead / 1000.0) / (timeElapsed / 1000.0);
                            long timeRemaining = (long) (1000 * ((contentLength - bytesRead) / (downloadRate * 1000)));
                            String restante = "Conclusão desconhecida";
                            if (timeRemaining / 1000 / 60 / 60 > 0) {
                                restante = timeRemaining / 1000 / 60 / 60 + " hora(s) restante(s)";
                            } else if (timeRemaining / 1000 / 60 > 0) {
                                restante = timeRemaining / 1000 / 60 + " minuto(s) restante(s)";
                            } else if (timeRemaining / 1000 > 0) {
                                restante = timeRemaining / 1000 + " segundo(s) restante(s)";
                            }

                            String dados = contentLength + "|" + bytesRead + "|" + (int) ((bytesRead / contentLength) * 100) + "%" + "|" + restante + "|" + (int) downloadRate + " kbp/s";
                            String[] d = dados.split("\\|");
                            map.put("tamanho", "" + d[0]);
                            map.put("baixado", "" + d[1]);
                            map.put("porcentagem", d[2]);
                            map.put("restante", d[3]);
                            map.put("velocidade", d[4]);
                            actionListener.actionPerformed(new ActionEvent(map, 0, dados));
                        } catch (Exception ex) {
                        }
                    }
                }
            }).start();
        }

        while (true) {
            int n = in.read(buffer);
            if (n == -1) {
                downloading = false;
                break;
            }
            out.write(buffer, 0, n);
            bytesRead += n;
        }
        in.close();
        out.close();
        return tmp;
    }

//    public File downloadFile(String source) throws Exception {
//        return downloadFile(source, null);
//    }

//    public static File downloadFile(String source, File dest, boolean showDownloadProgress) throws IOException {
//        URLConnection conn = new URL(source).openConnection();
//        conn.setReadTimeout(1800000);
//        int contentLength = conn.getContentLength();
//        int bytesRead = 0;
//        BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
//        File tmp = null;
//        if (dest != null) {
//            if (dest.isDirectory()) {
//                tmp = new File(dest, new File(source).getName());
//            } else {
//                tmp = dest;
//            }
//        } else {
//            tmp = new File(new File(System.getProperty("user.dir")), new File(source).getName());
//        }
//        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tmp));
//        byte[] buffer = new byte[4096]; // 4kB
//        NumberFormat nf = NumberFormat.getNumberInstance();
//        nf.setMaximumFractionDigits(1);
//        nf.setGroupingUsed(true);
//
//        long timeStarted = System.currentTimeMillis();
//        ProgressMonitor pm = null;
//        if (showDownloadProgress) {
//            pm = new ProgressMonitor(null, "", "download", 0, contentLength);
//            pm.setMillisToPopup(1);
//        }
//
//        while (true) {
//            int n = in.read(buffer);
//            if (n == -1) {
//                break;
//            }
//            out.write(buffer, 0, n);
//            bytesRead += n;
//
//            if (pm != null) {
//                long timeElapsed = System.currentTimeMillis() - timeStarted;
//                double downloadRate = (bytesRead / 1000.0) / (timeElapsed / 1000.0);
//                long timeRemaining = (long) (1000 * ((contentLength - bytesRead) / (downloadRate * 1000)));
//                String restante;
//                if (timeRemaining / 1000 / 60 / 60 > 0) {
//                    restante = timeRemaining / 1000 / 60 / 60 + " hora(s) restante(s)";
//                } else if (timeRemaining / 1000 / 60 > 0) {
//                    restante = timeRemaining / 1000 / 60 + " minuto(s) restante(s)";
//                } else if (timeRemaining / 1000 > 0) {
//                    restante = timeRemaining / 1000 + " segundo(s) restante(s)";
//                } else {
//                    restante = "Conclusão desconhecida";
//                }
//
//                pm.setProgress(bytesRead);
//                pm.setNote(restante);
//            }
//        }
//        in.close();
//        out.close();
//        return tmp;
//    }
//
//    public static File downloadFile(String source, boolean showDownloadProgress) throws Exception {
//        return downloadFile(source, null, showDownloadProgress);
//    }

    public static int getContentLenght(String source) throws IOException {
        return new URL(source).openConnection().getContentLength();
    }

    public static String getHumanContentLenght(String source) throws IOException {
        double tam = new URL(source).openConnection().getContentLength();
        DecimalFormat df = new DecimalFormat("###,###.00");
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
