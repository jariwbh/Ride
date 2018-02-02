package com.tms.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

public class CheckInternetConnectivity {

	boolean boolStatus;

	public static boolean checkinternetconnection(Context context) {

		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager.getActiveNetworkInfo() != null

				&& connectivityManager.getActiveNetworkInfo().isAvailable()

				&& connectivityManager.getActiveNetworkInfo().isConnected()) {

			return true;
		} else {
			Log.v("INTERNETWORKING", "Internet not present");
			return false;
		}

	}
}
	

