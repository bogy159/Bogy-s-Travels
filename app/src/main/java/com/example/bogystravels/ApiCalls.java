package com.example.bogystravels;

import android.content.ContentProvider;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ApiCalls {

    private static final String TAG = "API Calls";

    public List<String> apiCall(final String apiKey, final String apiID, final String prefix) throws IOException, InterruptedException {
        List<String> citiesList = new ArrayList<String>();

        try {
            //JSONArray arr = geoDBCities(apiKey, prefix);
            JSONArray arr = Back4AppCities(apiKey, apiID, prefix);

            CitiesQuery citiesQuery = new CitiesQuery();
            arr = citiesQuery.convertJSONA(arr);
            citiesQuery.set(arr);
            citiesList = citiesQuery.getCityCountryL();

        } catch (Throwable t) {
            Log.e(TAG, "Error: \"" + t + "\"");
        }

        return citiesList;
    }

    public JSONArray geoDBCities(final String apiKey, final String prefix) throws InterruptedException {
        final JSONArray[] result = new JSONArray[1];
        Thread newThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url("https://wft-geo-db.p.rapidapi.com/v1/geo/cities?limit=5&namePrefix="+ prefix +"&sort=-population&types=CITY")
                            .get()
                            .addHeader("x-rapidapi-host", "wft-geo-db.p.rapidapi.com")
                            .addHeader("x-rapidapi-key", apiKey)
                            .build();

                    Response response = client.newCall(request).execute();
                    String jsonData = response.body().string();

                    try {
                        JSONObject obj = new JSONObject(jsonData);
                        result[0] = obj.getJSONArray("data");

                    } catch (Throwable t) {
                        Log.e(TAG, "Could not parse malformed JSON: \"" + jsonData + "\"");
                        Log.e(TAG, "Error: \"" + t + "\"");
                    }
                }
                catch (Exception e) {
                    Log.e(TAG,"An error has occured: " + e);
                }
            }
        });

        newThread.start();
        newThread.join();

        return result[0];
    }

    public String goeDBCountry(final String apiKey, final String prefix) throws InterruptedException {

        final String[] result = {""};
        Thread newThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url("https://wft-geo-db.p.rapidapi.com/v1/geo/cities?limit=1&namePrefix="+ prefix +"&sort=-population&types=CITY")
                            .get()
                            .addHeader("x-rapidapi-host", "wft-geo-db.p.rapidapi.com")
                            .addHeader("x-rapidapi-key", apiKey)
                            .build();

                    Response response = client.newCall(request).execute();
                    String jsonData = response.body().string();

                    try {
                        JSONObject obj = new JSONObject(jsonData);
                        JSONArray arr = obj.getJSONArray("data");

                        CitiesQuery citiesQuery = new CitiesQuery();
                        citiesQuery.setDefault(arr.getJSONObject(0));

                        result[0] = arr.getJSONObject(0).get("country").toString();

                    } catch (Throwable t) {
                        Log.e(TAG, "Could not parse malformed JSON: \"" + jsonData + "\"");
                        Log.e(TAG, "Error: \"" + t + "\"");
                    }
                }
                catch (Exception e) {
                    Log.e(TAG,"An error has occured: " + e);
                }
            }
        });

        newThread.start();
        newThread.join();

        return result[0];
    }

    public JSONArray Back4AppCities(final String apiKey, final String apiID, final String prefix) throws InterruptedException {
        final JSONArray[] result = new JSONArray[1];
        Thread newThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String where = URLEncoder.encode("{" +
                            "    \"name\": {" +
                            "        \"$regex\": \"(?i)^mona\"" +
                            "    }" +
                            "}", "utf-8");
                    System.out.println(where);

                    where = "%7B++++%22name%22%3A+%7B++++++++%22%24regex%22%3A+%22%28%3Fi%29%5E" + prefix + "%22++++%7D%7D";

                    URL url = new URL("https://parseapi.back4app.com/classes/Continentscountriescities_City?limit=5&order=-population&include=country&keys=name,country,country.name,country.code,location&where=" + where);
                    HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setRequestProperty("X-Parse-Application-Id", apiID); // This is your app's application id
                    urlConnection.setRequestProperty("X-Parse-REST-API-Key", apiKey); // This is your app's REST API key
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        JSONObject data = new JSONObject(stringBuilder.toString()); // Here you have the data that you need
                        result[0] = data.getJSONArray("results");
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.toString());
                }

            }
        });

        newThread.start();
        newThread.join();

        return result[0];
    }

    public String back4AppCountry(final String apiKey, final String apiID, final String prefix) throws InterruptedException {

        final String[] result = {""};
        Thread newThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url("https://parseapi.back4app.com/classes/Continentscountriescities_City?limit=1&order=-population&include=country&keys=name,country,country.name,country.code,location&where=%7B++++%22name%22%3A+%7B++++++++%22%24regex%22%3A+%22%28%3Fi%29%5E" + prefix + "%22++++%7D%7D")
                            .get()
                            .addHeader("X-Parse-Application-Id", apiID)
                            .addHeader("X-Parse-REST-API-Key", apiKey)
                            .build();

                    Response response = client.newCall(request).execute();
                    String jsonData = response.body().string();

                    try {
                        JSONObject obj = new JSONObject(jsonData);
                        JSONArray arr = obj.getJSONArray("results");

                        CitiesQuery citiesQuery = new CitiesQuery();
                        arr = citiesQuery.convertJSONA(arr);
                        citiesQuery.setDefault(arr.getJSONObject(0));

                        result[0] = arr.getJSONObject(0).get("country").toString();

                    } catch (Throwable t) {
                        Log.e(TAG, "Could not parse malformed JSON: \"" + jsonData + "\"");
                        Log.e(TAG, "Error: \"" + t + "\"");
                    }
                }
                catch (Exception e) {
                    Log.e(TAG,"An error has occured: " + e);
                }
            }
        });

        newThread.start();
        newThread.join();

        return result[0];
    }
}
