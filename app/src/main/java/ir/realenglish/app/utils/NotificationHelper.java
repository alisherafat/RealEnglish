package ir.realenglish.app.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import ir.realenglish.app.R;

/**
 * Created by ALI-PC on 4/27/2016.
 */
public class NotificationHelper {
    private String title = "", message = "";
    private Context context;
    private Intent intent;
    private int notificationId = 0,iconID = 0;


    public NotificationHelper(Context context) {
        this.context = context;
    }

    public NotificationHelper setTitle(String title) {
        this.title = title;
        return this;
    }

    public NotificationHelper setMessage(String message) {
        this.message = message;
        return this;
    }

    public NotificationHelper setIntent(Intent intent) {
        this.intent = intent;
        return this;
    }

    public NotificationHelper setNotificationID(int id) {
        this.notificationId = id;
        return this;
    }
    public NotificationHelper setIconID(int id){
        this.iconID = id;
        return this;
    }

    public void showNotification() {
        checkFields();
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Notification notification = mBuilder.setSmallIcon(iconID).setTicker(title)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message)
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), iconID)).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notification);
    }

    private void checkFields() {
        if (notificationId == 0) {
            notificationId = 15384;
        }
        if (iconID == 0){
            iconID = R.mipmap.ic_launcher;
        }
        if (intent == null) {
            intent = new Intent();
        }
    }

}
