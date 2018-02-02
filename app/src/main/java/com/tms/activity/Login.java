package com.tms.activity;

import android.R.style;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.tms.R;
import com.tms.newui.MainActivity4;
import com.tms.utility.CheckInternetConnectivity;
import com.tms.utility.EmailValidity;
import com.tms.utility.SendXmlAsync;
import com.tms.utility.ToroSharedPrefrnce;
import com.tms.utility.URL;
import com.tms.utility.Utility;
import com.tms.utility.UtilsSingleton;
import com.tms.utility.XmlListener;

import org.json.JSONObject;

/**
 * @author arvind.agarwal
 *         this class is used to login the application by putting
 *         email and password
 */
public class Login extends Activity implements OnClickListener, XmlListener {
    // objects of View class
    private Button btnLogin, btnBack;
    private InputMethodManager imm;
    private TextView btnForgottPass;
    private Button btnRegister;
    private EditText edtEmail, edtPassword;
    ToroSharedPrefrnce mPrefrnce;
    private Typeface typeFace, typeFaceForgot;
    String username = "username";
    String password = "password";
    String remember = "remember";
    private CheckBox rememberMe;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    //private ImageView selectorEmail, selectorPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        typeFace = Typeface.createFromAsset(getAssets(), Utility.TYPE_FACE);
        typeFaceForgot = Typeface.createFromAsset(getAssets(),
                Utility.TYPE_FACE_FORGOT);
        initialize();
        mPrefrnce = new ToroSharedPrefrnce(Login.this);

    }

    /**
     * initializing view
     */
    public void initialize() {
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(this);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtEmail.setTypeface(typeFace);

        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtPassword.setTypeface(typeFace);
        btnForgottPass = (TextView) findViewById(R.id.btnForgotPassword);
        btnForgottPass.setOnClickListener(this);
        rememberMe = (CheckBox)findViewById(R.id.btnRemember);
        sharedPreferences = getSharedPreferences("file",MODE_PRIVATE);
        editor = sharedPreferences.edit();
       if(sharedPreferences.getBoolean(remember,false)){
           edtEmail.setText(sharedPreferences.getString(username,""));
           edtPassword.setText(sharedPreferences.getString(password,""));
           rememberMe.setChecked(true);
        }else {
           rememberMe.setChecked(false);
        }
		/*
		 * edtEmail.addTextChangedListener(new TextWatcher() {
		 * 
		 * @Override public void onTextChanged(CharSequence s, int start, int
		 * before, int count) { // TODO Auto-generated method stub
		 * 
		 * 
		 * 
		 * if (edtEmail.getText().length() > 0) { //
		 * edtEmail.setBackgroundResource(R.drawable.text_field_blank);
		 * edtEmail.setBackgroundResource(R.drawable.text_field_blank_slctd); //
		 * imgEmail.setBackgroundResource(R.drawable.mail_icon);
		 * imgEmail.setBackgroundResource(R.drawable.mail_iconhvr); } else {
		 * edtEmail.setBackgroundResource(R.drawable.text_field_blank_black); //
		 * imgEmail.setBackgroundResource(R.drawable.mail_icon);
		 * imgEmail.setBackgroundResource(R.drawable.mail_icon); } }
		 * 
		 * @Override public void beforeTextChanged(CharSequence s, int start,
		 * int count, int after) { // TODO Auto-generated method stub
		 * 
		 * }
		 * 
		 * @Override public void afterTextChanged(Editable s) { // TODO
		 * Auto-generated method stub
		 * 
		 * } });
		 */

        edtPassword.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
				/*if (hasFocus) {
					selectorPassword
							.setBackgroundResource(R.drawable.background_text_field_selected);
					// imgEmail.setBackgroundResource(R.drawable.mail_icon);
				} else {
					selectorPassword
							.setBackgroundResource(R.drawable.background_text_field);
				}*/

            }
        });

        edtPassword.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                // TODO Auto-generated method stub

                // webservice hit
                if (!CheckInternetConnectivity
                        .checkinternetconnection(Login.this)) {
                    showDialog("Alert !", "Check Internet Connection.");

                } else if (edtEmail.getText().toString().equals("")) {

                    showDialog("Alert !", "Enter EmailId.");
                    edtEmail.requestFocus();

                } else if (edtPassword.getText().toString().equals("")) {

                    showDialog("Alert !", "Enter Password.");
                    edtPassword.requestFocus();

                } else if (!EmailValidity.isEmailValid(edtEmail.getText()
                        .toString())) {

                    showDialog("Alert !", "Invalid Email.");
                    edtEmail.requestFocus();

                } else {

//					String strGcmRegId=mPrefrnce.getGcm();

//					if(mPrefrnce.getGcm().equals("")){
//
//
//						GCMRegistrar.register(Login.this,
//								com.tms.GCMCommonUtilities.SENDER_ID);
//
//						strGcmRegId = GCMRegistrar.getRegistrationId(Login.this);
//						Log.i("regId", "regId:" + strGcmRegId);
//						mPrefrnce.setGcm(strGcmRegId);
//
//					}
                    btnLogin.setEnabled(false);

                    imm.showSoftInput(edtPassword,
                            InputMethodManager.HIDE_IMPLICIT_ONLY);
                    imm.showSoftInput(edtEmail,
                            InputMethodManager.HIDE_IMPLICIT_ONLY);

                    mPrefrnce.setEmail(edtEmail.getText().toString().trim());
                    mPrefrnce.setPassword(edtPassword.getText().toString()
                            .trim());
                    String loginXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><userLogin><email><![CDATA["
                            + edtEmail.getText().toString().trim()
                            + "]]></email><password><![CDATA["
                            + edtPassword.getText().toString().trim()
                            + "]]></password><deviceType><![CDATA[2]]></deviceType><deviceToken><![CDATA["
                            + mPrefrnce.getGcm()
                            + "]]></deviceToken></userLogin>";
                    new SendXmlAsync(URL.BASE_URL + URL.LOGIN, loginXML,
                            Login.this, Login.this, true).execute();

                }
                return false;
            }
        });
		/*
		 * edtPassword.addTextChangedListener(new TextWatcher() {
		 * 
		 * @Override public void onTextChanged(CharSequence s, int start, int
		 * before, int count) { // TODO Auto-generated method stub
		 * dialog.dismiss();
		 * 
		 * if (edtPassword.getText().length() > 0) { //
		 * edtEmail.setBackgroundResource(R.drawable.text_field_blank);
		 * edtPassword
		 * .setBackgroundResource(R.drawable.text_field_blank_slctd); //
		 * imgEmail.setBackgroundResource(R.drawable.mail_icon);
		 * imgPassword.setBackgroundResource(R.drawable.key_slctd); } else {
		 * 
		 * }
		 * 
		 * }
		 * 
		 * @Override public void beforeTextChanged(CharSequence s, int start,
		 * int count, int after) { // TODO Auto-generated method stub
		 * 
		 * }
		 * 
		 * @Override public void afterTextChanged(Editable s) { // TODO
		 * Auto-generated method stub
		 * 
		 * } });
		 */


    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            case R.id.btnRegister:

                startActivity(new Intent(Login.this, Register.class));
                Login.this.finish();
                break;

            case R.id.btnLogin:

                // webservice hit
                if (!CheckInternetConnectivity.checkinternetconnection(Login.this)) {
                    showDialog("Alert !", "Check Internet Connection.");

                } else if (edtEmail.getText().toString().equals("")) {

                    showDialog("Alert !", "Enter EmailId.");
                    edtEmail.requestFocus();

                } else if (edtPassword.getText().toString().equals("")) {

                    showDialog("Alert !", "Enter Password.");
                    edtPassword.requestFocus();

                } else if (!EmailValidity.isEmailValid(edtEmail.getText()
                        .toString())) {

                    showDialog("Alert !", "Invalid Email.");
                    edtEmail.requestFocus();

                } else {
                    if(rememberMe.isChecked()) {
                        saveinSharedPref(edtEmail.getText().toString(),edtPassword.getText().toString(),true);
                    }else {
                        saveinSharedPref("","",false);
                    }
                    btnLogin.setEnabled(false);
                    mPrefrnce.setEmail(edtEmail.getText().toString().trim());
                    mPrefrnce.setPassword(edtPassword.getText().toString().trim());
                    imm.showSoftInput(edtPassword,
                            InputMethodManager.HIDE_IMPLICIT_ONLY);
                    imm.showSoftInput(edtEmail,
                            InputMethodManager.HIDE_IMPLICIT_ONLY);
                    String loginXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><userLogin><email><![CDATA["
                            + edtEmail.getText().toString().trim()
                            + "]]></email><password><![CDATA["
                            + edtPassword.getText().toString().trim()
                            + "]]></password><deviceType><![CDATA[2]]></deviceType><deviceToken><![CDATA["
                            + mPrefrnce.getGcm() + "]]></deviceToken></userLogin>";
                    new SendXmlAsync(URL.BASE_URL + URL.LOGIN, loginXML,
                            Login.this, Login.this, true).execute();

                }
                break;
            case R.id.btnForgotPassword:
                startActivity(new Intent(this, ForgottPassword.class));

                break;


//		case R.id.btnBack:
//			startActivity(new Intent(Login.this,Option.class));
//			finish();
//			break;
            default:
                break;
        }

    }

    private void saveinSharedPref(String user,String pass,boolean isRemember) {
        editor.putString(username,user);
        editor.putString(password,pass);
        editor.putBoolean(remember,isRemember);
        editor.commit();
    }
    /**
     * getting response from server
     */
    @Override
    public void onResponse(String respose) {
        // TODO Auto-generated method stub
        btnLogin.setEnabled(true);

        try {
            JSONObject mJsonObject = new JSONObject(respose);
            String resp = mJsonObject.getString("userLogin");
            if (resp.equalsIgnoreCase("-2")) {
                Toast.makeText(Login.this, "Server Error", Toast.LENGTH_SHORT)
                        .show();
            } else if (resp.equalsIgnoreCase("-1")) {
                showDialog("Alert Message", "Invalid Email Address");
                edtEmail.setText("");

                edtPassword.setText("");
            } else if (resp.equalsIgnoreCase("-3")) {
                showDialog("Alert Message", "Invalid Password");
                edtPassword.setText("");

            } else if (resp.equalsIgnoreCase("-4")) {
                showDialog("Alert Message", mJsonObject.getString("message"));

            } else if (resp.equalsIgnoreCase("-5")) {
                showDialog("Alert Message", mJsonObject.getString("message"));

            } else {
                if (mJsonObject.getString("verifyStatus").equalsIgnoreCase("1")) {

                    Toast.makeText(Login.this, "Successfully Login",
                            Toast.LENGTH_LONG).show();

                    mPrefrnce.setUserId(resp);
                    mPrefrnce.setUserId(resp);
                    mPrefrnce.setReferal(mJsonObject.getString("referralCode"));
                    mPrefrnce.setPhone(mJsonObject.getString("phone"));
                    mPrefrnce.setFirstName(mJsonObject.getString("firstName"));
                    mPrefrnce.setLastname(mJsonObject.getString("lastName"));
                    mPrefrnce.setEmail(mJsonObject.getString("emailAddress"));
                    mPrefrnce.setImageUrl(mJsonObject.getString("uImage"));
                    mPrefrnce.setDistance(mJsonObject.getString("distance"));

                    if (Utility.isPaymentGatewayOn) {

                        if (!mJsonObject.getString("cardDetail").equals("[]")) {
                            mPrefrnce.setCreditcardno(mJsonObject
                                    .getJSONObject("cardDetail").getString(
                                            "cardNumber"));

                            mPrefrnce.setCardType(mJsonObject.getJSONObject(
                                    "cardDetail").getString("cardType"));
                            mPrefrnce.setExpirydateMonth(mJsonObject
                                    .getJSONObject("cardDetail").getString(
                                            "month"));
                            mPrefrnce.setExpirydateYear(mJsonObject
                                    .getJSONObject("cardDetail").getString(
                                            "year"));
                            mPrefrnce.setZip(mJsonObject.getJSONObject(
                                    "cardDetail").getString("zipCode"));

                            mPrefrnce.setPaymentViaCreditCard(true);
                        } else {

                            mPrefrnce.setPaymentViaCreditCard(false);
                        }

                    }
                    mPrefrnce.setAccountVerified(true);

                    UtilsSingleton.getInstance().setBookingId(
                            mJsonObject.getString("bookingId"));
                    UtilsSingleton.getInstance().setPaymentStatus(
                            mJsonObject.getString("paymentStatus"));

                    if (mJsonObject.getString("paymentStatus").equals("0")
                            && mJsonObject.getString("bookingId").equals("0")) {

                        startActivity(new Intent(Login.this, MainActivity4.class));
                    } else if (!mJsonObject.getString("paymentStatus").equals(
                            "0")
                            && !mJsonObject.getString("bookingId").equals("0")) {

                        // mPrefrnce.setBookingId(mJsonObject.getString("bookingId"));
                        // mPrefrnce.setPaymentStatus(mJsonObject.getString("paymentStatus"));
                        startActivity(new Intent(Login.this,
                                ChangeCardType.class).putExtra("pending", "1"));
                    }

                    //Option.mActivity.finish();
                    finish();

                } else {
                    Toast.makeText(Login.this, "Verify Your Phone First",
                            Toast.LENGTH_LONG).show();
                    mPrefrnce.setUserId(resp);
                    mPrefrnce.setPhone(mJsonObject.getString("phone"));
                    mPrefrnce.setFirstName(mJsonObject.getString("firstName"));
                    mPrefrnce.setLastname(mJsonObject.getString("lastName"));
                    mPrefrnce.setEmail(mJsonObject.getString("emailAddress"));
                    mPrefrnce.setImageUrl(mJsonObject.getString("uImage"));
                    mPrefrnce.setCountryCode(mJsonObject
                            .getString("countryCode"));
                    // mPrefrnce.setReferal(mJsonObject.getString("referralCode"));

                    startActivity(new Intent(Login.this, VarifyMobile.class)
                            .putExtra("via", "login"));

                }
            }
        } catch (Exception e) {
          //  Toast.makeText(Login.this, "Server Error", Toast.LENGTH_SHORT)
            //        .show();
            //startActivity(new Intent(Login.this, com.tms.activity.MainActivity2.class));

        }
    }

    /**
     * Dialog to show alerts
     *
     * @param title   setting title of dialog
     * @param message setting message
     */

    private void showDialog(String title, String message) {

        final Dialog dialog = new Dialog(Login.this,
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


    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edtPassword,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);
                imm.showSoftInput(edtEmail,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);

            }

        }

        ;
    };

}
