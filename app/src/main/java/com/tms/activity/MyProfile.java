package com.tms.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.style;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tms.R;
import com.tms.utility.CircleTransform;
import com.tms.utility.ResponseListener;
import com.tms.utility.ToroSharedPrefrnce;
import com.tms.utility.URL;
import com.tms.utility.Utility;
import com.tms.utility.WebServiceAsynk;
import com.squareup.picasso.Picasso;

@SuppressLint("NewApi")
public class MyProfile extends Activity implements OnClickListener,
        ResponseListener {
    private Button btnLogout;
    private ImageButton btnBack;
    Button btnEditProfile;
    public static Activity activity;
    ImageView mImageView;
    ToroSharedPrefrnce mPrefrnce;
    TextView txtFUserName, txtLUserName, txtuserEmail, txtuserPhone,
            txtuserPassword;
    RelativeLayout mPhotoLayout;
    Typeface typeFace, typeFaceHelveticaLight;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        typeFace = Typeface.createFromAsset(getAssets(), Utility.TYPE_FACE);
        typeFaceHelveticaLight = Typeface.createFromAsset(getAssets(), Utility.TYPE_FACE_HELVATICA_NEUE_LIGHT);
        ((TextView) findViewById(R.id.headerText)).setTypeface(typeFace);
        activity = this;
        mPrefrnce = new ToroSharedPrefrnce(MyProfile.this);
        initializeViews();
    }

    public void initializeViews() {
        mPhotoLayout = (RelativeLayout) findViewById(R.id.layoutuserPic);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
        mImageView = (ImageView) findViewById(R.id.imgUserProfile);

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);
        btnEditProfile = (Button) findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(this);
        txtuserEmail = (TextView) findViewById(R.id.txtUserEmail);
        txtuserEmail.setTypeface(typeFaceHelveticaLight);
        txtFUserName = (TextView) findViewById(R.id.txtFUserName);
        txtFUserName.setTypeface(typeFaceHelveticaLight);
        txtLUserName = (TextView) findViewById(R.id.txtLUserName);
        txtLUserName.setTypeface(typeFaceHelveticaLight);
        txtuserPassword = (TextView) findViewById(R.id.txtUserPassword);
        txtuserPassword.setTypeface(typeFace);
        txtuserPhone = (TextView) findViewById(R.id.txtUserPhone);
        txtuserPhone.setTypeface(typeFaceHelveticaLight);

    }

    @Override
    protected void onResume() {
        super.onResume();
        new WebServiceAsynk(URL.BASE_URL + URL.MY_PROFILE_DETAIL
                + mPrefrnce.getUserId(), MyProfile.this, MyProfile.this, false,
                "first").execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnLogout:
                showDialog("Alert !", "Are you sure to log out ?");
                break;
            case R.id.btnEditProfile:
                startActivity(new Intent(MyProfile.this, EditProfile.class));
                break;
        }
    }

    @Override
    public void response(String strresponse, String webservice) {
        Log.i("reso", strresponse);
        if (webservice.equals(URL.LOG_OUT)) {
            try {
                JSONObject jsonObject = new JSONObject(strresponse);
                if (jsonObject.getString("userLogout").equals("1")) {
                    Toast.makeText(MyProfile.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    /*if (com.tms.activity.Home.mActivity != null) {
                        com.tms.activity.Home.mActivity.finish();
                    }
                    if (DriverDetails.driverDetils != null) {
                        DriverDetails.driverDetils.finish();
                    }
                    if (Receipt.ratingActivity != null) {
                        Receipt.ratingActivity.finish();
                    }*/
                    startActivity(new Intent(MyProfile.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    mPrefrnce.clearPrefRence();
                    dialog.dismiss();
                    finish();
                } else {
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            try {
                JSONObject mJsonObject = new JSONObject(strresponse);
                JSONObject mJsonObject2 = mJsonObject
                        .getJSONObject("getUserDetail");
                mPrefrnce.setFirstName(mJsonObject2.getString("firstName"));
                mPrefrnce.setLastname(mJsonObject2.getString("lastName"));
                mPrefrnce.setEmail(mJsonObject2.getString("emailAddress"));
                mPrefrnce.setPhone(mJsonObject2.getString("phone"));
                mPrefrnce.setCountryCode(mJsonObject2.getString("countryCode"));
                mPrefrnce.setPhoto(mJsonObject2.getString("uImage"));
                mPrefrnce.setImageUrl(mJsonObject2.getString("uImage"));
                mPrefrnce.setImageName(mJsonObject2.getString("userImage"));
                txtuserEmail.setText(mPrefrnce.getEmail());
                txtFUserName.setText(mPrefrnce.getFirstName());
                txtLUserName.setText(mPrefrnce.getLastname());
                StringBuffer mBuffer = new StringBuffer();
                for (int i = 0; i < mPrefrnce.getPassword().length(); i++) {
                    mBuffer.append("*");
                }
                txtuserPassword.setText(mBuffer.toString());
                txtuserPhone.setText(mPrefrnce.getCountryCode() + " "
                        + mPrefrnce.getPhone());

                if (!mJsonObject2.getString("uImage").equals("")) {
                    Picasso.with(MyProfile.this).load(mJsonObject2.getString("uImage"))
                            .resize(200, 200).transform(new CircleTransform())
                            .into(mImageView);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void showDialog(String title, String message) {

        if (MyProfile.this != null) {
            dialog = new Dialog(MyProfile.this,
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
            RelativeLayout layout = (RelativeLayout) dialog
                    .findViewById(R.id.layoutYesNo);
            layout.setVisibility(View.VISIBLE);
            btnYes.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
    

                    new WebServiceAsynk(URL.BASE_URL + URL.LOG_OUT + mPrefrnce.getUserId(), MyProfile.this, MyProfile.this, true, URL.LOG_OUT).execute();
                    dialog.dismiss();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }


}
