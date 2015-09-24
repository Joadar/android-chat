package com.example.jonathan.chat.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.jonathan.chat.HomeActivity;
import com.example.jonathan.chat.R;
import com.example.jonathan.chat.Utils.Tools;

/**
 * Created by Jonathan on 21/09/15.
 */
public class FragmentRegisterSexe extends Fragment implements View.OnClickListener {

    private Button boyButton;
    private Button girlButton;
    private ImageView backButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_sexe, container, false);

        boyButton = (Button) view.findViewById(R.id.boyButton);
        girlButton = (Button) view.findViewById(R.id.girlButton);

        backButton = (ImageView) view.findViewById(R.id.backButton);

        // add event click on buttons
        boyButton.setOnClickListener(this);
        girlButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0); // hide the keyboard
    }

    @Override
    public void onClick(View v) {
        if(v == boyButton){
            nextFragment("1"); // pass to next fragment with "boy" as value sexe argument
        } else if (v == girlButton){
            nextFragment("0"); // pass to next fragment with "girl" as value sexe argument
        } else if (v == backButton){
            Intent intent = new Intent(getContext(), HomeActivity.class);
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
            startActivity(intent);
        }
    }

    public void nextFragment(String sexe){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);

        FragmentRegisterUsername nextFrag = new FragmentRegisterUsername();

        Bundle data = new Bundle();
        data.putString("sexe", sexe);
        nextFrag.setArguments(data); // add argument to the fragment

        ft.replace(R.id.contentFragment, nextFrag, "detailFragment");

        // Start the animated transition.
        ft.commit();
    }
}
