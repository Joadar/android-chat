package com.example.jonathan.chat.Fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.jonathan.chat.R;
import com.example.jonathan.chat.Utils.SocketServer;
import com.example.jonathan.chat.Utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jonathan on 03/10/2015.
 */
public class SuggestRoomFragment extends DialogFragment {

    private EditText roomName;
    private EditText roomArgument;
    private Button valid;

    public SuggestRoomFragment(){
        //SuggestRoomFragment dialog = new SuggestRoomFragment();

        //return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set style to dialog
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_suggest_room, container, false);

        roomName = (EditText) v.findViewById(R.id.room_name);
        roomArgument = (EditText) v.findViewById(R.id.room_argument);
        valid = (Button) v.findViewById(R.id.valid);

        valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject obj = new JSONObject();
                try {
                    obj.put("name", roomName.getText().toString());
                    obj.put("argument", roomArgument.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                SocketServer.getInstance().getSocket().emit("suggest_room", obj);

                getDialog().dismiss();
            }
        });

        return v;
    }
}
