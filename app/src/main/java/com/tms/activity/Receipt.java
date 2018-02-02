package com.tms.activity;

import android.R.style;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

//import com.navdrawer.SimpleSideDrawer;
import com.squareup.picasso.Picasso;
import com.tms.R;
import com.tms.SharingToro;
import com.tms.newui.MainActivity4;
import com.tms.utility.SendXmlAsync;
import com.tms.utility.ToroSharedPrefrnce;
import com.tms.utility.URL;
import com.tms.utility.Utility;
import com.tms.utility.XmlListener;

import org.json.JSONException;
import org.json.JSONObject;

public class Receipt extends Activity implements XmlListener {
	
	
	/**
	 * @author arvind.agarwal
	 */
	Intent mIntent;
	TextView txtRating, txtUserName, txtPhone, txtTaxiModel, txtTaxiNo,
			txtDate, txtFair, imgReceipt_text2,txtRatingtType;
	ImageView driverImage, carIcon;
	RatingBar mRatingBar;
	Button btnSubmit;
	ToroSharedPrefrnce mPrefrnce;
	EditText edtComments;
	//public static SimpleSideDrawer mNav;

	public static Activity ratingActivity;
	private Typeface typeFaceItalic, typeFace;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		ratingActivity = this;

		if (DriverDetails.driverDetils != null) {
			DriverDetails.driverDetils.finish();

		}
		setContentView(R.layout.activity_your_reciept);
		
		// getting typeface
		typeFace = Typeface
				.createFromAsset(getAssets(), Utility.TYPE_FACE);
		
		
		typeFaceItalic = Typeface.createFromAsset(getAssets(),
				Utility.TYPE_FACE_FORGOT);
    	((TextView)findViewById(R.id.headerText)).setTypeface(typeFace);

		imgReceipt_text2 = (TextView) findViewById(R.id.imgReceipt_text2);
		imgReceipt_text2.setTypeface(typeFaceItalic);

		((Button) findViewById(R.id.btnToroWebLink))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent i = new Intent(Intent.ACTION_SEND);

						i.setType("message/rfc822");
						/*
						 * i.setClassName("com.google.android.gm",
						 * "com.google.android.gm.ComposeActivityGmail");
						 */
						i.putExtra(Intent.EXTRA_EMAIL,
								new String[] { "info@taximobilesolutions.com" });

