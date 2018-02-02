package com.tms.activity;

import android.R.style;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.tms.PlaceJSONParser;
import com.tms.R;
import com.tms.model.LocationsName;
import com.tms.newui.MainActivity4;
import com.tms.utility.CheckInternetConnectivity;
import com.tms.utility.ResponseListener;
import com.tms.utility.ToroSharedPrefrnce;
import com.tms.utility.URL;
import com.tms.utility.Utility;
import com.tms.utility.UtilsSingleton;
import com.tms.utility.WebServiceAsynk;

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

/**
 * 
 * 
 * @author arvind.agarwal
 * this view is useful for setting Pickup and DropOff location 
 * 
 */
public class PickUpLocation extends Activity implements OnClickListener,
		ResponseListener {
	RelativeLayout mRecentLayout, mSearchLayout;
	Button btnSearch, btnBack, btnCross, btnPickUp, btnDestination;
	ListView listRecentSearch, listSearchResult;
	TextView txtHeader;
	EditText edtSearch;
	ToroSharedPrefrnce mPrefrnce;
	ArrayList<LocationsName> mArrayList;
	LocationsName mLocationsName;
	PickUpAdapter mAdapter;
	Intent mIntent;
	PlacesTask placesTask = null;
	int StartAddressType = 1;
	Typeface typeFace, typeFaceHelvaticaNEULight;
	ImageButton imageButtonSearch, imageButtonRecent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pic_location);
		
		//fetching type face
		typeFace = Typeface.createFromAsset(getAssets(), Utility.TYPE_FACE);
		typeFaceHelvaticaNEULight = Typeface.createFromAsset(getAssets(),
				Utility.TYPE_FACE_HELVATICA_NEUE_LIGHT);
		// initializing shared preference
		mPrefrnce = new ToroSharedPrefrnce(PickUpLocation.this);
		mArrayList = new ArrayList<LocationsName>();
		initialize();
		mIntent = getIntent();
		
		
		// setting header title
		if (mIntent.getExtras().getString("via")
				.equalsIgnoreCase("PICK UP LOCATION")) {
			txtHeader.setText("PICK UP LOCATION");

		} else {
			btnPickUp.setBackgroundResource(R.drawable.icon_destination);
			btnDestination.setBackgroundResource(R.drawable.icon_pickup);

			txtHeader.setText("DROP OFF LOCATION");
		}

	}

	/**
	 * method to initialize views
	 */

	private void initialize() {
		// TODO Auto-generated method stub
		((TextView) findViewById(R.id.txtSearchResult))
				.setTypeface(typeFaceHelvaticaNEULight);
		((TextView) findViewById(R.id.txtReecentResult))
				.setTypeface(typeFaceHelvaticaNEULight);
		btnPickUp = (Button) findViewById(R.id.btnPicUp);
		btnDestination = (Button) findViewById(R.id.btnDestination);
		imageButtonRecent = (ImageButton) findViewById(R.id.imgBtnRecent);
		imageButtonSearch = (ImageButton) findViewById(R.id.imgBtnSearch);
		txtHeader = (TextView) findViewById(R.id.txtHeader);
		edtSearch = (EditText) findViewById(R.id.edtPickUpAddress);
		edtSearch.setTypeface(typeFaceHelvaticaNEULight);
		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(this);
		btnCross = (Button) findViewById(R.id.btnCross);
		btnCross.setOnClickListener(this);

		mRecentLayout = (RelativeLayout) findViewById(R.id.layoutRecentResult);
		mRecentLayout.setOnClickListener(this);
		mSearchLayout = (RelativeLayout) findViewById(R.id.layoutSearchResult);
		mSearchLayout.setOnClickListener(this);
		listRecentSearch = (ListView) findViewById(R.id.listRecentSerchResult);
		listSearchResult = (ListView) findViewById(R.id.listSerchResult);

		edtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				imageButtonSearch
						.setImageResource(R.drawable.arrow_right_pick_up);

				if ((s.equals("")) && (s.length() > 0)) {
					edtSearch.getHandler().postDelayed(new Runnable() {

						@Override
						public void run() {
							if (placesTask != null) {
								placesTask.cancel(true);

							}

						}
					}, 200);
				} else {
					placesTask = new PlacesTask(PickUpLocation.this,
							StartAddressType);
					placesTask.execute(s.toString());

				}

			}
		});

		edtSearch.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				
				// closing key board

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);

				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
						|| (actionId == EditorInfo.IME_ACTION_DONE)) {
					if (!edtSearch.getText().toString().trim()
							.equalsIgnoreCase("")) {

						imageButtonSearch
								.setImageResource(R.drawable.arrow_down_pick_up);
						/*
						 * mSearchLayout
						 * .setBackgroundResource(R.drawable.title_black_bar_open
						 * );
						 */
						
						// search for location
						new WebServiceAsynk(URL.ADDRESS_LAT_LNG
								+ UtilsSingleton.getInstance().getCurrent_lat()
								+ ","
								+ UtilsSingleton.getInstance()
										.getCurrent_longi()
								+ URL.INTENT_LOCATION
								+ URL.QUERY
								+ edtSearch.getText().toString().trim()
										.replace(" ", "%20") + URL.OAUTH_TOKEN,
								PickUpLocation.this, PickUpLocation.this, true,
								"").execute();
					} else {
						Toast.makeText(PickUpLocation.this, "Enter Location",
								Toast.LENGTH_LONG).show();
					}
				}
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnBack:
			onBackPressed();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);

			break;
		case R.id.btnSearch:
			if (CheckInternetConnectivity
					.checkinternetconnection(PickUpLocation.this)) {
				mArrayList.clear();
				if (edtSearch.getText().toString().trim().equalsIgnoreCase("")) {
					Toast.makeText(PickUpLocation.this, "Enter Place Name",
							Toast.LENGTH_LONG).show();
				} else {

					imageButtonSearch
							.setImageResource(R.drawable.arrow_down_pick_up);
					/*
					 * mSearchLayout
					 * .setBackgroundResource(R.drawable.title_black_bar_open);
					 */
					
					// search for location
					new WebServiceAsynk(URL.ADDRESS_LAT_LNG
							+ UtilsSingleton.getInstance().getCurrent_lat()
							+ ","
							+ UtilsSingleton.getInstance().getCurrent_longi()
							+ URL.INTENT_LOCATION
							+ URL.QUERY
							+ edtSearch.getText().toString().trim()
									.replace(" ", "%20") + URL.OAUTH_TOKEN,
							PickUpLocation.this, PickUpLocation.this, true, "")
							.execute();
				}

			} else {
				Toast.makeText(PickUpLocation.this, "No Internet",
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.layoutSearchResult:
			if (CheckInternetConnectivity
					.checkinternetconnection(PickUpLocation.this)) {
				mArrayList.clear();
				if (edtSearch.getText().toString().trim().equalsIgnoreCase("")) {
					Toast.makeText(PickUpLocation.this, "Enter Place Name",
							Toast.LENGTH_LONG).show();
				} else {

					imageButtonSearch
							.setImageResource(R.drawable.arrow_down_pick_up);
					/*
					 * mSearchLayout
					 * .setBackgroundResource(R.drawable.title_black_bar_open);
					 */
					new WebServiceAsynk(URL.ADDRESS_LAT_LNG
							+ UtilsSingleton.getInstance().getCurrent_lat()
							+ ","
							+ UtilsSingleton.getInstance().getCurrent_longi()
							+ URL.INTENT_LOCATION
							+ URL.QUERY
							+ edtSearch.getText().toString().trim()
									.replace(" ", "%20") + URL.OAUTH_TOKEN,
							PickUpLocation.this, PickUpLocation.this, true, "")
							.execute();
				}

			} else {
				Toast.makeText(PickUpLocation.this, "No Internet",
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.btnCross:
			edtSearch.setText("");
			break;
		default:
			break;
		}

	}

	
	
	 /*
	  * (non-Javadoc)
	  * @see com.ontimecab.utility.ResponseListener#response(java.lang.String, java.lang.String)
	  * 
	  * this method is used to tackle webservice response
	  */
	@Override
	public void response(String strresponse, String webservice) {
		// TODO Auto-generated method stub
		// if (strresponse.contains("response")) {
		//
		// try {
		// mArrayList.clear();
		// JSONObject mJsonObject = new JSONObject(strresponse);
		// JSONObject mJsonObject2 = mJsonObject.getJSONObject("response");
		// JSONArray mJsonArray = mJsonObject2.getJSONArray("venues");
		// if (mJsonArray.length() > 0) {
		// if (mJsonArray.length() < 5) {
		// for (int i = 0; i < mJsonArray.length(); i++) {
		// JSONObject mJsonObject3 = mJsonArray
		// .getJSONObject(i);
		// JSONObject mJsonObject4 = mJsonObject3
		// .getJSONObject("location");
		// mLocationsName = new LocationsName();
		// mLocationsName.setLocationName(mJsonObject3
		// .getString("name"));
		//
		// if (mJsonObject4.toString().contains("country")) {
		// mLocationsName.setCountry(mJsonObject4
		// .getString("country"));
		// }
		// if (mJsonObject4.toString().contains("state")) {
		// mLocationsName.setState(mJsonObject4
		// .getString("state"));
		//
		// }
		// if (mJsonObject4.toString().contains("lat")) {
		// mLocationsName.setLat(mJsonObject4
		// .getString("lat"));
		// }
		// if (mJsonObject4.toString().contains("lng")) {
		// mLocationsName.setLng(mJsonObject4
		// .getString("lng"));
		// }
		// if (mJsonObject4.toString().contains("address")) {
		// mLocationsName.setAddress(mJsonObject4
		// .getString("address"));
		//
		// }
		// mArrayList.add(mLocationsName);
		//
		// }
		// UtilsSingleton.getInstance().mRecentSearch = mArrayList;
		// mAdapter = new PickUpAdapter(PickUpLocation.this,
		// mArrayList, edtSearch);
		// listSearchResult.setVisibility(View.VISIBLE);
		// listSearchResult.setAdapter(mAdapter);
		// listRecentSearch.setVisibility(View.GONE);
		// mRecentLayout
		// .setBackgroundResource(R.drawable.title_black_bar_closed);
		// } else {
		//
		// for (int i = 0; i < 5; i++) {
		// JSONObject mJsonObject3 = mJsonArray
		// .getJSONObject(i);
		// JSONObject mJsonObject4 = mJsonObject3
		// .getJSONObject("location");
		// mLocationsName = new LocationsName();
		// mLocationsName.setLocationName(mJsonObject3
		// .getString("name"));
		//
		// if (mJsonObject4.toString().contains("country")) {
		// mLocationsName.setCountry(mJsonObject4
		// .getString("country"));
		// }
		// if (mJsonObject4.toString().contains("state")) {
		// mLocationsName.setState(mJsonObject4
		// .getString("state"));
		//
		// }
		// if (mJsonObject4.toString().contains("lat")) {
		// mLocationsName.setLat(mJsonObject4
		// .getString("lat"));
		// }
		// if (mJsonObject4.toString().contains("lng")) {
		// mLocationsName.setLng(mJsonObject4
		// .getString("lng"));
		// }
		// if (mJsonObject4.toString().contains("address")) {
		// mLocationsName.setAddress(mJsonObject4
		// .getString("address"));
		// mArrayList.add(mLocationsName);
		// }
		//
		// }
		// UtilsSingleton.getInstance().mRecentSearch = mArrayList;
		// mAdapter = new PickUpAdapter(PickUpLocation.this,
		// mArrayList, edtSearch);
		// listSearchResult.setVisibility(View.VISIBLE);
		// listSearchResult.setAdapter(mAdapter);
		// listRecentSearch.setVisibility(View.GONE);
		// mRecentLayout
		// .setBackgroundResource(R.drawable.title_black_bar_closed);
		// }
		// } else {
		//
		// showDialog("Message", "No Location Found");
		// /*
		// * Toast.makeText(PickUpLocation.this, "No Location Found",
		// * Toast.LENGTH_LONG).show();
		// */
		// }
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (UtilsSingleton.getInstance().mRecentSearch.size() > 0) {
			imageButtonRecent.setImageResource(R.drawable.arrow_down_pick_up);
			mAdapter = new PickUpAdapter(PickUpLocation.this,
					UtilsSingleton.getInstance().mRecentSearch, edtSearch);
			listRecentSearch.setVisibility(View.GONE);
			listRecentSearch.setAdapter(mAdapter);
		}
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		Intent mIntent = new Intent(PickUpLocation.this, MainActivity4.class);
		mIntent.putExtra("name", "FF");
		setResult(1, mIntent);
		this.finish();
	}

	private void showDialog(String title, String message) {

		final Dialog dialog = new Dialog(PickUpLocation.this,
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

	// Fetches all places from GooglePlaces AutoComplete Web Service
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
			String types="types=(establishment|address)";
			
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
			String parameters = input +  "&" + types + "&" + sensor + "&"
					+ Location + "&" + key;

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
				Toast.makeText(getApplicationContext(), "No place found", Toast.LENGTH_SHORT)
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
					JSONObject jsonObj;
					try {
						mArrayList.clear();
						jsonObj = new JSONObject(result.toString());
						JSONArray predsJsonArray = jsonObj
								.getJSONArray("predictions");

						for (int i = 0; i < predsJsonArray.length(); i++) {
							mLocationsName = new LocationsName();
							mLocationsName.setId(predsJsonArray
									.getJSONObject(i).getString("id"));
							mLocationsName.setName(predsJsonArray
									.getJSONObject(i).getString("description"));
							mLocationsName.setReference(predsJsonArray
									.getJSONObject(i).getString("reference"));
							mArrayList.add(mLocationsName);
						}

						UtilsSingleton.getInstance().mRecentSearch = mArrayList;
						mAdapter = new PickUpAdapter(PickUpLocation.this,
								mArrayList, edtSearch);
						listSearchResult.setVisibility(View.VISIBLE);
						listSearchResult.setAdapter(mAdapter);
						mAdapter.notifyDataSetChanged();
						listRecentSearch.setVisibility(View.GONE);
						imageButtonRecent
								.setImageResource(R.drawable.arrow_right_pick_up);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}
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
				int[] to = new int[] { android.R.id.text1 };

				// Creating a SimpleAdapter for the AutoCompleteTextView
				SimpleAdapter adapter = new SimpleAdapter(getBaseContext(),
						result, android.R.layout.simple_spinner_item, from, to);

				// Setting the adapter

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

}
