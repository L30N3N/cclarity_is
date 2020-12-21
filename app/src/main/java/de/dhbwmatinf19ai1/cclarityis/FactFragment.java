package de.dhbwmatinf19ai1.cclarityis;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * @author Yannick Schroth
 */

public class FactFragment extends Fragment implements CoronaResponseAsync, LocationAsync {

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
    AutoLocation location;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_fact, container, false);
        btn = root.findViewById(R.id.button);
        textView = root.findViewById(R.id.textView);
        textView2 = root.findViewById(R.id.textView2);
        editText = root.findViewById(R.id.editTextTextPersonName);
        imageView = root.findViewById(R.id.imageView);
        standortbtn = root.findViewById(R.id.standortbtn);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                input = editText.getText().toString();
                if (input.matches("")){ //Wird aufgerufen, wenn nichts eingegeben wurde
                    textView.setVisibility(View.VISIBLE);
                    textView2.setVisibility(View.GONE);
                    imageView.setVisibility(View.GONE);
                    textView.setText("Bitte Ort eingeben!");
                    Log.d("Start", "Nichts in Textfeld eingegeben");
                }else { //Knopfdruch bei Eingabe führt folgende Methoden aus
                    steuerung = new DataAmpelSteuerung();
                    steuerung.delegate = FactFragment.this;
                    textView.setVisibility(View.GONE);
                    steuerung.initalize(input, LastLongitudeS, LastLatitudeS, 0);
                    steuerung.execute();
                    Log.d("Start", "Start über Textfeldeingabe");
                }
            }
        });

        standortbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //Onclick für die automatische Standortermittlung
                if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){ //Überprüfung der Standorterlaubnis
                    textView.setVisibility(View.VISIBLE);
                    textView2.setVisibility(View.GONE);
                    imageView.setVisibility(View.GONE);
                    textView.setText("Automatische Standortermittlung nicht möglich, bestätigen Sie zuvor die entsprechende Erlaubnis");
                    Log.d("Start", "Methoden wurde aufgrund fehlender Berechtigungen nicht ausgeführt");
                }else {
                    textView.setVisibility(View.GONE);
                    location = new AutoLocation(getContext());
                    location.delegater = FactFragment.this;
                    location.initialize();
                    Log.d("Start", "Start über automatische Standortermittlung");
                }
            }
        });
        return root;
    }

    /**
     * Ampel wird entsprechend des Inzidenzwertes vom Landkreis angezeigt
     */
    public void showAmpel() {
        int inzidenz = steuerung.werte.getInzidenzlandkreis();
        if (inzidenz < 35) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.ampel_gruen);
            Log.d("Ampel", "Grüne Ampel");
        }else if (inzidenz >= 50) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.ampel_rot);
            Log.d("Ampel", "Rote Ampel");
        }else if (inzidenz >= 35 && inzidenz < 50) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.ampel_gelb);
            Log.d("Ampel", "Gelbe Ampel");
        }
    }

    /**
     * Implementierte Methode vom Interface CoronaResponseAsync
     * @param output --> Liefert das entsprechende Objekt aus der Klasse DataAmpelSteuerung
     */
    @Override
    public void finished(Coronazahlen output) {
        //Variablen mit entsprechenden Werten füllen
        int inzidenz = output.getInzidenzlandkreis();
        int anzfall = output.getAnzfaelle();
        String landkreis = output.getLankreis();
        String BL = output.getBundesland();
        int inzidenz_bl = output.getInzidenzbundesland();
        int tode = output.getTode();
        String rate = output.getTodesrate();
        String last_update = output.getStand();

        if (landkreis == null) { //Wenn kein Landkreis ermittelt werden konnte
            textView2.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            textView.setText("Es konnten keine Daten ermittelt werden");
            textView.setVisibility(View.VISIBLE);
            Log.d("Ausgabe", "Keine Daten gefunden");
        } else { //Wenn ein Landkreis ermittelt wurde
            textView2.setVisibility(View.VISIBLE);
            textView2.setText(landkreis + "\n" + "(" + BL + ")" + "\n\n" + "Anzahl der Fälle: " + anzfall +
                    "\n\n" + "Inzidenzwert: " + inzidenz + "\n\n" + "Inzidenzwert (BL): " + inzidenz_bl +
                    "\n\n" + "Todesfälle: " + tode + "\n\n" + "Todesrate: " + rate + "\n\n" + "Stand: " + last_update); //Ausgabe der Werte
            Log.d("Ausgabe", "Daten ausgegeben");
            showAmpel(); //Entsprechende Ampel wird angezeigt
        }
    }


    /**
     * Implementierte Methode vom Interface LocationAsync
     * @param output --> Liefert benötigte Koordinaten (Longitude und Latitude)
     */
    @Override
    public void finishedLocation(LocationData output) {
        steuerung = new DataAmpelSteuerung();
        steuerung.delegate = FactFragment.this;
        LastLatitudeS =  output.getLatitude();
        LastLongitudeS = output.getLongitude();
        steuerung.initalize(input, LastLongitudeS, LastLatitudeS, 1);
        steuerung.execute();
    }
}