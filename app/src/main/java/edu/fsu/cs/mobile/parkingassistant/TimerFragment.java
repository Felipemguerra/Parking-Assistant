package edu.fsu.cs.mobile.parkingassistant;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

/**
 * Created by Felipe on 3/23/2018.
 */

public class TimerFragment extends Fragment {

    private Button setBtn;
    private Button backBtn;
    private Button cancelBtn;

    private NumberPicker p1;
    private NumberPicker p2;
    private NumberPicker p3;
    private NumberPicker p4;

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
        p1 = v.findViewById(R.id.num1);
        p2 = v.findViewById(R.id.num2);
        p3 = v.findViewById(R.id.num4);
        p4 = v.findViewById(R.id.num3);
    }

    private void setText() {
        setBtn.setText(getString(R.string.timer));
        backBtn.setText(getString(R.string.goback));
        cancelBtn.setText(getString(R.string.cancel));
        p1.setMaxValue(2);
        p2.setMaxValue(3);
        p3.setMaxValue(5);
        p4.setMaxValue(9);
    }

    private void setListeners() {
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set timer for assigned value
                if(p1.getValue() == 0 && p2.getValue() == 0 && p3.getValue() == 0 && p4.getValue() == 0) {
                    setBtn.setError("Please enter a valid time");
                }
                else {
                    ((MainActivity) getActivity()).setTimer(p1.getValue()+p2.getValue(), p3.getValue()+p4.getValue());
                    ((MainActivity) getActivity()).setMainFrag();
                }
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
