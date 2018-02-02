package com.tms.activity;

import android.R.style;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.tms.R;
import com.tms.utility.CheckInternetConnectivity;
import com.tms.utility.EmailValidity;
import com.tms.utility.SendXmlAsync;
import com.tms.utility.URL;
import com.tms.utility.Utility;
import com.tms.utility.XmlListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author arvind.agarwal this class is used to recieve password
 */
public class ForgottPassword extends Activity implements OnClickListener,
		XmlListener {

	// objects of view class
	private Button btnBack, btnSendPassword;

	private EditText edtEmail;
	private Typeface typeFace,typeFace2;
	
	private ImageView selectorEmail;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgott_password);
		typeFace=Typeface.createFromAsset(getAssets(),Utility.TYPE_FACE);
		typeFace2=Typeface.createFromAsset(getAssets(),Utility.TYPE_FACE_FORGOT);
		((TextView)findViewById(R.id.txtHeader)).setTypeface(typeFace);
		((TextView)findViewById(R.id.txtInfo)).setTypeface(typeFace2);
		initializeViews();
	}

	/**
	 * Initializing view
	 */
	public void initializeViews() {
		
		selectorEmail = (ImageView) findViewById(R.id.selector_email);
		btnBack = (Button) findViewById(R.id.btnBack);

		btnBack.setOnClickListener(this);

		
		btnSendPassword = (Button) findViewById(R.id.btnSendNew);
		btnSendPassword.setOnClickListener(this);

		edtEmail = (EditText) findViewById(R.id.edtEmail);
		edtEmail.setTypeface(typeFace);
		
		
		edtEmail.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				
				if(hasFocus){
					selectorEmail.setBackgroundResource(R.drawable.background_text_field_selected);
					// imgEmail.setBackgroundResource(R.drawable.mail_icon);
					
				
				}else{
					selectorEmail.setBackgroundResource(R.drawable.background_text_field);
					// imgEmail.setBackgroundResource(R.drawable.mail_icon);
					
				}
				
			}
		});
		
		edtEmail.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				
				if (!CheckInternetConnectivity
						.checkinternetconnection(ForgottPassword.this)) {
					showDialog("Alert !", "Check Internet Connection.");
				} else if (!EmailValidity.isEmailValid(edtEmail.getText()
						.toString())) {
					edtEmail.setError("Invalid Email");
				} else {

					String xmlRecoverPassword = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><forgotPassword><email><![CDATA["+edtEmail.getText().toString().trim()+"]]></email></forgotPassword>";
					new SendXmlAsync(URL.BASE_URL + URL.FORGOTPASSWORD,
							xmlRecoverPassword, ForgottPassword.this,
							ForgottPassword.this, true).execute();
				}
				return false;
			}
		});
		
		/*edtEmail.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

				if (edtEmail.getText().length() > 0) {
					// edtEmail.setBackgroundResource(R.drawable.text_field_blank);
					edtEmail.setBackgroundResource(R.drawable.text_field_blank_slctd);
					// imgEmail.setBackgroundResource(R.drawable.mail_icon);
					imgEmail.setBackgroundResource(R.drawable.mail_iconhvr);
				} else {
					edtEmail.setBackgroundResource(R.drawable.text_field_blank_black);
					// imgEmail.setBackgroundResource(R.drawable.mail_icon);
					imgEmail.setBackgroundResource(R.drawable.mail_icon);
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
		});*/
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnBack:
			finish();
			break;

		case R.id.btnSendNew:
			
			//this method is called to retrive password 
			if (!CheckInternetConnectivity
					.checkinternetconnection(ForgottPassword.this)) {
				showDialog("Alert !", "Check Internet Connection.");
			} else if (!EmailValidity.isEmailValid(edtEmail.getText()
					.toString())) {
				edtEmail.setError("Invalid Email");
				//showDialog("Alert!", "Invalid Email");
			
			} else {

				String xmlRecoverPassword = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><forgotPassword><email><![CDATA["+edtEmail.getText().toString().trim()+"]]></email></forgotPassword>";
				new SendXmlAsync(URL.BASE_URL + URL.FORGOTPASSWORD,
						xmlRecoverPassword, ForgottPassword.this,
						ForgottPassword.this, true).execute();
			}
			break;

		default:
			break;
		}

	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ontimecab.utility.XmlListener#onResponse(java.lang.String)
	 * 
	 * this method is use to tackle webservice response 
	 */

	@Override
	public void onResponse(String respose) {
		// TODO Auto-generated method stub

		try {
			JSONObject jsonObject = new JSONObject(respose);

			String forgotPassword = jsonObject.getString("forgotPassword");
			
			if (forgotPassword.equalsIgnoreCase("1")) {
				Toast.makeText(ForgottPassword.this,
						jsonObject.getString("message"), Toast.LENGTH_SHORT)
						.show();
				finish();
			} else {
				
				showDialog("Message", jsonObject.getString("message"));
			/*	Toast.makeText(Forg
						ottPassword.this,
						jsonObject.getString("message"), Toast.LENGTH_LONG)
						.show();*/
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	/*
	 * this method is useful to show Alert message 
	 */
	private void showDialog(String title, String message) {

		final Dialog dialog = new Dialog(ForgottPassword.this,style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.dialog_warning);
		TextView txtTitle = (TextView) dialog
				.findViewById(R.id.dialog_title_text);
		TextView txtMessage = (TextView) dialog
				.findViewById(R.id.dialog_message_text);

		Button btnYes = (Button) dialog.findViewById(R.id.dialog_ok_btn);
		Button btnNo = (Button) dialog.findViewById(R.id.dialog_cancel_btn);
		btnYes.setVisibility(View.GONE);
		btnNo.setVisibility(View.GONE);
		
		Button btnOk=(Button)dialog.findViewById(R.id.btnDone);
		
		btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				dialog.dismiss();
				
			}
		});
		
		

		txtTitle.setText(title);
		txtMessage.setText(message);

		dialog.show();

	}
	
	
}
