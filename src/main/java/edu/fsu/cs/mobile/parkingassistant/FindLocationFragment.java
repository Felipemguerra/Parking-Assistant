package edu.fsu.cs.mobile.parkingassistant;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FindLocationFragment extends Fragment {

    private Button backBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_find_location, container, false);
        getViews(v);
        setText();
        setListeners();
        return v;
    }

    private void getViews(View v) {
        backBtn = v.findViewById(R.id.findBackBtn);
    }

    private void setText() {
        backBtn.setText(getString(R.string.goback));
    }

    private void setListeners(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).setMainFrag();
            }
        });
    }
}
