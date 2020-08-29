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
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
                launchMapActivity(false);
                break;
            case R.id.button2:
                launchMapActivity(true);
                break;
            case R.id.buttonBack:
                finish();
                break;
        }
    }

    private void launchMapActivity(Boolean time) {
        Intent i = new Intent(this, MapActivity.class);
        i.putExtra("value","");
        i.putExtra("time",time);
        startActivity(i);
    }
}