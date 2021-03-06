package com.example.bogystravels;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.core.map.series.Connector;
import com.anychart.core.map.series.Marker;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.SelectionMode;
import com.anychart.graphics.vector.SolidFill;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DotMapActivity extends AppCompatActivity  implements View.OnClickListener{

    String TAG = "DotMapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dot_map);

        Bundle bundle = getIntent().getExtras();

        CitiesQuery citiesQuery = new CitiesQuery();
        ArrayList<Map<String,?>> collectionG = citiesQuery.getCollection();

        if(collectionG.isEmpty()){
            Toast.makeText(DotMapActivity.this, "No trip records found. Please, add more data!", Toast.LENGTH_LONG).show();
        }
        else {
            assert bundle != null;
            ArrayList<java.util.Map<String, Object>> dataRaw = reduceColCityCo(collectionG);
            List<DataEntry> dataEntries = countCities(dataRaw);
            if (dataEntries.isEmpty()){
                Toast.makeText(DotMapActivity.this, "Not enough data for this view.", Toast.LENGTH_LONG).show();
            }
            else{
                if (Objects.equals(bundle.getString("value"), "")){
                    Toast.makeText(DotMapActivity.this, "Please, choose the map buttons at the top.", Toast.LENGTH_LONG).show();
                }
                else{
                    if (bundle.getBoolean("con")){
                        List<DataEntry> routeEntries = getRouteData(dataRaw);
                        GenerateConnMap(dataEntries, routeEntries, bundle.getString("value"), bundle.getString("text"));
                    }
                    else {
                        GenerateDotMap(dataEntries,bundle.getString("value"), bundle.getString("text"));
                    }
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
                Toast.makeText(DotMapActivity.this, button.getText().toString(), Toast.LENGTH_LONG).show();
                reloadActivity(button.getText().toString().toLowerCase(),"the World");
                break;
            case R.id.buttonEurope:
            case R.id.buttonAfrica:
                Toast.makeText(DotMapActivity.this, button.getText().toString(), Toast.LENGTH_LONG).show();
                reloadActivity(button.getText().toString().toLowerCase(),button.getText().toString());
                break;
            case R.id.buttonAsia:
                Toast.makeText(DotMapActivity.this, button.getText().toString(), Toast.LENGTH_LONG).show();
                reloadActivity("bogy","Asia");
                break;
            case R.id.buttonSouthA:
                Toast.makeText(DotMapActivity.this, button.getText().toString(), Toast.LENGTH_LONG).show();
                reloadActivity("south", "South America");
                break;
            case R.id.buttonNorthA:
                Toast.makeText(DotMapActivity.this, button.getText().toString(), Toast.LENGTH_LONG).show();
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

    private void GenerateDotMap(List<DataEntry> data, String continent, String text){
        Log.e(TAG,"I shouldn't be here");

        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(findViewById(R.id.progress_bar));

        com.anychart.charts.Map map = AnyChart.map();

        map.credits().enabled(true);
        //map.credits()
        //        .url("https://opendata.socrata.com/dataset/Airport-Codes-mapped-to-Latitude-Longitude-in-the-/rxrh-4cxm")
        //        .text("Data source: https://opendata.socrata.com")
        //        .logoSrc("https://opendata.socrata.com/stylesheets/images/common/favicon.ico");

        map.unboundRegions()
                .enabled(true)
                .fill(new SolidFill("#E1E1E1", 1))
                .stroke("#D2D2D2");

        map.geoData("anychart.maps." + continent);

        map.title()
                .enabled(true)
                .useHtml(true)
                .padding(0, 0, 10, 0)
                .text("Cities visited in " + text + "<br/><span style=\"color:#929292; font-size: 12px;\">" +
                        "</span>");

        Marker series = map.marker(data);
        series.tooltip()
                .useHtml(true)
                .padding(8, 13, 10, 13)
                .title(false)
                .separator(false)
                .fontSize(14)
                //.format("function() {\n" +
                //        "            return '<span>' + this.getData('city') + '</span><br/>' +\n" +
                //        "              '<span style=\"font-size: 12px; color: #E1E1E1\">City: ' +\n" +
                //        "              this.getData('city') + '</span>';\n" +
                //        "          }");

                .format("function() {\n" +
                "            return '<span>' + this.getData('city') + '</span><br/>'  + '</span>';\n" +
                "          }");

        series.size(5)
                .labels(false);
        series.stroke("2 #E1E1E1")
                .fill("#1976d2", 1);
        series.selectionMode(SelectionMode.NONE);

        anyChartView.addScript("file:///android_asset/" + continent + ".js");
        anyChartView.addScript("file:///android_asset/proj4.js");
        anyChartView.setChart(map);
    }

    private void GenerateConnMap(List<DataEntry> data, List<DataEntry> routes,  String continent, String text){

        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(findViewById(R.id.progress_bar));

        com.anychart.charts.Map map = AnyChart.connector();

        map.unboundRegions()
                .enabled(true)
                .fill(new SolidFill("#E1E1E1", 1))
                .stroke("1 #D2D2D2");

        map.geoData("anychart.maps." + continent);
        map.interactivity().selectionMode(SelectionMode.NONE);

        map.title().enabled(true);
        map.title().useHtml(true);
        map.title().text("Journey Route: " + text + " Roundtrip<br/>");

        Connector connectorSeries = map.connector(routes);
        //connectorSeries.startSize(0)
        //        .endSize(0)
        //        .stroke("2 #64b5f6")
        //        .name("Rent a Car");
        connectorSeries.curvature(0);
        //connectorSeries.legendItem("{" +
        //        "        iconType: 'circle'," +
        //        "        fill: '#64b5f6'," +
        //        "        iconStroke: '2 #E1E1E1'" +
        //        "      }");
        //connectorSeries.hovered().stroke("1.5 #212121");

        connectorSeries.tooltip()
                .padding(8, 13, 10, 13)
                .fontSize(12)
                .fontColor("#fff")
                .format("To {%to} from {%from}.")
                .titleFormat("{%number}. <span style=\"font-size: 13px; color: #E1E1E1\">{%from}</span>")
                .useHtml(true);

        map.legend(false);

        Marker citiesSeries = map.marker(data);
        citiesSeries.type(MarkerType.CIRCLE)
                .size(4)
                .fill(new SolidFill("#64b5f6", 1))
                .stroke("2 #E1E1E1")
                .tooltip(false);
//
        citiesSeries.hovered()
                .size(4)
                .fill(new SolidFill("#64b5f6", 1))
                .stroke("2 #E1E1E1");

        citiesSeries.labels().enabled(true);
        citiesSeries.labels().position("center-bottom");
        citiesSeries.labels().fontColor("#263238");
        citiesSeries.labels()
                .offsetX(5)
                .offsetY(0)
                .anchor(Anchor.LEFT_CENTER)
                .format("{%city}");

        citiesSeries.legendItem().enabled(false);

        anyChartView.addScript("file:///android_asset/" + continent + ".js");
        anyChartView.addScript("file:///android_asset/proj4.js");
        anyChartView.setChart(map);
    }

    class CustomDataEntry extends DataEntry {
        public CustomDataEntry(String city, Double latitude, Double longitude) {
            setValue("city", city);
            setValue("lat", latitude);
            setValue("long", longitude);
        }

        public CustomDataEntry(Number[] points, String from, String to ) {
            setValue("points", points);
            setValue("from", from);
            setValue("to", to);
        }
    }

    public ArrayList<java.util.Map<String, Object>> reduceColCityCo(final ArrayList<java.util.Map<String,?>> col){

        LinkedHashSet<Map<String, Object>> hashSet = new LinkedHashSet<>();

        if (col != null){
            for (int i=0; i<col.size(); i++){
                java.util.Map<String, Object> item = new HashMap<>();
                item.put("location", col.get(i).get("location").toString());
                item.put("coordinates", col.get(i).get("coordinates"));
                if (item.get("coordinates")!=null){
                    hashSet.add(item);
                }
            }
        }

        return new ArrayList<>(hashSet);
    }

    public List<DataEntry> countCities(ArrayList<java.util.Map<String, Object>> col){
        List<DataEntry> data =  new ArrayList<>();

        if (col != null){
            for (int i=0; i<col.size(); i++){
                GeoPoint coo = (GeoPoint) col.get(i).get("coordinates");
                assert coo != null;
                DotMapActivity.CustomDataEntry customDataEntry = new CustomDataEntry(col.get(i).get("location").toString(), coo.getLatitude(), coo.getLongitude());
                data.add(customDataEntry);
            }
        }

        return data;
    }

    private List<DataEntry> getRouteData(ArrayList<java.util.Map<String, Object>> col){
        List<DataEntry> data = new ArrayList<>();

        if (col != null){
            for (int i=col.size()-2; i>=0; i--){
                GeoPoint coo1 = (GeoPoint) col.get(i+1).get("coordinates");
                GeoPoint coo2 = (GeoPoint) col.get(i).get("coordinates");
                assert coo1 != null;
                assert coo2 != null;
                //DotMapActivity.CustomDataEntry customDataEntry = new CustomDataEntry(col.get(i).get("location").toString(), coo.getLatitude(), coo.getLongitude());
                //DotMapActivity.CustomDataEntry customDataEntry = new CustomDataEntry(coo1.getLatitude(), coo1.getLongitude(), coo2.getLatitude(), coo2.getLongitude());
                DotMapActivity.CustomDataEntry customDataEntry = new CustomDataEntry(new Number[]{ coo1.getLatitude(), coo1.getLongitude(), coo2.getLatitude(), coo2.getLongitude()}, col.get(i+1).get("location").toString(), col.get(i).get("location").toString());
                data.add(customDataEntry);
            }
        }

        return data;
    }

}