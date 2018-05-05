package edu.fsu.cs.mobile.parkingassistant.adapter;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

//import com.mapbox.storelocator.R;
import com.mapbox.mapboxsdk.geometry.LatLng;

import edu.fsu.cs.mobile.parkingassistant.MainActivity;
import edu.fsu.cs.mobile.parkingassistant.activity.MapActivity;
import edu.fsu.cs.mobile.parkingassistant.mapToNav;
import edu.fsu.cs.mobile.parkingassistant.model.IndividualLocation;

import java.util.List;

import edu.fsu.cs.mobile.parkingassistant.R;
import edu.fsu.cs.mobile.parkingassistant.navigation;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

/**
 * RecyclerView adapter to display a list of location cards on top of the map
 */
public class LocationRecyclerViewAdapter extends
  RecyclerView.Adapter<LocationRecyclerViewAdapter.ViewHolder> {

  private List<IndividualLocation> listOfLocations;
  private Context context;
  LatLng singleLocationLatLng;

  public LocationRecyclerViewAdapter(List<IndividualLocation> styles, Context context, ClickListener cardClickListener, int selectedTheme) {
    this.context = context;
    this.listOfLocations = styles;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    int singleRvCardToUse = R.layout.single_location_map_view_rv_card;
    View itemView = LayoutInflater.from(parent.getContext()).inflate(singleRvCardToUse, parent, false);
    return new ViewHolder(itemView);
  }

  public interface ClickListener {
    void onItemClick(int position);
  }

  @Override
  public int getItemCount() {
    return listOfLocations.size();
  }

  @Override
  public void onBindViewHolder(ViewHolder card, int position) {
    IndividualLocation locationCard = listOfLocations.get(position);

    singleLocationLatLng = locationCard.getLocation();
  }


  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    CardView cardView;

    ViewHolder(View itemView) {
      super(itemView);
      cardView = itemView.findViewById(R.id.map_view_location_card);
      ConstraintLayout layout = itemView.findViewById(R.id.navi2);
      layout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          fam();
        }
      });
    }

    private void fam() {
      Intent intent = new Intent(context,mapToNav.class);
      Log.i("button","clickly " + " hah " + singleLocationLatLng.getLatitude());
      intent.putExtra ("latitude",singleLocationLatLng.getLatitude());
      intent.putExtra("longitude",singleLocationLatLng.getLongitude());
      context.startActivity(intent);
    }

    @Override
    public void onClick(View view) {
    }
  }
}
