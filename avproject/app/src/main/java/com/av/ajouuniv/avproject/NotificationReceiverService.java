package com.av.ajouuniv.avproject;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.os.Build;
import android.os.UserHandle;
import android.service.notification.NotificationListenerService;
import android.support.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
@SuppressLint("OverrideAbstract")
public class NotificationReceiverService extends NotificationListenerService {
    public NotificationReceiverService() {
    }

    @Override
    public void onNotificationChannelModified(String pkg, UserHandle user, NotificationChannel channel, int modificationType) {
        super.onNotificationChannelModified(pkg, user, channel, modificationType);
    }
}
