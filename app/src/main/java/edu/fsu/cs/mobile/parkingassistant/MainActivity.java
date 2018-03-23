package edu.fsu.cs.mobile.parkingassistant;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private FragmentManager manager = getFragmentManager();
    private TimerService timer;
    private TimerInterface interf;
    private ServiceConnection connection;
    private boolean bound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //if shared preferences not initialized, initialise them, locationSet is false
        //stores coordinates and floor
        setMainFrag();
    }

    public void setTimer() {
        interf = new TimerInterface();
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                TimerService.LocalBinder binder = (TimerService.LocalBinder) iBinder;
                timer = binder.getService();
                timer.connect(interf);
                bound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                timer.onDestroy();
            }
        };
        Intent service = new Intent(this, TimerService.class);
        bindService(service, connection, Context.BIND_AUTO_CREATE);
    }

    public void setMainFrag() {
        MainFragment mainFrag = new MainFragment();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragContainer, mainFrag, "mainFrag");
        transaction.commit();
    }

    public void setLocationFrag() {
        SetLocationFragment setFrag = new SetLocationFragment();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragContainer, setFrag, "setFrag");
        transaction.commit();
    }

    public void setFindFrag() {
        FindLocationFragment findFrag = new FindLocationFragment();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragContainer, findFrag, "findFrag");
        transaction.commit();
    }

    public void setTimerFrag() {
        TimerFragment timerFrag = new TimerFragment();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragContainer, timerFrag, "timerFrag");
        transaction.commit();
    }

    public void cancelTimer() {
        if(bound) {
            unbindService(connection);
            timer.cancel();
        }
        bound = false;
    }

    public class TimerInterface{
        void finish() {
            bound = false;
            unbindService(connection);
            timer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bound) unbindService(connection);
    }
}

