package de.dhbwmatinf19ai1.cclarityis;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


public class AutoLocation {

    private FusedLocationProviderClient fusedLocationClient;
    private String Ergebnis = "null";
    public LocationAsync delegater = null;



    private Context context;

    public AutoLocation(Context context) {
        this.context = context;
    }

    public void initialize() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        getLocation();
    }
    public void getLocation() {
        LocationData data = new LocationData();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        double LastLatitude;
                        double LastLongitude;
                        String LastLatitudeS;
                        String LastLongitudeS;
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            LastLatitude = location.getLatitude();
                            LastLongitude = location.getLongitude();
                            LastLatitudeS = Double.toString(LastLatitude);
                            LastLongitudeS = Double.toString(LastLongitude);
                            data.setLatitude(LastLatitudeS);
                            data.setLongitude(LastLongitudeS);
                            delegater.finishedLocation(data);
                            Log.d("autolocation", "Koordinaten: " + LastLatitude + " " +  LastLongitude);
                        }else{
                            Log.d("autolocation", "Koordinaten konnten nicht ermittelt werden");
                            LastLatitude = 360;
                            LastLongitude = 360;
                        }
                    }
                });
    }
}
