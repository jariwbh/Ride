package com.tms.activity;

import android.R.style;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tms.R;
import com.tms.newui.MainActivity4;
import com.tms.utility.CheckInternetConnectivity;
import com.tms.utility.ResponseListener;
import com.tms.utility.SendXmlAsync;
import com.tms.utility.ToroSharedPrefrnce;
import com.tms.utility.URL;
import com.tms.utility.Utility;
import com.tms.utility.UtilsSingleton;
import com.tms.utility.XmlListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.fabric.sdk.android.Fabric;

/**
 * Splash screen of application It also fetch user current location latitude and
 * longitude and update to server
 *
 * @author arvind.agarwal
 */
public class Splash extends Activity implements XmlListener, ResponseListener
        , LocationListener {

    public static float anglenew;
    protected final IntentFilter mIntentFilter = new IntentFilter(
            ConnectivityManager.CONNECTIVITY_ACTION); // A filter for a BR. We
    // an
    // want to listen to
    // internet changes
    protected final ConnectionDetector mConnectionDetector = new ConnectionDetector(); // Creating
    LocationManager mLocationManager;
    String provider;
    String strGcmRegId;
    ToroSharedPrefrnce mPrefrnce;
    double preLat = 0, preLng = 0;
    Handler mHandler;
    com.tms.utility.MyLocation myLocation;

    Boolean bool = true;
    Location location = null;

    RelativeLayout netRel;
    com.tms.utility.MyLocation.LocationResult locationResultObj;

    private Typeface typeFace;
    boolean isPermissionGranted;

    public static void AskRuntimePermission(String[] permission, Integer requestCode, Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                permission[0]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity, permission[0])) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{
                                "android.permission.ACCESS_COARSE_LOCATION",
                                "android.permission.ACCESS_FINE_LOCATION"
                        }, requestCode);
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{
                                "android.permission.ACCESS_COARSE_LOCATION",
                                "android.permission.ACCESS_FINE_LOCATION",
                        }, requestCode);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);
        typeFace = Typeface.createFromAsset(getAssets(), Utility.TYPE_FACE_HELVATICA_NEUE_LIGHT);
        ((TextView) findViewById(R.id.logo)).setTypeface(typeFace);
        //Crittercism.init(getApplicationContext(), "5257e581e432f57758000001");
        mPrefrnce = new ToroSharedPrefrnce(this);
        /* Setting car type */
        // 1=regular 2=suv 3=black
        getHashKey();
        /***********************************************/
        //UtilsSingleton us=new UtilsSingleton();
        //System.out.println(us);

        PreferenceManager.getDefaultSharedPreferences(Splash.this).edit()
                .putInt("type", 1).commit();
//		GCMRegistrar.checkDevice(this);

//		GCMRegistrar.checkManifest(this);
//		strGcmRegId = GCMRegistrar.getRegistrationId(Splash.this);

