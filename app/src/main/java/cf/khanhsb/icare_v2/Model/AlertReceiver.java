package cf.khanhsb.icare_v2.Model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import cf.khanhsb.icare_v2.Model.NotificationHelper;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannel1Notification("Alarm Alert!","Time to wake up");
        notificationHelper.getManager().notify(1,nb.build());
    }
}
