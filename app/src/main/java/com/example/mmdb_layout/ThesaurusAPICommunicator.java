package com.example.mmdb_layout;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class ThesaurusAPICommunicator
{
    static RequestQueue requestQueue = null;
    static String url = "";
    static String urlBegin = "http://thesaurus.altervista.org/thesaurus/v1?word=";
    static String urlEnd ="&language=en_US&key=af0TnP3LuKxCpWfNRnKJ&output=json";
    static ArrayList<String> synonyms = new ArrayList<String>();
    static JsonObjectRequest jsonObjectRequest = null;



    public static void synonymRequest(Context context)
    {
        requestQueue = Volley.newRequestQueue(context);

        jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            saveSynonyms(response.getJSONArray("response").getJSONObject(0).getJSONObject("list").getString("synonyms"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        requestQueue.add(jsonObjectRequest);
        requestQueue.start();
    }

    static void saveSynonyms(String stringIN)
    {
        String[] synonymsRaw = stringIN.split(Pattern.quote("|"));

        for(String s : synonymsRaw)
        {
            synonyms.add(s.split(" ")[0]);

        }

        for(String s: synonyms)
        {
            Log.d("SynonymRequest", s);
        }
    }

    public static ArrayList<String> getSynonyms(String word,Context context)
    {
        synonyms.clear();
        url = urlBegin+word+urlEnd;
        synonymRequest(context);


        Log.d("Response",synonyms.size()+"");
        for(String s : synonyms)
        {
            Log.d("Response",s);
        }


        return synonyms;
    }

}
