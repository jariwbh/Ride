package com.tms.newui;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
    private static final String LOG_TAG = "Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "your_api_key";
    private ArrayList<Result> resultList;
    private Context context = null;

    public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }


    @Override
    public int getCount() {
        if (resultList != null)
            return resultList.size();
        else
            return 0;
    }

    @Override
    public Result getItem(int index) {
        return resultList.get(index);
    }

    public class Result{
        String mainText;
        String secondText;
    }

    public ArrayList<Result> autocomplete(String input) {
        ArrayList<Result> resultList = null;
       // ArrayList<String> descriptionList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + com.tms.utility.URL.GOOGLEAPIKEY);
            sb.append("&components=country:CA");
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
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            Log.d(LOG_TAG, jsonResults.toString());
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            //descriptionList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                try {
                    Result result = new Result();
                    JSONObject jsonObject = predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting");
                    result.mainText = jsonObject.getString("main_text");
                    result.secondText=jsonObj.getString("secondary_text");
                    resultList.add(result);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }


    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = autocomplete(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {

                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}