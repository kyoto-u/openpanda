package org.sakaiproject.alert.model;

/**
 * Created by kajita on 5/5/16.
 */
public class Notification {
    private String title;
    private String channel;
    private boolean confirmed;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {

        this.confirmed = confirmed;
    }

    public Notification(String title) {
        this.title = title;
    }
}
