package edu.fsu.cs.mobile.parkingassistant.model;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * POJO class for an individual location
 */
public class IndividualLocation {

  private String name;
  private String distance;
  private LatLng location;

  public IndividualLocation(LatLng location) {
    this.location = location;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDistance() {
    return distance;
  }

  public void setDistance(String distance) {
    this.distance = distance;
  }

  public LatLng getLocation() {
    return location;
  }
}
