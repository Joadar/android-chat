package com.example.jonathan.chat.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import java.util.ArrayList;

/**
 * Created by Jonathan on 18/09/15.
 */
public class Tools {

    public static final String API_SERVER = "http://192.168.1.42:1337/";

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

    public static boolean getBooleanFromInt(int value) {
        if(value == 1)
            return true;

        return false;
    }

    public void linksDrawerHome(int position){

        ArrayList<ArrayList> link = new ArrayList<>();

        switch (position){
            case 1:
                // add links + title
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                break;
        }
        /*
            Home
            Friends
            Parameters
         */

    }
}
