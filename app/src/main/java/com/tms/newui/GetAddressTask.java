package com.tms.newui;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.tms.newui.interf.GetAddressFromLatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class GetAddressTask extends AsyncTask<Void, Void, String> {
    Context mContext;
    private GetAddressFromLatLng listner;
    LatLng latLng;


    public GetAddressTask(Context context, LatLng loc, GetAddressFromLatLng listner) {

        mContext = context;
        latLng = loc;
        this.listner = listner;
    }

    @Override
    protected String doInBackground(Void... params) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude,
                    latLng.longitude, 1);

        } catch (IOException e1) {
            return ("Error Try Again");

        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
            return ("Error Try Again");
        }

        if (addresses != null && addresses.size() > 0) {
            return  addresses.get(0).getAddressLine(0);
        } else {
            return "No address found";
        }
    }

    @Override
    protected void onPostExecute(String address) {
       this.listner.onGetAddressCompleted(address);
    }
}