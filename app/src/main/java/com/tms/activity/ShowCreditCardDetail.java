package com.tms.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.style;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
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
 *this class is used show credit cards details
 *delete card
 *make card as primary 
 */

public class ShowCreditCardDetail extends Activity implements OnClickListener,
		XmlListener {

	ToroSharedPrefrnce mSharedPreference;

	private Bundle bundle;
	private TextView txtCardNo, txtExpiryDate, txtZip, txtName;
	private Button btnEditCard, btnBack, btnDelete, btnPrimary;
	private TextView imgText;

	public static Activity activity;

	private ToroSharedPrefrnce sharedPref;

	private String cardNo, cardType;

	private RelativeLayout relativeDetail;
	Typeface typeFace, typeFaceHelveticeLight, typeFaceHelveticaMedium,
			typeFaceForgot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mSharedPreference = new ToroSharedPrefrnce(this);
		bundle = getIntent().getExtras();
		activity = this;
		setContentView(R.layout.activity_show_creditcard);

		
		// initializing shared preference
		sharedPref = new ToroSharedPrefrnce(ShowCreditCardDetail.this);
		
		// getting type face
		typeFaceForgot = Typeface.createFromAsset(getAssets(),
				Utility.TYPE_FACE_FORGOT);
		typeFace = Typeface.createFromAsset(getAssets(), Utility.TYPE_FACE);
		typeFaceHelveticeLight = Typeface.createFromAsset(getAssets(),
				Utility.TYPE_FACE_HELVATICA_NEUE_LIGHT);
		typeFaceHelveticaMedium = Typeface.createFromAsset(getAssets(),
				Utility.TYPE_FACE_HELVATICA_NEUE_MEDIUM);
		((TextView) findViewById(R.id.header_text)).setTypeface(typeFace);
		((TextView) findViewById(R.id.txtCreditCard))
				.setTypeface(typeFaceHelveticeLight);

		((TextView) findViewById(R.id.imgExpiryDate))
				.setTypeface(typeFaceHelveticaMedium);
		((TextView) findViewById(R.id.imgZip))
				.setTypeface(typeFaceHelveticaMedium);

		initializeView();
		
		
		
		
		// chack whether the card is primary or not

		if (bundle.getString("PRIM").equals("1")) {
			
		
			btnPrimary.setVisibility(View.INVISIBLE);
			
			if(Utility.cashFlow){
				
				imgText.setVisibility(View.INVISIBLE);
				btnDelete.setVisibility(View.VISIBLE);
				
				
			}else{
				
				imgText.setVisibility(View.VISIBLE);
				btnDelete.setVisibility(View.INVISIBLE);
			}

		} else {
			imgText.setVisibility(View.INVISIBLE);
			btnDelete.setVisibility(View.VISIBLE);
			
			btnPrimary.setVisibility(View.VISIBLE);
		}
		
		
		
		
		

	}
	
	//initializing views

	private void initializeView() {
		// TODO Auto-generated method stub

		btnPrimary = (Button) findViewById(R.id.btnPrimary);
		btnPrimary.setOnClickListener(this);

		btnDelete = (Button) findViewById(R.id.btnDelete);
		btnDelete.setOnClickListener(this);

		relativeDetail = (RelativeLayout) findViewById(R.id.detail_layout);

		txtCardNo = (TextView) findViewById(R.id.txtCardNo2);
		txtCardNo.setTypeface(typeFaceHelveticaMedium);

		txtZip = (TextView) findViewById(R.id.txtZip);
		txtZip.setTypeface(typeFaceHelveticaMedium);
		txtName = (TextView) findViewById(R.id.txtName);
		txtName.setTypeface(typeFaceHelveticaMedium);
		txtExpiryDate = (TextView) findViewById(R.id.txtExpiryDate);
		txtExpiryDate.setTypeface(typeFaceHelveticaMedium);
		btnBack = (Button) findViewById(R.id.btnBack);
		btnEditCard = (Button) findViewById(R.id.btnEdtCard);
		btnEditCard.setVisibility(View.GONE);
		imgText = (TextView) findViewById(R.id.imgTextInfo);
		imgText.setTypeface(typeFaceForgot);
		txtZip.setText(bundle.getString("ZIPCODE"));
		txtCardNo.setText(bundle.getString("CARDNO").substring(4));
		if (bundle.getString("CARDEXPDATEMONTH").length() == 1) {
			txtExpiryDate.setText("0" + bundle.getString("CARDEXPDATEMONTH")
					+ "/" + bundle.getString("CARDEXPDATEYEAR"));

		} else {

			txtExpiryDate.setText(bundle.getString("CARDEXPDATEMONTH") + "/"
					+ bundle.getString("CARDEXPDATEYEAR"));
		}
		txtName.setText(mSharedPreference.getFirstName() + " "
				+ mSharedPreference.getLastname());

		btnBack.setOnClickListener(this);
		btnEditCard.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.btnPrimary:
			
			showDialogPrimary("Message","Are you sure to set this card as primary card?");

			break;

		case R.id.btnDelete:
			showDialogDelete("Message",
					"Are you sure to delete this credit card?");

			break;

		case R.id.btnBack:

			finish();
			break;

		case R.id.btnEdtCard:

			Intent i = new Intent(ShowCreditCardDetail.this,
					com.tms.activity.EditCreditCard.class);
			i.putExtra("CARDID", bundle.getString("CARDID"));
			i.putExtra("CARDTYPE", bundle.getString("CARDTYPE"));
			i.putExtra("CARDEXPDATEYEAR", bundle.getString("CARDEXPDATEYEAR"));
			i.putExtra("CARDEXPDATEMONTH", bundle.getString("CARDEXPDATEMONTH"));
			i.putExtra("CARDNO", bundle.getString("CARDNO"));
			i.putExtra("ZIPCODE", bundle.getString("ZIPCODE"));
			i.putExtra("PRIM", bundle.getString("PRIM"));
			startActivity(i);
			break;

		}

	}
	
	
	//alert message

	private void showDialogDelete(String title, String message) {

		final Dialog dialog = new Dialog(ShowCreditCardDetail.this,
				style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.dialog_warning);
		TextView txtTitle = (TextView) dialog
				.findViewById(R.id.dialog_title_text);
		TextView txtMessage = (TextView) dialog
				.findViewById(R.id.dialog_message_text);

		Button btnYes = (Button) dialog.findViewById(R.id.dialog_ok_btn);
		Button btnNo = (Button) dialog.findViewById(R.id.dialog_cancel_btn);

		btnYes.setVisibility(View.VISIBLE);
		btnNo.setVisibility(View.VISIBLE);

		Button btnOk = (Button) dialog.findViewById(R.id.btnDone);
		btnOk.setVisibility(View.GONE);

		btnYes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (!CheckInternetConnectivity
						.checkinternetconnection(ShowCreditCardDetail.this)) {

					dialog.dismiss();
					showDialog("Alert!", "Check Internet Connection.");

				} else {

					new SendXmlAsync(URL.BASE_URL + URL.DELETECREDITCARD
							+ bundle.getString("CARDID") + "&userId="
							+ mSharedPreference.getUserId(), "",
							ShowCreditCardDetail.this,
							ShowCreditCardDetail.this, true).execute();

					dialog.dismiss();
				}

			}
		});

		btnNo.setOnClickListener(new OnClickListener() {

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

	@Override
	public void onResponse(String respose) {
		// TODO Auto-generated method stub
		
		
		
		// delete card response handling
		JSONObject mJsonObj;

		String message;
		if (respose.contains("deleteCard")) {
			try {

				mJsonObj = new JSONObject(respose);
				message = mJsonObj.getString("message");
				if (mJsonObj.getString("deleteCard").equals("1")) {

					
					
					
					if(!mJsonObj
							.getString("cardListArr").equals("[]")){
						mSharedPreference.setCreditcardno(mJsonObj.getJSONObject(
								"cardListArr").getString("cardNumber"));
						mSharedPreference.setCardType(mJsonObj.getJSONObject(
								"cardListArr").getString("cardType"));
					mSharedPreference.setExpirydateMonth(mJsonObj
							.getJSONObject("cardListArr").getString("month"));
					mSharedPreference.setExpirydateYear(mJsonObj.getJSONObject(
							"cardListArr").getString("year"));
					mSharedPreference.setZip(mJsonObj.getJSONObject(
							"cardListArr").getString("zipCode"));
					}

					Toast.makeText(ShowCreditCardDetail.this, message,
							Toast.LENGTH_LONG).show();
					this.finish();

				} else {
					showDialog("Alert!", message);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			// handling response for making card as primary
		} else if (respose.contains("changeCreditCard")) {
			// {"changeCreditCard":"1","message":"Card updated successfully."}

			try {
				mJsonObj = new JSONObject(respose);

				if (mJsonObj.getString("changeCreditCard").equals("1")) {

					UtilsSingleton.getInstance().setPaymentStatus("0");
					UtilsSingleton.getInstance().setBookingId("0");

					Toast.makeText(ShowCreditCardDetail.this,
							mJsonObj.getString("message"), Toast.LENGTH_SHORT)
							.show();
					sharedPref.setCreditcardno(cardNo);
					sharedPref.setCardType(cardType);
					
					btnDelete.setVisibility(View.GONE);
					btnPrimary.setVisibility(View.GONE);

					/*
					 * mPrefrnce.setBookingId("0");
					 * mPrefrnce.setPaymentStatus("0");
					 */

				} else if (mJsonObj.getString("changeCreditCard").equals("-4")) {

					showDialog("Alert!", mJsonObj.getString("message"));
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	
	/*
	 * this method is used show alert dialog
	 */
	private void showDialog(String title, String message) {

		final Dialog dialog = new Dialog(ShowCreditCardDetail.this,
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
	 * 
	 * @param title
	 * @param message
	 * 
	 * confirmation for making card primary
	 */
	private void showDialogPrimary(String title, String message) {

		final Dialog dialog = new Dialog(ShowCreditCardDetail.this,
				style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.dialog_warning);
		TextView txtTitle = (TextView) dialog
				.findViewById(R.id.dialog_title_text);
		TextView txtMessage = (TextView) dialog
				.findViewById(R.id.dialog_message_text);

		Button btnYes = (Button) dialog.findViewById(R.id.dialog_ok_btn);
		Button btnNo = (Button) dialog.findViewById(R.id.dialog_cancel_btn);

		btnYes.setVisibility(View.VISIBLE);
		btnNo.setVisibility(View.VISIBLE);

		Button btnOk = (Button) dialog.findViewById(R.id.btnDone);
		btnOk.setVisibility(View.GONE);

		btnYes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (!CheckInternetConnectivity
						.checkinternetconnection(ShowCreditCardDetail.this)) {

					dialog.dismiss();
					showDialog("Alert!", "Check Internet Connection.");

				} else {

					String xmlTOSend = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><changeCreditCard><riderId><![CDATA["
							+ sharedPref.getUserId()
							+ "]]></riderId><cardId><![CDATA["
							+ bundle.getString("CARDID")
							+ "]]></cardId><paymentStatus><![CDATA["
							+ UtilsSingleton.getInstance().getPaymentStatus()
							+ "]]></paymentStatus><bookingId><![CDATA["
							+ UtilsSingleton.getInstance().getBookingId()
							+ "]]></bookingId></changeCreditCard>";

					/* change credit card type */
					new SendXmlAsync(URL.BASE_URL + URL.CHANGE_CARD, xmlTOSend,
							ShowCreditCardDetail.this,
							ShowCreditCardDetail.this, true).execute();

					cardNo = bundle.getString("CARDNO");
					cardType = bundle.getString("CARDTYPE");

					dialog.dismiss();
				}

			}
		});

		btnNo.setOnClickListener(new OnClickListener() {

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
