package de.dhbwmatinf19ai1.cclarityis.Geofences;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.GeofencingEvent;

import de.dhbwmatinf19ai1.cclarityis.R;

import static de.dhbwmatinf19ai1.cclarityis.Geofences.Constants.CHANNEL_ID;

/**
 * @author Tobias Schweikart
 */
public class MyBroadCastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeoFenceBroadcast";

    private String audioFile;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Location change Recieved");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        String test = geofencingEvent.getTriggeringGeofences().toString();
        GeofenceTransitionsJobIntentService.enqueueWork(context, intent);
        Log.d(TAG, "Aktivierter Geofence"+test);

        //sendNotification("Test", context);
    }




    private void sendNotification(String locId, Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Location Reached")
                .setContentText(" you reached " + locId)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }


}
