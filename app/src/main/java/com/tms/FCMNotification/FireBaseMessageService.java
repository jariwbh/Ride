package com.tms.FCMNotification;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tms.R;
import com.tms.activity.ReservationDetail;


public class FireBaseMessageService extends FirebaseMessagingService {

    public static MediaPlayer player;
    public static int count, i;
    static String driverRatingStatus;
    static String message, ExtraMessage, via, child_id, phoneNo, userName,childLocation;
    static Dialog mDialog;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getData()!=null) {
            message = remoteMessage.getData().get("message");
            String id = remoteMessage.getData().get("reservationId");

            if(id!=null && !id.equals("null") && !id.equals("")) {

                player = MediaPlayer.create(FireBaseMessageService.this, R.raw.reservation);


                player.start();


                Intent i = new Intent(FireBaseMessageService.this, ReservationDetail.class);
                i.putExtra("RESERVATION_ID", id);
                i.putExtra("via", "PUSH");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                FireBaseMessageService.this.startActivity(i);
            }
        }
    }
}


