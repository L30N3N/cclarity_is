package de.dhbwmatinf19ai1.cclarityis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        url = "https://services7.arcgis.com/mOBPykOjAyBO2ZKk/arcgis/rest/services/RKI_Landkreisdaten/FeatureServer/0/query?where=1%3D1&outFields=GEN,death_rate,cases_per_100k,BL,county,last_update,cases7_per_100k,cases7_bl_per_100k,cases,deaths&returnGeometry=false&outSR=4326&f=json";
        btn = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getJsonFromWeb(url);
            }
        });
    }

    public void getJsonFromWeb(String url) {
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
                            calculateCases(myResponse);
                        }
                    });
                }
            }
        });
    }

    public void calculateCases(String json) {

        int anzfall = 0;
        String landkreis = null;
        int inzidenz = 0;
        String last_update = null;
        int todesrate = 0;
        int tode = 0;
        int inzidenz_bl = 0;
        String BL = null;


        try {
            JSONObject reader = new JSONObject(json);
            JSONArray features = reader.getJSONArray("features");

            for (int i = 0; i < features.length(); i++) {
                JSONObject c = features.getJSONObject(i);
                String test = c.getString("attributes");
                JSONObject reader1 = new JSONObject(test);
                landkreis = reader1.getString("GEN");
                if (landkreis.equals("Ostholstein")) {
                    BL = reader1.getString("BL");
                    anzfall = reader1.getInt("cases");
                    inzidenz = reader1.getInt("cases7_per_100k");
                    last_update = reader1.getString("last_update");
                    inzidenz_bl = reader1.getInt("cases7_bl_per_100k");
                    todesrate = reader1.getInt("death_rate");
                    tode = reader1.getInt("deaths");
                    break;
                }

            }

            textView2.setText(landkreis + "\n" + "(" + BL + ")"+  "\n\n" + "Anzahl der Fälle: " + anzfall + "\n\n" + "Inzidenzwert: " + inzidenz + "\n\n" + "Inzidenzwert (BL): " + inzidenz_bl + "\n\n" + "Todesfälle: " + tode + "\n\n" + "Todesrate: " + todesrate + "\n\n" + "Stand: " + last_update);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}