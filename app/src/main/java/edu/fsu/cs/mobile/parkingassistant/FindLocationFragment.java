package edu.fsu.cs.mobile.parkingassistant;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Felipe on 3/25/2018.
 */

public class FindLocationFragment extends Fragment implements SensorEventListener, LocationListener {

    private Button backBtn;

    private ImageView arrow;

    private TextView floorText;

    private String floor;

    private double originLat;
    private double originLong;

    private SensorManager manager;
    private Sensor sensor1;
    private Sensor sensor2;

    private final float[] accelReading = new float[3];
    private final float[] magReading = new float[3];

    private LocationManager lmanager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_find_location, container, false);
        SharedPreferences settings = getActivity().getSharedPreferences("info", 0);
        originLat = Double.parseDouble(settings.getString("latitude","30.444630"));
        originLong = Double.parseDouble(settings.getString("longitude","-84.298605"));
        floor = settings.getString("floor","NaN");

        lmanager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            lmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, this);
        }

        getViews(v);
        setText();
        setListeners();

        manager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensor1 = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensor2 = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        return v;
    }

    private void getViews(View v) {
        backBtn = v.findViewById(R.id.findBackBtn);
        arrow = v.findViewById(R.id.arrowImg);
        floorText = v.findViewById(R.id.floorText);
    }

    private void setText() {
        String text = getString(R.string.floorStart)+floor;
        floorText.setText(text);
    }

    private void setListeners() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).setMainFrag();
            }
        });
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelReading,
                    0, accelReading.length);
            updateArrow();
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magReading,
                    0, magReading.length);
            updateArrow();
        }
    }

    private void updateArrow() {
        float[] angles =  new float[3];
        float[] matrix = new float[9];
        manager.getRotationMatrix(matrix, null, accelReading, magReading);
        manager.getOrientation(matrix, angles);

        double longitude = 0;
        double latitude = 0;
         if(getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
           Location location = lmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
           if(location != null) {
               latitude = location.getLatitude();
               longitude = location.getLongitude();
               float degree = (float)(Math.toDegrees(angles[0])+360)%360;
               double cngLat = originLat - latitude;
               double cngLong = originLong - longitude;
               if(Math.abs(cngLat) > Math.abs(cngLong)) {
                   if(cngLat > 0) {
                       //need to go north
                       if(degree < 45 || degree > 315) changeArrowUp();
                       else if(degree > 45 && degree < 135) changeArrowLeft();
                       else if(degree > 135 && degree < 225) changeArrowDown();
                       else changeArrowRight();
                   }
                   else {
                       //need to go south
                       if(degree < 45 || degree > 315) changeArrowDown();
                       else if(degree > 45 && degree < 135) changeArrowRight();
                       else if(degree > 135 && degree < 225) changeArrowUp();
                       else changeArrowLeft();
                   }
               }
               else {
                   if(cngLong > 0) {
                       //need to go east
                       if(degree < 45 || degree > 315) changeArrowRight();
                       else if(degree > 45 && degree < 135) changeArrowUp();
                       else if(degree > 135 && degree < 225) changeArrowLeft();
                       else changeArrowDown();
                   }
                   else {
                       //need to go west
                       if(degree < 45 || degree > 315) changeArrowLeft();
                       else if(degree > 45 && degree < 135) changeArrowDown();
                       else if(degree > 135 && degree < 225) changeArrowRight();
                       else changeArrowUp();
                   }
               }
           }
           else{
               arrow.setImageResource(R.drawable.error);
           }
        }
        else {
             getActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
         }
    }

    private void changeArrowUp() {
        arrow.setImageResource(R.drawable.uparrow);
    }
    private void changeArrowDown() {
        arrow.setImageResource(R.drawable.downarrow);
    }
    private void changeArrowRight() {
        arrow.setImageResource(R.drawable.rightarrow);
    }
    private void changeArrowLeft() {
        arrow.setImageResource(R.drawable.leftarrow);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onResume() {
        super.onResume();
        manager.registerListener(this, sensor1, SensorManager.SENSOR_DELAY_NORMAL);
        manager.registerListener(this, sensor2, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public  void onPause() {
        super.onPause();
        manager.unregisterListener(this);
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
