package com.tms.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tms.R;
import com.tms.utility.CheckInternetConnectivity;
import com.tms.utility.ResponseListener;


/**
 * 
 * @author arvind.agarwal
 *
 */
public class EnterAddress extends Activity implements ResponseListener {

	Button btnSearch;
	EditText edtSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);
	}

	public void initializeViews() {
		btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (CheckInternetConnectivity
						.checkinternetconnection(EnterAddress.this)) {
//					new WebServiceAsynk(URL.ADDRESS_LOCATION_NAME
//							+ edtSearch.getText().toString().trim()
//									.replace(" ", "%20"), EnterAddress.this,
//							EnterAddress.this, true, "").execute();
				} else {
					Toast.makeText(EnterAddress.this, "NO INTERNET",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		edtSearch = (EditText) findViewById(R.id.edtPickUpAddress);
	}

	@Override
	public void response(String strresponse, String webservice) {
		// TODO Auto-generated method stub
		try {
			JSONObject mJsonObject = new JSONObject(strresponse);
			JSONObject mObject = mJsonObject.getJSONObject("response");
			JSONArray mJsonArray = mObject.getJSONArray("venues");
			for (int i = 0; i < mJsonArray.length(); i++) {
				JSONObject mChildJsonObject = mJsonArray.getJSONObject(i);

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
