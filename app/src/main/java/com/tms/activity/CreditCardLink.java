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
import android.content.Intent;
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
import com.tms.utility.CreditCardValidation;
import com.tms.utility.ToroSharedPrefrnce;
import com.tms.utility.Utility;
import com.tms.utility.XmlListener;

/**
 * 
 * @author arvind.agarwal this class is useful to link credit card
 */
public class CreditCardLink extends Activity implements OnClickListener,
		XmlListener {

	// Objects of View Class

	private String mMonth, mYear;

	private ArrayList<Integer> arrayListMonth = new ArrayList<Integer>();
	private EditText edtCardNo, edtCVVno, edtZipCode;
	private EditText edtExpirydate;
	private Button btnSubmit, btnBack;
	ToroSharedPrefrnce mPrefrnce;
	static final int DEFAULTDATESELECTOR_ID = 0;
	public static Activity activity;

	private Dialog mDialog;
	private InputMethodManager imm;
	private Typeface typeFace, typeFace2;

	private ImageView selectorCreditCard, selectorExpiryDate, selectorCVV,
			selectorZIP;
	
	private TextView txtSkip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credit_card_linkup);
		activity = this;

		// getting type face path from utility class
		typeFace2 = Typeface.createFromAsset(getAssets(),
				Utility.TYPE_FACE_FORGOT);
		typeFace = Typeface.createFromAsset(getAssets(), Utility.TYPE_FACE);

		// setting type face on text views
		((TextView) findViewById(R.id.txtHeader)).setTypeface(typeFace);
		((TextView) findViewById(R.id.txt_info)).setTypeface(typeFace2);

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// initializing shared prference object.
		mPrefrnce = new ToroSharedPrefrnce(CreditCardLink.this);
		initializeViews();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		
		case R.id.txtSkip:
			
			mPrefrnce
			.setCreditcardno("");
	mPrefrnce.setCvv("");
	mPrefrnce.setExpirydateYear("");
	mPrefrnce.setExpirydateMonth("");
	mPrefrnce.setZip("");
	mPrefrnce.setCardType("");
	mPrefrnce.setPaymentViaCreditCard(false);

	startActivity(new Intent(CreditCardLink.this,
			TermsCondition.class));
			
			break;
		case R.id.btnBack:

			finish();
			break;
		case R.id.btnSubmit:
			
			//checking the validities of credit card and details

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
				
				//saving details of credit card
				mPrefrnce
						.setCreditcardno(edtCardNo.getText().toString().trim());
				mPrefrnce.setCvv(edtCVVno.getText().toString().trim());
				mPrefrnce.setExpirydateYear(mYear.substring(2));
				mPrefrnce.setExpirydateMonth(mMonth);
				mPrefrnce.setZip(edtZipCode.getText().toString().trim());
				mPrefrnce.setCardType(whichTypeCreditCard(edtCardNo.getText()
						.toString().trim()));
				mPrefrnce.setPaymentViaCreditCard(true);

				startActivity(new Intent(CreditCardLink.this,
						TermsCondition.class));
			}
			break;

		default:
			break;
		}

	}

	/**
	 * initializing view
	 */

	public void initializeViews() {

		
		txtSkip=(TextView)findViewById(R.id.txtSkip);
		txtSkip.setOnClickListener(this);

        if(Utility.cashFlow){

            txtSkip.setVisibility(View.GONE);
        }else{
            txtSkip.setVisibility(View.GONE);

        }
		// image objects
		
		selectorCreditCard = (ImageView) findViewById(R.id.selector_card_no);
		selectorExpiryDate = (ImageView) findViewById(R.id.selector_expiry);
		selectorCVV = (ImageView) findViewById(R.id.selector_cvv);
		selectorZIP = (ImageView) findViewById(R.id.selector_zip);

		edtExpirydate = (EditText) findViewById(R.id.edtexpirydate);
		edtExpirydate.setTypeface(typeFace);

		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnSubmit.setOnClickListener(this);
		edtCardNo = (EditText) findViewById(R.id.edtCardNo);
		edtCardNo.setTypeface(typeFace);
		edtCVVno = (EditText) findViewById(R.id.edtCVVNO);
		edtCVVno.setTypeface(typeFace);

		edtZipCode = (EditText) findViewById(R.id.edtZip);
		edtZipCode.setTypeface(typeFace);
		
		//handling focus of card no. field

		edtCardNo.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub

				if (hasFocus) {
					
					// if having focus then set background background_text_field_selected
					selectorCreditCard
							.setBackgroundResource(R.drawable.background_text_field_selected);
					// imgEmail.setBackgroundResource(R.drawable.mail_icon);

				} else {
					// if not  having focus then set background background_text_field_selected
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

				edtExpirydate.requestFocus();

				return false;
			}
		});

		//handling focus of cvv no. field
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

		//handling focus of expiry field
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

		//handling focus of zip code field
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

		
		// handling zip code edit text action 
		edtZipCode.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				
				// checking for credit card details

				if (edtCardNo.getText().toString().trim().equalsIgnoreCase("")) {

					showDialogg("Alert!", "Enter Card number");

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
				} else {
					
					
					//saving details of credit card
					mPrefrnce.setCreditcardno(edtCardNo.getText().toString()
							.trim());
					mPrefrnce.setCvv(edtCVVno.getText().toString().trim());
					mPrefrnce.setExpirydateYear(mYear.substring(2));
					mPrefrnce.setExpirydateMonth(mMonth);
					mPrefrnce.setZip(edtZipCode.getText().toString().trim());
					mPrefrnce.setCardType(whichTypeCreditCard(edtCardNo
							.getText().toString().trim()));
					mPrefrnce.setPaymentViaCreditCard(true);

					startActivity(new Intent(CreditCardLink.this,
							TermsCondition.class));
				}
				return false;
			}
		});

	}
	
	
	/**
	 * interface methode to handle response from webservice hit
	 */

	@Override
	public void onResponse(String respose) {
		// TODO Auto-generated method stub
		Log.i("Response..", respose);
		try {
			JSONObject mJsonObject = new JSONObject(respose);
			String resp = mJsonObject.getString("userRegistration");
			if (resp.equalsIgnoreCase("-2")) {
				Toast.makeText(CreditCardLink.this, "Server Error",
						Toast.LENGTH_LONG).show();
			} else if (resp.equalsIgnoreCase("-3")) {
				Toast.makeText(CreditCardLink.this, "Email Exists",
						Toast.LENGTH_LONG).show();
			} else if (resp.equalsIgnoreCase("-4")) {
				Toast.makeText(CreditCardLink.this, "Phone Exists",
						Toast.LENGTH_LONG).show();
			} else if (resp.equalsIgnoreCase("-5")) {
				Toast.makeText(CreditCardLink.this, "Email & Phone Exists",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(CreditCardLink.this, "Successfully register",
						Toast.LENGTH_LONG).show();
				startActivity(new Intent(CreditCardLink.this, com.tms.activity.Login.class)
						.putExtra("via", "creditCard"));
				finish();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * 
	 * @param creditCard(creditcard no. in string)
	 * @return
	 * 
	 * this methode is called to check Credit card type
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

	/*
	 * private boolean creditCardValidity(String creditCardNo) {
	 * 
	 * String zero = creditCardNo.substring(0, 1); String one =
	 * creditCardNo.substring(1, 2); String two = creditCardNo.substring(2, 3);
	 * String three = creditCardNo.substring(3, 4); String four =
	 * creditCardNo.substring(4, 5); String five = creditCardNo.substring(5, 6);
	 * String six = creditCardNo.substring(6, 7); String seven =
	 * creditCardNo.substring(7, 8); String eight = creditCardNo.substring(8,
	 * 9); String nine = creditCardNo.substring(9, 10); String ten =
	 * creditCardNo.substring(10, 11); String eleven =
	 * creditCardNo.substring(11, 12); String tweleve =
	 * creditCardNo.substring(12, 13); String thirteen =
	 * creditCardNo.substring(13, 14); String fourteen =
	 * creditCardNo.substring(14, 15); String fifteen =
	 * creditCardNo.substring(15, 16);
	 * 
	 * if (String.valueOf(2 * Integer.parseInt(zero)).length() > 1) {
	 * 
	 * zero = doubleToSingle(zero); } else { zero = String.valueOf(2 *
	 * Integer.parseInt(zero)); } if (String.valueOf(2 *
	 * Integer.parseInt(two)).length() > 1) {
	 * 
	 * two = doubleToSingle(two); } else { two = String.valueOf(2 *
	 * Integer.parseInt(two)); } if (String.valueOf(2 *
	 * Integer.parseInt(four)).length() > 1) {
	 * 
	 * four = doubleToSingle(four); } else { four = String.valueOf(2 *
	 * Integer.parseInt(four)); }
	 * 
	 * if (String.valueOf(2 * Integer.parseInt(six)).length() > 1) {
	 * 
	 * six = doubleToSingle(six); } else { six = String.valueOf(2 *
	 * Integer.parseInt(six)); }
	 * 
	 * if (String.valueOf(2 * Integer.parseInt(eight)).length() > 1) {
	 * 
	 * eight = doubleToSingle(eight); } else { eight = String.valueOf(2 *
	 * Integer.parseInt(eight)); }
	 * 
	 * if (String.valueOf(2 * Integer.parseInt(ten)).length() > 1) {
	 * 
	 * ten = doubleToSingle(ten); } else { ten = String.valueOf(2 *
	 * Integer.parseInt(ten)); } if (String.valueOf(2 *
	 * Integer.parseInt(tweleve)).length() > 1) {
	 * 
	 * tweleve = doubleToSingle(tweleve); } else { tweleve = String.valueOf(2 *
	 * Integer.parseInt(tweleve)); } if (String.valueOf(2 *
	 * Integer.parseInt(fourteen)).length() > 1) {
	 * 
	 * fourteen = doubleToSingle(fourteen); } else { fourteen = String.valueOf(2
	 * * Integer.parseInt(fourteen)); }
	 * 
	 * if ((Integer.parseInt(zero) + Integer.parseInt(one) +
	 * Integer.parseInt(two) + Integer.parseInt(three) + Integer.parseInt(four)
	 * + Integer.parseInt(five) + Integer.parseInt(six) +
	 * Integer.parseInt(seven) + Integer.parseInt(eight) +
	 * Integer.parseInt(nine) + Integer.parseInt(ten) + Integer.parseInt(eleven)
	 * + Integer.parseInt(tweleve) + Integer.parseInt(thirteen) +
	 * Integer.parseInt(fourteen) + Integer.parseInt(fifteen)) % 10 == 0) {
	 * 
	 * return true;
	 * 
	 * } else { return false; }
	 * 
	 * }
	 * 
	 * private String doubleToSingle(String value) {
	 * 
	 * return String.valueOf(Integer.parseInt(String.valueOf( 2 *
	 * Integer.parseInt(value)).substring(0, 1)) +
	 * Integer.parseInt(String.valueOf(2 * Integer.parseInt(value))
	 * .substring(1, 2))); }
	 */

	/**
	 * 
	 * this method is used to date picker for setting expiry date of credit card
	 */
	private void showDatePicker() {

		selectorCreditCard
				.setBackgroundResource(R.drawable.background_text_field);
		// imgEmail.setBackgroundResource(R.drawable.mail_icon);
		selectorCVV.setBackgroundResource(R.drawable.background_text_field);
		// imgEmail.setBackgroundResource(R.drawable.mail_icon);

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

		int l = cal.get(Calendar.MONTH) + 02;

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

		for (int m = 01; m <= 12; m++) {
			arrayListMonthOption2.add(m);

		}

		arrayListMonth = arrayListMonthOption1;

		mDialog = new Dialog(CreditCardLink.this,
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
							.from(CreditCardLink.this);

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
						.from(CreditCardLink.this);

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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
	}

	/**
	 * 
	 * @param title
	 *            this method is used to show alert dialog
	 * 
	 * @param message
	 */

	private void showDialogg(String title, String message) {

		final Dialog dialog = new Dialog(CreditCardLink.this,
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
