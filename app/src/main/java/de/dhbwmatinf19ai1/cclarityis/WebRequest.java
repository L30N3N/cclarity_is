package de.dhbwmatinf19ai1.cclarityis;

import android.os.AsyncTask;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WebRequest extends AsyncTask {
//features:[{attributes:{GEN:Rhein-Pfalz-Kreis,death_rate:0.808897876643074,cases_per_100k:1279.3563117283,BL:Rheinland-Pfalz,county:LK Rhein-Pfalz-Kreis,last_update:05.12.2020, 00:00 Uhr,cases7_per_100k:271.65300855707,cases7_bl_per_100k:130.511152804549,cases:1978,deaths:16}}]
   String result = "";
   Boolean received = false;
   String url;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void initialize(String url) {
        this.url = url;
    }

    public void getJsonFromWeb() {

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
                    setResult(myResponse);
                }
            }
        });
    }

    public boolean received() {
        return received;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Log.d("Tag", "onpostexecute" + result);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        getJsonFromWeb();
        return null;
    }
}
