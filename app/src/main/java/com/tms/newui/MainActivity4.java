package com.tms.newui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;
import com.tms.R;
import com.tms.SharingToro;
import com.tms.activity.AboutUs;
import com.tms.activity.AddCreditCard;
import com.tms.activity.CreditCardListing;
import com.tms.activity.EditProfile;
import com.tms.activity.Login;
import com.tms.activity.PromoCodeListing;
import com.tms.activity.Reservation;
import com.tms.newui.interf.RouteInterface;
import com.tms.utility.CircleTransform;
import com.tms.utility.MarkerMoving;
import com.tms.utility.ResponseListener;
import com.tms.utility.SendXmlAsync;
import com.tms.utility.ToroSharedPrefrnce;
import com.tms.utility.URL;
import com.tms.utility.Utility;
import com.tms.utility.WebServiceAsynk;
import com.tms.utility.XmlListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity4 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    public GoogleMap mMap;
    private static String TAG = "VIREN";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private int MY_PERMISSIONS_REQUEST_LOCATION = 100;
    private TextView txtDestPage1;
    private RelativeLayout carOptionsDialog;
    private TextView srEditText, dsEditText;
    private ImageView btnDirection;
    //private Place destPlace, sourcePlace;
    private DrawerLayout drawer;
    public FloatingActionButton btnMyLocation;
    Button btnSUV, btnHybrid, btnBlack, btnLuxury, btnArmour;
    private Button btnRequest;
    public int hybridCount = 0, suvCount = 0, blackCount = 0, luxuryCount = 0, limoCount = 0;
    int taxiType = 1;
    public ToroSharedPrefrnce sharedPreference;
    private Button btnCard;
    private MarkerMoving markerMoving;
    private Bitmap carBitmap;

    SelectedBundle selectedBundle;
    private double endLat, endLNG;
    public String eta;
    boolean isBookingEPageOpened = false;
    private MarkerOptions endPointMarker;
    private Marker endMarker;
    public String mDistance, mTime;
    private SupportMapFragment mapFragment;

    public void clearMap() {
        if (routeLine != null) {
            routeLine.remove();
        }
        if (endMarker != null) {
            endMarker.remove();
        }
    }

    public void showRouteFromRiderToDriver(String source, String destinationID) {
        if (destinationID == null) {
            double secondLat = sourceLatLng.latitude;
            double secondLong = sourceLatLng.longitude;
            if (secondLat != 0 && secondLat != 0) {
                destinationID = secondLat + "," + secondLong;
            }
        }

        String sourceID = null;
        if (sourceLatLng != null && source.equalsIgnoreCase("Current location")) {
            sourceID = sourceLatLng.latitude + "," + sourceLatLng.longitude;
        } else if (sourceLatLng == null && source.equalsIgnoreCase("Current location")) {
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.cordinatorLayout), getResources().getString(R.string.route_not_found), Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        } else {
            sourceID = URLEncoder.encode(source);
        }

        if (sourceID != null && destinationID != null) {
            mDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.setMessage("Loading...");
            mDialog.setCancelable(true);
            // mDialog.show();
            final String finalSourceID = sourceID;
            RouteTask routeTask = new RouteTask(MainActivity4.this, new RouteInterface() {
                @Override
                public void onRouteLoad(List<List<HashMap<String, String>>> result, JSONObject distance, JSONObject duration) {
                    // mDialog.dismiss();
                    try {
                        mDistance = distance.getString("text");
                        mTime = duration.getString("text");
                        sharedPreference.setDistance(mDistance);
                        sharedPreference.setTime(mTime);
                        sharedPreference.setDistanceInMeter(distance.getInt("value"));
                        sharedPreference.setTimeInSeconds(distance.getInt("value"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (result != null && result.size() > 0) {
                        try {
                            drawRoute(result,true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(findViewById(R.id.cordinatorLayout), getResources().getString(R.string.route_not_found), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }

                @Override
                public void onRouteLoadFailed() {
                    mDialog.dismiss();
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.cordinatorLayout), getResources().getString(R.string.route_not_found), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });

            routeTask.execute(sourceID, URLEncoder.encode(destinationID));
        }
    }

    public interface SelectedBundle {
        void onBundleSelect(Bundle bundle);
    }

    public void setOnBundleSelected(SelectedBundle selectedBundle) {
        this.selectedBundle = selectedBundle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapHome);
        mapFragment.getMapAsync(this);
        registerForLocationUpdates();

        // WhereToFragment newFragment = new WhereToFragment();
        showFragment(new WhereToFragment());

        sharedPreference = new ToroSharedPrefrnce(this);

        btnMyLocation = (FloatingActionButton) findViewById(R.id.fab);
        btnMyLocation.setVisibility(View.GONE);
        btnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkLocationPermission()) {
                    fusedLocationProviderClient.getLastLocation()
                            .addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // GPS location can be null if GPS is switched off
                                    if (location != null) {
                                        sourceLatLng = new LatLng(location.getLatitude(),
                                                location.getLongitude());

                                        CameraPosition position = CameraPosition.builder()
                                                .target(sourceLatLng)
                                                .zoom(16)
                                                //.tilt(30)
                                                .build();
                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));

                                    } else {
                                        Snackbar snackbar = Snackbar
                                                .make(findViewById(R.id.drawer_layout), "Location not available.", Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar snackbar = Snackbar
                                            .make(findViewById(R.id.drawer_layout), "Location not available.", Snackbar.LENGTH_LONG);
                                    snackbar.show();

                                    Log.d("MapDemoActivity", "Error trying to get last GPS location");
                                    e.printStackTrace();
                                }
                            });
                }
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.title1);
        TextView email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.title2);
        TextView phone = (TextView) navigationView.getHeaderView(0).findViewById(R.id.title3);
        if (sharedPreference.getEmail() != null)
            email.setText(sharedPreference.getEmail());
        name.setText(sharedPreference.getFirstName() + " " + sharedPreference.getLastname());
        email.setText(sharedPreference.getEmail());
        phone.setText("+1-" + sharedPreference.getPhone());
        RelativeLayout editPicture = (RelativeLayout) navigationView.getHeaderView(0).findViewById(R.id.editPicture);
        editPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity4.this, EditProfile.class));

            }
        });
        ImageView profileView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        if (!sharedPreference.getImageUrl().equals("")) {
            Picasso.with(this).load(sharedPreference.getImageUrl())
                    .resize(80, 80).transform(new CircleTransform())
                    .into(profileView);

        }

       // txtDestPage1 = (TextView) findViewById(R.id.txt_dest);


        /*findViewById(R.id.btnCloseBookingPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.address_bar).setVisibility(
                        View.GONE);
                findViewById(R.id.btnCloseBookingPage).setVisibility(
                        View.GONE);
                carOptionsDialog.setVisibility(View.GONE);
                btnMyLocation.setVisibility(View.VISIBLE);
                // btnCabFilter.setVisibility(View.VISIBLE);
                hideKeybord();
                if (routeLine != null) {
                    routeLine.remove();
                }
                mMap.clear();
            }
        });*/


        /*carOptionsDialog = (RelativeLayout) findViewById(R.id.car_options_dialog);
        txtDestPage1 = (TextView) findViewById(R.id.txt_dest);
        srEditText = (TextView) findViewById(R.id.source_editText);
        dsEditText = (TextView) findViewById(R.id.destination_editText);
*/
        /*txtDestPage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new AddressSearchFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(R.id.fragment_holder, newFragment).commit();
            }
        });*/
       /* srEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findPlace(view, 1);
            }
        });

        dsEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findPlace(view, 0);
            }
        });
   */     BitmapFactory.Options options = new BitmapFactory.Options();

        //Start marker
        options.inSampleSize = 1;
        carBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.map_pin_hybrid1, options);


    }


    protected void showFragment(Fragment fragment) {
        String TAG = fragment.getClass().getSimpleName();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_holder, fragment, TAG);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commitAllowingStateLoss();
    }

    protected void backstackFragment() {
        if (routeLine != null) {
            routeLine.remove();
        }
        if (endMarker != null) {
            endMarker.remove();
        }
        Log.d("Stack count", getFragmentManager().getBackStackEntryCount() + "");
        if (getFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        }
        getFragmentManager().popBackStack();
        //removeCurrentFragment();
    }

    private void removeCurrentFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment currentFrag = getFragmentManager()
                .findFragmentById(R.id.fragment_holder);

        if (currentFrag != null) {
            transaction.remove(currentFrag);
        }
        transaction.commitAllowingStateLoss();
    }


    private void hideKeybord() {
        InputMethodManager imm = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(dsEditText.getWindowToken(), 0);

    }

    public Fragment getCurrentFragment() {
        Fragment currentFragment = getFragmentManager()
                .findFragmentById(R.id.fragment_holder);
        return currentFragment;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            switch (getCurrentFragment().getTag()) {
                case "AddressSearchFragment":
                case "BookFragment":
                    backstackFragment();
                    break;
                case "WhereToFragment":
                    finish();
                    break;
            }
        }

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity4, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        /*if (id == R.id.home_menu) {
        }*//* else if (id == R.id.nav_profile) {
            startActivity(new Intent(MainActivity4.this, MyProfile.class));
        } */
        if (id == R.id.nav_promocode) {
            startActivity(new Intent(MainActivity4.this,
                    PromoCodeListing.class).putExtra("what", 1));
        } else if (id == R.id.nav_creditcard) {
            startActivity(new Intent(MainActivity4.this,
                    CreditCardListing.class));
        } else if (id == R.id.nav_reservation) {
            startActivity(new Intent(MainActivity4.this, Reservation.class));
        } else if (id == R.id.nav_referafriend) {
            startActivity(new Intent(MainActivity4.this, SharingToro.class));
        } else if (id == R.id.nav_contact_us) {
            Intent i = new Intent(Intent.ACTION_SEND);

            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL,
                    new String[]{URL.SUPPORT_MAIL});

            startActivity(i);

        } else if (id == R.id.nav_aboutus) {
            startActivity(new Intent(MainActivity4.this, AboutUs.class));
        } else if (id == R.id.nav_logout) {
            showDialog("Alert !", "Are you sure to log out ?");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (checkLocationPermission()) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
            btnMyLocation.performClick();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long UPDATE_INTERVAL = 30 * 1000;
    private long FASTEST_INTERVAL = 30 * 1000;

    public void registerForLocationUpdates() {
        FusedLocationProviderClient locationProviderClient = getFusedLocationProviderClient();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        Looper looper = Looper.myLooper();
        if (checkLocationPermission()) {
            locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, looper);
        }
    }


    private FusedLocationProviderClient getFusedLocationProviderClient() {
        if (fusedLocationProviderClient == null) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        }
        return fusedLocationProviderClient;
    }


    void unregisterForLocationUpdates() {
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    public LatLng sourceLatLng;
    public String sourceAddress;
    private LocationCallback locationCallback = new LocationCallback() {

        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location lastLocation = locationResult.getLastLocation();
            sourceLatLng = new LatLng(lastLocation.getLatitude(),
                    lastLocation.getLongitude());

            DriverViewFragment myFragment = (DriverViewFragment) getFragmentManager().findFragmentByTag("DriverViewFragment");
            if (myFragment != null && myFragment.isVisible()) {

            } else {
                //Get address from latlng
                /*new GetAddressTask(MainActivity4.this, sourceLatLng, new GetAddressFromLatLng() {
                    @Override
                    public void onGetAddressCompleted(String address) {
                        srEditText.setText(address);
                        sourceAddress = address;
                        Bundle bundle = new Bundle();
                        bundle.putString("source", address);
                        if (selectedBundle != null) selectedBundle.onBundleSelect(bundle);
                        Log.d(TAG, "Current location address: " + address);
                    }
                }).execute();*/

                //Set current location to mapview
                if (mMap != null) {
                    CameraPosition position = CameraPosition.builder()
                            .target(sourceLatLng)
                            .zoom(16)
                            //.tilt(30)
                            .build();
                    //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
                }
                fetchCars(0);
            }
            Log.d(TAG, "Current location " + lastLocation);
        }
    };

    public void fetchCars(int taxiType) {
        String url = URL.BASE_URL + URL.GET_DRIVER
                + sharedPreference.getUserId() + URL.LAT
                + sourceLatLng.latitude + URL.LNG
                + sourceLatLng.longitude + URL.TAXI_TYPE
                + taxiType + "&bookingId=0";

        new WebServiceAsynk(url, new ResponseListener() {
            @Override
            public void response(String response, String webservice) {
                try {
                    showMarkers(response, 0);
                    /*if (webservice.equals("call1")) {
                        //car1 = new JSONObject(response);
                        showMarkers(response ,1);
                        fetchCars(2);
                    }else if (webservice.equals("call2")) {
                        //car2 = new JSONObject(response);
                        showMarkers(response,2);
                        fetchCars(3);
                    }else if (webservice.equals("call3")) {
                        showMarkers(response,3);
                        //car3 = new JSONObject(response);
                        //showAllMarker();
                    }*/

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, MainActivity4.this, false, "call" + taxiType).execute();
    }


    JSONObject car1;
    JSONObject car2;
    JSONObject car3;
    HashMap<String, Marker> hashMap = new HashMap<>();
    HashMap<String, String> fetchedMap = new HashMap<>();
    public HashMap<Integer, Integer> ETA = new HashMap<>();

    private void showMarkers(String strresponse, int taxyType) {
        try {
            JSONObject mJsonObject = new JSONObject(strresponse);
            JSONArray mJsonArray = mJsonObject
                    .getJSONArray("getDriversById");
            switch (taxyType) {
                case 1:
                    hybridCount = mJsonArray.length();
                    break;
                case 2:
                    suvCount = mJsonArray.length();
                    break;
                case 3:
                    blackCount = mJsonArray.length();
                    break;
            }

            if (mJsonArray.length() > 0) {
                for (int i = 0; i < mJsonArray.length(); i++) {
                    JSONObject mJsonObject2 = mJsonArray
                            .getJSONObject(i);
                    ETA.put(mJsonObject2.getInt("taxiType"), mJsonObject2.getInt("ETA"));
                    if (hashMap.containsKey(mJsonObject2.getString("id"))) {
                        Marker updateMarker = hashMap.get(mJsonObject2.getString("id"));
                        String id = mJsonObject2.getString("id");
                        Matrix matrix = new Matrix();
                        matrix.postRotate(Float.parseFloat(mJsonObject2
                                .getString("bearingDegree")) - 90);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(carBitmap, 0, 0,
                                carBitmap.getWidth(),
                                carBitmap.getHeight(), matrix, true);
                        LatLng newLatLng = new LatLng(Double.parseDouble(mJsonObject2.getString("driverLat")),
                                Double.parseDouble(mJsonObject2.getString("driverLong")));
                        float bearingDegree = (float) MapUtil.bearing(updateMarker.getPosition().latitude, updateMarker.getPosition().longitude, newLatLng.latitude, newLatLng.longitude);
                        updateMarker.setRotation(bearingDegree);
                        updateMarker.setPosition(new LatLng(Double.parseDouble(mJsonObject2.getString("driverLat")),
                                Double.parseDouble(mJsonObject2.getString("driverLong"))));
                        Log.d(TAG, "update Marker" + mJsonObject2.getString("id"));
                    } else {

                        Matrix matrix = new Matrix();
                        Bitmap rotatedBitmap = Bitmap.createBitmap(carBitmap, 0, 0,
                                carBitmap.getWidth(),
                                carBitmap.getHeight(), matrix, true);

                        Marker usersMarker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(mJsonObject2.getString("driverLat")),
                                        Double.parseDouble(mJsonObject2.getString("driverLong"))))
                                .icon(BitmapDescriptorFactory.fromBitmap(rotatedBitmap)));

                        hashMap.put(mJsonObject2.getString("id"), usersMarker);
                        Log.d(TAG, "add Marker" + mJsonObject2.getString("id"));
                    }
                }

            } else {
                // hashMap.clear();
                Log.d(TAG, "clear Markers");
            }
            JSONObject mObject = mJsonObject
                    .getJSONObject("taxiAvailable");
           /* hybridCount = Integer.parseInt(mObject.getString("Hybrid"));
            suvCount = Integer.parseInt(mObject.getString("SUV"));
            blackCount = Integer.parseInt(mObject.getString("Black"));
            luxuryCount = Integer.parseInt(mObject.getString("luxury"));
            limoCount = Integer.parseInt(mObject.getString("limo"));*/
            //  eta = mJsonObject.getString("time");
            if (!mJsonObject.getString("BookingIDOnServer").equals("0") && !isBookingEPageOpened) {
               /* Intent mIntent = new Intent(MainActivity4.this,
                        LoadingRequest.class);
                mIntent.putExtra("bookingId",
                        mJsonObject.getString("BookingIDOnServer"));
                mIntent.putExtra("via", "auto");
                startActivity(mIntent);*/
                isBookingEPageOpened = true;
                RequestBooking requestBookingFragment = new RequestBooking();
                RequestBooking myFragment = (RequestBooking) getFragmentManager().findFragmentByTag("RequestBooking");
                if (myFragment != null && myFragment.isVisible()) {
                    // already visible
                    Log.d(TAG, "Already visible RequestBooking fragment");
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("bookingId", mJsonObject.getString("BookingIDOnServer"));
                    bundle.putString("via", "auto");
                    bundle.putString("userid", sharedPreference.getUserId());
                    requestBookingFragment.setArguments(bundle);
                    showFragment(requestBookingFragment);
                }
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            //}
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.cordinatorLayout), getResources().getString(R.string.gpsError), Snackbar.LENGTH_LONG)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });

                snackbar.show();
            }
        }
    }

    public void findPlace(View view, int status) {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setCountry("CA")/*.setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS).setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)*/.build();

            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, status);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

   /* // A place has been received; use requestCode to track the request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    destPlace = PlaceAutocomplete.getPlace(this, data);
                    txtDestPage1.setText(destPlace.getAddress());
                    dsEditText.setText(destPlace.getAddress());
                    break;
                case 1:
                    sourcePlace = PlaceAutocomplete.getPlace(this, data);
                    srEditText.setText(sourcePlace.getAddress());
                    break;
            }

            //showRoute();//TODO call After 2 second
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(this, data);
            Log.i(TAG, status.getStatusMessage());
        } else if (resultCode == RESULT_CANCELED) {
            Log.i(TAG, "RESULT_CANCELED");
        }
    }*/

    private void showGPSEnableDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(
                "Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    @SuppressWarnings("unused") final DialogInterface dialog,
                                    @SuppressWarnings("unused") final int id) {
                                startActivity(new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        dialog.cancel();

                        MainActivity4.this.finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean checkEnableGPS() {
        String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        Log.d(TAG, "Location Provider " + provider);
        if (!provider.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkEnableGPS() == false) {
            showGPSEnableDialog();
        }
        Log.d(TAG, "Location enable " + checkEnableGPS());
        registerForLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterForLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void showDrawer(View v) {
        drawer.openDrawer(Gravity.LEFT);
    }

    ProgressDialog mDialog;

    public void showRoute(final String destinationAddress, final String source) {
        String sourceID = null;
        if (sourceLatLng != null && source.equalsIgnoreCase("Current location")) {
            sourceID = sourceLatLng.latitude + "," + sourceLatLng.longitude;
        } else if (sourceLatLng == null && source.equalsIgnoreCase("Current location")) {
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.cordinatorLayout), getResources().getString(R.string.route_not_found), Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        } else {
            sourceID = URLEncoder.encode(source);
        }

        if (sourceID != null && destinationAddress != null) {
            mDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.setMessage("Loading...");
            mDialog.setCancelable(true);
            // mDialog.show();
            final String finalSourceID = sourceID;
            RouteTask routeTask = new RouteTask(MainActivity4.this, new RouteInterface() {
                @Override
                public void onRouteLoad(List<List<HashMap<String, String>>> result, JSONObject distance, JSONObject duration) {
                    // mDialog.dismiss();
                    try {
                        mDistance = distance.getString("text");
                        mTime = duration.getString("text");
                        sharedPreference.setDistance(mDistance);
                        sharedPreference.setTime(mTime);
                        sharedPreference.setDistanceInMeter(distance.getInt("value"));
                        sharedPreference.setTimeInSeconds(distance.getInt("value"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    sharedPreference.setDestAddress(destinationAddress);
                    sharedPreference.setSourceAddress(finalSourceID);

                    if (result != null && result.size() > 0) {
                        try {
                            drawRoute(result,false);
                            //AddressSearchFragment fragment = (AddressSearchFragment) getFragmentManager().findFragmentByTag("AddressSearchFragment");
                            //fragment.hideBottomView();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(findViewById(R.id.cordinatorLayout), getResources().getString(R.string.route_not_found), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }

                @Override
                public void onRouteLoadFailed() {
                    mDialog.dismiss();
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.cordinatorLayout), getResources().getString(R.string.route_not_found), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });

            routeTask.execute(sourceID, URLEncoder.encode(destinationAddress));
        }
    }

   /* public void showRoute(String s) {
        String sourceID = null;
        if (sourceLatLng != null) {
            sourceID = sourceLatLng.latitude + "," + sourceLatLng.longitude;
        } else if (sourcePlace != null) {
            sourceID = sourcePlace.getLatLng().latitude + "," + sourcePlace.getLatLng().longitude;
        }else{
            sourceID = sourceAddress;
        }

        String destinationID = null;
        if (destPlace != null) {
            destinationID = destPlace.getLatLng().latitude + "," + destPlace.getLatLng().longitude;
        }
        if (sourceID != null && destinationID != null) {
            RouteTask routeTask = new RouteTask(MainActivity4.this, new RouteInterface() {
                @Override
                public void onRouteLoad(List<List<HashMap<String, String>>> result) {
                    if (result != null) {
                        drawRoute(result);
                    }
                }

                @Override
                public void onRouteLoadFailed() {

                }
            });
            routeTask.execute(sourceID, destinationID);
        }
    }*/

    private Polyline routeLine;

    private void drawRoute(List<List<HashMap<String, String>>> result, boolean forDriver) {
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;
        if (routeLine != null && !forDriver) {
            routeLine.setVisible(false);
        }

        if (result != null)
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                if(!forDriver) {
                    endLat = points.get(path.size() - 1).latitude;
                    endLNG = points.get(path.size() - 1).longitude;
                    MarkerOptions startPointMarker = new MarkerOptions();
                    startPointMarker.position(points.get(0));
                    startPointMarker.flat(true);
                    startPointMarker.anchor(0.5f, 0.5f);
                    startPointMarker.icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.green_marker));

                    endPointMarker = new MarkerOptions();
                    endPointMarker.position(points.get(points.size() - 1));
                    endPointMarker.flat(true);
                    endPointMarker.icon(bitmapDescriptorFromVector(this, R.drawable.ic_destination_blue));
                    endMarker = mMap.addMarker(endPointMarker);
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                if(!forDriver) {
                    lineOptions.color(Color.BLACK);
                }else{
                    lineOptions.color(Color.parseColor("#93CFFC"));
                }
                // Drawing polyline in the Google Map for the i-th route
                if (lineOptions != null) {
                    routeLine = mMap.addPolyline(lineOptions);
                    if (points != null)
                        MapUtil.zoomRoute(mMap, points);
                    if(!forDriver) {
                        showFragment(new BookFragment());
                    }
                }
            }
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_destination_blue);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    public void showRideOptionDialog() {
        btnMyLocation.setVisibility(View.GONE);
        btnSUV = (Button) findViewById(R.id.btnSUV);
        btnBlack = (Button) findViewById(R.id.btnBLACK);
        btnHybrid = (Button) findViewById(R.id.btnHYBRID);
        btnLuxury = (Button) findViewById(R.id.btnLUXURY);
        btnArmour = (Button) findViewById(R.id.btnArmour);
        carOptionsDialog = (RelativeLayout) findViewById(R.id.car_options_dialog);
        carOptionsDialog.setVisibility(View.VISIBLE);
        btnRequest = (Button) findViewById(R.id.btnRequest);
        btnCard = (Button) findViewById(R.id.cardBtn);

        if (sharedPreference.getCreditcardno().equals("")) {
            btnCard.setText("ADD CARD");
            btnCard.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity4
                            .this, AddCreditCard.class));
                    //sharedPreference.setPaymentViaCreditCard(true);
                    //btnCard.setTextColor(getResources().getColor(R.color.title_color));
                    //btnCard.setBackgroundColor(getResources().getColor(R.color.colorEditTextFocusLost));
                }
            });
        } else {
            btnCard.setText("**** "
                    + sharedPreference.getCreditcardno().subSequence(
                    sharedPreference.getCreditcardno().length() - 4,
                    sharedPreference.getCreditcardno().length()));
            sharedPreference.setPaymentViaCreditCard(true);

        }
        showCarDetail(R.id.btnHYBRID, 0);
    }

    public void showCarDetail(View view) {
        showCarDetail(view.getId(), 0);
    }

    String CONFIRM = "CONFIRM ";

    public void showCarDetail(int id, int fare) {
        btnHybrid.setBackgroundResource(R.drawable.car_regular);
        btnSUV.setBackgroundResource(R.drawable.car_suv);
        btnBlack.setBackgroundResource(R.drawable.car_black_car);
        btnLuxury.setBackgroundResource(R.drawable.car_luxury);
        btnArmour.setBackgroundResource(R.drawable.car_armour);
        long price = fare;

        btnHybrid.setText(Utility.CAR_1 + "\n $" + price);
        btnSUV.setText(Utility.CAR_2 + "\n $" + price);
        btnBlack.setText(Utility.CAR_3 + "\n $" + price);
        btnLuxury.setText(Utility.CAR_4 + "\n $" + price);
        btnArmour.setText(Utility.CAR_5 + "\n $" + price);


        switch (id) {
            case R.id.btnHYBRID:
                //findFareQuote(1, startPointMarker.getPosition().latitude, startPointMarker.getPosition().latitude, startPointMarker.getPosition().longitude, startPointMarker.getPosition().longitude);
                if (hybridCount > 0) {
                    btnRequest.setText(CONFIRM + Utility.CAR_1);
                } else {
                    btnRequest.setText("Not Available");
                }
                btnHybrid.setBackgroundResource(R.drawable.car_regular_slctd);
                taxiType = 1;
                break;
            case R.id.btnSUV:
                if (suvCount > 0) {
                    btnRequest.setText(CONFIRM + Utility.CAR_2);
                } else {
                    btnRequest.setText("Not Available");
                }
                btnSUV.setBackgroundResource(R.drawable.car_suv_slctd);
                taxiType = 2;
                break;
            case R.id.btnBLACK:
                if (blackCount > 0) {
                    btnRequest.setText(CONFIRM + Utility.CAR_3);
                } else {
                    btnRequest.setText("Not Available");
                }
                btnBlack.setBackgroundResource(R.drawable.car_black_car_slctd);
                taxiType = 3;
                break;
            case R.id.btnLUXURY:
                if (luxuryCount > 0) {
                    btnRequest.setText(CONFIRM + Utility.CAR_4);
                } else {
                    btnRequest.setText("Not Available");
                }

                btnLuxury.setBackgroundResource(R.drawable.car_luxury_slctd);
                taxiType = 4;
                break;
            case R.id.btnArmour:
                btnRequest.setText(CONFIRM + Utility.CAR_5);
                if (limoCount > 0) {
                    btnRequest.setText(CONFIRM + Utility.CAR_5);
                } else {
                    btnRequest.setText("Not Available");
                }
                btnArmour.setBackgroundResource(R.drawable.car_armour_slctd);
                taxiType = 5;
                break;
        }

        if (btnRequest.getText().toString().equals("Not Available")) {
            btnRequest.setOnClickListener(null);
        } else {
            btnRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // bookCab();
                }
            });
        }

    }

    public void bookCab(int taxiType) {

        String bookingType = "";
        if (Utility.cashFlow) {
            if (sharedPreference.getPaymentViaCreditCard()) {
                bookingType = "1";
            } else {
                bookingType = "0";
            }
        } else {
            bookingType = "1";
            if (sharedPreference.getCreditcardno().equals("")) {
                final Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.drawer_layout), "Please Add Credit card", Snackbar.LENGTH_LONG)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                btnCard.performClick();
                            }
                        });

                //snackbar.show();
                //return;
            }
        }

        PreferenceManager.getDefaultSharedPreferences(MainActivity4.this).edit()
                .putInt("type", 1).commit();

        String xmlBookCab = "";

        String dropOffStatus = "1";
        {
            xmlBookCab = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><bookcab><userId><![CDATA["
                    + sharedPreference.getUserId()
                    + "]]></userId><taxiType><![CDATA["
                    + taxiType
                    + "]]></taxiType><latitude><![CDATA["
                    + sourceLatLng.latitude
                    + "]]></latitude><longitude><![CDATA["
                    + sourceLatLng.longitude
                    + "]]></longitude>"
                    + ""
                    + "<dropoffLat><![CDATA["
                    + endLat
                    + "]]></dropoffLat><dropoffLong><![CDATA["
                    + endLNG
                    + "]]></dropoffLong><dropoffStatus><![CDATA["
                    + dropOffStatus
                    + "]]></dropoffStatus><dropoffLocation><![CDATA["
                    + ""
                    + "]]></dropoffLocation>"
                    + "<dropoffAddress><![CDATA["
                    + ""
                    + "]]></dropoffAddress><promoCodeVal><![CDATA["
                    + ""
                    + "]]></promoCodeVal>"
                    + "<userPromoCodeId><![CDATA["
                    + ""
                    + "]]></userPromoCodeId><pickUpLocation><![CDATA["
                    + ""
                    + "]]></pickUpLocation><pickUpAddress><![CDATA["
                    + ""
                    + "]]></pickUpAddress>"
                    + "<bookingType><![CDATA["
                    + bookingType
                    + "]]></bookingType>" + "</bookcab>";

        }
        new SendXmlAsync(URL.BASE_URL + URL.BOOK_NOW, xmlBookCab,
                new com.tms.utility.XmlListener() {
                    @Override
                    public void onResponse(String respose) {
                        Log.d(TAG, URL.BASE_URL + URL.BOOK_NOW + "\n" + respose);
                        try {
                            JSONObject mJsonObject = new JSONObject(respose);
                            String message = mJsonObject.getString("message");
                            String status = mJsonObject.getString("bookcab");
                            if (status.equalsIgnoreCase("-1") || status.equalsIgnoreCase("-2") ||
                                    status.equalsIgnoreCase("-3") || status.equalsIgnoreCase("-4")) {
                                //showDialog("Alert !", message, 0);
                                final Snackbar snackbar = Snackbar
                                        .make(findViewById(R.id.drawer_layout), message, Snackbar.LENGTH_LONG)
                                        .setAction("OK", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                            }
                                        });

                                snackbar.show();

                            } else {
                               /* Intent mIntent = new Intent(MainActivity4.this,
                                        LoadingRequest.class);
                                mIntent.putExtra("bookingId",
                                        mJsonObject.getString("bookcab"));
                                mIntent.putExtra("via", "manual");
                                startActivity(mIntent);
                                finish();*/
                                RequestBooking requestBookingFragment = new RequestBooking();
                                Bundle bundle = new Bundle();
                                bundle.putString("bookingId",
                                        mJsonObject.getString("bookcab"));
                                bundle.putString("via", "manual");
                                bundle.putString("userid", sharedPreference.getUserId());
                                requestBookingFragment.setArguments(bundle);
                                showFragment(requestBookingFragment);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, MainActivity4.this, false).execute();

    }

    Dialog dialog;

    private void showDialog(String title, String message) {
        dialog = new Dialog(this,
                android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_warning);
        TextView txtTitle = (TextView) dialog
                .findViewById(R.id.dialog_title_text);
        TextView txtMessage = (TextView) dialog
                .findViewById(R.id.dialog_message_text);

        Button btnYes = (Button) dialog.findViewById(R.id.dialog_ok_btn);
        Button btnNo = (Button) dialog.findViewById(R.id.dialog_cancel_btn);

        btnYes.setVisibility(View.VISIBLE);
        btnNo.setVisibility(View.VISIBLE);

        Button btnOk = (Button) dialog.findViewById(R.id.btnDone);
        btnOk.setVisibility(View.GONE);
        RelativeLayout layout = (RelativeLayout) dialog
                .findViewById(R.id.layoutYesNo);
        layout.setVisibility(View.VISIBLE);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WebServiceAsynk(URL.BASE_URL + URL.LOG_OUT + sharedPreference.getUserId(), new ResponseListener() {
                    @Override
                    public void response(String strresponse, String webservice) {
                        try {
                            JSONObject jsonObject = new JSONObject(strresponse);
                            if (jsonObject.getString("userLogout").equals("1")) {
                                Toast.makeText(MainActivity4.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity4.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                sharedPreference.clearPrefRence();
                                dialog.dismiss();
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, MainActivity4.this, true, URL.LOG_OUT).execute();
                //dialog.dismiss();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        txtTitle.setText(title);
        txtMessage.setText(message);
        dialog.show();

    }

    public void findFareQuote(int type, XmlListener listner) {
        findFareQuote(type, sourceLatLng.latitude, sourceLatLng.longitude, endLat, endLNG, listner);
    }

    public void findFareQuote(int taxiType, double startLat, double startLNG,
                              double endLat, double endLNG, XmlListener listner) {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><fareQuote><userId><![CDATA["
                + sharedPreference.getUserId()
                + "]]></userId><taxiType><![CDATA["
                + taxiType
                + "]]></taxiType><latitude><![CDATA["
                + startLat
                + "]]></latitude><longitude><![CDATA["
                + startLNG
                + "]]></longitude><destinationLat><![CDATA["
                + endLat
                + "]]></destinationLat><destinationLong><![CDATA["
                + endLNG
                + "]]></destinationLong></fareQuote>";
        new SendXmlAsync(URL.BASE_URL + URL.FARE_QUOTE, xml, listner, this, false).execute();

    }

    public void bookingRequestCancelled(String message) {
        final Snackbar snackbar = Snackbar
                .make(findViewById(R.id.drawer_layout), "Booking Cancelled", Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showFragment(new WhereToFragment());
                    }
                });

        snackbar.show();

    }

    public void bookingRequestCancelled(String title, String message) {
        dialog = new Dialog(MainActivity4.this,
                android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_warning);
        TextView txtTitle = (TextView) dialog
                .findViewById(R.id.dialog_title_text);
        TextView txtMessage = (TextView) dialog
                .findViewById(R.id.dialog_message_text);

        txtTitle.setText(title);
        txtMessage.setText(message);

        Button btnYes = (Button) dialog.findViewById(R.id.dialog_ok_btn);
        Button btnNo = (Button) dialog.findViewById(R.id.dialog_cancel_btn);

        btnYes.setVisibility(View.GONE);
        btnNo.setVisibility(View.GONE);
        Button btnOk = (Button) dialog.findViewById(R.id.btnDone);
        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                backstackFragment();
            }
        });
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }
    public void setMapHeight(int bookingHeight){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
        params.height = (height-bookingHeight);
        mapFragment.getView().setLayoutParams(params);
    }
}


