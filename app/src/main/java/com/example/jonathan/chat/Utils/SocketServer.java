package com.example.jonathan.chat.Utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.example.jonathan.chat.HomeActivity;

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
    private static boolean disconnected = false;

    private SocketServer(){
        try {
            socket = IO.socket(Tools.API_SERVER);
        } catch(Exception e){

        }

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                disconnected = false;
            }

        });

        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                disconnected = true;
            }
        });

        socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                disconnected = true;
            }
        });

        socket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                disconnected = true;
            }
        });


        socket.connect();
    }

    public static SocketServer getInstance(){
        if(mSocket == null){
            mSocket = new SocketServer();
        }

        return mSocket;
    }

    /*public static SocketServer getInstance(JSONObject user){
        if(mSocket == null){
            mSocket = new SocketServer();
        }

        return mSocket;
    }*/

    public Socket getSocket(){
        return socket;
    }

    public boolean isDisconnected() { return disconnected; }
}
