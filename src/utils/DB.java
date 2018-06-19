package utils;

import model.ItemHist;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class DB {

    private static Data data;

    private static final String dbName = "jnav.db";

    public static void store() {
        try {
            File jnav = new File(System.getProperty("user.home"), ".jnav");
            if (!jnav.exists()) {
                jnav.mkdir();
            }
            FileOutputStream fos = new FileOutputStream(new File(jnav, dbName));

            ObjectOutput out = new ObjectOutputStream(fos);
            out.writeObject(data);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao salvar dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void read() {
        try {
            File jnav = new File(System.getProperty("user.home"), ".jnav");
            if (!jnav.exists()) {
                jnav.mkdir();
            }
            File dados = new File(jnav, dbName);

            if (dados.exists()) {
                byte[] buffer = null;
                try (InputStream is = new FileInputStream(dados)) {
                    buffer = new byte[is.available()];
                    is.read(buffer);
                }

                try (ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
                        ObjectInput in = new ObjectInputStream(bis)) {
                    Data d = (Data) in.readObject();
                    data = d;
                } catch (ClassNotFoundException ex) {
                    System.err.println("Erro ao LER arquivo DB");
                    ex.printStackTrace();
                    data = new Data();
                }
            } else {
                data = new Data();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao ler dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            data = new Data();
        }
    }

    public static void addHist(ItemHist item) {
        data.hist.add(0, item);
        store();
    }

    public static List<ItemHist> getHist() {
        return data.hist;
    }

    public static String getHome() {
        return data.home;
    }

    static class Data implements Serializable {

        private static final long serialVersionUID = 1L;

        private List<ItemHist> hist = new ArrayList<>();
        private String home = "http://www.google.com.br";
    }

}
