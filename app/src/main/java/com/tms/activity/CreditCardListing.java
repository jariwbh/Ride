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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tms.R;
import com.tms.model.Credicardslisting;
import com.tms.utility.CheckInternetConnectivity;
import com.tms.utility.SendXmlAsync;
import com.tms.utility.ToroSharedPrefrnce;
import com.tms.utility.URL;
import com.tms.utility.Utility;
import com.tms.utility.UtilsSingleton;
import com.tms.utility.XmlListener;

/**
 * @author arvind.agarwal this class shows list of credit card of user
 * 
 */
public class CreditCardListing extends Activity implements OnClickListener,
		XmlListener {

	private TextView txtNoCard;
	private String cardNo="";
	private String cardType="";
	ToroSharedPrefrnce mPrefrnce;

	// objects of view class
	private ListView listCrediCard;
	private ImageView btnBack;
	private Button btnAdd1/*, btnAdd2*/;
	private AdapterForCarList adapter;

	private Credicardslisting modalCrediCardListing;
	
	//model type arraylist that contains all info of credit card

	private ArrayList<Credicardslisting> arrayListCrediCard;

	
	
	private Typeface typeFace,typeFaceHelvetica;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mPrefrnce = new ToroSharedPrefrnce(CreditCardListing.this);
		setContentView(R.layout.activity_credi_card_listing);
		
		// fetching type face path from utility class
		typeFace = Typeface.createFromAsset(getAssets(), Utility.TYPE_FACE);
		typeFaceHelvetica=Typeface.createFromAsset(getAssets(), Utility.TYPE_FACE_HELVETICA);
		//setting type face on header text
		((TextView) findViewById(R.id.header_text)).setTypeface(typeFace);
		initializeView();


	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// webservice hit for credit cards list attached in user account
		

		if (!CheckInternetConnectivity.checkinternetconnection(this)) {
			showDialog("Alert !", "No Internet Connection.");
		} else {
			
			txtNoCard.setVisibility(View.GONE);
			
			arrayListCrediCard.clear();
			//webservice hit for credit card list
			new SendXmlAsync(URL.BASE_URL + URL.CREDITCARDLIST
					+ mPrefrnce.getUserId(), "", CreditCardListing.this,
					CreditCardListing.this, true).execute();
		}
	}

	/**
	 * initializing view
	 */

	private void initializeView() {
		txtNoCard=(TextView)findViewById(R.id.txtNoCard);
		txtNoCard.setVisibility(View.GONE);
		listCrediCard = (ListView) findViewById(R.id.listCreditCard);
		arrayListCrediCard = new ArrayList<Credicardslisting>();
		
		adapter = new AdapterForCarList();

		listCrediCard.setAdapter(adapter);

		btnBack = (ImageView) findViewById(R.id.btnBack);
		btnAdd1 = (Button) findViewById(R.id.btn1);
		btnAdd1.setTypeface(typeFace);
	/*	btnAdd2 = (Button) findViewById(R.id.btn2);
		btnAdd2.setTypeface(typeFace);*/

		btnBack.setOnClickListener(this);
		btnAdd1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				// direct user to add credit card screen

				startActivity(new Intent(CreditCardListing.this,
						com.tms.activity.AddCreditCard.class));

			}
		});
	/*	btnAdd2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				startActivity(new Intent(CreditCardListing.this,
						AddCreditCard.class));

			}
		});*/

	}
	
	/**
	 * 
	 * @author arvind.agarwal
	 *adapter for credit card listing
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
						.from(CreditCardListing.this);

				convertView = inflater.inflate(
						R.layout.credit_card_listing_adapter, null);

				holder.txtcardNo = (TextView) convertView
						.findViewById(R.id.txtCardNo2);
				holder.imgCardType = (ImageView) convertView
						.findViewById(R.id.imgCard);
				holder.btnNext = (ImageButton) convertView
						.findViewById(R.id.btnNext);
				holder.imgTick=(ImageView)convertView.findViewById(R.id.imgTick);

				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.imgTick.setVisibility(View.VISIBLE);
			
			holder.txtcardNo.setTypeface(typeFaceHelvetica);

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
			
			

		/*	holder.btnNext.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Intent i = new Intent(CreditCardListing.this,
							ShowCreditCardDetail.class);
					i.putExtra("CARDID", arrayListCrediCard.get(position)
							.getCardId());
					i.putExtra("CARDTYPE", arrayListCrediCard.get(position)
							.getCardType());
					i.putExtra("CARDEXPDATEMONTH",
							arrayListCrediCard.get(position)
									.getCardExpiryDateMonth());
					i.putExtra("CARDEXPDATEYEAR",
							arrayListCrediCard.get(position)
									.getCardExpiryDateYear());

					i.putExtra("CARDNO", arrayListCrediCard.get(position)
							.getCardNo());
					i.putExtra("ZIPCODE", arrayListCrediCard.get(position)
							.getZipCode());

					startActivity(i);

				}
			});*/

			
			if (arrayListCrediCard.get(position).isChecked()) {
				holder.imgTick.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.tick_black));
				
			

			} else {
				holder.imgTick.setImageBitmap(null);

			}
			holder.btnNext.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent(CreditCardListing.this,
							ShowCreditCardDetail.class);
					i.putExtra("CARDID", arrayListCrediCard.get(position)
							.getCardId());
					i.putExtra("CARDTYPE", arrayListCrediCard.get(position)
							.getCardType());
					i.putExtra("CARDEXPDATEMONTH",
							arrayListCrediCard.get(position)
									.getCardExpiryDateMonth());
					i.putExtra("CARDEXPDATEYEAR",
							arrayListCrediCard.get(position)
									.getCardExpiryDateYear());
					i.putExtra("PRIM", arrayListCrediCard.get(position)
							.getPrim());
					i.putExtra("CARDNO", arrayListCrediCard.get(position)
							.getCardNo());
					i.putExtra("ZIPCODE", arrayListCrediCard.get(position)
							.getZipCode());

					startActivity(i);

				}
			});
			
			
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
							.checkinternetconnection(CreditCardListing.this)) {

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
								xmlTOSend, CreditCardListing.this,
								CreditCardListing.this, true).execute();
						
						
						cardNo=arrayListCrediCard.get(position).getCardNo();
						cardType=arrayListCrediCard.get(position).getCardType();
					}

					

				}
			});

			return convertView;
		}
		
		
		/**
		 * 
		 * @author arvind.agarwal
		 *this class holds objects of views placed in each item of listview of credit card listing
		 */

		class ViewHolder {
			ImageView imgCardType,imgTick;
			ImageButton btnNext;
			TextView txtcardNo;

		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {

		case R.id.btnBack:

			finish();

			break;

		}

	}
	
	/**
	 * interface for handling response from server
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
		
		// response for creditcard list

		if (respose.contains("creditCardListing")) {
			try {
				mJsonObject = new JSONObject(respose);

				JSONArray mJsonArray = mJsonObject
						.getJSONArray("creditCardListing");
				if (mJsonArray.length() != 0) {

					
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

						modalCrediCardListing.setPrim(mJsonArray.getJSONObject(
								i).getString("primaryCard"));

						modalCrediCardListing.setZipCode(mJsonArray
								.getJSONObject(i).getString("zipCode"));
						if (mJsonArray.getJSONObject(i)
								.getString("primaryCard").equals("1")) {

							modalCrediCardListing.setChecked(true);
							
							mPrefrnce.setCardType(mJsonArray
								.getJSONObject(i).getString("cardType"));
							mPrefrnce.setCreditcardno(mJsonArray
								.getJSONObject(i).getString("cardNumber"));
							mPrefrnce.setExpirydateMonth(mJsonArray
									.getJSONObject(i).getString("month"));
							mPrefrnce.setExpirydateYear(mJsonArray
									.getJSONObject(i).getString("year"));
						} else {
							modalCrediCardListing.setChecked(false);
						}
						
						arrayListCrediCard.add(modalCrediCardListing);
					}
					// setting adapter in listview
			

				}else{
					
					txtNoCard.setVisibility(View.VISIBLE);
					txtNoCard.setText(mJsonObject.getString("message"));
					mPrefrnce.setCardType("");
					mPrefrnce.setCreditcardno("");
					mPrefrnce.setExpirydateMonth("");
					mPrefrnce.setExpirydateYear("");
					
					
				}
				adapter.notifyDataSetChanged();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}else if (respose.contains("changeCreditCard")) {
			// {"changeCreditCard":"1","message":"Card updated successfully."}

			try {
				mJsonObject = new JSONObject(respose);

				if (mJsonObject.getString("changeCreditCard").equals("1")) {
					
					
					UtilsSingleton.getInstance().setPaymentStatus("0");
					UtilsSingleton.getInstance().setBookingId("0");
					
					Toast.makeText(CreditCardListing.this,
							mJsonObject.getString("message"),
							Toast.LENGTH_SHORT).show();
					mPrefrnce.setCreditcardno(cardNo);
					mPrefrnce.setCardType(cardType);
					
				/*mPrefrnce.setBookingId("0");
				mPrefrnce.setPaymentStatus("0");*/
				
				
				
					
					
					
					/*finish();*/
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
	 * @param title
	 * @param message
	 * 
	 * method to show alert
	 */

	private void showDialog(String title, String message) {

		final Dialog dialog = new Dialog(CreditCardListing.this,
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

}
