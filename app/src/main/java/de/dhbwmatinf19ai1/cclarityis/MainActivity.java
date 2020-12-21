package de.dhbwmatinf19ai1.cclarityis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

/**
 * @author Yannick Schroth, Tobias Schweikart, Leon Nehring
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        FactFragment factFragment = new FactFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.fragment1, factFragment, factFragment.getTag())
                .commit();

        //Überprüfung, ob Standortabfrage erlaubt wurde
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("Erlaubnis", "Standorerlaubnis gewährt");
        }else{
            Log.d("Erlaubnis", "Standorterlaubnis nicht gewährt");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }




    //TODO Aktuelle Corona Regeln einbinden
    //TODO Automatische Standorterfassung
    //TODO Push-Benachrichtigung wenn Ampel auf Rot springt, wenn man den Landkreis wechselt
}