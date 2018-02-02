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
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.tms.R;
import com.tms.utility.CheckInternetConnectivity;
import com.tms.utility.CreditCardValidation;
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
 * this view adds credit card
 * 
 */
public class AddCreditCard extends Activity implements OnClickListener,
		XmlListener {

	private TextView txtTitle,txtSkip;
	private EditText edtCardNo, edtCVVno, edtZipCode;
	private EditText edtExpirydate;
	private Button btnSubmit;ImageView btnBack;
	ToroSharedPrefrnce mPrefrnce;

	static final int DEFAULTDATESELECTOR_ID = 0;
	private Dialog mDialog;
	private InputMethodManager imm;
	private String mMonth, mYear;
	private ArrayList<Integer> arrayListMonth = new ArrayList<Integer>();

	
	//object of wheel liberary(third party liberary)
	//year wheel
	WheelView wheelYear;
	
	//month wheel
	WheelView wheelMonth;

	Typeface typeFace;
	
	
	private ImageView selectorCreditCard, selectorExpiryDate, selectorCVV,
			selectorZIP;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credit_card_linkup);

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		mPrefrnce = new ToroSharedPrefrnce(AddCreditCard.this);

		typeFace = Typeface.createFromAsset(getAssets(), Utility.TYPE_FACE);
		((TextView) findViewById(R.id.txtHeader)).setTypeface(typeFace);
		((TextView) findViewById(R.id.txt_info)).setVisibility(View.GONE);
		initializeView();

	}

	
	/**
	 * initializing Views
	 */
	private void initializeView() {
		txtSkip=(TextView)findViewById(R.id.txtSkip);
		txtSkip.setVisibility(View.GONE);
		selectorCreditCard = (ImageView) findViewById(R.id.selector_card_no);
		selectorExpiryDate = (ImageView) findViewById(R.id.selector_expiry);
		selectorCVV = (ImageView) findViewById(R.id.selector_cvv);
		selectorZIP = (ImageView) findViewById(R.id.selector_zip);
		txtTitle = (TextView) findViewById(R.id.txtHeader);
		txtTitle.setText("ADD CREDIT CARD");

		// image objects

		edtExpirydate = (EditText) findViewById(R.id.edtexpirydate);
		edtExpirydate.setTypeface(typeFace);
		btnBack = (ImageView) findViewById(R.id.btnBack);

		btnBack.setOnClickListener(this);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnSubmit.setOnClickListener(this);
		edtCardNo = (EditText) findViewById(R.id.edtCardNo);
		edtCardNo.setTypeface(typeFace);
		edtCVVno = (EditText) findViewById(R.id.edtCVVNO);
		edtCVVno.setTypeface(typeFace);
		edtZipCode = (EditText) findViewById(R.id.edtZip);
		edtZipCode.setTypeface(typeFace);
		
		
		//changing background drawables on focus change of edit text 
		edtCardNo.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub

				if (hasFocus) {
					selectorCreditCard
							.setBackgroundResource(R.drawable.background_text_field_selected);
					// imgEmail.setBackgroundResource(R.drawable.mail_icon);

				} else {
					selectorCreditCard
							.setBackgroundResource(R.drawable.background_text_field);
					// imgEmail.setBackgroundResource(R.drawable.mail_icon);

				}

			}
		});
		edtCardNo.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub

				
				//changing focus
				edtExpirydate.requestFocus();

				return false;
			}
		});
		
		//changing background drawables on focus change of edit text 

		edtCVVno.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub

				if (hasFocus) {

					selectorCVV
							.setBackgroundResource(R.drawable.background_text_field_selected);
					// imgEmail.setBackgroundResource(R.drawable.mail_icon);

				} else {
					selectorCVV
							.setBackgroundResource(R.drawable.background_text_field);
					// imgEmail.setBackgroundResource(R.drawable.mail_icon);

				}

			}
		});
		
		
		edtCVVno.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (edtCVVno.getText().length() > 0) {

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

		
		
		// calling  date picker on focus 
		edtExpirydate.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub

				if (hasFocus) {
					selectorExpiryDate
							.setBackgroundResource(R.drawable.background_text_field_selected);
					// imgEmail.setBackgroundResource(R.drawable.mail_icon);

					showDatePicker();

				} else {
					selectorExpiryDate
							.setBackgroundResource(R.drawable.background_text_field);
					// imgEmail.setBackgroundResource(R.drawable.mail_icon);

				}
			}
		});

		edtZipCode.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					selectorZIP
							.setBackgroundResource(R.drawable.background_text_field_selected);
					// imgEmail.setBackgroundResource(R.drawable.mail_icon);

				} else {
					selectorZIP
							.setBackgroundResource(R.drawable.background_text_field);
					// imgEmail.setBackgroundResource(R.drawable.mail_icon);

				}
			}
		});

		edtZipCode.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub

				if (!CheckInternetConnectivity
						.checkinternetconnection(AddCreditCard.this)) {

					Toast.makeText(AddCreditCard.this,
							"Check Internet Connection!", Toast.LENGTH_LONG)
							.show();

				} else

				if (edtCardNo.getText().toString().trim().equalsIgnoreCase("")) {

					showDialogg("Alert!", "Enter Card number");
					edtCardNo.requestFocus();

				} else if (edtExpirydate.getText().toString().trim()
						.equalsIgnoreCase("")) {

					showDialogg("Alert!", "Enter Expiry date");

				} else if (edtCVVno.getText().toString().trim()
						.equalsIgnoreCase("")) {
					showDialogg("Alert!", "Enter CVV Number");

					edtCVVno.requestFocus();
					imm.showSoftInput(edtCVVno,
							InputMethodManager.SHOW_IMPLICIT);

				} else if (edtZipCode.getText().toString().trim()
						.equalsIgnoreCase("")) {
					showDialogg("Alert!", "Enter Zip Code");

					edtZipCode.requestFocus();
					imm.showSoftInput(edtZipCode,
							InputMethodManager.SHOW_IMPLICIT);

				} else if (edtCVVno.getText().length() < 3) {

					showDialogg("Alert!", "Enter valid CVV Number");
					edtCVVno.requestFocus();
					imm.showSoftInput(edtCVVno,
							InputMethodManager.SHOW_IMPLICIT);

				} else if (edtZipCode.getText().length() < 5) {
					showDialogg("Alert!", "Enter valid Zip code");

					edtZipCode.requestFocus();
					imm.showSoftInput(edtZipCode,
							InputMethodManager.SHOW_IMPLICIT);
				} else if (!CreditCardValidation.isValid(Long
						.parseLong(edtCardNo.getText().toString().trim()))) {
					showDialogg("Alert!", "Invalid Card number");

					edtCardNo.requestFocus();
					imm.showSoftInput(edtCardNo,
							InputMethodManager.SHOW_IMPLICIT);
				} else if (!CreditCardValidation.whichTypeCreditCard(edtCardNo
						.getText().toString())) {
					showDialogg(
							"Alert!",
							"Only Visa Cards, Master Cards, American Express Cards, Discover Cards are allowed.");
					edtCardNo.requestFocus();
					imm.showSoftInput(edtCardNo,
							InputMethodManager.SHOW_IMPLICIT);
				}

				else {
					
					
					//adding credit card 
					String addCreditCard = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><addCreditCard><userId><![CDATA["
							+ mPrefrnce.getUserId()
							+ "]]></userId><cardNumber><![CDATA["
							+ edtCardNo.getText().toString()
							+ "]]></cardNumber><month><![CDATA["
							+ mMonth
							+ "]]></month><year><![CDATA["
							+ mYear.substring(2)
							+ "]]></year><cvv><![CDATA["
							+ edtCVVno.getText().toString()
							+ "]]></cvv><cardType><![CDATA["
							+ whichTypeCreditCard(edtCardNo.getText()
									.toString())
							+ "]]></cardType><zipCode><![CDATA["
							+ edtZipCode.getText().toString()
							+ "]]></zipCode><paymentStatus><![CDATA["
							+ UtilsSingleton.getInstance().getPaymentStatus()
							+ "]]></paymentStatus><bookingId><![CDATA["
							+ UtilsSingleton.getInstance().getBookingId()
							+ "]]></bookingId></addCreditCard>";

					new SendXmlAsync(URL.BASE_URL + URL.ADDCREDITCARD,
							addCreditCard, AddCreditCard.this,
							AddCreditCard.this, true).execute();

				}
				return false;
			}
		});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.btnSubmit:

			if (!CheckInternetConnectivity
					.checkinternetconnection(AddCreditCard.this)) {

				Toast.makeText(AddCreditCard.this,
						"Check Internet Connection!", Toast.LENGTH_LONG).show();

			} else

			if (edtCardNo.getText().toString().trim().equalsIgnoreCase("")) {

				showDialogg("Alert!", "Enter Card number");

			} else if (edtExpirydate.getText().toString().trim()
					.equalsIgnoreCase("")) {

				showDialogg("Alert!", "Enter Expiry date");

			} else if (edtCVVno.getText().toString().trim()
					.equalsIgnoreCase("")) {
				showDialogg("Alert!", "Enter CVV Number");

				edtCVVno.requestFocus();
				imm.showSoftInput(edtCVVno, InputMethodManager.SHOW_IMPLICIT);

			} else if (edtZipCode.getText().toString().trim()
					.equalsIgnoreCase("")) {
				showDialogg("Alert!", "Enter Zip Code");

				edtZipCode.requestFocus();
				imm.showSoftInput(edtZipCode, InputMethodManager.SHOW_IMPLICIT);

			} else if (edtCVVno.getText().length() < 3) {

				showDialogg("Alert!", "Enter valid CVV Number");
				edtCVVno.requestFocus();
				imm.showSoftInput(edtCVVno, InputMethodManager.SHOW_IMPLICIT);

			} else if (edtZipCode.getText().length() < 5) {
				showDialogg("Alert!", "Enter valid Zip code");

				edtZipCode.requestFocus();
				imm.showSoftInput(edtZipCode, InputMethodManager.SHOW_IMPLICIT);
			} else if (!CreditCardValidation.isValid(Long.parseLong(edtCardNo
					.getText().toString().trim()))) {
				showDialogg("Alert!", "Invalid Card number");

				edtCardNo.requestFocus();
				imm.showSoftInput(edtCardNo, InputMethodManager.SHOW_IMPLICIT);
			} else if (!CreditCardValidation.whichTypeCreditCard(edtCardNo
					.getText().toString())) {
				showDialogg(
						"Alert!",
						"Only Visa Cards, Master Cards, American Express Cards, Discover Cards are allowed.");
				edtCardNo.requestFocus();
				imm.showSoftInput(edtCardNo, InputMethodManager.SHOW_IMPLICIT);
			}

			else {
				
				//adding credit card
				String addCreditCard = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><addCreditCard><userId><![CDATA["
						+ mPrefrnce.getUserId()
						+ "]]></userId><cardNumber><![CDATA["
						+ edtCardNo.getText().toString()
						+ "]]></cardNumber><month><![CDATA["
						+ mMonth
						+ "]]></month><year><![CDATA["
						+ mYear.substring(2)
						+ "]]></year><cvv><![CDATA["
						+ edtCVVno.getText().toString()
						+ "]]></cvv><cardType><![CDATA["
						+ whichTypeCreditCard(edtCardNo.getText().toString())
						+ "]]></cardType><zipCode><![CDATA["
						+ edtZipCode.getText().toString()
						+ "]]></zipCode><paymentStatus><![CDATA["
						+ UtilsSingleton.getInstance().getPaymentStatus()
						+ "]]></paymentStatus><bookingId><![CDATA["
						+ UtilsSingleton.getInstance().getBookingId()
						+ "]]></bookingId></addCreditCard>";

				new SendXmlAsync(URL.BASE_URL + URL.ADDCREDITCARD,
						addCreditCard, AddCreditCard.this, AddCreditCard.this,
						true).execute();

			}

			break;

		case R.id.btnBack:

			finish();
			break;

		}

	}
	
	/**
	 * 
	 * @param creditCard
	 * @return
	 * 
	 * this method provides type of credit card
	 */

	private String whichTypeCreditCard(String creditCard) {

		String cardType = "";

		if (creditCard.substring(0, 1).equals("4")) {
			cardType = "Visa";

		} else if (creditCard.substring(0, 1).equals("5")) {
			cardType = "Master";

		} else if (creditCard.substring(0, 1).equals("6")) {
			cardType = "Discover";

		} else if (creditCard.substring(0, 2).equals("34")
				|| creditCard.substring(0, 2).equals("37")) {

			cardType = "AmEx";

		} else {

		}

		return cardType;

	}
	
	
	/**
	 * this method is called to show date picker
	 * 
	 * it contains 2 wheel objects (month and year)
	 */

	private void showDatePicker() {

		// imgEmail.setBackgroundResource(R.drawable.mail_icon);

		
		// creating instance of calendar
		Calendar cal = Calendar.getInstance();

		if ((cal.get(Calendar.MONTH) + 1) == 12) {
			mMonth = "01";
			mYear = String.valueOf(cal.get(Calendar.YEAR) + 1);
		} else {
			mMonth = String.valueOf(cal.get(Calendar.MONTH) + 02);
			mYear = String.valueOf(cal.get(Calendar.YEAR));

		}

		if (mMonth.length() == 1) {
			mMonth = "0" + mMonth;
		}

		int l = cal.get(Calendar.MONTH) + 2;

		final ArrayList<Integer> arrayList = new ArrayList<Integer>();

		final ArrayList<Integer> arrayListMonthOption1 = new ArrayList<Integer>();
		final ArrayList<Integer> arrayListMonthOption2 = new ArrayList<Integer>();
		if (cal.get(Calendar.MONTH) + 1 == 12) {
			for (int i = 0; i < 25; i++) {
				arrayList.add(cal.get(Calendar.YEAR) + 1 + i);

			}

			for (int m = 01; m <= 12; m++) {
				arrayListMonthOption1.add(m);

			}
		} else {
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

		mDialog = new Dialog(AddCreditCard.this,
				style.Theme_Translucent_NoTitleBar);
		mDialog.setContentView(R.layout.dialog_date_picker);

		wheelYear = (WheelView) mDialog
				.findViewById(R.id.wheel_1);
		wheelMonth = (WheelView) mDialog
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
							.from(AddCreditCard.this);

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

		
		//setting month adapter
		wheelMonth.setViewAdapter(adapterMonth);
		wheelYear.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub

				mYear = String.valueOf(arrayList.get(newValue));

				if (newValue == 0) {
					arrayListMonth = arrayListMonthOption1;
					wheelMonth.setViewAdapter(adapterMonth);

					if (arrayListMonth.size() - 1 > wheelMonth.getCurrentItem()) {

						mMonth = String.valueOf(arrayListMonth.get(wheelMonth
								.getCurrentItem()));
					} else {
						wheelMonth.setCurrentItem(arrayListMonth.size() - 1);
						mMonth = String.valueOf(arrayListMonth
								.get(arrayListMonth.size() - 1));
					}
					if (mMonth.length() == 1) {
						mMonth = "0" + mMonth;
					}
				} else {
					arrayListMonth = arrayListMonthOption2;
					wheelMonth.setViewAdapter(adapterMonth);
					mMonth = String.valueOf(arrayListMonth.get(wheelMonth
							.getCurrentItem()));
					if (mMonth.length() == 1) {
						mMonth = "0" + mMonth;
					}

				}

			}
		});

		wheelMonth.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub

				mMonth = String.valueOf(arrayListMonth.get(newValue));
				if (mMonth.length() == 1) {
					mMonth = "0" + mMonth;
				}

			}
		});

		btnDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				edtExpirydate.setText(mMonth + " / " + mYear.substring(2));
				mDialog.dismiss();

				edtCVVno.requestFocus();

			}
		});
		mDialog.show();

	}

	private class ViewHolder {

		TextView mText;

	}
	
	/**
	 * adapter for month
	 */

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
						.from(AddCreditCard.this);

				convertView = inflater.inflate(R.layout.dialog_text, null);

				holder.mText = (TextView) convertView
						.findViewById(R.id.textView);

				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}
			if (String.valueOf(arrayListMonth.get(index)).length() == 1) {
				holder.mText.setText("0"
						+ String.valueOf(arrayListMonth.get(index)));
			} else {
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

	/*
	 * private void showDialog() {
	 * 
	 * final Dialog dialog = new Dialog(AddCreditCard.this,
	 * style.Theme_Translucent_NoTitleBar);
	 * dialog.setContentView(R.layout.dialog_information_gonr);
	 * 
	 * Button btnYes = (Button) dialog.findViewById(R.id.dialog_ok_btn); Button
	 * btnNo = (Button) dialog.findViewById(R.id.dialog_cancel_btn);
	 * btnYes.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub
	 * 
	 * AddCreditCard.this.finish();
	 * 
	 * } });
	 * 
	 * btnNo.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub
	 * 
	 * dialog.dismiss();
	 * 
	 * } });
	 * 
	 * dialog.show();
	 * 
	 * }
	 */
	
	
	/**
	 * interface to handle webservice response
	 */

	@Override
	public void onResponse(String respose) {
		// TODO Auto-generated method stub

		JSONObject mJsonObject;
		Log.i("", "" + respose);

		if (respose.contains("addCreditCard")) {
			try {
				mJsonObject = new JSONObject(respose);
				if (Integer.parseInt(mJsonObject.getString("addCreditCard")) > 0) {

					UtilsSingleton.getInstance().setBookingId("0");
					UtilsSingleton.getInstance().setPaymentStatus("0");
					
					
					mPrefrnce=new  ToroSharedPrefrnce(AddCreditCard.this);
					
					mPrefrnce.setCreditcardno(edtCardNo.getText().toString().substring(edtCardNo.getText().toString().length()-4, edtCardNo.getText().toString().length()));
					mPrefrnce.setCvv(edtCVVno.getText().toString());
					mPrefrnce.setExpirydateMonth(mMonth);
					mPrefrnce.setExpirydateYear(mYear);
					/*
					 * mPrefrnce.setBookingId("0");
					 * mPrefrnce.setPaymentStatus("0");
					 */
					finish();
					Toast.makeText(AddCreditCard.this,
							mJsonObject.getString("message"), Toast.LENGTH_LONG)
							.show();

				} else {

					showDialogg("Alert!", mJsonObject.getString("message"));

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
	 * 
	 * this method is called to show alert
	 */

	private void showDialogg(String title, String message) {

		final Dialog dialog = new Dialog(AddCreditCard.this,
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
