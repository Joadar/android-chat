package com.example.jonathan.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.jonathan.chat.Utils.SocketServer;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText usernameText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameText = (EditText) findViewById(R.id.username);
        loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(this);

        // stop focus
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
           login();
        }
    }

    private void login(){
        // just for test if the username is empty
        if(usernameText.length() != 0){
            SharedPreferences preferences = getSharedPreferences("account", Context.MODE_APPEND);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("username", usernameText.getText().toString());
            editor.apply();

            // Sending an object
            JSONObject obj = new JSONObject();
            try {
                obj.put("username", usernameText.getText().toString());
            } catch(Exception e){

            }

            // instance the socket with the username
            SocketServer.getInstance(usernameText.getText().toString());

            Intent intent = new Intent(this, ListActivity.class);
            finish();
            startActivity(intent);
        }
    }
}
