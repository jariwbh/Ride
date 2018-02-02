package com.tms.newui;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.tms.DataParser;
import com.tms.newui.interf.RouteInterface;
import com.tms.utility.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by viren on 7/12/17.
 */

public class RouteTask extends AsyncTask<String, Void, String> {
    Context context;
    RouteInterface listner;
    private String TAG = "RouteTask";
    private JSONObject distance,duration;

    public RouteTask(Context context, RouteInterface listner) {
        this.context = context;
        this.listner = listner;
    }

    @Override
    protected String doInBackground(String... place) {
        // For storing data from web service
        String data = "";
        Log.d(TAG, "Route task doInBackground");
        // Obtain browser key from https://code.google.com/apis/console
        String key = "&key=" + URL.GOOGLEAPIKEY.trim();

        /*String sourceID = "";
        if (target != null) {
            sourceID = target.latitude + "," + target.longitude;
        }

        String destinationID = "";
        if (destPlace.getLatLng() != null) {
            destinationID = destPlace.getLatLng().latitude + "," + destPlace.getLatLng().longitude;
        }*/

        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + place[0] + "&destination=" + place[1] + "&key=" + URL.GOOGLEAPIKEY.trim() + "&sensor=false&units=metric&mode=driving";

        try {
            // Fetching the data from web service in background
            data = downloadUrl(url);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        Log.d(TAG, url + " \n" + data);
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        try {
            ParserTask parserTask = new ParserTask(listner);
            parserTask.execute(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * A class to parse the Google Places in JSON format
     */
    public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        RouteInterface listner;
        public ParserTask(RouteInterface listner) {
            this.listner = listner;
        }

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                //Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());
                JSONObject routes1 = jObject.getJSONArray("routes").getJSONObject(0);
                JSONObject legs = routes1.getJSONArray("legs").getJSONObject(0);
                 distance = legs.getJSONObject("distance");
                 duration = legs.getJSONObject("duration");
                Log.d(TAG,"distance:"+distance.getString("text")+"\n time"+duration.getString("text"));
                
                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            this.listner.onRouteLoad(result,distance,duration);
          /*  ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            if (routeLine != null) {
                routeLine.remove();
            }
            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                endLat = points.get(path.size() - 1).latitude;
                endLNG = points.get(path.size() - 1).longitude;


                startPointMarker = new MarkerOptions();
                startPointMarker.position(points.get(0));
                startPointMarker.flat(true);
                startPointMarker.anchor(0.5f, 0.5f);
                startPointMarker.icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.green_marker));

                endPointMarker = new MarkerOptions();
                endPointMarker.position(points.get(points.size() - 1));
                endPointMarker.flat(true);
                endPointMarker.anchor(0.5f, 0.5f);
                endPointMarker.icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.red_marker));
                mMap.addMarker(startPointMarker);
                mMap.addMarker(endPointMarker);

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.BLACK);

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                routeLine = mMap.addPolyline(lineOptions);
                if (points != null)
                    MapUtil.zoomRoute(mMap, points);
                showRideOptionDialog();
            } else {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.main_rl), "Route not available.", Snackbar.LENGTH_LONG);
                snackbar.show();
                Log.d("onPostExecute", "without Polylines drawn");
                //btnClose.performClick();

            }*/
        }
    }

    public String downloadUrl(String strUrl) {

        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            java.net.URL url = new java.net.URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                iStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            urlConnection.disconnect();
        }
        return data;

    }

}