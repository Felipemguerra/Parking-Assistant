package edu.fsu.cs.mobile.parkingassistant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class SetLocationFragment extends android.app.Fragment {

    private TextView title;
    private TextView instruct;

    private Spinner floors;

    private Button setBtn;
    private Button backBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_set_location, container, false);
        getViews(v);
        setText();
        setListeners();
        return v;
    }

    private void getViews(View v) {
        title = v.findViewById(R.id.setTitleText);
        instruct = v.findViewById(R.id.instructText);
        floors = v.findViewById(R.id.floorSpinner);
        setBtn = v.findViewById(R.id.locationBtn);
        backBtn = v.findViewById(R.id.setBackBtn);
    }

    private void setText() {
        title.setText(getString(R.string.locationTitle));
        instruct.setText(getString(R.string.instruct));
        setBtn.setText(getString(R.string.set));
        backBtn.setText(getString(R.string.goback));
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.spinnerArray, R.layout.support_simple_spinner_dropdown_item);
        floors.setAdapter(adapter);
    }

    private void setListeners() {
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(floors.getSelectedItemPosition() == 0) setBtn.setError("Please select your floor");
                else {
                    //get coordinates and save in shared preferences
                    //save floor
                    //set locationSet to true
                    ((MainActivity)getActivity()).setMainFrag();
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).setMainFrag();
            }
        });
    }
}
