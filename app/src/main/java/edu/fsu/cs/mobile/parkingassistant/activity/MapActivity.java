package edu.fsu.cs.mobile.parkingassistant.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import android.location.LocationListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.androidsdk.plugins.building.BuildingPlugin;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import edu.fsu.cs.mobile.parkingassistant.adapter.LocationRecyclerViewAdapter;
import edu.fsu.cs.mobile.parkingassistant.model.IndividualLocation;
import edu.fsu.cs.mobile.parkingassistant.util.LinearLayoutManagerWithSmoothScroller;
import com.mapbox.turf.TurfConversion;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import edu.fsu.cs.mobile.parkingassistant.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.core.constants.Constants.PRECISION_6;
/**
 * Activity with a Mapbox map and recyclerview to view various locations
 */
public class MapActivity extends AppCompatActivity implements LocationRecyclerViewAdapter.ClickListener, LocationListener {
  private static  LatLng MOCK_DEVICE_LOCATION_LAT_LNG ;
  private static final int MAPBOX_LOGO_OPACITY = 75;
  private static final int CAMERA_MOVEMENT_SPEED_IN_MILSECS = 1200;
  private static final float NAVIGATION_LINE_WIDTH = 9;
  private DirectionsRoute currentRoute;
  private MapboxMap mapboxMap;
  private MapView mapView;
  private MapboxDirections directionsApiClient;
  private RecyclerView locationsRecyclerView;
  private ArrayList<IndividualLocation> listOfIndividualLocations;
  private CustomThemeManager customThemeManager;
  private LocationRecyclerViewAdapter styleRvAdapter;
  private int chosenTheme;
  private LocationManager manager;
  double longitude;
  double latitude;
  private FirebaseFirestore database;
  private ArrayList<LatLng> list;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    database = FirebaseFirestore.getInstance();

    manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    if(this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
      manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, this);
    }
    
    getlocation();
    // Configure the Mapbox access token. Configuration can either be called in your application
    // class or in the same activity which contains the mapview.
    Mapbox.getInstance(this, getString(R.string.access_token));

    // Hide the status bar for the map to fill the entire screen
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    // Inflate the layout with the the MapView. Always inflate this after the Mapbox access token is configured.
    setContentView(R.layout.activity_map);

    // Initialize a list of IndividualLocation objects for future use with recyclerview
    listOfIndividualLocations = new ArrayList<>();

    // Initialize the theme that was selected in the previous activity. The blue theme is set as the backup default.
    chosenTheme = R.style.AppTheme_Neutral;

    // Set up the Mapbox map
    mapView = findViewById(R.id.mapView);
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(final MapboxMap mapboxMap) {

        // Setting the returned mapboxMap object (directly above) equal to the "globally declared" one
        MapActivity.this.mapboxMap = mapboxMap;

        // Initialize the custom class that handles marker icon creation and map styling based on the selected theme
        customThemeManager = new CustomThemeManager(chosenTheme, MapActivity.this, mapView, mapboxMap);

        // Adjust the opacity of the Mapbox logo in the lower left hand corner of the map
        ImageView logo = mapView.findViewById(R.id.logoView);
        logo.setImageAlpha(MAPBOX_LOGO_OPACITY);

        list = new ArrayList<>();
        database.collection("spots").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
          @Override
          public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {
              for (DocumentSnapshot document : task.getResult()) {
                list.add(new LatLng(Double.valueOf(document.get("latitude").toString()), Double.valueOf(document.get("longitude").toString())));
              }
              populate();
            }
          }
        });
      }
    });
  }

  private void populate() {
    for (int x = 0; x < list.size(); x++) {
      //Feature singleLocation = featureList.get(x);
      LatLng singleLocation = list.get(x);

      // Get the single location's LatLng coordinates
      Double stringLong = singleLocation.getLongitude();
      Double stringLat = singleLocation.getLatitude();

      // Create a new LatLng object with the Position object created above
      LatLng singleLocationLatLng = new LatLng(stringLat, stringLong);

      // Add the location to the Arraylist of locations for later use in the recyclerview
      listOfIndividualLocations.add(new IndividualLocation(singleLocationLatLng));
      Log.i("MapActivity", String.valueOf(singleLocationLatLng));
      // Add the location's marker to the map
      mapboxMap.addMarker(new MarkerOptions()
              .position(singleLocationLatLng)
              .icon(customThemeManager.getUnselectedMarkerIcon()));

      // Call getInformationFromDirectionsApi() to eventually display the location's
      // distance from mocked device location
      getInformationFromDirectionsApi(singleLocationLatLng.getLatitude(),
              singleLocationLatLng.getLongitude(), false, x);
    }

    // Add the fake device location marker to the map. In a real use case scenario, the Mapbox location layer plugin
    // can be used to easily display the device's location
    setUpMarkerClickListener();
    setUpRecyclerViewOfLocationCards(chosenTheme);
  }

  private void getlocation() {
    if(this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
      if (location != null) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        MOCK_DEVICE_LOCATION_LAT_LNG = new LatLng(latitude, longitude);
      }
    } else {
      this.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
    }
  }

  @Override
  public void onItemClick(int position) {

    // Get the selected individual location via its card's position in the recyclerview of cards
    IndividualLocation selectedLocation = listOfIndividualLocations.get(position);

    // Reposition the map camera target to the selected marker
    LatLng selectedLocationLatLng = selectedLocation.getLocation();
    repositionMapCamera(selectedLocationLatLng);

    // Check for an internet connection before making the call to Mapbox Directions API
    if (deviceHasInternetConnection()) {
      // Start call to the Mapbox Directions API
      getInformationFromDirectionsApi(selectedLocationLatLng.getLatitude(),
        selectedLocationLatLng.getLongitude(), true, null);
    } else {
      Toast.makeText(this, R.string.no_internet_message, Toast.LENGTH_LONG).show();
    }
  }

  private void getInformationFromDirectionsApi(double destinationLatCoordinate, double destinationLongCoordinate,
                                               final boolean fromMarkerClick, @Nullable final Integer listIndex) {
    // Set up origin and destination coordinates for the call to the Mapbox Directions API

    Point mockCurrentLocation = Point.fromLngLat(MOCK_DEVICE_LOCATION_LAT_LNG.getLongitude(),
      MOCK_DEVICE_LOCATION_LAT_LNG.getLatitude());
    Point destinationMarker = Point.fromLngLat(destinationLongCoordinate, destinationLatCoordinate);

    // Initialize the directionsApiClient object for eventually drawing a navigation route on the map
    directionsApiClient = MapboxDirections.builder()
      .origin(mockCurrentLocation)
      .destination(destinationMarker)
      .overview(DirectionsCriteria.OVERVIEW_FULL)
      .profile(DirectionsCriteria.PROFILE_DRIVING)
      .accessToken(getString(R.string.access_token))
      .build();
    Log.i("MapActivity", String.valueOf(mockCurrentLocation));
    directionsApiClient.enqueueCall(new Callback<DirectionsResponse>() {
      @Override
      public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
        // Check that the response isn't null and that the response has a route
        if (response.body() == null) {
          Log.e("MapActivity", "No routes found, make sure you set the right user and access token.");
        } else if (response.body().routes().size() < 1) {
          Log.e("MapActivity", "No routes found");
        } else {
          if (fromMarkerClick) {
            // Retrieve and draw the navigation route on the map
            currentRoute = response.body().routes().get(0);
            drawNavigationPolylineRoute(currentRoute);
          } else {
            // Use Mapbox Turf helper method to convert meters to miles and then format the mileage number
            DecimalFormat df = new DecimalFormat("#.#");
            String finalConvertedFormattedDistance = String.valueOf(df.format(TurfConversion.convertLength(
              response.body().routes().get(0).distance(), "meters", "miles")));

            // Set the distance for each location object in the list of locations
            if (listIndex != null) {
              listOfIndividualLocations.get(listIndex).setDistance(finalConvertedFormattedDistance);
              // Refresh the displayed recyclerview when the location's distance is set
              styleRvAdapter.notifyDataSetChanged();
            }
          }
        }
      }

      @Override
      public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
        Toast.makeText(MapActivity.this, R.string.failure_to_retrieve, Toast.LENGTH_LONG).show();
      }
    });
  }

  private void repositionMapCamera(LatLng newTarget) {
    CameraPosition newCameraPosition = new CameraPosition.Builder()
      .target(newTarget)
      .build();
    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition), CAMERA_MOVEMENT_SPEED_IN_MILSECS);
  }


  private void setUpRecyclerViewOfLocationCards(int chosenTheme) {
    // Initialize the recyclerview of location cards and a custom class for automatic card scrolling
    locationsRecyclerView = findViewById(R.id.map_layout_rv);
    locationsRecyclerView.setHasFixedSize(true);
    locationsRecyclerView.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(this));
    styleRvAdapter = new LocationRecyclerViewAdapter(listOfIndividualLocations,
      getApplicationContext(), this, chosenTheme);
    locationsRecyclerView.setAdapter(styleRvAdapter);
    SnapHelper snapHelper = new LinearSnapHelper();
    snapHelper.attachToRecyclerView(locationsRecyclerView);
  }

  private void setUpMarkerClickListener() {
    mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
      @Override
      public boolean onMarkerClick(@NonNull Marker marker) {

        // Get the position of the selected marker
        LatLng positionOfSelectedMarker = marker.getPosition();

        // Check that the selected marker isn't the mock device location marker
        if (!marker.getPosition().equals(MOCK_DEVICE_LOCATION_LAT_LNG)) {

          for (int x = 0; x < mapboxMap.getMarkers().size(); x++) {
            if (mapboxMap.getMarkers().get(x).getPosition() == positionOfSelectedMarker) {
              // Scroll the recyclerview to the selected marker's card. It's "x-1" below because
              // the mock device location marker is part of the marker list but doesn't have its own card
              // in the actual recyclerview.
              locationsRecyclerView.smoothScrollToPosition(x);
            }
          }
        }
        // Return true so that the selected marker's info window doesn't pop up
        return true;
      }
    });
  }

  private void drawNavigationPolylineRoute(DirectionsRoute route) {
    // Check for and remove a previously-drawn navigation route polyline before drawing the new one
    if (mapboxMap.getPolylines().size() > 0) {
      mapboxMap.removePolyline(mapboxMap.getPolylines().get(0));
    }

    // Convert LineString coordinates into a LatLng[]
    LineString lineString = LineString.fromPolyline(route.geometry(), PRECISION_6);
    List<Point> coordinates = lineString.coordinates();
    LatLng[] polylineDirectionsPoints = new LatLng[coordinates.size()];
    for (int i = 0; i < coordinates.size(); i++) {
      polylineDirectionsPoints[i] = new LatLng(
        coordinates.get(i).latitude(),
        coordinates.get(i).longitude());
    }

    // Draw the navigation route polyline on to the map
    mapboxMap.addPolyline(new PolylineOptions()
      .add(polylineDirectionsPoints)
      .color(customThemeManager.getNavigationLineColor())
      .width(NAVIGATION_LINE_WIDTH));
  }

  // Add the mapView's lifecycle to the activity's lifecycle methods
  @Override
  public void onResume() {
    super.onResume();
    mapView.onResume();
  }

  @Override
  protected void onStart() {
    super.onStart();
    mapView.onStart();
  }

  @Override
  protected void onStop() {
    super.onStop();
    mapView.onStop();
  }

  @Override
  public void onPause() {
    super.onPause();
    mapView.onPause();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mapView.onDestroy();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mapView.onSaveInstanceState(outState);
  }

  private boolean deviceHasInternetConnection() {
    ConnectivityManager connectivityManager = (ConnectivityManager)
      getApplicationContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
    return activeNetwork != null && activeNetwork.isConnected();
  }

  @Override
  public void onLocationChanged(Location location) {}

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {}

  @Override
  public void onProviderEnabled(String provider) {}

  @Override
  public void onProviderDisabled(String provider) {}

  /**
   * Custom class which creates marker icons and colors based on the selected theme
   */
  class CustomThemeManager {
    private static final String BUILDING_EXTRUSION_COLOR = "#c4dbed";
    private static final float BUILDING_EXTRUSION_OPACITY = .8f;
    private Icon unselectedMarkerIcon;
    private int navigationLineColor;

    CustomThemeManager(int selectedTheme, Context context,
                       MapView mapView, MapboxMap mapboxMap) {
    }

    Icon getUnselectedMarkerIcon() {
      return unselectedMarkerIcon;
    }
    int getNavigationLineColor() {
      return navigationLineColor;
    }
  }
}
