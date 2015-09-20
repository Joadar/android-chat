package com.example.jonathan.chat.Utils;

import android.graphics.Color;

/**
 * Created by Jonathan on 18/09/15.
 */
public class Tools {

    public static final String API_SERVER = "http://192.168.1.42:1337/";

    public static int intColorFromString(String colorString){
        int color;

        switch (colorString){
            case "red":
                color = Color.RED;
                break;
            case "white":
                color = Color.WHITE;
                break;
            default:
                color = Color.WHITE;
                break;
        }

        return color;
    }

}