						startActivity(i);

					}
				});

		
		// initializing shared preference object
		mPrefrnce = new ToroSharedPrefrnce(Receipt.this);
		
		/*if(!mPrefrnce.getPaymentViaCreditCard()){
			imgReceipt_text2.setText("The amount above will be charged by cash and a receipt for the charge was sent to your registered email address");
		}*/
		mIntent = getIntent();
		initialize();

		//navDrawer();

		
		//setting values in text fields
		
		if(mIntent.getExtras().getString("payVia").equals("0")){
			
			imgReceipt_text2.setText(getResources().getString(R.string.payvia0));
			
		}else{
			
			imgReceipt_text2.setText(getResources().getString(R.string.payvia1));

		}
		
		txtPhone.setText(mIntent.getExtras().getString("driverPhone"));
		txtRating.setText(mIntent.getExtras().getString("rating"));
		txtTaxiModel.setText(mIntent.getExtras().getString("taxiModel"));
		txtTaxiNo.setText(mIntent.getExtras().getString("taxiNumber"));
		txtUserName.setText(mIntent.getExtras().getString("driverName"));
		txtDate.setText(mIntent.getExtras().getString("completedDate"));
		if (!mIntent.getExtras().getString("tripAmount")
				.equalsIgnoreCase("null")) {
			txtFair.setText( mIntent.getExtras().getString("tripAmount"));
		} else {
			txtFair.setText("$ " + "N/A");
		}
		if (mIntent.getExtras().getString("taxiType").equalsIgnoreCase("1")) {
			carIcon.setBackgroundResource(R.drawable.icon_blackcar_grey);
		} else if (mIntent.getExtras().getString("taxiType")
				.equalsIgnoreCase("2")) {
		
			carIcon.setBackgroundResource(R.drawable.icon_mini_grey);
		} else if (mIntent.getExtras().getString("taxiType")
				.equalsIgnoreCase("3")) {
			carIcon.setBackgroundResource(R.drawable.icon_mini_grey);
		} else if (mIntent.getExtras().getString("taxiType")
				.equalsIgnoreCase("4")) {
			carIcon.setBackgroundResource(R.drawable.icon_suvcar_grey);
		}
		
		
		
		
		/*if(mIntent.getStringExtra("payvia").equals("0")){
			
			
		}else{
			
			
			
		}*/
		/*
		 * ImageUtils.getInstance(Receipt.this).setImageUrlToView(
		 * mIntent.getExtras().getString("driverImage"), driverImage, null, 0,
		 * false, true, 0, 0, false);
		 */

		if (!mIntent.getExtras().getString("driverImage").equals("")) {
			Picasso.with(Receipt.this)
					.load(mIntent.getExtras().getString("driverImage"))
					.resize(70, 70).centerCrop().into(driverImage);
		}
	}
	
	
	/**
	 * initializing view
	 */

	public void initialize() {
		txtRatingtType=(TextView)findViewById(R.id.txtRatingtType);
		
		txtRating = (TextView) findViewById(R.id.txtRating);
		txtUserName = (TextView) findViewById(R.id.txtDriverName);
		txtUserName.setTypeface(typeFace);
		txtPhone = (TextView) findViewById(R.id.txtDriverPhone);
		txtPhone.setTypeface(typeFace);
		txtRating = (TextView) findViewById(R.id.txtRating);

		txtTaxiModel = (TextView) findViewById(R.id.txtCompanyName);
		txtTaxiModel.setTypeface(typeFace);
		txtTaxiNo = (TextView) findViewById(R.id.txtTaxiNo);
		txtTaxiNo.setTypeface(typeFace);
		driverImage = (ImageView) findViewById(R.id.imgDriverPic);
		txtDate = (TextView) findViewById(R.id.txtDate);
		txtDate.setTypeface(typeFace);
		txtFair = (TextView) findViewById(R.id.txtFare);
		txtFair.setTypeface(typeFace);
		carIcon = (ImageView) findViewById(R.id.imgCarIcon);
		mRatingBar = (RatingBar) findViewById(R.id.ratingBarDriver);
		mRatingBar.setStepSize(1);
		mRatingBar.setRating(3.0f);
		txtRatingtType.setText("OK");
		
		edtComments = (EditText) findViewById(R.id.edtComments);
		edtComments.setTypeface(typeFace);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnSubmit.setTypeface(typeFace);
		/*edtComments.setEnabled(false);
		btnSubmit.setEnabled(false);*/

		mRatingBar
				.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

					@Override
					public void onRatingChanged(RatingBar ratingBar,
							float rating, boolean fromUser) {
						// TODO Auto-generated method stub
						
						if(rating==1.0f){
							
							txtRatingtType.setText("POOR");
						}else if(rating==2.0f){
							
							txtRatingtType.setText("FAIR");
						}else if(rating==3.0f){
							
							txtRatingtType.setText("OK");
						}else if(rating==4.0f){
							
							txtRatingtType.setText("GOOD");
						}else if(rating==5.0f){
							
							txtRatingtType.setText("GREAT");
						}
						
						edtComments.setEnabled(true);
						btnSubmit.setEnabled(true);

					}
				});
		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*String  ratingStr = String
						.valueOf(mRatingBar.getRating());*/
				
				float valueRating = mRatingBar.getRating();
				if(valueRating<1.0f){
					showDialogForRating("Alert !", getString(R.string.rating_alert));
				}else{
					String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><driverRating><bookingId><![CDATA["
							+ mIntent.getExtras().getString("bookingId")
							+ "]]></bookingId><driverId><![CDATA["
							+ mIntent.getExtras().getString("id")
							+ "]]></driverId><Id><![CDATA["
							+ mPrefrnce.getUserId()
							+ "]]></Id><rating><![CDATA["
							+ mRatingBar.getRating()
							+ "]]></rating><comment><![CDATA["
							+ edtComments.getText().toString().trim()
							+ "]]></comment></driverRating>";
					new SendXmlAsync(URL.BASE_URL + URL.RATING_DRIVER, xml,
							Receipt.this, Receipt.this, true).execute();
				}
				
				
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

	}

	
	/*
	 * (non-Javadoc)
	 * @see com.ontimecab.utility.XmlListener#onResponse(java.lang.String)
	 * 
	 */
	@Override
	public void onResponse(String respose) {
		// TODO Auto-generated method stub
		try {
			JSONObject mJsonObject = new JSONObject(respose);

			/*
			 * showDialog("Message", mJsonObject.getString("message"));
			 */startActivity(new Intent(Receipt.this,MainActivity4.class));
			finish();

			Toast.makeText(Receipt.this, mJsonObject.getString("message"),
					Toast.LENGTH_SHORT).show();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void showDialog(String title, String message) {

		final Dialog dialog = new Dialog(Receipt.this,
				style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.dialog_warning);
		TextView txtTitle = (TextView) dialog
				.findViewById(R.id.dialog_title_text);
		TextView txtMessage = (TextView) dialog
				.findViewById(R.id.dialog_message_text);

		txtTitle.setText(title);
		txtMessage.setText(message);

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

				startActivity(new Intent(Receipt.this, MainActivity4.class));
				finish();

			}
		});

		dialog.show();

	}
	
	
	// dialog for rating
	
	private void showDialogForRating(String title, String message) {

		final Dialog dialog = new Dialog(Receipt.this,
				style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.dialog_warning);
		TextView txtTitle = (TextView) dialog
				.findViewById(R.id.dialog_title_text);
		TextView txtMessage = (TextView) dialog
				.findViewById(R.id.dialog_message_text);

		txtTitle.setText(title);
		txtMessage.setText(message);

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

		dialog.show();

	}
	

	private void navDrawer() {

		/*mNav = new SimpleSideDrawer(this);

		mNav.setLeftBehindContentView(R.layout.activity_behind_left_simple);
		mNav.setDrawingCacheEnabled(true);
		findViewById(R.id.btnDrawer).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				mNav.toggleLeftDrawer();
				mNav.clearFocus();

				*//* edtPickUpAddress.bringToFront(); *//*

				// onPause();
			}
		});
*/
		// if (mNav.isActivated()) {

		findViewById(R.id.btnMyProfile).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(Receipt.this, MyProfile.class));
					}
				});
		findViewById(R.id.btnCreditcard).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(Receipt.this,
								CreditCardListing.class));
					}
				});
		findViewById(R.id.btnPromoCode).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(Receipt.this,
								PromoCodeListing.class).putExtra("what", 1));
					}
				});
		findViewById(R.id.btnReferFrnd).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(Receipt.this,
								SharingToro.class));

					}
				});
		findViewById(R.id.btnAboutUs).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent(Receipt.this, AboutUs.class));
			}
		});

		findViewById(R.id.btnContactUs).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent i = new Intent(Intent.ACTION_SEND);

						i.setType("message/rfc822");
						/*
						 * i.setClassName("com.google.android.gm",
						 * "com.google.android.gm.ComposeActivityGmail");
						 */
						i.putExtra(Intent.EXTRA_EMAIL,
								new String[] { "info@taximobilesolutions.com" });

						startActivity(i);

					}
				});

		findViewById(R.id.btnReservation).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						/*startActivity(new Intent(Receipt.this,
								Reservation.class));*/

					}
				});

		findViewById(R.id.btnBack).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				/*mNav.close();
				mNav.clearFocus();*/

				/* edtPickUpAddress.bringToFront(); */

				// onPause();
			}
		});

	}
}
