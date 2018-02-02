package com.tms.broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.support.v4.app.NotificationCompat;

import com.tms.R;

import java.util.Calendar;
import java.util.Random;

public class TimeAlarm  extends BroadcastReceiver{
	
	
	Calendar cal=Calendar.getInstance();
			

	@Override
	public void onReceive(Context context, Intent intent) {
		NotificationManager nm=null;
		
//		 nm = (NotificationManager) context
//				    .getSystemService(Context.NOTIFICATION_SERVICE);
				
				
				  PendingIntent contentIntent = PendingIntent.getActivity(context,(int) intent.getExtras().getInt("ID"),
						  intent, 0);

//				  Notification notif = new Notification(R.drawable.app_icon,
//						"ToroRide Reservation",/*intent.getExtras().getLong("LONG")*/System.currentTimeMillis());
//
//				  notif.defaults |= Notification.DEFAULT_SOUND;
//					notif.flags |= Notification.FLAG_AUTO_CANCEL;
////				  notif.setLatestEventInfo(context, "ToroRide Reservation",intent.getExtras().getString("NOTIFICATION"), contentIntent);
//			notif.when=intent.getExtras().getLong("LONG");
//
//				  nm.notify(intent.getExtras().getInt("ID"), notif);

			NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
				.setTicker("ToroRide Reservation")
				.setSmallIcon(R.drawable.app_icon)
				.setContentTitle("ToroRide Reservation")
				.setContentText(intent.getExtras().getString("NOTIFICATION"))
				.setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
				.setWhen(System.currentTimeMillis())
				.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon))
				.setContentIntent(contentIntent);


			NotificationManager notificationManager =
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

			notificationManager.notify(intent.getExtras().getInt("ID"), notificationBuilder.build());

				  
				  MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.reservation);
				  mediaPlayer.start(); // no need to call prepare(); create() does that for you
				  
				  
				  
			//	context.startActivity(new Intent(context, ReservationDetail.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("RESERVATION_ID", String.valueOf(intent.getExtras().getInt("ID"))));
		}		 
	

}
