package com.tms.newui;

import android.Manifest;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.tms.R;
import com.tms.activity.CancelCaseReciept;
import com.tms.activity.ChangeCardType;
import com.tms.utility.CheckInternetConnectivity;
import com.tms.utility.CircleTransform;
import com.tms.utility.MarkerMoving;
import com.tms.utility.ResponseListener;
import com.tms.utility.SendXmlAsync;
import com.tms.utility.ServiceResponce;
import com.tms.utility.URL;
import com.tms.utility.Utility;
import com.tms.utility.WebServiceAsynk;
import com.tms.utility.XmlListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by viren on 7/1/18.
 */

public class DriverViewFragment extends Fragment implements View.OnClickListener {
    TextView txtRating, txtUserName, txtPhone, txtTaxiModel, txtTaxiNo;
    ImageView driverImage, carIcon;
    RelativeLayout driverImageLayout;
    private int taxiType;
    String bookingId;
    Marker myLocationMarker, driverMarker;
    private MarkerMoving markerMoving;
    private Bitmap scaledBitmap;
    boolean isFirst = true;
    private Intent intentToReciept;
    private BottomSheetBehavior mBottomSheetBehavior;
    private TextView txtTime,txtDistance;
    private MainActivity4 activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_view, container, false);

        bookingId = getArguments().getString("bookingId");
        txtRating = (TextView) view.findViewById(R.id.txtRating);
        txtUserName = (TextView) view.findViewById(R.id.txtDriverName);
        txtPhone = (TextView) view.findViewById(R.id.txtDriverPhone);
        txtRating = (TextView) view.findViewById(R.id.txtRating);
        txtTaxiModel = (TextView) view.findViewById(R.id.txtCompanyName);
        txtTaxiNo = (TextView) view.findViewById(R.id.txtTaxiNo);
        driverImage = (ImageView) view.findViewById(R.id.imgDriverPic);
        //driverImageLayout = (RelativeLayout) view.findViewById(R.id.driverImageThumb);
        txtTime = (TextView)view.findViewById(R.id.txtime);

        txtPhone.setText("4.00"/*mIntent.getExtras().getString("driverPhone")*/);
       // txtRating.setText(getArguments().getString("rating"));
        txtTaxiModel.setText(getArguments().getString("taxiModel"));
        txtTaxiNo.setText(getArguments().getString("taxiNumber"));
        txtUserName.setText(getArguments().getString("driverName"));
        taxiType = Integer.parseInt(getArguments().getString("taxiType"));
        if (!getArguments().getString("driverImage").equalsIgnoreCase("")) {
            Picasso.with(getActivity())
                    .load(getArguments().getString("driverImage"))
                    .resize(90, 90).transform(new CircleTransform()).into(driverImage);
        }
        txtDistance = (TextView) view.findViewById(R.id.txtkm);
        ImageView btnMyLocation = (ImageView) view.findViewById(R.id.btnMyLocationNew);
        btnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity4) getActivity()).btnMyLocation.performClick();
            }
        });

        ImageView btnCall = (ImageView) view.findViewById(R.id.btnCallDriver);
        //btnCall.setTypeface(typeFaceHelvetica);
        btnCall.setOnClickListener(this);
        ImageView btnCancelTrip = (ImageView) view.findViewById(R.id.btnCancelTrip);
        //btnCancelTrip.setTypeface(typeFaceHelvetica);
        btnCancelTrip.setOnClickListener(this);
        ImageView btnSMS = (ImageView) view.findViewById(R.id.btnSMSDriver);
        //btnSMS.setTypeface(typeFaceHelvetica);
        btnSMS.setOnClickListener(this);
        View bottomSheet = view.findViewById(R.id.design_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markerMoving = new MarkerMoving();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnSMSDriver:
                checkSMSPermission();
                Intent sendSMS = new Intent(Intent.ACTION_SENDTO,
                        Uri.parse("smsto:" + txtPhone.getText().toString().trim()));
                startActivity(sendSMS);
                break;

            case R.id.btnCallDriver:
                String uri = "tel:" + txtPhone.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));
                checkCallPermission();
                startActivity(intent);
                break;
            case R.id.btnCancelTrip:
                showDialog1("Alert!", "Are you sure you want to cancel the booking?");
                break;

        }
    }

    public boolean checkCallPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CALL_PHONE},
                    10);
            //}
            return false;
        } else {
            return true;
        }
    }

    public boolean checkSMSPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.SEND_SMS},
                    11);
            //}
            return false;
        } else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String uri = "tel:" + txtPhone.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            } else {
                /*Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.cordinatorLayout),"Permission not granted", Snackbar.LENGTH_LONG)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });

                snackbar.show();*/
            }
        }else if (requestCode ==11){
            Intent sendSMS = new Intent(Intent.ACTION_SENDTO,
                    Uri.parse("smsto:" + txtPhone.getText().toString().trim()));
            startActivity(sendSMS);
        }
    }

    Dialog dialog;

    private void showDialog1(String title, String message) {
        dialog = new Dialog(getActivity(),
                android.R.style.Theme_Translucent_NoTitleBar);
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
        btnYes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><cancelBookingReq><bookingId><![CDATA["
                        + bookingId + "]]></bookingId></cancelBookingReq>";
                new SendXmlAsync(URL.BASE_URL + URL.CANCEL_REQUEST, xml,
                        new XmlListener() {
                            @Override
                            public void onResponse(String respose) {
                                cancelledResponseHandle(respose);
                            }
                        }, getActivity(), true)
                        .execute();

                dialog.dismiss();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        txtTitle.setText(title);
        txtMessage.setText(message);

        dialog.show();
    }

    private void cancelledResponseHandle(String respose) {
        if (respose.contains("cancelBookingReq")) {
            try {
                final JSONObject mJsonObject = new JSONObject(respose);
                if (mJsonObject.getString("cancelBookingReq").equalsIgnoreCase(
                        "1")) {
                    /*
					 * if (bool) { showDialog("Trip cancelled !",
					 * "You have cancelled the trip."); } else {
					 * showDialog("Trip cancelled !",
					 * "Driver has cancelled the trip.");
					 *
					 * }
					 */

					/*
					 * {"cancelBookingReq":"1","message":
					 * "Booking has been cancelled successfully."
					 * ,"payvia":"1","cancellationFare"
					 * :"25","paymentStatus":"1"} (7:23 PM) mandeep.singh:
					 * cancellationTime
					 */
                    // showDialog("Job cancelled !",
                    // "You have cancelled the job.");
                    // below commented 03/06/16
                    /*if (!mJsonObject.getString("declinePaymentStatus").equals("0")) {

                        UtilsSingleton.getInstance().setBookingId(bookingId);
                        UtilsSingleton.getInstance().setPaymentStatus(
                                mJsonObject.getString("declinePaymentStatus"));
                        // sharedPreference.setBookingId(bookingId);
                        // sharedPreference.setPaymentStatus(mJsonObject2.getString("paymentStatus"));
                        startActivity(new Intent(getActivity(),
                                ChangeCardType.class).putExtra("pending", "1"));
                    }*/  if (mJsonObject.getString("paymentStatus").equals("1")) {

                        String date = mJsonObject.getString("cancellationTime");
                        String fare = mJsonObject.getString("cancellationFare");
                        String payVia = mJsonObject.getString("payvia");


                        startActivity(new Intent(getActivity(),
                                CancelCaseReciept.class).putExtra(
                                Utility.PAYMENT_DECLINE_DATE_KEY, date)
                                .putExtra(Utility.PAYMENT_DECLINE_FARE_KEY,
                                        fare).putExtra(
                                        Utility.PAYMENT_DECLINE_PAYVIA,
                                        payVia));

                    } else {
                        showDialog("Trip cancelled !",
                                "You have cancelled the trip.");

                    }
                } else if (mJsonObject.getString("cancelBookingReq")
                        .equalsIgnoreCase("-1")) {
                    Toast.makeText(getActivity(),
                            mJsonObject.getString("message"),
                            Toast.LENGTH_SHORT).show();
                } else if (mJsonObject.getString("cancelBookingReq")
                        .equalsIgnoreCase("-2")) {
                    Toast.makeText(getActivity(),
                            mJsonObject.getString("message"),
                            Toast.LENGTH_SHORT).show();
                } else if (mJsonObject.getString("cancelBookingReq")
                        .equalsIgnoreCase("-4")) {
                    Toast.makeText(getActivity(),
                            mJsonObject.getString("message"),
                            Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void showDialog(String title, String message) {
        dialog = new Dialog(getActivity(),
                android.R.style.Theme_Translucent_NoTitleBar);
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
        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                ((MainActivity4) getActivity()).showFragment(new WhereToFragment());
            }
        });

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CheckInternetConnectivity
                .checkinternetconnection(getActivity())) {
            new WebServiceAsynk(URL.BASE_URL + URL.GET_DRIVER
                    + getArguments().getString("userid") + URL.LAT
                    + activity.sourceLatLng.latitude + URL.LNG
                    + activity.sourceLatLng.longitude
                    + URL.TAXI_TYPE + taxiType + "&bookingId=" + bookingId,
                    new ResponseListener() {
                        @Override
                        public void response(String strresponse, String webservice) {
                            getDriverDetail(strresponse);
                        }
                    }, getActivity(), true, "first")
                    .execute();

        } else {
            Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_LONG)
                    .show();
        }
    }
    BitmapFactory.Options options = new BitmapFactory.Options();

    private void getDriverDetail(String strresponse) {

        try{
            JSONObject mJsonObject = new JSONObject(strresponse);
            String getDriverById = mJsonObject.getString("getDriversById");
            if (getDriverById.equals("-3")) {
            JSONObject mJsonObject2 = mJsonObject
                        .getJSONObject("getDriversById");
            if (mJsonObject2.getString("cancelStatus").equals("1")) {
                    if (!mJsonObject2.getString("declinePaymentStatus").equals("0")) {

                       /* UtilsSingleton.getInstance().setBookingId(bookingId);
                        UtilsSingleton.getInstance().setPaymentStatus(
                                mJsonObject2.getString("declinePaymentStatus"));*/
                        startActivity(new Intent(getActivity(),
                                ChangeCardType.class).putExtra("pending", "1"));
                } else if (mJsonObject2.getString("paymentChargeStatus").equals("1")) {
                        String date = mJsonObject2.getString("cancellationTime");
                        String fare = mJsonObject2.getString("cancelFare");
                        String payVia = mJsonObject2.getString("payvia");

                        startActivity(new Intent(getActivity(),
                                CancelCaseReciept.class).putExtra(
                                Utility.PAYMENT_DECLINE_DATE_KEY, date)
                                .putExtra(Utility.PAYMENT_DECLINE_FARE_KEY,
                                        fare).putExtra(
                                        Utility.PAYMENT_DECLINE_PAYVIA,
                                        payVia));
                    } else {
                        showDialog("Alert!", mJsonObject.getString("message"));
                    }
                } else {
                    showDialog("Trip cancelled !",
                            "Driver has cancelled the trip.");
                }
            } else {
                JSONObject mJsonObject2 = mJsonObject
                        .getJSONObject("getDriversById");
                Double lat = mJsonObject2.getDouble("driverLat");
                Double lng = mJsonObject2.getDouble("driverLong");
                try {
                    String eta = ((MainActivity4) getActivity()).sharedPreference.getTime();
                    txtTime.setText(eta + "");
                    txtDistance.setText(((MainActivity4) getActivity()).sharedPreference.getDistance());
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (driverMarker == null) {
                    driverMarker =((MainActivity4)getActivity()).mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng)));
                    showRoute();
                } else {
                    driverMarker.setPosition(new LatLng(lat, lng));
                    showRoute();
                }
                if (isFirst == true) {
                    markerMoving.reScheduleTimer(
                            getActivity(),
                            URL.BASE_URL
                                    + URL.GET_DRIVER
                                    + getArguments().getString("userid")
                                    + URL.LAT + activity.sourceLatLng.latitude
                                    + URL.LNG + activity.sourceLatLng.longitude
                                    + URL.TAXI_TYPE
                                    + taxiType + "&bookingId=" + bookingId,
                            new ServiceResponce() {
                                @Override
                                public void onResp(String responce) {
                                    getMarkerResponse(responce);
                                }
                            });
                    isFirst = false;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void showRoute() {
        try {
            String destinationID = null;
            String sourceID = "";

            double firstLat = driverMarker.getPosition().latitude;
            double firstLong = driverMarker.getPosition().longitude;
            if (firstLat != 0 && firstLong != 0) {
                sourceID = firstLat + "," + firstLong;
            }


            ((MainActivity4) getActivity()).showRouteFromRiderToDriver(sourceID,destinationID);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getMarkerResponse(String responce) {
        try {
            String eta = ((MainActivity4) getActivity()).sharedPreference.getTime();
                txtTime.setText(eta + "");
                txtDistance.setText(((MainActivity4) getActivity()).sharedPreference.getDistance());
        }catch (Exception e){
            e.printStackTrace();
        }
             try {
                JSONObject mJsonObject = new JSONObject(responce);
		        String getDriverById = mJsonObject.getString("getDriversById");

                if (getDriverById.equalsIgnoreCase("-3")) {
                    if (driverMarker != null) {
                        driverMarker.remove();

                        JSONObject mJsonObject2 = mJsonObject
                                .getJSONObject("getDriversById");

                        if (mJsonObject2.getString("cancelStatus").equals("1")) {

                            if (!mJsonObject2.getString("declinePaymentStatus")
                                    .equals("0")) {

                                /*UtilsSingleton.getInstance().setBookingId(
                                        bookingId);
                                UtilsSingleton
                                        .getInstance()
                                        .setPaymentStatus(
                                                mJsonObject2
                                                        .getString("declinePaymentStatus"));*/
                                startActivity(new Intent(getActivity(),
                                        ChangeCardType.class).putExtra(
                                        "pending", "1"));


                            } else if (mJsonObject2.getString("paymentChargeStatus")
                                    .equals("1")) {
                                String date = mJsonObject2
                                        .getString("cancellationTime");
                                String fare = mJsonObject2
                                        .getString("cancelFare");

                                String payVia = mJsonObject2
                                        .getString("payvia");

                                startActivity(new Intent(getActivity(),
                                        CancelCaseReciept.class)
                                        .putExtra(
                                                Utility.PAYMENT_DECLINE_DATE_KEY,
                                                date)
                                        .putExtra(
                                                Utility.PAYMENT_DECLINE_FARE_KEY,
                                                fare).putExtra(
                                                Utility.PAYMENT_DECLINE_PAYVIA,
                                                payVia));
                            } else {

                                showDialog("Alert!",
                                        mJsonObject.getString("message"));
                            }

                        } else {

                            showDialog("Trip cancelled !",
                                    "Driver has cancelled the trip.");
                        }
                    }
                } else {
                    JSONObject mJsonObject2 = mJsonObject
                            .getJSONObject("getDriversById");
                    Double lat = mJsonObject2.getDouble("driverLat");
                    Double lng = mJsonObject2.getDouble("driverLong");
                    if (driverMarker == null) {
                        driverMarker = ((MainActivity4)getActivity()).mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lng)));
                        showRoute();
                    } else {
                        driverMarker.setPosition(new LatLng(lat, lng));
                        showRoute();
                    }
                    JSONArray mJsonArray = mJsonObject2
                            .getJSONArray("tripStatus");

                    if (!mJsonObject2.getString("paymentStatus").equals("0")) {

                       /* UtilsSingleton.getInstance().setBookingId(bookingId);
                        UtilsSingleton.getInstance().setPaymentStatus(
                                mJsonObject2.getString("paymentStatus"));*/
                        // sharedPreference.setBookingId(bookingId);
                        // sharedPreference.setPaymentStatus(mJsonObject2.getString("paymentStatus"));
                        startActivity(new Intent(getActivity(),
                                ChangeCardType.class).putExtra("pending", "1"));

                    } else if (mJsonArray.length() > 0) {
                        JSONObject mObject = mJsonArray.getJSONObject(0);
                        Bundle bundle = new Bundle();
                                bundle.putString("tripAmount",
                                        mObject.getString("tripAmount"));
                                bundle.putString("completedDate",
                                        mObject.getString("completedDate"));
                                bundle.putString("driverName",
                                        mObject.getString("driverName"));
                                bundle.putString("driverPhone",
                                        mObject.getString("driverPhone"));
                                bundle.putString("taxiModel",
                                        mObject.getString("taxiModel"));
                                bundle.putString("taxiNumber",
                                        mObject.getString("taxiNumber"));
                                bundle.putString("taxiType",
                                        mObject.getString("taxiType"));
                                bundle.putString("driverImage",
                                        mObject.getString("driverImage"));
                                bundle.putString("rating", mObject.getString("rating"));
                                bundle.putString("payVia", mObject.getString("payvia"));
                                bundle.putString("bookingId", bookingId);
                                bundle.putString("id", mJsonObject2.getString("id"));
                                bundle.putString("payvia",
                                        mObject.getString("driverImage"));
                        markerMoving.destroy();
                        if(driverMarker != null)
                            driverMarker.remove();
                        try {
                            ReceiptFragment receiptFragment = new ReceiptFragment();
                            receiptFragment.setArguments(bundle);
                            ((MainActivity4) getActivity()).showFragment(receiptFragment);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    if (mJsonObject2.getString("cancelStatus").equals("1")) {

                        if (!mJsonObject2.getString("declinePaymentStatus")
                                .equals("0")) {

                            /*UtilsSingleton.getInstance()
                                    .setBookingId(bookingId);
                            UtilsSingleton.getInstance().setPaymentStatus(
                                    mJsonObject2
                                            .getString("declinePaymentStatus"));*/
                            startActivity(new Intent(getActivity(),
                                    ChangeCardType.class).putExtra("pending",
                                    "1"));


                        } else if (mJsonObject2.getString("paymentChargeStatus")
                                .equals("1")) {
                            String date = mJsonObject2
                                    .getString("cancellationTime");
                            String fare = mJsonObject2.getString("cancelFare");
                            String payVia = mJsonObject2
                                    .getString("payvia");


                            startActivity(new Intent(getActivity(),
                                    CancelCaseReciept.class).putExtra(
                                    Utility.PAYMENT_DECLINE_DATE_KEY, date)
                                    .putExtra(Utility.PAYMENT_DECLINE_FARE_KEY,
                                            fare).putExtra(
                                            Utility.PAYMENT_DECLINE_PAYVIA,
                                            payVia));
                        }

                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

    }
}
