package com.example.bogystravels;

import android.app.Application;

import com.google.firebase.firestore.GeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CitiesQuery extends Application {
    public static JSONArray globalJA;
    public static JSONObject globalJO;
    public static ArrayList<Map<String,?>> collectionAM;

    public void set(JSONArray item) {
        globalJA = item;
    }

    public JSONArray convertJSONA(JSONArray arr) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < arr.length(); i++){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("city", arr.getJSONObject(i).get("name").toString());
            JSONObject jsonObjectC = (JSONObject) arr.getJSONObject(i).get("country");
            jsonObject.put("country", jsonObjectC.get("name").toString());
            jsonObject.put("countryCode", jsonObjectC.get("code").toString());
            JSONObject jsonObjectL = (JSONObject) arr.getJSONObject(i).get("location");
            jsonObject.put("latitude", jsonObjectL.get("latitude"));
            jsonObject.put("longitude", jsonObjectL.get("longitude").toString());
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    public void setCollection(ArrayList<Map<String,?>> item) {
        collectionAM = item;
    }

    public void setDefaultById(int i) throws JSONException {
        globalJO = globalJA.getJSONObject(i);
    }

    public void setDefault(JSONObject item) throws JSONException {
        globalJO = item;
    }

    public JSONArray get() {
        return globalJA;
    }

    public ArrayList<Map<String,?>> getCollection() {
        return collectionAM;
    }

    public JSONObject getById(int i) throws JSONException {
        return globalJA.getJSONObject(i);
    }

    public JSONObject getDefault(){
        return globalJO;
    }

    public JSONObject getDefaultS(){
        JSONObject result = globalJO;
        try {
            assert result != null;
            result.remove("id");
            result.remove("wikiDataId");
            result.remove("type");
            result.remove("name");
            result.remove("region");
            result.remove("regionCode");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getValue(int position, String value) throws JSONException {
        return this.getById(position).get(value).toString();
    }

    public List<String> getCityCountryL(){
        final List<String> citiesList = new ArrayList<String>();
        try{
            assert globalJA != null;
            for (int i = 0; i < globalJA.length(); i++) {
                citiesList.add(globalJA.getJSONObject(i).get("city").toString() + ", " + globalJA.getJSONObject(i).get("country").toString());
                //JSONObject neshto = (JSONObject) globalJA.getJSONObject(i).get("country");
                //citiesList.add(globalJA.getJSONObject(i).get("name").toString() + ", " + neshto.get("name").toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return citiesList;
    }

    public GeoPoint getDefaultCo() throws JSONException {
        GeoPoint result = new GeoPoint(globalJO.getDouble("latitude"), globalJO.getDouble("longitude"));
        return result;
    }

}