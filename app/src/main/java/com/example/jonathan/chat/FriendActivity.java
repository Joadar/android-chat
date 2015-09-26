package com.example.jonathan.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class FriendActivity extends AppCompatActivity {

    /** TODO
     * Two recyclers view accessible with tabs:
     * - One is the list of the friends request accepted,
     * - The second is the list of friends request not accepted, display the number request in the tab's title
     *
     * Need to know how to do two recyclers view on two tabs (fragments but surely something else)
     **/

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        // toolbar
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Friends");
        setSupportActionBar(toolbar); // set our own toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend, menu);
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
        } else if (id == android.R.id.home){
            backToListRooms();
        }

        return super.onOptionsItemSelected(item);
    }

    private void backToListRooms(){
        startActivity(new Intent(this, ListActivity.class));
        finish();
    }
}
