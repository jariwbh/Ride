package com.tms.activity;

import android.R.style;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.tms.R;
import com.tms.utility.CheckInternetConnectivity;
import com.tms.utility.SendXmlAsync;
import com.tms.utility.ToroSharedPrefrnce;
import com.tms.utility.URL;
import com.tms.utility.Utility;
import com.tms.utility.XmlListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Activity to register user with Application First step of user registration
 * 
 * @author arvind.agarwal
 * 
 */
public class Register extends Activity implements OnClickListener, XmlListener {
	private Button btnBack, btnNext;
	public static Activity activity;

	private EditText edtEmail, edtPhone, edtPassword, edtCountryCode,
			edtConfirmPassword;
	ToroSharedPrefrnce mPrefrnce;

	private Typeface typeFace, typeFaceMessage;
	private ImageView selectorEmail, selectorPhone, selectorPassword,
			selectorConfirmationPassword;
	private ScrollView scrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		activity = this;
		mPrefrnce = new ToroSharedPrefrnce(Register.this);
		typeFace = Typeface.createFromAsset(getAssets(), Utility.TYPE_FACE);

		typeFaceMessage = Typeface.createFromAsset(getAssets(),
				Utility.TYPE_FACE_FORGOT);
		((TextView) findViewById(R.id.txtHeader)).setTypeface(typeFace);
		((TextView) findViewById(R.id.txtMessage)).setTypeface(typeFaceMessage);

		initializeViews();

