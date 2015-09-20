package com.example.jonathan.chat.Utils;

import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Jonathan on 19/09/15.
 */
public class SocketServer {

    private static SocketServer mSocket = null;
    private Socket socket;

    private SocketServer(final String username){
        try {
            socket = IO.socket(Tools.API_SERVER);
        } catch(Exception e){

        }

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("username", username);
                } catch(Exception e){

                }

                // emit the new user
                socket.emit("new_user", obj);
            }

        });

        socket.connect();
    }

    public static SocketServer getInstance(){
        //if(mSocket == null){
          //  mSocket = new SocketServer();
        //}

        return mSocket;
    }

    public static SocketServer getInstance(String username){
        if(mSocket == null){
            mSocket = new SocketServer(username);
        }

        return mSocket;
    }

    public Socket getSocket(){
        return socket;
    }
}
