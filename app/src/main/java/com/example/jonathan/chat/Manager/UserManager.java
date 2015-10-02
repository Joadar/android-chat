package com.example.jonathan.chat.Manager;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.jonathan.chat.ListActivity;
import com.example.jonathan.chat.Utils.SocketServer;
import com.example.jonathan.chat.Utils.Tools;

import org.json.JSONObject;

import io.socket.emitter.Emitter;

/**
 * Created by Jonathan on 02/10/2015.
 */
public class UserManager {

    private Activity context;

    public UserManager(Activity context){
        this.context = context;
    }

    public void register(final String username, final String password, final String sexe){
        // Sending an object
        JSONObject newUser = new JSONObject();
        try {
            newUser.put("username", username);
            newUser.put("password", password);
            newUser.put("sexe", sexe);
        } catch (Exception e) {

        }

        SocketServer.getInstance().getSocket().emit("user_register", newUser);

        // get rooms one by one
        SocketServer.getInstance().getSocket().on("user_register_fail", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                Toast.makeText(context.getApplicationContext(), "Error, try again", Toast.LENGTH_LONG).show();
            }
        });

        SocketServer.getInstance().getSocket().on("user_register_success", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                // second we emit the user login if there is no problem before

               connect(username, password, sexe);
            }
        });
    }

    public void connect(final String username, final String password, final String sexe){
        Log.d("userManager", "username = " + username + " | password = " + password + " | sexe = " + sexe);
        // Sending an object
        JSONObject obj = new JSONObject();
        try {
            obj.put("username", username);
            obj.put("sexe", sexe);
            obj.put("password", password);
        } catch (Exception e) {

        }

        // emit the new user
        SocketServer.getInstance().getSocket().emit("new_user", obj);

        SocketServer.getInstance().getSocket().on("logged", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {

                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Tools.saveToPreferences(context.getApplicationContext(), "connected", "true");
                        Tools.saveToPreferences(context.getApplicationContext(), "username", username);
                        Tools.saveToPreferences(context.getApplicationContext(), "password", password);
                        Tools.saveToPreferences(context.getApplicationContext(), "sexe", String.valueOf(args[0]));

                        Intent intent = new Intent(context.getApplicationContext(), ListActivity.class);
                        context.finish();
                        context.startActivity(intent);
                    }
                });
            }
        });

        SocketServer.getInstance().getSocket().on("error_user", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {

                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context.getApplicationContext(), "Error about the account", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
