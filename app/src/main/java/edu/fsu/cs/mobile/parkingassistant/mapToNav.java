package edu.fsu.cs.mobile.parkingassistant;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.services.android.location.LostLocationEngine;
import com.mapbox.services.android.navigation.ui.v5.NavigationContract;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.location.LocationEnginePriority;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;

import java.util.List;

public class mapToNav extends AppCompatActivity implements LocationEngineListener,
        PermissionsListener {

    private double latitudeDestination;
    private double longitudeDestination;


    private LatLng origin;
    private LatLng destinationCoord;
    private LatLng originCoord;

    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationPlugin;
    private LocationEngine locationEngine;
    private Location originLocation;
    private Point originPosition;
    private Point destinationPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_to_nav);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            latitudeDestination = bundle.getDouble("latitude");
            longitudeDestination = bundle.getDouble("longitude");

        }
        Mapbox.getInstance(this, "pk.eyJ1IjoicmVobHVjdCIsImEiOiJjamY2NjljYWowMGpwNDBuODh4Z2g4YTZmIn0.Bjo0LAOxeHjkE8-VcFSmkQ");
        enableLocationPlugin();
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationPlugin() {
        Log.i("88", "88");
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Create an instance of LOST location engine
            Log.i("~~", "~~");
            initializeLocationEngine();
        } else {
            Log.i("==", "==");
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void initializeLocationEngine() {
        locationEngine = new LostLocationEngine(this);
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();
        Log.i("hoh", "pip");
        Location lastLocation = locationEngine.getLastLocation();
        Log.i("hoh", String.valueOf(lastLocation));
        if (lastLocation != null) {
            Log.i("==", "butbut");
            originLocation = lastLocation;
            locationEngine.addLocationEngineListener(this);
            //setCameraPosition(lastLocation);
        } else {
            Log.i("==", "chuchu");
            locationEngine.addLocationEngineListener(this);
        }
    }

    private void setCameraPosition(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 13));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationPlugin();
        } else {
            finish();
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onConnected() {
        locationEngine.requestLocationUpdates();

    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("here");
        if (location != null) {
            originLocation = location;

            originCoord = new LatLng(originLocation.getLatitude(), originLocation.getLongitude());
            originPosition = Point.fromLngLat(originCoord.getLongitude(), originCoord.getLatitude());

            destinationCoord = new LatLng(latitudeDestination,  longitudeDestination);
            destinationPosition = Point.fromLngLat(destinationCoord.getLongitude(), destinationCoord.getLatitude());

            Point origin = originPosition;
            Point destination = destinationPosition;
            // Pass in your Amazon Polly pool id for speech synthesis using Amazon Polly
            // Set to null to use the default Android speech synthesizer
            String awsPoolId = null;
            boolean simulateRoute = false;
            NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                    .origin(origin)
                    .destination(destination)
                    .awsPoolId(awsPoolId)
                    .shouldSimulateRoute(simulateRoute)
                    .build();

            // Call this method with Context from within an Activity
            NavigationLauncher.startNavigation(this,options);
            /*setCameraPosition(location);*/
            locationEngine.removeLocationEngineListener(this);
            finish();
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onStart() {
        super.onStart();
        if (locationEngine != null) {
            locationEngine.requestLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStart();
        }
        // mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStop();
        }
        //mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // mapView.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //mapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        //mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //   mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //   mapView.onSaveInstanceState(outState);
    }

}
