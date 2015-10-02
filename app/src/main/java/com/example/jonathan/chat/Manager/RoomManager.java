package com.example.jonathan.chat.Manager;

import android.app.Activity;

import com.example.jonathan.chat.Adapter.RoomAdapter;
import com.example.jonathan.chat.ListActivity;
import com.example.jonathan.chat.Model.Room;
import com.example.jonathan.chat.Utils.SocketServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

/**
 * Created by Jonathan on 02/10/2015.
 */
public class RoomManager {

    private Activity context;
    private ArrayList<Room> listRooms;
    private RoomAdapter roomAdapter;

    public RoomManager(Activity context, ArrayList<Room> listRooms, RoomAdapter roomAdapter){
        this.context = context;
        this.listRooms = listRooms;
        this.roomAdapter = roomAdapter;
    }

    public void callRooms(){
        // notice rooms are coming
        SocketServer.getInstance().getSocket().on("rooms_are_coming", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // delete all rooms from list
                        listRooms.clear();
                        roomAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    public void getRoom(){
        SocketServer.getInstance().getSocket().on("room", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject data = (JSONObject) args[0];

                        String name;
                        int space;
                        int nbUser;
                        String image;
                        boolean like;
                        try {
                            name = data.getString("name");
                            space = data.getInt("space");
                            nbUser = data.getInt("nb_user");
                            image = data.getString("image");
                            like = data.getBoolean("like");

                        } catch (JSONException e) {
                            return;
                        }

                        // Add room to list rooms
                        Room room = new Room();
                        room.setName(name);
                        room.setSpace(space);
                        room.setNbUser(nbUser);
                        room.setImage(image);
                        room.setLike(like);
                        listRooms.add(room);
                        roomAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    public void editRoom(){
        // when a room is modified, we edit this room
        SocketServer.getInstance().getSocket().on("edit_room", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
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
                    }
                });
            }
        });
    }

}
