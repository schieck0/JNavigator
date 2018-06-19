package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ItemHist implements Serializable {

    private LocalDateTime dateTime;
    private String url;

    public ItemHist(LocalDateTime dateTime, String url) {
        this.dateTime = dateTime;
        this.url = url;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFormattedDate() {
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"));
    }

}
