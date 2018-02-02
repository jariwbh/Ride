package com.tms.activity;

import java.util.ArrayList;
import java.util.Calendar;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.WheelViewAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.style;
import android.app.Activity;
import android.app.Dialog;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class EditCreditCard extends Activity implements OnClickListener,
		XmlListener {
	
	
	
	/**
	 * @author arvind.agarwal
	 * 
	 * this view is used to edit card(cvv, exp date, zip code)
	 */

	ToroSharedPrefrnce mSharedPreference;
	private Bundle bundle;
	private TextView txtCardNo;
	
	private  Dialog mDialog;

	private EditText  edtZip,  edtCvv;
	
	private Button edtExpiryDate;
	private Button btnSave, btnCancel, btnDelete;
	private RelativeLayout relativeDetail;
	
	private ArrayList<Integer> arrayListMonth = new ArrayList<Integer>();

	private String mMonth, mYear;
	private TextView txtName;
	private TextView imgText;
	
	Typeface typeFace,typeFaceHelveticaMedium,typeFaceForgot,typeFaceHelveticeLight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mSharedPreference = new ToroSharedPrefrnce(this);
		bundle = getIntent().getExtras();
		setContentView(R.layout.activity_edit_card);
		typeFaceForgot = Typeface.createFromAsset(getAssets(),
				Utility.TYPE_FACE_FORGOT);
		
		// fetching type face from utility class
		typeFaceHelveticeLight=Typeface.createFromAsset(getAssets(), Utility.TYPE_FACE_HELVATICA_NEUE_LIGHT);
		typeFaceHelveticaMedium=Typeface.createFromAsset(getAssets(), Utility.TYPE_FACE_HELVATICA_NEUE_MEDIUM);

		
		typeFace=Typeface.createFromAsset(getAssets(),Utility.TYPE_FACE);
		
		
		//setting type face on text views
		
		((TextView)findViewById(R.id.header_text)).setTypeface(typeFace);
		((TextView)findViewById(R.id.txtCreditCard)).setTypeface(typeFaceHelveticeLight);
		
		((TextView)findViewById(R.id.imgExpiryDate)).setTypeface(typeFaceHelveticaMedium);
		((TextView)findViewById(R.id.imgZip)).setTypeface(typeFaceHelveticaMedium);
		((TextView)findViewById(R.id.imgCvv)).setTypeface(typeFaceHelveticaMedium);
		mMonth=bundle.getString("CARDEXPDATEMONTH");
		mYear=bundle.getString("CARDEXPDATEYEAR");

		initializeView();
		
		// check whether the card is primary or not
		if(bundle.getString("PRIM").equals("1")){
			imgText.setVisibility(View.VISIBLE);
			btnDelete.setVisibility(View.INVISIBLE);
			
		}else{
			imgText.setVisibility(View.INVISIBLE);
			btnDelete.setVisibility(View.VISIBLE);
		}
		
		
	}

	
	
	/*initializing view
	 * 
	 */
	private void initializeView() {
		// TODO Auto-generated method stub

		relativeDetail = (RelativeLayout) findViewById(R.id.detail_layout);
		

		txtCardNo = (TextView) findViewById(R.id.txtCardNo2);
		txtCardNo.setTypeface(typeFaceHelveticaMedium);
		edtZip = (EditText) findViewById(R.id.txtZip);
		edtZip.setTypeface(typeFaceHelveticaMedium);
		txtName = (TextView) findViewById(R.id.txtName);
		txtName.setTypeface(typeFaceHelveticaMedium);
		edtExpiryDate = (Button) findViewById(R.id.txtExpiryDate);
		edtExpiryDate.setTypeface(typeFaceHelveticaMedium);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnSave = (Button) findViewById(R.id.btnSave);
		btnDelete = (Button) findViewById(R.id.btnDelete);
		edtCvv = (EditText) findViewById(R.id.edtCvv);
		edtCvv.setTypeface(typeFaceHelveticaMedium);
		imgText=(TextView)findViewById(R.id.imgTextInfo);
		imgText.setTypeface(typeFaceForgot);
		edtExpiryDate.setOnClickListener(this);

		
		// getting zip code
		edtZip.setText(bundle.getString("ZIPCODE"));
		txtCardNo.setText(bundle.getString("CARDNO").substring(4));
		
		
		//setting month 
		if(mMonth.length()==1){
		edtExpiryDate.setText("0"+mMonth+"/"+mYear);
		}else{
			edtExpiryDate.setText(mMonth+"/"+mYear);
			
		}
		
		
		// setting name on credit card
		txtName.setText(mSharedPreference.getFirstName() + " "
				+ mSharedPreference.getLastname());

		btnSave.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
		
		
		
		

		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.btnCancel:

			finish();
			break;
		case R.id.btnSave:
			
			
			// saving card details
		if	(!CheckInternetConnectivity.checkinternetconnection(EditCreditCard.this)){
			showDialog("Alert!", "Check Internet Connection.");
			
		}else if(edtExpiryDate.getText().toString().equals("")){
			showDialog("Alert!", "Enter Expiry Date");
			
		}
		else if(edtZip.getText().toString().equals("")){
			showDialog("Alert!", "Enter Zip Code ");
		}else if(edtZip.getText().length()<5){
			showDialog("Error","Enter Valid Zip Code");
		}
		
		else if(edtCvv.getText().toString().equals("")){
			showDialog("Alert!", "Enter CVV No.");
		}else if(edtCvv.getText().length()<3){
			showDialog("Error", "Enter Valid CVV No.");
		}
		else{
			
			if(mYear.length()==4){
				mYear=mYear.substring(2);
			}

			String edtCreditCard = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><editCreditCard><cardId><![CDATA["
					+ bundle.getString("CARDID")
					+ "]]></cardId><cardNumber><![CDATA["
					+ bundle.getString("CARDNO")
					+ "]]></cardNumber><month><![CDATA[" + mMonth
					+ "]]></month><year><![CDATA[" + mYear
					+ "]]></year><cvv><![CDATA[" + edtCvv.getText().toString()
					+ "]]></cvv><cardType><![CDATA["
					+ bundle.getString("CARDTYPE")
					+ "]]></cardType><zipCode><![CDATA["
					+ edtZip.getText().toString()
					+ "]]></zipCode></editCreditCard>";
			new SendXmlAsync(URL.BASE_URL + URL.EDITCREDITCARD, edtCreditCard,
					this, this, true).execute();
		}
			break;

		case R.id.btnDelete:
			
		
		showDialogDelete("Message", "Are you sure to delete this credit card?");
			
		
		
			break;
			
		case R.id.txtExpiryDate:
			
			showDatePicker();
			break;

		}

	}

	@Override
	public void onResponse(String respose) {
		// TODO Auto-generated method stub
		String message ="";
		
		JSONObject mJsonObj;
		if(respose.contains("editCreditCard")){
			try {
				mJsonObj=new JSONObject(respose);
				message=mJsonObj.getString("message");
				if(mJsonObj.getString("editCreditCard").contains("1")){
					
					Toast.makeText(EditCreditCard.this, message, Toast.LENGTH_LONG).show();
					this.finish();
					ShowCreditCardDetail.activity.finish();
				}else {
					showDialog("Alert!", message);
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else if(respose.contains("deleteCard")){
			try {
				
				
				
				mJsonObj=new JSONObject(respose);
				message=mJsonObj.getString("message");
				if(mJsonObj.getString("deleteCard").contains("1")){
					
					
					mSharedPreference.setCreditcardno(mJsonObj.getJSONObject("cardListArr").getString("cardNumber"));
					mSharedPreference.setCardType(mJsonObj.getJSONObject("cardListArr").getString("cardType"));
					mSharedPreference.setExpirydateMonth(mJsonObj.getJSONObject("cardListArr").getString("month"));
					mSharedPreference.setExpirydateYear(mJsonObj.getJSONObject("cardListArr").getString("year"));
					mSharedPreference.setZip(mJsonObj.getJSONObject("cardListArr").getString("zipCode"));
					
					Toast.makeText(EditCreditCard.this, message, Toast.LENGTH_LONG).show();
					this.finish();
					ShowCreditCardDetail.activity.finish();
				}else {
					showDialog("Alert!", message);
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		

	}
	
	
	/*
	 * this method is used to show datepicker for credit card expiry date
	 */

	private void showDatePicker() {

		Calendar cal = Calendar.getInstance();

		if((cal.get(Calendar.MONTH)+1)==12){
			mMonth="01";
			mYear = String.valueOf(cal.get(Calendar.YEAR)+1);
		}else{
				mMonth = String.valueOf(cal.get(Calendar.MONTH) + 02);
				mYear = String.valueOf(cal.get(Calendar.YEAR));
				
		}

		int l = cal.get(Calendar.MONTH) + 2;

		final ArrayList<Integer> arrayList = new ArrayList<Integer>();

		final ArrayList<Integer> arrayListMonthOption1 = new ArrayList<Integer>();
		final ArrayList<Integer> arrayListMonthOption2 = new ArrayList<Integer>();
		if(cal.get(Calendar.MONTH)+1==12){
			for (int i = 0; i < 25; i++) {
				arrayList.add(cal.get(Calendar.YEAR)+1 + i);

			}
			
			for (int m = 01; m <= 12; m++) {
				arrayListMonthOption1.add(m);

			}
		}else{
		for (int i = 0; i < 25; i++) {
			arrayList.add(cal.get(Calendar.YEAR) + i);

		}
		
		for (int m = l; m <= 12; m++) {
			arrayListMonthOption1.add(m);

		}
		}
		

		for (int m = 1; m <= 12; m++) {
			arrayListMonthOption2.add(m);

		}

		arrayListMonth = arrayListMonthOption1;

		 mDialog = new Dialog(EditCreditCard.this,
				style.Theme_Translucent_NoTitleBar);
		mDialog.setContentView(R.layout.dialog_date_picker);

		WheelView wheelYear = (WheelView) mDialog
				.findViewById(R.id.wheel_1);
		final WheelView wheelMonth = (WheelView) mDialog
				.findViewById(R.id.wheel_2);

		Button btnDone = (Button) mDialog.findViewById(R.id.done_btn);

		wheelYear.setViewAdapter(new WheelViewAdapter() {

			@Override
			public void unregisterDataSetObserver(DataSetObserver observer) {
				// TODO Auto-generated method stub

			}

			@Override
			public void registerDataSetObserver(DataSetObserver observer) {
				// TODO Auto-generated method stub

			}

			@Override
			public int getItemsCount() {
				// TODO Auto-generated method stub
				return arrayList.size();
			}

			@Override
			public View getItem(int index, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				ViewHolder holder = new ViewHolder();

				if (convertView == null) {
					LayoutInflater inflater = LayoutInflater
							.from(EditCreditCard.this);

					convertView = inflater.inflate(R.layout.dialog_text, null);

					holder.mText = (TextView) convertView
							.findViewById(R.id.textView);

					convertView.setTag(holder);

				} else {

					holder = (ViewHolder) convertView.getTag();
				}

				holder.mText.setText(String.valueOf(arrayList.get(index)));

				return convertView;
			}

			@Override
			public View getEmptyItem(View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				return null;
			}
		});

		wheelMonth.setViewAdapter(adapterMonth);
		wheelYear.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub

				mYear = String.valueOf(arrayList.get(newValue));

				if (newValue == 0) {
					arrayListMonth = arrayListMonthOption1;
					wheelMonth.setViewAdapter(adapterMonth);

					if(arrayListMonth.size()-1>wheelMonth
							.getCurrentItem()){

				mMonth = String.valueOf(arrayListMonth.get(wheelMonth
						.getCurrentItem()));
					}else{
						wheelMonth.setCurrentItem(arrayListMonth.size()-1);
						mMonth=String.valueOf(arrayListMonth.get(arrayListMonth.size()-1));
					}
					if(mMonth.length()==1)
					{
						mMonth="0"+mMonth;
					}
				} else {
					arrayListMonth = arrayListMonthOption2;
					wheelMonth.setViewAdapter(adapterMonth);
					mMonth = String.valueOf(arrayListMonth.get(wheelMonth.getCurrentItem()));
					if(mMonth.length()==1)
					{
						mMonth="0"+mMonth;
					}

				}

			}
		});

		wheelMonth.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub

				mMonth = String.valueOf(arrayListMonth.get(newValue));
				if(mMonth.length()==1)
				{
					mMonth="0"+mMonth;
				}

			}
		});

		btnDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				edtExpiryDate.setText(mMonth + "/" + mYear.substring(2));
				mDialog.dismiss();

			}
		});
		mDialog.show();

	}

	
	/**
	 * 
	 * @author arvind.agarwal
	 *this class holds objects of particular item in listview
	 */
	private class ViewHolder {

		TextView mText;

	}
	
	// adapter for wheel

	WheelViewAdapter adapterMonth = new WheelViewAdapter() {

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub

		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub

		}

		@Override
		public int getItemsCount() {
			// TODO Auto-generated method stub
			return arrayListMonth.size();
		}

		@Override
		public View getItem(int index, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = new ViewHolder();

			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater
						.from(EditCreditCard.this);

				convertView = inflater.inflate(R.layout.dialog_text, null);

				holder.mText = (TextView) convertView
						.findViewById(R.id.textView);

				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			if(String.valueOf(arrayListMonth.get(index)).length()==1){
				holder.mText.setText("0"+String.valueOf(arrayListMonth.get(index)));
				}else{
					holder.mText.setText(String.valueOf(arrayListMonth.get(index)));
				}

			return convertView;
		}

		@Override
		public View getEmptyItem(View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}
	};
	
	
	/**
	 * 
	 * @param title
	 * @param message
	 * 
	 * this method is used to show alert dialog with positive and negative buttons
	 * 
	 * 
	 */
	private void showDialog(String title, String message) {

		final Dialog dialog = new Dialog(EditCreditCard.this,
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
	 * 
	 * this method is used to show prompt, when user wants to delete credit card
	 */
	
	private void showDialogDelete(String title, String message) {

		final Dialog dialog = new Dialog(EditCreditCard.this,
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
				
				if	(!CheckInternetConnectivity.checkinternetconnection(EditCreditCard.this)){
					
					dialog.dismiss();
					showDialog("Alert!", "Check Internet Connection.");
					
				}else{

				new SendXmlAsync(URL.BASE_URL + URL.DELETECREDITCARD
						+ bundle.getString("CARDID")+"&userId="
						+ mSharedPreference.getUserId(), "", EditCreditCard.this, EditCreditCard.this, true)
						.execute();
				
				
				
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
