package com.tms.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tms.R;
import com.tms.model.LocationsName;
import com.tms.newui.MainActivity4;
import com.tms.utility.URL;
import com.tms.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Adapter to set on the list of Pick up locations
 * 
 * @author arvind.agarwal
 * 
 */
public class PickUpAdapter extends BaseAdapter {

	ArrayList<LocationsName> mArrayList;
	Activity mContext;
	int index;
	EditText edtSearch;
	double startAddressLat, startAddressLongi;
	String Name, Address = "";
	View  bottomsheetbar;
	Typeface typeFace;

	public PickUpAdapter(Activity mContext,
						 ArrayList<LocationsName> mArrayList, EditText edtSearch) {
		// TODO Auto-generated constructor stub
		this.mArrayList = mArrayList;

		this.mContext = mContext;
		this.edtSearch = edtSearch;
		typeFace=Typeface.createFromAsset(mContext.getAssets(), Utility.TYPE_FACE_HELVATICA_NEUE_MEDIUM);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mArrayList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int arg0, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		
		ViewHolder holder=new ViewHolder();
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.list_layout_address,
					parent, false);
			holder.txtAddress = (TextView) convertView
					.findViewById(R.id.txtLocationAddress);
			 holder.txtLocationName = (TextView) convertView
					.findViewById(R.id.txtLocationName);
			 holder.imgIcon=(ImageView)convertView.findViewById(R.id.icon_location);
			 
			 convertView.setTag(holder);

			

		}else{
			holder=(ViewHolder)convertView.getTag();
		}

		
		
		holder.txtAddress.setText(mArrayList.get(arg0).getName());
		holder.txtAddress.setVisibility(View.GONE);
		holder.txtLocationName.setText(mArrayList.get(arg0).getName());
		holder.txtLocationName.setTypeface(typeFace);
		
		/*convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// hiding keyboard
				InputMethodManager imm = (InputMethodManager) mContext
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);

				//

				//GoogleRefrenceApiNew googleRefrenceApi = new GoogleRefrenceApiNew(
				//		mContext, mArrayList.get(arg0).getReference());
				//googleRefrenceApi.execute();
				// ankit chhabra @1.10PM 5 december modified

				Name = mArrayList.get(arg0).getName();
				edtSearch.setText(Name);

			}
		});*/
		return convertView;
	}

	class GoogleRefrenceApiNew extends AsyncTask<Void, Void, Void> {
		String refrence = "";
		Context context;
		String status = "";
		StringBuilder jsonResults;
		JSONObject jsonObjectNew;
		ProgressDialog progressDialog = null;

		public GoogleRefrenceApiNew(Context context, String string) {
			this.context = context;
			this.refrence = string;

		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			progressDialog =  new ProgressDialog(mContext, ProgressDialog.THEME_HOLO_LIGHT);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Loading...");
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
			final String TYPE_DETAILS = "/details";
			final String OUT_JSON = "/json";

			HttpURLConnection conn = null;
			StringBuilder jsonResults = new StringBuilder();
			try {
				StringBuilder sb = new StringBuilder(PLACES_API_BASE);
				sb.append(TYPE_DETAILS);
				sb.append(OUT_JSON);
				sb.append("?sensor=true");
				sb.append("&key=" + URL.GOOGLEAPIKEY);

				sb.append("&reference=" + URLEncoder.encode(refrence, "utf8"));

				java.net.URL url = new java.net.URL(sb.toString());
				conn = (HttpURLConnection) url.openConnection();
				InputStreamReader in = new InputStreamReader(
						conn.getInputStream());

				// Load the results into a StringBuilder
				int read;
				char[] buff = new char[1024];
				while ((read = in.read(buff)) != -1) {
					jsonResults.append(buff, 0, read);
				}
			} catch (MalformedURLException e) {
				return null;
			} catch (IOException e) {
				return null;
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}

			try {
				// Create a JSON object hierarchy from the results
				JSONObject jsonObject = new JSONObject(jsonResults.toString());

			status = jsonObject.getString("status");
				if (status.equalsIgnoreCase("OK")) {

					JSONObject result = jsonObject.getJSONObject("result");

					JSONObject geometry = result.getJSONObject("geometry");
					jsonObject = geometry.getJSONObject("location");

					startAddressLat = Double.parseDouble(jsonObject
							.getString("lat"));
					startAddressLongi = Double.parseDouble(jsonObject
							.getString("lng"));

				} else if (status.equalsIgnoreCase("ZERO_RESULTS")) {

					Toast.makeText(context, "Invalid address", Toast.LENGTH_SHORT).show();

				}

			} catch (final JSONException e) {

			}
			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub

			super.onPostExecute(result);
			if (status.equalsIgnoreCase("OK")) {
				progressDialog.dismiss();

				Intent mIntent = new Intent(mContext, MainActivity4.class);
				mIntent.putExtra("name", Name);
				mIntent.putExtra("address", "");
				mIntent.putExtra("lat", String.valueOf(startAddressLat));
				mIntent.putExtra("lng", String.valueOf(startAddressLongi));
				((Activity) context).setResult(1, mIntent);
				((Activity) context).finish();

			}
		}
	}
	
	class ViewHolder{
		TextView txtAddress,txtLocationName;
		
		ImageView imgIcon;
	}

}
