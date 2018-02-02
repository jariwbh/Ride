package com.tms.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.style;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tms.R;
import com.tms.newui.MainActivity4;
import com.tms.utility.SendXmlAsync;
import com.tms.utility.ToroSharedPrefrnce;
import com.tms.utility.URL;
import com.tms.utility.Utility;
import com.tms.utility.XmlListener;

/**
 * @author arvind.agarwal
 *         this class fetch the info about driver and sends the data to Driver details screen
 */
public class LoadingRequest extends Activity implements XmlListener {
    ToroSharedPrefrnce mPrefrnce;
    String bookingId;
    Intent mIntent;

    Dialog dialog;

    public static Activity loadingRequest;

    // bool whether activity exists
    boolean bool = true;

    boolean isCancelled = false, isActivityActivated = true;
    private Typeface typeFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mPrefrnce = new ToroSharedPrefrnce(LoadingRequest.this);
        mIntent = getIntent();
        bookingId = mIntent.getExtras().getString("bookingId");
        setContentView(R.layout.activity_loading_request);
        typeFace = Typeface
                .createFromAsset(getAssets(), Utility.TYPE_FACE);

        loadingRequest = this;
        Button btnCancelRequest = (Button) findViewById(R.id.btnCancelBooking);
        btnCancelRequest.setTypeface(typeFace);

        if (mIntent.getStringExtra("via").equals("auto")) {
            btnCancelRequest.setVisibility(View.GONE);
        }


        // call to cancel booking
        btnCancelRequest.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                isCancelled = true;
                String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><cancelBookingReq><bookingId><![CDATA["
                        + bookingId + "]]></bookingId></cancelBookingReq>";
                new SendXmlAsync(URL.BASE_URL + URL.CANCEL_REQUEST, xml,
                        LoadingRequest.this, LoadingRequest.this, true)
                        .execute();
            }
        });

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub

        super.onResume();


        // call for driver details
        isActivityActivated = true;
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><arrivingDriver><riderId><![CDATA["
                + mPrefrnce.getUserId()
                + "]]></riderId><bookingId><![CDATA["
                + bookingId + "]]></bookingId></arrivingDriver>";
        new SendXmlAsync(URL.BASE_URL + URL.LOADING_REQUEST, xml,
                LoadingRequest.this, LoadingRequest.this, false).execute();
    }

	
	/*
	 * (non-Javadoc)
	 * @see com.ontimecab.utility.XmlListener#onResponse(java.lang.String)
	 * 
	 * interface to handle response
	 */

    @Override
    public void onResponse(String respose) {
        // TODO Auto-generated method stub

        if (bool) {


            //cancel request response

            if (respose.contains("cancelBookingReq")) {
                try {
                    JSONObject mJsonObject = new JSONObject(respose);
                    if (mJsonObject.getString("cancelBookingReq")
                            .equalsIgnoreCase("1")) {

                        bool = false;
                        showDialog("Booking Cancelled !",
                                mJsonObject.getString("message"));

                    } else if (mJsonObject.getString("cancelBookingReq")
                            .equalsIgnoreCase("-1")) {
						/*
						 * Toast.makeText(LoadingRequest.this,
						 * mJsonObject.getString("message"),
						 * Toast.LENGTH_SHORT).show();
						 */
                    } else if (mJsonObject.getString("cancelBookingReq")
                            .equalsIgnoreCase("-2")) {
						/*
						 * Toast.makeText(LoadingRequest.this,
						 * mJsonObject.getString("message"),
						 * Toast.LENGTH_SHORT).show();
						 */
                    } else if (mJsonObject.getString("cancelBookingReq")
                            .equalsIgnoreCase("-3")) {
                        bool = false;
                        showDialog("Booking Cancelled !",
                                mJsonObject.getString("message"));

                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (respose.contains("acceptBooking")) {

                try {
                    JSONObject mJsonObject = new JSONObject(respose);
                    if (mJsonObject.getString("acceptBooking")
                            .equalsIgnoreCase("1")) {
                        Toast.makeText(LoadingRequest.this,
                                mJsonObject.getString("message"),
                                Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(LoadingRequest.this,
                                mJsonObject.getString("message"),
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {

                Log.i("response", "" + respose);
                try {
                    JSONObject mJsonObject = new JSONObject(respose);
                    if (mJsonObject.getString("arrivingDriver")
                            .equalsIgnoreCase("-2")) {
                        Toast.makeText(LoadingRequest.this,
                                mJsonObject.getString("message"),
                                Toast.LENGTH_SHORT).show();
                    } else if (mJsonObject.getString("arrivingDriver")
                            .equalsIgnoreCase("-3")) {

                        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><arrivingDriver><riderId><![CDATA["
                                + mPrefrnce.getUserId()
                                + "]]></riderId><bookingId><![CDATA["
                                + bookingId
                                + "]]></bookingId></arrivingDriver>";
                        new SendXmlAsync(URL.BASE_URL + URL.LOADING_REQUEST,
                                xml, LoadingRequest.this, LoadingRequest.this,
                                false).execute();
                    } else if (mJsonObject.getString("arrivingDriver")
                            .equalsIgnoreCase("-1")) {
                        bool = false;
                        showDialog("Booking Cancelled !",
                                mJsonObject.getString("message"));

                    } else {

                        if (!isCancelled && isActivityActivated) {
                            JSONObject mJsonObject2 = mJsonObject
                                    .getJSONObject("arrivingDriver");
                            startActivity(new Intent(LoadingRequest.this,
                                    com.tms.activity.DriverDetails.class)
                                    .putExtra("bookingId", bookingId)
                                    .putExtra(
                                            "driverName",
                                            mJsonObject2
                                                    .getString("driverName"))
                                    .putExtra(
                                            "driverPhone",
                                            mJsonObject2
                                                    .getString("driverPhone"))
                                    .putExtra("taxiModel",
                                            mJsonObject2.getString("taxiModel"))
                                    .putExtra(
                                            "taxiNumber",
                                            mJsonObject2
                                                    .getString("taxiNumber"))
                                    .putExtra("taxiType",
                                            mJsonObject2.getString("taxiType"))
                                    .putExtra(
                                            "driverImage",
                                            mJsonObject2
                                                    .getString("driverImage"))
                                    .putExtra("rating",
                                            mJsonObject2.getString("rating"))
                                    .putExtra("LAT",
                                            mJsonObject2.getString("bookLat"))
                                    .putExtra("LONG",
                                            mJsonObject2.getString("bookLong"))
                                    .putExtra("ETA",
                                            mJsonObject2.getString("ETA"))
                                    .setFlags(
                                            Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                                                    | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP));

                            //"bookLat":"30.723920983758312","bookLong":"76.84657104313374"
                            finish();

                            bool = false;
                            // Toast.makeText(LoadingRequest.this,
                            // mJsonObject.getString("message"),
                            // Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    }

    private void showDialog(String title, String message) {

        if (LoadingRequest.this != null) {

            dialog = new Dialog(LoadingRequest.this,
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

                    dialog = null;

                    finish();
                    startActivity(new Intent(LoadingRequest.this, MainActivity4 .class));

                }
            });

            txtTitle.setText(title);
            txtMessage.setText(message);

            dialog.show();
        }

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        if (dialog != null) {
            dialog.dismiss();
        }
        bool = false;
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        isActivityActivated = false;
    }
}
