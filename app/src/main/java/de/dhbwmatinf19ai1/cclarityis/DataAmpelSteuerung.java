package de.dhbwmatinf19ai1.cclarityis;


import android.location.Address;

import android.os.AsyncTask;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.location.GeocoderNominatim;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Yannick Schroth, Tobias Schweikart
 */

public class DataAmpelSteuerung extends AsyncTask<Void, Void, Coronazahlen> {
    String eingabeTextfeldLocation;

    String ausgewaehlterLandkreis;

    final String rkiurl = "https://services7.arcgis.com/mOBPykOjAyBO2ZKk/arcgis/rest/services/RKI_Landkreisdaten/FeatureServer/0/query?where=1%3D1&outFields=GEN,death_rate,cases_per_100k,BL,county,last_update,cases7_per_100k,cases7_bl_per_100k,cases,deaths&returnGeometry=false&outSR=4326&f=json";

    Coronazahlen werte  = new Coronazahlen();

    String longitudeAuto;

    String latitudeAuto;

    int methodchooser;



    public DataAmpelSteuerung() {

    }

    public void initalize(String eingabe, String longitude, String latitude, int i){
        this.eingabeTextfeldLocation = eingabe;
        this.longitudeAuto = longitude;
        this.latitudeAuto = latitude;
        this.methodchooser = i;
    }


    public Coronazahlen runAmpel() throws IOException, JSONException {
            String linkCounty = getCoordinatesLink();
            String responseCounty = getJsonFromWeb(linkCounty);
            ausgewaehlterLandkreis = getCounty(responseCounty);
            String responseRKI = getJsonFromWeb(rkiurl);
            calculateCases(responseRKI);
            return werte;
    }

    public Coronazahlen runAmpelAuto() throws IOException, JSONException {
        String linkCounty = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + latitudeAuto + "&lon=" + longitudeAuto;
        String responseCounty = getJsonFromWeb(linkCounty);
        ausgewaehlterLandkreis = getCounty(responseCounty);
        String responseRKI = getJsonFromWeb(rkiurl);
        calculateCases(responseRKI);
        return werte;
    }


    public String getJsonFromWeb(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response responses = null;

            responses = client.newCall(request).execute();
            Log.d("Web-Request", "Web-Abfrage für JSON");
        return responses.body().string();
    }




    public void calculateCases(String json) {


        try {
            JSONObject reader = new JSONObject(json);
            JSONArray features = reader.getJSONArray("features");

            for (int i = 0; i < features.length(); i++) {
                JSONObject c = features.getJSONObject(i);
                String test = c.getString("attributes");
                JSONObject reader1 = new JSONObject(test);
                String landkreis = reader1.getString("GEN");
                werte.setLankreis(landkreis);
                if (ausgewaehlterLandkreis.contains(landkreis)) {
                    werte.setBundesland(reader1.getString("BL"));
                    werte.setAnzfaelle(reader1.getInt("cases"));
                    werte.setInzidenzlandkreis(reader1.getInt("cases7_per_100k"));
                    werte.setStand(reader1.getString("last_update"));
                    werte.setInzidenzbundesland(reader1.getInt("cases7_bl_per_100k"));
                    DecimalFormat f = new DecimalFormat("#0.00");
                    double todesrate = reader1.getDouble("death_rate");
                    werte.setTodesrate(f.format(todesrate));
                    werte.setTode(reader1.getInt("deaths"));
                    Log.d("Werte", "Werte für Landkreis gesetzt");
                    break;
                }else{
                    Log.d("Werte", "Werte konnten nicht gesetzt werden");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getCoordinatesLink() {
        final GeocoderNominatim geocoderNominatim = new GeocoderNominatim("default-user-agent");
        String urladd = "";
        try {
            List<Address> addresses = geocoderNominatim.getFromLocationName(eingabeTextfeldLocation, 10);
            Address address = addresses.get(0);
            String latitude = Double.toString(address.getLatitude());
            String longitude = Double.toString(address.getLongitude());
            urladd = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + latitude + "&lon=" + longitude;
            Log.d("Koordinaten", "Koordinaten wurden ermittelt");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Koordinaten", "Koordinaten nicht gefunden");
        }
        return urladd;
    }

    public String getCounty(String responses) throws JSONException {

        String jsonData = responses;
        JSONObject reader = new JSONObject(jsonData);
        if (!reader.getJSONObject("address").isNull("county")) {
            String county = reader.getJSONObject("address").getString("county");
            return county;
        }else {
            if (reader.getJSONObject("address").has("town")) {
                eingabeTextfeldLocation = reader.getJSONObject("address").getString("town");
                Log.d("Landkreisermittlung", "Über town gefunden");
            }else if(reader.getJSONObject("address").has("state") && reader.getJSONObject("address").has("city")) {
                eingabeTextfeldLocation = reader.getJSONObject("address").getString("city");
                Log.d("Landkreisermittlung", "Über city gefunden");
            }else if(reader.getJSONObject("address").has("state")) {
                eingabeTextfeldLocation = reader.getJSONObject("address").getString("state");
                Log.d("Landkreisermittlung", "Über state gefunden");
            }
            return eingabeTextfeldLocation;
        }
    }


    @Override
    protected Coronazahlen doInBackground(Void... voids) {
        try {
            switch(methodchooser) {
                case 0:
                    runAmpel();
                    Log.d("Ausführung", "Methoden über Textfeldeingabe");
                    break;
                case 1:
                    runAmpelAuto();
                    Log.d("Ausführung", "Methoden über automatische Ermittlung");
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return werte;
    }

    public CoronaResponseAsync delegate = null;

    @Override
    protected void onPostExecute(Coronazahlen coronazahlen) {

        delegate.finished(coronazahlen);
    }

}
