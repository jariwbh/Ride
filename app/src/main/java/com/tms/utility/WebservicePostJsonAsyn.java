package com.tms.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;


public class WebservicePostJsonAsyn extends AsyncTask<Void, Void, Void> {

	Context mContext;
	/*
	 * Listener to catch and parse response
	 */
	com.tms.utility.ResponseListenerNew mListener;
	/*
	 * URl of Webservice
	 */
	ProgressDialog mProgressDialog;
	String url;
	JSONObject mJsonObject;
	String response;
	boolean isProgressDialog;
	String via;

	public WebservicePostJsonAsyn(Context mContext,
			com.tms.utility.ResponseListenerNew mListenerRoot, String url,
			JSONObject mJsonObject, boolean isProgressDialog, String via) {
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
		this.mJsonObject = mJsonObject;
		this.mListener = mListenerRoot;
		this.url = url;
		this.isProgressDialog = isProgressDialog;
		this.via = via;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		if (isProgressDialog == true) {
			mProgressDialog =  new ProgressDialog(mContext, ProgressDialog.THEME_HOLO_LIGHT);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setMessage("Loading...");
			mProgressDialog.show();

		}
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (response != null) {
			mListener.response(response, via);

		} else {
			if (CheckInternetConnectivity.checkinternetconnection(mContext) == false) {
				Toast.makeText(mContext, "No Internet Connection",
						Toast.LENGTH_SHORT).show();
			}
		}
		if (mProgressDialog!=null && isProgressDialog == true && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}

	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		if (CheckInternetConnectivity.checkinternetconnection(mContext)) {
			response = SendHttpPost(url, mJsonObject);
		} else {
			response = null;
		}

		return null;
	}

	private static final String TAG = "HttpClient";

	public static String SendHttpPost(String URL, JSONObject jsonObjSend) {
		String resultString = "";
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPostRequest = new HttpPost(URL);

			StringEntity se;
			se = new StringEntity(jsonObjSend.toString());

			// Set HTTP parameters
			httpPostRequest.setEntity(se);
			httpPostRequest.setHeader("Accept", "application/json");
			httpPostRequest.setHeader("Content-type", "application/json");
			httpPostRequest.setHeader("Accept-Encoding", "gzip"); // only set
																	// this
																	// parameter
																	// if you
																	// would
																	// like to
																	// use gzip
																	// compression

			long t = System.currentTimeMillis();
			HttpResponse response = (HttpResponse) httpclient
					.execute(httpPostRequest);
			Log.i(TAG,
					"HTTPResponse received in ["
							+ (System.currentTimeMillis() - t) + "ms]");

			// Get hold of the response entity (-> the data):
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				// Read the content stream
				InputStream instream = entity.getContent();
				Header contentEncoding = response
						.getFirstHeader("Content-Encoding");
				if (contentEncoding != null
						&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {
					instream = new GZIPInputStream(instream);
				}

				// convert content stream to a String
				resultString = convertStreamToString(instream);
				instream.close();

				return resultString;
			}

		} catch (Exception e) {
			// More about HTTP exception handling in another tutorial.
			// For now we just print the stack trace.
			e.printStackTrace();
		}
		return resultString;
	}

	public static String SendHttpPoststring(String URL) {
		String resultString = "";
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPostRequest = new HttpPost(URL);

			// Set HTTP parameters

			httpPostRequest.setHeader("Accept", "application/json");
			httpPostRequest.setHeader("Content-Type", "application/json");
			httpPostRequest.setHeader("Accept-Encoding", "gzip"); // only set
																	// this
																	// parameter
																	// if you
																	// would
																	// like to
																	// use gzip
																	// compression

			long t = System.currentTimeMillis();
			HttpResponse response = (HttpResponse) httpclient
					.execute(httpPostRequest);
			Log.i(TAG,
					"HTTPResponse received in ["
							+ (System.currentTimeMillis() - t) + "ms]");

			// Get hold of the response entity (-> the data):
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				// Read the content stream
				InputStream instream = entity.getContent();
				Header contentEncoding = response
						.getFirstHeader("Content-Encoding");
				if (contentEncoding != null
						&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {
					instream = new GZIPInputStream(instream);
				}

				// convert content stream to a String
				resultString = convertStreamToString(instream);
				instream.close();

				return resultString;
			}

		} catch (Exception e) {
			// More about HTTP exception handling in another tutorial.
			// For now we just print the stack trace.
			e.printStackTrace();
		}
		return resultString;
	}

	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 * 
		 * (c) public domain:
		 * http://senior.ceng.metu.edu.tr/2009/praeda/2009/01/
		 * 11/a-simple-restful-client-at-android/
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
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
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
