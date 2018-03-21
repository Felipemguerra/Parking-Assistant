package edu.fsu.cs.mobile.parkingassistant;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private FragmentManager manager = getFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //if shared preferences not initialized, initialise them, locationSet is false
        //stores coordinates and floor
        setMainFrag();
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
}

