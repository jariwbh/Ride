package com.tms.FCMNotification;


import android.content.Context;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.tms.ServerUtilities;
import com.tms.utility.ToroSharedPrefrnce;

public class FireBaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        ToroSharedPrefrnce prefrnce = new ToroSharedPrefrnce(this);
        prefrnce.setGcm(FirebaseInstanceId.getInstance().getToken());
    }


}
