package cf.khanhsb.icare_v2.Model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;

import cf.khanhsb.icare_v2.Model.NotificationHelper;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

public class AlertReceiver extends BroadcastReceiver {

    private FirebaseFirestore firestore;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannel1Notification("Sleep Reminder","It's time for your to go to sleep");
        notificationHelper.getManager().notify(1,nb.build());

        String timeOfSleep = intent.getStringExtra("time");
        String userEmail = intent.getStringExtra("userEmail");

        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(previousOrSame(MONDAY));

        firestore = FirebaseFirestore.getInstance();
        firestore.collection("daily").
                document("week-of-" + monday.toString()).
                collection(today.toString()).
                document(userEmail).update("sleep_time", timeOfSleep);
    }
}
