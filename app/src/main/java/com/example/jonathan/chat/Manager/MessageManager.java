package com.example.jonathan.chat.Manager;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.jonathan.chat.Model.Message;
import com.example.jonathan.chat.Model.User;
import com.example.jonathan.chat.Utils.SocketServer;
import com.example.jonathan.chat.Utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.emitter.Emitter;

/**
 * Created by Jonathan on 02/10/2015.
 */
public class MessageManager {

    private Activity context;
    private String mRoom;
    private ArrayList<Message> listMessages;
    private RecyclerView recyclerViewMessage;
    private TextView mAction;

    public MessageManager(Activity context, String mRoom, ArrayList<Message> listMessages, RecyclerView recyclerViewMessage, TextView mAction){
        this.context = context;
        this.mRoom = mRoom;
        this.listMessages = listMessages;
        this.recyclerViewMessage = recyclerViewMessage;
        this.mAction = mAction;
    }

    public void setNewMessage(String message, boolean information){
        // tell to everybody than we just enter in the room
        JSONObject obj = new JSONObject();
        try{
            obj.put("message", message);
            obj.put("information", information);
            obj.put("room", mRoom);
        } catch(Exception e){

        }

        // emit our enter in the room
        SocketServer.getInstance().getSocket().emit("new_message", obj);
    }

    public void getNewMessage(){
        SocketServer.getInstance().getSocket().on("new_message", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject data = (JSONObject) args[0];
                        JSONObject user;
                        String message;
                        boolean information;
                        String room;

                        User author = new User();

                        try {
                            user = data.getJSONObject("user");
                            author.setUsername(user.getString("username"));
                            author.setId(user.getInt("id"));
                            author.setSexe(Tools.getBooleanFromInt(user.getInt("sexe")));

                            room = data.getString("room");
                            message = data.getString("message");
                            information = data.getBoolean("information");
                        } catch (JSONException e) {
                            return;
                        }

                        // if message's room sent is the room where I am
                        if(room.equals(mRoom)) {
                            Message msg = new Message();
                            msg.setAuthor(author);
                            msg.setMessage(message.trim());
                            msg.setInformation(information);

                            addMessage(msg);
                        }
                    }
                });
            }
        });

    }

    private void addMessage(Message msg){
        listMessages.add(msg);
        recyclerViewMessage.scrollToPosition(listMessages.size() - 1);
        //messageAdapter.notifyItemInserted(listMessages.size() - 1);
    }

    public void isWriting(){
        SocketServer.getInstance().getSocket().on("writing", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //Log.d("RoomActivityLog", "writing called = " + args[0]);

                        JSONObject data = (JSONObject) args[0];
                        JSONObject user;
                        String username;
                        String room;
                        try {
                            user = data.getJSONObject("user");
                            username = user.getString("username");
                            room = user.getString("room");
                        } catch (JSONException e) {
                            return;
                        }

                        // if I'm in the room where an user is writing
                        if (room.equals(mRoom)) {
                            // if is not me who is writing
                            if (!username.equals(Tools.readFromPreferences(context.getApplicationContext(), "username", null)))
                                mAction.setText(username + " is writing");
                        }
                    }
                });
            }
        });
    }

    public void endWriting(){
        SocketServer.getInstance().getSocket().on("end_writing", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAction.setText("");
                    }
                });
            }
        });
    }
}
