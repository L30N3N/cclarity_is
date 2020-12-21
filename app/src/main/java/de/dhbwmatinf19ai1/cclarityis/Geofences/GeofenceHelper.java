package de.dhbwmatinf19ai1.cclarityis.Geofences;

import android.Manifest;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;


import static com.google.android.gms.common.util.CollectionUtils.listOf;

public class GeofenceHelper{
    private Context context;
    private PendingIntent geofencePendingIntent;
    private static int counter =1;

    public GeofenceHelper(Context context){
        this.context = context;
    }

    private GeofencingRequest buildingGeofencingRequest(List<Geofence> geofenceList) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        //  registerReceiver(broadcastReceiver,new IntentFilter("GEOFENCE"));
        Intent intent = new Intent(context, MyBroadCastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    private Geofence buildGeofence(double Longitude, double Latitude, float radius) {
        //entspricht populateGeofenceList

        //Geofence
        Geofence builder = new Geofence.Builder()
                .setRequestId("GeoCorona00"+counter++)
                .setCircularRegion(
                        Latitude,
                        Longitude,
                        radius)
                //Reagiere wenn Geofence verlassen wird
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)//TODO Informiert nur das 1. mal
                //Geofence wird erstmal noch nicht zu einer bestimmten Zeit entfernt
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setNotificationResponsiveness(50)
                .build();
        return builder;
    }

    public void addGeofence(Context context, Double userLat, Double userLong, GeofencingClient geofencingClient){
        Geofence geofence = buildGeofence(userLong,userLat,1000);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        List<Geofence> gefenceList = new ArrayList<Geofence>();
        gefenceList.add(geofence);
        geofencingClient.addGeofences(buildingGeofencingRequest(gefenceList), getGeofencePendingIntent())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("GeofenceHelper","Geofence Added");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("GeofenceHelper","Geofence added Failed");
                }
        });
    }


    public void removeGeofence(GeofencingClient geofencingClient, Context context) {
        geofencingClient.removeGeofences(getGeofencePendingIntent()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("GeofenceHelper","Geofence remove");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("GeofenceHelper","Geofence remove Failed");
            }
        });
    }
}

