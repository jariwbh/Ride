package com.tms.newui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tms.R;
import com.tms.utility.SendXmlAsync;
import com.tms.utility.URL;
import com.tms.utility.XmlListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by viren on 7/1/18.
 */

public class RequestBooking extends Fragment {
    private String bookingId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_loading_request, container, false);

        bookingId = getArguments().getString("bookingId");


        Button btnCancelRequest = (Button) view.findViewById(R.id.btnCancelBooking);
        // call to cancel booking
        btnCancelRequest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><cancelBookingReq><bookingId><![CDATA["
                        + bookingId + "]]></bookingId></cancelBookingReq>";
                new SendXmlAsync(URL.BASE_URL + URL.CANCEL_REQUEST, xml,
                        new XmlListener() {
                            @Override
                            public void onResponse(String respose) {
                                if (respose.contains("cancelBookingReq")) {
                                    try {
                                        JSONObject mJsonObject = new JSONObject(respose);
                                        showDialog("Booking Cancelled !",
                                                mJsonObject.getString("message"));
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, getActivity(), true)
                        .execute();
            }
        });

        if (getArguments().getString("via").equals("auto")) {
            btnCancelRequest.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><arrivingDriver><riderId><![CDATA["
                + getArguments().getString("userid")
                + "]]></riderId><bookingId><![CDATA["
                + bookingId + "]]></bookingId></arrivingDriver>";
        new SendXmlAsync(URL.BASE_URL + URL.LOADING_REQUEST, xml,
                new XmlListener() {
                    @Override
                    public void onResponse(String respose) {
                        parseResponse(respose);
                    }
                }, getActivity(), false).execute();

    }

    private void parseResponse(String respose) {
        try {
            JSONObject mJsonObject = new JSONObject(respose);
            if (mJsonObject.getString("arrivingDriver")
                    .equalsIgnoreCase("-2")) {
                /*Toast.makeText(getActivity(),
                        mJsonObject.getString("message"),
                        Toast.LENGTH_SHORT).show();*/
                showDialog("Booking Cancelled !",
                        mJsonObject.getString("message"));
            } else if (mJsonObject.getString("arrivingDriver")
                    .equalsIgnoreCase("-3")) {

                String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><arrivingDriver><riderId><![CDATA["
                        + getArguments().getString("userid")
                        + "]]></riderId><bookingId><![CDATA["
                        + bookingId
                        + "]]></bookingId></arrivingDriver>";
                new SendXmlAsync(URL.BASE_URL + URL.LOADING_REQUEST,
                        xml, new XmlListener() {
                    @Override
                    public void onResponse(String respose) {
                        parseResponse(respose);
                    }
                }, getActivity(),
                        false).execute();
            } else if (mJsonObject.getString("arrivingDriver")
                    .equalsIgnoreCase("-1")) {
                showDialog("Booking Cancelled !",
                        mJsonObject.getString("message"));
            } else {
                Bundle bundle = new Bundle();
                    JSONObject mJsonObject2 = mJsonObject
                            .getJSONObject("arrivingDriver");
                   /* startActivity(new Intent(getActivity(),
                            com.tms.activity.DriverDetails.class)*/
                bundle.putString("bookingId", bookingId);
                bundle.putString(
                        "driverName",
                        mJsonObject2
                                .getString("driverName"));
                bundle.putString(
                        "driverPhone",
                        mJsonObject2
                                .getString("driverPhone"));
                bundle.putString("taxiModel",
                        mJsonObject2.getString("taxiModel"));
                bundle.putString(
                        "taxiNumber",
                        mJsonObject2
                                .getString("taxiNumber"));
                bundle.putString("taxiType",
                        mJsonObject2.getString("taxiType"));
                bundle.putString(
                        "driverImage",
                        mJsonObject2
                                .getString("driverImage"));
                bundle.putString("rating",
                        mJsonObject2.getString("rating"));
                bundle.putString("LAT",
                        mJsonObject2.getString("bookLat"));
                bundle.putString("LONG",
                        mJsonObject2.getString("bookLong"));
                bundle.putString("userid",getArguments().getString("userid"));
                bundle.putString("ETA", mJsonObject2.getString("ETA"));
                DriverViewFragment driverViewFragment = new DriverViewFragment();
                driverViewFragment.setArguments(bundle);
                ((MainActivity4) getActivity()).showFragment(driverViewFragment);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void showDialog(String s, String message) {
        ((MainActivity4) getActivity()).bookingRequestCancelled(s,message);
    }

}
