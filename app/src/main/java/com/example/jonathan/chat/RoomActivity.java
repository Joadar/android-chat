package com.example.jonathan.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jonathan.chat.Adapter.MessageAdapter;
import com.example.jonathan.chat.Manager.MessageManager;
import com.example.jonathan.chat.Model.Message;
import com.example.jonathan.chat.Model.Room;
import com.example.jonathan.chat.Model.User;
import com.example.jonathan.chat.Utils.SocketServer;
import com.example.jonathan.chat.Utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class RoomActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener {

    private MessageManager messageManager;

    private Toolbar toolbar;

    // Action
    private TextView mAction;

    // Chat
    private String mUsername;
    private String mRoom;
    private Room gRoom;

    // Message
    private ArrayList<Message> listMessages;
    private RecyclerView recyclerViewMessage;
    private MessageAdapter messageAdapter;

    private EditText newMessage;
    private ImageView sendButton;
    
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listMessages = new ArrayList<Message>();

        messageAdapter = new MessageAdapter(this, listMessages);

        recyclerViewMessage = (RecyclerView) findViewById(R.id.list_messages);
        recyclerViewMessage.setAdapter(messageAdapter);
        recyclerViewMessage.setLayoutManager(new LinearLayoutManager(this));

        messageManager = new MessageManager(this, mRoom, listMessages, recyclerViewMessage, mAction);


        newMessage = (EditText) findViewById(R.id.new_message);
        sendButton = (ImageView) findViewById(R.id.send_message);
        sendButton.setOnClickListener(this);

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

                // no need to tell the user that we write when we clear the edit text
                if(count != 0)
                    SocketServer.getInstance().getSocket().emit("writing", mUsername);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            private Timer timer = new Timer();
            private final long DELAY = 500; // milliseconds

            @Override
            public void afterTextChanged(Editable s) {
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                SocketServer.getInstance().getSocket().emit("end_writing", mUsername);
                            }
                        },
                        DELAY
                );
            }
        });

        // if the server have an error
        SocketServer.getInstance().getSocket().on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {

            @Override
            public void call(Object... args) {

                if(!error) {
                    error = true;
                    closeActivity();
                }

            }
        });

        // get all messages one by one
        messageManager.getNewMessage();

        // X is writing
        messageManager.isWriting();

        // X finish writing
        messageManager.endWriting();

        // if full room is on
        /*SocketServer.getInstance().getSocket().on("full_room", new Emitter.Listener() {

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
        leftRoom();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void init(){

        // if we haven't any socket, we go back to the login screen
        if(SocketServer.getInstance().getSocket() == null){
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            this.finish();
        }

        // emit the user want to join again this room
        //SocketServer.getInstance().getSocket().emit("enter_again_room", mRoom);

        // delete all messages from list
        listMessages.clear();

        // need all messages from the current room
        SocketServer.getInstance().getSocket().emit("messages", mRoom);

        messageManager.setNewMessage("join the room", true);
    }

    private void sendMessage(){
        String message = newMessage.getText().toString();
        if(message.length() != 0) {
            newMessage.setText("");

            messageManager.setNewMessage(message, false);
        }
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


        messageManager.setNewMessage("left the room", true);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        SocketServer.getInstance().getSocket().emit("tiping");
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    @Override
    public void onClick(View v) {
        if(v == sendButton){
            sendMessage();
        }
    }
}
