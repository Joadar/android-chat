package com.example.jonathan.chat.Model;

/**
 * Created by Jonathan on 27/09/15.
 */
public class MenuItem {

    private String title;
    private int icon;

    public MenuItem(String title, int icon){
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
