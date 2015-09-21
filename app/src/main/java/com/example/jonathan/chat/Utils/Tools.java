package com.example.jonathan.chat.Utils;

import android.content.Context;
import android.content.SharedPreferences;
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

    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences("account", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferences(Context context, String preferenceName, String defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences("account", Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

}
