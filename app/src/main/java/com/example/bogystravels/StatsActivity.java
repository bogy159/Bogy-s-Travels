package com.example.bogystravels;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StatsActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        findViewById(R.id.buttonBack).setOnClickListener(this);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
                launchMapActivity(false);
                break;
            case R.id.button2:
                launchMapActivity(true);
                break;
            case R.id.button3:
                launchDotMapActivity(false);
                break;
            case R.id.button4:
                launchDotMapActivity(true);
                break;
            case R.id.button5:
                launchChartActivity();
                break;
            case R.id.button6:
                launchKotlinProbaActivity();
                break;
            case R.id.buttonBack:
                finish();
                break;
        }
    }

    private void launchMapActivity(Boolean time) {
        Intent i = new Intent(this, MapActivity.class);
        i.putExtra("value","world");
        i.putExtra("text","the World");
        i.putExtra("time",time);
        startActivity(i);
    }

    private void launchDotMapActivity(Boolean con) {
        Intent i = new Intent(this, DotMapActivity.class);
        i.putExtra("value","world");
        i.putExtra("text","the World");
        i.putExtra("con",con);
        startActivity(i);
    }

    private void launchChartActivity() {
        Intent i = new Intent(this, ChartActivity.class);
        //i.putExtra("value","world");
        //i.putExtra("text","the World");
        //i.putExtra("con",con);
        startActivity(i);
    }

    private void launchKotlinProbaActivity() {
        //Intent i = new Intent(this, KotlinProbaActivity.class);
        //startActivity(i);
    }
}