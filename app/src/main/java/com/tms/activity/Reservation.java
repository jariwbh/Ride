package com.tms.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.style;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsoluteLayout;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.tms.R;
import com.tms.broadcast.TimeAlarm;
import com.tms.model.PlacesResult;
import com.tms.utility.CheckInternetConnectivity;
import com.tms.utility.ResponseListenerNew;
import com.tms.utility.SendXmlAsync;
import com.tms.utility.ToroSharedPrefrnce;
import com.tms.utility.URL;
import com.tms.utility.Utility;
import com.tms.utility.WebservicePostJsonAsyn;
import com.tms.utility.XmlListener;

public class Reservation extends Activity implements XmlListener,
		ResponseListenerNew {

	/**
	 * @author arvind.agarwal
	 * this class is used to make reservation 
	 */
	static final String PICK_UP_LOCATION = "PICK_UP_LOCATION",
			DROP_OFF_LOCATION = "DROP_OFF_LOCATION";
	private static final String TAG = "reservation";
	private SimpleAdapter adapter1, adapter2;

	private ArrayList<PlacesResult> arrayListNew;

	RelativeLayout relPLoc;

	RelativeLayout mainRel;
	EditText edtName, edtPhNumber, edtItem, edtWeight, edtDimensions,
			edtComments;
	TextView edtPLoc;
	TextView edtDLoc;
	Button edtPTime, edtPDate, edtCar;
	ToroSharedPrefrnce sharedPrefrnce;

	ImageView backBtn;
	Button btnReservation;
	ImageView btnListing;

	TimePickerDialog timePicker;

	DatePickerDialog datePicker;

	ProgressDialog pDialog;

	EditText edtCountryCode;
    HttpURLConnection urlConnection = null;

	String carType = "";
	Dialog dialog = null;
	String strStartPlaceId = "", strEndPlaceId = "";
	int StartAddressType = 1;
	int DestinationAddressType = 2;
	String startAddressLat = "", startAddressLongi = "",
			destinationAddressLat = "", destinationAddressLongi = "";
	ProgressDialog progressDialog = null;
	Handler mAddRouteHandler = null;
	boolean NowTurnOFDestinationAddress = false;
	PlacesTask placesTask = null;

	TextView textTitle2;

	String currentTime = "";

	String currentDate = "";
	private EditText edtAirLine, edtFlight;
	boolean isReservationDone = false;
	private Typeface typeFace;

	private LinearLayout listPickup, listDropOff;
	private boolean recentlySearched = false;

	private int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	private int minute = Calendar.getInstance().get(Calendar.MINUTE);
	private int year = Calendar.getInstance().get(Calendar.YEAR);
	private int month = Calendar.getInstance().get(Calendar.MONTH);
	private int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	String paymentMode = "";
	private Place destPlace,sourcePlace;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.reservation);
		
		// getting typeFace from Utility
		typeFace = Typeface.createFromAsset(getAssets(), Utility.TYPE_FACE);
		((TextView) findViewById(R.id.txtTitle)).setTypeface(typeFace);

		sharedPrefrnce = new ToroSharedPrefrnce(Reservation.this);

		initialize();

		if (getIntent().hasExtra(Utility.TAG_RESERVATION)) {

			edtCar.setText(getIntent().getStringExtra(Utility.TAG_RESERVATION));
			carType = "4";
			edtCar.setEnabled(false);
			edtComments.setVisibility(View.VISIBLE);
			edtItem.setVisibility(View.VISIBLE);
			edtWeight.setVisibility(View.VISIBLE);
			edtDimensions.setVisibility(View.VISIBLE);

		}

		// toFromLax();

		textTitle2 = (TextView) findViewById(R.id.txtTitle2);
		textTitle2.setVisibility(View.GONE);
		textTitle2
				.setText("Only from LAX\n(Los Angeles International Airport)and to LAX");

		edtPhNumber.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == KeyEvent.ACTION_DOWN) {

					switch (keyCode) {
						case KeyEvent.KEYCODE_ENTER:

							if (getIntent().hasExtra(Utility.TAG_RESERVATION)) {

							} else {
								showCarDialor();
							}

							return true;

						default:
							break;
					}

				}

				return false;
			}
		});


		edtDLoc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				findPlace(0);
			}
		});

		edtPLoc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				findPlace(1);
			}
		});



		edtName.setText(sharedPrefrnce.getFirstName()+" "+sharedPrefrnce.getLastname());
		edtPhNumber.setText(sharedPrefrnce.getPhone());

		//showDialog();

	}

	/***************
	 *********
	 ********
    //Method to show Alert Dialog for Cash/Credit Card
	 *********
	 *******
	*********************/
	private void showDialog() {

		final Dialog dialog = new Dialog(Reservation.this,
				style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.dialog_paymentmode);

		Button btnCash = (Button) dialog.findViewById(R.id.dialog_ok_btn);
		final Button btnCredit = (Button) dialog.findViewById(R.id.dialog_cancel_btn);
		TextView txtTitle = (TextView) dialog.findViewById(R.id.text);

//		btnCash.setWidth(btnCredit.getWidth());
		txtTitle.setText("Payment Option \n Please choose your payment option.");
		btnCash.setText("Cash");

		if (sharedPrefrnce.getCreditcardno().equals("")) {

			//btnCredit.setText("Credit Card");

			btnCredit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					startActivity(new Intent(Reservation.this, AddCreditCard.class));
					//btnCredit.setText("Credit Card");
					btnCredit.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							paymentMode = "1";
							dialog.dismiss();
							Add();
						}
					});
				}
			});

		}else{
			btnCredit.setText("Credit Card");
			btnCredit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					paymentMode = "1";
					dialog.dismiss();
					Add();
				}
			});
		}





		btnCash.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				paymentMode = "0";
				dialog.dismiss();
				Add();
			}
		});



		dialog.show();

	}
	/**
	 * making reservation
	 */
	protected void Add() {

		// hit webservces
		String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><reservation><riderId><![CDATA["
				+ sharedPrefrnce.getUserId()
				+ "]]></riderId><name><![CDATA["
				+ edtName.getText().toString().trim()
				+ "]]></name><picUpLoc><![CDATA["
				+ edtPLoc.getText().toString().trim()
				+ "]]></picUpLoc><dropOffLoc><![CDATA["
				+ edtDLoc.getText().toString().trim()
				+ "]]></dropOffLoc><picUpTime><![CDATA["
				+ edtPTime.getText().toString().trim()
				+ "]]></picUpTime><picUpDate><![CDATA["
				+ edtPDate.getText().toString().trim()
				+ "]]></picUpDate><phone><![CDATA["
				+ edtPhNumber.getText().toString().trim()
				+ "]]></phone><carType><![CDATA["
				+ carType
				+ "]]></carType><picUpLat><![CDATA["
				+ startAddressLat
				+ "]]></picUpLat><picUpLong><![CDATA["
				+ startAddressLongi
				+ "]]></picUpLong><destiLat><![CDATA["
				+ destinationAddressLat
				+ "]]></destiLat><destiLong><![CDATA["
				+ destinationAddressLongi
				+ "]]></destiLong><airline><![CDATA["
				+ edtAirLine.getText().toString()
				+ "]]></airline>"
				+ "<flightNumber><![CDATA["
				+ edtFlight.getText().toString()
				+ "]]></flightNumber>"
				+ "<curTime><![CDATA["
				+ currentTime
				+ "]]></curTime><curDate><![CDATA["
				+ currentDate
				+ "]]></curDate><countryCode><![CDATA["
				+ edtCountryCode.getText().toString()
				+ "]]></countryCode><itemToMove><![CDATA["
				+ edtItem.getText().toString()
				+ "]]></itemToMove><weight><![CDATA["
				+ edtWeight.getText().toString()
				+ "]]></weight><dimension><![CDATA["
				+ edtDimensions.getText().toString()
				+ "]]></dimension><comment><![CDATA["
				+ edtComments.getText().toString()
				+ "]]></comment><paymentMode><![CDATA["
				+paymentMode+"]]></paymentMode></reservation>";

		Log.i("xml", xmlString);

		// String
		// xml="<?xml version=\"1.0\" encoding=\"UTF-8\" ?><reservation><riderId><![CDATA[7]]></riderId><name><![CDATA[Kapil Bansal]]></name><picUpLoc><![CDATA[DT Cinema Chandigarh]]></picUpLoc><dropOffLoc><![CDATA[ISBT 43 Chandigarh]]></dropOffLoc><picUpTime><![CDATA[22:10]]></picUpTime><picUpDate><![CDATA[2015-02-12]]></picUpDate><phone><![CDATA[+919780666296]]></phone><carType><![CDATA[2]]></carType><picUpLat><![CDATA[30.73331]]></picUpLat><picUpLong><![CDATA[76.77941]]></picUpLong><destiLat><![CDATA[30.7164]]></destiLat><destiLong><![CDATA[76.74411]]></destiLong><airline><![CDATA[Flight Name]]></airline><flightNumber><![CDATA[123456]]></flightNumber><curTime><![CDATA[]]></curTime><curDate><![CDATA[]]></curDate><countryCode><![CDATA[]]></countryCode></reservation>";

		new SendXmlAsync(URL.BASE_URL + URL.RESERVATION, xmlString,
				Reservation.this, Reservation.this, true).execute();

	}
	
	/**
	 * initializing view
	 */

	private void initialize() {

		edtComments = (EditText) findViewById(R.id.edtComment);
		edtDimensions = (EditText) findViewById(R.id.edtItemDimension);
		edtWeight = (EditText) findViewById(R.id.edtItemWeight);
		edtItem = (EditText) findViewById(R.id.edtItemToMove);
		edtComments.setTypeface(typeFace);
		edtDimensions.setTypeface(typeFace);
		edtWeight.setTypeface(typeFace);
		edtItem.setTypeface(typeFace);
		listPickup = (LinearLayout) findViewById(R.id.listPickUp);
		listDropOff = (LinearLayout) findViewById(R.id.listDropOff);

		edtCountryCode = (EditText) findViewById(R.id.edtPhCode);
		edtCountryCode.setText(sharedPrefrnce.getCountryCode());
		edtCountryCode.setTypeface(typeFace);

		edtCountryCode.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub

				if (hasFocus) {

				} else {

				}

			}
		});

		edtCountryCode.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (!edtCountryCode.getText().toString().contains("+")) {
					if (s.length() == 0) {
						edtCountryCode.setText("+");
					}

					if (!edtCountryCode.getText().toString().contains("+")) {
						edtCountryCode.setText("+"
								+ edtCountryCode.getText().toString());

					}

					if (!edtCountryCode.getText().toString().substring(0, 1)
							.equals("+")) {
						edtCountryCode.setText(edtCountryCode.getText()
								.toString().substring(1));

					}
					if (s.length() == 1
							&& edtCountryCode.getText().toString().equals("+")) {
						edtCountryCode.setSelection(1);
					}
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

				if (s.length() == 0) {
					edtCountryCode.setText("+");
					edtCountryCode.setSelection(1);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}
		});

		edtAirLine = (EditText) findViewById(R.id.edtAirLine);
		edtAirLine.setTypeface(typeFace);
		edtCountryCode.setTypeface(typeFace);
		edtFlight = (EditText) findViewById(R.id.edtFlight);
		edtFlight.setTypeface(typeFace);

		btnListing = (ImageView) findViewById(R.id.btnReservationList);
		btnListing.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(Reservation.this,
						ReservationListing.class));

			}
		});

		mainRel = (RelativeLayout) findViewById(R.id.mainRel);

		edtName = (EditText) findViewById(R.id.edtName);
		edtName.setTypeface(typeFace);
		edtName.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == KeyEvent.KEYCODE_ENTER) {

					listPickup.setVisibility(View.VISIBLE);

				}

				return false;
			}
		});
		edtPLoc = (TextView) findViewById(R.id.edtPLoc);
		edtPLoc.setTypeface(typeFace);

		edtDLoc = (TextView) findViewById(R.id.edtDLoc);
		edtDLoc.setTypeface(typeFace);

		edtPTime = (Button) findViewById(R.id.edtPTime);
		edtPTime.setTypeface(typeFace);
		edtPDate = (Button) findViewById(R.id.edtPDate);
		edtPDate.setTypeface(typeFace);
		edtPhNumber = (EditText) findViewById(R.id.edtPhNumber);
		edtPhNumber.setTypeface(typeFace);
		edtCar = (Button) findViewById(R.id.edtCarType);

		btnReservation = (Button) findViewById(R.id.btn_registration);
		backBtn = (ImageView) findViewById(R.id.btnBack);

		edtPTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				timePicker = new TimePickerDialog(Reservation.this,
						new OnTimeSetListener() {

							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								// TODO Auto-generated method stub

								hour = hourOfDay;
								Reservation.this.minute = minute;				
								String  AM_PM="AM";
								
								if(hourOfDay>=12){
									
									AM_PM="PM";
									
							
									
									
								}
								
								
								if(hourOfDay>12){
									hourOfDay=hourOfDay-12;
								}
								
								


								if (minute <= 9) {
									edtPTime.setText(hourOfDay + ":" + "0"
											+ minute+" "+AM_PM);
								} else {
									edtPTime.setText(hourOfDay + ":" + minute+" "+AM_PM);

								}

							}
						}, hour, minute, false);

				timePicker.show();

			}
		});

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(edtName.getWindowToken(), 0);

				imm.hideSoftInputFromWindow(edtName.getWindowToken(), 0);

				imm.hideSoftInputFromWindow(edtPhNumber.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(edtDLoc.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(edtPLoc.getWindowToken(), 0);

				finish();

			}
		});

		btnReservation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Calendar cal = Calendar.getInstance();

				String hour = "" + cal.get(Calendar.HOUR_OF_DAY);
				String minute = "" + cal.get(Calendar.MINUTE);

				String month = "" + (cal.get(Calendar.MONTH) + 1);
				String day = "" + cal.get(Calendar.DAY_OF_MONTH);

				if (month.length() == 1) {
					month = "0" + month;
				}

				if (day.length() == 1) {
					day = "0" + day;
				}
				currentDate = cal.get(Calendar.YEAR) + "-" + month + "-" + day;

				if (hour.length() == 1) {
					hour = "0" + hour;
				}

				if (minute.length() == 1) {
					minute = "0" + minute;
				}

				currentTime = hour + ":" + minute;

				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd hh:mm a");
				Date date = null;
				try {
					date = dateFormat.parse(edtPDate.getText().toString() + " "
							+ edtPTime.getText().toString());

				} catch (Exception e) {
					e.printStackTrace();
				}

				if (!CheckInternetConnectivity
						.checkinternetconnection(Reservation.this)) {
					showDialog("Alert !", "Check internet connectivity.");
				} else if (edtName.getText().toString().equals("")) {
					showDialog("Alert !", "Please enter Name.");
				} else if (edtPLoc.getText().toString().equals("")) {
					showDialog("Alert !", "Please enter pickup location.");
				} else if (edtDLoc.getText().toString().equals("")) {
					showDialog("Alert !", "Please enter drop off location.");
				}
				else if (edtPTime.getText().toString().equals("")) {
					showDialog("Alert !", "Please enter pickup time.");
				} else if (edtPhNumber.getText().toString().equals("")) {
					showDialog("Alert !", "Please enter phone number.");
				} else if (edtCar.getText().toString().equals("")) {
					showDialog("Alert !", "Please specify car type.");
				} else if (carType.equals("4")) {
					if (edtItem.getText().toString().trim().equals("")) {
						showDialog("Alert!", "Please enter item to move.");

					} else if (edtWeight.getText().toString().trim().equals("")) {
						showDialog("Alert!", "Please enter weight of item.");

					} else if (edtDimensions.getText().toString().trim()
							.equals("")) {
						showDialog("Alert!", "Please enter dimensions of item.");

					} else if (edtComments.getText().toString().trim()
							.equals("")) {
						showDialog("Alert!", "Please enter comments.");

					} else {
						//Add();
						showDialog();
					}
				}

				/*else if (date.getTime() - System.currentTimeMillis() <= (10800000)) {//  user cannot make reservation 3 hours prior
					showDialog("Alert!",
							"Reservation should be made 3 hours prior");

				} else if (startAddressLat.equals("")
						|| startAddressLongi.equals("")) {
					showDialog("Alert!",
							"Please select another pickup location.");
				} else if (destinationAddressLat.equals("")
						|| destinationAddressLongi.equals("")) {
					showDialog("Alert!",
							"Please select another dropoff location.");
				}*/

				else {
//					Add();
					showDialog();
				}
				/*
				 * if (edtPLoc.getText().toString().equals("LAX")) { }
				 * 
				 * new GoogleRefrenceApiNew(strRefrenceDestinationAddress, 2)
				 * .execute(); } else if
				 * (edtDLoc.getText().toString().equals("LAX")) { new
				 * GoogleRefrenceApiNew(strRefrenceStartAddress, 1) .execute();
				 * }
				 */

			}
		});



		edtName.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub

			}
		});

		edtPLoc.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub

			}
		});

		edtDLoc.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub

			}
		});

		edtPTime.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub

			}
		});

		edtPDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				datePicker = new DatePickerDialog(Reservation.this,
						new OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								// TODO Auto-generated method stub
								
								month=monthOfYear;
								day=dayOfMonth;
								Reservation.this.year=year;

								String month = "" + (monthOfYear + 1), day = ""
										+ dayOfMonth;

								if (month.length() == 1) {

									month = "0" + month;

								}

								if (day.length() == 1) {
									day = "0" + day;
								}

								edtPDate.setText(year + "-" + month + "-" + day);

							}
						}, year, month, day);

				datePicker.show();

			}
		});
		edtPDate.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub

			}
		});

		edtPhNumber.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub

			}
		});

		edtCar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				showCarDialor();

			}
		});

		edtCar.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub

			}
		});

		edtAirLine.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub

			}
		});

		edtFlight.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	public void onResponse(String respose) {
		// TODO Auto-generated method stub

		Log.i("response", respose);
		if (pDialog != null) {
			pDialog.dismiss();

		}

		Log.i("response", respose);

		if (respose.contains("confirmReservation")) {

			/*
			 * Server Error :
			 * {"confirmReservation":"-2","message":"Server Error."} Success :
			 * {"confirmReservation"
			 * :"1","message":"your reservation has been confirmed successfully."
			 * } Already Confirmed :
			 * {"confirmReservation":"-4","message":"Reservation already confirmed."
			 * } Payment Declined (Un-confirmed) :
			 * {"confirmReservation":"-3","message":
			 * "Please check with your bank and try agian. If this continues to happen, please try a different card."
			 * }
			 */

			try {
				JSONObject jsonObject = new JSONObject(respose);

				if (jsonObject.getString("confirmReservation").equals("1")) {

					showDialogg("Message", jsonObject.getString("message"));
					
				

				} else {
					showDialog("Message", jsonObject.getString("message"));
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (respose.contains("reservation")) {

			JSONObject jObj = null;
			try {
				jObj = new JSONObject(respose);

				if (jObj.getString("reservation").equals("-2")
						|| jObj.getString("reservation").equals("-3")
						|| jObj.getString("reservation").equals("-1")) {

					showDialog("Alert!", jObj.getString("message"));

				} else {
					isReservationDone = true;

					showConfirmationPopUp(edtPLoc.getText().toString(), edtDLoc
							.getText().toString(), jObj.getString("fareQuote"),
							jObj.getString("reservation"));
					
					clearData();
					// showDialog("Message", jObj.getString("message"));

					// setNotification(edtPDate.getText().toString()+" "+edtPTime.getText().toString(),
					// "77");

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// showDialogg("Message",
		// "Your reservation request has been received.");

	}

	
	/**
	 * 
	 * @param title
	 * @param message
	 * this method is used to show alert dialog
	 */
	private void showDialog(String title, String message) {

		final Dialog dialog = new Dialog(Reservation.this,
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

	
	/**
	 * 
	 * @param title
	 * @param message
	 * this method is used to show alert dialog
	 */
	private void showDialogg(String title, String message) {

		final Dialog dialog = new Dialog(Reservation.this,
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

				finish();

			}
		});

		txtTitle.setText(title);
		txtMessage.setText(message);

		dialog.show();

	}

	private void setNotification(String dateTimeStr, String id) {

		SimpleDateFormat formatToCompare = new SimpleDateFormat(
				"MM/dd/yyyy hh:mm");

		Date dateNotification = null;

		try {
			dateNotification = formatToCompare.parse(dateTimeStr);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Intent intent = null;
		intent = new Intent(getApplicationContext(), TimeAlarm.class);
		intent.putExtra("NOTIFICATION", "You have made a reservation.");
		intent.putExtra("ID", Integer.parseInt(id));
		intent.putExtra("LONG", dateNotification.getTime());

		PendingIntent sender = PendingIntent.getBroadcast(
				getApplicationContext(), Integer.parseInt(id), intent, 0);

		AlarmManager am = null;
		am = (AlarmManager) getSystemService(ALARM_SERVICE);

		// am.setRepeating(AlarmManager.RTC_WAKEUP,
		// dateNotification.getTime(), AlarmManager.INTERVAL_DAY,
		// sender);

		am.set(AlarmManager.RTC_WAKEUP, dateNotification.getTime(), sender);

	}
	/**
	 * this method is used to show car picker dialog
	 * 
	 */
	private void showCarDialor() {

		dialog = new Dialog(Reservation.this,
				style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.picker_reservation);
		Button hyb = (Button) dialog.findViewById(R.id.hybCarBtn);
		Button suv = (Button) dialog.findViewById(R.id.suvCarBtn);
		Button black = (Button) dialog.findViewById(R.id.blackCarBtn);

		Button lux = (Button) dialog.findViewById(R.id.luxuryCarBtn);
        Button  armour=(Button)dialog.findViewById(R.id.armourCarBtn);

		hyb.setText(Utility.CAR_1);
		suv.setText(Utility.CAR_2);
		black.setText(Utility.CAR_3);
        lux.setText(Utility.CAR_4);
        armour.setText(Utility.CAR_5);

        armour.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                dialog.dismiss();
                carType = "5";

                edtCar.setText(Utility.CAR_5);

                edtAirLine.requestFocus();



            }
        });

		lux.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				dialog.dismiss();
				carType = "4";

				edtCar.setText(Utility.CAR_4);

				edtAirLine.requestFocus();

				

			}
		});
		hyb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				dialog.dismiss();
				carType = "1";

				edtCar.setText(Utility.CAR_1);

				edtAirLine.requestFocus();

				edtItem.setVisibility(View.GONE);
				edtWeight.setVisibility(View.GONE);
				edtDimensions.setVisibility(View.GONE);
				edtComments.setVisibility(View.GONE);

			}
		});

		suv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				dialog.dismiss();

				carType = "2";

				edtCar.setText(Utility.CAR_2);

				edtAirLine.requestFocus();
				edtItem.setVisibility(View.GONE);
				edtWeight.setVisibility(View.GONE);
				edtDimensions.setVisibility(View.GONE);
				edtComments.setVisibility(View.GONE);

			}
		});

		black.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				dialog.dismiss();
				carType = "3";
				edtCar.setText(Utility.CAR_3);

				edtAirLine.requestFocus();
				edtItem.setVisibility(View.GONE);
				edtWeight.setVisibility(View.GONE);
				edtDimensions.setVisibility(View.GONE);
				edtComments.setVisibility(View.GONE);

			}
		});

		dialog.show();

	}

    /**
     *
     * @author arvind.agarwal Calling places api
     */

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

            // String types="";
            try {
                types = URLEncoder.encode(types, HTTP.UTF_8);
            } catch (UnsupportedEncodingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            // Sensor enabled
            String sensor = "sensor=true";

            // Building the parameters to the web service
            String parameters = input + "&" + types + "&" + sensor + "&"
			/* + Location + "&" */+ key;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"
                    + output + "?" + parameters;

            if (Utility.intraContinent) {

                url = url + "&components=country:us";
            }

            try {
                // Fetching the data from web service in background
                data = downloadUrl(url);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
                Toast.makeText(getApplicationContext(),
                        getString(R.string.reservation_no_place_found), Toast.LENGTH_SHORT)
                        .show();

            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (AddressType == 1) {
                // Creating ParserTask

                if (result.contains("ZERO_RESULTS")) {

                }

                else {
                    ParserTask parserTask = new ParserTask(Reservation.this, 1);

                    // Starting Parsing the JSON string returned by Web Service
                    parserTask.execute(result);

                }
            } else if (AddressType == 2) {

                if (result.contains("ZERO_RESULTS")) {

                }

                else {
                    // Creating ParserTask
                    ParserTask parserTask = new ParserTask(Reservation.this, 2);

                    // Starting Parsing the JSON string returned by Web Service
                    parserTask.execute(result);

                }

            }
        }
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;

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

        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends
            AsyncTask<String, Integer, List<HashMap<String, String>>> {
        int AddressType = 0;
        Context context;
        JSONObject jObject;

        public ParserTask(Context context, int AddressType) {
            this.context = context;
            this.AddressType = AddressType;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(
                String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {
            if (AddressType == 1) {
                String[] from = new String[] { "description" };
                int[] to = new int[] { R.id.txt };

                // Creating a SimpleAdapter for the AutoCompleteTextView
                adapter1 = new SimpleAdapter(getBaseContext(), result,
                        R.layout.adapter_pick_up, from, to);

                // Setting the adapter

                listPickup.removeAllViews();
                listDropOff.removeAllViews();


                if(edtPLoc.getText().toString().equals("")){
                    listPickup.removeAllViews();
                    return;
                }

                if (arrayListNew.size() > 0) {

                    for (int i = 0; i < arrayListNew.size(); i++) {

                        listPickup.addView(locationName(i, AddressType));

                    }



                }

				/*
				 * listPickup.setAdapter(adapter1);
				 * adapter1.notifyDataSetChanged();
				 */

            } else if (AddressType == 2) {
                String[] from = new String[] { "description" };
                int[] to = new int[] { R.id.txt };

                // Creating a SimpleAdapter for the AutoCompleteTextView
                adapter2 = new SimpleAdapter(getBaseContext(), result,
                        R.layout.adapter_pick_up, from, to);

                // Setting the adapter
				/*
				 * listDropOff.setAdapter(adapter2);
				 *
				 * adapter2.notifyDataSetChanged();
				 */

                listPickup.removeAllViews();
                listDropOff.removeAllViews();

                if(edtDLoc.getText().toString().equals("")){
                    listDropOff.removeAllViews();
                    return;
                }
                if (arrayListNew.size() != 0) {
                    for (int i = 0; i < arrayListNew.size(); i++) {

                        listDropOff.addView(locationName(i, AddressType));

                    }

                }else{
                    listDropOff.removeAllViews();
                }

            }
        }

    }

    // JsonParser

    public class PlaceJSONParser {

        /** Receives a JSONObject and returns a list */
        public ArrayList<HashMap<String, String>> parse(JSONObject jObject) {

            JSONArray jPlaces = null;
            try {
                /** Retrieves all the elements in the 'places' array */
                jPlaces = jObject.getJSONArray("predictions");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /**
             * Invoking getPlaces with the array of json object where each json
             * object represent a place
             */
            return getPlaces(jPlaces);
        }

        private ArrayList<HashMap<String, String>> getPlaces(JSONArray jPlaces) {
            int placesCount = jPlaces.length();
            ArrayList<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> place = null;

            arrayListNew = new ArrayList<PlacesResult>();

            /** Taking each place, parses and adds to list object */
            for (int i = 0; i < placesCount; i++) {
                try {
                    /** Call getPlace with place JSON object to parse the place */
                    place = getPlace((JSONObject) jPlaces.get(i));
                    placesList.add(place);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return placesList;
        }

        /** Parsing the Place JSON object */
        private HashMap<String, String> getPlace(JSONObject jPlace) {

            HashMap<String, String> place = new HashMap<String, String>();

            String id = "";
            String reference = "";
            String description = "";
            String place_id = "";

            try {

                description = jPlace.getString("description");
                id = jPlace.getString("id");
                // reference = jPlace.getString("reference");
                place_id = jPlace.getString("place_id");

                PlacesResult result = new PlacesResult();
                result.set_id(id);
                result.setDescription(description);
                result.setPlace_id(place_id);
                result.setReferenceId(reference);
                arrayListNew.add(result);

                place.put("description", description);
                place.put("_id", id);
                place.put("reference", place_id);
                place.put("place_id", place_id);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return place;
        }
    }

	@Override
	public void response(String strresponse, String via) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub

		// TODO Auto-generated method stub

		if (via.equals(PICK_UP_LOCATION)) {

			if (strresponse.contains("html_attributions")) {

				Log.i("response", strresponse);

				try {
					JSONObject jsonObject = new JSONObject(strresponse);

					JSONObject jsonChildObj = jsonObject
							.getJSONObject("result");

					JSONArray jsonArray = jsonChildObj
							.getJSONArray("address_components");

					/*
					 * for(int i=0;i<jsonArray.length();i++){
					 * if(jsonArray.getJSONObject
					 * (i).toString().contains("locality")){
					 * startCityName=jsonArray
					 * .getJSONObject(i).getString("long_name");
					 * 
					 * 
					 * 
					 * } }
					 */

					if (jsonChildObj.toString().contains("geometry")) {

						JSONObject jsonGrandChildObj = jsonChildObj
								.getJSONObject("geometry");
						JSONObject jsonGreatGrandChildObj = jsonGrandChildObj
								.getJSONObject("location");

						startAddressLat = jsonGreatGrandChildObj
								.getString("lat");
						startAddressLongi = jsonGreatGrandChildObj
								.getString("lng");

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} else if (via.equals(DROP_OFF_LOCATION)) {

			if (strresponse.contains("html_attributions")) {

				Log.i("response", strresponse);

				try {
					JSONObject jsonObject = new JSONObject(strresponse);

					JSONObject jsonChildObj = jsonObject
							.getJSONObject("result");

					JSONArray jsonArray = jsonChildObj
							.getJSONArray("address_components");

					if (jsonChildObj.toString().contains("geometry")) {

						JSONObject jsonGrandChildObj = jsonChildObj
								.getJSONObject("geometry");
						JSONObject jsonGreatGrandChildObj = jsonGrandChildObj
								.getJSONObject("location");

						destinationAddressLat = jsonGreatGrandChildObj
								.getString("lat");
						destinationAddressLongi = jsonGreatGrandChildObj
								.getString("lng");

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	private void showConfirmationPopUp(String from, String to, String fare,
			final String reservationId) {
		final Dialog dialogConfirmation = new Dialog(Reservation.this,
				style.Theme_Translucent_NoTitleBar_Fullscreen);

		dialogConfirmation.setContentView(R.layout.dialog_confirm_reservation);

		TextView txtPickUp, txtDropOff, txtfareQuote, txtHeader;
		Button btnConfirm;
		txtHeader = (TextView) dialogConfirmation.findViewById(R.id.txtHeader);
		txtPickUp = (TextView) dialogConfirmation.findViewById(R.id.txtFrom);
		txtDropOff = (TextView) dialogConfirmation.findViewById(R.id.txtTo);
		txtfareQuote = (TextView) dialogConfirmation.findViewById(R.id.txtFare);
		btnConfirm = (Button) dialogConfirmation
				.findViewById(R.id.btnConfirmation);

		txtPickUp.setText(from);
		txtDropOff.setText(to);

		txtfareQuote.setText(fare);

		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String xmlToSend = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><confirmReservation><riderId><![CDATA["
						+ (new ToroSharedPrefrnce(Reservation.this).getUserId())
						+ "]]></riderId><reservationId><![CDATA["
						+ reservationId
						+ "]]></reservationId></confirmReservation>";

				new SendXmlAsync(URL.BASE_URL + URL.CONFIRM_RESERVATION,
						xmlToSend, Reservation.this, Reservation.this, true)
						.execute();

				dialogConfirmation.dismiss();

			}
		});

		txtHeader.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogConfirmation.dismiss();

			}
		});

		dialogConfirmation.show();

	}

	private void clearData() {

		//edtName.setText("");
		edtPLoc.setText("");
		edtDLoc.setText("");
		edtPDate.setText("");
		edtPTime.setText("");
		//edtPhNumber.setText("");
		edtCar.setText("");
		edtAirLine.setText("");
		edtFlight.setText("");
		
		startAddressLat="";
		startAddressLongi="";
		
		destinationAddressLat="";
		destinationAddressLongi="";

	}

	private View locationName(final int position, final int type) {

		View view = null;

		LayoutInflater inflater = LayoutInflater.from(Reservation.this);

		view = inflater.inflate(R.layout.adapter_pick_up, null);

		TextView txtViewPlaceName = (TextView) view.findViewById(R.id.txt);
		
		try{
		txtViewPlaceName.setText(arrayListNew.get(position).getDescription());
		}catch(Exception e){
			
		}

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (type == 1) {
					try {

						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(edtPLoc.getWindowToken(), 0);

						strStartPlaceId = arrayListNew.get(position)
								.getPlace_id();
						String str = arrayListNew.get(position)
								.getDescription();

						str = str.replace("}", "");
						edtPLoc.setText(str);

						new WebservicePostJsonAsyn(Reservation.this,
								Reservation.this,
								"https://maps.googleapis.com/maps/api/place/details/json?placeid="
										+ strStartPlaceId + "&key="
										+ URL.GOOGLEAPIKEY, new JSONObject(),
								true, PICK_UP_LOCATION).execute();

						listPickup.setVisibility(View.GONE);

						recentlySearched = true;

					} catch (Exception e) {

					}

				} else {

					try {

						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(edtDLoc.getWindowToken(), 0);

						strEndPlaceId = arrayListNew.get(position)
								.getPlace_id();
						String str = arrayListNew.get(position)
								.getDescription();

						edtDLoc.setText(str);

						new WebservicePostJsonAsyn(Reservation.this,
								Reservation.this,
								"https://maps.googleapis.com/maps/api/place/details/json?placeid="
										+ strEndPlaceId + "&key="
										+ URL.GOOGLEAPIKEY, new JSONObject(),
								true, DROP_OFF_LOCATION).execute();

						listDropOff.setVisibility(View.GONE);
						recentlySearched = true;
					} catch (Exception e) {
						// TODO: handle exception
					}

				}

			}
		});

		return view;

	}

    private class DismissHits extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            arrayListNew = new ArrayList<PlacesResult>();
            listPickup.removeAllViews();
            listDropOff.removeAllViews();
        }

    }
	public void findPlace(int status) {
		try {
			AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setCountry("CA")/*.setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS).setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)*/.build();

			Intent intent =
					new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
							.setFilter(typeFilter)
							.build(this);
			startActivityForResult(intent, status);
		} catch (GooglePlayServicesRepairableException e) {
			e.printStackTrace();
		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
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
					edtDLoc.setText(destPlace.getAddress());
					break;
				case 1:
					sourcePlace = PlaceAutocomplete.getPlace(this, data);
					edtPLoc.setText(sourcePlace.getAddress());
					break;
			}
		} else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
			Status status = PlaceAutocomplete.getStatus(this, data);
			Log.i(TAG, status.getStatusMessage());
		} else if (resultCode == RESULT_CANCELED) {
			Log.i(TAG, "RESULT_CANCELED");
		}
	}
}
