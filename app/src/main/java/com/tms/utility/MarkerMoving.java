package com.tms.utility;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class MarkerMoving {
    MyTimerTask timerTask;
    Context mContext;
    String url;
    ServiceResponce mResponce;
    Timer mTimer;
    boolean isFirst;
    String resp;
    final static String  TAG = "Rider request:";


    /**
     * method to convert input stream to String data
     *
     * @param instream
     * @return
     */
    private String convertStreamToString(InputStream instream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                instream));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                instream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();

    }

    public MarkerMoving() {
        // TODO Auto-generated constructor stub

        mTimer = new Timer("alertTimer", true);
    }

    public void callWebService() {

        mTimer.schedule(timerTask, 30000, 30000);
    }

    public void destroy() {
        mTimer.cancel();
        Log.i("Destroy", "Destroy");

    }

    public class Webservice extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response;
            HttpGet httpget = new HttpGet(url);
            try {
                response = httpClient.execute(httpget);

                if (response.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();

                    if (entity != null) {
                        InputStream instream = entity.getContent();
                        // mResponce.onResp(convertStreamToString(instream));
                        resp = convertStreamToString(instream);
                        instream.close();

                    }
                Log.d(TAG,"url:"+url+"\n"+resp);
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub


            if (resp != null) {
                mResponce.onResp(resp);
            }
            super.onPostExecute(result);
        }
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            // Do stuff
            new Webservice().execute();

        }
    }

    public void reScheduleTimer(Context mContext, String url,
                                ServiceResponce mResponce) {

        this.mContext = mContext;
        this.mResponce = mResponce;
        this.url = url;

        mTimer = new Timer("alertTimer", true);
        timerTask = new MyTimerTask();
        mTimer.schedule(timerTask, 30000, 30000);
    }

}