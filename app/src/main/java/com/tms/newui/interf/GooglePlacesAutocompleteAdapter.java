package com.tms.newui.interf;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.tms.R;


public class GooglePlacesAutocompleteAdapter extends BaseAdapter implements
        Filterable {
    public ArrayList<AddressData> resultList = new ArrayList<AddressData>();

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    String TAG = "VIREN";
    Context context;
    String serverKey;

    LayoutInflater inflater;

    public AddressFilter addressFilter = null;
    public ArrayList<AddressData> addressList;

    public GooglePlacesAutocompleteAdapter(Context context,
                                           int textViewResourceId) {
        super();
        this.context = context;
        serverKey = com.tms.utility.URL.GOOGLEAPIKEY;
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public AddressData getItem(int index) {
        return resultList.get(index);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<AddressData> autocomplete(String input) {

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            String countrycode = getUserCountry(context);
            countrycode ="ca";
            StringBuilder sb = new StringBuilder(PLACES_API_BASE
                    + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + serverKey);
            sb.append("&components=country:" + countrycode);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Places API URL", e);
            return addressList;
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
            return addressList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            addressList = new ArrayList<AddressData>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                JSONObject addressObj = predsJsonArray.getJSONObject(i);
                JSONArray termsArray = addressObj.getJSONArray("terms");
                JSONObject termsObj = termsArray.getJSONObject(0);
                String captionString = termsObj.getString("value");
                String descriptionString = addressObj.getString("description");

                AddressData addressData = new AddressData(captionString,
                        descriptionString);
                addressList.add(addressData);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }

        return addressList;
    }

    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String networkCountry = tm.getNetworkCountryIso();
            if (networkCountry != null && networkCountry.length() == 2) { // network
                // country
                // code
                // is
                // available
                return networkCountry.toLowerCase();
            }

        } catch (Exception e) {
            return "ca";
        }
        return "ca";
    }

    @Override
    public Filter getFilter() {
        addressFilter = new AddressFilter();
        return addressFilter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_address, null);

            viewHolder = new ViewHolder();
            viewHolder.tvCaption = (TextView) convertView
                    .findViewById(R.id.tv_address_caption);
            viewHolder.tvDescription = (TextView) convertView
                    .findViewById(R.id.tv_address_description);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        try {
            AddressData addressData = resultList.get(position);
            viewHolder.tvCaption.setText(addressData.mainText);
            viewHolder.tvDescription.setText(addressData.secondText);
        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }

    private class ViewHolder {
        TextView tvCaption;
        TextView tvDescription;
    }

    @SuppressLint("DefaultLocale")
    private class AddressFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            if ((constraint != null) && (constraint.toString().length() > 0)) {
                try {
                    resultList = autocomplete(constraint.toString());

                    results.count = resultList.size();
                    results.values = resultList;
                } catch (Exception e) {
                    Log.e("TAG", "Error in Filtering results");
                }
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            try {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            } catch (Exception e) {
                Log.e("TAG", "Error in publishing results");
            }
        }
    }

}