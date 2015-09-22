package com.example.jonathan.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jonathan.chat.Adapter.MessageAdapter;
import com.example.jonathan.chat.Fragment.NavigationDrawerFragment;
import com.example.jonathan.chat.Model.Message;
import com.example.jonathan.chat.Model.Room;
import com.example.jonathan.chat.Utils.SocketServer;
import com.example.jonathan.chat.Utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class RoomActivity extends AppCompatActivity implements TextWatcher {

    private Toolbar toolbar;

    // Action
    private TextView mAction;

    // Chat
    private String mUsername;
    private String mRoom;
    private Room gRoom;

    // Message
    private ArrayList<Message> listMessages;
    private ListView listViewMessage;
    private MessageAdapter messageAdapter;

    private EditText newMessage;

    private SocketServer socketServer;

    private boolean error;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        error = false;

        // action
        mAction = (TextView) findViewById(R.id.action);
        mAction.setText("");

        // username
        SharedPreferences preferences = getSharedPreferences("account", Context.MODE_APPEND);
        mUsername = preferences.getString("username", "");
        mRoom = (String) getIntent().getSerializableExtra("room");

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(mRoom);

        setSupportActionBar(toolbar); // set our own toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Fragment for the drawer layout
        final NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        listMessages = new ArrayList<Message>();

        messageAdapter = new MessageAdapter(this, listMessages);

        listViewMessage = (ListView) findViewById(R.id.list_messages);
        listViewMessage.setAdapter(messageAdapter);

        newMessage = (EditText) findViewById(R.id.new_message);

        newMessage.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // when button done is hit
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendMessage();
                    return true;
                }
                return false;
            }

        });

        newMessage.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("RoomActivityLog", "onTextChangedCalled");
                socketServer.getInstance().getSocket().emit("writing", mUsername);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                socketServer.getInstance().getSocket().emit("end_writing", mUsername);
                Log.d("RoomActivityLog", "afterTextChanged");
            }
        });

        // if the server have an error
        socketServer.getInstance().getSocket().on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {

            @Override
            public void call(Object... args) {

                if(!error) {
                    error = true;
                    closeActivity();
                }

            }
        });

        // get all messages one by one
        socketServer.getInstance().getSocket().on("new_message", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("RoomActivityLog", "args[0] = " + args[0]);

                        JSONObject data = (JSONObject) args[0];
                        JSONObject user;
                        String username;
                        String message;
                        String color;
                        try {
                            user = data.getJSONObject("user");

                            username = user.getString("username");
                            message = data.getString("message");
                            color = data.getString("color");
                        } catch (JSONException e) {
                            return;
                        }

                        Log.d("RoomActivityLog", "username = " + username + " || message = " + message);

                        final Message msg1 = new Message();
                        msg1.setAuthor(username);
                        msg1.setMessage(message);
                        msg1.setColor(Tools.intColorFromString(color));
                        listMessages.add(msg1);

                        messageAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        // X is writing
        socketServer.getInstance().getSocket().on("writing", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.d("RoomActivityLog", "writing called = " + args[0]);

                        JSONObject data = (JSONObject) args[0];
                        String username;
                        try {
                            username = data.getString("username");
                        } catch (JSONException e) {
                            return;
                        }
                        mAction.setText(username + " is writing");
                    }
                });
            }
        });

        // X finish writing
        socketServer.getInstance().getSocket().on("end_writing", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAction.setText("");
                    }
                });
            }
        });

        // if full room is on
        /*socketServer.getInstance().getSocket().on("full_room", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("RoomActivityLog", "full_room received");

                        JSONObject data = (JSONObject) args[0];
                        boolean roomFull;
                        try {
                            roomFull = data.getBoolean("value");
                        } catch (JSONException e) {
                            return;
                        }

                        if (roomFull) {
                            Toast.makeText(RoomActivity.this, "Room full, you can't join this room", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                            finish();
                            startActivity(intent);
                        }
                    }
                });
            }
        });*/

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

        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("RoomActivityLog", "onPause");
        leftRoom();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void init(){

        // if we haven't any socket, we go back to the login screen
        if(socketServer.getInstance().getSocket() == null){
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            this.finish();
        }

        // emit the user want to join again this room
        //SocketServer.getInstance().getSocket().emit("enter_again_room", mRoom);

        // delete all messages from list
        listMessages.clear();

        // need all messages from the current room
        socketServer.getInstance().getSocket().emit("messages", mRoom);

        // tell to everybody than we just enter in the room
        JSONObject obj = new JSONObject();
        try{
            obj.put("message", "join the room");
            obj.put("color", "red");
            obj.put("room", mRoom);
        } catch(Exception e){

        }

        // emit our enter in the room
        SocketServer.getInstance().getSocket().emit("new_message", obj);
    }

    private void sendMessage(){
        String message = newMessage.getText().toString();
        newMessage.setText("");

        JSONObject obj = new JSONObject();
        try{
            obj.put("message", message);
            obj.put("color", "white");
            obj.put("room", mRoom);
        } catch(Exception e){

        }
        socketServer.getInstance().getSocket().emit("new_message", obj);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onBackPressed(){
        backToListRooms();
    }

    private void backToListRooms(){
        Intent intent = new Intent(this, ListActivity.class);
        this.finish();

        startActivity(intent);
    }

    private void leftRoom(){
        SocketServer.getInstance().getSocket().emit("left_room", mRoom);

        JSONObject obj = new JSONObject();
        try{
            obj.put("message", "left the room");
            obj.put("color", "red");
            obj.put("room", mRoom);
        } catch(Exception e){

        }

        SocketServer.getInstance().getSocket().emit("new_message", obj);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        socketServer.getInstance().getSocket().emit("tiping");
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    public void getRoom(){

        // Add room to list rooms
        final Room room = new Room();
        gRoom = new Room();

        SocketServer.getInstance().getSocket().emit("room", mRoom);
        socketServer.getInstance().getSocket().on("room", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject data = (JSONObject) args[0];

                        String name;
                        int space;
                        int nbUser;
                        String image;
                        try {
                            name = data.getString("name");
                            space = data.getInt("space");
                            nbUser = data.getInt("nb_user");
                            image = data.getString("image");
                        } catch (JSONException e) {
                            return;
                        }
                        room.setName(name);
                        room.setSpace(space);
                        room.setNbUser(nbUser);
                        room.setImage(image);

                        gRoom = room;
                    }
                });
            }
        });
    }

    public final String getImageRoom(){
        getRoom();
        return gRoom.getImage();
    }
}
