package com.matics.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.matics.Utils.Utility;

public class LocationHelper implements LocationSource {

	static Geocoder geocoder;
	static String MAIN_URL = "https://maps.googleapis.com/maps/api/directions/json?";
	static String originSpot = "origin=%s";
	static String destinationSpot = "destination=%s";
	String key_routes = "routes", key_legs = "legs", key_distance = "distance";
	static String unit_measure = "units=metric";
	static GoogleMap googleMap;
	static Location myLocation;
	LocationManager locationManager;
	boolean isProviderEnabled;
	// dummy lat-long
	double latitude = 32.8119771, longitude = -79.9543551;
	OnLocationChangedListener mLocationChangedListener;

	public LocationHelper(Context context) {
		super();
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
	}

	public String getDistanceUsingDirectionApi(String source, String destination)
			throws Exception {
		source = source.replace(" ", "%20");
		destination = destination.replace(" ", "%20");
		String originSpot = String.format("origin=%s", source);
		String destinationSpot = String.format("destination=%s", destination);
		String urlString = new StringBuilder().append(MAIN_URL).append(originSpot)
				.append("&").append(destinationSpot).append("&")
				.append(unit_measure).toString();
		System.out.println("URL to test: \n" + urlString);
		String response = Utility.httpGet(urlString);

		return response;
	}

	public double getDistance(String urlResponse) throws JSONException {
		double distance = 0.0;
		JSONObject jsonObject = new JSONObject(urlResponse);
		JSONArray jsonArray = jsonObject.getJSONArray(key_routes);
		JSONObject route = jsonArray.getJSONObject(0);
		JSONArray legs = route.getJSONArray(key_legs);
		JSONObject steps = legs.getJSONObject(0);
		JSONObject distanceObject = steps.getJSONObject(key_distance);
		distance = Double.parseDouble(distanceObject.getString("text")
				.replaceAll("[^\\.0123456789]", ""));
		distance = Utility.roundValue(distance*0.621371, 2);
		System.out.println("Rounded Miles: "+ distance);
		return distance;
	}

	public Location getLocationManaging() throws Exception {
		isProviderEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!isProviderEnabled)
			isProviderEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (!isProviderEnabled)
			isProviderEnabled = locationManager
					.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
		System.out.println("Location Provider enabled: " + isProviderEnabled);
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, false);
		return locationManager.getLastKnownLocation(provider);
	}

	public void setUpMapOptions(GoogleMap map)  throws Exception{
		googleMap = map;
		map.getUiSettings().setZoomControlsEnabled(true);
		map.getUiSettings().setMyLocationButtonEnabled(true);
		map.setMyLocationEnabled(true);
		map.setLocationSource(this);
		map.setOnMyLocationButtonClickListener(myLocationButtonClickListener);
		myLocation = getLocationManaging();
		if (myLocation == null) {
			myLocation = new Location(LocationManager.GPS_PROVIDER);
			myLocation.setLatitude(latitude);
			myLocation.setLongitude(longitude);
		}
		addMarker(myLocation, "Me", BitmapDescriptorFactory.HUE_GREEN);
		zoomToLocation(myLocation);
	}

	public void addMarker(Location location, String title, float markerStyle) {
		if (googleMap != null) {
			googleMap.addMarker(new MarkerOptions()
					.icon(BitmapDescriptorFactory.defaultMarker(markerStyle))
					.position(
							new LatLng(location.getLatitude(), location
									.getLongitude())).title(title));
		}
	}

	OnMyLocationButtonClickListener myLocationButtonClickListener = new OnMyLocationButtonClickListener() {
		@Override
		public boolean onMyLocationButtonClick() {
			zoomToLocation(googleMap.getMyLocation());
			return false;
		}
	};

	private void zoomToLocation(Location location) {
		if (googleMap != null) {
			if (location != null) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				System.out.println("Location Provider: "
						+ location.getLongitude());
			}
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
					new LatLng(latitude, longitude), 10);
			googleMap.animateCamera(cameraUpdate, new CancelableCallback() {
				@Override
				public void onFinish() {
				}

				@Override
				public void onCancel() {
				}
			});
		}
	}

	public void zoomToLatLng(LatLng latLng) {
		if (googleMap != null) {
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
					latLng, 10);
			googleMap.animateCamera(cameraUpdate, new CancelableCallback() {
				public void onFinish() {
				}

				public void onCancel() {
				}
			});
		}
	}

	@Override
	public void activate(OnLocationChangedListener changedListener) {
		mLocationChangedListener = changedListener;
	}

	@Override
	public void deactivate() {
		mLocationChangedListener = null;
	}

}
