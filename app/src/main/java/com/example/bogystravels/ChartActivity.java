package com.example.bogystravels;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Pair;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.TreeDataEntry;
import com.anychart.charts.TreeMap;
import com.anychart.core.ui.Title;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.anychart.enums.Orientation;
import com.anychart.enums.SelectionMode;
import com.anychart.enums.TreeFillingMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);


        CitiesQuery citiesQuery = new CitiesQuery();
        ArrayList<java.util.Map<String,?>> collectionG = citiesQuery.getCollection();

        //ArrayList<java.util.Map<String,Object>> neshto = durationPerCity(collectionG);

        //System.out.println("Bogy vij surovoto: " + neshto);

        generateTreeChart(treeData(durationPerCity(collectionG)));
    }

    private void generateTreeChart(List<DataEntry> data){
        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(findViewById(R.id.progress_bar));

        TreeMap treeMap = AnyChart.treeMap();

        treeMap.data(data, TreeFillingMethod.AS_TABLE);

        Title title = treeMap.title();
        title.enabled(true);
        title.useHtml(true);
        title.padding(0d, 0d, 20d, 0d);
        title.text("Visited locations<br/>' +\n" +
                "      '<span style=\"color:#212121; font-size: 13px;\">(Time spent in them in days)</span>");
/*
        treeMap.colorScale().ranges(new String[]{
                "{ less: 25000 }",
                "{ from: 25000, to: 30000 }",
                "{ from: 30000, to: 40000 }",
                "{ from: 40000, to: 50000 }",
                "{ from: 50000, to: 100000 }",
                "{ greater: 100000 }"
        });
*/
        treeMap.colorScale().colors(new String[]{
                "#9ecae1", "#6baed6", "#4292c6", "#2171b5", "#08519c", "#08306b"
        });

        treeMap.padding(10d, 10d, 10d, 20d);
        treeMap.maxDepth(2d);
        treeMap.hovered().fill("#F3F703", 1d);
        treeMap.selectionMode(SelectionMode.NONE);

        treeMap.legend().enabled(true);
        treeMap.legend()
                .padding(0d, 0d, 0d, 20d)
                .position(Orientation.RIGHT)
                .align(Align.TOP)
                .itemsLayout(LegendLayout.VERTICAL);

        treeMap.labels().useHtml(true);
        treeMap.labels().fontColor("#212121");
        treeMap.labels().fontSize(12d);
        treeMap.labels().format(
                "function() {\n" +
                        "      return this.getData('place');\n" +
                        "    }");

        treeMap.headers().format(
                "function() {\n" +
                        "    return this.getData('place');\n" +
                        "  }");

        treeMap.tooltip()
                .useHtml(true)
                .titleFormat("{%place}")
                .format("function() {\n" +
                        "      return '<span style=\"color: #bfbfbf\">Days spent: </span>' +\n" +
                        "        anychart.format.number(this.value, {\n" +
                        "          groupsSeparator: ' '\n" +
                        "        });\n" +
                        "    }");

        // set the maximum depth of levels shown
        treeMap.maxDepth(4);

        // set the depth of hints
        treeMap.hintDepth(2);

        // set the opacity of hints
        treeMap.hintOpacity(0.7);

        anyChartView.setChart(treeMap);
    }

    public ArrayList<java.util.Map<String,Object>> durationPerCity(ArrayList<java.util.Map<String,?>> col){

        ArrayList<java.util.Map<String,Object>> newData = new ArrayList<>();

        if (col != null){
            for (int i=0; i<col.size(); i++){
                java.util.Map<String,Object> item = new HashMap<>();
                item.put("location", col.get(i).get("location").toString());
                item.put("country", col.get(i).get("country").toString());
                item.put("continent", col.get(i).get("continent").toString());
                com.google.firebase.Timestamp a = (com.google.firebase.Timestamp) col.get(i).get("dateA");
                com.google.firebase.Timestamp d = (com.google.firebase.Timestamp) col.get(i).get("dateD");
                long elapsed = d.getSeconds()-a.getSeconds();
                int days = (int) (TimeUnit.SECONDS.toDays(elapsed) + 1);
                item.put("time", days);
                newData.add(item);
            }
        }

        return newData;
    }

    private List<DataEntry> treeData(ArrayList<java.util.Map<String,Object>> col){
        List<DataEntry> data = new ArrayList<>();
        data.add(new CustomTreeDataEntry("World", null, "World"));
        data.add(new CustomTreeDataEntry("Europe", "World", "Europe"));
        data.add(new CustomTreeDataEntry("Asia", "World", "Asia"));
        data.add(new CustomTreeDataEntry("North America", "World", "North America"));
        data.add(new CustomTreeDataEntry("South America", "World", "North America"));
        data.add(new CustomTreeDataEntry("Africa", "World", "Africa"));
        data.add(new CustomTreeDataEntry("Oceania", "World", "Oceania"));
        data.add(new CustomTreeDataEntry("Other", "World", "Other"));

        if (col != null){
            for (int i=0; i< col.size(); i++){
                String continent = col.get(i).get("continent").toString();
                if (!continent.equals("Europe") && !continent.equals("Asia") && !continent.equals("Africa") && !continent.equals("North America") && !continent.equals("South America") && !continent.equals("Oceania")) {
                    continent = "Other";
                }
                String country = col.get(i).get("country").toString();
                if (country.equals("")) {
                    country = "Other Nation";
                }
                CustomTreeDataEntry customTreeDataEntry1 = new CustomTreeDataEntry(country, continent, country);
                CustomTreeDataEntry customTreeDataEntry2 = new CustomTreeDataEntry(col.get(i).get("location").toString(), country,col.get(i).get("location").toString(), (Integer) col.get(i).get("time"));
                data.add(customTreeDataEntry1);
                data.add(customTreeDataEntry2);
            }
        }

        return data;
    }

    private class CustomTreeDataEntry extends TreeDataEntry {
        CustomTreeDataEntry(String id, String parent, String place, Integer value) {
            super(id, parent, value);
            setValue("place", place);
        }

        CustomTreeDataEntry(String id, String parent, String place) {
            super(id, parent);
            setValue("place", place);
        }
    }
}