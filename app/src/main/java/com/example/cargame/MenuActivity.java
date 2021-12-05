package com.example.cargame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {
    private MaterialButton menu_BTN_Records;
    private MaterialButton menu_BTN_game;
    private SwitchMaterial switch_Sensor;
    private ArrayList<Integer> leaderboardScores;
    private int sensorEnabled = 0;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        switch_Sensor = findViewById(R.id.sensor_Switch);
        menu_BTN_Records = findViewById(R.id.menu_BTN_Records);
        menu_BTN_game = findViewById(R.id.menu_BTN_game);
        leaderboardScores = new ArrayList<Integer>();
        menu_BTN_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });
        menu_BTN_Records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecords();

            }
        });

        if (Leaderboard.getInstance().getRecordsArray() == null) {
            Leaderboard.getInstance().loadRecordsArrayFromSharedPreferences(getApplicationContext());
        }

        switch_Sensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                if (switch_Sensor.isChecked()) {
                    if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                        switch_Sensor.setChecked(true);
                        sensorEnabled = 1;
                    } else {
                        switch_Sensor.setChecked(false);
                    }
                } else {
                    sensorEnabled = 0;
                }
            }
        });

    }

    private void showRecords() {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
        //finish();
    }


    private void startGame() {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("SENSOR_ENABLED", sensorEnabled);
        startActivity(intent);
       // finish();
    }

}