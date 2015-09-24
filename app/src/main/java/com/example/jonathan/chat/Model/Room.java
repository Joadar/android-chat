package com.example.jonathan.chat.Model;

/**
 * Created by Jonathan on 17/09/15.
 */
public class Room {

    private int id;
    private String name;
    private int space; // number place total
    private int nbUser; // number user in this room
    private String image; // url image
    private boolean like; // if user like this room

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSpace() {
        return space;
    }

    public void setSpace(int space) {
        this.space = space;
    }

    public int getNbUser() {
        return nbUser;
    }

    public void setNbUser(int nbUser) {
        this.nbUser = nbUser;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }
}
