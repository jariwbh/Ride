package com.tms;
//
//
//import android.app.AlertDialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.location.LocationManager;
//import android.provider.Settings;
//import android.support.v4.app.FragmentActivity;
//
//public class GpsChangeReceiver  extends BroadcastReceiver
//{   
//	  @Override
//	  public void onReceive( final Context context, Intent intent )
//	  {
//	      final LocationManager manager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );
//	          if (manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
//	         //do something
//	            }
//	         else
//	         {
//	            //do something else
//	        	 AlertDialog.Builder builder = new AlertDialog.Builder(context);
//	 			builder.setTitle("Location Manager");
//	 			builder.setMessage("Would you like to enable GPS?");
//	 			builder.setPositiveButton("Yes",
//	 					new DialogInterface.OnClickListener() {
//	 						@Override
//	 						public void onClick(DialogInterface dialog, int which) {
//	 							// Launch settings, allowing user to make a change
//	 							((FragmentActivity) context).startActivityForResult(new Intent(
//	 									Settings.ACTION_LOCATION_SOURCE_SETTINGS),
//	 									786);
//	 						}
//	 					});
//	 			// builder.setNegativeButton("No", new
//	 			// DialogInterface.OnClickListener() {
//	 			// @Override
//	 			// public void onClick(DialogInterface dialog, int which) {
//	 			// //No location service, no Activity
//	 			// finish();
//	 			// }
//	 			// });
//	 			builder.create().show();
//	         }
//	  }
//
//}
