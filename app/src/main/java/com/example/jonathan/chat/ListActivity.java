package com.example.jonathan.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jonathan.chat.Adapter.RoomAdapter;
import com.example.jonathan.chat.Model.Room;
import com.example.jonathan.chat.Utils.SocketServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.emitter.Emitter;

public class ListActivity extends AppCompatActivity {

    // Rooms
    private Room room;
    private RoomAdapter roomAdapter;
    private ArrayList<Room> listRooms;

    private RecyclerView recyclerViewRoom;

    private SocketServer socketServer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Log.d("ListActivityLog", "onCreate");

        listRooms = new ArrayList<Room>();

        roomAdapter = new RoomAdapter(this, listRooms);

        recyclerViewRoom = (RecyclerView) findViewById(R.id.list_rooms);
        recyclerViewRoom.setAdapter(roomAdapter);
        recyclerViewRoom.setLayoutManager(new LinearLayoutManager(this));

        roomAdapter.setOnItemClickListener(new RoomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                // if view is not null
                if(view != null){

                    // if view.getTag is not null
                    if(view.getTag() != null){

                        // if view.getTag is equals to id "R.mipmap.nolike"
                        if(view.getTag().equals(R.mipmap.nolike)) {
                            ImageView imageView = (ImageView) view;
                            imageView.setImageResource(R.mipmap.like);
                            imageView.setTag(R.mipmap.like);
                        } else if (view.getTag().equals(R.mipmap.like)){
                            ImageView imageView = (ImageView) view;
                            imageView.setImageResource(R.mipmap.nolike);
                            imageView.setTag(R.mipmap.nolike);
                        }
                    } else {
                        accessToRoom(position); // join the room at this position
                    }
                }
            }
        });

        // notice rooms are coming
        socketServer.getInstance().getSocket().on("rooms_are_coming", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // delete all rooms from list
                        listRooms.clear();
                        roomAdapter.notifyDataSetChanged();

                        Log.d("ListActivityLog", "Rooms are comiiiiiiiiiing!");

                    }
                });
            }
        });

        // get rooms one by one
        socketServer.getInstance().getSocket().on("room", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject data = (JSONObject) args[0];
                        Log.d("ListActivityLog", "args[0] = " + args[0]);

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

                        // Add room to list rooms
                        Room room = new Room();
                        room.setName(name);
                        room.setSpace(space);
                        room.setNbUser(nbUser);
                        room.setImage(image);
                        listRooms.add(room);
                        roomAdapter.notifyDataSetChanged();

                        Log.d("ListActivityLog", "listRooms size = " + listRooms.size());

                    }
                });
            }
        });

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

    private void init(){


        /*
            init() method called once but sockets more...

            I DON'T UNDERSTAND THE PROBLEM !!

            Hello World

         */



        // if we haven't any socket, we go back to the login screen
        if(socketServer.getInstance().getSocket() == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            this.finish();
        }

        Log.d("ListActivityLog", "init called");

        // need all rooms from the server
        socketServer.getInstance().getSocket().emit("rooms");


         /*
            When a room have is number user changed,
            we need to change only his number of user and not all rooms
            because there is a lag when all rooms are updated
         */

        // when a room is modified, we edit this room
        socketServer.getInstance().getSocket().on("edit_room", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //listRooms.clear();

                        JSONObject data = (JSONObject) args[0];
                        String name;
                        int space;
                        int nbUser;
                        int position;
                        try {
                            name = data.getString("name");
                            space = data.getInt("space");
                            nbUser = data.getInt("nb_user");
                            position = data.getInt("position");
                        } catch (JSONException e) {
                            return;
                        }

                        if (listRooms.size() > 0) {
                            // Edit room to list rooms
                            listRooms.get(position).setName(name);
                            listRooms.get(position).setSpace(space);
                            listRooms.get(position).setNbUser(nbUser);
                            roomAdapter.notifyDataSetChanged();
                        }
                        Log.d("ListActivityLog", "edit room = " + listRooms.size());
                    }
                });
            }
        });
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void accessToRoom(int position) {
        final Room room = (Room) listRooms.get(position);

        // emit the user want to join this room
        SocketServer.getInstance().getSocket().emit("enter_room", room.getName());

        // if full room is on
        socketServer.getInstance().getSocket().on("full_room", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("ListActivityLog", "full_room received");

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
