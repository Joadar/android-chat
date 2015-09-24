package com.example.jonathan.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.example.jonathan.chat.Fragment.FragmentLoginAccountExisting;
import com.example.jonathan.chat.Fragment.FragmentLoginNewAccount;
import com.example.jonathan.chat.Utils.Tools;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{

    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        backButton = (ImageView) findViewById(R.id.backButton);
        backButton.setOnClickListener(this);

        Fragment nextFragment;

        // if we have an account saved, we display this account
        if(!Tools.readFromPreferences(this, "username", "0").equals("0") && !Tools.readFromPreferences(this, "password", "0").equals("0") && !Tools.readFromPreferences(this, "sexe", "0").equals("null")) {
            nextFragment = new FragmentLoginAccountExisting();
        } else { // display form to login with an other account
            nextFragment = new FragmentLoginNewAccount();
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.accountFragment, nextFragment);
        transaction.commit();
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
    public void onBackPressed(){
        backToHome();
    }

    private void backToHome(){
        Intent intent = new Intent(this, HomeActivity.class);
        finish();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(v == backButton){
            backToHome();
        }
    }
}
