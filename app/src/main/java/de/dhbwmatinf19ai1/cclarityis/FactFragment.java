package de.dhbwmatinf19ai1.cclarityis;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;



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
                if (input.matches("")){
                    textView.setVisibility(View.VISIBLE);
                    textView2.setVisibility(View.GONE);
                    imageView.setVisibility(View.GONE);
                    textView.setText("Bitte Ort eingeben!");
                }else {
                    steuerung = new DataAmpelSteuerung();
                    steuerung.delegate = FactFragment.this;
                    textView.setVisibility(View.GONE);
                    steuerung.initalize(input, LastLongitudeS, LastLatitudeS, 0);
                    steuerung.execute();
                }
            }
        });

        standortbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setVisibility(View.GONE);
                location = new AutoLocation(getContext());
                location.delegater = FactFragment.this;
                location.initialize();
            }
        });
        return root;
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
        if (landkreis == null) {
            textView2.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            textView.setText("Es konnten keine Daten ermittelt werden");
            textView.setVisibility(View.VISIBLE);
        } else {
            textView2.setVisibility(View.VISIBLE);
            textView2.setText(landkreis + "\n" + "(" + BL + ")" + "\n\n" + "Anzahl der Fälle: " + anzfall +
                    "\n\n" + "Inzidenzwert: " + inzidenz + "\n\n" + "Inzidenzwert (BL): " + inzidenz_bl +
                    "\n\n" + "Todesfälle: " + tode + "\n\n" + "Todesrate: " + rate + "\n\n" + "Stand: " + last_update);

            showAmpel();
        }
    }

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