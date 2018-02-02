package com.tms.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.style;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.tms.R;
import com.tms.newui.MainActivity4;
import com.tms.utility.CheckInternetConnectivity;
import com.tms.utility.SendXmlAsync;
import com.tms.utility.ToroSharedPrefrnce;
import com.tms.utility.URL;
import com.tms.utility.Utility;
import com.tms.utility.XmlListener;

/**
 * Activity to verify the phone number
 * 
 * @author arvind.agarwal
 * 
 */
public class VarifyMobile extends Activity implements OnClickListener,
		XmlListener {
	private Button  btnResend, btnChangePhNo;
	private ImageView btnCross;
	EditText edtCode1, edtCode2, edtCode3, edtCode4;
	TextView txtPhoneNo;
	
	ToroSharedPrefrnce mPrefrnce;
	Intent mIntent;
	String via;
	
	boolean check=false;
	private Typeface typeFaceItalic,typeFaceNormal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_varify_mobile);
		typeFaceItalic=Typeface.createFromAsset(getAssets(), Utility.TYPE_FACE_FORGOT);
		typeFaceNormal=Typeface.createFromAsset(getAssets(), Utility.TYPE_FACE);
		((TextView)findViewById(R.id.imgVerfyText)).setTypeface(typeFaceItalic);
		((TextView)findViewById(R.id.txtTitle)).setTypeface(typeFaceNormal);
		mPrefrnce = new ToroSharedPrefrnce(VarifyMobile.this);
		initialize();
		mIntent = getIntent();
		via = mIntent.getExtras().getString("via");
		
		/*
		 * setaccountverify is reset on call of this activity
		 */
		new ToroSharedPrefrnce(VarifyMobile.this).setAccountVerified(false);
	}

	/**
	 * method to initialize all views of screen
	 */
	public void initialize() {
		btnChangePhNo = (Button) findViewById(R.id.btnChangePhNo);
		btnChangePhNo.setOnClickListener(this);
		btnChangePhNo.setTypeface(typeFaceNormal);
		btnCross = (ImageView) findViewById(R.id.btnCrossVerfiy);
		btnCross.setOnClickListener(this);
		btnResend = (Button) findViewById(R.id.btnResendCode);
		btnResend.setOnClickListener(this);
		btnResend.setTypeface(typeFaceNormal);
		edtCode1 = (EditText) findViewById(R.id.edtPhoneCode1);

		edtCode3 = (EditText) findViewById(R.id.edtPhoneCode3);
		edtCode2 = (EditText) findViewById(R.id.edtPhoneCode2);
		edtCode4 = (EditText) findViewById(R.id.edtPhoneCode4);
		
		
		edtCode1.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
			if(check){
				
				edtCode2.setText("");
				edtCode3.setText("");
				edtCode4.setText("");
				edtCode1.setText("");
				edtCode1.requestFocus();
				
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(edtCode1,
						InputMethodManager.SHOW_FORCED);
				
			}
				
				
				
				return false;
			}
		});
		
		
		edtCode2.setOnKeyListener(new   OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				
				
				
				if(keyCode==KeyEvent.KEYCODE_BACK){
					
					if(edtCode2.getText().length()==0){
						edtCode1.setText("");
						edtCode1.setSelection(edtCode1.getText().length());
						
					}else{
						
						edtCode2.setText("");
						edtCode2.setSelection(edtCode1.getText().length());
					}
					
					
					
				}
				return false;
			}
		});
		
	/*	edtCode1.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				
				
				if(hasFocus  &&  check){
					edtCode2.setText("");
					edtCode3.setText("");
					edtCode4.setText("");
					
					
				}
				
			}
		});*/
		edtCode1.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				if (!edtCode1.getText().toString().trim().equalsIgnoreCase("")) {
					edtCode2.requestFocus();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});
		edtCode2.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				if (!edtCode2.getText().toString().trim().equalsIgnoreCase("")) {
					edtCode3.requestFocus();
				} else {
					edtCode1.requestFocus();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});
		edtCode3.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				if (!edtCode3.getText().toString().trim().equalsIgnoreCase("")) {
					edtCode4.requestFocus();
				} else {
					edtCode2.requestFocus();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});
		edtCode4.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (edtCode4.getText().toString().trim().equalsIgnoreCase("")) {
					edtCode3.requestFocus();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		edtCode4.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

				 if(s.length()==1 && edtCode1.getText().length()!=0&& edtCode2.getText().length()!=0&& edtCode3.getText().length()!=0){
					 
					 
					 
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.showSoftInput(edtCode4,
								InputMethodManager.HIDE_IMPLICIT_ONLY);
					 
					 check=true;
					 
						if (!edtCode1.getText().toString().trim()
								.equalsIgnoreCase("")
								&& !edtCode1.getText().toString().trim()
										.equalsIgnoreCase("")
								&& !edtCode2.getText().toString().trim()
										.equalsIgnoreCase("")
								&& !edtCode3.getText().toString().trim()
										.equalsIgnoreCase("")
								&& !edtCode4.getText().toString().trim()
										.equalsIgnoreCase("")) {
							if (CheckInternetConnectivity
									.checkinternetconnection(VarifyMobile.this)) {
								String varifyXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><verifyPhone><userId><![CDATA["
										+ mPrefrnce.getUserId()
										+ "]]></userId><verifycode><![CDATA["
										+ edtCode1.getText().toString().trim()
										+ edtCode2.getText().toString().trim()
										+ edtCode3.getText().toString().trim()
										+ edtCode4.getText().toString().trim()
										+ "]]></verifycode></verifyPhone>";

								new SendXmlAsync(URL.BASE_URL + URL.VARIFY_MOBILE,
										varifyXML, VarifyMobile.this,
										VarifyMobile.this, true).execute();
							} else {
								Toast.makeText(VarifyMobile.this,
										"No Internet Connection", Toast.LENGTH_LONG)
										.show();
							}
						}
				 }

			}
		});
		edtCode4.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
						|| (actionId == EditorInfo.IME_ACTION_DONE)) {
					if (!edtCode1.getText().toString().trim()
							.equalsIgnoreCase("")
							&& !edtCode1.getText().toString().trim()
									.equalsIgnoreCase("")
							&& !edtCode2.getText().toString().trim()
									.equalsIgnoreCase("")
							&& !edtCode3.getText().toString().trim()
									.equalsIgnoreCase("")
							&& !edtCode4.getText().toString().trim()
									.equalsIgnoreCase("")) {
						if (CheckInternetConnectivity
								.checkinternetconnection(VarifyMobile.this)) {
							String varifyXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><verifyPhone><userId><![CDATA["
									+ mPrefrnce.getUserId()
									+ "]]></userId><verifycode><![CDATA["
									+ edtCode1.getText().toString().trim()
									+ edtCode2.getText().toString().trim()
									+ edtCode3.getText().toString().trim()
									+ edtCode4.getText().toString().trim()
									+ "]]></verifycode></verifyPhone>";

							new SendXmlAsync(URL.BASE_URL + URL.VARIFY_MOBILE,
									varifyXML, VarifyMobile.this,
									VarifyMobile.this, true).execute();
						} else {
							Toast.makeText(VarifyMobile.this,
									"No Internet Connection", Toast.LENGTH_LONG)
									.show();
						}
					}

				}
				return false;
			}
		});
		txtPhoneNo = (TextView) findViewById(R.id.txtPhoneVerfy);
		txtPhoneNo.setText(mPrefrnce.getPhone());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnChangePhNo:
			startActivity(new Intent(VarifyMobile.this, com.tms.activity.ChangePhone.class));
			break;
		case R.id.btnCrossVerfiy:
			
			mPrefrnce.clearPrefRence();
			if (via.equalsIgnoreCase("creditCard")) {
				Register.activity.finish();
				CreateProfile.activity.finish();
				CreditCardLink.activity.finish();
				TermsCondition.activity.finish();
				startActivity(new Intent(VarifyMobile.this, Login.class));
			}
			
			else if(via.equalsIgnoreCase("EditProfile")){
				startActivity(new Intent(VarifyMobile.this, Login.class));

			}
			finish();
			break;
		case R.id.btnResendCode:
			if (CheckInternetConnectivity
					.checkinternetconnection(VarifyMobile.this)) {
				
				/*
				 * resend code call to phone number
				 */

				String resendXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><resendCode><email><![CDATA["
						+ mPrefrnce.getEmail()
						+ "]]></email><CountryCode><![CDATA["
						+ new ToroSharedPrefrnce(VarifyMobile.this).getCountryCode()+ "]]></CountryCode><phone><![CDATA["
						+ new ToroSharedPrefrnce(VarifyMobile.this).getPhone() + "]]></phone></resendCode>";
				new SendXmlAsync(URL.BASE_URL + URL.RESEND_CODE, resendXML,
						VarifyMobile.this, VarifyMobile.this, true).execute();
			} else {
				Toast.makeText(VarifyMobile.this, "No Internet Connection",
						Toast.LENGTH_LONG).show();
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		txtPhoneNo.setText(mPrefrnce.getPhone());
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ontimecab.utility.XmlListener#onResponse(java.lang.String)
	 */

	@Override
	public void onResponse(String respose) {
		// TODO Auto-generated method stub

		if (respose.contains("verifyPhone")) {
			try {
				JSONObject mJsonObject = new JSONObject(respose);
				String resp = mJsonObject.getString("verifyPhone");
				String message = mJsonObject.getString("message");
				if (resp.equalsIgnoreCase("-1")) {
					showDialogg("Error", message);
				} else if (resp.equalsIgnoreCase("-2")) {
					showDialogg("Error", message);
				} else if (resp.equalsIgnoreCase("-3")) {
					showDialogg("Error", message);
				} else if (resp.equalsIgnoreCase("-4")) {
					
					edtCode1.setText("");
					edtCode2.setText("");
					edtCode3.setText("");
					edtCode4.setText("");
					edtCode1.requestFocus();
					
					
					
					showDialogg("Error", message);
				} else if (resp.equalsIgnoreCase("1")) {
					Toast.makeText(VarifyMobile.this, "Success",
							Toast.LENGTH_LONG).show();
					
					
					new ToroSharedPrefrnce(VarifyMobile.this).setAccountVerified(true);
					
					
					
					
					if(via.equalsIgnoreCase("EditProfile")){
						
					//donot do anything	
						
						startActivity(new Intent(VarifyMobile.this, MainActivity4.class));
						
					}else{
						
						
						
					// clearing account info after fetching gcm	
					String  gcm =	new ToroSharedPrefrnce(VarifyMobile.this).getGcm();
					
					new ToroSharedPrefrnce(VarifyMobile.this).clearPrefRence();
					
					
					new ToroSharedPrefrnce(VarifyMobile.this).setGcm(gcm);

					startActivity(new Intent(VarifyMobile.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
					if (via.equalsIgnoreCase("terms")) {
				
						if(Register.activity!=null){
						Register.activity.finish();
						}
						
						if(CreateProfile.activity!=null){
						CreateProfile.activity.finish();
						}
						if(CreditCardLink.activity!=null){
						CreditCardLink.activity.finish();
						}
						if(TermsCondition.activity!=null){
						TermsCondition.activity.finish();
						}
					}
					}

					finish();

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (respose.contains("resendCode")) {
			try {
				JSONObject mJsonObject = new JSONObject(respose);
				String resp = mJsonObject.getString("resendCode");
				String message = mJsonObject.getString("message");
				if (resp.equalsIgnoreCase("-1")) {
					showDialogg("Error", message);
				} else if (resp.equalsIgnoreCase("-2")) {
					showDialogg("Error", message);
				} else if (resp.equalsIgnoreCase("-3")) {
					showDialogg("Error", message);
				} else if (resp.equalsIgnoreCase("1")) {
				Toast.makeText(VarifyMobile.this, message, Toast.LENGTH_SHORT).show();
					
					
					

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

		final Dialog dialog = new Dialog(this,
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
	
	
	
	
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 * 
	 * 
	 * handling back press
	 * kiling background activities
	 */
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		mPrefrnce.clearPrefRence();
		if (via.equalsIgnoreCase("creditCard")) {
			Register.activity.finish();
			CreateProfile.activity.finish();
			CreditCardLink.activity.finish();
			TermsCondition.activity.finish();
			startActivity(new Intent(VarifyMobile.this, Login.class));
		}
		
		else if(via.equalsIgnoreCase("EditProfile")){
			startActivity(new Intent(VarifyMobile.this, Login.class));

		}
		finish();
	}
	
}
