package com.tms.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tms.utility.CheckInternetConnectivity;

public class InternetConnectivityBroadcast extends BroadcastReceiver{
int count=0;
	@Override
	public void onReceive(Context context, Intent arg1) {
		
		
		
		// TODO Auto-generated method stub
		
		
//		AlertDialog.Builder builder=new AlertDialog.Builder(arg0);
		
		
		
//		  boolean noConnectivity = arg1.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
		boolean noConnectivity = CheckInternetConnectivity.checkinternetconnection(context);  
		  
		/*  if (!noConnectivity) {
//	       arg0.startActivity(new Intent(arg0,Splash.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)); 	
//	       builder.setMessage("No Internet");
//	       builder.show();
	        	
	        //	Toast.makeText(arg0, "No Internet connectivity.", Toast.LENGTH_LONG).show();
	       count++;
	       Log.e("LOGS", "******************"+count);
			  Intent i = new Intent();
	        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        context.startActivity(i);
	        }else{
	        	
	        	
	        }
		*/
		
		
	}

}
