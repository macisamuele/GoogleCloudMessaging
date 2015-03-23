package it.macisamuele.googlecloudmessaging;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.android.google.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

    public static final int NOTIFICATION_ID = 1;

    public GCMIntentService() {
        super(GCMIntentService.class.getName());
    }

    @Override
    protected void onSendError() {
        sendNotification("Send error: ");
    }

    @Override
    protected void onMessageDeleted(int total) {
        sendNotification("Deleted messages on server: " + total);
    }

    @Override
    protected void onMessageReceived(Intent intent) {
        sendNotification("Received: " + intent.getExtras().toString());
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("GCM Notification")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}