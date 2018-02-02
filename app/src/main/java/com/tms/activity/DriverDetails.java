package com.tms.activity;

import android.Manifest;
import android.R.style;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;
import com.tms.DataParser;
import com.tms.R;
import com.tms.SharingToro;
import com.tms.newui.MainActivity4;
import com.tms.utility.CheckInternetConnectivity;
import com.tms.utility.CircleTransform;
import com.tms.utility.DecimalListener;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author arvind.agarwal this class shows approaching driver on map and also
 *         details of driver user can cancel trip, call driver, sms driver from
 *         this screen
 */

public class DriverDetails extends FragmentActivity implements OnMapReadyCallback,
		OnMarkerClickListener, ResponseListener, ServiceResponce,
		OnClickListener, XmlListener, GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	private static final String TAG = "DriverDetails";
	private Dialog dialogTip;

	Intent intentToReciept;

	BitmapFactory.Options options = new BitmapFactory.Options();
	Bitmap scaledBitmap;
	Bitmap rotatedBitmap;

	String FARE = "";

	Matrix matrix;

	boolean isPopOpen = false;

	private Bitmap regularBmp, suvBmp, blackBmp, luxuryBmp;

	// object for map
	GoogleMap mMap;
	FragmentManager myFragmentManager;
	SupportMapFragment mySupportMapFragment;
	//public static SimpleSideDrawer mNav;
	ToroSharedPrefrnce sharedPreference;

	// marker object for pick up and driver
	Marker myLocationMarker, driverMarker;
	String bookingId;
	Intent mIntent;

	boolean isCheckPopShown = false;

	// object of class that updated latlong on every location change
	MarkerMoving markerMoving;

	// isFirst boolean is used to check whether the driver response is first
	// time or not
	boolean isFirst = false;
	ImageView btnCall, btnSMS, btnCancelTrip;
	Button btnOpenSlider, btnCloseSlider;
	RelativeLayout driverImageLayout;
	TextView txtRating, txtUserName, txtPhone, txtTaxiModel, txtTaxiNo;

	// object of animation class
	Animation mAnimation, closeAnimation;
	ImageView driverImage, carIcon;
	Handler mDriverDeatils;

	int taxiType;

	boolean cancelTrip = false;

	Boolean bool = false;

	Dialog dialog;
	Location myLocation;
	TextView imgHighLighter;
	AlertDialog.Builder builder;
	Dialog dialogNew;

	RelativeLayout relativePopUp, rel_1;

	Button btnCrossNew;
	GoogleApiClient googleApiClient;

	public static Activity driverDetils;

	private Typeface typeFace, typeFaceItalic, typeFaceNeueLight,
			typeFaceHelvetica, typeFace1;
	private Polyline routeLine = null;

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_detail);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		myFragmentManager = getSupportFragmentManager();
		// Getting reference to the SupportMapFragment of activity_main.xml


		SupportMapFragment mapFragment = (SupportMapFragment) myFragmentManager
				.findFragmentById(R.id.mapHome);
		mapFragment.getMapAsync(this);


		findViewById(R.id.relImageName).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

					}
				});

		typeFace = Typeface
				.createFromAsset(getAssets(), Utility.TYPE_FACE_HOME);
		typeFaceHelvetica = Typeface.createFromAsset(getAssets(),
				Utility.TYPE_FACE_HELVETICA);

		typeFaceItalic = Typeface.createFromAsset(getAssets(),
				Utility.TYPE_FACE_FORGOT);
		typeFaceNeueLight = Typeface.createFromAsset(getAssets(),
				Utility.TYPE_FACE_HELVATICA_NEUE_LIGHT);
		typeFace1 = Typeface.createFromAsset(getAssets(), Utility.TYPE_FACE);
		((TextView) findViewById(R.id.txtTag)).setTypeface(typeFace1);
		options.inSampleSize = 1;

		regularBmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.map_pin_hybrid1, options);

		suvBmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.map_pin_suv1, options);
		blackBmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.map_pin_black_car1, options);

		luxuryBmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.map_pin_black_car1, options);

		// bitmap for regular car

		googleApiClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();

		driverDetils = DriverDetails.this;

		/*if (Home.activity != null) {
			Home.activity.finish();
		}*/

		/*if (LoadingRequest.loadingRequest != null) {
			LoadingRequest.loadingRequest.finish();

		}*/

		// checking whether GPS is enabled or not
		final LocationManager manager = (LocationManager) DriverDetails.this
				.getSystemService(Context.LOCATION_SERVICE);
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			// do something

			displayAlert();

		}
		//IntentFilter filter = new IntentFilter(Home.ACTION);
		// this.registerReceiver(broadcastReceiver, filter);
		initialize();

		imgHighLighter = (TextView) findViewById(R.id.imgDriverRoute);
		imgHighLighter.setTypeface(typeFaceItalic);

		imgHighLighter.setVisibility(View.VISIBLE);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				if (DriverDetails.this != null) {
					imgHighLighter.setVisibility(View.INVISIBLE);
				}

			}
		}, 10000);

		mAnimation = AnimationUtils.loadAnimation(DriverDetails.this,
				R.anim.move);
		closeAnimation = AnimationUtils.loadAnimation(DriverDetails.this,
				R.anim.close);
		markerMoving = new MarkerMoving();
		mIntent = getIntent();

		// getting booking id
		bookingId = mIntent.getExtras().getString("bookingId");
		// getting phone number
		txtPhone.setText("4.00"/*mIntent.getExtras().getString("driverPhone")*/);

		// getting driver rating
		txtRating.setText(mIntent.getExtras().getString("rating"));

		txtTaxiModel.setText(mIntent.getExtras().getString("taxiModel"));
		// getting taxi number
		txtTaxiNo.setText(mIntent.getExtras().getString("taxiNumber"));

		// getting driver name
		txtUserName.setText(mIntent.getExtras().getString("driverName"));
		// getting type
		taxiType = Integer.parseInt(mIntent.getExtras().getString("taxiType"));
		if (mIntent.getExtras().getString("driverImage").equalsIgnoreCase("")) {

		} else {

			// setting driver image using picasso
			Picasso.with(DriverDetails.this)
					.load(mIntent.getExtras().getString("driverImage"))
					.resize(90, 90).transform(new CircleTransform()).into(driverImage);

			/*
			 * ImageUtils.getInstance(DriverDetails.this).setImageUrlToView(
			 * mIntent.getExtras().getString("driverImage"), driverImage, null,
			 * 0, false, true, driverImageLayout.getHeight(),
			 * driverImageLayout.getWidth(), false);
			 */
		}

		/*findViewById(R.id.layoutHomeTop).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

					}
				});
*/
		// handling

		mDriverDeatils = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if (msg.what == 1) {
					startActivity(new Intent(DriverDetails.this, MainActivity4.class));
					finish();
				} else if (msg.what == 2) {
					showDialog("Trip cancelled !",
							"You have cancelled the trip.");

				} else if (msg.what == 3) {
					showDialog("Trip cancelled !",
							"Driver has cancelled the trip.");
				}
			}
		};

	}

	/**
	 * showing alert pop up
	 */

	private void displayAlert() {
		dialogNew = new Dialog(DriverDetails.this,
				style.Theme_Translucent_NoTitleBar);
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
		txtMessage.setText("Would you like to enable GPS?");
		dialogNew.setCanceledOnTouchOutside(false);
		dialogNew.show();

	}

	/**
	 * method to initialize views
	 */
	public void initialize() {

		((TextView) findViewById(R.id.txtCompanyName1))
				.setTypeface(typeFaceHelvetica);
		((TextView) findViewById(R.id.txtTaxiNo1))
				.setTypeface(typeFaceHelvetica);

		btnCrossNew = (Button) findViewById(R.id.btnCross_new);

		btnCrossNew.setOnClickListener(this);
		relativePopUp = (RelativeLayout) findViewById(R.id.popUp);
		relativePopUp.setOnClickListener(this);
		rel_1 = (RelativeLayout) findViewById(R.id.rel_1);
		rel_1.setOnClickListener(this);

		sharedPreference = new ToroSharedPrefrnce(DriverDetails.this);
		btnOpenSlider = (Button) findViewById(R.id.btnDriverOption);
		btnOpenSlider.setTypeface(typeFaceNeueLight);
		btnOpenSlider.setOnClickListener(this);

		btnCall = (ImageView) findViewById(R.id.btnCallDriver);
		//btnCall.setTypeface(typeFaceHelvetica);
		btnCall.setOnClickListener(this);
		btnCancelTrip = (ImageView) findViewById(R.id.btnCancelTrip);
		//btnCancelTrip.setTypeface(typeFaceHelvetica);
		btnCancelTrip.setOnClickListener(this);
		btnSMS = (ImageView) findViewById(R.id.btnSMSDriver);
		//btnSMS.setTypeface(typeFaceHelvetica);
		btnSMS.setOnClickListener(this);
		txtRating = (TextView) findViewById(R.id.txtRating);
		txtUserName = (TextView) findViewById(R.id.txtDriverName);
		txtUserName.setTypeface(typeFaceNeueLight);
		txtPhone = (TextView) findViewById(R.id.txtDriverPhone);
		txtPhone.setTypeface(typeFaceNeueLight);
		txtRating = (TextView) findViewById(R.id.txtRating);
		txtTaxiModel = (TextView) findViewById(R.id.txtCompanyName);
		txtTaxiModel.setTypeface(typeFaceHelvetica);
		txtTaxiNo = (TextView) findViewById(R.id.txtTaxiNo);
		txtTaxiNo.setTypeface(typeFaceHelvetica);
		driverImage = (ImageView) findViewById(R.id.imgDriverPic);
		driverImageLayout = (RelativeLayout) findViewById(R.id.driverImageThumb);

		int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getBaseContext());
		if (status != ConnectionResult.SUCCESS) { // Google Play Services are
			// not available

			int requestCode = 10;
			Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(DriverDetails.this, status,
					requestCode);
			dialog.show();

		} else {
			myFragmentManager = getSupportFragmentManager();
			// Getting reference to the SupportMapFragment of activity_main.xml
			mySupportMapFragment = (SupportMapFragment) myFragmentManager
					.findFragmentById(R.id.mapHome);


			ImageView btnMyLocation = (ImageView) findViewById(R.id.btnMyLocationNew);
			btnMyLocation.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					myLocation = mMap.getMyLocation();

					if (myLocation != null) {

						LatLng target = new LatLng(myLocation.getLatitude(),
								myLocation.getLongitude());
						/*
						 * CameraPosition position = mMap.getCameraPosition();
						 */
						CameraPosition.Builder builder = new CameraPosition.Builder();
						builder.zoom(17);
						builder.target(target);

						mMap.animateCamera(CameraUpdateFactory
								.newCameraPosition(builder.build()));

					}
				}
			});


		/*	mNav = new SimpleSideDrawer(this);
			mNav.setLeftBehindContentView(R.layout.activity_behind_left_simple);
			mNav.setDrawingCacheEnabled(true);

			//((TextView) findViewById(R.id.txtTag)).setTypeface(typeFace1);
			findViewById(R.id.btnDrawer).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {

							mNav.toggleLeftDrawer();

						}
					});
*/
			findViewById(R.id.btnMyProfile).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							startActivity(new Intent(DriverDetails.this,
									com.tms.activity.MyProfile.class));
						}
					});
			//((Button) findViewById(R.id.btnMyProfile)).setTypeface(typeFace);
			findViewById(R.id.btnCreditcard).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							startActivity(new Intent(DriverDetails.this,
									CreditCardListing.class));
						}
					});
			//((Button) findViewById(R.id.btnCreditcard)).setTypeface(typeFace);

			if (Utility.isPaymentGatewayOn) {

				findViewById(R.id.btnCreditcard)
						.setVisibility(View.VISIBLE);
				//((View) findViewById(R.id.dividerCreditcard)).setVisibility(View.VISIBLE);
			} else {
				findViewById(R.id.btnCreditcard)
						.setVisibility(View.GONE);
				//((View) findViewById(R.id.dividerCreditcard)).setVisibility(View.GONE);
			}
			findViewById(R.id.btnPromoCode).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							startActivity(new Intent(DriverDetails.this,
									PromoCodeListing.class).putExtra("what", 1));
						}
					});
			//((Button) findViewById(R.id.btnPromoCode)).setTypeface(typeFace);
			findViewById(R.id.btnReferFrnd).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							startActivity(new Intent(DriverDetails.this,
									SharingToro.class));

						}
					});
			//((Button) findViewById(R.id.btnReferFrnd)).setTypeface(typeFace);
			findViewById(R.id.btnAboutUs).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							startActivity(new Intent(DriverDetails.this,
									AboutUs.class));
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

							startActivity(new Intent(DriverDetails.this,
									Reservation.class));

						}
					});
			//((Button) findViewById(R.id.btnReservation)).setTypeface(typeFace);

			if (!Utility.isReservationAvailable) {

				findViewById(R.id.btnReservation).setVisibility(View.GONE);
				//findViewById(R.id.reservationDivider).setVisibility(View.GONE);

			}

			findViewById(R.id.btnBack).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {

					/*		mNav.close();
							mNav.clearFocus();
*/
							/* edtPickUpAddress.bringToFront(); */

							// onPause();
						}
					});
			//((Button) findViewById(R.id.btnBack)).setTypeface(typeFace);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "onResume");

		// focusing camera on lat long of the place for where cab is called
		CameraPosition.Builder builder = new CameraPosition.Builder();

		builder.target(new LatLng(Double.parseDouble(getIntent().getExtras()
				.getString("LAT")), Double.parseDouble(getIntent().getExtras()
				.getString("LONG"))));
		builder.zoom(17.0f);
		try {
		/*	mMap.animateCamera(CameraUpdateFactory.newCameraPosition(builder
					.build()));
		*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		isFirst = true;
		bool = false;
		// isCheckPopShown=false;

		// webservices hit to get driver information and its location for first
		// time when user
		if (CheckInternetConnectivity
				.checkinternetconnection(DriverDetails.this)) {
			new WebServiceAsynk(URL.BASE_URL + URL.GET_DRIVER
					+ sharedPreference.getUserId() + URL.LAT
					+ UtilsSingleton.getInstance().getCurrent_lat() + URL.LNG
					+ UtilsSingleton.getInstance().getCurrent_lat()
					+ URL.TAXI_TYPE + taxiType + "&bookingId=" + bookingId,
					DriverDetails.this, DriverDetails.this, true, "first")
					.execute();

		} else {
			Toast.makeText(DriverDetails.this, "No Internet", Toast.LENGTH_LONG)
					.show();
		}

	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ontimecab.utility.ResponseListener#response(java.lang.String,
	 * java.lang.String)
	 * 
	 * 
	 * method of an interface to handle web services response
	 */

	@Override
	public void response(String strresponse, String webservice) {
		// TODO Auto-generated method stub'
		// {"getDriversById":"-3","message":"In-valid Booking"}

		Log.i("", strresponse);
		if (strresponse.contains("In-valid Booking")) {
			if (cancelTrip) {
				showDialog("Trip cancelled !", "You have cancelled the trip.");
			} else {
				showDialog("Alert !", "Driver has cancelled the trip.");
			}

		} else {

			Log.i("response", strresponse);
			try {
				JSONObject mJsonObject = new JSONObject(strresponse);


				String getDriverById = mJsonObject.getString("getDriversById");
				if (getDriverById.equals("-3")) {


					JSONObject mJsonObject2 = mJsonObject
							.getJSONObject("getDriversById");

					if (mJsonObject2.getString("cancelStatus").equals("1")) {
						bool = true;


							/*cancellationTime
							(7:59 PM) kapil.bansal: paymentChargeStatus
							(7:59 PM) kapil.bansal: cancelFare*/
						if (!mJsonObject2.getString("declinePaymentStatus").equals("0")) {

							UtilsSingleton.getInstance().setBookingId(bookingId);
							UtilsSingleton.getInstance().setPaymentStatus(
									mJsonObject2.getString("declinePaymentStatus"));
							// sharedPreference.setBookingId(bookingId);
							// sharedPreference.setPaymentStatus(mJsonObject2.getString("paymentStatus"));
							startActivity(new Intent(DriverDetails.this,
									ChangeCardType.class).putExtra("pending", "1"));

							finish();

						} else if (mJsonObject2.getString("paymentChargeStatus").equals("1")) {
							String date = mJsonObject2.getString("cancellationTime");
							String fare = mJsonObject2.getString("cancelFare");
							String payVia = mJsonObject2.getString("payvia");

							startActivity(new Intent(DriverDetails.this,
									CancelCaseReciept.class).putExtra(
									Utility.PAYMENT_DECLINE_DATE_KEY, date)
									.putExtra(Utility.PAYMENT_DECLINE_FARE_KEY,
											fare).putExtra(
											Utility.PAYMENT_DECLINE_PAYVIA,
											payVia));

							finish();
						} else {


							showDialog("Alert!", mJsonObject.getString("message"));
						}

					} else {


						showDialog("Trip cancelled !",
								"Driver has cancelled the trip.");
					}


					bool = true;

				} else {


					JSONObject mJsonObject2 = mJsonObject
							.getJSONObject("getDriversById");


					Double lat = mJsonObject2.getDouble("driverLat");
					Double lng = mJsonObject2.getDouble("driverLong");
					if (driverMarker == null) {
						driverMarker = mMap.addMarker(new MarkerOptions()
								.position(new LatLng(lat, lng)));
						showRoute();
					} else {
						driverMarker.setPosition(new LatLng(lat, lng));
						showRoute();
					}

					matrix = new Matrix();

					matrix.postRotate(Float.parseFloat(mJsonObject2
							.getString("bearingDegree")) - 90);
					scaledBitmap = null;

					taxiType = Integer.parseInt(mJsonObject2.getString("taxiType"));
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

				/*
				 * File strMyImagePath = new File(
				 * Environment.getExternalStorageDirectory(), "photo.png");
				 * FileOutputStream fos = null; try { fos = new
				 * FileOutputStream(strMyImagePath);
				 * scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
				 *
				 * fos.flush(); fos.close(); //
				 * MediaStore.Images.Media.insertImage(getContentResolver(), //
				 * b, // "Screen", "screen"); } catch (FileNotFoundException e)
				 * { e.printStackTrace(); } catch (Exception e) {
				 * e.printStackTrace(); }
				 *
				 * try { rotatedBitmap = DecorderImage .getInstance(this)
				 * .roatedBitmap( Uri.parse(strMyImagePath.getPath()),
				 * scaledBitmap.getWidth(), scaledBitmap.getHeight(),
				 * Integer.parseInt(mJsonObject2 .getString("bearingDegree")) -
				 * 90); } catch (IOException e) { // TODO Auto-generated catch
				 * block e.printStackTrace(); }
				 */
					try {
						if (scaledBitmap != null) {
							rotatedBitmap = null;
							rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
									scaledBitmap.getWidth(), scaledBitmap.getHeight(),
									matrix, true);
							driverMarker.setIcon(BitmapDescriptorFactory
									.fromBitmap(rotatedBitmap));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (isFirst == true) {
						markerMoving.reScheduleTimer(
								DriverDetails.this,
								URL.BASE_URL
										+ URL.GET_DRIVER
										+ sharedPreference.getUserId()
										+ URL.LAT
										+ UtilsSingleton.getInstance()
										.getCurrent_lat()
										+ URL.LNG
										+ UtilsSingleton.getInstance()
										.getCurrent_lat() + URL.TAXI_TYPE
										+ taxiType + "&bookingId=" + bookingId,
								DriverDetails.this);
					}
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void showRoute() {
		try {
			String destinationID = "";
			String sourceID = "";

			double firstLat = driverMarker.getPosition().latitude;
			double firstLong = driverMarker.getPosition().longitude;
			if (firstLat != 0 && firstLong != 0) {
				sourceID = firstLat + "," + firstLong;
			}
			double secondLat = myLocationMarker.getPosition().latitude;
			double secondLong = myLocationMarker.getPosition().longitude;
			if (secondLat != 0 && secondLat != 0) {
				destinationID = secondLat + "," + secondLong;
			}


			String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + sourceID + "&destination=" + destinationID + "&key=" + URL.GOOGLEAPIKEY.trim() + "&sensor=false&units=metric&mode=driving";

			RouteTask routeTask = new RouteTask(DriverDetails.this, 1);
			routeTask.execute(url);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub

		markerMoving.destroy();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		markerMoving.destroy();
		super.onDestroy();
		if (scaledBitmap != null) {
			scaledBitmap.recycle();

		}

		if (rotatedBitmap != null) {

			rotatedBitmap.recycle();
		}

		blackBmp.recycle();
		regularBmp.recycle();
		suvBmp.recycle();
		luxuryBmp.recycle();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ontimecab.utility.ServiceResponce#onResp(java.lang.String)
	 * 
	 * handles continously coming response from webservices
	 */

	@Override
	public void onResp(String responce) {
		// TODO Auto-generated method stub\

		// {"getDriversById":{"id":"16","driverLat":"30.722584","driverLong":"76.846474","taxiType":"1","bearingDegree":"358","bearingWR":"N","tripStatus":[{"tripAmount":"5.00","completedDate":"10 February 2014"}],"cancelStatus":"0","time":"3 days 9 hours","ETA":4866,"paymentStatus":0},"message":"Driver Latitude & Longitude"}
		// {"getDriversById":{"id":"1","driverLat":"30.7238048","driverLong":"76.8464153","taxiType":"4","bearingDegree":"201","bearingWR":"SSW","tripStatus":[],"cancelStatus":"0","time":"0 min","ETA":0,"paymentStatus":0},"message":"Driver Latitude & Longitude","newdriverLat":"30.729958","newdriverLong":"76.842402","curTripStatus":"1","bookingType":"2"}
		Log.i("message", "" + responce);

		if (bool == false) {

			try {
				JSONObject mJsonObject = new JSONObject(responce);
				/*
				 * JSONObject mJsonObject2 = mJsonObject
				 * .getJSONObject("getDriversById");
				 */

				String getDriverById = mJsonObject.getString("getDriversById");

				if (getDriverById.equalsIgnoreCase("-3")) {
					if (driverMarker != null) {
						driverMarker.remove();

						JSONObject mJsonObject2 = mJsonObject
								.getJSONObject("getDriversById");

						if (mJsonObject2.getString("cancelStatus").equals("1")) {
							bool = true;

							/*
							 * cancellationTime (7:59 PM) kapil.bansal:
							 * paymentChargeStatus (7:59 PM) kapil.bansal:
							 * cancelFare
							 */
							if (!mJsonObject2.getString("declinePaymentStatus")
									.equals("0")) {

								UtilsSingleton.getInstance().setBookingId(
										bookingId);
								UtilsSingleton
										.getInstance()
										.setPaymentStatus(
												mJsonObject2
														.getString("declinePaymentStatus"));
								// sharedPreference.setBookingId(bookingId);
								// sharedPreference.setPaymentStatus(mJsonObject2.getString("paymentStatus"));
								startActivity(new Intent(DriverDetails.this,
										ChangeCardType.class).putExtra(
										"pending", "1"));

								finish();

							} else if (mJsonObject2.getString("paymentChargeStatus")
									.equals("1")) {
								String date = mJsonObject2
										.getString("cancellationTime");
								String fare = mJsonObject2
										.getString("cancelFare");

								String payVia = mJsonObject2
										.getString("payvia");

								startActivity(new Intent(DriverDetails.this,
										CancelCaseReciept.class)
										.putExtra(
												Utility.PAYMENT_DECLINE_DATE_KEY,
												date)
										.putExtra(
												Utility.PAYMENT_DECLINE_FARE_KEY,
												fare).putExtra(
												Utility.PAYMENT_DECLINE_PAYVIA,
												payVia));

								finish();
							} else {

								showDialog("Alert!",
										mJsonObject.getString("message"));
							}

						} else {

							showDialog("Trip cancelled !",
									"Driver has cancelled the trip.");
						}

						bool = true;

						/*
						 * Toast.makeText(DriverDetails.this,
						 * "testing driver reject your request", 3).show();
						 */

					}
				} else {
					JSONObject mJsonObject2 = mJsonObject
							.getJSONObject("getDriversById");
					Double lat = mJsonObject2.getDouble("driverLat");
					Double lng = mJsonObject2.getDouble("driverLong");
					if (driverMarker == null) {
						driverMarker = mMap.addMarker(new MarkerOptions()
								.position(new LatLng(lat, lng)));
					} else {
						driverMarker.setPosition(new LatLng(lat, lng));
						showRoute();
					}

					if (mJsonObject.getString("curTripStatus").equals("1")
							&& !isCheckPopShown) {

						/*runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								showCheckDialog();

							}
						});*/

						//isCheckPopShown = true;

					}

					matrix = new Matrix();

					matrix.postRotate(Float.parseFloat(mJsonObject2
							.getString("bearingDegree")) - 90);

					int taxiType = Integer.parseInt(mJsonObject2
							.getString("taxiType"));

					// setting carc icon to show marker on map

					if (taxiType == 1) {

						scaledBitmap = regularBmp;

					} else if (taxiType == 2) {
						scaledBitmap = suvBmp;
					} else if (taxiType == 3) {
						scaledBitmap = blackBmp;
					} else if (taxiType == 4) {
						scaledBitmap = luxuryBmp;
					}

					if (scaledBitmap != null) {

						// setting marker on map
						rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
								scaledBitmap.getWidth(),
								scaledBitmap.getHeight(), matrix, true);
						driverMarker.setIcon(BitmapDescriptorFactory
								.fromBitmap(rotatedBitmap));
					}

					/* if payment is made it will take to rating screen */

					JSONArray mJsonArray = mJsonObject2
							.getJSONArray("tripStatus");

					if (!mJsonObject2.getString("paymentStatus").equals("0")) {

						UtilsSingleton.getInstance().setBookingId(bookingId);
						UtilsSingleton.getInstance().setPaymentStatus(
								mJsonObject2.getString("paymentStatus"));
						// sharedPreference.setBookingId(bookingId);
						// sharedPreference.setPaymentStatus(mJsonObject2.getString("paymentStatus"));
						startActivity(new Intent(DriverDetails.this,
								ChangeCardType.class).putExtra("pending", "1"));

						finish();

					} else if (mJsonArray.length() > 0) {
						JSONObject mObject = mJsonArray.getJSONObject(0);

						intentToReciept = (new Intent(DriverDetails.this,
								Receipt.class)
								.putExtra("tripAmount",
										mObject.getString("tripAmount"))
								.putExtra("completedDate",
										mObject.getString("completedDate"))

								// RX
								// 400","taxiNumber":"Pankajzzzzz","taxiType":"1"}]
								.putExtra("driverName",
										mObject.getString("driverName"))
								.putExtra("driverPhone",
										mObject.getString("driverPhone"))
								.putExtra("taxiModel",
										mObject.getString("taxiModel"))
								.putExtra("taxiNumber",
										mObject.getString("taxiNumber"))
								.putExtra("taxiType",
										mObject.getString("taxiType"))
								.putExtra("driverImage",
										mObject.getString("driverImage"))
								.putExtra("rating", mObject.getString("rating"))
								.putExtra("payVia", mObject.getString("payvia"))
								/*
								 * .putExtra("taxiType",
								 * mJsonObject2.getString("taxiType"))
								 */
								/*
								 * .putExtra("bookingId", bookingId).putExtra(
								 * "id",
								 * mJsonObject2.getString("id")).putExtra("payvia"
								 * , mObject.getString("driverImage"))
								 * .putExtra("rating",
								 * mObject.getString("payvia")));
								 */

								.putExtra("bookingId", bookingId)
								.putExtra("id", mJsonObject2.getString("id"))
								.putExtra("payvia",
										mObject.getString("driverImage")));
						// .putExtra("rating", mObject.getString("payvia")));

						if (Utility.isTipON) {

							if (mObject.getString("tipStatus").equals("1")) {

								/*
								 * if(mObject.getString("bookingType").equals("2"
								 * )){
								 *
								 * startActivity(intentToReciept);
								 * DriverDetails.this.finish(); bool = true;
								 *
								 * }else{
								 */

								FARE = mObject.getString("tripAmount")
										.replaceAll(",", "");
								showTipDialog(mObject.getString("driverId"),
										bookingId);
								bool = true;

								// }

							} else {

								startActivity(intentToReciept);

								DriverDetails.this.finish();
								bool = true;
							}

						} else {

							// directing app flow to receipt screen
							startActivity(intentToReciept);

							DriverDetails.this.finish();
							bool = true;
						}
						/*
						 * if (mObject.getString("tipStatus").equals("1")) {
						 * 
						 * FARE = mObject.getString("tripAmount");
						 * showTipDialog(mObject.getString("driverId"),
						 * bookingId);
						 * 
						 * } else {
						 * 
						 * startActivity(intentToReciept);
						 * 
						 * DriverDetails.this.finish(); // }
						 * 
						 * /* sharedPreference.setBookingId("0");
						 * sharedPreference.setPaymentStatus
						 * (mJsonObject2.getString("paymentStatus"));
						 */

					}

					if (mJsonObject2.getString("cancelStatus").equals("1")) {
						bool = true;

						/*
						 * cancellationTime (7:59 PM) kapil.bansal:
						 * paymentChargeStatus (7:59 PM) kapil.bansal:
						 * cancelFare
						 */

						if (!mJsonObject2.getString("declinePaymentStatus")
								.equals("0")) {

							UtilsSingleton.getInstance()
									.setBookingId(bookingId);
							UtilsSingleton.getInstance().setPaymentStatus(
									mJsonObject2
											.getString("declinePaymentStatus"));
							// sharedPreference.setBookingId(bookingId);
							// sharedPreference.setPaymentStatus(mJsonObject2.getString("paymentStatus"));
							startActivity(new Intent(DriverDetails.this,
									ChangeCardType.class).putExtra("pending",
									"1"));

							finish();

						} else if (mJsonObject2.getString("paymentChargeStatus")
								.equals("1")) {
							String date = mJsonObject2
									.getString("cancellationTime");
							String fare = mJsonObject2.getString("cancelFare");
							String payVia = mJsonObject2
									.getString("payvia");


							startActivity(new Intent(DriverDetails.this,
									CancelCaseReciept.class).putExtra(
									Utility.PAYMENT_DECLINE_DATE_KEY, date)
									.putExtra(Utility.PAYMENT_DECLINE_FARE_KEY,
											fare).putExtra(
											Utility.PAYMENT_DECLINE_PAYVIA,
											payVia));

							finish();
						} else {

							if (cancelTrip) {
								showDialog("Trip cancelled !",
										"You have cancelled the trip.");

							} else {
								showDialog("Alert!",
										mJsonObject.getString("message"));
							}
						}

					}

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.btnDriverOption:

				// toggle more info

				if (isPopOpen) {
					btnOpenSlider.setText("MORE\nINFO");
					relativePopUp.setVisibility(View.GONE);
				} else {
					btnOpenSlider.setText("LESS\nINFO");
					relativePopUp.setVisibility(View.VISIBLE);
				}

				isPopOpen = !isPopOpen;
				// driverOptionLayout.startAnimation(mAnimation);

				break;

			case R.id.btnCross_new:

				break;

			case R.id.btnCancelTrip:
				// bool = true;

				relativePopUp.setVisibility(View.GONE);
				btnOpenSlider.setText("MORE\nINFO");

				showDialog1("Alert!",
						"Are you sure you want to cancel the booking?");
				break;
			case R.id.btnSMSDriver:

				// closing option screen
				relativePopUp.setVisibility(View.GONE);
				btnOpenSlider.setText("MORE\nINFO");

				isPopOpen = !isPopOpen;

				// intrinsic intent to send sms
				Intent sendSMS = new Intent(Intent.ACTION_SENDTO,
						Uri.parse("smsto:" + txtPhone.getText().toString().trim()));
				startActivity(sendSMS);
				break;
			case R.id.btnCallDriver:

				// closing option screen
				relativePopUp.setVisibility(View.GONE);
				btnOpenSlider.setText("MORE\nINFO");
				isPopOpen = !isPopOpen;

				// intent to call driver
				String uri = "tel:" + txtPhone.getText().toString().trim();
				Intent intent = new Intent(Intent.ACTION_CALL);
				//intent.setPackage("com.android.phone");
				intent.setData(Uri.parse(uri));
				checkCallPermission();
				startActivity(intent);
			break;

		case R.id.popUp:

			// hide option screen
			relativePopUp.setVisibility(View.GONE);
			btnOpenSlider.setText("MORE\nINFO");
			isPopOpen = !isPopOpen;

			break;

		case R.id.rel_1:
			break;
		default:
			break;
		}

	}

	public boolean checkCallPermission() {
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.CALL_PHONE)
				!= PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.CALL_PHONE},
					10);
			//}
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 10) {
			if (grantResults.length > 0
					&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

			} else {
				Snackbar snackbar = Snackbar
						.make(findViewById(R.id.cordinatorLayout),"Permission not granted", Snackbar.LENGTH_LONG)
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
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ontimecab.utility.XmlListener#onResponse(java.lang.String)
	 * 
	 * to handle response from server
	 */
	@Override
	public void onResponse(String respose) {
		// TODO Auto-generated method stub

		// cancel booking response
        Log.e("Driver Details","cancelBookingReq respose "  + respose);
		if (respose.contains("cancelBookingReq")) {
			try {
				final JSONObject mJsonObject = new JSONObject(respose);
				if (mJsonObject.getString("cancelBookingReq").equalsIgnoreCase(
						"1")) {
					/*
					 * if (bool) { showDialog("Trip cancelled !",
					 * "You have cancelled the trip."); } else {
					 * showDialog("Trip cancelled !",
					 * "Driver has cancelled the trip.");
					 * 
					 * }
					 */

					/*
					 * {"cancelBookingReq":"1","message":
					 * "Booking has been cancelled successfully."
					 * ,"payvia":"1","cancellationFare"
					 * :"25","paymentStatus":"1"} (7:23 PM) mandeep.singh:
					 * cancellationTime
					 */
					// showDialog("Job cancelled !",
					// "You have cancelled the job.");
                    // below commented 03/06/16
					if(!mJsonObject.getString("declinePaymentStatus").equals("0")){

						UtilsSingleton.getInstance().setBookingId(bookingId);
						UtilsSingleton.getInstance().setPaymentStatus(
								mJsonObject.getString("declinePaymentStatus"));
						// sharedPreference.setBookingId(bookingId);
						// sharedPreference.setPaymentStatus(mJsonObject2.getString("paymentStatus"));
						startActivity(new Intent(DriverDetails.this,
								ChangeCardType.class).putExtra("pending", "1"));

						finish();

					}else


					if (mJsonObject.getString("paymentStatus").equals("1")) {

						String date = mJsonObject.getString("cancellationTime");
						String fare = mJsonObject.getString("cancellationFare");
						String payVia=mJsonObject.getString("payvia");


						startActivity(new Intent(DriverDetails.this,
								CancelCaseReciept.class).putExtra(
								Utility.PAYMENT_DECLINE_DATE_KEY, date)
								.putExtra(Utility.PAYMENT_DECLINE_FARE_KEY,
										fare).putExtra(
												Utility.PAYMENT_DECLINE_PAYVIA,
												payVia));

						finish();

					}else{
						showDialog("Trip cancelled !",
								"You have cancelled the trip.");

					}

					bool = true;

					// showDialog("Message", "Booking cancelled");

				} else if (mJsonObject.getString("cancelBookingReq")
						.equalsIgnoreCase("-1")) {
					Toast.makeText(DriverDetails.this,
							mJsonObject.getString("message"),
							Toast.LENGTH_SHORT).show();
				} else if (mJsonObject.getString("cancelBookingReq")
						.equalsIgnoreCase("-2")) {
					Toast.makeText(DriverDetails.this,
							mJsonObject.getString("message"),
							Toast.LENGTH_SHORT).show();
				} else if (mJsonObject.getString("cancelBookingReq")
						.equalsIgnoreCase("-4")) {
					Toast.makeText(DriverDetails.this,
							mJsonObject.getString("message"),
							Toast.LENGTH_SHORT).show();
					bool = false;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (respose.contains("updateTripPayment")) {// update trip
															// payment when tip
															// is added

			try {
				JSONObject jsonObj = new JSONObject(respose);

				if (intentToReciept != null) {
					Toast.makeText(DriverDetails.this,
							jsonObj.getString("message"), Toast.LENGTH_SHORT)
							.show();
					startActivity(intentToReciept.putExtra("tripAmount",
							jsonObj.getString("updateTripPayment")));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// {"updateTripPayment":"4.00","message":"Tip updated sucessfully."}

		}
	}

	@Override
	public void onBackPressed() {

		if (dialog != null) {
			dialog.setCancelable(false);
		}
	}

	/*
	 * this method is called to alert message
	 * 
	 * it contains positive and negative button
	 */

	private void showDialog(String title, String message) {
		if (DriverDetails.this != null) {

			dialog = new Dialog(DriverDetails.this,
					style.Theme_Translucent_NoTitleBar);
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
			btnOk.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					dialog.dismiss();

					finish();
					startActivity(new Intent(DriverDetails.this, MainActivity4.class));

				}
			});

			dialog.show();

		}

	}

	/*
	 * '(non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onLowMemory()
	 */
	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub

		// intimating JVM to release unused object to release memory as map and
		// markers has consumed much memory
		System.gc();

	}

	/*
	 * broadcast for GPS
	 */

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Home.ACTION.equals(action)) {
				final LocationManager manager = (LocationManager) context
						.getSystemService(Context.LOCATION_SERVICE);
				if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					// do something
				} else {

					displayAlert();
				}
				// do something else
			}
		}
	};

	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);

		if (arg0 == 786) {
			final LocationManager manager = (LocationManager) DriverDetails.this
					.getSystemService(Context.LOCATION_SERVICE);
			if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				// do something
				displayAlert();
			} else {

				dialogNew.dismiss();
				myLocation = mMap.getMyLocation();

				if (myLocation != null) {

					LatLng target = new LatLng(myLocation.getLatitude(),
							myLocation.getLongitude());
					/*
					 * CameraPosition position = mMap.getCameraPosition();
					 */
					CameraPosition.Builder builder = new CameraPosition.Builder();
					builder.zoom(17);
					builder.target(target);

					mMap.animateCamera(CameraUpdateFactory
							.newCameraPosition(builder.build()));

				}
			}
		}

	};

	@Override
	protected void onStop() {

		System.gc();

		// unregisterReceiver(broadcastReceiver);
		super.onStop();
		// setting boolean bool to true so not handle view any more and this
		// prevents from crashes

		bool = true;

	}

	/*
	 * this method is used to show alert messages
	 */

	private void showDialog1(String title, String message) {

		if (DriverDetails.this != null) {
			dialog = new Dialog(DriverDetails.this,
					style.Theme_Translucent_NoTitleBar);
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
			btnYes.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					isPopOpen = !isPopOpen;
					cancelTrip = true;
					String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><cancelBookingReq><bookingId><![CDATA["
							+ bookingId + "]]></bookingId></cancelBookingReq>";
					new SendXmlAsync(URL.BASE_URL + URL.CANCEL_REQUEST, xml,
							DriverDetails.this, DriverDetails.this, true)
							.execute();

					dialog.dismiss();
				}
			});
			btnNo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});

			txtTitle.setText(title);
			txtMessage.setText(message);

			dialog.show();

		}

	}

	/*
	 * this method is called to show tip dialog
	 */

	private void showTipDialog(final String driverId, final String bookingId) {

		String tipToSend = "0";

		FARE = FARE.replace("$", "");
		dialogTip = new Dialog(DriverDetails.this,
				style.Theme_Translucent_NoTitleBar);
		dialogTip.setContentView(R.layout.dialog_tip);
		dialogTip.setCancelable(false);
		dialogTip.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		Button btnSubmit = (Button) dialogTip.findViewById(R.id.btnSubmit);
		Button btnCancel = (Button) dialogTip.findViewById(R.id.btnCancel);
		TextView txtTotalAmount = (TextView) dialogTip
				.findViewById(R.id.txtTotalAmount);

		final RadioButton rb_main = (RadioButton) dialogTip
				.findViewById(R.id.rb_main);
		final RadioGroup rg = (RadioGroup) dialogTip
				.findViewById(R.id.radioGroup);
		final RadioButton radioButton1 = (RadioButton) dialogTip
				.findViewById(R.id.rb1);
		final RadioButton radioButton2 = (RadioButton) dialogTip
				.findViewById(R.id.rb2);
		final RadioButton radioButton3 = (RadioButton) dialogTip
				.findViewById(R.id.rb3);
		final EditText edtTip = (EditText) dialogTip.findViewById(R.id.edtTip);
		final TextView textFareTip = (TextView) dialogTip
				.findViewById(R.id.txtTip);
		txtTotalAmount.setText("Trip fare                $" + FARE);

		textFareTip.setText(String.valueOf(String.format("%.2f",
				0.2 * Float.parseFloat(FARE))));

		edtTip.setEnabled(false);

		edtTip.setKeyListener(new DecimalListener());

		rb_main.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub

				if (isChecked) {
					edtTip.setEnabled(true);

					radioButton1.setChecked(false);
					radioButton2.setChecked(false);
					radioButton3.setChecked(false);

					edtTip.setText("");
					edtTip.requestFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(edtTip, InputMethodManager.SHOW_IMPLICIT);

				}

			}
		});

		radioButton1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub

				if (isChecked) {

					edtTip.setText("");

					rb_main.setChecked(false);
					textFareTip.setText(String.valueOf(String.format("%.2f",
							0.1 * Float.parseFloat(FARE))));

				}

			}
		});

		radioButton2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub

				if (isChecked) {
					edtTip.setText("");
					rb_main.setChecked(false);
					textFareTip.setText(String.valueOf(String.format("%.2f",
							0.2 * Float.parseFloat(FARE))));

				}

			}
		});
		radioButton3.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub

				if (isChecked) {
					edtTip.setText("");
					rb_main.setChecked(false);
					textFareTip.setText(String.valueOf(String.format("%.2f",
							0.3 * Float.parseFloat(FARE))));

				}

			}
		});
		edtTip.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

				textFareTip.setText(s);

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (textFareTip.getText().toString().trim().equals("")
						|| textFareTip.getText().toString().trim().equals(".")) {

					Toast.makeText(DriverDetails.this,
							"Please enter valid amount.", Toast.LENGTH_LONG)
							.show();

				} else {

					// submiting tip

					String xmlDriverTip = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><updateTripPayment><driverId><![CDATA["
							+ driverId
							+ "]]></driverId><riderId><![CDATA["
							+ new ToroSharedPrefrnce(DriverDetails.this)
									.getUserId()
							+ "]]></riderId><bookingId><![CDATA["
							+ bookingId
							+ "]]></bookingId><Tip><![CDATA["
							+ textFareTip.getText().toString().trim()
							+ "]]></Tip></updateTripPayment>";
					new SendXmlAsync(URL.BASE_URL + URL.DRIVER_TIP,
							xmlDriverTip, DriverDetails.this,
							DriverDetails.this, true).execute();

				}

			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// updating fare without any tip

				String xmlDriverTip = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><updateTripPayment><driverId><![CDATA["
						+ driverId
						+ "]]></driverId><riderId><![CDATA["
						+ new ToroSharedPrefrnce(DriverDetails.this)
								.getUserId()
						+ "]]></riderId><bookingId><![CDATA["
						+ bookingId
						+ "]]></bookingId><Tip><![CDATA["
						+ "0"
						+ "]]></Tip></updateTripPayment>";
				new SendXmlAsync(URL.BASE_URL + URL.DRIVER_TIP, xmlDriverTip,
						DriverDetails.this, DriverDetails.this, true).execute();
				dialogTip.dismiss();

			}
		});

		dialogTip.show();

	}

	/**
	 * this method is used to call check driver dialog
	 */

	private void showCheckDialog() {
		final Dialog dialog = new Dialog(DriverDetails.this,
				style.Theme_Translucent_NoTitleBar);

		imgHighLighter.setVisibility(View.GONE);

		dialog.setContentView(R.layout.pop_up_check_list);

		ImageView imgTouch = (ImageView) dialog.findViewById(R.id.imgTouch);
		imgTouch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				isCheckPopShown = true;
			}
		});

		Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				isCheckPopShown = true;

			}
		});

		dialog.show();

	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		Log.d(TAG,"onMapReady");
