package edu.fsu.cs.mobile.parkingassistant;

import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Felipe on 3/25/2018.
 */

public class FindLocationFragment extends Fragment implements SensorEventListener {

    private Button backBtn;

    private ImageView arrow;

    private TextView floorText;

    private String floor;

    private double originLat = 30.444630;
    private double originLong = -84.298605;

    private SensorManager manager;
    private Sensor sensor1;
    private Sensor sensor2;

    private final float[] accelReading = new float[3];
    private final float[] magReading = new float[3];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_find_location, container, false);
        floor = "4"; // get floor from shared preferences
        //get origin lat and long
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
        String text = getString(R.string.floorStart)+floor+getString(R.string.floorEnd);
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
        //get current coor, then calculate azimuth to destination
        //then get current azimuth and update arrow based on both azimuths
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
}
