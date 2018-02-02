package com.tms.activity;

import android.R.style;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tms.R;
import com.tms.utility.CheckInternetConnectivity;
import com.tms.utility.ResponseListener;
import com.tms.utility.ToroSharedPrefrnce;
import com.tms.utility.URL;
import com.tms.utility.Utility;
import com.tms.utility.WebServiceAsynk;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Activity for login or Register option screen
 * 
 * @author arvind.agarwal
 *
 */
public class Option extends Activity implements OnClickListener,
		ResponseListener {
	private Button btnLogin, btnregister;

	private RelativeLayout relativeLayout;

	String strGcmRegId;
	ToroSharedPrefrnce mPrefrnce;
	public static Activity mActivity;

	RelativeLayout netRel;
	private Typeface typeFace;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);
		typeFace = Typeface.createFromAsset(getAssets(),
				Utility.TYPE_FACE_HELVATICA_NEUE_LIGHT);

		((TextView) findViewById(R.id.frag_login_web_link))
				.setOnClickListener(this);
		netRel = (RelativeLayout) findViewById(R.id.net_rel);
		initializeViews();

		mActivity = Option.this;
		mPrefrnce = new ToroSharedPrefrnce(this);
//		GCMRegistrar.checkDevice(this);
//		GCMRegistrar.checkManifest(this);
//		strGcmRegId = GCMRegistrar.getRegistrationId(this);
//		strGcmRegId = GCMRegistrar.getRegistrationId(this);
//		if (strGcmRegId.equals("")) {
//			// Registration is not present, register now
//			// with GCM
//			GCMRegistrar.register(this, "718354980400");
//			strGcmRegId = GCMRegistrar.getRegistrationId(this);
//			Log.i("regId", "regId:" + strGcmRegId);
//			mPrefrnce.setGcm(strGcmRegId);
//
//		} else {
//
//			Log.i("regId", "regId:" + strGcmRegId);
//			mPrefrnce.setGcm(strGcmRegId);
//
//		}

		if (CheckInternetConnectivity.checkinternetconnection(this)) {
			new WebServiceAsynk(URL.BASE_URL + URL.GETCOUNTRYCODE, Option.this,
					Option.this, false, "first").execute();

		} else {

			showDialog("Alert !", "No Internet Connection.");
		}

	}

	/**
	 * method to initialize views
	 */
	public void initializeViews() {

		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setTypeface(typeFace);
		btnLogin.setOnClickListener(this);
		btnregister = (Button) findViewById(R.id.btnRegister);
		btnregister.setTypeface(typeFace);
		relativeLayout = (RelativeLayout) findViewById(R.id.layoutOptionTop);
		btnregister.setOnClickListener(this);
		
		

	
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.frag_login_web_link:

			Intent intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(URL.TMS_LINK));
			startActivity(intent);

			break;
		case R.id.btnLogin:
			
			
			
			startActivity(new Intent(Option.this, com.tms.activity.Login.class));
			
			
			this.finish();
			break;

		case R.id.btnRegister:
			startActivity(new Intent(Option.this, Register.class));
			this.finish();
			break;

		default:
			break;
		}

	}

	@Override
	public void response(String strresponse, String webservice) {
		// TODO Auto-generated method stub

		JSONObject mJsonObject = null;
		try {
			mJsonObject = new JSONObject(strresponse);

			new ToroSharedPrefrnce(Option.this).setCountryCode(mJsonObject
					.getString("CountryCode"));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void showDialog(String title, String message) {

		final Dialog dialog = new Dialog(Option.this,
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

	protected class ConnectionDetector extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub

			Log.d("app", "Network connectivity change");
			if (intent.getExtras() != null) {
				NetworkInfo ni = (NetworkInfo) intent.getExtras().get(
						ConnectivityManager.EXTRA_NETWORK_INFO);

				if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
					Log.i("app", "Network " + ni.getTypeName() + " connected");

					netRel.setVisibility(View.GONE);

				} else if (intent.getBooleanExtra(
						ConnectivityManager.EXTRA_NO_CONNECTIVITY,
						Boolean.FALSE)) {

					netRel.setVisibility(View.VISIBLE);

				}

			}

		}
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();

		System.gc();
	}

}
