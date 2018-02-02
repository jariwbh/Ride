package com.tms.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * SendXmlAsync is Asynctask class to send xml data to web service
 *
 * @author rishab.bhardwaj
 *
 */
public class SendXmlAsync extends AsyncTask<Void, Void, Void> {

    String strUrl;
    String strEntity;
    com.tms.utility.XmlListener mListener;
    String response = "";
    ProgressDialog mDialog;
    Context mContext;
    boolean isDialog;
    final static String  TAG = "Rider request:";
    /**
     * constructor of class
     *
     * @param strUrl
     *            url of web service
     * @param strEntity
     *            xml string to send data
     * @param mListener
     *            listner to catch response
     *
     * @param mContext
     *            context of class
     */
    public SendXmlAsync(String strUrl, String strEntity, com.tms.utility.XmlListener mListener,
                        Context mContext, boolean isDialog) {
        // TODO Auto-generated constructor stub
        this.strEntity = strEntity;
        this.mListener = mListener;
        this.strUrl = strUrl;
        this.mContext = mContext;
        this.isDialog = isDialog;

    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub

        super.onPreExecute();
        if (isDialog == true) {
            mDialog =  new ProgressDialog(mContext, ProgressDialog.THEME_HOLO_LIGHT);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.setMessage("Loading...");
            mDialog.setCancelable(false);
            mDialog.show();

        }

    }

    @Override
    protected void onPostExecute(Void result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        try {
            if (!response.equalsIgnoreCase("") && response != null) {
                mListener.onResponse(response);
                if (isDialog == true) {
                    mDialog.dismiss();
                }

            } else {
                Log.e(TAG, "Error");
                mListener.onResponse(response);
                if (isDialog == true) {
                    mDialog.dismiss();
                }
            }
        }catch(Exception e){
            if (isDialog == true) {
                mDialog.dismiss();
            }

        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        // TODO Auto-generated method stub
        try {

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(strUrl);
            StringEntity stringEntity = new StringEntity(strEntity,"UTF-8");

            stringEntity.setContentType("application/atom+xml");
            httpPost.setEntity(stringEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();

            String Response = EntityUtils.toString(httpEntity);

            response = Response;
            Log.d(TAG,"url "+strUrl+",\n  xml: "+strEntity +"\n response"+response);
			/*
			 * mListener.onResponse(Response); mDialog.dismiss();
			 */
        } catch (Exception e) {
            e.printStackTrace();
            if (isDialog == true) {
                mDialog.dismiss();
            }

        }

        return null;
    }

}
