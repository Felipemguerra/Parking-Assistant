package edu.fsu.cs.mobile.parkingassistant;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.List;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

// classes needed to initialize map
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;

// classes needed to add location layer
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import android.location.Location;

import com.mapbox.mapboxsdk.geometry.LatLng;

import android.support.annotation.NonNull;

import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.services.android.location.LostLocationEngine;
import com.mapbox.services.android.navigation.ui.v5.NavigationActivity;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.location.LocationEnginePriority;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;

// classes needed to add a marker
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;




// classes to calculate a route
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;

// classes needed to launch navigation UI
import android.view.View;
import android.widget.Button;

import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;




public class navigation extends Fragment implements LocationEngineListener,
PermissionsListener {

    private LatLng origin;
    private LatLng destinationCoord;
    private LatLng originCoord;

    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationPlugin;
    private LocationEngine locationEngine;
    private Location originLocation;
    Double latitudeDestination ;

    Double longitudeDestination;

    private MapView mapView;

    private Point originPosition;


    private Point destinationPosition;

    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    private Marker destinationMarker;
    private double lat ,longy;
    public navigation() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Bundle bundle = this.getArguments();
        if (bundle != null) {
           latitudeDestination = bundle.getDouble("latitude");

           longitudeDestination = bundle.getDouble("longitude");
           // Log.i("navi", String.valueOf(latitudeDestinatdoubleion)+" "+ longitudeDestination);
        }

        View v = inflater.inflate(R.layout.fragment_navigation, container, false);
        Mapbox.getInstance(this.getContext(), "pk.eyJ1IjoicmVobHVjdCIsImEiOiJjamY2NjljYWowMGpwNDBuODh4Z2g4YTZmIn0.Bjo0LAOxeHjkE8-VcFSmkQ");
        /*
        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);*/

        /*
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                map = mapboxMap;
                enableLocationPlugin();
            }
        } );*/
        enableLocationPlugin();

        return v;
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this.getContext())) {
            // Create an instance of LOST location engine
            initializeLocationEngine();
/*
            locationPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
            locationPlugin.setLocationLayerEnabled(LocationLayerMode.TRACKING);
            */
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this.getActivity());
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void initializeLocationEngine() {
        locationEngine = new LostLocationEngine(this.getContext());
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();
        Log.i("hoh", "pip");
        Location lastLocation = locationEngine.getLastLocation();
        Log.i("hoh", String.valueOf(lastLocation));
        if (lastLocation != null) {
            originLocation = lastLocation;
            //setCameraPosition(lastLocation);

        } else {
            locationEngine.addLocationEngineListener(this);
            Log.i("hoh", "pyyp");
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
            getActivity().finish();
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onConnected() {
        locationEngine.requestLocationUpdates();

    }

    @Override
    public void onLocationChanged(Location location) {

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
            boolean simulateRoute = true;
            NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                    .origin(origin)
                    .destination(destination)
                    .awsPoolId(awsPoolId)
                    .shouldSimulateRoute(simulateRoute)
                    .build();

            // Call this method with Context from within an Activity
            NavigationLauncher.startNavigation(this.getActivity(), options);
            /*setCameraPosition(location);*/
            locationEngine.removeLocationEngineListener(this);
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