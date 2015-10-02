package com.example.jonathan.chat.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.jonathan.chat.Utils.Tools;

/**
 * Created by Jonathan on 20/09/15.
 */
public class User implements Parcelable {

    private int id;
    private String username;
    private boolean sexe;

    public User(){

    }

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

    // parcelable
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source)
        {
            return new User(source);
        }

        @Override
        public User[] newArray(int size)
        {
            return new User[size];
        }
    };

    public User(Parcel in) {
        this.username = in.readString();
        this.sexe = Tools.getBooleanFromInt(in.readInt());
        this.id = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeInt((sexe) ? 1 : 0);
        dest.writeInt(id);
    }
}
