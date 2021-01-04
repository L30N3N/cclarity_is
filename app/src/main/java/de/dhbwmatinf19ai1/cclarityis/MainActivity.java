package de.dhbwmatinf19ai1.cclarityis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import de.dhbwmatinf19ai1.cclarityis.Geofences.GeofenceHelper;
import de.dhbwmatinf19ai1.cclarityis.Geofences.GeofenceTransitionsJobIntentService;
import de.dhbwmatinf19ai1.cclarityis.Rules.RulesFragment;

/**
 * @author Yannick Schroth, Tobias Schweikart, Leon Nehring
 */

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        FactFragment factFragment = new FactFragment();
        RulesFragment rulesFragment = new RulesFragment();
        factFragment.setRulesFragment(rulesFragment);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.fragment1, factFragment, factFragment.getTag())
                .replace(R.id.fragment2, rulesFragment, rulesFragment.getTag())
                .commit();



               //Überprüfung, ob Standortabfrage erlaubt wurde
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("Erlaubnis", "Standorerlaubnis gewährt");
        }else{
            Log.d("Erlaubnis", "Standorterlaubnis nicht gewährt");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1);
        }

        Intent receivedIntent = this.getIntent();
        if (receivedIntent != null && receivedIntent.hasExtra(Intent.EXTRA_TEXT)) {
            String receivedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
            // Die empfangenen Daten können nun verwendet werden ...
            factFragment.setLocationGeofence();
        }else{
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            setupGeofence();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Info");
                builder.setIcon(R.drawable.ic_baseline_info_24);
                builder.setMessage("Verwendete Quellen: \n - RKI API (https://services7.arcgis.com/) \n - Openstreetmap  \n - Tourismus-Wegweiser (https://tourismus-wegweiser.de/json/) \n\n Alle Angaben ohne Gewähr \n\n\n Entwickler: \n Tobias Schweikart \n Leon Nehring \n Yannick Schroth");
                builder.show();
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void setupGeofence(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d("Geo","No Permission");
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_BACKGROUND_LOCATION},1);
            return;
        }
        Context context = this;
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
//                            maketoast(location.toString());
                            GeofenceHelper Helper = new GeofenceHelper(context);
                            GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);
                            Helper.addGeofence(context,location.getLatitude(),location.getLongitude(),geofencingClient);
                        }else {
                            Log.d("getLastLocation", "Keine Location empfangen");
                        }
                    }
                });
    }

    public void maketoast(String content){
        Toast.makeText(this,content, Toast.LENGTH_SHORT).show();
    }
}