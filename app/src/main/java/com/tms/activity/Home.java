package com.tms.activity;


import android.Manifest;
import android.R.style;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.view.ViewTreeObserver.OnTouchModeChangeListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
//import com.navdrawer.SimpleSideDrawer;
import com.squareup.picasso.Picasso;
import com.tms.DataParser;
import com.tms.Gps.GPS;
import com.tms.Gps.IGPSActivity;
import com.tms.MapBoxOnlineTileProvider;
import com.tms.R;
import com.tms.SharingToro;
import com.tms.model.Company;
import com.tms.model.LocationsName;
import com.tms.model.MyMarker;
import com.tms.newui.MapUtil;
import com.tms.utility.CheckInternetConnectivity;
import com.tms.utility.CircleTransform;
import com.tms.utility.DirectionsJSONParser;
import com.tms.utility.LocationClass;
import com.tms.utility.LocationClass.OnLocationFinder;
import com.tms.utility.MarkerMoving;
import com.tms.utility.ResponseListener;
import com.tms.utility.SendXmlAsync;
import com.tms.utility.ServiceResponce;
import com.tms.utility.ToroSharedPrefrnce;
import com.tms.utility.URL;
import com.tms.utility.Utility;
import com.tms.utility.UtilsSingleton;
import com.tms.utility.WebServiceAsynk;
import com.tms.utility.XmlListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class Home extends FragmentActivity implements
        OnTouchModeChangeListener, OnScrollChangedListener, ServiceResponce,
        XmlListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener, ResponseListener, IGPSActivity, OnLocationFinder {

    private TextView srEditText, dsEditText;
    private Button btnDestination1, edtDropOffAddress1;
    private RelativeLayout layoutDestination;

    private ImageView img;
    boolean isPermssionGranted;

    private String HYBRID = "HYBRID", SUV = "SUV", BLACK = "BLACK",
            LUXURY = "LUXURY", ARMOUR = "ARMOUR";

    Dialog dialog;

    private String whatIsSelected = HYBRID;

    BitmapFactory.Options options = new BitmapFactory.Options();
    Matrix matrix;
    private Bitmap regularBmp, suvBmp, blackBmp, luxuryBmp;

    Bitmap scaledBitmap;
    Bitmap rotatedBitmap;

    boolean setReset = false;

    public static Activity Home;

    boolean BOOKCAB = false;

    // private ProgressDialog pBarMap;
    boolean homeBool = false, layoutConfirmationVisibilityBool = false,
            layoutFareQuoteVisibilityBool = false;

    AlertDialog.Builder builder;

//    ProgressBar prgressBarTime;
    ImageView btnMyLocation;
    Button btnCash, btnCard, btnBookRide;

    boolean isDestroyed = false;

    boolean isONCreate = false;

    private LocationClass mLocationClass;

    public static final String ACTION = "android.location.PROVIDERS_CHANGED";
    boolean isDestinationChange = false;

    GetAddressTask getAddress = null;

    //ProgressBar pBar, pBar1, progressTaxiInfo, progressTaxiInfo2,
    //        progressTaxiInfo3;

    RelativeLayout netRel;

    boolean isLocationSet = false;
    boolean iSGpsPresent = false;
    Dialog dialogNew;

    private RelativeLayout btnPromoCodeConfirmation, btnFareQuote;
    private Button btnRequest;
    private RelativeLayout btnChangeCreditCard;

    private ImageView imgCrediCardType;
    private TextView txtcardNumber, textConfirmation;

    private ToroSharedPrefrnce sharedPreference;
    private LinearLayout layoutConfirmationpop;

    private Button btnDestinationShow, btnDestinationCancel;

    //

    private RelativeLayout layoutShowLocation, layoutConfirmationHeader,
            layoutHomeTop, mSearchLocation, layoutShowDestination;

    private ImageButton btnCancel;

    private TextView txtLocationName, txtLocationAddress, txtDestinationName,
            txtDestinationAddress;
    GoogleMap mMap;
    FragmentManager myFragmentManager;
    SupportMapFragment mySupportMapFragment;
    Button edtPickUpAddress, btnPickUp, btnDestination;
    RelativeLayout mPopUpLayout;
    boolean isVisible = false;
    public static Activity mActivity;
    //public static SimpleSideDrawer mNav;
    ImageView locationPin;
    RelativeLayout popup, tutorialView;
    TextView txtTime;

    Button btnSUV, btnHybrid, btnBlack, btnLuxury, btnArmour;

    SeekBar seekBar;

    RelativeLayout mLayout;

    TextView txtTitlePopUp, txtEta, txtbasefair, txtMaxPerson, txtbasefare1,
            txtBaseFare3, txtBaseFare2;

    Handler mHandler;
    HashMap<String, MyMarker> markersMap = new HashMap<String, MyMarker>();
    ArrayList<MyMarker> mDriverArrayList;
    ArrayList<MyMarker> newDriversList;

    boolean hasMarkersLoaded;

    String empty = "j";
    String locationName = " ", locationAddress = " ";

    double firstLat, firstLong;

    LatLng newPosition;

    int taxiType = 0, taxiTypePrev = 0;
    MarkerMoving markerMoving;
    RelativeLayout mFareQuate;
    TextView txtCarName, txtCarFare;
    Button btnEnterNewDestination;

    // TextView txtTitle;
    ImageView fareNA;
    RelativeLayout imgFareEA;
    /**
     * Start and Destination lat ,lng
     */
    double startLat, startLNG, endLat = 0.0d, endLNG = 0.0d;
    // private LocationClient mLocationClient;
    boolean isFirstTime = true;
    public static LatLng target = null;
    Geocoder geocoder;
    List<Address> addresses = null;

    GoogleApiClient googleApiClient;

    public static Activity activity;

    private GPS gps;

    private Typeface typeFace, typeFace1, typeFaceMedium, typeFaceItalic;

    private boolean APP_IN_BACKGROUND = false;
    final private String TAG = "VIREN";
    private ListView listSerchResult;
    private View bottomSheet;
    private View bottomsheetbarView;
    private BottomSheetBehavior<View> behavior;
    private ViewFlipper mViewFlipper;
    private float initialX;
    private String countOfHybrid, countOfBlack, countOfSUV;
    private RelativeLayout carOptionsDialog;
    private MarkerOptions endPointMarker;
    private MarkerOptions startPointMarker;
    private TileOverlay tileOverlay;
    private ImageView btnClose;
    private ImageView btnCabFilter;
    private LocationsName sourceAddress;
    private LocationsName destinationAddress;
    private TextWatcher textChangeListern;
    private TextWatcher textChangeListernDS;
    private Polyline routeLine;
    private LocationManager mLocationManager;
    private FusedLocationProviderClient fusedLocationProviderClient;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private Location myLocation;
    private int hybridCount, suvCount, blackCount, luxuryCount, limoCount;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE;
    private TextView txtDestPage1;
    private ImageView btnDirection;
    private Place destPlace,sourcePlace;

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

    private long UPDATE_INTERVAL = 120 * 1000;
    private long FASTEST_INTERVAL = 60* 1000;
    public void registerForLocationUpdates() {
        FusedLocationProviderClient locationProviderClient = getFusedLocationProviderClient();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        Looper looper = Looper.myLooper();
        checkLocationPermission();
        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, looper);
    }


    private FusedLocationProviderClient getFusedLocationProviderClient() {
        if (fusedLocationProviderClient == null) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Home.this);
        }
        return fusedLocationProviderClient;
    }


    void unregisterForLocationUpdates() {
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    private LocationCallback locationCallback = new LocationCallback() {

        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location lastLocation = locationResult.getLastLocation();
            //updatePosition(lastLocation);
            target = new LatLng(lastLocation.getLatitude(),
                    lastLocation.getLongitude());
            getAddress = new GetAddressTask(Home.this, target);
            getAddress.execute();
            if(mMap != null)
            {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        target, 16));

            }
            Log.d(TAG, "VIREN:" + lastLocation);

        }
    };

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Utility.cashFlow) {
            findViewById(R.id.rel_cash_card).setVisibility(View.VISIBLE);
            findViewById(R.id.rel_1).setVisibility(View.GONE);
        } else {
            findViewById(R.id.rel_cash_card).setVisibility(View.GONE);
            findViewById(R.id.rel_1).setVisibility(View.VISIBLE);

        }
        registerForLocationUpdates();
        //new ui

        txtDestPage1 = (TextView) findViewById(R.id.txt_dest);
        btnDirection = (ImageView) findViewById(R.id.directionButton);
        btnDirection.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //findViewById(R.id.dest).setVisibility(View.GONE);
                findViewById(R.id.address_bar).setVisibility(View.VISIBLE);
                findViewById(R.id.btnCloseBookingPage).setVisibility(View.VISIBLE);
                dsEditText.setText(txtDestPage1.getText());
                showRoute();
            }
        });

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        carOptionsDialog = (RelativeLayout) findViewById(R.id.car_options_dialog);
        txtDestPage1 = (TextView) findViewById(R.id.txt_dest);
        bottomSheet = findViewById(R.id.design_bottom_sheet);
        srEditText = (TextView) findViewById(R.id.source_editText);
        dsEditText = (TextView) findViewById(R.id.destination_editText);

        txtDestPage1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                findPlace(view, 0);
            }
        });
        dsEditText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                findPlace(view, 1);
            }
        });

        dsEditText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                findPlace(view, 2);
            }
        });

        listSerchResult = (ListView) findViewById(R.id.listSerchResult);
        bottomsheetbarView = findViewById(R.id.bottomsheetbar_view);
        listSerchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (StartAddressType == 1) {
                    srEditText.setText(mAddressList.get(i).getName());
                    sourceAddress = mAddressList.get(i);
                } else {
                    unsetTextListners();
                    dsEditText.setText(mAddressList.get(i).getName());
                    destinationAddress = mAddressList.get(i);
                }

                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                String sourceAddress = srEditText.getText().toString();
                String destinationAddress = dsEditText.getText().toString();
                if (!TextUtils.isEmpty(sourceAddress) && !TextUtils.isEmpty(destinationAddress)) {

                    RouteTask routeTask = new RouteTask(Home.this, StartAddressType);
                    routeTask.execute("");
                    bottomsheetbarView.setVisibility(View.GONE);
                    findViewById(R.id.address_bar).setVisibility(
                            View.GONE);
                    findViewById(R.id.bottomsheetbar_view).setVisibility(
                            View.GONE);
                }
            }
        });
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_SETTLING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_HIDDEN");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("BottomSheetCallback", "slideOffset: " + slideOffset);
            }
        });
        getTaxiCompanyList();
       /* srEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    bottomsheetbarView.setVisibility(View.VISIBLE);
                }
            }
        });

        dsEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    bottomsheetbarView.setVisibility(View.VISIBLE);
                }
            }
        });
        initTextListeners();*/
        sharedPreference = new ToroSharedPrefrnce(Home.this);

        //end new ui
        btnClose = (ImageView) findViewById(R.id.btnCloseBookingPage);
        btnCard = (Button) findViewById(R.id.cardBtn);
        btnCash = (Button) findViewById(R.id.cashBtn);
        btnBookRide = (Button) findViewById(R.id.bookRideBtn);
        findViewById(R.id.wheretobutton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.address_bar).setVisibility(View.VISIBLE);
                bottomsheetbarView.setVisibility(View.VISIBLE);
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                findViewById(R.id.where_to).setVisibility(View.GONE);
                findViewById(R.id.btnDrawer).setVisibility(View.GONE);
                btnClose.setVisibility(View.VISIBLE);
                btnMyLocation.setVisibility(View.GONE);
                btnCabFilter.setVisibility(View.GONE);
                setTextListners();


            }
        });
        findViewById(R.id.btnCloseBookingPage).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.address_bar).setVisibility(
                        View.GONE);
                findViewById(R.id.btnCloseBookingPage).setVisibility(
                        View.GONE);
                /*findViewById(R.id.bottomsheetbar_view).setVisibility(
                        View.GONE);
                findViewById(R.id.where_to).setVisibility(View.VISIBLE);
                findViewById(R.id.btnDrawer).setVisibility(View.VISIBLE);
                btnClose.setVisibility(View.GONE);
             */   carOptionsDialog.setVisibility(View.GONE);
                btnMyLocation.setVisibility(View.VISIBLE);
               // btnCabFilter.setVisibility(View.VISIBLE);
                hideKeybord();
                unsetTextListners();
                if (routeLine != null) {
                    routeLine.remove();
                }
                mMap.clear();
            }
        });
        btnBookRide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, Reservation.class));
            }
        });

        typeFace = Typeface
                .createFromAsset(getAssets(), Utility.TYPE_FACE_HOME);
        typeFace1 = Typeface.createFromAsset(getAssets(), Utility.TYPE_FACE);
        typeFaceItalic = Typeface.createFromAsset(getAssets(),
                Utility.TYPE_FACE_FORGOT);
        typeFaceMedium = Typeface.createFromAsset(getAssets(),
                Utility.TYPE_FACE_HELVATICA_NEUE_MEDIUM);
        ((TextView) findViewById(R.id.txt_bottom_bar))
                .setTypeface(typeFaceItalic);

        ((RelativeLayout) findViewById(R.id.main_head)).bringToFront();
        mLocationClass = new LocationClass(this);

        options.inSampleSize = 1;
        regularBmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.map_pin_hybrid1, options);

        suvBmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.map_pin_suv1, options);
        blackBmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.map_pin_black_car1, options);

        luxuryBmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.map_pin_black_car1, options);


        if (DriverDetails.driverDetils != null) {
            DriverDetails.driverDetils.finish();
        }

        Home = this;
        isONCreate = true;

        // pBarMap = new ProgressDialog(Home.this, ProgressDialog.THEME_HOLO_LIGHT);
        //pBarMap.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //pBarMap.setMessage("Loading...");
        //pBarMap.show();

        /*if (android.os.Build.VERSION.SDK_INT >= 23) {
            if ((ContextCompat.checkSelfPermission(Home.this,
                    "android.permission.ACCESS_COARSE_LOCATION") != PackageManager.PERMISSION_GRANTED)
                    && (ContextCompat.checkSelfPermission(Home.this,
                    "android.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED)) {
                isPermssionGranted = false;
               // pBarMap.dismiss();
                AskRuntimePermission(new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 101, Home.this);
            } else {
                isPermssionGranted = true;
                gps = new GPS(Home.this);
            }
        } else {
            isPermssionGranted = true;
            gps = new GPS(Home.this);
        }*/

       /* progressTaxiInfo = (ProgressBar) findViewById(R.id.progressBarTaxiInfo1);
        progressTaxiInfo2 = (ProgressBar) findViewById(R.id.progressBarTaxiInfo2);

        progressTaxiInfo3 = (ProgressBar) findViewById(R.id.progressBarTaxiInfo3);
*/
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        activity = this;
  /*      pBar = (ProgressBar) findViewById(R.id.ProgressBar01);
        pBar1 = (ProgressBar) findViewById(R.id.ProgressBar02);
  */      netRel = (RelativeLayout) findViewById(R.id.net_rel);
        netRel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

            }
        });

