package de.dhbwmatinf19ai1.cclarityis;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    DataAmpelSteuerung steuerung;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        btn = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        editText = findViewById(R.id.editTextTextPersonName);
        imageView = findViewById(R.id.imageView);

        steuerung = new DataAmpelSteuerung();
        steuerung.delegate = this;

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    String input = editText.getText().toString();
                    if (input.matches("")){
                        textView.setVisibility(View.VISIBLE);
                        textView2.setVisibility(View.GONE);
                        imageView.setVisibility(View.GONE);
                        textView.setText("Bitte Ort eingeben!");
                    }else {
                        textView.setVisibility(View.GONE);
                        textView2.setVisibility(View.VISIBLE);
                        steuerung.initalize(input);
                        steuerung.execute();
                      //  textView2.setText(steuerung.werte.getLankreis());

                    }
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
        System.out.println(output.anzfaelle);
        //Toast.makeText(this, output.anzfaelle,Toast.LENGTH_SHORT).show();
    }


    //TODO Aktuelle Corona Regeln einbinden
    //TODO Automatische Standorterfassung
    //TODO Push-Benachrichtigung wenn Ampel auf Rot springt, wenn man den Landkreis wechselt
}