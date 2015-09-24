package com.example.jonathan.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jonathan.chat.Utils.SocketServer;
import com.example.jonathan.chat.Utils.Tools;

import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button loginButton;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // instance the socket with the username
        SocketServer.getInstance();

        // if the server is not disconnected, we can access to the rest
        if (!SocketServer.getInstance().isDisconnected()) {

            // if the user is still connected without logout manually, then he pass directly on the list activity
            if (Tools.readFromPreferences(this, "connected", null) != null && Tools.readFromPreferences(this, "connected", null).equals("true")) {

                // Sending an object
                JSONObject obj = new JSONObject();
                try {
                    obj.put("username", Tools.readFromPreferences(this, "username", null));
                    obj.put("sexe", Tools.readFromPreferences(this, "sexe", null));
                    obj.put("password", Tools.readFromPreferences(this, "password", null));
                } catch (Exception e) {

                }

                // emit the new user
                SocketServer.getInstance().getSocket().emit("new_user", obj);

                SocketServer.getInstance().getSocket().on("logged", new Emitter.Listener() {

                    @Override
                    public void call(final Object... args) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Tools.saveToPreferences(getApplicationContext(), "connected", "true");
                                Tools.saveToPreferences(getApplicationContext(), "username", Tools.readFromPreferences(getApplicationContext(), "username", null));
                                Tools.saveToPreferences(getApplicationContext(), "password", Tools.readFromPreferences(getApplicationContext(), "password", null));
                                Tools.saveToPreferences(getApplicationContext(), "sexe", String.valueOf(args[0]));

                                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                                finish();
                                startActivity(intent);
                            }
                        });
                    }
                });

                SocketServer.getInstance().getSocket().on("error_user", new Emitter.Listener() {

                    @Override
                    public void call(final Object... args) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Error about the account", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        }



        loginButton = (Button) findViewById(R.id.loginButton);
        registerButton = (Button) findViewById(R.id.registerButton);

        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v == loginButton){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
            finish();
        } else if (v == registerButton){
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
            finish();
        }
    }
}
