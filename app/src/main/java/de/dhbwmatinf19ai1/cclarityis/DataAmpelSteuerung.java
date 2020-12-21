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

    /**
     * Methode zur Übergabe der wichtigen Parameter
     * @param eingabe
     * @param longitude
     * @param latitude
     * @param i
     */
    public void initalize(String eingabe, String longitude, String latitude, int i){
        this.eingabeTextfeldLocation = eingabe;
        this.longitudeAuto = longitude;
        this.latitudeAuto = latitude;
        this.methodchooser = i; //Zum prüfen, welche Methoden aufgerufen werden
    }

    /**
     * Methode wird aufgerufen, wenn es eine Eingabe über das Textfeld gab und der entpsrechende
     * Button gedrückt wurde
     *
     * @return --> Objekt für die Zahlen
     * @throws IOException
     * @throws JSONException
     */
    public Coronazahlen runAmpel() throws IOException, JSONException {
            String linkCounty = getCoordinatesLink(); //Link für Openstreetmap Json wird erstellt
            String responseCounty = getJsonFromWeb(linkCounty); //Link wird für die Webrequest verwendet
            ausgewaehlterLandkreis = getCounty(responseCounty); //Landkreis wird aus der Json ermittelt
            String responseRKI = getJsonFromWeb(rkiurl); //RKI-Json wird abgefragt
            calculateCases(responseRKI); //Die entsprechenden Daten werden aus der RKI-Json ausgelesen und gesetzt
            return werte;
    }

    /**
     * Methode wird ausgeführt, wenn die automatische Standortermittlung genutzt wird
     *
     * @return
     * @throws IOException
     * @throws JSONException
     */

    public Coronazahlen runAmpelAuto() throws IOException, JSONException {
        String linkCounty = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + latitudeAuto + "&lon=" + longitudeAuto; //Link für Openstreetmap Json zum entsprechenden Ort
        String responseCounty = getJsonFromWeb(linkCounty); //Webrequest mit vorher erstelltem Link
        ausgewaehlterLandkreis = getCounty(responseCounty); //Ermittlung des Landkreises
        String responseRKI = getJsonFromWeb(rkiurl); //Abfrage der RKI-Json
        calculateCases(responseRKI); //Die entsprechenden Daten werden aus der RKI-Json ausgelesen und gesetzt
        return werte;
    }


    /**
     * Methode für Webrequest über OkHTTP
     *
     * @param url --> Link der entsprechend erreicht werden soll
     * @return
     * @throws IOException
     */
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


    /**
     * Methode zum Auslesen der Json und zum Setzen der Werte
     * @param json
     */
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
                if (ausgewaehlterLandkreis.contains(landkreis)) { //Filter nach ausgewähltem Landkreis
                    werte.setBundesland(reader1.getString("BL")); //Bundesland wird gesetzt
                    werte.setAnzfaelle(reader1.getInt("cases")); //Anzahl der Fälle wird gesetzt (Im Landkreis)
                    werte.setInzidenzlandkreis(reader1.getInt("cases7_per_100k")); //Inzidenzwert für Landkreis wird gesetzt
                    werte.setStand(reader1.getString("last_update")); //Aktueller Stand wird gesetzt
                    werte.setInzidenzbundesland(reader1.getInt("cases7_bl_per_100k")); //Inzidenzwert für Bundesland wird gesetzt
                    DecimalFormat f = new DecimalFormat("#0.00"); //Verwendet um die Todesrate entsprechend darzustellen
                    double todesrate = reader1.getDouble("death_rate");
                    werte.setTodesrate(f.format(todesrate)); //Todesrate wird gesetzt
                    werte.setTode(reader1.getInt("deaths")); //Anzahl der Tode wird gesetzt
                    Log.d("Werte", "Werte für Landkreis gesetzt");
                    break; //Schleife wird abgebrochen
                }else{
                    Log.d("Werte", "Werte konnten nicht gesetzt werden");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Diese Methode dient dazu, einen Link aus den Koordinaten zu erstellen, um auf eine Json zu verweisen,
     * die relevante Standortinformationen beseitzt
     *
     * @return --> Eine entsprechende Url wird ausgegben
     */
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

    /**
     * Diese Methode liest aus der Json von Openstreetmap den entsprechenden Landkreis aus
     * @param responses --> Ist die abgefragte Json, in Form eines Strings
     * @return
     * @throws JSONException
     */
    public String getCounty(String responses) throws JSONException {

        String jsonData = responses;
        JSONObject reader = new JSONObject(jsonData);
        if (!reader.getJSONObject("address").isNull("county")) {
            String county = reader.getJSONObject("address").getString("county");
            return county;
        }else {
            if (reader.getJSONObject("address").has("town")) {
                eingabeTextfeldLocation = reader.getJSONObject("address").getString("town"); //Landkreis über town gefunden --> Stadtkreise
                Log.d("Landkreisermittlung", "Über town gefunden");
            }else if(reader.getJSONObject("address").has("city")) {
                eingabeTextfeldLocation = reader.getJSONObject("address").getString("city"); //Landkreis über city gefunden --> Stadkreise
                Log.d("Landkreisermittlung", "Über city gefunden");
            }else if(reader.getJSONObject("address").has("state")) {
                eingabeTextfeldLocation = reader.getJSONObject("address").getString("state"); //Landkreis über state gefunden
                Log.d("Landkreisermittlung", "Über state gefunden");
            }
            return eingabeTextfeldLocation;
        }
    }


    /**
     * Methode wird für die ausführung von Asynchronen Tasks benötigt
     * @param voids
     * @return
     */
    @Override
    protected Coronazahlen doInBackground(Void... voids) {
        try {
            switch(methodchooser) {
                case 0:
                    runAmpel(); //Methoden für Textfeldeingabe werden ausgeführt
                    Log.d("Ausführung", "Methoden über Textfeldeingabe");
                    break;
                case 1:
                    runAmpelAuto(); //Methoden für automatische Standortermittlung werden ausgeführt
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

    /**
     * Übergabe des Objektes an Interface, für die entsprechende Verwendeung in der Klasse, welche
     * das Interface implementiert
     * @param coronazahlen
     */
    @Override
    protected void onPostExecute(Coronazahlen coronazahlen) {

        delegate.finished(coronazahlen);
    }

}