//        mLocationClient = new LocationClient(this, this, this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        markerMoving = new MarkerMoving();
        newPosition = new LatLng(UtilsSingleton.getInstance().getCurrent_lat(),
                UtilsSingleton.getInstance().getCurrent_longi());
        mActivity = Home.this;

        initializeViews();
        mDriverArrayList = new ArrayList<MyMarker>();
        newDriversList = new ArrayList<MyMarker>();

        // mSimpleGestureFilter = new SimpleGestureFilter(this, this);

        layoutHomeTop = (RelativeLayout) findViewById(R.id.layoutHomeTop);

        mSearchLocation = (RelativeLayout) findViewById(R.id.layoutEditLocation);
        findViewById(R.id.edtPickUpAddress).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub


                startActivityForResult(new Intent(Home.this,
                        PickUpLocation.class).putExtra("via",
                        "PICK UP LOCATION"), 0);

            }
        });


      /*  mNav = new SimpleSideDrawer(this);

        mViewFlipper = (ViewFlipper) this.findViewById(R.id.view_flipper);

        mViewFlipper.setVisibility(View.VISIBLE);
        mNav.setLeftBehindContentView(R.layout.activity_behind_left_simple);
        mNav.setDrawingCacheEnabled(true);*/
        //((TextView) findViewById(R.id.txtTag)).setTypeface(typeFace1);
        TextView name = (TextView) findViewById(R.id.txtTag);
        TextView phone = (TextView) findViewById(R.id.textView2);
        TextView email = (TextView) findViewById(R.id.textView7);
        if (sharedPreference.getEmail() != null)
            email.setText(sharedPreference.getEmail());
        name.setText(sharedPreference.getFirstName() + sharedPreference.getLastname());
        phone.setText(sharedPreference.getPhone());
        ImageView profileView = (ImageView) findViewById(R.id.profile_image);
        if (!sharedPreference.getImageUrl().equals("")) {
            Picasso.with(this).load(sharedPreference.getImageUrl())
                    .resize(80, 80).transform(new CircleTransform())
                    .into(profileView);

        }
        findViewById(R.id.btnDrawer).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                /*mNav.toggleLeftDrawer();
                mNav.clearFocus();*/

            }
        });


        findViewById(R.id.btnMyProfile).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Home.this, MyProfile.class));
                    }
                });

        // ((Button) findViewById(R.id.btnMyProfile)).setTypeface(typeFace);

        findViewById(R.id.btnCreditcard).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Home.this,
                                CreditCardListing.class));
                    }
                });
        // ((Button) findViewById(R.id.btnCreditcard)).setTypeface(typeFace);

        if (Utility.isPaymentGatewayOn) {

            findViewById(R.id.btnCreditcard)
                    .setVisibility(View.VISIBLE);
            // ((View) findViewById(R.id.dividerCreditcard))
            //       .setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.btnCreditcard)
                    .setVisibility(View.GONE);
            //((View) findViewById(R.id.dividerCreditcard))
            //.setVisibility(View.GONE);
        }
        findViewById(R.id.btnPromoCode).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Home.this,
                                PromoCodeListing.class).putExtra("what", 1));
                    }
                });
        //((Button) findViewById(R.id.btnPromoCode)).setTypeface(typeFace);
        findViewById(R.id.btnReferFrnd).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Home.this, SharingToro.class));

                    }
                });
        // ((Button) findViewById(R.id.btnReferFrnd)).setTypeface(typeFace);
        findViewById(R.id.btnAboutUs).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(Home.this, AboutUs.class));
            }
        });
        //((Button) findViewById(R.id.btnAboutUs)).setTypeface(typeFace);

        findViewById(R.id.btnContactUs).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent i = new Intent(Intent.ACTION_SEND);

                        i.setType("message/rfc822");
                        /*
                         * i.setClassName("com.google.android.gm",
						 * "com.google.android.gm.ComposeActivityGmail");
						 */
                        i.putExtra(Intent.EXTRA_EMAIL,
                                new String[]{URL.SUPPORT_MAIL});

                        startActivity(i);

                    }
                });
        //((Button) findViewById(R.id.btnContactUs)).setTypeface(typeFace);
        findViewById(R.id.btnReservation).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub

                        startActivity(new Intent(Home.this, Reservation.class));

                    }
                });
        //((Button) findViewById(R.id.btnReservation)).setTypeface(typeFace);

        findViewById(R.id.btnBack).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                /*mNav.close();
                mNav.clearFocus();
*/
				/* edtPickUpAddress.bringToFront(); */

                // onPause();
            }
        });


        if (!Utility.isReservationAvailable) {
            findViewById(R.id.btnReservation).setVisibility(View.GONE);

            //findViewById(R.id.reservationDivider).setVisibility(View.INVISIBLE);
        }
        //((Button) findViewById(R.id.btnBack)).setTypeface(typeFace);
        // }
        mHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == 1) {
                    if (CheckInternetConnectivity
                            .checkinternetconnection(mActivity)) {
                        if (mMap.getCameraPosition().zoom > 12.8f) {

                        }
                    }
                } else if (msg.what == 2) {

                    Home.this.finish();

                }
            }

            ;
        };

        // bottomBarControl();

        destinationWork();

        mLocationClass.onStart(this);

        getTaxiCompanyList();
        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewFlipper.setAutoStart(true);
                mViewFlipper.setFlipInterval(1000);
                mViewFlipper.startFlipping();
                Snackbar.make(view, "Automatic view flipping has started", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewFlipper.stopFlipping();

                Snackbar.make(view, "Automatic view flipping has stopped", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void buildAlertMessageNoGps() {
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

                        Home.this.finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = touchevent.getX();
                break;
            case MotionEvent.ACTION_UP:
                float finalX = touchevent.getX();
                if (initialX > finalX) {
                    if (mViewFlipper.getDisplayedChild() == 1)
                        break;

                    mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_left));
                    mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.out_from_left));

                    mViewFlipper.showNext();
                } else {
                    if (mViewFlipper.getDisplayedChild() == 0)
                        break;

                    mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_right));
                    mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.out_from_right));

                    mViewFlipper.showPrevious();
                }
                break;
        }
        return true;
    }

    /*private void TraceMe() {
        String srcParam = srEditText.getText().toString().replace("null", "");
        String destParam = dsEditText.getText().toString().replace("null", "");
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + URLEncoder.encode(srcParam) + "&destination=" + URLEncoder.encode(destParam) + "&sensor=false&units=metric&mode=driving";
        new WebServiceAsynk(url, Home.this,
                Home.this, false, "route").execute();
    }*/

    private void showRoute() {
        String sourceAddress = srEditText!=null?srEditText.getText().toString():null;
        if (sourceAddress!= null && destPlace != null) {
            RouteTask routeTask = new RouteTask(Home.this, StartAddressType);
            routeTask.execute("");
        }
    }

    private void unsetTextListners() {
        srEditText.removeTextChangedListener(textChangeListern);
        dsEditText.removeTextChangedListener(textChangeListernDS);
    }

    private void setTextListners() {
        srEditText.addTextChangedListener(textChangeListern);
        dsEditText.addTextChangedListener(textChangeListernDS);
    }

    private void initTextListeners() {

        textChangeListern = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if ((s.equals("")) && (s.length() > 3)) {
                    srEditText.getHandler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            if (placesTask != null) {
                                placesTask.cancel(true);

                            }

                        }
                    }, 200);
                } else {
                    StartAddressType = 1;
                    placesTask = new PlacesTask(Home.this,
                            StartAddressType);
                    placesTask.execute(s.toString());

                }

            }
        };


        textChangeListernDS = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if ((s.equals("")) && (s.length() > 3)) {
                    dsEditText.getHandler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            if (placesTask != null) {
                                placesTask.cancel(true);

                            }

                        }
                    }, 200);
                } else {
                    StartAddressType = 2;
                    placesTask = new PlacesTask(Home.this,
                            StartAddressType);
                    placesTask.execute(s.toString());

                }

            }
        };

    }

    PlacesTask placesTask = null;
    int StartAddressType = 1;

    private void getTaxiCompanyList() {
        new SendXmlAsync(URL.COMPANY_LIST, "", Home.this,
                Home.this, true).execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isPermssionGranted = true;
                gps = new GPS(Home.this);
            } else {
                isPermssionGranted = false;
                showDialog(getResources().getString(R.string.gpsPermission), getResources().getString(R.string.gpsError));
            }
        }
    }

    @SuppressLint("NewApi")
    public void initializeViews() {

        taxiType = PreferenceManager.getDefaultSharedPreferences(Home.this)
                .getInt("type", 1);
        taxiTypePrev = taxiType;
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Home.this);

        // Showing status
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are
            // not available

            int requestCode = 10;
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(Home.this, status, requestCode);
            dialog.show();

        } else {
            myFragmentManager = getSupportFragmentManager();
            // Getting reference to the SupportMapFragment of activity_main.xml


            SupportMapFragment mapFragment = (SupportMapFragment) myFragmentManager
                    .findFragmentById(R.id.mapHome);
            mapFragment.getMapAsync(this);


            /*FragmentManager fm = getSupportFragmentManager();
            BlankFragment autoFragment = (BlankFragment) fm.findFragmentByTag("autocomplete");
            if (autoFragment == null) {
                autoFragment = new BlankFragment();
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.autocomplete_holder, autoFragment, "autocomplete");
                ft.commit();
                fm.executePendingTransactions();
            }*/
            mLayout = (RelativeLayout) findViewById(R.id.layoutHomeBottom);
            edtPickUpAddress = (Button) findViewById(R.id.edtPickUpAddress);
            edtPickUpAddress.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                }
            });


            // Enabling MyLocation Layer of Google Map
            btnCabFilter = (ImageView) findViewById(R.id.poweredByGoogle);
            btnCabFilter.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    showSingleChoiceCabDialog();
                }
            });

            btnMyLocation = (ImageView) findViewById(R.id.btnMyLocation);
            btnMyLocation.setOnClickListener(new OnClickListener() {

                @SuppressWarnings("MissingPermission")
                @Override
                public void onClick(View v) {


                    fusedLocationProviderClient.getLastLocation()
                            .addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // GPS location can be null if GPS is switched off
                                    if (location != null) {
                                        LatLng target = new LatLng(location.getLatitude(),
                                                location.getLongitude());

                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                                target, 16));
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar snackbar = Snackbar
                                            .make(findViewById(R.id.main_rl), "Location not available.", Snackbar.LENGTH_LONG);
                                    snackbar.show();

                                    Log.d("MapDemoActivity", "Error trying to get last GPS location");
                                    e.printStackTrace();
                                }
                            });
                }
            });

            ImageView imgLocation = (ImageView) findViewById(R.id.imgLocation);

            imgLocation.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                }
            });
            popup = (RelativeLayout) findViewById(R.id.imgPopUp);
            popup.setVisibility(View.GONE);
            tutorialView = (RelativeLayout) findViewById(R.id.tutorialView);

            tutorialView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    tutorialView.setVisibility(View.GONE);
                    sharedPreference.setTutorialShown(true);

                }
            });
            txtTime = (TextView) findViewById(R.id.txtTime);
            //
            // LayoutParams params=(LayoutParams) popup.getLayoutParams();
            //
            // popup.setLayoutParams(params);
            popup.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    if (CheckInternetConnectivity
                            .checkinternetconnection(Home.this)) {
                        if (edtPickUpAddress.getText().toString()
                                .equalsIgnoreCase("Pick up address")) {

                            displayAlert();

                        } else {

                            popup.setVisibility(View.GONE);

                            findViewById(R.id.layoutLocations).setVisibility(
                                    View.GONE);

                            confirmationPopView(locationName, locationAddress);

                            mLayout.setVisibility(View.GONE);
                            startLat = newPosition.latitude;
                            startLNG = newPosition.longitude;

                            if (!sharedPreference.isTutorialShown()) {
                                setTutorialViewArrow();
                            }
                        }

                        // }

                    } else {
                        showDialog("Alert !", "No Internet Connection.", 0);
                    }

                }

            });
            btnPickUp = (Button) findViewById(R.id.btnPicUp);
            btnPickUp.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    startActivityForResult(new Intent(Home.this,
                            PickUpLocation.class).putExtra("via",
                            "PICK UP LOCATION"), 0);
                }
            });
            btnDestination = (Button) findViewById(R.id.btnDestination);
            btnDestination.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    mPopUpLayout.setVisibility(View.GONE);
                    isVisible = false;

                    if (whatIsSelected.equals(HYBRID)) {
                        btnHybrid
                                .setBackgroundResource(R.drawable.car_regular_slctd);

                    }

                    if (whatIsSelected.equals(SUV)) {

                        btnSUV.setBackgroundResource(R.drawable.car_suv_slctd);
                    }

                    if (whatIsSelected.equals(BLACK)) {

                        btnBlack.setBackgroundResource(R.drawable.car_black_car_slctd);
                    }

                    if (whatIsSelected.equals(LUXURY)) {

                        btnLuxury
                                .setBackgroundResource(R.drawable.car_luxury_slctd);
                    }

                    if (whatIsSelected.equals(ARMOUR)) {
                        btnArmour
                                .setBackgroundResource(R.drawable.car_armour_slctd);

                    }

                    popup.setVisibility(View.GONE);

                    confirmationPopView(locationName, locationAddress);

                    isDestinationChange = true;

                    Intent i = new Intent(Home.this, PickUpLocation.class);
                    i.putExtra("via", "ADD DESTINATION");
                    startActivityForResult(i, 1);

                }
            });

            findViewById(R.id.edtDestinationAddress).setOnClickListener(
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub

                            mPopUpLayout.setVisibility(View.GONE);
                            isVisible = false;

                            if (whatIsSelected.equals(HYBRID)) {
                                btnHybrid
                                        .setBackgroundResource(R.drawable.car_regular_slctd);

                            }

                            if (whatIsSelected.equals(SUV)) {

                                btnSUV.setBackgroundResource(R.drawable.car_suv_slctd);
                            }

                            popup.setVisibility(View.GONE);
                            // locationPin.setVisibility(View.GONE);

                            confirmationPopView(locationName, locationAddress);

                            isDestinationChange = true;

                            Intent i = new Intent(Home.this,
                                    PickUpLocation.class);
                            i.putExtra("via", "ADD DESTINATION");
                            startActivityForResult(i, 1);

                        }
                    });

            mPopUpLayout = (RelativeLayout) findViewById(R.id.layoutPopUp);
            mPopUpLayout.setVisibility(View.GONE);
            mPopUpLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    mPopUpLayout.setVisibility(View.GONE);
                    isFirstTime = true;
                    // mMap.clear();
                    markerMoving.destroy();
                    gettingDataForFirstTime();
                    hidingPopUpOnDoubleTap();

                }
            });

            RelativeLayout relativeChild = (RelativeLayout) findViewById(R.id.layoutPopUpChild);
            relativeChild.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                }
            });
            txtTitlePopUp = (TextView) findViewById(R.id.txtTitlePopUp);
            txtEta = (TextView) findViewById(R.id.txtETA);
            txtMaxPerson = (TextView) findViewById(R.id.txtMaxPerson);
            txtbasefair = (TextView) findViewById(R.id.txtBasefare);
            btnSUV = (Button) findViewById(R.id.btnSUV);
            btnBlack = (Button) findViewById(R.id.btnBLACK);
            btnHybrid = (Button) findViewById(R.id.btnHYBRID);
            btnLuxury = (Button) findViewById(R.id.btnLUXURY);
            btnArmour = (Button) findViewById(R.id.btnArmour);
            btnHybrid.setText(Utility.CAR_1);
            btnBlack.setText(Utility.CAR_3);
            btnSUV.setText(Utility.CAR_2);
            btnLuxury.setText(Utility.CAR_4);
            btnArmour.setText(Utility.CAR_5);

            //prgressBarTime = (ProgressBar) findViewById(R.id.progressBarTime);
            txtbasefare1 = (TextView) findViewById(R.id.txtBaseFare1);
            txtbasefare1.setTypeface(typeFaceMedium);
            txtBaseFare3 = (TextView) findViewById(R.id.txtBaseFare3);
            txtBaseFare3.setTypeface(typeFaceMedium);
            txtBaseFare2 = (TextView) findViewById(R.id.txtBaseFare2);
            txtBaseFare2.setTypeface(typeFaceMedium);
            fareNA = (ImageView) findViewById(R.id.imgFareNA);
            imgFareEA = (RelativeLayout) findViewById(R.id.layoutfareMid2);
            imgFareEA.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                }
            });

            // edtPickUpAddress = (Button) findViewById(R.id.edtPickUpAddress);
            locationPin = (ImageView) findViewById(R.id.imgLocation);

            LayoutParams layoutParams = new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            layoutParams.setMargins(0, 0, 0, 150);
            locationPin.setLayoutParams(layoutParams);

        }

        layoutShowDestination = (RelativeLayout) findViewById(R.id.layoutShowDestination);
        txtDestinationName = (TextView) findViewById(R.id.txtDestinationName);
        layoutConfirmationpop = (LinearLayout) findViewById(R.id.confirmation_pop_up);
        textConfirmation = (TextView) findViewById(R.id.textConfirmation);
        textConfirmation.setTypeface(typeFace);

        layoutShowLocation = (RelativeLayout) findViewById(R.id.layoutShowLocation);
        layoutConfirmationHeader = (RelativeLayout) findViewById(R.id.layoutConfirmationHeader);
        btnDestinationShow = (Button) findViewById(R.id.btnDestination_show);
        txtLocationName = (TextView) findViewById(R.id.txtLocationName);
        txtLocationAddress = (TextView) findViewById(R.id.txtLocationAddress);
        btnCancel = (ImageButton) findViewById(R.id.btnCancel);
        btnPromoCodeConfirmation = (RelativeLayout) findViewById(R.id.btnPromoCodeConfirmation);
        btnFareQuote = (RelativeLayout) findViewById(R.id.btnFareQuote);
        btnRequest = (Button) findViewById(R.id.btnRequest);
        btnRequest.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final LocationManager manager = (LocationManager) Home.this
                        .getSystemService(Context.LOCATION_SERVICE);
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    displayAlert();
                } else {
                    bookCab();
                   /* if (BOOKCAB) {

                        bookCab();
                        BOOKCAB = false;

                    } else {
                        showDialogg("PickUp Address Confirmation ",
                                "Please confirm if the pickup address '"
                                        + edtPickUpAddress.getText().toString()
                                        + "'  is Confirmed.");
                    }*/
                    /* bookCab(); */

                }
            }
        });
        btnChangeCreditCard = (RelativeLayout) findViewById(R.id.rel_1);
        imgCrediCardType = (ImageView) findViewById(R.id.imgCreditCardType);
        txtcardNumber = (TextView) findViewById(R.id.txtCreditCardNumber);
        btnDestinationCancel = (Button) findViewById(R.id.btnDestination_cancel);
        btnPromoCodeConfirmation = (RelativeLayout) findViewById(R.id.btnPromoCodeConfirmation);
        btnFareQuote = (RelativeLayout) findViewById(R.id.btnFareQuote);

        mFareQuate = (RelativeLayout) findViewById(R.id.layoutFare);
        mFareQuate.setVisibility(View.GONE);
        layoutFareQuoteVisibilityBool = false;
        txtCarFare = (TextView) findViewById(R.id.txtCarFare);
        txtCarName = (TextView) findViewById(R.id.txtCarName);
        btnEnterNewDestination = (Button) findViewById(R.id.btnNewDestination);
        btnEnterNewDestination.setTypeface(typeFace);

        mFareQuate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                hidingFareQuoteScreen();

            }
        });

        btnEnterNewDestination.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                isDestinationChange = true;

                Intent i = new Intent(Home.this, PickUpLocation.class);
                i.putExtra("via", "ADD DESTINATION");
                startActivityForResult(i, 1);
            }
        });

    }

    int selectedCompanyIndex = 0;

    public void showSingleChoiceCabDialog() {
        new AlertDialog.Builder(this)
                .setSingleChoiceItems(comapanyNames, selectedCompanyIndex, null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        selectedCompanyIndex = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        for (int i = 0; i < mDriverArrayList.size(); i++) {
                            //Hide unselected company
                            Marker marker = mDriverArrayList.get(i).getMarker();
                            if (selectedCompanyIndex == 0) {
                                marker.setVisible(true);
                                Log.d(TAG, " Alert show all " + selectedCompanyIndex);
                            } else if (companies.get(selectedCompanyIndex - 1).id.equalsIgnoreCase(mDriverArrayList.get(i).getCompanyId())) {
                                marker.setVisible(true);
                                Log.d(TAG, " Alert show" + selectedCompanyIndex + "-" + mDriverArrayList.get(i).getCompanyId());
                            } else {
                                marker.setVisible(false);
                                Log.d(TAG, "Alert hide" + selectedCompanyIndex + "-" + mDriverArrayList.get(i).getCompanyId());
                            }


                        }
                        // Do something useful withe the position of the selected radio button
                    }
                })
                .show();
    }

    ArrayList<Integer> seletedItems;
    String[] comapanyNames;
    boolean selectedIndex[];

    private boolean isSelectedComapny(String companyID) {
        boolean isAvailable = false;
        if (selectedIndex[0]) {
            Log.d(TAG, "All selected");
            return true;
        }
        for (int i = 1; i < selectedIndex.length; i++) {
            Log.d(TAG, companies.get(i).id + " ==  " + companyID);
            if (companies.get(i).id.equalsIgnoreCase(companyID)) {
                isAvailable = true;
                break;
            }
        }
        return isAvailable;
    }

    @Override
    protected void onStop() {

        mLocationClass.onStop();
        if (isPermssionGranted) {
            if (gps != null) {
                gps.stopGPS();
            }
        }
        markerMoving.destroy();
        super.onStop();
    }

    private boolean checkEnableGPS() {
        String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.equals("")) {
            //GPS Enabled
            /*Toast.makeText(Home.this, "GPS Enabled: " + provider,
                    Toast.LENGTH_LONG).show();*/
            return true;
        } else {
            /*Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            startActivity(intent);*/
            return false;
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (!checkEnableGPS()) {
            buildAlertMessageNoGps();
        } else {


            if (newPosition != null) {

                new WebServiceAsynk(URL.BASE_URL + URL.GET_DRIVER
                        + sharedPreference.getUserId() + URL.LAT
                        + newPosition.latitude + URL.LNG + newPosition.longitude
                        + URL.TAXI_TYPE + taxiType + "&bookingId=0", Home.this,
                        Home.this, false, "first").execute();
            }

            APP_IN_BACKGROUND = false;

            if (sharedPreference.getCreditcardno().equals("")) {
                sharedPreference.setPaymentViaCreditCard(false);
            }

            if (sharedPreference.getPaymentViaCreditCard()) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    btnCard.setBackgroundDrawable(ContextCompat.getDrawable(Home.this, R.drawable.one_side_right_corner_button));
                } else {
                    btnCard.setBackground(ContextCompat.getDrawable(Home.this, R.drawable.one_side_right_corner_button));
                }
                btnCard.setBackgroundColor(getResources().getColor(R.color.colorEditTextFocusLost));
                btnCash.setBackgroundColor(getResources().getColor(R.color.transparent));

            } else {

                btnCash.setBackgroundColor(getResources().getColor(R.color.colorEditTextFocusLost));
                btnCard.setBackgroundColor(getResources().getColor(
                        android.R.color.transparent));
            }

            if (sharedPreference.getCreditcardno().equals("")) {
                btnCard.setText("ADD CARD");
                btnCard.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Home.this, AddCreditCard.class));
                        btnCard.setTextColor(getResources().getColor(R.color.title_color));
                        btnCard.setBackgroundColor(getResources().getColor(R.color.colorEditTextFocusLost));
                        btnCash.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                });
            } else {
                btnCard.setText("**** "
                        + sharedPreference.getCreditcardno().subSequence(
                        sharedPreference.getCreditcardno().length() - 4,
                        sharedPreference.getCreditcardno().length()));
                btnCard.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        btnCard.setBackgroundColor(getResources().getColor(R.color.colorEditTextFocusLost));
                        btnCash.setBackgroundColor(getResources().getColor(R.color.transparent));
                        sharedPreference.setPaymentViaCreditCard(true);
                    }
                });

            }

            btnCash.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    btnCash.setBackgroundColor(getResources().getColor(R.color.colorEditTextFocusLost));
                    btnCard.setBackgroundColor(getResources().getColor(
                            android.R.color.transparent));
                    sharedPreference.setPaymentViaCreditCard(false);

                }
            });

            setReset = false;

            if (isPermssionGranted && gps != null && !gps.isRunning()) {

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        gps.resumeGPS();

                    }
                }, 2000);

            }

            if ((homeBool == true && isVisible == false
                    && layoutConfirmationVisibilityBool == false && layoutFareQuoteVisibilityBool == false)) {
                mLocationClass.onStart(this);

                new WebServiceAsynk(URL.BASE_URL + URL.GET_DRIVER
                        + sharedPreference.getUserId() + URL.LAT
                        + newPosition.latitude + URL.LNG + newPosition.longitude
                        + URL.TAXI_TYPE + taxiType + "&bookingId=0", Home.this,
                        Home.this, false, "first").execute();
            }


            if (isONCreate) {

                mLocationClass.onStart(this);

                isONCreate = false;
            }

        }
    }
