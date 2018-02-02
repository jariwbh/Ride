package com.tms.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.style;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tms.R;
import com.tms.utility.CheckInternetConnectivity;
import com.tms.utility.SendXmlAsync;
import com.tms.utility.ToroSharedPrefrnce;
import com.tms.utility.URL;
import com.tms.utility.Utility;
import com.tms.utility.UtilsSingleton;
import com.tms.utility.XmlListener;


/**
 * 
 * @author arvind.agarwal
 *this class shows terms  and conditions 
 */

public class TermsCondition extends Activity implements XmlListener {
	ToroSharedPrefrnce mPrefrnce;
	public static Activity activity;	
	//webview to load terms and conditions
	private WebView webView;
	Typeface typeFace;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mPrefrnce = new ToroSharedPrefrnce(TermsCondition.this);
		setContentView(R.layout.activity_terms_condition);
		
		typeFace=Typeface.createFromAsset(getAssets(),Utility.TYPE_FACE);
		((TextView)findViewById(R.id.txtHeader)).setTypeface(typeFace);
		activity = this;
		webView=(WebView)findViewById(R.id.txtTitle);
	// url for terms and conditions
		
		webView.loadUrl(URL.BASE_URL+URL.TERMS_CONDITION);
		
		
		Button btnAccept = (Button) findViewById(R.id.btnAcceptCondition);
		btnAccept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (CheckInternetConnectivity
						.checkinternetconnection(TermsCondition.this)) {
					
					String registrationBit=null;
					
					if(mPrefrnce.getPaymentViaCreditCard()){
						registrationBit="1";
					}else{
						registrationBit="0";
					}
					
					
					//accepting terms and conditions and sending user detials to server saved in shared 
					String registration = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><userRegistration><firstName><![CDATA["
							+ mPrefrnce.getFirstName()
							+ "]]></firstName><lastName><![CDATA["
							+ mPrefrnce.getLastname()
							+ "]]></lastName><email><![CDATA["
							+ mPrefrnce.getEmail()
							+ "]]></email><phone><![CDATA["
							+ mPrefrnce.getPhone()
							+ "]]></phone><password><![CDATA["
							+ mPrefrnce.getPassword()
							+ "]]></password><imgBytes><![CDATA["
							+ mPrefrnce.getPhoto().trim()
							+ "]]></imgBytes><cardNumber><![CDATA["
							+ mPrefrnce.getCreditcardno().trim()
							+ "]]></cardNumber><month><![CDATA["
							+ mPrefrnce.getExpirydateMonth()
							+ "]]></month><year><![CDATA["
							+ mPrefrnce.getExpirydateYear()
							+ "]]></year><cvv><![CDATA["
							+ mPrefrnce.getCvv().trim()
							+ "]]></cvv><cardType><![CDATA["
							+ mPrefrnce.getCardType().trim()
							+ "]]></cardType><zipCode><![CDATA["
							+ mPrefrnce.getZip().trim()
							+ "]]></zipCode><latitude><![CDATA["
							+ UtilsSingleton.getInstance().getCurrent_lat()
							+ "]]></latitude><longitude><![CDATA["
							+ UtilsSingleton.getInstance().getCurrent_longi()
							+ "]]></longitude><deviceType><![CDATA[2]]></deviceType><deviceToken><![CDATA["
							+ mPrefrnce.getGcm()
							+ "]]></deviceToken><CountryCode><![CDATA["
							+ mPrefrnce.getCountryCode()
							+ "]]></CountryCode><registrationBit><![CDATA["
							+ registrationBit
							+ "]]></registrationBit></userRegistration>";

Log.e("Termsssssssssss", "registration: " + registration);

					new SendXmlAsync(URL.BASE_URL + URL.REGISTRATION,
							registration, TermsCondition.this,
							TermsCondition.this, true).execute();
				}

				else {
					Toast.makeText(TermsCondition.this, "No Internet",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		Button btnReject = (Button) findViewById(R.id.btnDeclineCondition);
		btnReject.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showDialog();
			}
		});
	}

	
	/*
	 * (non-Javadoc)
	 * @see com.ontimecab.utility.XmlListener#onResponse(java.lang.String)
	 * interface to handle server response
	 */
	@Override
	public void onResponse(String respose) {
		// TODO Auto-generated method stub
		
		try {
			JSONObject mJsonObject = new JSONObject(respose);
			String resp = mJsonObject.getString("userRegistration");
			String message = mJsonObject.getString("message");
			if (resp.equalsIgnoreCase("-1")) {
				showDialogg("Error", message);
			} else if (resp.equalsIgnoreCase("-2")) {
				showDialogg("Error", message);
			} else if (resp.equalsIgnoreCase("-3")) {
				showDialogg("Error", message);

			} else if (resp.equalsIgnoreCase("-4")) {
				showDialogg("Error", message);
			} else if (resp.equalsIgnoreCase("-5")) {
				showDialogg("Error", message);
			} else if (resp.equalsIgnoreCase("-6")) {
				showDialogg("Error", message);

			} else if (resp.equalsIgnoreCase("-7")) {
				showDialogg("Error", message);
			} else if (resp.equalsIgnoreCase("-8")) {
				showDialogg("Error", message);
			} else {
				// showDialogg("Success", )
				mPrefrnce.setUserId(resp);
				Toast.makeText(TermsCondition.this, "Success",
						Toast.LENGTH_LONG).show();
				startActivity(new Intent(TermsCondition.this,
						com.tms.activity.VarifyMobile.class).putExtra("via", "terms"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @param title title for dialog(String)
	 * @param message to show(String)
	 */
	private void showDialogg(String title, String message) {

		final Dialog dialog = new Dialog(this,style.Theme_Translucent_NoTitleBar);
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

	

	private void showDialog() {

		final Dialog dialog = new Dialog(TermsCondition.this,
				style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.dialog_information_gonr);

		Button btnYes = (Button) dialog.findViewById(R.id.dialog_ok_btn);
		Button btnNo = (Button) dialog.findViewById(R.id.dialog_cancel_btn);
		btnYes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Register.activity.finish();
				CreateProfile.activity.finish();
				try {
					CreditCardLink.activity.finish();
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				PreferenceManager
						.getDefaultSharedPreferences(TermsCondition.this)
						.edit().clear().commit();

				startActivity(new Intent(TermsCondition.this, Login.class));

				TermsCondition.this.finish();

			}
		});

		btnNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				dialog.dismiss();

			}
		});

		dialog.show();

	}
}
