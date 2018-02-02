package com.tms.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.style;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tms.R;
import com.tms.model.Credicardslisting;
import com.tms.newui.MainActivity4;
import com.tms.utility.CheckInternetConnectivity;
import com.tms.utility.ResponseListener;
import com.tms.utility.SendXmlAsync;
import com.tms.utility.ToroSharedPrefrnce;
import com.tms.utility.URL;
import com.tms.utility.Utility;
import com.tms.utility.UtilsSingleton;
import com.tms.utility.XmlListener;
/**
 * 
 * @author arvind.agarwal
 * 
 * this view represents list of credit cards attached in user profile
 *
 */
public class ChangeCardType extends Activity implements OnClickListener,
		XmlListener, ResponseListener {

	ToroSharedPrefrnce mPrefrnce;

	// objects of view class
	private ListView listCrediCard;
	private Button btnBack;
	private AdapterForCarList adapter;

	private Credicardslisting modalCrediCardListing;
	
	private String cardNo="";
	private String cardType="";

	
	//model kind arraylist that contains info of credit cards
	private ArrayList<Credicardslisting> arrayListCrediCard;
	private Button btnAdd1/* btnAdd2*/;

	private Typeface typeFace;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mPrefrnce = new ToroSharedPrefrnce(ChangeCardType.this);
		setContentView(R.layout.activity_change_card);
		
		// getting type face from utility
		typeFace = Typeface.createFromAsset(getAssets(), Utility.TYPE_FACE);
		((TextView) findViewById(R.id.header_text)).setTypeface(typeFace);
		initializeView();


	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// the parameter pending  comes from home screen 
		// if pending ==1, this means there is pending payment that needs to be clear by selecting a credit card
		
		
		
		if(getIntent().getStringExtra("pending").equals("1")){
			
			
			showDialog("Alert!", " Your last payment had been declined.Please select other credit card or add new credit card.");
		}

		if (!CheckInternetConnectivity.checkinternetconnection(this)) {
			showDialog("Alert !", "No Internet Connection.");
		} else {
			
			//webservice hit to get list of credit cards
			new SendXmlAsync(URL.BASE_URL + URL.CREDITCARDLIST
					+ mPrefrnce.getUserId(), "", ChangeCardType.this,
					ChangeCardType.this, true).execute();
		}
	}

	/**
	 * initializing view
	 */

	private void initializeView() {
		
		btnAdd1 = (Button) findViewById(R.id.btn1);
		btnAdd1.setTypeface(typeFace);
	
		
		btnAdd1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				
				
				//intent to add credit card view
				startActivity(new Intent(ChangeCardType.this,
						com.tms.activity.AddCreditCard.class));

			}
		});
	
		
		listCrediCard = (ListView) findViewById(R.id.listCreditCard);

		btnBack = (Button) findViewById(R.id.btnBack);
		

		btnBack.setOnClickListener(this);
	

	}

	
	/**
	 * 
	 * @author arvind.agarwal
	 *adapter to set on credit card list
	 */
	private class AdapterForCarList extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrayListCrediCard.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = new ViewHolder();

			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater
						.from(ChangeCardType.this);

				convertView = inflater.inflate(
						R.layout.change_credit_card_adapter, null);

				holder.txtcardNo = (TextView) convertView
						.findViewById(R.id.txtCardNo2);
				holder.imgCardType = (ImageView) convertView
						.findViewById(R.id.imgCard);
				holder.btnNext = (ImageView) convertView
						.findViewById(R.id.btnNext);

				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			
			// represnting what type of card
			if (arrayListCrediCard.get(position).getCardType().equals("Visa")) {

				holder.imgCardType.setImageResource(R.drawable.icon_cc_visa);

			} else if (arrayListCrediCard.get(position).getCardType()
					.equals("Master")) {
				holder.imgCardType
						.setImageResource(R.drawable.icon_cc_master_card);
			} else if (arrayListCrediCard.get(position).getCardType()
					.equals("AmEx")) {
				holder.imgCardType
						.setImageResource(R.drawable.icon_cc_american_excepress);
			} else if (arrayListCrediCard.get(position).getCardType()
					.equals("Discover")) {
				holder.imgCardType
						.setImageResource(R.drawable.icon_cc_discover);

			}
			holder.txtcardNo.setText(arrayListCrediCard.get(position)
					.getCardNo().substring(4));

			
			// if card is selcted or not
			if (arrayListCrediCard.get(position).isChecked()) {
				holder.btnNext.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.tick));

			} else {
				holder.btnNext.setImageBitmap(null);

			}

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				/*	mPrefrnce.setCreditcardno(arrayListCrediCard.get(position)
							.getCardNo());
					mPrefrnce.setCardType(arrayListCrediCard.get(position)
							.getCardType());*/

					for (int i = 0; i < arrayListCrediCard.size(); i++) {

						arrayListCrediCard.get(i).setChecked(false);
					}

					arrayListCrediCard.get(position).setChecked(true);
					adapter.notifyDataSetChanged();

					if (CheckInternetConnectivity
							.checkinternetconnection(ChangeCardType.this)) {

						String xmlTOSend = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><changeCreditCard><riderId><![CDATA["
								+ mPrefrnce.getUserId()
								+ "]]></riderId><cardId><![CDATA["
								+ arrayListCrediCard.get(position).getCardId()
								+ "]]></cardId><paymentStatus><![CDATA["
								+ UtilsSingleton.getInstance().getPaymentStatus()
								+ "]]></paymentStatus><bookingId><![CDATA["
								+ UtilsSingleton.getInstance().getBookingId()
								+ "]]></bookingId></changeCreditCard>";
						
						/*change credit card type*/
						new SendXmlAsync(URL.BASE_URL + URL.CHANGE_CARD,
								xmlTOSend, ChangeCardType.this,
								ChangeCardType.this, true).execute();
						
						
						cardNo=arrayListCrediCard.get(position).getCardNo();
						cardType=arrayListCrediCard.get(position).getCardType();
					}

					

				}
			});

			return convertView;
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {

		case R.id.btnBack:
			
			if(!UtilsSingleton.getInstance().getPaymentStatus().equals("0") && !UtilsSingleton.getInstance().getBookingId().equals("0")){
				
				//if payment status !=0 and booking id!=0, so that means there is some pending payment corresponding to booking id
				//so it will never let the user go back to home screen until the payment is not get cleared
						
				showDialog("Alert!", " Your last payment had been declined.Please select other credit card or add new credit card.");
			}else{
			finish();
			}

			break;
		

		}

	}
	/**
	 * interface to handle response from webservices hit
	 */

	@Override
	public void onResponse(String respose) {
		// TODO Auto-generated method stub
		/*
		 * {"creditCardListing":[{"cardId":"2","cardNumber":"4417123456789113",
		 * "cardType"
		 * :"Visa","month":"4","year":"15"}],"message":"User has cards."}
		 */

		JSONObject mJsonObject;

		if (respose.contains("creditCardListing")) {
			try {
				mJsonObject = new JSONObject(respose);

				JSONArray mJsonArray = mJsonObject
						.getJSONArray("creditCardListing");
				if (mJsonArray.length() != 0) {

					arrayListCrediCard = new ArrayList<Credicardslisting>();
					for (int i = 0; i < mJsonArray.length(); i++) {

						modalCrediCardListing = new Credicardslisting();
						modalCrediCardListing.setCardId(mJsonArray
								.getJSONObject(i).getString("cardId"));
						modalCrediCardListing.setCardType(mJsonArray
								.getJSONObject(i).getString("cardType"));
						modalCrediCardListing.setCardNo(mJsonArray
								.getJSONObject(i).getString("cardNumber"));
						modalCrediCardListing.setCardExpiryDateMonth(mJsonArray
								.getJSONObject(i).getString("month"));
						modalCrediCardListing.setCardExpiryDateYear(mJsonArray
								.getJSONObject(i).getString("year"));

						if (mJsonArray.getJSONObject(i)
								.getString("primaryCard").equals("1")) {

							modalCrediCardListing.setChecked(true);

						} else {
							modalCrediCardListing.setChecked(false);
						}

						modalCrediCardListing.setZipCode(mJsonArray
								.getJSONObject(i).getString("zipCode"));

						arrayListCrediCard.add(modalCrediCardListing);
					}
					// setting adapter in listview
					adapter = new AdapterForCarList();

					listCrediCard.setAdapter(adapter);

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (respose.contains("changeCreditCard")) {
			// {"changeCreditCard":"1","message":"Card updated successfully."}

			try {
				mJsonObject = new JSONObject(respose);

				if (mJsonObject.getString("changeCreditCard").equals("1")) {
					
					
					UtilsSingleton.getInstance().setPaymentStatus("0");
					UtilsSingleton.getInstance().setBookingId("0");
					
					Toast.makeText(ChangeCardType.this,
							mJsonObject.getString("message"),
							Toast.LENGTH_SHORT).show();
					mPrefrnce.setCreditcardno(cardNo);
					mPrefrnce.setCardType(cardType);
					
				/*mPrefrnce.setBookingId("0");
				mPrefrnce.setPaymentStatus("0");*/
				
				
				
					startActivity(new Intent(ChangeCardType.this, MainActivity4.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
							| Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP));
				
					
					
					finish();
				}else if(mJsonObject.getString("changeCreditCard").equals("-4")){
					
					
					showDialog("Alert!",mJsonObject.getString("message") );
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
	
	/**
	 * 
	 * @author arvind.agarwal
	 *View holder class 
	 *that holds objects in each item of listview(card list)
	 *
	 */

	class ViewHolder {
		ImageView imgCardType;
		ImageView btnNext;
		TextView txtcardNo;

	}
	
	/**
	 * 
	 * @param title
	 * @param message
	 * dialog to show
	 * 
	 */

	private void showDialog(String title, String message) {

		final Dialog dialog = new Dialog(ChangeCardType.this,
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
	public void response(String strresponse, String webservice) {
		// TODO Auto-generated method stub

		Log.i("response", strresponse);

	}

	/**
	 * handling back button press
	 * 
	 */

	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//if payment status !=0 and booking id!=0, so that means there is some pending payment corresponding to booking id
		//so it will never let the user go back to home screen until the payment is not get cleared
				
		
		if(!UtilsSingleton.getInstance().getPaymentStatus().equals("0") && !UtilsSingleton.getInstance().getBookingId().equals("0")){
			
			
			showDialog("Alert!", " Your last payment had been declined.Please select other credit card or add new credit card.");
		}else{
			super.onBackPressed();
		}
		
	}
}