/*
    @SuppressWarnings("MissingPermission")
    @Override
    public void onCameraChange(CameraPosition position) {

        if ((position.target.latitude == 0) && (position.target.longitude == 0)) {
            // Toast.makeText(Home.this, "in Sea", Toast.LENGTH_LONG).show();

            Location myLocation = null;
            if (isPermssionGranted) {

                myLocation = LocationServices.FusedLocationApi
                        .getLastLocation(googleApiClient);
                // pBarMap.show();
            }

            if (myLocation != null) {

                LatLng target = new LatLng(myLocation.getLatitude(),
                        myLocation.getLongitude());

                // CameraPosition position = mMap.getCameraPosition();

                CameraPosition.Builder builder = new CameraPosition.Builder();
                builder.zoom(12);
                builder.target(target);

                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(builder.build()));

                UtilsSingleton.getInstance().setCurrent_lat(
                        myLocation.getLatitude());
                UtilsSingleton.getInstance().setCurrent_longi(
                        myLocation.getLongitude());

            }

        } else {

            //pBarMap.dismiss();

            if (CheckInternetConnectivity.checkinternetconnection(Home.this)) {

                if (getAddress != null) {
                    getAddress.cancel(true);
                }

                //pBar.setVisibility(View.VISIBLE);

                target = position.target;
                newPosition = target;

                UtilsSingleton.getInstance().setCurrent_lat(target.latitude);
                UtilsSingleton.getInstance().setCurrent_longi(target.longitude);

                if (!layoutConfirmationpop.isShown()) {
                    //popup.setVisibility(View.VISIBLE);
                } else {

                    popup.setVisibility(View.INVISIBLE);

                }

                if (isLocationSet == false) {

                    // prgressBarTime.setVisibility(View.VISIBLE);
                    //prgressBarTime.setVisibility(View.GONE);
                    txtTime.setVisibility(View.GONE);

                   // getAddress = new GetAddressTask(Home.this, target);

                   // getAddress.execute();

                    firstLat = target.latitude;
                    firstLong = target.longitude;

                    Log.i("ltlng", target.latitude + "jjj" + target.longitude);
                }

                isLocationSet = false;

            } else {
                showDialog("Alert !", "No Internet Connection.", 0);
            }
        }

    }*/

  /*  @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
        if (arg0 == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (arg1 == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, arg2);
                Log.i(TAG, "Place: " + place.getName());
                txtDestPage1.setText(place.getName());
            } else if (arg1 == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, arg2);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

                // The user canceled the operation.
            }
        }


        if (arg0 == 0) {

            if (arg2 == null) {

                findViewById(R.id.layoutLocations).setVisibility(View.GONE);
                //	mLayout.setVisibility(View.GONE);
                findViewById(R.id.layoutLocations).setVisibility(
                        View.VISIBLE);

                mSearchLocation.setVisibility(View.VISIBLE);
                layoutDestination.setVisibility(View.VISIBLE);
                pBar.setVisibility(View.GONE);


            } else if (arg2 != null) {

                BOOKCAB = true;

                popup.setVisibility(View.GONE);
                locationPin.setVisibility(View.VISIBLE);
//				confirmationPopView(arg2.getExtras().getString("name"), arg2
//						.getExtras().getString("address"));

                findViewById(R.id.layoutHomeBottom).setVisibility(View.VISIBLE);

                locationName = arg2.getExtras().getString("name");
                locationAddress = arg2.getExtras().getString("address");
                startLat = Double
                        .parseDouble(arg2.getExtras().getString("lat"));
                startLNG = Double
                        .parseDouble(arg2.getExtras().getString("lng"));
                //	mLayout.setVisibility(View.GONE);
                final LatLng newTaget = new LatLng(startLat, startLNG);

                firstLat = startLat;
                firstLong = startLNG;

                CameraPosition.Builder builder = new CameraPosition.Builder();
                builder.zoom(12);
                builder.target(newTaget);

                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(builder.build()));

                edtPickUpAddress.setText(arg2.getExtras().getString("name"));

                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        new WebServiceAsynk(URL.BASE_URL + URL.GET_DRIVER
                                + sharedPreference.getUserId() + URL.LAT
                                + newTaget.latitude + URL.LNG
                                + newTaget.longitude + URL.TAXI_TYPE + taxiType
                                + "&bookingId=0", Home.this, Home.this, false,
                                "first").execute();
                    }
                }, 1000);

                UtilsSingleton.getInstance().setCurrent_lat(startLat);
                UtilsSingleton.getInstance().setCurrent_longi(startLNG);

                isLocationSet = true;
            }

        } else if (arg0 == 1) {

            if (arg2 == null) {


                if (layoutConfirmationVisibilityBool && !isDestinationChange) {
                    findViewById(R.id.layoutLocations).setVisibility(
                            View.VISIBLE);
                    layoutDestination.setVisibility(View.VISIBLE);

                }

                mSearchLocation.setVisibility(View.VISIBLE);

                pBar.setVisibility(View.GONE);

            } else if (arg2 != null) {
                //	popup.setVisibility(View.GONE);
                locationPin.setVisibility(View.VISIBLE);

                txtDestinationAddress = (TextView) findViewById(R.id.txtDestinationAddress);

                endLat = Double.parseDouble(arg2.getExtras().getString("lat"));
                endLNG = Double.parseDouble(arg2.getExtras().getString("lng"));


                //layoutShowDestination.setVisibility(View.VISIBLE);
                btnDestinationShow.setVisibility(View.GONE);

                txtDestinationName.setText(arg2.getExtras().getString("name"));
                txtDestinationAddress.setText(arg2.getExtras().getString(
                        "address"));
                edtDropOffAddress1.setText(arg2.getExtras().getString("name"));
                if (isDestinationChange == true) {
                    layoutShowDestination.setVisibility(View.VISIBLE);
                    findFareQuote(taxiType, startLat, startLNG, endLat, endLNG);

                }

                isDestinationChange = false;
                btnDestinationCancel.setVisibility(View.GONE);

                btnDestinationCancel.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        //btnDestinationShow.setVisibility(View.VISIBLE);
                        layoutShowDestination.setVisibility(View.GONE);
                        txtDestinationName.setText("");
                    }
                });
            }
        } else if (arg0 == 786) {
            // final LocationManager manager = (LocationManager) Home.this
            // .getSystemService(Context.LOCATION_SERVICE);
            // if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // // do something
            // displayAlert();
            // } else {
            googleApiClient.connect();
//                mLocationClient = new LocationClient(this, this, this);

            newPosition = new LatLng(UtilsSingleton.getInstance()
                    .getCurrent_lat(), UtilsSingleton.getInstance()
                    .getCurrent_longi());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    newPosition, 17);
            mMap.animateCamera(cameraUpdate);
            getAddress = new GetAddressTask(Home.this, newPosition);

            getAddress.execute();
        } else if (arg0 == LOCATIONFORBOOKING) {

            if (arg2 != null) {

                popup.setVisibility(View.GONE);
                locationPin.setVisibility(View.VISIBLE);
                confirmationPopView(arg2.getExtras().getString("name"), arg2
                        .getExtras().getString("address"));

                locationName = arg2.getExtras().getString("name");
                locationAddress = arg2.getExtras().getString("address");
                startLat = Double
                        .parseDouble(arg2.getExtras().getString("lat"));
                startLNG = Double
                        .parseDouble(arg2.getExtras().getString("lng"));
                mLayout.setVisibility(View.GONE);
                final LatLng newTaget = new LatLng(startLat, startLNG);

                firstLat = startLat;
                firstLong = startLNG;

                CameraPosition.Builder builder = new CameraPosition.Builder();
                builder.zoom(12);
                builder.target(newTaget);

                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(builder.build()));

                edtPickUpAddress.setText(arg2.getExtras().getString("name"));

                bookCab();

                UtilsSingleton.getInstance().setCurrent_lat(startLat);
                UtilsSingleton.getInstance().setCurrent_longi(startLNG);

                isLocationSet = true;
            }

        } else if (arg0 == 111) {
            btnCard.setText("**** "
                    + sharedPreference.getCreditcardno().subSequence(
                    sharedPreference.getCreditcardno().length() - 4,
                    sharedPreference.getCreditcardno().length()));

            txtcardNumber.setText("**** **** **** "
                    + sharedPreference.getCreditcardno().subSequence(
                    sharedPreference.getCreditcardno().length() - 4,
                    sharedPreference.getCreditcardno().length()));
            if (sharedPreference.getCardType().equals("Visa")) {

                imgCrediCardType.setImageResource(R.drawable.icon_cc_visa);

            } else if (sharedPreference.getCardType().equals("Master")) {
                imgCrediCardType
                        .setImageResource(R.drawable.icon_cc_master_card);
            } else if (sharedPreference.getCardType().equals("AmEx")) {
                imgCrediCardType
                        .setImageResource(R.drawable.icon_cc_american_excepress);
            } else if (sharedPreference.getCardType().equals("Discover")) {
                imgCrediCardType.setImageResource(R.drawable.icon_cc_discover);

            }
        }
        // }
        //
        // }
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onTouchModeChanged(boolean isInTouchMode) {
        // TODO Auto-generated method stub
        if (isInTouchMode) {
            Toast.makeText(Home.this, "Toching", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(Home.this, "NOT Toching", Toast.LENGTH_LONG).show();

        }

    }


    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub

        // popup.setVisibility(View.GONE);
        // super.onUserInteraction();
    }

    /**
     * List of address by adding lat long of location
     *
     * @param latitude  lat of location
     * @param longitude long of location
     * @return
     */
    @SuppressLint("NewApi")
    public List<Address> getCurrentAddress(final double latitude,
                                           final double longitude) {
        try {

            geocoder = new Geocoder(Home.this);

            if ((latitude != 0 || longitude != 0)) {

                new GetMarkerAddress(Home.this, latitude, longitude).execute();
                return addresses;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void response(String strresponse, String webservice) {

        // Log.i("", strresponse);

        if (strresponse.contains("formatted_address")) {
            try {
                JSONObject mJsonObject = new JSONObject(strresponse);
                JSONArray mJsonArray = mJsonObject.getJSONArray("results");
                if (mJsonArray.length() > 0) {
                    JSONObject mChild = mJsonArray.getJSONObject(0);
                    JSONArray mArray = mChild
                            .getJSONArray("address_components");
                    String formatted_address = mChild
                            .getString("formatted_address");
                    JSONObject mchildJsonObject = mArray.getJSONObject(0);
                    String name = mchildJsonObject.getString("long_name");
                    edtPickUpAddress.setText(formatted_address);

                    //pBar.setVisibility(View.GONE);
                    txtLocationName.setText(formatted_address);
                    //srEditText.setText(formatted_address);
                    locationAddress = formatted_address;
                    locationName = name;
                    // pBar.setVisibility(View.GONE);
                    // markerMoving.destroy();
                    // new WebServiceAsynk(URL.BASE_URL + URL.GET_DRIVER
                    // + sharedPreference.getUserId() + URL.LAT
                    // + target.latitude + URL.LNG + target.longitude
                    // + URL.TAXI_TYPE + taxiType + "&bookingId=0",
                    // Home.this, Home.this, false, "first").execute();
                    addresses.clear();
                    mHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            new WebServiceAsynk(URL.BASE_URL + URL.GET_DRIVER
                                    + sharedPreference.getUserId() + URL.LAT
                                    + target.latitude + URL.LNG
                                    + target.longitude + URL.TAXI_TYPE
                                    + taxiType + "&bookingId=0", Home.this,
                                    Home.this, false, "first").execute();
                        }
                    }, 5000);

                } else {
                    edtPickUpAddress.setText("Pick up address");
                    txtLocationName.setText("Pick Up address");

                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (strresponse.contains("getDriversById")) {

            JSONObject obj;
            try {
                obj = new JSONObject(strresponse);

                if (obj.getString("getDriversById").equals("-4")) {

                    showDialog("Alert!", obj.getString("message"), 1);

                } else {

                    //showMarkers(strresponse);
                    try {

                        markerMoving.destroy();

                        if (isFirstTime == true) {
                            // mMap.clear();
                            mDriverArrayList.clear();
                            newDriversList.clear();
                            hasMarkersLoaded = false;
                        }

                        JSONObject mJsonObject = new JSONObject(strresponse);

                        if (strresponse.contains("time")) {

                            textConfirmation
                                    .setText("PICKUP TIME IS APPROXIMATELY "
                                            + mJsonObject.getString("time"));

                            if (!mJsonObject.getString("time").equals("")) {

                                if (mJsonObject.getString("time")
                                        .subSequence(1, 2).equals(" ")) {

                                    txtTime.setText("  "
                                            + mJsonObject.getString("time")
                                            .substring(0, 1)
                                            + " "
                                            + mJsonObject.getString("time")
                                            .substring(2, 5));
                                } else {
                                    try {
                                        txtTime.setText(mJsonObject.getString(
                                                "time").substring(0, 2)
                                                + " "
                                                + mJsonObject.getString("time")
                                                .substring(3, 6));
                                    } catch (Exception e) {
                                        // TODO: handle exception
                                    }

                                }

                                //  prgressBarTime.setVisibility(View.GONE);
                                txtTime.setVisibility(View.VISIBLE);

                            }
                        } else {
                            textConfirmation
                                    .setText("PICKUP TIME IS APPROXIMATELY N.A.");

                            txtTime.setText("");

                        }

                        JSONArray mJsonArray = mJsonObject
                                .getJSONArray("getDriversById");

                        if (mJsonArray.length() > 0) {
                            popup.setBackgroundResource(R.drawable.btn_set_pickup_location);
                            txtTime.setVisibility(View.VISIBLE);
                            empty = "full";
                            for (int i = 0; i < mJsonArray.length(); i++) {
                                JSONObject mJsonObject2 = mJsonArray
                                        .getJSONObject(i);

                                MyMarker marker = new MyMarker();
                                marker.setDriverId(mJsonObject2.getString("id"));
                                marker.setLatitude(Double
                                        .parseDouble(mJsonObject2
                                                .getString("driverLat")));
                                marker.setLongitude(Double
                                        .parseDouble(mJsonObject2
                                                .getString("driverLong")));
                                marker.setAddress("address");
                                marker.setName("name");
                                marker.setCompanyId(mJsonObject2.getString("companyID"));
                                marker.setAddress("name");
                                marker.setBearing(mJsonObject2
                                        .getString("bearingDegree"));
                                if (!hasMarkersLoaded) {
                                    mDriverArrayList.add(marker);
                                } else {
                                    newDriversList.add(marker);
                                }

                            }
                            if (isFirstTime == true) {
                                markerMoving.reScheduleTimer(mActivity,
                                        URL.BASE_URL + URL.GET_DRIVER
                                                + sharedPreference.getUserId()
                                                + URL.LAT
                                                + newPosition.latitude
                                                + URL.LNG
                                                + newPosition.longitude
                                                + URL.TAXI_TYPE + taxiType
                                                + "&bookingId=0", Home.this);
                            }

                        } else {

                            empty = "empty";
                            // showDialog("Alert !",
                            // "No Taxi available in this region");
                            markerMoving.reScheduleTimer(mActivity,
                                    URL.BASE_URL + URL.GET_DRIVER
                                            + sharedPreference.getUserId()
                                            + URL.LAT + newPosition.latitude
                                            + URL.LNG + newPosition.longitude
                                            + URL.TAXI_TYPE + taxiType
                                            + "&bookingId=0", Home.this);
                            popup.setBackgroundResource(R.drawable.btn_no_cabs_available);
                            //prgressBarTime.setVisibility(View.GONE);
                        }
                        showMarkers();
                        JSONObject mObject = mJsonObject
                                .getJSONObject("taxiAvailable");
                        hybridCount = Integer.parseInt(mObject.getString("Hybrid"));
                        suvCount = Integer.parseInt(mObject.getString("SUV"));
                        blackCount = Integer.parseInt(mObject.getString("Black"));
                        luxuryCount = Integer.parseInt(mObject.getString("luxury"));
                        limoCount = Integer.parseInt(mObject.getString("limo"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

        } else if (strresponse.contains("getTaxiDetail")) {
            try {
                //pBar1.setVisibility(View.VISIBLE);
                //progressTaxiInfo.setVisibility(View.GONE);
               // progressTaxiInfo2.setVisibility(View.GONE);
               // progressTaxiInfo3.setVisibility(View.GONE);

                JSONObject mJsonObject = new JSONObject(strresponse);
                JSONObject mJsonObject2 = mJsonObject
                        .getJSONObject("getTaxiDetail");

                txtEta.setText(mJsonObject2.getString("ETA"));
               // pBar1.setVisibility(View.GONE);
               // progressTaxiInfo.setVisibility(View.GONE);
                txtbasefair.setText(mJsonObject2.getString("minFare"));
                txtMaxPerson.setText(mJsonObject2.getString("persons"));
                txtbasefare1.setText("Base Fare "
                        + mJsonObject2.getString("baseFare"));
                txtBaseFare3.setText(mJsonObject2.getString("above") + " and "
                        + mJsonObject2.getString("below"));
                txtBaseFare2.setText("+");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private void showMarkers(String strresponse) {
        try {
            JSONObject jsonObject = new JSONObject(strresponse);
            JSONObject mObject = jsonObject
                    .getJSONObject("taxiAvailable");
            hybridCount = Integer.parseInt(mObject.getString("Hybrid"));
            suvCount = Integer.parseInt(mObject.getString("SUV"));
            blackCount = Integer.parseInt(mObject.getString("Black"));
            luxuryCount = Integer.parseInt(mObject.getString("luxury"));
            limoCount = Integer.parseInt(mObject.getString("limo"));

            JSONArray mJsonArray = jsonObject
                    .getJSONArray("getDriversById");
            if (mJsonArray.length() == 0) {
                clearAllMarkers();
            } else {
                for (int i = 0; i < mJsonArray.length(); i++) {
                    JSONObject mJsonObject2 = mJsonArray
                            .getJSONObject(i);

                    MyMarker marker = new MyMarker();
                    marker.setDriverId(mJsonObject2.getString("id"));
                    marker.setLatitude(Double
                            .parseDouble(mJsonObject2
                                    .getString("driverLat")));
                    marker.setLongitude(Double
                            .parseDouble(mJsonObject2
                                    .getString("driverLong")));
                    marker.setAddress("address");
                    marker.setName("name");
                    marker.setCompanyId(mJsonObject2.getString("companyID"));
                    marker.setAddress("name");
                    marker.setBearing(mJsonObject2
                            .getString("bearingDegree"));

                    matrix = new Matrix();
                    matrix = new Matrix();
                    matrix.postRotate(Float.parseFloat(marker.getBearing()) - 90);

                    switch (Integer.parseInt(jsonObject.getString("taxiType"))) {
                        case 1:
                            scaledBitmap = regularBmp;
                            break;
                        case 2:
                            scaledBitmap = suvBmp;
                            break;

                        case 3:
                            scaledBitmap = blackBmp;
                            break;
                        case 4:
                            scaledBitmap = suvBmp;
                            break;
                        case 5:
                            scaledBitmap = luxuryBmp;
                            break;
                        default:
                            scaledBitmap = luxuryBmp;
                            break;

                    }

                    rotatedBitmap = Bitmap
                            .createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(),
                                    scaledBitmap.getHeight(), matrix, true);

                    Marker marker1 = mMap.addMarker(new MarkerOptions()
                            .position(marker.getMarker().getPosition()).title(marker.getMarker().getSnippet())
                            .snippet(marker.getMarker().getSnippet())
                            .icon(BitmapDescriptorFactory.fromBitmap(rotatedBitmap)));
                    Log.d(TAG, "Marker Added" + marker1.getId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void zoomRoute(GoogleMap googleMap, ArrayList<LatLng> lstLatLngRoute) {

        if (googleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 50;
        LatLngBounds latLngBounds = boundsBuilder.build();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
    }

    private void clearAllMarkers() {
        if (mDriverArrayList != null) mDriverArrayList.clear();
        if (newDriversList != null) newDriversList.clear();
    }

    private Marker addMarkerDriver(GoogleMap map, double lat, double lon,
                                   String title, String snippet, MyMarker myMarker) {
        matrix = new Matrix();

        matrix.postRotate(0);
        if (taxiType == 1) {

            scaledBitmap = regularBmp;

        } else if (taxiType == 2) {
            scaledBitmap = suvBmp;

        } else if (taxiType == 3) {
            scaledBitmap = blackBmp;

        } else if (taxiType == 4) {

            scaledBitmap = suvBmp;

        } else {

            scaledBitmap = blackBmp;
        }

        rotatedBitmap = Bitmap
                .createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(),
                        scaledBitmap.getHeight(), matrix, true);

        Marker marker = null;
        try {
            marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lon)).title(title)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.fromBitmap(rotatedBitmap)));

            if (selectedCompanyIndex == 0) {
                marker.setVisible(true);
                Log.d(TAG, "Addmarker  show all " + selectedCompanyIndex);
            } else if (companies.get(selectedCompanyIndex - 1).id.equalsIgnoreCase(myMarker.getCompanyId())) {
                marker.setVisible(true);
                Log.d(TAG, "Addmarker  show" + selectedCompanyIndex + "-" + myMarker.getCompanyId());
            } else {
                marker.setVisible(false);
                Log.d(TAG, "Addmarker  hide" + selectedCompanyIndex + "-" + myMarker.getCompanyId());
            }
        } catch (Exception e) {// may throw unknown source exception

        }
        return marker;

    }

    /*public void setData(){
        mDriverArrayList = new ArrayList<MyMarker>();
        MyMarker marker = new MyMarker();
        marker.setDriverId("d1");
        marker.setLatitude(19.2513789);
        marker.setLongitude(72.9734032);
        marker.setCompanyName(items[0]);
        mDriverArrayList.add(marker);
        Marker driversMarker = addMarkerDriver(mMap,
                marker.getLatitude(), marker.getLongitude(),
                marker.getCompanyName(), "");
        marker.setMarker(driversMarker);

        MyMarker marker1 = new MyMarker();
        marker1.setDriverId("d4");
        marker1.setLatitude(19.2512426);
        marker1.setLongitude(72.9798767);
        marker1.setCompanyName(items[1]);
        mDriverArrayList.add(marker1);
         driversMarker = addMarkerDriver(mMap,
                 marker1.getLatitude(), marker1.getLongitude(),
                 marker1.getCompanyName(), "");
        marker1.setMarker(driversMarker);

        MyMarker marker2 = new MyMarker();
        marker2.setDriverId("d2");
        marker2.setLatitude(19.2512536);
        marker2.setLongitude(72.9732345);
        marker2.setCompanyName(items[2]);
        mDriverArrayList.add(marker2);
        driversMarker = addMarkerDriver(mMap,
                marker2.getLatitude(), marker2.getLongitude(),
                marker2.getCompanyName(), "");
        marker1.setMarker(driversMarker);

        MyMarker marker3 = new MyMarker();
        marker3.setDriverId("d3");
        marker3.setLatitude(19.2518755);
        marker3.setLongitude(72.9737567);
        marker3.setCompanyName(items[3]);
        mDriverArrayList.add(marker3);
        driversMarker = addMarkerDriver(mMap,
                marker3.getLatitude(), marker3.getLongitude(),
                marker3.getCompanyName(), "");
        marker1.setMarker(driversMarker);
    }*/

    private void showMarkers() {
        //First time run
        if (newDriversList.size() == 0) {
            if (empty.equalsIgnoreCase("empty")) {
                //MyMarker marker = mDriverArrayList.get(i);
                //marker.getMarker().remove();
                // markersMap.
                mDriverArrayList.clear();
                newDriversList.clear();
                //mMap.clear();
            }
            for (int i = 0; i < mDriverArrayList.size(); i++) {
                if (empty.equalsIgnoreCase("empty")) {
                    MyMarker marker = mDriverArrayList.get(i);
                    marker.getMarker().remove();
                    markersMap.remove(marker.getDriverId());
                    mDriverArrayList.clear();
                    newDriversList.clear();
                } else {

                    MyMarker marker = mDriverArrayList.get(i);
                    Marker driversMarker = addMarkerDriver(mMap,
                            marker.getLatitude(), marker.getLongitude(),
                            marker.getAddress(), "", mDriverArrayList.get(i));
                    marker.setMarker(driversMarker);
                }
            }
            hasMarkersLoaded = true;
        } else {
            //Second time run
            //Remove marker which are not in updated list
            for (int i = 0; i < mDriverArrayList.size(); i++) {
                MyMarker marker = mDriverArrayList.get(i);
                boolean isToBeDeleted = true;
                for (int j = 0; j < newDriversList.size(); j++) {
                    MyMarker newMarker = newDriversList.get(j);
                    if (marker.getDriverId().equals(newMarker.getDriverId())) {
                        markersMap.put(marker.getDriverId(), marker);
                        isToBeDeleted = false;
                        break;
                    }
                }
                if (isToBeDeleted && marker.getMarker() != null) {
                    marker.getMarker().remove();
                    markersMap.remove(marker.getDriverId());
                }
            }

            for (int i = 0; i < newDriversList.size(); i++) {
                MyMarker marker = newDriversList.get(i);
                //Already ploated marker then update
                if (markersMap.containsKey(marker.getDriverId())) {
                    MyMarker marker2 = markersMap.get(marker.getDriverId());
                    Marker driversMarker = marker2.getMarker();
                    if (driversMarker != null) {
                        driversMarker.setPosition(new LatLng(marker
                                .getLatitude(), marker.getLongitude()));
                        matrix = new Matrix();
                        matrix.postRotate(Float.parseFloat(marker.getBearing()) - 90);
                        scaledBitmap = null;
                        if (taxiType == 1) {
                            scaledBitmap = regularBmp;
                        } else if (taxiType == 2) {
                            scaledBitmap = suvBmp;
                        } else if (taxiType == 3) {
                            scaledBitmap = blackBmp;
                        } else if (taxiType == 4) {
                            scaledBitmap = suvBmp;
                        } else {
                            scaledBitmap = blackBmp;
                        }

                        rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                                scaledBitmap.getWidth(),
                                scaledBitmap.getHeight(), matrix, true);
                        driversMarker.setIcon(BitmapDescriptorFactory
                                .fromBitmap(rotatedBitmap));
                        marker.setMarker(driversMarker);
                    }
                } else { // or add new
                    Marker driversMarker = addMarkerDriver(mMap,
                            marker.getLatitude(), marker.getLongitude(),
                            marker.getName(), marker.getPhoneNumber() + ";"
                                    + marker.getDriverImage() + ";"
                                    + marker.getAddress(), newDriversList.get(i));
                    marker.setMarker(driversMarker);
                    markersMap.put(marker.getDriverId(), marker);
                }
            }
            mDriverArrayList.clear();
            for (int i = 0; i < newDriversList.size(); i++) {
                mDriverArrayList.add(newDriversList.get(i));

                Marker marker = mDriverArrayList.get(i).getMarker();
                if (selectedCompanyIndex == 0) {
                    marker.setVisible(true);
                    Log.d(TAG, "setmarker  show all " + selectedCompanyIndex);
                } else if (companies.get(selectedCompanyIndex - 1).id.equalsIgnoreCase(mDriverArrayList.get(i).getCompanyId())) {
                    marker.setVisible(true);
                    Log.d(TAG, "setmarker  show" + selectedCompanyIndex + "-" + mDriverArrayList.get(i).getCompanyId());
                } else {
                    marker.setVisible(false);
                    Log.d(TAG, "setmarker  hide" + selectedCompanyIndex + "-" + mDriverArrayList.get(i).getCompanyId());
                }


            }
        }

    }

    private void confirmationPopView(String location, String address) {

        if (!Utility.isPaymentGatewayOn) {
            findViewById(R.id.rel_1).setVisibility(View.GONE);
            findViewById(R.id.confirm_divieder_1).setVisibility(View.GONE);
        }
        btnChangeCreditCard.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                startActivityForResult(new Intent(Home.this,
                        ChangeCardType.class).putExtra("pending", "0"), 111);

            }
        });

        layoutShowLocation.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

            }
        });
        layoutShowDestination.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

            }
        });
        btnFareQuote = (RelativeLayout) findViewById(R.id.btnFareQuote);
        btnFareQuote.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (!layoutShowDestination.isShown()) {

                    isDestinationChange = true;

                    Intent i = new Intent(Home.this, PickUpLocation.class);
                    i.putExtra("via", "ADD DESTINATION");
                    startActivityForResult(i, 1);

                } else {

                    findFareQuote(taxiType, startLat, startLNG, endLat, endLNG);
                }

            }
        });
        //btnDestinationShow.setVisibility(View.VISIBLE);
        layoutShowLocation.setVisibility(View.VISIBLE);
        layoutConfirmationHeader.setVisibility(View.VISIBLE);

        layoutHomeTop.setVisibility(View.GONE);

        mSearchLocation.setVisibility(View.GONE);
        layoutConfirmationpop.setVisibility(View.VISIBLE);
        layoutConfirmationVisibilityBool = true;
        txtLocationName.setText(location);

        txtLocationAddress.setText(address);

        btnPromoCodeConfirmation.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivity(new Intent(Home.this, PromoCodeListing.class)
                        .putExtra("what", 2));
            }
        });

        btnDestinationShow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (!layoutShowDestination.isShown()) {

                    // isDestinationChange = true;

                    Intent i = new Intent(Home.this, PickUpLocation.class);
                    i.putExtra("via", "ADD DESTINATION");
                    startActivityForResult(i, 1);

                } else {

                    findFareQuote(taxiType, startLat, startLNG, endLat, endLNG);
                }

				/*
                 * startActivityForResult(new Intent(Home.this,
				 * PickUpLocation.class).putExtra("via", "PICK UP LOCATION"),
				 * 0);
				 */

				/*
                 * Intent i = new Intent(Home.this, PickUpLocation.class);
				 * i.putExtra("via", "ADD DESTINATION");
				 * startActivityForResult(i, 1);
				 */

            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                isLocationSet = false;

                if (tutorialView.isShown()) {
                    tutorialView.setVisibility(View.GONE);
                } else if (!mFareQuate.isShown()) {
                    //popup.setVisibility(View.VISIBLE);
                    locationPin.setVisibility(View.VISIBLE);
                    layoutShowLocation.setVisibility(View.GONE);
                    layoutConfirmationHeader.setVisibility(View.GONE);

                    layoutConfirmationpop.setVisibility(View.GONE);
                    layoutConfirmationVisibilityBool = false;
                    layoutShowDestination.setVisibility(View.GONE);

                    layoutHomeTop.setVisibility(View.VISIBLE);

                    findViewById(R.id.layoutLocations).setVisibility(
                            View.VISIBLE);
                    findViewById(R.id.layoutHomeBottom)
                            .setVisibility(View.GONE);

                    mSearchLocation.setVisibility(View.VISIBLE);
                    btnDestinationCancel.setVisibility(View.VISIBLE);
                    mLayout.setVisibility(View.VISIBLE);

                    layoutDestination.setVisibility(View.VISIBLE);

                    endLat = 0.0d;
                    endLNG = 0.0d;

                    edtDropOffAddress1.setText(" Drop off address");


                } else {

                    // txtTitle.setText("CONFIRMATION");

                    layoutConfirmationpop.setVisibility(View.VISIBLE);
                    layoutConfirmationVisibilityBool = true;
                    mFareQuate.setVisibility(View.GONE);
                    layoutFareQuoteVisibilityBool = false;
                    //btnMyLocation.setVisibility(View.VISIBLE);
                    findViewById(R.id.layoutHomeBottom)
                            .setVisibility(View.GONE);

                }

            }
        });
        // btnPromoCodeConfirmation,btnFareQuote,btnRequest

        txtcardNumber.setTypeface(typeFace);
        btnRequest.setTypeface(typeFace);
        ((TextView) findViewById(R.id.txtNewFareQuote)).setTypeface(typeFace);
        ((TextView) findViewById(R.id.txtNewPromoCode)).setTypeface(typeFace);

        if (Utility.isPaymentGatewayOn) {

            if (sharedPreference.getCreditcardno().length() > 4) {
                txtcardNumber.setText("**** **** **** "
                        + sharedPreference.getCreditcardno()
                        .substring(
                                sharedPreference.getCreditcardno()
                                        .length() - 4,
                                sharedPreference.getCreditcardno()
                                        .length()));

            }
            if (sharedPreference.getCardType().equals("Visa")) {
                imgCrediCardType.setImageResource(R.drawable.icon_cc_visa);

            } else if (sharedPreference.getCardType().equals("Master")) {
                imgCrediCardType
                        .setImageResource(R.drawable.icon_cc_master_card);
            } else if (sharedPreference.getCardType().equals("AmEx")) {
                imgCrediCardType
                        .setImageResource(R.drawable.icon_cc_american_excepress);
            } else if (sharedPreference.getCardType().equals("Discover")) {
                imgCrediCardType.setImageResource(R.drawable.icon_cc_discover);

            }

        }

        if (taxiType == 3) {
            btnRequest.setText("REQUEST " + Utility.CAR_3);

        } else if (taxiType == 1) {

            btnRequest.setText("REQUEST " + Utility.CAR_1);

        } else if (taxiType == 2) {
            btnRequest.setText("REQUEST " + Utility.CAR_2);

        } else if (taxiType == 4) {

            btnRequest.setText("REQUEST " + Utility.CAR_4);
        } else if (taxiType == 5) {
            btnRequest.setText("REQUEST " + Utility.CAR_5);

        }

        if (endLat != 0.0d && endLNG != 0.0d) {

            layoutShowDestination.setVisibility(View.VISIBLE);

        }

        layoutDestination.setVisibility(View.GONE);

    }

    @Override
    public void onScrollChanged() {
        // TODO Auto-generated method stub
        popup.setVisibility(View.GONE);
    }

    /**
     * formula to calculate distance between two destination android
     */
	/*
	 * private static final int earthRadius = 6371;
	 * 
	 * public static double calculateDistance(double lat1, double lon1, double
	 * lat2, double lon2) { double dLat = (double) Math.toRadians(lat2 - lat1);
	 * double dLon = (double) Math.toRadians(lon2 - lon1); double a = (double)
	 * (Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math
	 * .cos(Math.toRadians(lat1)) Math.cos(Math.toRadians(lat2)) Math.sin(dLon /
	 * 2) * Math.sin(dLon / 2)); double c = (double) (2 *
	 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))); double d = earthRadius * c;
	 * 
	 * return d; }
	 */
	/*
	 * public double CalculationByDistance(double lat1, double lon1, double
	 * lat2, double lon2) { int Radius = 6371;// radius of earth in Km
	 * 
	 * double dLat = Math.toRadians(lat2 - lat1); double dLon =
	 * Math.toRadians(lon2 - lon1); double a = Math.sin(dLat / 2) *
	 * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
	 * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) Math.sin(dLon / 2);
	 * double c = 2 * Math.asin(Math.sqrt(a)); double valueResult = Radius * c;
	 * double km = valueResult / 1; DecimalFormat newFormat = new
	 * DecimalFormat("####"); // kmInDec =
	 * Integer.valueOf(newFormat.format(km)); double meter = valueResult % 1000;
	 * int meterInDec = Integer.valueOf(newFormat.format(meter));
	 * Log.i("Radius Value", "" + valueResult + "   KM  " + "" + " Meter   " +
	 * meterInDec); // Toast.makeText(mActivity, meterInDec + "  ", //
	 * Toast.LENGTH_SHORT).show();
	 * 
	 * double y = Math.sin(dLon) * Math.cos(lat2); double x = Math.cos(lat1) *
	 * Math.sin(lat2) - Math.sin(lat1) Math.cos(lat2) * Math.cos(dLon); double b
	 * = Math.atan2(y, x); double degree = Math.toDegrees(b);
	 * 
	 * Matrix matrix = new Matrix();
	 * 
	 * matrix.postRotate((float) degree); Bitmap scaledBitmap =
	 * BitmapFactory.decodeResource(getResources(),
	 * R.drawable.map_pin_black_car1); Bitmap rotatedBitmap = Bitmap
	 * .createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(),
	 * scaledBitmap.getHeight(), matrix, true); //
	 * myLocMarker.setIcon(BitmapDescriptorFactory.fromBitmap(rotatedBitmap));
	 * 
	 * return Radius * c; }
	 */
    @Override
    public void onResp(String responce) {
        // TODO Auto-generated method stub
        Log.i("response", "onResp" + responce);
        if (setReset == false) {

            // Log.i("Servce Response", responce);
            try {
                JSONObject mJsonObject = new JSONObject(responce);

                if (mJsonObject.getString("getDriversById").equals("-4")) {

                    final String messageLocal = mJsonObject
                            .getString("message");

                    Home.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub

                            showDialog("Alert!", messageLocal, 1);

                        }
                    });

                } else {

                    JSONArray mJsonArray = mJsonObject
                            .getJSONArray("getDriversById");

                    if (mJsonArray.length() > 0) {

                        if (responce.contains("time")) {
                            textConfirmation
                                    .setText("PICKUP TIME IS APPROXIMATELY "
                                            + mJsonObject.getString("time"));

                            if (mJsonObject.getString("time").subSequence(1, 2)
                                    .equals(" ")) {

                                txtTime.setText("  "
                                        + mJsonObject.getString("time")
                                        .substring(0, 1)
                                        + " "
                                        + mJsonObject.getString("time")
                                        .substring(2, 5));
                            } else {
                                txtTime.setText(mJsonObject.getString("time")
                                        .substring(0, 2)
                                        + " "
                                        + mJsonObject.getString("time")
                                        .substring(3, 6));

                            }

                        } else {
                            textConfirmation
                                    .setText("PICKUP TIME IS APPROXIMATELY N.A.");
                            txtTime.setText("");

                        }
                        popup.setBackgroundResource(R.drawable.btn_set_pickup_location);
                        txtTime.setVisibility(View.VISIBLE);
                        newDriversList.clear();
                        empty = "full";
                        for (int i = 0; i < mJsonArray.length(); i++) {
                            JSONObject mJsonObject2 = mJsonArray
                                    .getJSONObject(i);

                            MyMarker marker = new MyMarker();
                            marker.setDriverId(mJsonObject2.getString("id"));
                            marker.setLatitude(Double.parseDouble(mJsonObject2
                                    .getString("driverLat")));
                            marker.setLongitude(Double.parseDouble(mJsonObject2
                                    .getString("driverLong")));
                            marker.setAddress("address");
                            marker.setCompanyId(mJsonObject2.getString("companyID"));
                            marker.setName("name");
                            marker.setAddress("aa");
                            marker.setBearing(mJsonObject2
                                    .getString("bearingDegree"));
                            if (!hasMarkersLoaded) {
                                mDriverArrayList.add(marker);
                            } else {
                                newDriversList.add(marker);
                            }

                        }

                    } else {

                        textConfirmation
                                .setText("PICKUP TIME IS APPROXIMATELY N.A.");
                        txtTime.setText("");
                        newDriversList.clear();
                        popup.setBackgroundResource(R.drawable.btn_no_cabs_available);
                        empty = "empty";
						/*
						 * Toast.makeText(mActivity, "No Taxi Available",
						 * Toast.LENGTH_LONG).show();
						 */

                    }

                    /*
                    "taxiAvailable":{"Hybrid":"1","SUV":"0","Black":"0","luxury":"0","limo":"0"}
                     */
                    //showMarkers(responce);
                    JSONObject mObject = mJsonObject
                            .getJSONObject("taxiAvailable");
                    hybridCount = Integer.parseInt(mObject.getString("Hybrid"));
                    suvCount = Integer.parseInt(mObject.getString("SUV"));
                    blackCount = Integer.parseInt(mObject.getString("Black"));
                    luxuryCount = Integer.parseInt(mObject.getString("luxury"));
                    limoCount = Integer.parseInt(mObject.getString("limo"));

                }

                if (!mJsonObject.getString("paymentStatus").equals("0")) {

                    startActivity(new Intent(Home.this, ChangeCardType.class)
                            .putExtra("pending", "1"));
                    setReset = true;

                }

                if (!mJsonObject.getString("BookingIDOnServer").equals("0")) {
                    // Toast.makeText(Home.this, "test",
                    // Toast.LENGTH_SHORT).show();

                    UtilsSingleton.getInstance()
                            .setCurrent_lat(target.latitude);
                    UtilsSingleton.getInstance().setCurrent_longi(
                            target.longitude);

                    if (!APP_IN_BACKGROUND) {

                        Intent mIntent = new Intent(Home.this,
                                LoadingRequest.class);
                        mIntent.putExtra("bookingId",
                                mJsonObject.getString("BookingIDOnServer"));

                        mIntent.putExtra("via", "auto");
                        startActivity(mIntent);
                        mHandler.sendEmptyMessage(2);
                    }

                    setReset = true;
                }

                UtilsSingleton.getInstance().setPaymentStatus(
                        mJsonObject.getString("paymentStatus"));
                UtilsSingleton.getInstance().setBookingId(
                        mJsonObject.getString("bookingId"));

            } catch (Exception e) {
                // TODO: handle exception

                e.printStackTrace();
            }
        } else if (responce.contains("geocoded_waypoints")) {
            GetJsonResponse(responce);
        }
    }

    public void findFareQuote(int taxiType, double startLat, double startLNG,
                              double endLat, double endLNG) {

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
        new SendXmlAsync(URL.BASE_URL + URL.FARE_QUOTE, xml, Home.this,
                Home.this, true).execute();

    }

    @Override
    public void onResponse(String respose) {
        // TODO Auto-generated method stub
        Log.d("responce requet", respose);
        // Log.i("Response", "" + respose);
        if (!respose.equalsIgnoreCase("")) {

            if (respose.contains("userLogout")) {

                try {
                    JSONObject jObj = new JSONObject(respose);

                    if (jObj.getString("userLogout").equals("1")) {

                        Home.this.finish();

                        sharedPreference.clearPrefRence();
                        startActivity(new Intent(Home.this, Option.class));

                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else if (respose.contains("checkUserStatus")) {

                try {
                    JSONObject jObj = new JSONObject(respose);

                    if (jObj.getString("checkUserStatus").equals("-1")) {

                        showDialog("Alert", jObj.getString("message"), 1);
                    } else {

                        if ((homeBool == true && isVisible == false
                                && layoutConfirmationVisibilityBool == false && layoutFareQuoteVisibilityBool == false)) {

                            if (Home.this != null) {
                                // pBarMap.show();

                                new Handler().postDelayed(new Runnable() {

                                    @SuppressWarnings("MissingPermission")
                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        Location myLocation = null;
                                        if (isPermssionGranted) {
                                            myLocation = LocationServices.FusedLocationApi
                                                    .getLastLocation(googleApiClient);
                                        }
//                                        Location myLocation = mMap
//                                                .getMyLocation();
                                        if (myLocation != null) {

                                            LatLng target = new LatLng(
                                                    myLocation.getLatitude(),
                                                    myLocation.getLongitude());
											/*
											 * CameraPosition position =
											 * mMap.getCameraPosition();
											 */

                                            // Toast.makeText(Home.this,
                                            // "Latback"+UtilsSingleton.getInstance().getBackGroundLat()+" LongBack "
                                            // +UtilsSingleton.getInstance().getBackGroundLong(),
                                            // Toast.LENGTH_SHORT).show();

                                            // if(UtilsSingleton.getInstance().getBackGroundLat()==target.latitude
                                            // &&
                                            // UtilsSingleton.getInstance().getBackGroundLong()==target.longitude){

                                            if (((UtilsSingleton.getInstance()
                                                    .getBackGroundLat() - target.latitude) >= 0.00000001000 || (UtilsSingleton
                                                    .getInstance()
                                                    .getBackGroundLat() - target.latitude) <= 0.00000001000)
                                                    && ((UtilsSingleton
                                                    .getInstance()
                                                    .getBackGroundLong() - target.longitude) >= 0.00000001000 || (UtilsSingleton
                                                    .getInstance()
                                                    .getBackGroundLong() - target.longitude) <= 0.00000001000)
                                                    && UtilsSingleton.counterBack > 2) {

                                                // Toast.makeText(Home.this,
                                                // "Latcurrent "+target.latitude+" LongCurrent "
                                                // +target.longitude,
                                                // Toast.LENGTH_SHORT).show();

                                                // pBarMap.show();
                                                UtilsSingleton.counterBack++;

                                                onResume();

                                            } else {

                                                // Toast.makeText(Home.this,
                                                // "else",
                                                // Toast.LENGTH_LONG).show();

                                                myLocationShow();

                                                homeBool = false;
                                            }

                                            // Toast.makeText(Home.this,
                                            // "Lat"+target.latitude+"Long"+target.longitude,
                                            // Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                }, 6000);
                            }
                        }

                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else if (respose.contains("fareQuote")) {
                try {
                    JSONObject mJsonObject = new JSONObject(respose);
                    if (mJsonObject.getString("fareQuote").equalsIgnoreCase(
                            "-1")) {
                        Toast.makeText(mActivity,
                                mJsonObject.getString("message"),
                                Toast.LENGTH_LONG).show();
                    } else if (mJsonObject.getString("fareQuote")
                            .equalsIgnoreCase("-2")) {
                        Toast.makeText(mActivity,
                                mJsonObject.getString("message"),
                                Toast.LENGTH_LONG).show();
                    } else if (mJsonObject.getString("fareQuote")
                            .equalsIgnoreCase("-3")) {

                    } else {
                        if (mJsonObject.getString("fareQuote").contains("N.A")) {
                            fareNA.setVisibility(View.VISIBLE);
                            txtCarFare.setVisibility(View.INVISIBLE);
                            ((TextView) findViewById(R.id.txtFareQuoteInfo))
                                    .setText("We are unable to estimate the fare between these locations.");
                            ((TextView) findViewById(R.id.txtFareQuoteInfo))
                                    .setTypeface(typeFaceItalic);
							/*
							 * imgFareEA
							 * .setBackgroundResource(R.drawable.bg_farequote_box2_1
							 * );
							 */
                        } else {
                            switch (taxiType) {

                            }
                            txtCarFare.setVisibility(View.VISIBLE);
                            txtCarFare.setText(mJsonObject
                                    .getString("fareQuote"));
                            fareNA.setVisibility(View.GONE);
                            ((TextView) findViewById(R.id.txtFareQuoteInfo))
                                    .setTypeface(typeFaceItalic);

                            ((TextView) findViewById(R.id.txtFareQuoteInfo))
                                    .setText("NOTE: This is an approximate estimate. Actual Cost may be different.");
                        }

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (respose.contains("bookcab")) {

                Log.i("response", "" + respose);
                try {
                    JSONObject mJsonObject = new JSONObject(respose);
                    String message = mJsonObject.getString("message");
                    if (mJsonObject.getString("bookcab").equalsIgnoreCase("-1")) {
                        showDialog("Alert !", message, 0);

                        if (!layoutConfirmationpop.isShown()) {
                            // popup.setVisibility(View.INVISIBLE);

                        } else {
                            // popup.setVisibility(View.VISIBLE);

                        }
                        locationPin.setVisibility(View.VISIBLE);
                        layoutShowLocation.setVisibility(View.GONE);
                        layoutConfirmationHeader.setVisibility(View.GONE);

                        layoutConfirmationpop.setVisibility(View.GONE);
                        layoutConfirmationVisibilityBool = false;
                        layoutShowDestination.setVisibility(View.GONE);

                        layoutHomeTop.setVisibility(View.VISIBLE);

                        mSearchLocation.setVisibility(View.VISIBLE);
                        btnDestinationCancel.setVisibility(View.VISIBLE);
                        mLayout.setVisibility(View.VISIBLE);

                        findViewById(R.id.layoutLocations).setVisibility(
                                View.VISIBLE);
                        layoutDestination.setVisibility(View.VISIBLE);

                    } else if (mJsonObject.getString("bookcab")
                            .equalsIgnoreCase("-2")) {
                        showDialog("Alert !", message, 0);
                        //popup.setVisibility(View.VISIBLE);
                        if (!layoutConfirmationpop.isShown()) {
                            //  popup.setVisibility(View.VISIBLE);
                        } else {
                            //   popup.setVisibility(View.VISIBLE);

                        }

                        findViewById(R.id.layoutLocations).setVisibility(
                                View.VISIBLE);
                        locationPin.setVisibility(View.VISIBLE);
                        layoutShowLocation.setVisibility(View.GONE);
                        layoutConfirmationHeader.setVisibility(View.GONE);

                        layoutConfirmationpop.setVisibility(View.GONE);
                        layoutConfirmationVisibilityBool = false;
                        layoutShowDestination.setVisibility(View.GONE);

                        layoutHomeTop.setVisibility(View.VISIBLE);

                        mSearchLocation.setVisibility(View.VISIBLE);
                        btnDestinationCancel.setVisibility(View.VISIBLE);
                        mLayout.setVisibility(View.VISIBLE);
                        layoutDestination.setVisibility(View.VISIBLE);

                    } else if (mJsonObject.getString("bookcab")
                            .equalsIgnoreCase("-3")
                            || mJsonObject.getString("bookcab")
                            .equalsIgnoreCase("-4")) {
                        showDialog("Alert !", message, 0);
                        //popup.setVisibility(View.VISIBLE);

                        findViewById(R.id.layoutLocations).setVisibility(
                                View.VISIBLE);

						/*
						 * if (!layoutConfirmationpop.isShown()) {
						 * popup.setVisibility(View.VISIBLE); } else {
						 * popup.setVisibility(View.INVISIBLE); }
						 * 
						 * popup.setVisibility(View.VISIBLE);
						 */
                        locationPin.setVisibility(View.VISIBLE);
                        layoutShowLocation.setVisibility(View.GONE);
                        layoutConfirmationHeader.setVisibility(View.GONE);

                        layoutConfirmationpop.setVisibility(View.GONE);
                        layoutConfirmationVisibilityBool = false;
                        layoutShowDestination.setVisibility(View.GONE);

                        layoutHomeTop.setVisibility(View.VISIBLE);

                        mSearchLocation.setVisibility(View.VISIBLE);
                        btnDestinationCancel.setVisibility(View.VISIBLE);
                        mLayout.setVisibility(View.VISIBLE);
                        layoutDestination.setVisibility(View.VISIBLE);

                    } else {

                        if (!APP_IN_BACKGROUND) {
                            Intent mIntent = new Intent(Home.this,
                                    LoadingRequest.class);
                            mIntent.putExtra("bookingId",
                                    mJsonObject.getString("bookcab"));

                            mIntent.putExtra("via", "manual");
                            startActivity(mIntent);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else if (respose.contains("companies")) {
                try {
                    companies = new ArrayList<Company>();
                    JSONObject objectResponse = new JSONObject(respose);
                    JSONArray jsonArray = objectResponse.getJSONArray("companies");
                    comapanyNames = new String[jsonArray.length() + 1];
                    selectedIndex = new boolean[comapanyNames.length + 1];
                    comapanyNames[0] = "All";
                    selectedIndex[0] = true;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Company company = new Company();
                        company.id = object.get("id").toString();
                        company.name = object.get("companyName").toString();
                        company.address = object.get("companyAddress").toString();
                        company.email = object.get("emailAddress").toString();
                        company.phone = object.get("companyPhone").toString();
                        company.registrationNumber = object.get("registrationNumber").toString();
                        company.status = object.get("status").toString();
                        companies.add(company);
                        comapanyNames[i + 1] = object.get("companyName").toString();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (respose.contains("geocoded_waypoints")) {
                GetJsonResponse(respose);
            }

        }

    }

    public void GetJsonResponse(String response) {
        if (response != null) {
            DirectionsJSONParser parser = new DirectionsJSONParser();
            List<List<HashMap<String, String>>> routes = null;
            try {
                routes = parser.parse(new JSONObject(response));

                ArrayList<LatLng> points = null;

                for (int i = 0; i < routes.size(); i++) {
                    points = new ArrayList<>();
                    // lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = routes.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }
                }

                drawPoints(points);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
//

    public void addTiles() {
        TileOverlayOptions opts = new TileOverlayOptions();

// Find your MapBox online map ID.
        String myMapID = "mapbox.satellite";

// Create an instance of MapBoxOnlineTileProvider.
        MapBoxOnlineTileProvider provider = new MapBoxOnlineTileProvider(myMapID);

// Set the tile provider on the TileOverlayOptions.
        opts.tileProvider(provider);

// Add the tile overlay to the map.
        TileOverlay overlay = mMap.addTileOverlay(opts);

        //TileOverlayOptions overlay = new TileOverlayOptions().tileProvider(provider);
        // mMap.addTileOverlay(overlay);
        overlay.setZIndex(-1);


    }

    private ArrayList<LatLng> traceOfMe = null;
    private Polyline mPolyline = null;

    private void drawPoints(ArrayList<LatLng> points) {
        if (points == null) {
            return;
        }
        traceOfMe = points;
        PolylineOptions polylineOpt = new PolylineOptions();
        polylineOpt.geodesic(true);
        for (LatLng latlng : traceOfMe) {
            polylineOpt.add(latlng);
        }
        polylineOpt.color(ContextCompat.getColor(this, R.color.black));
        if (mPolyline != null) {
            mPolyline.remove();
            mPolyline = null;
        }
        if (mMap != null) {
            mPolyline = mMap.addPolyline(polylineOpt);
        }

        startPointMarker = new MarkerOptions();
        startPointMarker.position(traceOfMe.get(0));
        startPointMarker.flat(true);
        startPointMarker.anchor(0.5f, 0.5f);
        startPointMarker.icon(BitmapDescriptorFactory
                .fromResource(R.drawable.map_pin_user_red));

        endPointMarker = new MarkerOptions();
        endPointMarker.position(traceOfMe.get(traceOfMe.size() - 1));
        endPointMarker.flat(true);
        endPointMarker.anchor(0.5f, 0.5f);
        endPointMarker.icon(BitmapDescriptorFactory
                .fromResource(R.drawable.map_pin_user_red_new));

        mMap.addMarker(startPointMarker);
        mMap.addMarker(endPointMarker);

        if (mPolyline != null)
            mPolyline.setWidth(10);

        LatLngBounds routeArea = new LatLngBounds(startPointMarker.getPosition(), endPointMarker.getPosition());
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(routeArea.getCenter(), 50));
    }

    ArrayList<Company> companies;

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub

    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub
        Log.i("***", arg0 + "");
        if (UtilsSingleton.getInstance().getCurrent_lat() == 0) {
            if (googleApiClient.isConnected()) {
                Location currentLocation = null;
                if (isPermssionGranted) {
                    currentLocation = LocationServices.FusedLocationApi
                            .getLastLocation(googleApiClient);
                }

                if (currentLocation == null) {
                } else {

                    UtilsSingleton.getInstance().setCurrent_lat(
                            currentLocation.getLatitude());
                    UtilsSingleton.getInstance().setCurrent_longi(
                            currentLocation.getLongitude());
                    if (isPermssionGranted) {
//                        Location myLocation = mMap.getMyLocation();
                        myLocation = LocationServices.FusedLocationApi
                                .getLastLocation(googleApiClient);
                        mMap.setMyLocationEnabled(true);
                    }

                    if (myLocation != null) {
                        LatLng target = new LatLng(myLocation.getLatitude(),
                                myLocation.getLongitude());

                        CameraPosition position = mMap.getCameraPosition();

                        CameraPosition.Builder builder = new CameraPosition.Builder();
                        builder.zoom(12);
                        builder.target(target);

                        mMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(builder.build()));
                        getAddress = new GetAddressTask(Home.this, target);
                        getAddress.execute();
                    } else {
                        googleApiClient.connect();
                    }

                }

            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    public void bookCab() {

        String bookingType = "";


        if (Utility.cashFlow) {
            if (sharedPreference.getPaymentViaCreditCard()) {

                bookingType = "1";
            } else {
                bookingType = "0";
            }
        } else {


            bookingType = "1";
        }

        PreferenceManager.getDefaultSharedPreferences(Home.this).edit()
                .putInt("type", 1).commit();

        String xmlBookCab = "";

        UtilsSingleton.getInstance().setCurrent_lat(target.latitude);
        UtilsSingleton.getInstance().setCurrent_longi(target.longitude);

        String dropOffStatus = "1";
        /*if (endLat != 0.0d && endLNG != 0.0d)*/
        {

            //dropOffStatus = "1";

            xmlBookCab = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><bookcab><userId><![CDATA["
                    + sharedPreference.getUserId()
                    + "]]></userId><taxiType><![CDATA["
                    + taxiType
                    + "]]></taxiType><latitude><![CDATA["
                    + firstLat
                    + "]]></latitude><longitude><![CDATA["
                    + firstLong
                    + "]]></longitude>"
                    + ""
                    + "<dropoffLat><![CDATA["
                    + endLat
                    + "]]></dropoffLat><dropoffLong><![CDATA["
                    + endLNG
                    + "]]></dropoffLong><dropoffStatus><![CDATA["
                    + dropOffStatus
                    + "]]></dropoffStatus><dropoffLocation><![CDATA["
                    + txtDestinationName.getText().toString()
                    + "]]></dropoffLocation>"
                    + "<dropoffAddress><![CDATA["
                    + dsEditText.getText().toString()
                    + "]]></dropoffAddress><promoCodeVal><![CDATA["
                    + ""
                    + "]]></promoCodeVal>"
                    + "<userPromoCodeId><![CDATA["
                    + ""
                    + "]]></userPromoCodeId><pickUpLocation><![CDATA["
                    + locationName
                    + "]]></pickUpLocation><pickUpAddress><![CDATA["
                    + locationAddress
                    + "]]></pickUpAddress>"
                    + "<bookingType><![CDATA["
                    + bookingType
                    + "]]></bookingType>" + "</bookcab>";

        }/* else {

            // showDialog("Alert!","Please select destination place", 3);
            dropOffStatus = "0";

            xmlBookCab = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><bookcab><userId><![CDATA["
                    + sharedPreference.getUserId()
                    + "]]></userId><taxiType><![CDATA["
                    + taxiType
                    + "]]></taxiType><latitude><![CDATA["
                    + firstLat
                    + "]]></latitude><longitude><![CDATA["
                    + firstLong
                    + "]]></longitude>"
                    + ""
                    + "<dropoffLat><![CDATA["
                    + ""
                    + "]]></dropoffLat><dropoffLong><![CDATA["
                    + ""
                    + "]]></dropoffLong><dropoffStatus><![CDATA["
                    + dropOffStatus
                    + "]]></dropoffStatus><dropoffLocation><![CDATA["
                    + ""
                    + "]]></dropoffLocation>"
                    + "<dropoffAddress><![CDATA["
                    + ""
                    + "]]></dropoffAddress>"
                    + "<promoCodeVal><![CDATA["
                    + ""
                    + "]]></promoCodeVal>"
                    + "<userPromoCodeId><![CDATA["
                    + ""
                    + "]]></userPromoCodeId><pickUpLocation><![CDATA["
                    + locationName
                    + "]]></pickUpLocation><pickUpAddress><![CDATA["
                    + locationAddress
                    + "]]></pickUpAddress>"
                    + "<bookingType><![CDATA["
                    + bookingType
                    + "]]></bookingType>" + "</bookcab>";

        }*/

        new SendXmlAsync(URL.BASE_URL + URL.BOOK_NOW, xmlBookCab, Home.this,
                Home.this, true).execute();

        endLat = 0.0d;
        endLNG = 0.0d;

        edtDropOffAddress1.setText(" Drop off address");

    }

    /**
     * Dialog to show alerts
     *
     * @param title   setting title of dialog
     * @param message setting message
     */

    private void showDialog(String title, String message, final int type) {

        dialog = new Dialog(Home.this, style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_warning);
        TextView txtTitle = (TextView) dialog
                .findViewById(R.id.dialog_title_text);
        TextView txtMessage = (TextView) dialog
                .findViewById(R.id.dialog_message_text);

        Button btnYes = (Button) dialog.findViewById(R.id.dialog_ok_btn);
        Button btnNo = (Button) dialog.findViewById(R.id.dialog_cancel_btn);

        btnYes.setVisibility(View.GONE);
        btnNo.setVisibility(View.GONE);
        Button btnOk = (Button) dialog.findViewById(R.id.btnDone);
        btnOk.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                if (type == 1) {

                    new SendXmlAsync(URL.BASE_URL + URL.LOG_OUT
                            + sharedPreference.getUserId(), "", Home.this,
                            Home.this, true).execute();

                    dialog.dismiss();

                } else if (type == 2) {

                    seekBar.setProgress(0);
                    taxiType = 3;
                    isFirstTime = true;
                    //  mMap.clear();
                    markerMoving.destroy();
                    gettingDataForFirstTime();
                    dialog.dismiss();

					/*
					 * btnBlack.setImageResource(R.drawable.black_hr);
					 * btnSUV.setImageResource(R.drawable.suv);
					 * btnLuxury.setImageResource(R.drawable.lux);
					 * btnHybrid.setImageResource(R.drawable.exotic);
					 * btnArmour.setImageResource(R.drawable.armor);
					 */
                } else if (type == 3) {

                    dialog.dismiss();
					/*
					 * startActivityForResult(new Intent(Home.this,
					 * PickUpLocation.class).putExtra("via",
					 * "PICK UP LOCATION"), LOCATIONFORBOOKING);
					 */

                    // TODO Auto-generated method stub

                    mPopUpLayout.setVisibility(View.GONE);
                    isVisible = false;

                    if (whatIsSelected.equals(HYBRID)) {
                        btnHybrid
                                .setBackgroundResource(R.drawable.car_regular_slctd);

                    }

                    if (whatIsSelected.equals(SUV)) {

                        btnSUV.setBackgroundResource(R.drawable.car_suv_slctd);
                    }

                    if (whatIsSelected.equals(BLACK)) {

                        btnBlack.setBackgroundResource(R.drawable.car_black_car_slctd);
                    }

                    if (whatIsSelected.equals(LUXURY)) {

                        btnLuxury
                                .setBackgroundResource(R.drawable.car_luxury_slctd);
                    }

                    if (whatIsSelected.equals(ARMOUR)) {
                        btnArmour
                                .setBackgroundResource(R.drawable.car_armour_slctd);

                    }

                    popup.setVisibility(View.GONE);
                    // locationPin.setVisibility(View.GONE);

                    confirmationPopView(locationName, locationAddress);

                    isDestinationChange = true;

                    Intent i = new Intent(Home.this, PickUpLocation.class);
                    i.putExtra("via", "ADD DESTINATION");
                    startActivityForResult(i, 1);

                } else {

                    dialog.dismiss();
                }

            }
        });

        txtTitle.setText(title);
        txtMessage.setText(message);

        dialog.show();

    }

    @Override
    public boolean onMarkerClick(Marker arg0) {
        // TODO Auto-generated method stub
        return true;
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //if (isPermssionGranted) {
        mMap.setMyLocationEnabled(true);
        //}
        // set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
      //  mMap.setOnCameraChangeListener(this);
        mMap.setOnMarkerClickListener(this);
//        mMap.setOnCameraMoveListener(this);
//        mMap.setOnCameraIdleListener(this);
//        mMap.setOnCameraMoveStartedListener(this);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        mMap.getUiSettings().setScrollGesturesEnabled(true);
        //addTiles();
        // mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        //setUpMap();
        //    TileProvider wmsTileProvider = TileProviderFactory.getOsgeoWmsTileProvider();
        //  mMap.addTileOverlay(new TileOverlayOptions().tileProvider(wmsTileProvider));

        // Because the demo WMS layer we are using is just a white background map, switch the base layer
        // to satellite so we can see the WMS overlay.
        //   mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* @Override
    public void onCameraIdle() {
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onCameraMove() {
        *//*if ((position.target.latitude == 0) && (position.target.longitude == 0)) {
            // Toast.makeText(Home.this, "in Sea", Toast.LENGTH_LONG).show();
//            if(isPermssionGranted) {
//                gps = new GPS(Home.this);
//            }


            if(isPermssionGranted){

                myLocation = LocationServices.FusedLocationApi
                        .getLastLocation(googleApiClient);
                pBarMap.show();
            }

            if (myLocation != null) {

                LatLng target = new LatLng(myLocation.getLatitude(),
                        myLocation.getLongitude());
				*//**//*
				 * CameraPosition position = mMap.getCameraPosition();
				 *//**//*
                CameraPosition.Builder builder = new CameraPosition.Builder();
                builder.zoom(12.8f);
                builder.target(target);

                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(builder.build()));

                UtilsSingleton.getInstance().setCurrent_lat(
                        myLocation.getLatitude());
                UtilsSingleton.getInstance().setCurrent_longi(
                        myLocation.getLongitude());

                // Toast.makeText(Home.this,
                // "Lat"+target.latitude+"Long"+target.longitude,
                // Toast.LENGTH_SHORT).show();

            }

        } else {

            pBarMap.dismiss();

            if (CheckInternetConnectivity.checkinternetconnection(Home.this)) {

                if (getAddress != null) {
                    getAddress.cancel(true);
                }

                pBar.setVisibility(View.VISIBLE);

                target = position.target;
                newPosition = target;

                UtilsSingleton.getInstance().setCurrent_lat(target.latitude);
                UtilsSingleton.getInstance().setCurrent_longi(target.longitude);

                // Toast.makeText(
                // Home.this,
                // "latitude  :" + target.latitude + "  Long : "
                // + target.longitude, 5000).show();
                // if (!btnDestinationShow.isShown()) {

                if (!layoutConfirmationpop.isShown()) {
                    popup.setVisibility(View.VISIBLE);
                } else {

                    popup.setVisibility(View.INVISIBLE);

                }

                if (isLocationSet == false) {

                    // prgressBarTime.setVisibility(View.VISIBLE);
                    prgressBarTime.setVisibility(View.GONE);
                    txtTime.setVisibility(View.GONE);

                    getAddress = new GetAddressTask(Home.this, target);

                    getAddress.execute();
                    // getCurrentAddress(target.latitude, target.longitude);

					*//**//*
					 * if (CalculationByDistance(firstLat, firstLong,
					 * target.latitude, target.longitude) > 50)
					 *
					 * {
					 *//**//*

                    // }

                    firstLat = target.latitude;
                    firstLong = target.longitude;

                    Log.i("ltlng", target.latitude + "jjj" + target.longitude);
                }

                isLocationSet = false;

            } else {
                showDialog("Alert !", "No Internet Connection.", 0);
            }
        }*//*
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onCameraMoveStarted(int i) {
        Location myLocation = null;

        if (mMap.getCameraPosition().target.latitude==0 && mMap.getCameraPosition().target.longitude==0) {
            // Toast.makeText(Home.this, "in Sea", Toast.LENGTH_LONG).show();
//            if(isPermssionGranted) {
//                gps = new GPS(Home.this);
//            }

            if(isPermssionGranted){
                myLocation = LocationServices.FusedLocationApi
                        .getLastLocation(googleApiClient);

                gps = new GPS(Home.this);
            }

            pBarMap.show();
//            if(isPermssionGranted){
//
//                myLocation = LocationServices.FusedLocationApi
//                        .getLastLocation(googleApiClient);
//                pBarMap.show();
//            }

            if (myLocation != null) {

                LatLng target = new LatLng(myLocation.getLatitude(),
                        myLocation.getLongitude());
				*//*
				 * CameraPosition position = mMap.getCameraPosition();
				 *//*
                CameraPosition.Builder builder = new CameraPosition.Builder();
                builder.zoom(12.8f);
                builder.target(target);

                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(builder.build()));

                UtilsSingleton.getInstance().setCurrent_lat(
                        myLocation.getLatitude());
                UtilsSingleton.getInstance().setCurrent_longi(
                        myLocation.getLongitude());

                // Toast.makeText(Home.this,
                // "Lat"+target.latitude+"Long"+target.longitude,
                // Toast.LENGTH_SHORT).show();

            }

        } else {

            pBarMap.dismiss();

            if (CheckInternetConnectivity.checkinternetconnection(Home.this)) {

                if (getAddress != null) {
                    getAddress.cancel(true);
                }

                pBar.setVisibility(View.VISIBLE);

                if(isPermssionGranted){
                    myLocation = LocationServices.FusedLocationApi
                            .getLastLocation(googleApiClient);

                    gps = new GPS(Home.this);
                }

                if(myLocation!=null) {

                    LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    target = latLng;
                    newPosition = target;

                    UtilsSingleton.getInstance().setCurrent_lat(target.latitude);
                    UtilsSingleton.getInstance().setCurrent_longi(target.longitude);
                }

                // Toast.makeText(
                // Home.this,
                // "latitude  :" + target.latitude + "  Long : "
                // + target.longitude, 5000).show();
                // if (!btnDestinationShow.isShown()) {

                if (!layoutConfirmationpop.isShown()) {
                    popup.setVisibility(View.VISIBLE);
                } else {

                    popup.setVisibility(View.INVISIBLE);

                }

                if (isLocationSet == false) {

                    // prgressBarTime.setVisibility(View.VISIBLE);
                    prgressBarTime.setVisibility(View.GONE);
                    txtTime.setVisibility(View.GONE);

                    getAddress = new GetAddressTask(Home.this, target);

                    getAddress.execute();
                    // getCurrentAddress(target.latitude, target.longitude);

					*//*
					 * if (CalculationByDistance(firstLat, firstLong,
					 * target.latitude, target.longitude) > 50)
					 *
					 * {
					 *//*

                    // }

                    if(target!=null) {
                        firstLat = target.latitude;
                        firstLong = target.longitude;

                        Log.i("ltlng", target.latitude + "jjj" + target.longitude);
                    }
                }

                isLocationSet = false;

            } else {
                showDialog("Alert !", "No Internet Connection.", 0);
            }
        }
    }*/

    class GetMarkerAddress extends AsyncTask<Void, Void, Void> {
        double latitude, longitude;
        Context context;
        String currentLocation = "";

        public GetMarkerAddress(Context context, double latitude,
                                double longitude) {
            this.context = context;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                geocoder = new Geocoder(Home.this);
                addresses = geocoder.getFromLocation(this.latitude,
                        this.longitude, 1);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (addresses.size() != 0) {
                String address = addresses.get(0).getAddressLine(0);
                srEditText.setText(address);
                markerMoving.destroy();
                new WebServiceAsynk(URL.BASE_URL + URL.GET_DRIVER
                        + sharedPreference.getUserId() + URL.LAT
                        + target.latitude + URL.LNG + target.longitude
                        + URL.TAXI_TYPE + taxiType + "&bookingId=0", Home.this,
                        Home.this, false, "first").execute();
            }

        }
    }


    /**
     * Geocoder asynctask
     */
    public class GetAddressTask extends AsyncTask<Void, Void, String> {
        Context mContext;

        LatLng latLng;

        public GetAddressTask(Context context, LatLng loc) {

            mContext = context;
            latLng = loc;
        }

        @Override
        protected String doInBackground(Void... params) {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latLng.latitude,
                        latLng.longitude, 1);

            } catch (IOException e1) {
                return ("No address");

            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
                return ("No address.");
            }

            if (addresses != null && addresses.size() > 0) {

                Address address = addresses.get(0);

                String addressText = String.format(
                        "%s",
                        address.getAddressLine(0) + " "
                                + address.getAddressLine(1));
                locationAddress = address.getLocality();
                return addressText;

            } else {
                return "No address";
            }
        }

        @Override
        protected void onPostExecute(String address) {

            if (!address.equals("No address")) {
                // String addressnew = addresses.get(0).getAddressLine(0);
                //
                // String city = addresses.get(0).getAddressLine(1);
                // String country = addresses.get(0).getAddressLine(2);
                // Log.d("TAG", "address = " + address + ", city =" + city
                // + ", country = " + country);
                // String currentLocation = address + ", " + city;
                final LocationManager manager = (LocationManager) Home.this
                        .getSystemService(Context.LOCATION_SERVICE);
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    // do something
                    // srEditText.setText("Pick up address");
                    edtPickUpAddress.setText("Pick up address");
                    //pBar.setVisibility(View.GONE);
                } else {
                    edtPickUpAddress.setText(address + " " + locationAddress);
                    //pBar.setVisibility(View.GONE);
                    locationName = address;
                    // locationAddress = address;
                    srEditText.setText(address);
                    edtPickUpAddress.setText(address.replace("null", ""));
                }

                if (locationName != null) {
                    txtLocationName.setText(locationName.replace("null", ""));
                } else {
                    txtLocationName.setText("No Location Found");
                }

                if (locationAddress != null) {
                    txtLocationAddress.setText(locationAddress.replace("null",
                            ""));
                } else {
                    txtLocationAddress.setText("");
                }

                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        if (target != null) {
                            new WebServiceAsynk(URL.BASE_URL + URL.GET_DRIVER
                                    + sharedPreference.getUserId() + URL.LAT
                                    + target.latitude + URL.LNG
                                    + target.longitude + URL.TAXI_TYPE
                                    + taxiType + "&bookingId=0", Home.this,
                                    Home.this, false, "first").execute();
                        }
                    }
                }, 1000);

            } else {
                new GoogleGeocode(Home.this, latLng.latitude, latLng.longitude)
                        .execute();
            }
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION.equals(action)) {
                final LocationManager manager = (LocationManager) context
                        .getSystemService(Context.LOCATION_SERVICE);
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    // do something
                } else {
                    edtPickUpAddress.setText("Pick up address");
                    displayAlert();
                }
                // do something else
            }
        }
    };

    protected void displayAlert() {

        dialogNew = new Dialog(Home.this, style.Theme_Translucent_NoTitleBar);
        dialogNew.setContentView(R.layout.dialog_warning);
        TextView txtTitle = (TextView) dialogNew
                .findViewById(R.id.dialog_title_text);
        TextView txtMessage = (TextView) dialogNew
                .findViewById(R.id.dialog_message_text);

        Button btnYes = (Button) dialogNew.findViewById(R.id.dialog_ok_btn);
        Button btnNo = (Button) dialogNew.findViewById(R.id.dialog_cancel_btn);

        btnYes.setVisibility(View.GONE);
        btnNo.setVisibility(View.GONE);
        Button btnOk = (Button) dialogNew.findViewById(R.id.btnDone);
        btnOk.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                dialogNew.dismiss();
                startActivityForResult(new Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS), 786);
            }
        });

        txtTitle.setText("Location Manager");
        txtMessage.setText("Please enable GPS to proceed.");
        dialogNew.setCanceledOnTouchOutside(false);
        dialogNew.show();

    }

    @Override
    public void onLowMemory() {
        System.gc();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

    }

    @Override
    public void locationChanged(double longitude, double latitude) {
        if ((longitude == 0) && (latitude == 0)) {
            if (isPermssionGranted) {
                gps = new GPS(Home.this);
            }

        } else {

        }
    }

    @Override
    public void displayGPSSettingsDialog() {
        displayAlert();

    }

    class GoogleGeocode extends AsyncTask<Void, Void, Void> {

        JSONObject ret = null;
        double lat, lngi;
        Context context;

        public GoogleGeocode(Context context, double lat, double lng) {
            this.context = context;
            this.lat = lat;

            this.lngi = lng;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // get lat and lng value
            ret = getLocationInfo(lat, lngi);

            return null;
        }

        @Override
        protected void onPreExecute() {
            //pBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            JSONObject location;
            String location_string;
            try {
                // Get JSON Array called "results" and then get the 0th complete
                // object as JSON
                location = ret.getJSONArray("results").getJSONObject(0);
                // Get the value of the attribute whose name is
                // "formatted_string"
                location_string = location.getString("formatted_address");
                Log.d("test", "formattted address:" + location_string);
                edtPickUpAddress.setText(location_string);
                //pBar.setVisibility(View.GONE);
                locationName = location_string;
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        new WebServiceAsynk(URL.BASE_URL + URL.GET_DRIVER
                                + sharedPreference.getUserId() + URL.LAT
                                + target.latitude + URL.LNG + target.longitude
                                + URL.TAXI_TYPE + taxiType + "&bookingId=0",
                                Home.this, Home.this, false, "first").execute();
                    }
                }, 1000);
            } catch (JSONException e1) {
                e1.printStackTrace();

            }
            super.onPostExecute(result);

        }
    }

    public JSONObject getLocationInfo(double lat, double lngi) {
        HttpGet httpGet = new HttpGet(
                "http://maps.google.com/maps/api/geocode/json?latlng=" + lat
                        + "," + lngi + "&sensor=false");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        APP_IN_BACKGROUND = true;

        new Runbackground().execute();
        if (markerMoving != null) {
            markerMoving.destroy();
        }

    }

    class Runbackground extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            homeBool = isApplicationBroughtToBackground();

            return null;
        }

    }

    private boolean isApplicationBroughtToBackground() {
        ActivityManager am = (ActivityManager) Home.this
                .getSystemService(Context.ACTIVITY_SERVICE);

        try {
            List<RunningTaskInfo> tasks = am.getRunningTasks(1);
            if (!tasks.isEmpty()) {
                ComponentName topActivity = tasks.get(0).topActivity;
                if (!topActivity.getPackageName().equals(
                        Home.this.getPackageName())) {

                    UtilsSingleton.getInstance().setBackGroundLat(
                            target.latitude);
                    UtilsSingleton.getInstance().setBackGroundLong(
                            target.longitude);

                    UtilsSingleton.counterBack = 0;

                    return true;
                }
            }
        } catch (Exception e) {

        }

        return false;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub

        if (scaledBitmap != null) {
            scaledBitmap.recycle();
        }

        if (rotatedBitmap != null) {

            rotatedBitmap.recycle();
        }

        regularBmp.recycle();
        suvBmp.recycle();
        blackBmp.recycle();
        luxuryBmp.recycle();

        super.onDestroy();

        System.gc();
    }

    @SuppressWarnings("MissingPermission")
    private void myLocationShow() {

        if (Home.this != null) {
            Location myLocationNew = null;
            if (isPermssionGranted) {
                myLocationNew = LocationServices.FusedLocationApi
                        .getLastLocation(googleApiClient);
            }
//            Location myLocationNew = mMap.getMyLocation();
            if (myLocationNew != null) {

                LatLng target = new LatLng(myLocationNew.getLatitude(),
                        myLocationNew.getLongitude());

				/*
				 * LatLng target = new LatLng(34.171230d, -118.226402d);
				 */
				/*
				 * CameraPosition position = mMap.getCameraPosition();
				 */
                CameraPosition.Builder builder = new CameraPosition.Builder();
                builder.zoom(12.8f);
                builder.target(target);

                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(builder.build()));

                UtilsSingleton.getInstance().setCurrent_lat(
                        myLocationNew.getLatitude());
                UtilsSingleton.getInstance().setCurrent_longi(
                        myLocationNew.getLongitude());
                //pBarMap.dismiss();

            }

        }
    }

    private void checkAccountValidity() {

        new SendXmlAsync(URL.BASE_URL + URL.ACTIVATIONACCOUNT
                + sharedPreference.getUserId(), "", Home.this, Home.this, true)
                .execute();

    }

    private void showDialogg(String title, String message) {

        final Dialog dialog = new Dialog(Home.this,
                style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.pickup_confirmation_dialog);
        dialog.setCancelable(true);
        TextView txtTitle = (TextView) dialog
                .findViewById(R.id.dialog_title_text);
        TextView txtMessage = (TextView) dialog
                .findViewById(R.id.dialog_message_text);

        Button btnYes = (Button) dialog.findViewById(R.id.dialog_ok_btn);
        Button btnNo = (Button) dialog.findViewById(R.id.dialog_cancel_btn);

        btnYes.setVisibility(View.VISIBLE);
        btnNo.setVisibility(View.VISIBLE);

        btnYes.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
/*
                startActivityForResult(new Intent(Home.this,
                        PickUpLocation.class).putExtra("via",
                        "PICK UP LOCATION"), LOCATIONFORBOOKING);
                dialog.dismiss();*/

            }
        });

        btnNo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                bookCab();

                dialog.dismiss();

            }
        });

        txtTitle.setText(title);
        txtMessage.setText(message);

        dialog.show();

    }

    private void gettingDataForFirstTime() {

        new WebServiceAsynk(URL.BASE_URL + URL.GET_DRIVER
                + sharedPreference.getUserId() + URL.LAT + newPosition.latitude
                + URL.LNG + newPosition.longitude + URL.TAXI_TYPE + taxiType
                + "&bookingId=0", Home.this, Home.this, false, "first")
                .execute();
    }

    private void gettingInformationAboutTaxi() {
        //progressTaxiInfo.setVisibility(View.VISIBLE);
        //progressTaxiInfo2.setVisibility(View.VISIBLE);
        //progressTaxiInfo3.setVisibility(View.VISIBLE);

        txtEta.setText("");

        txtbasefair.setText("");
        txtMaxPerson.setText("");
        txtbasefare1.setText("");
        txtBaseFare3.setText("");
        txtBaseFare2.setText("");

        new WebServiceAsynk(URL.BASE_URL + URL.TAXI_DETAIL
                + sharedPreference.getUserId() + URL.TAXI_TYPE + taxiType
                + URL.LAT + newPosition.latitude + URL.LNG
                + newPosition.longitude, Home.this, Home.this, false, "")
                .execute();
    }

    private void hidingFareQuoteScreen() {

        findViewById(R.id.layoutHomeBottom).setVisibility(View.GONE);

        layoutConfirmationpop.setVisibility(View.VISIBLE);
        layoutConfirmationVisibilityBool = true;
        mFareQuate.setVisibility(View.GONE);
        layoutFareQuoteVisibilityBool = false;
        //btnMyLocation.setVisibility(View.VISIBLE);
    }

    @Override
    public void OnGettingLatitudeLongitude(double latitude, double longitude,
                                           Location location, int uniqueCode) {
        // TODO Auto-generated method stub

        LatLng latlng = new LatLng(latitude, longitude);
        CameraPosition.Builder builder = new CameraPosition.Builder();
        builder.zoom(12.8f);
        builder.target(latlng);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(builder
                .build()));
        // Toast.makeText(this, "animateCamera", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void noLocationFound() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGettingAddress(String address, int uniqueCode) {
        srEditText.setText(address);

    }

    @Override
    public void OnConnected() {
        // TODO Auto-generated method stub

        // Toast.makeText(this, "OnConnected", Toast.LENGTH_SHORT).show();
        mLocationClass.getLocations(this, this, 0);


    }

    String CONFIRM = "CONFIRM ";

    public void showCarDetail(View view) {
        showCarDetail(view.getId(), 0);
    }

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
            btnRequest.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    bookCab();
                }
            });
        }

    }

    private void bottomBarControl() {
        btnSUV.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //showCarDetail();
            }

        });

        btnHybrid.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (!isVisible) {
                    // onSwipe(SimpleGestureFilter.SWIPE_LEFT);

                    // mMap.clear();
                    isFirstTime = true;
                    markerMoving.destroy();

                    taxiType = 1;

                    if (whatIsSelected.equals(HYBRID)) {

                        mPopUpLayout.setVisibility(View.VISIBLE);
                        isVisible = true;
                        btnHybrid
                                .setBackgroundResource(R.drawable.car_regular_hvr);
                        btnSUV.setBackgroundResource(R.drawable.car_suv);
                        btnBlack.setBackgroundResource(R.drawable.car_black_car);
                        btnLuxury.setBackgroundResource(R.drawable.car_luxury);
                        btnArmour.setBackgroundResource(R.drawable.car_armour);

                        popup.setVisibility(View.GONE);
                        mMap.getUiSettings().setZoomGesturesEnabled(false);

                        mMap.getUiSettings().setScrollGesturesEnabled(false);

                        mMap.getUiSettings().setZoomControlsEnabled(false);
                        /*progressTaxiInfo.setVisibility(View.VISIBLE);
                        progressTaxiInfo2.setVisibility(View.VISIBLE);
                        progressTaxiInfo3.setVisibility(View.VISIBLE);
*/
                        txtEta.setText("");

                        txtbasefair.setText("");
                        txtMaxPerson.setText("");
                        txtbasefare1.setText("");
                        txtBaseFare3.setText("");
                        txtBaseFare2.setText("");

                        // txtTitlePopUp.setText("SUV");
                        txtTitlePopUp.setText(Utility.CAR_1);

                        mMap.getUiSettings().setScrollGesturesEnabled(false);

                        mPopUpLayout.setVisibility(View.VISIBLE);
                        isVisible = true;

                        new WebServiceAsynk(URL.BASE_URL + URL.TAXI_DETAIL
                                + sharedPreference.getUserId() + URL.TAXI_TYPE
                                + taxiType + URL.LAT + newPosition.latitude
                                + URL.LNG + newPosition.longitude, Home.this,
                                Home.this, false, "").execute();

                        mPopUpLayout.setVisibility(View.VISIBLE);
                        isVisible = true;

                    } else {

                        whatIsSelected = HYBRID;

                        btnHybrid
                                .setBackgroundResource(R.drawable.car_regular_slctd);
                        btnSUV.setBackgroundResource(R.drawable.car_suv);
                        btnBlack.setBackgroundResource(R.drawable.car_black_car);
                        btnLuxury.setBackgroundResource(R.drawable.car_luxury);
                        btnArmour.setBackgroundResource(R.drawable.car_armour);
                    }

                    new WebServiceAsynk(URL.BASE_URL + URL.GET_DRIVER
                            + sharedPreference.getUserId() + URL.LAT
                            + newPosition.latitude + URL.LNG
                            + newPosition.longitude + URL.TAXI_TYPE + taxiType
                            + "&bookingId=0", Home.this, Home.this, false,
                            "first").execute();

                } else {

                    if (taxiType == 1) {

                        hidingPopUpOnDoubleTap();

                    } else {

                        whatIsSelected = HYBRID;
                        btnHybrid
                                .setBackgroundResource(R.drawable.car_regular_hvr);
                        btnSUV.setBackgroundResource(R.drawable.car_suv);
                        btnBlack.setBackgroundResource(R.drawable.car_black_car);
                        btnLuxury.setBackgroundResource(R.drawable.car_luxury);
                        btnArmour.setBackgroundResource(R.drawable.car_armour);

                        /*progressTaxiInfo.setVisibility(View.VISIBLE);
                        progressTaxiInfo2.setVisibility(View.VISIBLE);
                        progressTaxiInfo3.setVisibility(View.VISIBLE);
*/
                        txtEta.setText("");

                        txtbasefair.setText("");
                        txtMaxPerson.setText("");
                        txtbasefare1.setText("");
                        txtBaseFare3.setText("");
                        txtBaseFare2.setText("");

                        // txtTitlePopUp.setText("SUV");
                        txtTitlePopUp.setText(Utility.CAR_1);

                        taxiType = 1;

                        new WebServiceAsynk(URL.BASE_URL + URL.TAXI_DETAIL
                                + sharedPreference.getUserId() + URL.TAXI_TYPE
                                + taxiType + URL.LAT + newPosition.latitude
                                + URL.LNG + newPosition.longitude, Home.this,
                                Home.this, false, "").execute();
                    }
                }

            }
        });

        btnBlack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (!isVisible) {
                    // onSwipe(SimpleGestureFilter.SWIPE_LEFT);

                    // mMap.clear();
                    isFirstTime = true;
                    markerMoving.destroy();
                    taxiType = 3;

                    if (whatIsSelected.equals(BLACK)) {

                        btnHybrid.setBackgroundResource(R.drawable.car_regular);
                        btnSUV.setBackgroundResource(R.drawable.car_suv);
                        btnBlack.setBackgroundResource(R.drawable.car_black_car_hvr);
                        btnLuxury.setBackgroundResource(R.drawable.car_luxury);
                        btnArmour.setBackgroundResource(R.drawable.car_armour);

                        popup.setVisibility(View.GONE);
                        mMap.getUiSettings().setZoomGesturesEnabled(false);

                        mMap.getUiSettings().setScrollGesturesEnabled(false);

                        mMap.getUiSettings().setZoomControlsEnabled(false);
                        /*progressTaxiInfo.setVisibility(View.VISIBLE);
                        progressTaxiInfo2.setVisibility(View.VISIBLE);
                        progressTaxiInfo3.setVisibility(View.VISIBLE);
*/
                        txtEta.setText("");

                        txtbasefair.setText("");
                        txtMaxPerson.setText("");
                        txtbasefare1.setText("");
                        txtBaseFare3.setText("");
                        txtBaseFare2.setText("");

                        // txtTitlePopUp.setText("Black Car");
                        txtTitlePopUp.setText(Utility.CAR_3);

                        mMap.getUiSettings().setScrollGesturesEnabled(false);

                        mPopUpLayout.setVisibility(View.VISIBLE);
                        isVisible = true;

                        new WebServiceAsynk(URL.BASE_URL + URL.TAXI_DETAIL
                                + sharedPreference.getUserId() + URL.TAXI_TYPE
                                + taxiType + URL.LAT + newPosition.latitude
                                + URL.LNG + newPosition.longitude, Home.this,
                                Home.this, false, "").execute();

                        mPopUpLayout.setVisibility(View.VISIBLE);
                        isVisible = true;
                    } else {

                        whatIsSelected = BLACK;

                        btnHybrid.setBackgroundResource(R.drawable.car_regular);
                        btnSUV.setBackgroundResource(R.drawable.car_suv);
                        btnBlack.setBackgroundResource(R.drawable.car_black_car_slctd);
                        btnLuxury.setBackgroundResource(R.drawable.car_luxury);
                        btnArmour.setBackgroundResource(R.drawable.car_armour);

                    }

                    new WebServiceAsynk(URL.BASE_URL + URL.GET_DRIVER
                            + sharedPreference.getUserId() + URL.LAT
                            + newPosition.latitude + URL.LNG
                            + newPosition.longitude + URL.TAXI_TYPE + taxiType
                            + "&bookingId=0", Home.this, Home.this, false,
                            "first").execute();
                } else {

                    if (taxiType == 3) {

                        hidingPopUpOnDoubleTap();

                    } else {
                        whatIsSelected = BLACK;
                        taxiType = 3;
                        /*progressTaxiInfo.setVisibility(View.VISIBLE);
                        progressTaxiInfo2.setVisibility(View.VISIBLE);
                        progressTaxiInfo3.setVisibility(View.VISIBLE);
*/
                        txtEta.setText("");

                        txtbasefair.setText("");
                        txtMaxPerson.setText("");
                        txtbasefare1.setText("");
                        txtBaseFare3.setText("");
                        txtBaseFare2.setText("");

                        // txtTitlePopUp.setText("Black Car");
                        txtTitlePopUp.setText(Utility.CAR_3);

                        btnHybrid.setBackgroundResource(R.drawable.car_regular);
                        btnSUV.setBackgroundResource(R.drawable.car_suv);
                        btnBlack.setBackgroundResource(R.drawable.car_black_car_hvr);
                        btnLuxury.setBackgroundResource(R.drawable.car_luxury);
                        btnArmour.setBackgroundResource(R.drawable.car_armour);

                        new WebServiceAsynk(URL.BASE_URL + URL.TAXI_DETAIL
                                + sharedPreference.getUserId() + URL.TAXI_TYPE
                                + taxiType + URL.LAT + newPosition.latitude
                                + URL.LNG + newPosition.longitude, Home.this,
                                Home.this, false, "").execute();

                    }

                }

            }
        });
        btnLuxury.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
				/*
				 * startActivity(new Intent(Home.this, Reservation.class)
				 * .putExtra(Utility.TAG_RESERVATION, Utility.CAR_4));
				 */

                // TODO Auto-generated method stub

                if (!isVisible) { // onSwipe(SimpleGestureFilter.SWIPE_LEFT);

                    //mMap.clear();
                    isFirstTime = true;
                    markerMoving.destroy();
                    taxiType = 4;

                    if (whatIsSelected.equals(LUXURY)) {

                        btnHybrid.setBackgroundResource(R.drawable.car_regular);
                        btnSUV.setBackgroundResource(R.drawable.car_suv);
                        btnBlack.setBackgroundResource(R.drawable.car_black_car);
                        btnLuxury
                                .setBackgroundResource(R.drawable.car_luxury_hvr);
                        btnArmour.setBackgroundResource(R.drawable.car_armour);
                        popup.setVisibility(View.GONE);
                        mMap.getUiSettings().setZoomGesturesEnabled(false);

                        mMap.getUiSettings().setScrollGesturesEnabled(false);

                        mMap.getUiSettings().setZoomControlsEnabled(false);

                        txtEta.setText("");

                        txtbasefair.setText("");
                        txtMaxPerson.setText("");
                        txtbasefare1.setText("");
                        txtBaseFare3.setText("");
                        txtBaseFare2.setText("");

                        // txtTitlePopUp.setText("Taxi-V");
                        txtTitlePopUp.setText(Utility.CAR_4);

                        mMap.getUiSettings().setScrollGesturesEnabled(false);

                        mPopUpLayout.setVisibility(View.VISIBLE);
                        isVisible = true;

                        new WebServiceAsynk(URL.BASE_URL + URL.TAXI_DETAIL
                                + sharedPreference.getUserId() + URL.TAXI_TYPE
                                + taxiType + URL.LAT + newPosition.latitude
                                + URL.LNG + newPosition.longitude, Home.this,
                                Home.this, false, "").execute();

                        mPopUpLayout.setVisibility(View.VISIBLE);
                        isVisible = true;
                    } else {

                        whatIsSelected = LUXURY;

                        btnHybrid.setBackgroundResource(R.drawable.car_regular);
                        btnSUV.setBackgroundResource(R.drawable.car_suv);
                        btnBlack.setBackgroundResource(R.drawable.car_black_car);
                        btnLuxury
                                .setBackgroundResource(R.drawable.car_luxury_slctd);
                    }
                    btnArmour.setBackgroundResource(R.drawable.car_armour);
                    new WebServiceAsynk(URL.BASE_URL + URL.GET_DRIVER
                            + sharedPreference.getUserId() + URL.LAT
                            + newPosition.latitude + URL.LNG
                            + newPosition.longitude + URL.TAXI_TYPE + taxiType
                            + "&bookingId=0", Home.this, Home.this, false,
                            "first").execute();

                } else {

                    if (taxiType == 4) {

                        hidingPopUpOnDoubleTap();

                    } else {

                        whatIsSelected = LUXURY;


                        txtEta.setText("");

                        txtbasefair.setText("");
                        txtMaxPerson.setText("");
                        txtbasefare1.setText("");
                        txtBaseFare3.setText("");
                        txtBaseFare2.setText("");

                        // txtTitlePopUp.setText("Taxi-V");
                        txtTitlePopUp.setText(Utility.CAR_4);

                        taxiType = 4;

                        new WebServiceAsynk(URL.BASE_URL + URL.TAXI_DETAIL
                                + sharedPreference.getUserId() + URL.TAXI_TYPE
                                + taxiType + URL.LAT + newPosition.latitude
                                + URL.LNG + newPosition.longitude, Home.this,
                                Home.this, false, "").execute();

                        btnHybrid.setBackgroundResource(R.drawable.car_regular);
                        btnSUV.setBackgroundResource(R.drawable.car_suv);
                        btnBlack.setBackgroundResource(R.drawable.car_black_car);
                        btnLuxury
                                .setBackgroundResource(R.drawable.car_luxury_hvr);
                        btnArmour.setBackgroundResource(R.drawable.car_armour);
                    }
                }
            }
        });

        btnArmour.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (!isVisible) {
                    // onSwipe(SimpleGestureFilter.SWIPE_LEFT);

                    // mMap.clear();
                    isFirstTime = true;
                    markerMoving.destroy();
                    taxiType = 5;

                    if (whatIsSelected.equals(ARMOUR)) {

                        btnHybrid.setBackgroundResource(R.drawable.car_regular);
                        btnSUV.setBackgroundResource(R.drawable.car_suv);
                        btnBlack.setBackgroundResource(R.drawable.car_black_car);
                        btnLuxury.setBackgroundResource(R.drawable.car_luxury);
                        btnArmour
                                .setBackgroundResource(R.drawable.car_armour_hvr);

                        popup.setVisibility(View.GONE);
                        mMap.getUiSettings().setZoomGesturesEnabled(false);

                        mMap.getUiSettings().setScrollGesturesEnabled(false);

                        mMap.getUiSettings().setZoomControlsEnabled(false);

                        txtEta.setText("");

                        txtbasefair.setText("");
                        txtMaxPerson.setText("");
                        txtbasefare1.setText("");
                        txtBaseFare3.setText("");
                        txtBaseFare2.setText("");

                        // txtTitlePopUp.setText("Black Car");
                        txtTitlePopUp.setText(Utility.CAR_5);

                        mMap.getUiSettings().setScrollGesturesEnabled(false);

                        mPopUpLayout.setVisibility(View.VISIBLE);
                        isVisible = true;

                        new WebServiceAsynk(URL.BASE_URL + URL.TAXI_DETAIL
                                + sharedPreference.getUserId() + URL.TAXI_TYPE
                                + taxiType + URL.LAT + newPosition.latitude
                                + URL.LNG + newPosition.longitude, Home.this,
                                Home.this, false, "").execute();

                        mPopUpLayout.setVisibility(View.VISIBLE);
                        isVisible = true;
                    } else {

                        whatIsSelected = ARMOUR;

                        btnHybrid.setBackgroundResource(R.drawable.car_regular);
                        btnSUV.setBackgroundResource(R.drawable.car_suv);
                        btnBlack.setBackgroundResource(R.drawable.car_black_car);
                        btnLuxury.setBackgroundResource(R.drawable.car_luxury);
                        btnArmour
                                .setBackgroundResource(R.drawable.car_armour_slctd);

                    }

                    new WebServiceAsynk(URL.BASE_URL + URL.GET_DRIVER
                            + sharedPreference.getUserId() + URL.LAT
                            + newPosition.latitude + URL.LNG
                            + newPosition.longitude + URL.TAXI_TYPE + taxiType
                            + "&bookingId=0", Home.this, Home.this, false,
                            "first").execute();
                } else {

                    if (taxiType == 5) {

                        hidingPopUpOnDoubleTap();

                    } else {
                        whatIsSelected = ARMOUR;
                        taxiType = 5;

                        txtEta.setText("");

                        txtbasefair.setText("");
                        txtMaxPerson.setText("");
                        txtbasefare1.setText("");
                        txtBaseFare3.setText("");
                        txtBaseFare2.setText("");

                        // txtTitlePopUp.setText("Black Car");
                        txtTitlePopUp.setText(Utility.CAR_5);

                        btnHybrid.setBackgroundResource(R.drawable.car_regular);
                        btnSUV.setBackgroundResource(R.drawable.car_suv);
                        btnBlack.setBackgroundResource(R.drawable.car_black_car);
                        btnLuxury.setBackgroundResource(R.drawable.car_luxury);
                        btnArmour
                                .setBackgroundResource(R.drawable.car_armour_hvr);

                        new WebServiceAsynk(URL.BASE_URL + URL.TAXI_DETAIL
                                + sharedPreference.getUserId() + URL.TAXI_TYPE
                                + taxiType + URL.LAT + newPosition.latitude
                                + URL.LNG + newPosition.longitude, Home.this,
                                Home.this, false, "").execute();

                    }

                }

            }
        });

        if (PreferenceManager.getDefaultSharedPreferences(Home.this).getInt(
                "type", 1) == 2) {

            taxiType = 2;

            btnBlack.setBackgroundResource(R.drawable.car_black_car);

            btnHybrid.setBackgroundResource(R.drawable.car_regular);
            btnSUV.setBackgroundResource(R.drawable.car_suv_slctd);
            btnLuxury.setBackgroundResource(R.drawable.car_luxury);
            btnArmour.setBackgroundResource(R.drawable.car_armour);
        } else if (PreferenceManager.getDefaultSharedPreferences(Home.this)
                .getInt("type", 1) == 3) {

            taxiType = 3;

            btnBlack.setBackgroundResource(R.drawable.car_black_car_slctd);

            btnHybrid.setBackgroundResource(R.drawable.car_regular);
            btnSUV.setBackgroundResource(R.drawable.car_suv);
            btnLuxury.setBackgroundResource(R.drawable.car_luxury);
            btnArmour.setBackgroundResource(R.drawable.car_armour);
        } else if (PreferenceManager.getDefaultSharedPreferences(Home.this)
                .getInt("type", 1) == 1) {

            taxiType = 1;

            btnBlack.setBackgroundResource(R.drawable.car_black_car);

            btnHybrid.setBackgroundResource(R.drawable.car_regular_slctd);
            btnSUV.setBackgroundResource(R.drawable.car_suv);
            btnLuxury.setBackgroundResource(R.drawable.car_luxury);
            btnArmour.setBackgroundResource(R.drawable.car_armour);
        } else if (PreferenceManager.getDefaultSharedPreferences(Home.this)
                .getInt("type", 1) == 4) {

            taxiType = 4;

            btnBlack.setBackgroundResource(R.drawable.car_black_car);

            btnHybrid.setBackgroundResource(R.drawable.car_regular);
            btnSUV.setBackgroundResource(R.drawable.car_suv);
            btnLuxury.setBackgroundResource(R.drawable.car_luxury_slctd);
            btnArmour.setBackgroundResource(R.drawable.car_armour);
        } else if (PreferenceManager.getDefaultSharedPreferences(Home.this)
                .getInt("type", 1) == 5) {

            taxiType = 5;

            btnBlack.setBackgroundResource(R.drawable.car_black_car);

            btnHybrid.setBackgroundResource(R.drawable.car_regular);
            btnSUV.setBackgroundResource(R.drawable.car_suv);
            btnLuxury.setBackgroundResource(R.drawable.car_luxury);
            btnArmour.setBackgroundResource(R.drawable.car_armour_slctd);
        }

    }


    private void hidingPopUpOnDoubleTap() {

        // TODO Auto-generated method stub
        mPopUpLayout.setVisibility(View.GONE);
        isVisible = false;

        //popup.setVisibility(View.VISIBLE);

        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (whatIsSelected.equals(HYBRID)) {

            btnHybrid.setBackgroundResource(R.drawable.car_regular_slctd);
            btnSUV.setBackgroundResource(R.drawable.car_suv);
            btnBlack.setBackgroundResource(R.drawable.car_black_car);
            btnLuxury.setBackgroundResource(R.drawable.car_luxury);

            taxiType = 1;
        } else if (whatIsSelected.equals(SUV)) {

            btnHybrid.setBackgroundResource(R.drawable.car_regular);
            btnSUV.setBackgroundResource(R.drawable.car_suv_slctd);
            btnBlack.setBackgroundResource(R.drawable.car_black_car);
            btnLuxury.setBackgroundResource(R.drawable.car_luxury);

            taxiType = 2;
        } else if (whatIsSelected.equals(BLACK)) {

            btnHybrid.setBackgroundResource(R.drawable.car_regular);
            btnSUV.setBackgroundResource(R.drawable.car_suv);
            btnBlack.setBackgroundResource(R.drawable.car_black_car_slctd);
            btnLuxury.setBackgroundResource(R.drawable.car_luxury);

            taxiType = 3;

        } else if (whatIsSelected.equals(LUXURY)) {

            btnHybrid.setBackgroundResource(R.drawable.car_regular);
            btnSUV.setBackgroundResource(R.drawable.car_suv);
            btnBlack.setBackgroundResource(R.drawable.car_black_car);
            btnLuxury.setBackgroundResource(R.drawable.car_luxury_slctd);
            taxiType = 4;

        } else if (whatIsSelected.equals(ARMOUR)) {

            btnHybrid.setBackgroundResource(R.drawable.car_regular);
            btnSUV.setBackgroundResource(R.drawable.car_suv);
            btnBlack.setBackgroundResource(R.drawable.car_black_car);
            btnLuxury.setBackgroundResource(R.drawable.car_luxury);
            btnArmour.setBackgroundResource(R.drawable.car_armour_slctd);
            taxiType = 5;

        }

        new WebServiceAsynk(URL.BASE_URL + URL.GET_DRIVER
                + sharedPreference.getUserId() + URL.LAT + newPosition.latitude
                + URL.LNG + newPosition.longitude + URL.TAXI_TYPE + taxiType
                + "&bookingId=0", Home.this, Home.this, false, "first")
                .execute();

    }

    private void setTutorialViewArrow() {

        tutorialView.setVisibility(View.VISIBLE);
        tutorialView.bringToFront();

        img = (ImageView) findViewById(R.id.img);

		/*
		 * img.setX(0); img.setY(0);
		 * 
		 * img.setX((btnDestinationShow.getX()+(btnDestinationShow.getWidth()/2))
		 * );
		 * img.setY(btnDestinationShow.getY()+btnDestinationShow.getHeight());
		 */

    }


    private void destinationWork() {
        btnDestination1 = (Button) findViewById(R.id.btnDestination1);
        edtDropOffAddress1 = (Button) findViewById(R.id.edtDropOffAddress1);
        layoutDestination = (RelativeLayout) findViewById(R.id.layoutDestination);


        btnDestination1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(Home.this, PickUpLocation.class);
                i.putExtra("via", "ADD DESTINATION");
                startActivityForResult(i, 1);


            }
        });

        edtDropOffAddress1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(Home.this, PickUpLocation.class);
                i.putExtra("via", "ADD DESTINATION");
                startActivityForResult(i, 1);


            }
        });

    }

    private void showDialog(String title, String message) {

        final Dialog dialog = new Dialog(Home.this,
                style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_warning);
        TextView txtTitle = (TextView) dialog
                .findViewById(R.id.dialog_title_text);
        TextView txtMessage = (TextView) dialog
                .findViewById(R.id.dialog_message_text);

        Button btnYes = (Button) dialog.findViewById(R.id.dialog_ok_btn);
        Button btnNo = (Button) dialog.findViewById(R.id.dialog_cancel_btn);

        btnYes.setVisibility(View.GONE);
        btnNo.setVisibility(View.GONE);
        Button btnOk = (Button) dialog.findViewById(R.id.btnDone);
        btnOk.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                dialog.dismiss();

            }
        });

        txtTitle.setText(title);
        txtMessage.setText(message);

        dialog.show();

    }


    public static void AskRuntimePermission(String[] permission, Integer requestCode, Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                permission[0]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity, permission[0])) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{
                                "android.permission.ACCESS_COARSE_LOCATION",
                                "android.permission.ACCESS_FINE_LOCATION"
                        }, requestCode);
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{
                                "android.permission.ACCESS_COARSE_LOCATION",
                                "android.permission.ACCESS_FINE_LOCATION",
                        }, requestCode);
            }
        }
    }

    ArrayList<LocationsName> mAddressList;
    LocationsName mLocationsName;
    PickUpAdapter mAdapter;

    private class PlacesTask extends AsyncTask<String, Void, String> {
        Context context;
        int AddressType = 0;

        public PlacesTask(Context context, int AddressType) {
            this.context = context;
            this.AddressType = AddressType;
        }

        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console
            String key = "key=" + URL.GOOGLEAPIKEY.trim();

            String input = "";

            try {
                input = "input="
                        + URLEncoder.encode(place[0], "utf-8").replace(" ", "")
                        .trim();
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            // place type to be searched
            // place type to be searched
            String types = "types=(establishment|address)";

            //	String types="";
            try {
                types = URLEncoder.encode(types, HTTP.UTF_8);
            } catch (UnsupportedEncodingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            // Sensor enabled
            String sensor = "sensor=true";
            String Location = "location="
                    + UtilsSingleton.getInstance().current_lat + ","
                    + UtilsSingleton.getInstance().current_longi;
            // Building the parameters to the web service
            String parameters = input + "&" + types + "&" + sensor + "&"
                    + Location + "&" + key;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"
                    + output + "?" + parameters;

            if (Utility.intraContinent) {

                url = url + "&components=country:ca";
            }

            try {
                // Fetching the data from web service in background
                data = downloadUrl(url);
                Log.d(TAG, url);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
                //Toast.makeText(getApplicationContext(), "No place found", Toast.LENGTH_SHORT)
                //      .show();

            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject jsonObj;
            try {
                mAddressList = new ArrayList<LocationsName>();
                jsonObj = new JSONObject(result.toString());
                JSONArray predsJsonArray = jsonObj
                        .getJSONArray("predictions");

                for (int i = 0; i < predsJsonArray.length(); i++) {
                    mLocationsName = new LocationsName();
                    mLocationsName.setId(predsJsonArray
                            .getJSONObject(i).getString("place_id"));
                    mLocationsName.setName(predsJsonArray
                            .getJSONObject(i).getString("description"));
                    mLocationsName.setReference(predsJsonArray
                            .getJSONObject(i).getString("reference"));
                    mAddressList.add(mLocationsName);
                }

                UtilsSingleton.getInstance().mRecentSearch = mAddressList;
               /* if (AddressType == 1) {
                    mAdapter = new PickUpAdapter(Home.this,
                            mAddressList, srEditText);

                } else if (AddressType == 2) {
                    mAdapter = new PickUpAdapter(Home.this,
                            mAddressList, dsEditText);

                }*/
                listSerchResult.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public String downloadUrl(String strUrl) {

        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            java.net.URL url = new java.net.URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                iStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            urlConnection.disconnect();
        }
        return data;

    }

    private class RouteTask extends AsyncTask<String, Void, String> {
        Context context;
        int AddressType = 0;

        public RouteTask(Context context, int AddressType) {
            this.context = context;
            this.AddressType = AddressType;
        }

        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";
            Log.d(TAG, "Route task doInBackground");
            // Obtain browser key from https://code.google.com/apis/console
            String key = "&key=" + URL.GOOGLEAPIKEY.trim();

            String sourceID="";
            if (target != null) {
                sourceID = target.latitude + "," + target.longitude;
            }

            String destinationID="";
            if (destPlace.getLatLng() != null) {
                destinationID = destPlace.getLatLng().latitude + "," + destPlace.getLatLng().longitude;
            }

            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + sourceID + "&destination=" + destinationID + "&key=" + URL.GOOGLEAPIKEY.trim() + "&sensor=false&units=metric&mode=driving";

            try {
                // Fetching the data from web service in background
                data = downloadUrl(url);
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
            Log.d(TAG, url+" \n" + data);
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                // hiding keyboard
                hideKeybord();
                //showRideOptionDialog();
                //GetJsonResponse(result);
                /*findFareQuote(1, startPointMarker.getPosition().latitude, startPointMarker.getPosition().latitude, startPointMarker.getPosition().longitude, startPointMarker.getPosition().longitude);
                findFareQuote(2, startPointMarker.getPosition().latitude, startPointMarker.getPosition().latitude, startPointMarker.getPosition().longitude, startPointMarker.getPosition().longitude);
                findFareQuote(3, startPointMarker.getPosition().latitude, startPointMarker.getPosition().latitude, startPointMarker.getPosition().longitude, startPointMarker.getPosition().longitude);
                findFareQuote(4, startPointMarker.getPosition().latitude, startPointMarker.getPosition().latitude, startPointMarker.getPosition().longitude, startPointMarker.getPosition().longitude);
                findFareQuote(5, startPointMarker.getPosition().latitude, startPointMarker.getPosition().latitude, startPointMarker.getPosition().longitude, startPointMarker.getPosition().longitude);
*/
                ParserTask parserTask = new ParserTask();

                // Invokes the thread for parsing the JSON data
                parserTask.execute(result);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void hideKeybord() {
        InputMethodManager imm = (InputMethodManager) Home.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(dsEditText.getWindowToken(), 0);

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
        showCarDetail(R.id.btnHYBRID, 0);
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            if (routeLine != null) {
                routeLine.remove();
            }
            // Traversing through all the routes
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

                endLat = points.get(path.size() - 1).latitude;
                endLNG = points.get(path.size() - 1).longitude;


                startPointMarker = new MarkerOptions();
                startPointMarker.position(points.get(0));
                startPointMarker.flat(true);
                startPointMarker.anchor(0.5f, 0.5f);
                startPointMarker.icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.green_marker));

                endPointMarker = new MarkerOptions();
                endPointMarker.position(points.get(points.size() - 1));
                endPointMarker.flat(true);
                endPointMarker.anchor(0.5f, 0.5f);
                endPointMarker.icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.red_marker));
                mMap.addMarker(startPointMarker);
                mMap.addMarker(endPointMarker);

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.BLACK);

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                routeLine = mMap.addPolyline(lineOptions);
                if (points != null)
                    MapUtil.zoomRoute(mMap, points);
                showRideOptionDialog();
            } else {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.main_rl), "Route not available.", Snackbar.LENGTH_LONG);
                snackbar.show();
                Log.d("onPostExecute", "without Polylines drawn");
                //btnClose.performClick();

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

    // A place has been received; use requestCode to track the request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    destPlace = PlaceAutocomplete.getPlace(this, data);
                    txtDestPage1.setText(destPlace.getAddress());
                    break;
                case 1:
                    sourcePlace = PlaceAutocomplete.getPlace(this, data);
                    srEditText.setText(sourcePlace.getAddress());
                    break;
                case 2:
                    destPlace = PlaceAutocomplete.getPlace(this, data);
                    dsEditText.setText(destPlace.getAddress());
                    break;
            }

            showRoute();//TODO call After 2 second
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(this, data);
            Log.i(TAG, status.getStatusMessage());
        } else if (resultCode == RESULT_CANCELED) {
            Log.i(TAG, "RESULT_CANCELED");
        }
    }

    public void showDrawer(View v) {
      /*  mNav.toggleLeftDrawer();
        mNav.clearFocus();*/
    }


}
