package edu.fsu.cs.mobile.parkingassistant;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SetLocationFragment extends android.app.Fragment implements LocationListener{

    private Button setBtn;
    private Button backBtn;

    private LocationManager manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_set_location, container, false);
        manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, this);
        }
        getViews(v);
        setText();
        setListeners();
        return v;
    }

    private void getViews(View v) {
        setBtn = v.findViewById(R.id.locationBtn);
        backBtn = v.findViewById(R.id.setBackBtn);
    }

    private void setText() {
        setBtn.setText(getString(R.string.set));
        backBtn.setText(getString(R.string.goback));
    }

    private void setListeners() {
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double longitude;
                double latitude;
                if(getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        SharedPreferences settings = getActivity().getSharedPreferences("info", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("latitude", String.valueOf(latitude));
                        editor.putString("longitude", String.valueOf(longitude));
                        editor.commit();
                        ((MainActivity) getActivity()).setMainFrag();
                    }
                    else{
                        setBtn.setError("Problem fetching location");
                    }
                }
                else {
                    getActivity().requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
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

    @Override
    public void onLocationChanged(Location location) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}
}
