package com.example.jonathan.chat.Model;

/**
 * Created by Jonathan on 20/09/15.
 */
public class User {

    private int id;
    private String username;
    private boolean sexe;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isSexe() {
        return sexe;
    }

    public void setSexe(boolean sexe) {
        this.sexe = sexe;
    }
}
