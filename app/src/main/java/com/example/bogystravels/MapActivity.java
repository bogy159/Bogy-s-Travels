package com.example.bogystravels;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Map;
import com.anychart.core.map.series.Choropleth;
import com.anychart.core.ui.ColorRange;
import com.anychart.enums.SelectionMode;
import com.anychart.enums.SidePosition;
import com.anychart.graphics.vector.text.HAlign;
import com.anychart.scales.LinearColor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MapActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Bundle bundle = getIntent().getExtras();

        CitiesQuery citiesQuery = new CitiesQuery();
        ArrayList<java.util.Map<String,?>> collectionG = citiesQuery.getCollection();

        if(collectionG.isEmpty()){
            Toast.makeText(MapActivity.this, "No trip records found. Please, add more data!", Toast.LENGTH_LONG).show();
        }
        else{
            assert bundle != null;
            List<DataEntry> dataEntries;
            if (bundle.getBoolean("time")){
                dataEntries = countDurations(reduceColWithTime(collectionG,"country", "countryCode"));
            }
            else
            {
                dataEntries = countCountries(reduceColTo2(collectionG,"country", "countryCode"));
            }
            if (dataEntries.isEmpty()){
                Toast.makeText(MapActivity.this, "Not enough data for this view.", Toast.LENGTH_LONG).show();
            }
            else{
                if (Objects.equals(bundle.getString("value"), "")){
                    Toast.makeText(MapActivity.this, "Please, choose the map buttons at the top.", Toast.LENGTH_LONG).show();
                }
                else{
                    GenerateMap(dataEntries,bundle.getString("value"), bundle.getString("text"), bundle.getBoolean("time"));
                }
            }
        }

        findViewById(R.id.buttonWorld).setOnClickListener(this);
        findViewById(R.id.buttonEurope).setOnClickListener(this);
        findViewById(R.id.buttonAfrica).setOnClickListener(this);
        findViewById(R.id.buttonAsia).setOnClickListener(this);
        findViewById(R.id.buttonNorthA).setOnClickListener(this);
        findViewById(R.id.buttonSouthA).setOnClickListener(this);
    }

    public void onClick(View view) {
        Button button = (Button) view;
        switch (view.getId()) {
            case R.id.buttonWorld:
                Toast.makeText(MapActivity.this, button.getText().toString(), Toast.LENGTH_LONG).show();
                reloadActivity(button.getText().toString().toLowerCase(),"the World");
                break;
            case R.id.buttonEurope:
            case R.id.buttonAfrica:
                Toast.makeText(MapActivity.this, button.getText().toString(), Toast.LENGTH_LONG).show();
                reloadActivity(button.getText().toString().toLowerCase(),button.getText().toString());
                break;
            case R.id.buttonAsia:
                Toast.makeText(MapActivity.this, button.getText().toString(), Toast.LENGTH_LONG).show();
                reloadActivity("bogy","Asia");
                break;
            case R.id.buttonSouthA:
                Toast.makeText(MapActivity.this, button.getText().toString(), Toast.LENGTH_LONG).show();
                reloadActivity("south", "South America");
                break;
            case R.id.buttonNorthA:
                Toast.makeText(MapActivity.this, button.getText().toString(), Toast.LENGTH_LONG).show();
                reloadActivity("north", "North America");
                break;
        }
    }

    private void reloadActivity(String s, String t){
        Intent intent2 = getIntent().putExtra("value", s).putExtra("text", t);
        intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent2);
    }

    public ArrayList<java.util.Map<String,String>> reduceColTo2(final ArrayList<java.util.Map<String,?>> col, String arg1, String arg2){

        ArrayList<java.util.Map<String,String>> newData = new ArrayList<>();

        if (col != null){
            for (int i=0; i<col.size(); i++){
                java.util.Map<String,String> item = new HashMap<>();
                item.put(arg1, col.get(i).get(arg1).toString());
                item.put(arg2, col.get(i).get(arg2).toString());
                item.put("location", col.get(i).get("location").toString());
                newData.add(item);
            }
        }

        LinkedHashSet<java.util.Map<String, String>> hashSet = new LinkedHashSet<>(newData);
        ArrayList<java.util.Map<String, String>> result = new ArrayList<>(hashSet);

        for (int i=0; i<result.size(); i++){
            result.get(i).remove("location");
        }

        return result;
    }

    public Pair<String[], long[]> reduceColWithTime(final ArrayList<java.util.Map<String,?>> col, String arg1, String arg2){

        ArrayList<java.util.Map<String,Object>> newData = new ArrayList<>();
        Set<String> countrySet = new HashSet<String>();

        if (col != null){
            for (int i=0; i<col.size(); i++){
                java.util.Map<String,Object> item = new HashMap<>();
                item.put(arg1, col.get(i).get(arg1).toString());
                item.put(arg2, col.get(i).get(arg2).toString());
                com.google.firebase.Timestamp a = (com.google.firebase.Timestamp) col.get(i).get("dateA");
                com.google.firebase.Timestamp d = (com.google.firebase.Timestamp) col.get(i).get("dateD");
                long elapsed = d.getSeconds()-a.getSeconds();
                long days = TimeUnit.SECONDS.toDays(elapsed) + 1;
                countrySet.add(col.get(i).get(arg2).toString());
                item.put("time", days);
                newData.add(item);
            }
        }

        String countryArr[] = countrySet.toArray(new String[0]);

        long durations[] = new long[countryArr.length];

        for (int i=0; i<newData.size(); i++){
            for (int j=0; j<countryArr.length; j++){
                if (newData.get(i).get(arg2).toString().equals(countryArr[j])){
                    durations[j] = durations[j] + (long) newData.get(i).get("time");
                }
            }
        }

        return new Pair<>(countryArr, durations);
    }

    public List<DataEntry> countCountries(ArrayList<java.util.Map<String,String>> col){
        List<DataEntry> data =  new ArrayList<>();

        if (col != null){
            for (int i=0; i<col.size(); i++){
                CustomDataEntry customDataEntry = new CustomDataEntry(col.get(i).get("countryCode"), col.get(i).get("country"), Collections.frequency(col, col.get(i)), new LabelDataEntry(false));
                if (!data.contains(customDataEntry)){
                    data.add(customDataEntry);
                }
            }
        }

        return data;
    }

    public List<DataEntry> countDurations(Pair<String[], long[]> col){
        List<DataEntry> data =  new ArrayList<>();

        if (col != null){
            for (int i=0; i< col.first.length; i++){
                CustomDataEntry customDataEntry = new CustomDataEntry(col.first[i], col.second[i], new LabelDataEntry(false));
                data.add(customDataEntry);
            }
        }

        return data;
    }

    private void GenerateMap(List<DataEntry> data, String continent, String text, Boolean t){
        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(findViewById(R.id.progress_bar));

        Map map = AnyChart.map();

        map.title()
                .enabled(true)
                .useHtml(true)
                .hAlign(HAlign.CENTER)
                .fontFamily("Verdana, Helvetica, Arial, sans-serif")
                .padding(10, 0, 10, 0)
                .text("<span style=\"color:#7c868e; font-size: 18px\"> Most visited countries around " + text + ".</span> <br>" +
                        "<span style=\"color:#545f69; font-size: 14px\">Per number of cities visited</span>");

        Bundle bundle = getIntent().getExtras();
        if (bundle.getBoolean("time")){
            map.title().text("<span style=\"color:#7c868e; font-size: 18px\"> Time spent in countries around " + text + ".</span> <br>" +
                    "<span style=\"color:#545f69; font-size: 14px\">Per days spent in the country</span>");
        }

        map.credits().enabled(false);
        //map.credits()
        //        .enabled(true)
        //        .url("https://en.wikipedia.org/wiki/List_of_sovereign_states_and_dependent_territories_by_population_density")
        //        .text("Data source: https://en.wikipedia.org/wiki/List_of_sovereign_states_and_dependent_territories_by_population_density")
        //        .logoSrc("https://static.anychart.com/images/maps_samples/USA_Map_with_Linear_Scale/favicon.ico");

        map.geoData("anychart.maps." + continent);

        ColorRange colorRange = map.colorRange();
        colorRange.enabled(true)
                .colorLineSize(10)
                .stroke("#B9B9B9")
                .labels("{ 'padding': 3 }")
                .labels("{ 'size': 7 }");
        colorRange.ticks()
                .enabled(true)
                .stroke("#B9B9B9")
                .position(SidePosition.OUTSIDE)
                .length(10);
        colorRange.minorTicks()
                .enabled(true)
                .stroke("#B9B9B9")
                .position("outside")
                .length(5);

        map.interactivity().selectionMode(SelectionMode.NONE);
        map.padding(0, 0, 0, 0);

        //Choropleth series = map.choropleth(getData());
        Choropleth series = map.choropleth(data);
        LinearColor linearColor = LinearColor.instantiate();
        linearColor.colors(new String[]{ "#c2e9fb", "#81d4fa", "#01579b", "#002746"});
        series.colorScale(linearColor);
        series.hovered()
                .fill("#f48fb1")
                .stroke("#f99fb9");
        series.selected()
                .fill("#c2185b")
                .stroke("#c2185b");
        series.labels().enabled(true);
        series.labels().fontSize(10);
        series.labels().fontColor("#212121");
        series.labels().format("{%Value}");

        if (t){
            series.tooltip()
                    .useHtml(true)
                    .format("function() {\n" +
                            "            return '<span style=\"font-size: 13px\">' + this.value + ' days spent in country</span>';\n" +
                            "          }");
        }
        else {
            series.tooltip()
                    .useHtml(true)
                    .format("function() {\n" +
                            "            return '<span style=\"font-size: 13px\">' + this.value + ' visited cities</span>';\n" +
                            "          }");
        }

        anyChartView.addScript("file:///android_asset/" + continent + ".js");
        anyChartView.addScript("file:///android_asset/proj4.js");
        anyChartView.setChart(map);
    }

    class CustomDataEntry extends DataEntry {
        public CustomDataEntry(String id, long value, LabelDataEntry label) {
            setValue("id", id);
            setValue("value", value);
            setValue("label", label);
        }
        public CustomDataEntry(String id, String name, Number value) {
            setValue("id", id);
            setValue("name", name);
            setValue("value", value);
        }
        public CustomDataEntry(String id, String name, Number value, LabelDataEntry label) {
            setValue("id", id);
            setValue("name", name);
            setValue("value", value);
            setValue("label", label);
        }
    }

    class LabelDataEntry extends DataEntry {
        LabelDataEntry(Boolean enabled) {
            setValue("enabled", enabled);
        }
    }

}