package com.example.jonathan.chat;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.support.design.widget.Snackbar;

import com.example.jonathan.chat.Adapter.MenuAdapter;
import com.example.jonathan.chat.Adapter.RoomAdapter;
import com.example.jonathan.chat.Fragment.NavigationDrawerFragment;
import com.example.jonathan.chat.Fragment.ProfileFragment;
import com.example.jonathan.chat.Fragment.SuggestRoomFragment;
import com.example.jonathan.chat.Manager.FriendManager;
import com.example.jonathan.chat.Manager.RoomManager;
import com.example.jonathan.chat.Model.Room;
import com.example.jonathan.chat.Utils.SocketServer;
import com.example.jonathan.chat.Utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ListActivity extends AppCompatActivity {

    private RoomManager roomManager;
    private FriendManager friendManager;

    // friend
    private int idFriendRequest;

    // activity
    private Toolbar toolbar;

    // Rooms
    private Room room;
    private RoomAdapter roomAdapter;
    private ArrayList<Room> listRooms;

    private RecyclerView recyclerViewRoom;

    // suggest room
    private FloatingActionButton fab;

    // boolean test
    private boolean error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        friendManager = new FriendManager(this);

        error = false;

        // toolbar
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Rooms");
        setSupportActionBar(toolbar); // set our own toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Fragment for the drawer layout
        final NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        // floating action button
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                Fragment prev = fm.findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }

                ft.addToBackStack(null);

                // Create and show the dialog.
                DialogFragment newFragment = new SuggestRoomFragment();
                newFragment.show(ft, "dialog");
            }
        });

        listRooms = new ArrayList<Room>();

        roomAdapter = new RoomAdapter(this, listRooms);
        roomManager = new RoomManager(this, listRooms, roomAdapter);

        recyclerViewRoom = (RecyclerView) findViewById(R.id.list_rooms);
        recyclerViewRoom.setAdapter(roomAdapter);
        recyclerViewRoom.setLayoutManager(new LinearLayoutManager(this));

        roomAdapter.setOnItemClickListener(new RoomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                // if view is not null
                if (view != null) {

                    // if view.getTag is not null
                    if (view.getTag() != null) {

                        // if view.getTag is equals to id "R.mipmap.nolike"
                        if (view.getTag().equals(R.mipmap.nolike)) {

                            ImageView imageView = (ImageView) view;
                            imageView.setImageResource(R.mipmap.like);
                            imageView.setTag(R.mipmap.like);

                            Room iRoom = listRooms.get(position);
                            iRoom.setLike(true);
                            roomAdapter.notifyDataSetChanged();

                            SocketServer.getInstance().getSocket().emit("like_room", listRooms.get(position).getName());

                        } else if (view.getTag().equals(R.mipmap.like)) {

                            ImageView imageView = (ImageView) view;
                            imageView.setImageResource(R.mipmap.nolike);
                            imageView.setTag(R.mipmap.nolike);

                            Room iRoom = listRooms.get(position);
                            iRoom.setLike(false);
                            roomAdapter.notifyDataSetChanged();

                            SocketServer.getInstance().getSocket().emit("unlike_room", listRooms.get(position).getName());

                        }
                    } else {
                        accessToRoom(position); // join the room at this position
                    }
                }
            }
        });

        // if the server have an error
        SocketServer.getInstance().getSocket().on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {

            @Override
            public void call(Object... args) {

                if (!error) {
                    error = true;
                    closeActivity();
                }

            }
        });

        // notice rooms are coming
        roomManager.callRooms();

        // get rooms one by one
        roomManager.getRoom();

        // notice the user about the status of his suggestion
        roomManager.suggestRoom();

    }

    private void closeActivity(){
        Tools.saveToPreferences(getApplicationContext(), "connected", "false");
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        this.finish();
        startActivity(intent);
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d("ListActivityLog", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ListActivityLog", "onResume");

        init();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("ListActivityLog", "onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ListActivityLog", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("ListActivityLog", "onStop");
    }

    private void init() {
        // ask the server if we have some friend request
        SocketServer.getInstance().getSocket().emit("have_friend_request");

        // if we haven't any socket, we go back to the login screen
        if (SocketServer.getInstance().getSocket() == null) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            this.finish();
        }

        // need all rooms from the server
        SocketServer.getInstance().getSocket().emit("rooms");


         /*
            When a room have is number user changed,
            we need to change only his number of user and not all rooms
            because there is a lag when all rooms are updated
         */

        // when a room is modified, we edit this room
        roomManager.editRoom();
        
        // check our friends request
        friendManager.haveRequest();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            Tools.saveToPreferences(this, "connected", "false");

            SocketServer.getInstance().getSocket().disconnect();

            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void accessToRoom(int position) {
        final Room room = (Room) listRooms.get(position);

        // emit the user want to join this room
        SocketServer.getInstance().getSocket().emit("enter_room", room.getName());

        // if full room is on
        SocketServer.getInstance().getSocket().on("full_room", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject data = (JSONObject) args[0];
                        boolean roomFull;
                        try {
                            roomFull = data.getBoolean("value");
                        } catch (JSONException e) {
                            return;
                        }

                        if (roomFull) {
                            Toast.makeText(ListActivity.this, "Room full, you can't join this room", Toast.LENGTH_SHORT).show();
                        } else {

                            Intent intent = new Intent(getApplicationContext(), RoomActivity.class);
                            intent.putExtra("room", room.getName());
                            finish();

                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }
}
