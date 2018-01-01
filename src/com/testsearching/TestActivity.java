package com.testsearching;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TestActivity extends FragmentActivity implements LocationListener {
	private static final float DEFAULTZOOM = 15;
	private GoogleMap mMap;
	private EditText etSearch;
	private ListView searchResultList;
	protected double lat;
	protected double lon;
	private List<Address> list;
	private Context mContext;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	boolean canGetLocation = false;
	String locality;
	Location location; // location
	double latitude; // latitude
	double longitude; // longitude

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES =  1000 * 1; // 1
																		// minute

	// Declaring a Location Manager
	protected LocationManager locationManager;
	private Button btnCurrentLocation;
	private Marker marker;
	private Document doc;
	private Button btnTraceToute;
	protected Polyline polylin;
	private Marker locMark;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mContext = this;
		getLocation();
		if (ifServicesOk()) {
			setContentView(R.layout.activity_search);

			if (initMap()) {
				goToLocation(latitude, longitude, DEFAULTZOOM, false);
			}
			Toast.makeText(this, "all set", Toast.LENGTH_SHORT).show();
		}

		searchResultList = (ListView) findViewById(R.id.searchResultList);
		searchResultList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// When clicked, show a toast with the TextView text
				lat = list.get(position).getLatitude();
				lon = list.get(position).getLongitude();
				locality = list.get(position).getLocality();
				goToLocation(lat, lon, 15, true);
				searchResultList.setVisibility(View.GONE);
				hideKeyBoard();
				Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
			}
		});
		etSearch = (EditText) findViewById(R.id.edSearch);
		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				try {
					performSearch(false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					try {
						performSearch(true);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				}
				return false;
			}

		});

		btnCurrentLocation = (Button) findViewById(R.id.btnCurrentLocation);
		btnCurrentLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getLocation();
				goToLocation(latitude, longitude, DEFAULTZOOM, false);
			}
		});

		btnTraceToute = (Button) findViewById(R.id.btnTraceRoute);
		btnTraceToute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getLocation();
				traceRoute(new LatLng(latitude, longitude), new LatLng(lat, lon));
			}
		});

		// startService(new Intent(this, ServiceTest.class));
	}

	private void traceRoute(final LatLng source, final LatLng destination) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				try {
					Document doc = (Document) msg.obj;
					GMapV2Direction md = new GMapV2Direction();

					ArrayList<LatLng> directionPoint = md.getDirection(doc);
					PolylineOptions rectLine = new PolylineOptions().width(15).color(Color.YELLOW).geodesic(true);

					for (int i = 0; i < directionPoint.size(); i++) {
						rectLine.add(directionPoint.get(i));
					}

					if (polylin != null) {
						polylin.remove();
					}
					polylin = mMap.addPolyline(rectLine);
					MarkerOptions sourceMark = new MarkerOptions().position(source);
					MarkerOptions destMark = new MarkerOptions().position(destination);
					mMap.addMarker(sourceMark);
					mMap.addMarker(destMark);

					LatLngBounds.Builder builder = new LatLngBounds.Builder();
					builder.include(source);
					builder.include(destination);
					LatLngBounds bounds = builder.build();
					int padding = 200; // offset from edges of the map in pixels
					CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
					mMap.animateCamera(cu);
					md.getDurationText(doc);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			;
		};

		new GMapV2Direction(handler, source, destination, GMapV2Direction.MODE_DRIVING).execute();
	}

	protected void hideKeyBoard() {
		View view = this.getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}

	}

	private void performSearch(boolean b) throws IOException {
		if (b)
			hideKeyBoard();
		String searchText = etSearch.getText().toString();

		Geocoder gc = new Geocoder(this);
		list = gc.getFromLocationName(searchText, 3);
		List<String> localList = new ArrayList<String>();
		for (Address address : list) {
			localList.add(address.getFeatureName() + "  " + address.getLocality());
		}
		ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, localList);
		searchResultList.setAdapter(adapter);
		searchResultList.setVisibility(View.VISIBLE);

		// Toast.makeText(this, "search successfull",
		// Toast.LENGTH_SHORT).show();

	}

	public boolean ifServicesOk() {
		int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (isAvailable == ConnectionResult.SUCCESS) {
			return true;
		} else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, 9001);
		}
		return false;
	}

	private boolean initMap() {
		if (mMap == null) {

			SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.mapFrag);
			mMap = mapFrag.getMap();
		}
		return (mMap != null);
	}

	private void goToLocation(double lat, double lon, float defaultzoom, boolean putMarker) {
		LatLng ll = new LatLng(lat, lon);
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, defaultzoom);
		if (putMarker) {
			if (marker != null) {
				marker.remove();
			}
			marker = mMap.addMarker(new MarkerOptions().position(ll).title(locality));
			;
		}
		mMap.animateCamera(update);
	}

	public Location getLocation() {
		try {
			locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {
				this.canGetLocation = true;

				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {
							location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}

				// if not gps then get location from Network Provider
				else if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.d("Network", "Network");
					if (locationManager != null) {
						location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return location;
	}

	@Override
	public void onLocationChanged(Location loc) {
		Toast.makeText(this, loc.getProvider() + " : loc changes with accuracy : " + loc.getAccuracy(),
				Toast.LENGTH_SHORT).show();
		LatLng ll = new LatLng(loc.getLatitude(), loc.getLongitude());

		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, loc.getAccuracy());
		MarkerOptions options = new MarkerOptions().position(ll)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot_circle));
		if (locMark != null) {
			locMark.remove();
		}
		options.anchor(.5f, .5f);
		locMark = mMap.addMarker(options);
		mMap.animateCamera(update);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}
}
