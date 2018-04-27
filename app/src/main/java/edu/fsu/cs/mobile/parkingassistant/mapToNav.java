package edu.fsu.cs.mobile.parkingassistant;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class mapToNav extends AppCompatActivity {
    private FragmentManager manager = getFragmentManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_to_nav);

        Bundle b= getIntent().getExtras();

        navigation setFrag = new navigation();
        setFrag.setArguments(b);
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.mapcontainer, setFrag, "setFrag");
        transaction.commit();
    }
}
