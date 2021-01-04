package de.dhbwmatinf19ai1.cclarityis.Rules;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import de.dhbwmatinf19ai1.cclarityis.R;
import de.dhbwmatinf19ai1.cclarityis.Rules.*;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Leon Nehring
 */
public class RulesFragment extends Fragment {

    RulesAdapter adapter;
    ExpandableListView expandableListView;
    TextView textview3;


    public RulesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rules, container, false);
        // Inflate the layout for this fragment
        expandableListView = root.findViewById(R.id.exp_list);


        return root;
    }

    public void getJsonFromWeb1(String url, String bl) {

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

                    Log.d("RulesAPI", "Abfrage Erfolgreich");

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getCoronaRules(myResponse, bl);

                        }
                    });
                }
            }
        });
    }

    public void getCoronaRules(String response, String bl) {
        ArrayList<String> element_title = new ArrayList<>();
        HashMap<String, ArrayList<String>> elements_text = new HashMap<>();
        try {
            JSONArray reader = new JSONArray(response);
            for (int i = 0; i < reader.length(); i++) {
                JSONObject o = reader.getJSONObject(i);

                String bundesland = o.getString("Bundesland");
                if(bundesland.equalsIgnoreCase(bl)) {
                    JSONObject a = o.getJSONObject("allgemein");
                    Iterator iterator = a.keys();
                    Log.d("RulesAPI", "Bundesland " + i + ": " + bundesland);
                    while(iterator.hasNext()){
                        String key = iterator.next().toString();
                        JSONObject obj = a.getJSONObject(key);
                        element_title.add(Html.fromHtml(key).toString());
                        ArrayList<String> arr = new ArrayList<>();
                        arr.add(Html.fromHtml(obj.getString("text")).toString());
                        elements_text.put(key, arr);
                    }
                    break;
                }
            }
            adapter = new RulesAdapter(element_title, elements_text);
            expandableListView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showList(Boolean show) {
        if(show) {
            expandableListView.setVisibility(View.VISIBLE);
        }else {
            expandableListView.setVisibility(View.GONE);
        }

    }

}