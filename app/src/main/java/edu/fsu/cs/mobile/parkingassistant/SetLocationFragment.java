package edu.fsu.cs.mobile.parkingassistant;

import android.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
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
                    double longitude = 0;
                    double latitude = 0;
                    if(getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        LocationManager lmanager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                        Location location = lmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if(location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            SharedPreferences settings = getActivity().getSharedPreferences("info", 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("latitude", String.valueOf(latitude));
                            editor.putString("longitude", String.valueOf(longitude));
                            editor.putString("floor", floors.getSelectedItem().toString());
                            editor.commit();
                            ((MainActivity)getActivity()).setMainFrag();
                        }
                    }
                    else {
                        getActivity().requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                    }
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
