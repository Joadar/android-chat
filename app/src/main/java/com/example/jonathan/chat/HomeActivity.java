package com.example.jonathan.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.jonathan.chat.Utils.SocketServer;
import com.example.jonathan.chat.Utils.Tools;

import org.json.JSONObject;

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
        if(!SocketServer.getInstance().isDisconnected()) {

            // if the user is still connected without logout manually, then he pass directly on the list activity
            if (Tools.readFromPreferences(this, "connected", "0").equals("true")) {
                // Sending an object
                JSONObject obj = new JSONObject();
                try {
                    obj.put("username", Tools.readFromPreferences(this, "username", null));
                    obj.put("sexe", Tools.readFromPreferences(this, "sexe", "0"));
                } catch (Exception e) {

                }

                // instance the socket with the username
                SocketServer.getInstance().getSocket().emit("new_user", obj);

                Intent intent = new Intent(this, ListActivity.class);
                finish();
                startActivity(intent);
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
