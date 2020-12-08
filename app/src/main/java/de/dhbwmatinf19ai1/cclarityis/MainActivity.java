package de.dhbwmatinf19ai1.cclarityis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.osmdroid.bonuspack.location.GeocoderNominatim;


public class MainActivity extends AppCompatActivity implements CoronaResponseAsync {

    Button btn;
    TextView textView;
    TextView textView2;
    EditText editText;
    ImageView imageView;
    ImageButton standortbtn;
    String LastLatitudeS;
    String LastLongitudeS;
    String input;

    DataAmpelSteuerung steuerung;

    private FusedLocationProviderClient fusedLocationClient;
    private String Ergebnis = "null";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        btn = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        editText = findViewById(R.id.editTextTextPersonName);
        imageView = findViewById(R.id.imageView);
        standortbtn = findViewById(R.id.standortbtn);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    input = editText.getText().toString();
                    if (input.matches("")){
                        textView.setVisibility(View.VISIBLE);
                        textView2.setVisibility(View.GONE);
                        imageView.setVisibility(View.GONE);
                        textView.setText("Bitte Ort eingeben!");
                    }else {
                        steuerung = new DataAmpelSteuerung();
                        steuerung.delegate = MainActivity.this;
                        textView.setVisibility(View.GONE);
                        textView2.setVisibility(View.VISIBLE);
                        steuerung.initalize(input, LastLongitudeS, LastLatitudeS, 0);
                        steuerung.execute();
                    }
            }
        });

        standortbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                steuerung = new DataAmpelSteuerung();
                steuerung.delegate = MainActivity.this;
                getLocation();
                steuerung.initalize(input, LastLongitudeS, LastLatitudeS, 1);
                steuerung.execute();
            }
        });

    }



    public void showAmpel() {
        int inzidenz = steuerung.werte.getInzidenzlandkreis();
        if (inzidenz < 35) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.ampel_gruen);
        }else if (inzidenz >= 50) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.ampel_rot);
        }else if (inzidenz >= 35 && inzidenz < 50) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.ampel_gelb);
        }
    }

    @Override
    public void finished(Coronazahlen output) {
        int inzidenz = output.getInzidenzlandkreis();
        int anzfall = output.getAnzfaelle();
        String landkreis = output.getLankreis();
        String BL = output.getBundesland();
        int inzidenz_bl = output.getInzidenzbundesland();
        int tode = output.getTode();
        String rate = output.getTodesrate();
        String last_update = output.getStand();

        textView2.setText(landkreis + "\n" + "(" + BL + ")"+  "\n\n" + "Anzahl der Fälle: " + anzfall +
                "\n\n" + "Inzidenzwert: " + inzidenz + "\n\n" + "Inzidenzwert (BL): " + inzidenz_bl +
                "\n\n" + "Todesfälle: " + tode + "\n\n" + "Todesrate: " + rate + "\n\n" + "Stand: " + last_update);

        showAmpel();
    }



    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("in getlocation");
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        double LastLatitude;
                        double LastLongitude;
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // createGeofence(location.getLongitude(), location.getLatitude(), 1000); //GeofenceCreateAufruf
                            LastLatitude = location.getLatitude();
                            LastLongitude = location.getLongitude();
                            LastLatitudeS = Double.toString(LastLatitude);
                            LastLongitudeS = Double.toString(LastLongitude);
                            System.out.println(LastLatitude + " " +  LastLongitude);
                        }else{
                            System.out.println("keine location empfangen");
                            LastLatitude = 360;
                            LastLongitude = 360;
                        }
                    }
                });
        //TODO Achtung bug Ergebnis wird sofort zurückgegeben
    }

    //TODO Aktuelle Corona Regeln einbinden
    //TODO Automatische Standorterfassung
    //TODO Push-Benachrichtigung wenn Ampel auf Rot springt, wenn man den Landkreis wechselt
}