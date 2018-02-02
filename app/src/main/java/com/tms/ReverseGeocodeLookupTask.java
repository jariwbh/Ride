package com.tms;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReverseGeocodeLookupTask extends AsyncTask<Void, Void, Void> {

	protected Context applicationContext;
	String strAddress = "";
	String localityLatLngResponse = "";
	LatLngResponse latLngResponse;

	public ReverseGeocodeLookupTask(Context context, String strAddress,
			LatLngResponse latLngResponse) {
		this.applicationContext = context;
		this.strAddress = strAddress;
		this.latLngResponse = latLngResponse;
	}

	@Override
	protected void onPreExecute() {

	}

	@Override
	protected Void doInBackground(Void... params) {

		if (!strAddress.equalsIgnoreCase("")) {
			localityLatLngResponse = Geocoder
					.reverseGeocode(strAddress.toString());

		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (!localityLatLngResponse.equalsIgnoreCase("")) {
			try {
				if (localityLatLngResponse.contains("status")) {
					JSONObject jsonObject = new JSONObject(
							localityLatLngResponse);
					String status = jsonObject.getString("status");
					if (status.equalsIgnoreCase("OK")) {

						JSONArray mResults = jsonObject.getJSONArray("results");
						if (mResults.length() > 0) {

							JSONObject geometry = mResults.getJSONObject(0)
									.getJSONObject("geometry");

							jsonObject = geometry.getJSONObject("location");

							// {"types":["hindu_temple","place_of_worship","establishment"],"formatted_address":"Mata Mansa Devi, Mansa Devi Complex, Mansa Devi Temple Complex, Panchkula, Haryana 134114, India","address_components":[{"types":["establishment"],"short_name":"Mata Mansa Devi","long_name":"Mata Mansa Devi"},{"types":["neighborhood","political"],"short_name":"MDC","long_name":"Mansa Devi Complex"},{"types":["sublocality","political"],"short_name":"Mansa Devi Temple Complex","long_name":"Mansa Devi Temple Complex"},{"types":["locality","political"],"short_name":"Panchkula","long_name":"Panchkula"},{"types":["administrative_area_level_2","political"],"short_name":"Panchkula","long_name":"Panchkula"},{"types":["administrative_area_level_1","political"],"short_name":"HR","long_name":"Haryana"},{"types":["country","political"],"short_name":"IN","long_name":"India"},{"types":["postal_code"],"short_name":"134114","long_name":"134114"}],"geometry":{"bounds":{"southwest":{"lng":76.8604977,"lat":30.7235795},"northeast":{"lng":76.86103279999999,"lat":30.72393}},"viewport":{"southwest":{"lng":76.85941626970849,"lat":30.72240576970851},"northeast":{"lng":76.8621142302915,"lat":30.7251037302915}},"location":{"lng":76.8607902,"lat":30.7237431},"location_type":"APPROXIMATE"}}

							jsonObject = geometry.getJSONObject("location");
							String latituse = jsonObject.getString("lat");
							String longitude = jsonObject.getString("lng");
							LatLng latLng = new LatLng(
									Double.parseDouble(latituse),
									Double.parseDouble(longitude));
							latLngResponse.onResponseLatLng(latLng);

						} else {
							latLngResponse.onErrorLatLng();
						}
					} else if (status.equalsIgnoreCase("ZERO_RESULTS")) {

						latLngResponse.onNoLAtLongFound(status);
					}

				}

			} catch (JSONException e) {
//				SingletonData.getInstance().setIsAddRoute(false);
				e.printStackTrace();
			}
		}
	}
}
