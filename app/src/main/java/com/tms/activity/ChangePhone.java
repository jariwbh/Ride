package com.tms.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.style;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tms.R;
import com.tms.utility.CheckInternetConnectivity;
import com.tms.utility.SendXmlAsync;
import com.tms.utility.ToroSharedPrefrnce;
import com.tms.utility.URL;
import com.tms.utility.Utility;
import com.tms.utility.XmlListener;

/**
 * Activity to change phone entered for verification
 * 
 * @author arvind.agarwal
 * 
 *         this view represents the view to change phone number
 * 
 */
public class ChangePhone extends Activity implements OnClickListener,
		XmlListener {

	private Button btnBack, btnChangePh;
	EditText edtChangePh, edtCountryCode;

	ToroSharedPrefrnce mPrefrnce;
	TextView imgPhone;
	RelativeLayout rel2;
	Typeface typeFace;

	ImageView selectorPhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_phone);
		typeFace = Typeface.createFromAsset(getAssets(), Utility.TYPE_FACE);
		((TextView) findViewById(R.id.txtTitle)).setTypeface(typeFace);
		rel2 = (RelativeLayout) findViewById(R.id.rel_2);

		mPrefrnce = new ToroSharedPrefrnce(ChangePhone.this);
		initializeViews();


	}

	@Override
	public void onResponse(String respose) {
		// TODO Auto-generated method stub
		try {
			JSONObject mJsonObject = new JSONObject(respose);
			String resp = mJsonObject.getString("changePhoneNumber");
			String message = mJsonObject.getString("message");
			if (resp.equalsIgnoreCase("-1")) {
				showDialog("Error ", message);

			} else if (resp.equalsIgnoreCase("-2")) {

				showDialog("Error ", message);
			} else if (resp.equalsIgnoreCase("1")) {
				mPrefrnce.setPhone(edtChangePh.getText().toString().trim());
				mPrefrnce.setCountryCode(edtCountryCode.getText().toString()
						.trim());

				Toast.makeText(ChangePhone.this, "" + message,
						Toast.LENGTH_LONG).show();
				finish();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnBack:
			finish();

			break;
		case R.id.btnChangePhone:
			
			
			// web service hit for ohone number change
			if (CheckInternetConnectivity
					.checkinternetconnection(ChangePhone.this)) {

				if (edtChangePh.getText().toString().trim()
						.equalsIgnoreCase("")) {
					edtChangePh.setError("Enter phone number");
				}else if (edtChangePh.getText().toString().trim().length()<10 ) {
					// edtPhone.setError("Enter Phone Number");
					showDialog("Alert !", "Please enter valid Phone Number");
					edtChangePh.requestFocus();
				} 
				
				
				else if (edtCountryCode.getText().length() == 1) {
					edtCountryCode.setError("Enter valid country code");
				}

				else {
					
					
					//change phone number webservice hit
					String changePhone = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><changePhoneNumber><userId><![CDATA["
							+ mPrefrnce.getUserId()
							+ "]]></userId><phone><![CDATA["
							+ edtChangePh.getText().toString().trim()
							+ "]]></phone><CountryCode><![CDATA["
							+ (edtCountryCode.getText().toString())
							+ "]]></CountryCode></changePhoneNumber>";
					new SendXmlAsync(URL.BASE_URL + URL.CHANGE_PHONE,
							changePhone, ChangePhone.this, ChangePhone.this,
							true).execute();
				}
			} else {
				Toast.makeText(ChangePhone.this, "No Internet",
						Toast.LENGTH_LONG).show();
			}

			break;

		default:
			break;
		}

	}

	/**
	 * show dialog on different error
	 * 
	 * @param title
	 *            title of dialog
	 * @param message
	 *            Message in dialog
	 */
	private void showDialog(String title, String message) {

		final Dialog dialog = new Dialog(ChangePhone.this,
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
	 * method to initialize views of screen
	 */
	public void initializeViews() {

		selectorPhone = (ImageView) findViewById(R.id.selector_phone);
		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);

		btnChangePh = (Button) findViewById(R.id.btnChangePhone);

		btnChangePh.setOnClickListener(this);
		edtChangePh = (EditText) findViewById(R.id.edtUserPhone);
		edtChangePh.setTypeface(typeFace);
		edtChangePh.setText(mPrefrnce.getPhone());
		edtCountryCode = (EditText) findViewById(R.id.edtCountryCode);
		edtCountryCode.setTypeface(typeFace);
		edtCountryCode.setText(mPrefrnce.getCountryCode());

		edtCountryCode.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub

				if (hasFocus) {
					selectorPhone
							.setBackgroundResource(R.drawable.background_text_field_selected);
				} else {
					selectorPhone
							.setBackgroundResource(R.drawable.background_text_field);
				}

			}
		});

		/**
		 * handling country code edit text view
		 * 
		 */

		edtCountryCode.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (!edtCountryCode.getText().toString().contains("+")) {
					if (s.length() == 0) {
						edtCountryCode.setText("+");
						edtCountryCode.setSelection(1);
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

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}
		});
		
		/**
		 * handling phone number edit text view
		 * 
		 */


		edtChangePh.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					selectorPhone
							.setBackgroundResource(R.drawable.background_text_field_selected);
				} else {
					selectorPhone
							.setBackgroundResource(R.drawable.background_text_field);
				}
			}
		});
	}


}
