package com.tms.activity;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

/**
 * 
 * @author arvind.agarwal
 * this class is used make google places api hit and fetch the places response from google servers
 *
 */

public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements
		Filterable {
	private ArrayList<String> resultList;

	private static final String LOG_TAG = "Places_api";

	
	//url for places api hit
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	
	//params for hit
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
// getting value of api
	private static final String API_KEY = com.tms.utility.URL.GOOGLEAPIKEY
			.trim();

	private ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			String Location = "" + "&location=" + com.tms.activity.Home.target.latitude + ","
					+ com.tms.activity.Home.target.longitude;
			String types = "types=establishment";
			StringBuilder sb = new StringBuilder(PLACES_API_BASE
					+ TYPE_AUTOCOMPLETE + OUT_JSON);
			sb.append("?sensor=true&key=" + API_KEY);
			sb.append(Location);
			sb.append(types);
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());
			Log.d("====", "Requesst send");
			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, "Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			Log.e(LOG_TAG, "Error connecting to Places API", e);
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			Log.d("JSON", "Parsing resultant JSON :)");
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

			// Extract the Place descriptions from the results
			resultList = new ArrayList<String>(predsJsonArray.length());
			Log.d("JSON",
					"predsJsonArray has length " + predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {

				resultList.add(predsJsonArray.getJSONObject(i).getString(
						"description")/*+"*"+predsJsonArray.getJSONObject(i).getString(
								"reference")*/);
				Log.d("JSON", resultList.get(i));
			}
		} catch (JSONException e) {
			Log.e(LOG_TAG, "Cannot process JSON results", e);
		}

		return resultList;
	}

	public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	@Override
	public int getCount() {
		return resultList.size();
	}

	@Override
	public String getItem(int index) {
//		if (resultList.get(index).equalsIgnoreCase("")) {
			return resultList.get(index);
//		}
//		else {
//			return resultList.get(index).substring(0,resultList.get(index).indexOf("*"));
//					
//		}
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				if (constraint != null) {
					// Retrieve the autocomplete results.
					resultList = autocomplete(constraint.toString());

					// Assign the data to the FilterResults
					filterResults.values = resultList;
					filterResults.count = resultList.size();
				}
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				if (results != null && results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}

			public boolean onLoadClass(Class arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		return filter;
	}
}
