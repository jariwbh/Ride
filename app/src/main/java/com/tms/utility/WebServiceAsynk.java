package com.tms.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * WebServiceAsynk is class to get data from web service
 * 
 * @author rishab.bhardwaj
 * 
 */
public class WebServiceAsynk extends AsyncTask<Void, Void, Void> {
	final static String  TAG = "Rider request:";

	/*
	 * URL of WEb-service
	 */
	String url;
	/*
	 * response of Web-service
	 */
	String strresponse = "";
	/*
	 * Listener to catch and parse response 
	 */
	ResponseListener mListener;
	/*
	 * Context of Activity
	 */
	Context mContext;
	/*
	 * Input Stream
	 */
	static InputStream is = null;
	/*
	 * JSOn object
	 */
	static JSONObject jObj = null;
	/*
	 * ProgressDialog for showing progress
	 */
	ProgressDialog mDialog;
	boolean isDialog;
	String webserviceName;

	/**
	 * constructor
	 *
	 * @param url
	 *            URL of web service
	 * @param mListener
	 *            Listener to catch response
	 * @param mContext
	 *            Context of class
	 * @param isDialog
	 *            boolean to show dialog
	 *
	 */
	public WebServiceAsynk(String url, ResponseListener mListener,
			Context mContext, boolean isDialog,String webserviceName) {
		// TODO Auto-generated constructor stub
		this.mListener = mListener;

		this.url = url;
		this.mContext = mContext;
		this.isDialog=isDialog;
		this.webserviceName=webserviceName;
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		
		//Parse response to listner
		if (!strresponse.equalsIgnoreCase("")) {
			mListener.response(strresponse,webserviceName);
			if (isDialog == true) {
				mDialog.dismiss();

			}

		} else {
			Log.e(TAG, ":::error");
			mListener.response(strresponse,webserviceName);
			if (isDialog == true) {
				mDialog.dismiss();
			}

		}
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		if (isDialog == true) {
			mDialog =  new ProgressDialog(mContext, ProgressDialog.THEME_HOLO_LIGHT);
			mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mDialog.setMessage("Loading...");
			mDialog.show();

		}
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		
	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		// defaultHttpClient



        
		// mDialog.show();
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response;
		HttpGet httpget = new HttpGet(url);

		try {
			response = httpClient.execute(httpget);

			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();

				if (entity != null) {
					InputStream instream = entity.getContent();
					strresponse = convertStreamToString(instream);

					instream.close();

				}

			}
			Log.d(TAG,url+"\n"+strresponse);
		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}

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

}
