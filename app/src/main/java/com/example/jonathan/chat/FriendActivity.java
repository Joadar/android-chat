package com.example.jonathan.chat;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.jonathan.chat.Adapter.ViewPagerAdapter;
import com.example.jonathan.chat.Fragment.FriendFragment;
import com.example.jonathan.chat.Manager.FriendManager;
import com.example.jonathan.chat.Model.User;
import com.example.jonathan.chat.Utils.SocketServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.emitter.Emitter;

public class FriendActivity extends AppCompatActivity {

    private FriendManager friendManager;

    /** TODO
     * Two recyclers view accessible with tabs:
     * - One is the list of the friends request accepted,
     * - The second is the list of friends request not accepted, display the number request in the tab's title
     *
     * Need to know how to do two recyclers view on two tabs (fragments but surely something else)
     **/

    private Toolbar toolbar;

    private ArrayList<User> listFriends;
    private ArrayList<User> listRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        friendManager = new FriendManager(this);

        // toolbar
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Friends");
        setSupportActionBar(toolbar); // set our own toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFriends(); // get all friends
        getFriendRequest(); // get all friend request

        // tabs
        final ViewPager viewPager = (ViewPager) findViewById(R.id.tabanim_viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabanim_tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        Log.i("tabsLog", "one");
                        break;
                    case 1:
                        Log.i("tabsLog", "two");
                        break;
                    case 2:
                        Log.i("tabsLog", "three");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

        });
    }

    private void setupViewPager(ViewPager viewPager) {

        Log.d("friendFragment", "getFriendRequest().size() = " + listRequest.size());
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(FriendFragment.newInstance(getResources().getColor(R.color.accent_material_light), listFriends, false), "FRIENDS");
        adapter.addFrag(FriendFragment.newInstance(getResources().getColor(R.color.ripple_material_light), listRequest, true), "REQUEST");
        viewPager.setAdapter(adapter);
    }

    private void getFriends(){
        SocketServer.getInstance().getSocket().emit("get_friends");

        listFriends = new ArrayList<>();

        Log.d("friendFragment", "getFriend called");

        // get list friend request
        friendManager.getFriend(listFriends);
    }

    private void getFriendRequest(){
        SocketServer.getInstance().getSocket().emit("get_friend_request");

        listRequest = new ArrayList<>();

        Log.d("friendFragment", "getFriendRequest called");

        // get list friend request
        friendManager.getFriendRequest(listRequest);
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
