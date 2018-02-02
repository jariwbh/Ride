package com.tms.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.style;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.tms.R;
import com.tms.utility.CheckInternetConnectivity;
import com.tms.utility.SendXmlAsync;
import com.tms.utility.ToroSharedPrefrnce;
import com.tms.utility.URL;
import com.tms.utility.Utility;
import com.tms.utility.XmlListener;

/**
 * Activity to use promo code
 * 
 * @author arvind.agarwal
 * 
 */
public class PromoCode extends Activity implements XmlListener {


	EditText edtCode;
	ToroSharedPrefrnce mPrefrnce;
	ImageView btnSave,btnCancel;

	Bundle bundle;
	
	
	Typeface typeFace,typeFaceNeueLight;
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_promocode);
		mPrefrnce = new ToroSharedPrefrnce(PromoCode.this);
		typeFace=Typeface.createFromAsset(getAssets(),Utility.TYPE_FACE);
		typeFaceNeueLight=Typeface.createFromAsset(getAssets(),Utility.TYPE_FACE_HELVATICA_NEUE_LIGHT);
		((TextView)findViewById(R.id.headerText)).setTypeface(typeFace);
		((TextView)findViewById(R.id.txt_info_promo)).setTypeface(typeFaceNeueLight);
		
		((RelativeLayout)findViewById(R.id.layoutPromo)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		

		initialize();
		
	
	}

	

	/**
	 * method to initialize views
	 */
	public void initialize() {
		
		btnCancel = (ImageView) findViewById(R.id.btnCancelPromo);

	
		edtCode = (EditText) findViewById(R.id.edtPromoCode);
		edtCode.requestFocus();
		edtCode.setTypeface(typeFaceNeueLight);

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(edtCode.getWindowToken(), 0);

				finish();
			}
		});
		btnSave = (ImageView) findViewById(R.id.btnSavePromo);
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (edtCode.getText().toString().trim().equalsIgnoreCase("")) {
					showDialog("Alert !", "Enter Promo code");

				} else {

					if (CheckInternetConnectivity
							.checkinternetconnection(PromoCode.this)) {

						String xmlPromo = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><promoCode><userId><![CDATA["
								+ mPrefrnce.getUserId()
								+ "]]></userId><promoCode><![CDATA["
								+ edtCode.getText().toString()
								+ "]]></promoCode></promoCode>";
						new SendXmlAsync(URL.BASE_URL + URL.PROMO_CODE,
								xmlPromo, PromoCode.this, PromoCode.this, true)
								.execute();
					} else {
						Toast.makeText(PromoCode.this, "No Internet",
								Toast.LENGTH_LONG).show();
					}
				}
			}
		});
		
		edtCode.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

					
					if (edtCode.getText().toString().trim().equalsIgnoreCase("")) {
						showDialog("Alert !", "Enter Promo code");

					} else {

						if (CheckInternetConnectivity
								.checkinternetconnection(PromoCode.this)) {

							String xmlPromo = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><promoCode><userId><![CDATA["
									+ mPrefrnce.getUserId()
									+ "]]></userId><promoCode><![CDATA["
									+ edtCode.getText().toString()
									+ "]]></promoCode></promoCode>";
							new SendXmlAsync(URL.BASE_URL + URL.PROMO_CODE,
									xmlPromo, PromoCode.this, PromoCode.this, true)
									.execute();
						} else {
							Toast.makeText(PromoCode.this, "No Internet",
									Toast.LENGTH_LONG).show();
						}
					}
					
					return true;
				
	            }    
	            return false;
				
			}
		});
		
		/*edtCode.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				
				if(keyCode==KeyEvent.KEYCODE_ENTER){
					
					if (edtCode.getText().toString().trim().equalsIgnoreCase("")) {
						showDialog("Alert !", "Enter Promo code");

					} else {

						if (CheckInternetConnectivity
								.checkinternetconnection(PromoCode.this)) {

							String xmlPromo = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><promoCode><userId><![CDATA["
									+ mPrefrnce.getUserId()
									+ "]]></userId><promoCode><![CDATA["
									+ edtCode.getText().toString()
									+ "]]></promoCode></promoCode>";
							new SendXmlAsync(URL.BASE_URL + URL.PROMO_CODE,
									xmlPromo, PromoCode.this, PromoCode.this, true)
									.execute();
						} else {
							Toast.makeText(PromoCode.this, "No Internet",
									Toast.LENGTH_LONG).show();
						}
					}
					
					return true;
				}
				return false;
			}
		});
	*/
	
	}

	@Override
	public void onResponse(String respose) {
		// TODO Auto-generated method stub

		Log.i("response", respose);
		
	//	{"getPromoCodeListing":[{"id":"1","promoCodeId":"4","promoCode":"o782i","amount":"12%"}],"message":"You have promo codes."}

	

			try {
				JSONObject mJsonObject = new JSONObject(respose);
				String resp = mJsonObject.getString("promoCode");
				String message = mJsonObject.getString("message");
				if (resp.equalsIgnoreCase("1")) {
				/*	Toast.makeText(PromoCode.this, message, Toast.LENGTH_LONG)
							.show();
					finish();*/
					
					
					
					
					
					showDialog1("Message", message);

				} else {
					showDialog("Alert !", message);
				
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	

	/**
	 * Dialog to show alerts
	 * 
	 * @param title
	 *            setting title of dialog
	 * @param message
	 *            setting message
	 */

	private void showDialog(String title, String message) {

		final Dialog dialog = new Dialog(PromoCode.this,
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
				edtCode.setText("");
				edtCode.requestFocus();
				
				
			
			}
		});

		txtTitle.setText(title);
		txtMessage.setText(message);

		dialog.show();

	}
	
	private void showDialog1(String title, String message) {

		final Dialog dialog = new Dialog(PromoCode.this,
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
				edtCode.setText("");
				edtCode.requestFocus();
				
				/*if(com.tms.activity.Home.mNav!=null){
					//com.tms.activity.Home.mNav.close();
				}*/
				/*if(DriverDetails.mNav!=null){
					DriverDetails.mNav.close();
				}
				*/
				
				if(PromoCodeListing.promoCodeInstance!=null){
					
					PromoCodeListing.promoCodeInstance.finish();
					
				}
				
				finish();
				
				
				
			
			}
		});

		txtTitle.setText(title);
		txtMessage.setText(message);

		dialog.show();

	}
}