//		strGcmRegId = FirebaseInstanceId.getInstance().getToken();
        if (mPrefrnce.getGcm() == null || mPrefrnce.getGcm().equals("")) {
            // Registration is not present, register now
            // with GCM
//			GCMRegistrar.register(Splash.this,
//					com.tms.GCMCommonUtilities.SENDER_ID);
//
//			strGcmRegId = GCMRegistrar.getRegistrationId(Splash.this);
//			Log.i("regId", "regId:" + strGcmRegId);
//			mPrefrnce.setGcm(strGcmRegId);
            if (FirebaseInstanceId.getInstance().getToken() != null) {
                mPrefrnce.setGcm(FirebaseInstanceId.getInstance().getToken());
                strGcmRegId = mPrefrnce.getGcm();
            }
        } else {
            strGcmRegId = mPrefrnce.getGcm();
        }

        netRel = (RelativeLayout) findViewById(R.id.net_rel);

       /* System.out.println("longitude >>>>>>>>>>>>>>>>>>>>"+UtilsSingleton.getInstance());
        UtilsSingleton us=new UtilsSingleton();
        System.out.println("longitude <<<<<<<<<<<<<<<<<<"+us);*/

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if ((ContextCompat.checkSelfPermission(Splash.this,
                    "android.permission.ACCESS_COARSE_LOCATION") != PackageManager.PERMISSION_GRANTED)
                    && (ContextCompat.checkSelfPermission(Splash.this,
                    "android.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED)) {
                isPermissionGranted = false;
                AskRuntimePermission(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 101, Splash.this);
            }else{
                isPermissionGranted = true;
            }
        }else{
            isPermissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isPermissionGranted = true;
                } else {
                    showDialog(getResources().getString(R.string.gpsPermission), getResources().getString(R.string.gpsError));
                }

            }
        if (CheckInternetConnectivity.checkinternetconnection(Splash.this)) {

            mHandler = new Handler();

            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {


                    if (Utility.isPaymentGatewayOn) {

                        // TODO Auto-generated method stub
                        if (!mPrefrnce.getUserId().equalsIgnoreCase("")
                                && mPrefrnce.isAccountVerified()) {

                            startActivity(new Intent(Splash.this, MainActivity4.class));
                            //startActivity(new Intent(Splash.this, TermsCondition.class));
//						if(mPrefrnce.getBookingId().equals("0")&&mPrefrnce.getPaymentStatus().equals("0")){
//
//						}else{
//							startActivity(new Intent(Splash.this, ChangeCardType.class));
//						}

                        } else {

                            UtilsSingleton.getInstance().setCurrent_lat(0.0d);

                            UtilsSingleton.getInstance().setCurrent_longi(0.0d);
                            startActivity(new Intent(Splash.this, Login.class));
                            //startActivity(new Intent(Splash.this, TermsCondition.class));

                        }
                    } else {


                        if (!mPrefrnce.getUserId().equalsIgnoreCase("")
                                && mPrefrnce.isAccountVerified()) {

                            startActivity(new Intent(Splash.this, MainActivity4.class));
                            //startActivity(new Intent(Splash.this, TermsCondition.class));
//							if(mPrefrnce.getBookingId().equals("0")&&mPrefrnce.getPaymentStatus().equals("0")){
//
//							}else{
//								startActivity(new Intent(Splash.this, ChangeCardType.class));
//							}

                        } else {

                            UtilsSingleton.getInstance().setCurrent_lat(0.0d);

                            UtilsSingleton.getInstance().setCurrent_longi(0.0d);
                            startActivity(new Intent(Splash.this, Login.class));
                            //startActivity(new Intent(Splash.this, TermsCondition.class));

                        }


                    }


                    finish();

                    // }

                }
            }, 2000);

        }
    }

    @Override
    public void onResponse(String respose) {
        // TODO Auto-generated method stub
        Log.i("Resp", respose);

    }

    @Override
    public void response(String strresponse, String webservice) {
        // TODO Auto-generated method stub

        if (bool) {

            JSONObject mJsonObject = null;

            Log.i("response", strresponse);
            if (strresponse.contains("latitude")) {

                try {
                    mJsonObject = new JSONObject(strresponse);

                    if (!mJsonObject.getString("latitude").equals("")
                            && !mJsonObject.getString("longitude").equals("")) {
                        UtilsSingleton.getInstance().setCurrent_lat(
                                Double.parseDouble(mJsonObject
                                        .getString("latitude")));


                        UtilsSingleton.getInstance().setCurrent_longi(
                                Double.parseDouble(mJsonObject
                                        .getString("longitude")));
                        if (!mPrefrnce.getUserId().equalsIgnoreCase("")) {

                            String xmlLocation = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><updateLocation><userId><![CDATA["
                                    + mPrefrnce.getUserId()
                                    + "]]></userId><latitude><![CDATA["
                                    + mJsonObject.getString("latitude")
                                    + "]]></latitude><longitude><![CDATA["
                                    + mJsonObject.getString("longitude")
                                    + "]]></longitude></updateLocation>";
                            new SendXmlAsync(
                                    URL.BASE_URL + URL.UPDATE_LOCATION,
                                    xmlLocation, Splash.this, Splash.this,
                                    false).execute();

                        }

                    } else {

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        }

    }

    private void showDialog(String title, String message) {

        final Dialog dialog = new Dialog(Splash.this,
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
                finish();
                dialog.dismiss();

            }
        });

        txtTitle.setText(title);
        txtMessage.setText(message);

        dialog.show();

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

//        mHandler.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
                if (isPermissionGranted) {
                    if (CheckInternetConnectivity.checkinternetconnection(Splash.this)) {


                        mHandler = new Handler();

                        mHandler.postDelayed(new Runnable() {

                            @Override
                            public void run() {


                                if (Utility.isPaymentGatewayOn) {

                                    // TODO Auto-generated method stub
                                    if (!mPrefrnce.getUserId().equalsIgnoreCase("")
                                            && mPrefrnce.isAccountVerified()) {

                                        startActivity(new Intent(Splash.this, MainActivity4.class));
                                        //startActivity(new Intent(Splash.this, TermsCondition.class));
//						if(mPrefrnce.getBookingId().equals("0")&&mPrefrnce.getPaymentStatus().equals("0")){
//
//						}else{
//							startActivity(new Intent(Splash.this, ChangeCardType.class));
//						}

                                    } else {

                                        UtilsSingleton.getInstance().setCurrent_lat(0.0d);

                                        UtilsSingleton.getInstance().setCurrent_longi(0.0d);
                                        startActivity(new Intent(Splash.this, Login.class));
                                        //startActivity(new Intent(Splash.this, TermsCondition.class));

                                    }
                                } else {


                                    if (!mPrefrnce.getUserId().equalsIgnoreCase("")
                                            && mPrefrnce.isAccountVerified()) {

                                        startActivity(new Intent(Splash.this, MainActivity4.class));
                                        //startActivity(new Intent(Splash.this, TermsCondition.class));
//							if(mPrefrnce.getBookingId().equals("0")&&mPrefrnce.getPaymentStatus().equals("0")){
//
//							}else{
//								startActivity(new Intent(Splash.this, ChangeCardType.class));
//							}

                                    } else {

                                        UtilsSingleton.getInstance().setCurrent_lat(0.0d);

                                        UtilsSingleton.getInstance().setCurrent_longi(0.0d);
                                        startActivity(new Intent(Splash.this, Login.class));
                                        //startActivity(new Intent(Splash.this, TermsCondition.class));

                                    }


                                }


                                finish();

                                // }

                            }
                        }, 2000);


                    } else {
                        showDialog("Alert!", "No Internet Connection");
                    }
                }
//            }
//        },2000);
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

        if (location != null) {

            UtilsSingleton.getInstance().setCurrent_lat(location.getLatitude());
            UtilsSingleton.getInstance().setCurrent_longi(location.getLongitude());
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                if ((ContextCompat.checkSelfPermission(Splash.this,
                        "android.permission.ACCESS_COARSE_LOCATION") != PackageManager.PERMISSION_GRANTED)
                        && (ContextCompat.checkSelfPermission(Splash.this,
                        "android.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED)) {
                    mLocationManager.removeUpdates(Splash.this);
                }
            } else {
                mLocationManager.removeUpdates(Splash.this);
            }


        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    private void showDialog(String title, String message, final int type) {

        final Dialog dialog = new Dialog(Splash.this,
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
     * Get MD5 and hash key
     */
    public void getHashKey() {
        PackageInfo info;
        try {

            String packageName = getApplicationContext().getPackageName();

            Log.e("Package Name", packageName);

            info = getPackageManager().getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
                //System.out.println("Hash key" + something);
            }

        } catch (NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }

    }

    protected class ConnectionDetector extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            // TODO Auto-generated method stub

            Log.d("app", "Network connectivity change");
            if (intent.getExtras() != null) {
                NetworkInfo ni = (NetworkInfo) intent.getExtras().get(
                        ConnectivityManager.EXTRA_NETWORK_INFO);

                if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                    Log.i("app", "Network " + ni.getTypeName() + " connected");

                    netRel.setVisibility(View.GONE);

                    onResume();

                } else if (intent.getBooleanExtra(
                        ConnectivityManager.EXTRA_NO_CONNECTIVITY,
                        Boolean.FALSE)) {

                }

            }

        }
    }

}