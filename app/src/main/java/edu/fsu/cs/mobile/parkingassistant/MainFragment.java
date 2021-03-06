package edu.fsu.cs.mobile.parkingassistant;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MainFragment extends Fragment {

    private TextView title;
    private TextView select;

    private Button set;
    private Button find;
    private Button timer;
    private Button spots;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        getViews(v);
        setText();
        setListeners();
        return v;
    }

    private void getViews(View v) {
        title = v.findViewById(R.id.setTitleText);
        select = v.findViewById(R.id.selectText);
        set = v.findViewById(R.id.locationBtn);
        find = v.findViewById(R.id.findBtn);
        timer = v.findViewById(R.id.timerBtn);
        spots = v.findViewById(R.id.spotsBtn);
    }

    private void setText() {
        title.setText(getString(R.string.title));
        select.setText(getString(R.string.select));
        set.setText(getString(R.string.set));
        find.setText(getString(R.string.find));
        timer.setText(getString(R.string.timer));
    }

    private void setListeners() {
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).setLocationFrag();
            }
        });

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if locationSet is true
                SharedPreferences settings = getActivity().getSharedPreferences("info", 0);
                if(settings.contains("latitude") && settings.contains("longitude") && settings.contains("floor")) ((MainActivity)getActivity()).setFindFrag();
                else find.setError("Set a location first");
            }
        });

        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).setTimerFrag();
            }
        });
        spots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).setToSpotsFrag();
            }
        });
    }
}