		// GetCountryZipCode();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	/*
	 * initialzing views
	 */
	public void initializeViews() {
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		selectorEmail = (ImageView) findViewById(R.id.selector_email);
		selectorPassword = (ImageView) findViewById(R.id.selector_password);
		selectorConfirmationPassword = (ImageView) findViewById(R.id.selector_password_confirmation);
		selectorPhone = (ImageView) findViewById(R.id.selector_phone);

		btnBack = (Button) findViewById(R.id.btnBack);

		btnBack.setOnClickListener(this);
		btnNext = (Button) findViewById(R.id.btnNext);
		btnNext.setOnClickListener(this);

		edtEmail = (EditText) findViewById(R.id.edtEmailRegister);
		edtEmail.setTypeface(typeFace);
		edtPassword = (EditText) findViewById(R.id.edtPasswordRegister);
		edtPassword.setTypeface(typeFace);
		edtConfirmPassword = (EditText) findViewById(R.id.edtPasswordConfirmRegister);
		edtConfirmPassword.setTypeface(typeFace);
		edtPhone = (EditText) findViewById(R.id.edtPhone);
		edtPhone.setTypeface(typeFace);
		edtCountryCode = (EditText) findViewById(R.id.edtCountryCode);
		edtCountryCode.setTypeface(typeFace);

		edtCountryCode.setText(mPrefrnce.getCountryCode());

		// handling country code field focus
		edtCountryCode.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

				if (s.length() == 0) {
					edtCountryCode.setText("+");
				}

				if (!edtCountryCode.getText().toString().contains("+")) {
					edtCountryCode.setText("+"
							+ edtCountryCode.getText().toString());

				}

				if (!edtCountryCode.getText().toString().substring(0, 1)
						.equals("+")) {
					edtCountryCode.setText(edtCountryCode.getText().toString()
							.substring(1));

				}
				if (s.length() == 1
						&& edtCountryCode.getText().toString().equals("+")) {
					edtCountryCode.setSelection(1);
				}
				if (edtCountryCode.getSelectionStart() == 0) {
					edtCountryCode.setSelection(1);
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

		edtCountryCode.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub

				if (hasFocus) {
					// edtEmail.setBackgroundResource(R.drawable.text_field_blank);
					selectorPhone
							.setBackgroundResource(R.drawable.background_text_field_selected);
					// imgEmail.setBackgroundResource(R.drawable.mail_icon);
				} else {
					selectorPhone
							.setBackgroundResource(R.drawable.background_text_field);
				}

			}
		});

		edtPhone.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub

				if (hasFocus) {

					// edtEmail.setBackgroundResource(R.drawable.text_field_blank);
					selectorPhone
							.setBackgroundResource(R.drawable.background_text_field_selected);
					// imgEmail.setBackgroundResource(R.drawable.mail_icon);
				} else {

					selectorPhone
							.setBackgroundResource(R.drawable.background_text_field);

				}
			}
		});

		edtPassword.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					// edtEmail.setBackgroundResource(R.drawable.text_field_blank);
					selectorPassword
							.setBackgroundResource(R.drawable.background_text_field_selected);
					// imgEmail.setBackgroundResource(R.drawable.mail_icon);
				} else {
					selectorPassword
							.setBackgroundResource(R.drawable.background_text_field);
				}
			}
		});

		edtConfirmPassword
				.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						// TODO Auto-generated method stub
						if (hasFocus) {

							// edtEmail.setBackgroundResource(R.drawable.text_field_blank);
							selectorConfirmationPassword
									.setBackgroundResource(R.drawable.background_text_field_selected);
							// imgEmail.setBackgroundResource(R.drawable.mail_icon);
						} else {
							selectorConfirmationPassword
									.setBackgroundResource(R.drawable.background_text_field);
						}
					}
				});

		edtEmail.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					selectorEmail
							.setBackgroundResource(R.drawable.background_text_field_selected);
				} else {

					selectorEmail
							.setBackgroundResource(R.drawable.background_text_field);

				}
			}
		});

		edtPassword
				.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						// TODO Auto-generated method stub

						next();

						return false;

					}
				});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		/*case R.id.btnBack:

			startActivity(new Intent(Register.this, com.tms.activity.Option.class));
			finish();

			break;*/
		case R.id.btnNext:
			next();
			break;

		default:
			break;
		}
	}

	/**
	 * method to check valid format of emailId
	 * 
	 * @param email
	 *            string to pass for validation
	 * @return return true if email string is valid
	 */
	public boolean isEmailValid(String email) {

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);

		if (matcher.matches()) {
			return true;
		} else {

			Log.i(".....Email", "Not valid");
			// Toast.makeText(RegistrationScreen.this, "enter valid e-mail id",
			// Toast.LENGTH_SHORT).show();
			return false;

		}
	}

	/**
	 * show app dialog on error
	 * 
	 * @param title
	 *            Title of dialog
	 * @param message
	 *            Message in dialog
	 */
	private void showDialogg(String title, String message) {

		final Dialog dialog = new Dialog(Register.this,
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

	@Override
	public void onResponse(String respose) {
		// TODO Auto-generated method stub
		Log.i("Response..", respose);

		btnNext.setEnabled(true);
		try {
			JSONObject mJsonObject = new JSONObject(respose);
			String resp = mJsonObject.getString("validateEmail");
			String message = mJsonObject.getString("message");
			if (resp.equalsIgnoreCase("-2")) {

				showDialogg("Error !", message);
			} else if (resp.equalsIgnoreCase("-1")) {
				showDialogg("Error !", message);
			} else if (resp.equalsIgnoreCase("-3")) {
				showDialogg("Error !", message);
			} else if (resp.equalsIgnoreCase("-4")) {
				showDialogg("Error !", message);
			} else if (resp.equalsIgnoreCase("-5")) {
				showDialogg("Error !", message);
			} else if (resp.equalsIgnoreCase("1")) {

				startActivity(new Intent(Register.this, CreateProfile.class));

			} else {
				showDialogg("Error !", message);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	/*
	 * public String GetCountryZipCode() {
	 * 
	 * String CountryID = ""; String CountryZipCode = "";
	 * 
	 * TelephonyManager manager = (TelephonyManager) this
	 * .getSystemService(Context.TELEPHONY_SERVICE); // getNetworkCountryIso
	 * CountryID = manager.getSimCountryIso().toUpperCase(); String[] rl =
	 * this.getResources().getStringArray(R.array.CountryCodes);
	 * mPrefrnce.setCounrty(CountryID); for (int i = 0; i < rl.length; i++) {
	 * String[] g = rl[i].split(","); if (g[1].trim().equals(CountryID.trim()))
	 * { CountryZipCode = g[0]; break; } } return CountryZipCode;
	 * 
	 * }
	 */

	/**
	 * this method is used to check whether email and phone are already exists
	 * on server or not
	 */

	private void next() {
		if (!CheckInternetConnectivity.checkinternetconnection(Register.this)) {
			showDialogg("Alert !", "Check Internet Connection.");

		} else if (edtEmail.getText().toString().trim().equalsIgnoreCase("")) {
			// edtEmail.setError("Enter Email");
			showDialogg("Alert !", "Please enter Email");
			edtEmail.requestFocus();

		} else if (edtPhone.getText().toString().trim().equalsIgnoreCase("") ) {
			// edtPhone.setError("Enter Phone Number");
			showDialogg("Alert !", "Please enter Phone Number");
			edtPhone.requestFocus();
		}/*else if (edtPhone.getText().toString().trim().length()<10 ) {
			// edtPhone.setError("Enter Phone Number");
			showDialogg("Alert !", "Please enter valid Phone Number");
			edtPhone.requestFocus();
		}*/
		
		
		else if (edtPassword.getText().toString().trim().equalsIgnoreCase("")) {
			// edtPassword.setError("Enter password");
			showDialogg("Alert !", "Please enter Password");
			edtPassword.requestFocus();
		}/* else if (edtConfirmPassword.getText().toString().trim()
				.equalsIgnoreCase("")) {
			// edtPassword.setError("Enter password");
			showDialogg("Alert !", "Please enter Confirm Password");
			edtConfirmPassword.requestFocus();
		}*/

		else if (!isEmailValid(edtEmail.getText().toString().trim())) {
			showDialogg("Alert !", "Enter valid Email");
			// edtEmail.setError("Enter valid Email.");
			edtEmail.requestFocus();
		} else if (edtPassword.getText().length() < 5
				|| edtPassword.getText().length() > 20) {
			// edtPassword
			// .setError("Enter password between 5 to 8 characters");
			showDialogg("Alert !", "Enter password between 5 to 20 characters");
			edtPassword.requestFocus();
		} else if (edtCountryCode.getText().toString().trim().equals("")
				|| edtCountryCode.getText().toString().trim().equals("+")) {
			showDialogg("Alert !", "Enter valid country code");
			edtCountryCode.requestFocus();
		}

		/*else if (!edtPassword.getText().toString()
				.equals(edtConfirmPassword.getText().toString())) {

		
*//*
			scrollView.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					scrollView.fullScroll(ScrollView.FOCUS_DOWN);

				}
			});*//*

			edtConfirmPassword.requestFocus();
			showDialogg("Alert!",
					"Confirm password doesnot match with password.");

		}
*/
		else {

			edtEmail.clearFocus();
			edtPhone.clearFocus();
			edtPassword.clearFocus();
			mPrefrnce.setEmail(edtEmail.getText().toString().trim());
			mPrefrnce.setPhone(edtPhone.getText().toString().trim());
			mPrefrnce.setPassword(edtPassword.getText().toString().trim());
			mPrefrnce.setCountryCode(edtCountryCode.getText().toString());

			// String registerXML =
			// "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><userRegistration><email><![CDATA["
			// + mPrefrnce.getEmail()
			// + "]]></email><phone><![CDATA["
			// + mPrefrnce.getPhone()
			// + "]]></phone><password><![CDATA["
			// + mPrefrnce.getPhone()
			// +
			// "]]></password><latitude><![CDATA[30.7415]]></latitude><longitude><![CDATA[76.456]]></longitude><deviceType><![CDATA[2]]></deviceType><deviceToken><![CDATA[5f2aa2213848b6204455618b7fc8685211001212c5d6b7a410ea1692c28951c6]]></deviceToken></userRegistration>";

			// check for phone number and emailid, whether already exists or not
			String validateXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><validateEmail><email><![CDATA["
					+ mPrefrnce.getEmail()
					+ "]]></email><phone><![CDATA["
					+ mPrefrnce.getCountryCode()
					+ mPrefrnce.getPhone()
					+ "]]></phone></validateEmail>";
			new SendXmlAsync(URL.BASE_URL + URL.VALIDATE_EMAIL, validateXML,
					this, Register.this, true).execute();
			btnNext.setEnabled(false);

		}

	}
}