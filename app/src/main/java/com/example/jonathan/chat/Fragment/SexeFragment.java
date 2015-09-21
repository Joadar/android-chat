package com.example.jonathan.chat.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.jonathan.chat.R;
import com.example.jonathan.chat.Utils.Tools;

/**
 * Created by Jonathan on 21/09/15.
 */
public class SexeFragment extends Fragment implements View.OnClickListener {

    private Button boyButton;
    private Button girlButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sexe, container, false);

        boyButton = (Button) view.findViewById(R.id.boyButton);
        girlButton = (Button) view.findViewById(R.id.girlButton);

        // add event click on buttons
        boyButton.setOnClickListener(this);
        girlButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v == boyButton){

            Tools.saveToPreferences(getContext(),"sexe", "boy");

        } else if (v == girlButton){

            Tools.saveToPreferences(getContext(),"sexe", "girl");

        }

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);

        UsernameFragment nextFrag = new UsernameFragment();
        ft.replace(R.id.contentFragment, nextFrag, "detailFragment");

        // Start the animated transition.
        ft.commit();
    }
}
