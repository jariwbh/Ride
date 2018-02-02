/*
package com.tms;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.tms.activity.ReservationDetail;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
	public static int count, i;
	static String driverRatingStatus;
	static String message, ExtraMessage, via, child_id, phoneNo, userName,childLocation;
	static Dialog mDialog;

	static double lng, lat;
	public static MediaPlayer player;

	public GCMIntentService() {
		super(GCMCommonUtilities.SENDER_ID);
	}

	*/
/**
	 * Method called on device registered
	 **//*

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);
		// displayMessage(context, "Your device registred with GCM");
		// Log.d("NAME", MainActivity.name);
//		ServerUtilities.register(context, "", "", registrationId);
	}

	*/
/**
	 * Method called on device un registred
	 * *//*

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
//		GCMCommonUtilities.displayMessage(context, getString(R.string.gcm_unregistered));
//		ServerUtilities.unregister(context, registrationId);
	}

	*/
/**
	 * Method called on Receiving a new message
	 * *//*

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Received message");


		if(!intent.hasExtra("reservationId")){

			return;
		}
		 player=MediaPlayer.create(GCMIntentService.this, R.raw.reservation);


			player.start();





		Bundle bundle=intent.getExtras();
		message = intent.getExtras().getString("message");
		String id = intent.getExtras().getString("reservationId");

		Intent i=new Intent(context, ReservationDetail.class);
		i.putExtra("RESERVATION_ID", id);
		i.putExtra("via", "PUSH");
		 i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);

	//	startActivity(new Intent(context, ReservationDetail.class));




//		ExtraMessage = intent.getExtras().getString("extraMessage");
//		if (message != null && ExtraMessage != null) {
//			via = "first";
//			displayMessage(context, message);
//			try {
//				JSONObject mJsonObject = new JSONObject(ExtraMessage);
//
//				lat = Double.parseDouble(mJsonObject.getString("childLat"));
//				lng = Double.parseDouble(mJsonObject.getString("chlidLong"));
//				child_id = mJsonObject.getString("child_id");
//				phoneNo = mJsonObject.getString("phoneNo");
//				userName = mJsonObject.getString("childName");
//				childLocation=mJsonObject.getString("childLocation");
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			generateNotification(GCMIntentService.this, message);
//
//		} else {
//			message = intent.getExtras().getString("message1");
//			if (message != null) {
//				via = "second";
//				displayMessage(context, message);
//
//				generateNotification(GCMIntentService.this, message);
//			} else {
//				message = intent.getExtras().getString("childPush");
//				if (message != null) {
//					via = "child";
//					displayMessage(context, message);
//
//					generateNotification(GCMIntentService.this, message);
//				}
//			}
//
//		}

	}

	*/
/**
	 * Method called on receiving a deleted message
	 * *//*

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);

		GCMCommonUtilities.displayMessage(context, message);
		// notifies user
		generateNotification(context, message);
	}

	*/
/**
	 * Method called on Error
	 * *//*

	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
		GCMCommonUtilities.displayMessage(context, getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message

		GCMCommonUtilities.displayMessage(context,
                getString(R.string.gcm_recoverable_error, errorId));
		return super.onRecoverableError(context, errorId);
	}

	*/
/**
	 * Issues a notification to inform the user that server has sent a message.
	 *//*


	private static void generateNotification(final Context context,
			final String message) {
		// if (via.equalsIgnoreCase("first")) {
		// Intent notificationIntent = new Intent(context,
		// DialogGcmActivity.class);
		// notificationIntent.putExtra("message", message);
		// notificationIntent.putExtra("via", via);
		// notificationIntent.putExtra("childId", child_id);
		// notificationIntent.putExtra("phoneNo", phoneNo);
		// notificationIntent.putExtra("userName", userName);
		// notificationIntent.putExtra("lat", lat);
		// notificationIntent.putExtra("lng", lng);
		// notificationIntent.putExtra("childLocation",childLocation);
		// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// context.startActivity(notificationIntent);
		//
		// } else if (via.equalsIgnoreCase("second")) {
		// Intent notificationIntent = new Intent(context,
		// DialogGcmActivity.class);
		// notificationIntent.putExtra("message", message);
		// notificationIntent.putExtra("via", via);
		// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// context.startActivity(notificationIntent);
		// } else {
		// Intent notificationIntent = new Intent(context,
		// DialogGcmActivity.class);
		// notificationIntent.putExtra("message", message);
		// notificationIntent.putExtra("via", via);
		// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// context.startActivity(notificationIntent);
		// }

	}
	


}
*/
