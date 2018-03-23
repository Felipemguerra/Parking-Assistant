package edu.fsu.cs.mobile.parkingassistant;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Felipe on 3/23/2018.
 */

public class TimerFragment extends Fragment {

    private Button setBtn;
    private Button backBtn;
    private Button cancelBtn;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_timer, container, false);
        getViews(v);
        setText();
        setListeners();
        return v;
    }

    private void getViews(View v) {
        setBtn = v.findViewById(R.id.setTimerBtn);
        backBtn = v.findViewById(R.id.timerBackBtn);
        cancelBtn = v.findViewById(R.id.cancelBtn);
    }

    private void setText() {
        setBtn.setText(getString(R.string.timer));
        backBtn.setText(getString(R.string.goback));
        cancelBtn.setText(getString(R.string.cancel));
    }

    private void setListeners() {
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set timer for assigned value
                ((MainActivity)getActivity()).setTimer();
                ((MainActivity)getActivity()).setMainFrag();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).setMainFrag();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).cancelTimer();
                //cancel timer if is running
            }
        });
    }
}