// Getting GoogleMap object from the fragment
		mMap = googleMap;
		mMap.setMyLocationEnabled(true);
		mMap.getUiSettings().setMyLocationButtonEnabled(false);
		mMap.getUiSettings().setRotateGesturesEnabled(false);
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

		mMap.setOnMarkerClickListener(this);
		mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double
				.parseDouble(getIntent().getExtras().getString("LAT")),
				Double.parseDouble(getIntent().getExtras()
						.getString("LONG")))));
			/* cretaing custom camera position */

			/* setting custom camera position */

		mMap.animateCamera(CameraUpdateFactory.zoomTo(mMap
				.getMaxZoomLevel() - 7));

		myLocationMarker = mMap.addMarker(new MarkerOptions().position(
				new LatLng(Double.parseDouble(getIntent().getExtras()
						.getString("LAT")), Double.parseDouble(getIntent()
						.getExtras().getString("LONG")))).icon(
				BitmapDescriptorFactory
						.fromResource(R.drawable.map_pin_user_red_new)));

	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {

	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

	}

	private class RouteTask extends AsyncTask<String, Void, String> {
		Context context;
		int AddressType = 0;

		public RouteTask(Context context, int AddressType) {
			this.context = context;
			this.AddressType = AddressType;
		}

		@Override
		protected String doInBackground(String... url) {
			// For storing data from web service
			String data = "";
			Log.d(TAG, "Route task doInBackground");
			// Obtain browser key from https://code.google.com/apis/console
			String key = "&key=" + URL.GOOGLEAPIKEY.trim();

			try {
				// Fetching the data from web service in background
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d(TAG, e.toString());
			}
			Log.d(TAG,"url \n"+data);
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			try {

				ParserTask parserTask = new ParserTask();

				// Invokes the thread for parsing the JSON data
				parserTask.execute(result);
			} catch (Exception e) {
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
				Log.d("ParserTask",jsonData[0].toString());
				DataParser parser = new DataParser();
				Log.d("ParserTask", parser.toString());

				// Starts parsing data
				routes = parser.parse(jObject);
				Log.d("ParserTask","Executing routes");
				Log.d("ParserTask",routes.toString());

			} catch (Exception e) {
				Log.d("ParserTask",e.toString());
				e.printStackTrace();
			}
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points;
			if(routeLine != null){
				routeLine.remove();
			}
			PolylineOptions lineOptions = null;
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

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(10);
				lineOptions.color(Color.BLACK);

				Log.d("onPostExecute","onPostExecute lineoptions decoded");

			}

			// Drawing polyline in the Google Map for the i-th route
			if(lineOptions != null) {
			routeLine =	mMap.addPolyline(lineOptions);
			}
			else {
/*
				Snackbar snackbar = Snackbar
						.make(findViewById(R.id.main_rl), "Route not available.", Snackbar.LENGTH_LONG);

				snackbar.show();
				Log.d("onPostExecute","without Polylines drawn");
*/

			}
		}
	}
}
