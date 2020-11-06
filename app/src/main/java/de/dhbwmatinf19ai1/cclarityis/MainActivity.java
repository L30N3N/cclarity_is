package de.dhbwmatinf19ai1.cclarityis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Button btn;
    String url;
    TextView textView;
    TextView textView2;
    EditText editText;
    ImageView imageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        url = "https://services7.arcgis.com/mOBPykOjAyBO2ZKk/arcgis/rest/services/RKI_Landkreisdaten/FeatureServer/0/query?where=1%3D1&outFields=GEN,death_rate,cases_per_100k,BL,county,last_update,cases7_per_100k,cases7_bl_per_100k,cases,deaths&returnGeometry=false&outSR=4326&f=json";
        btn = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        editText = findViewById(R.id.editTextTextPersonName);
        imageView = findViewById(R.id.imageView);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getJsonFromWeb(url);
            }
        });
    }

    public void getJsonFromWeb(String url) {

        String landkreis_ausw = editText.getText().toString();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            calculateCases(myResponse, landkreis_ausw);
                        }
                    });
                }
            }
        });
    }

    public void calculateCases(String json, String landkreis_ausw) {

        int anzfall;
        String landkreis;
        int inzidenz = 0;
        String last_update;
        double todesrate;
        int tode;
        int inzidenz_bl;
        String BL;


        try {
            JSONObject reader = new JSONObject(json);
            JSONArray features = reader.getJSONArray("features");

            for (int i = 0; i < features.length(); i++) {
                JSONObject c = features.getJSONObject(i);
                String test = c.getString("attributes");
                JSONObject reader1 = new JSONObject(test);
                landkreis = reader1.getString("GEN");
                if (landkreis.equals(landkreis_ausw)) {
                    BL = reader1.getString("BL");
                    anzfall = reader1.getInt("cases");
                    inzidenz = reader1.getInt("cases7_per_100k");
                    last_update = reader1.getString("last_update");
                    inzidenz_bl = reader1.getInt("cases7_bl_per_100k");
                    DecimalFormat f = new DecimalFormat("#0.00");
                    todesrate = reader1.getDouble("death_rate");
                    String rate = f.format(todesrate);
                    tode = reader1.getInt("deaths");
                    textView2.setText(landkreis + "\n" + "(" + BL + ")"+  "\n\n" + "Anzahl der Fälle: " + anzfall +
                                      "\n\n" + "Inzidenzwert: " + inzidenz + "\n\n" + "Inzidenzwert (BL): " + inzidenz_bl +
                                      "\n\n" + "Todesfälle: " + tode + "\n\n" + "Todesrate: " + rate + "\n\n" + "Stand: " + last_update);
                    if (inzidenz < 35) {
                        imageView.setImageResource(R.drawable.ampel_gruen);
                    }else if (inzidenz >= 50) {
                        imageView.setImageResource(R.drawable.ampel_rot);
                    }else if (inzidenz >= 35 && inzidenz < 50) {
                        imageView.setImageResource(R.drawable.ampel_gelb);
                    }
                    break;
                }else{
                    textView2.setText("Unbekannter Landkreis!");
                    imageView.setImageResource(R.drawable.ampel_leer);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //TODO Stadt eingeben und Landkreis wird automatisch ermittelt
    //TODO Aktuelle Corona Regeln einbinden
    //TODO Automatische Standorterfassung
    //TODO Push-Benachrichtigung wenn Ampel auf Rot springt, wenn man den Landkreis wechselt
}