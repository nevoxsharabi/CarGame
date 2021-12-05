package com.example.cargame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.textview.MaterialTextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private ImageView car;
    private ArrayList<ImageView> rocks;
    private ArrayList<ImageView> hearts;
    private ArrayList<ImageView> coins;
    private Random random;
    private int score;
    private int sensorEnabled = 0;
    private int slowDelay = 600;
    private int fastDelay = 300;
    boolean fastDelayMode = false;
    private int distance;
    private int randomNumber;
    private MaterialTextView Game_MTV_distance;
    private MaterialTextView Game_MTV_score;
    private MaterialTextView Game_MTV_Speed;
    private LinearLayout linearLayoutArrows;

    private MediaPlayer crushSound;
    private MediaPlayer coinSound;
    private LocationManager lm;
    private float oldX = 10;
    private float oldZ = 10;
    private float newX = 0;
    private float newZ = 0;


    private LocationListener locationListener;
    private double longitude = 0;
    private double latitude = 0;
    private Record record;
    private Handler handler;
    private Runnable runnable;


    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener accSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            newX = event.values[0] + 10;
            newZ = event.values[2] + 10;
            if (newX - oldX > 0.5) {
                moveCarRightSensor();
            } else if (newX - oldX < -0.5) {
                moveCarLeftSensor();
            }

            if (newZ - oldZ > 1.5) {
                fastDelayMode = true;
                Game_MTV_Speed.setText("Speed Mode:fast");
            } else if (newZ - oldZ < -1.5) {
                fastDelayMode = false;
                Game_MTV_Speed.setText("Speed Mode:slow");

            }

            oldX = newX;
            oldZ = newZ;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Game_MTV_distance = findViewById(R.id.Game_MTV_distance);
        Game_MTV_score = findViewById(R.id.Game_MTV_score);
        Game_MTV_Speed = findViewById(R.id.Game_MTV_speedMode);
        linearLayoutArrows = findViewById(R.id.linearLayoutArrows);
        crushSound = MediaPlayer.create(GameActivity.this, R.raw.carsh);
        coinSound = MediaPlayer.create(GameActivity.this, R.raw.coin);
        random = new Random();
        score = 0;
        distance = 0;
        car = findViewById(R.id.imageViewCar2);
        hearts = new ArrayList<>();

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, locationListener);
        }


        for (int i = 0; i < 3; i++) {
            hearts.add(findViewById(getResources().getIdentifier("imageViewHeart" + i, "id", getPackageName())));
        }
        rocks = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            rocks.add(findViewById(getResources().getIdentifier("imageViewRock" + i, "id", getPackageName())));
        }
        coins = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            coins.add(findViewById(getResources().getIdentifier("imageViewCoin" + i, "id", getPackageName())));
        }

        Intent intent = getIntent();
        sensorEnabled = intent.getIntExtra("SENSOR_ENABLED", 0);
        if (sensorEnabled == 1) {
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            linearLayoutArrows.setVisibility(View.INVISIBLE);
        } else {
            Game_MTV_Speed.setVisibility(View.INVISIBLE);
            linearLayoutArrows.setVisibility(View.VISIBLE);
        }


        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dropItems();
                Game_MTV_distance.setText(++distance + "M");
                if (fastDelayMode) {
                    handler.postDelayed(this, fastDelay);
                } else {
                    handler.postDelayed(this, slowDelay);
                }

            }
        };
        handler.post(runnable);

    }


    private void moveCarLeftSensor() {
        int carTag = Integer.parseInt(car.getTag().toString());
        if (carTag != 0) {
            car.setVisibility(View.INVISIBLE);
            car = findViewById(getResources().getIdentifier("imageViewCar" + (carTag - 1), "id", getPackageName()));
            car.setVisibility(View.VISIBLE);
            if (checkCoin())
                updateScore();
            if (checkCrush())
                crush();

        }
    }


    private void moveCarRightSensor() {
        int carTag = Integer.parseInt(car.getTag().toString());
        if (carTag != 4) {
            car.setVisibility(View.INVISIBLE);
            car = findViewById(getResources().getIdentifier("imageViewCar" + (carTag + 1), "id", getPackageName()));
            car.setVisibility(View.VISIBLE);
            if (checkCoin())
                updateScore();

            if (checkCrush())
                crush();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorEnabled == 1)
            sensorManager.registerListener(accSensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorEnabled == 1)
            sensorManager.unregisterListener(accSensorEventListener);
    }

    private void initSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, locationListener);
            }
        }

    }



    @Override
    public void onBackPressed() {
        handler.removeCallbacks(runnable);
        Record record = new Record(distance, score, latitude, longitude);
        ScoreBoard.getInstance().addRecordToRecordsArray(getApplicationContext(), record);
        Intent intent1 = new Intent(getApplicationContext(), MenuActivity.class);
        lm.removeUpdates(locationListener);
        startActivity(intent1);
        finish();
    }


    private void updateScore() {
        score += 10;
        coinSound.start();
        Game_MTV_score.setText("Coins" + ": " + score + "$");
    }


    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }

    }

    private void startNewGame() {
        Record record = new Record(distance, score, latitude, longitude);
        ScoreBoard.getInstance().addRecordToRecordsArray(getApplicationContext(), record);
        for (int i = 39; i >= 0; i--) {
            rocks.get(i).setVisibility(View.INVISIBLE);
            coins.get(i).setVisibility(View.INVISIBLE);
        }
        car.setVisibility(View.INVISIBLE);
        car = findViewById(R.id.imageViewCar2);
        car.setVisibility(View.VISIBLE);
        score = 0;
        distance = 0;
        Game_MTV_score.setText("Coins" + ": " + score + "$");
        Game_MTV_distance.setText(0 + "M");

    }


    public void dropItems() {

        for (int i = 39; i >= 0; i--) {
            if (i > 34) {
                coins.get(i).setVisibility(View.INVISIBLE);
                rocks.get(i).setVisibility(View.INVISIBLE);
            } else {
                if (coins.get(i).getVisibility() == View.VISIBLE) {
                    coins.get(i).setVisibility(View.INVISIBLE);
                    coins.get(i + 5).setVisibility(View.VISIBLE);
                }
                if (rocks.get(i).getVisibility() == View.VISIBLE) {
                    rocks.get(i).setVisibility(View.INVISIBLE);
                    rocks.get(i + 5).setVisibility(View.VISIBLE);
                }
            }
        }
        if (checkCoin())
            updateScore();

        if (checkCrush())
            crush();
        randomNumber = random.nextInt(1000) % 12;
        if (randomNumber < 5)
            rocks.get(randomNumber).setVisibility(View.VISIBLE);

        randomNumber = random.nextInt(1000) % 20;
        if (randomNumber < 5 && rocks.get(randomNumber).getVisibility() == View.INVISIBLE)
            coins.get(randomNumber).setVisibility(View.VISIBLE);

    }

    private boolean checkCoin() {
        int carTag = Integer.parseInt(car.getTag().toString());
        switch (carTag) {
            case 0:
                if (coins.get(35).getVisibility() == View.VISIBLE) {
                    return true;
                } else
                    return false;


            case 1:
                if (coins.get(36).getVisibility() == View.VISIBLE) {
                    return true;
                } else
                    return false;

            case 2:
                if (coins.get(37).getVisibility() == View.VISIBLE) {
                    return true;
                } else
                    return false;

            case 3:
                if (coins.get(38).getVisibility() == View.VISIBLE) {
                    return true;
                } else
                    return false;

            case 4:
                if (coins.get(39).getVisibility() == View.VISIBLE) {
                    return true;
                } else
                    return false;

        }
        return false;

    }

    public void crush() {
        crushSound.start();
        if (hearts.get(0).getVisibility() == View.VISIBLE) {
            hearts.get(0).setVisibility(View.INVISIBLE);
            vibrate();
            toast("you got hit");

        } else if (hearts.get(1).getVisibility() == View.VISIBLE) {
            hearts.get(1).setVisibility(View.INVISIBLE);
            vibrate();
            toast("You got one life left, be careful");
        } else {
            vibrate();
            hearts.get(0).setVisibility(View.VISIBLE);
            hearts.get(1).setVisibility(View.VISIBLE);
            startNewGame();
            toast("you lost ,new game just started good luck");
        }
    }


    public boolean checkCrush() {
        int carTag = Integer.parseInt(car.getTag().toString());
        switch (carTag) {
            case 0:
                if (rocks.get(35).getVisibility() == View.VISIBLE) {
                    return true;
                } else
                    return false;


            case 1:
                if (rocks.get(36).getVisibility() == View.VISIBLE) {
                    return true;
                } else
                    return false;

            case 2:
                if (rocks.get(37).getVisibility() == View.VISIBLE) {
                    return true;
                } else
                    return false;

            case 3:
                if (rocks.get(38).getVisibility() == View.VISIBLE) {
                    return true;
                } else
                    return false;

            case 4:
                if (rocks.get(39).getVisibility() == View.VISIBLE) {
                    return true;
                } else
                    return false;

        }
        return false;

    }


    public void moveCar(View view) {
        int arrowTag = Integer.parseInt(view.getTag().toString());
        int carTag = Integer.parseInt(car.getTag().toString());
        if (arrowTag == 0) {
            if (carTag != 0) {
                car.setVisibility(View.INVISIBLE);
                car = findViewById(getResources().getIdentifier("imageViewCar" + (carTag - 1), "id", getPackageName()));
            }

        } else {
            if (carTag != 4) {
                car.setVisibility(View.INVISIBLE);
                car = findViewById(getResources().getIdentifier("imageViewCar" + (carTag + 1), "id", getPackageName()));
            }

        }
        car.setVisibility(View.VISIBLE);
        if (checkCoin())
            updateScore();

        if (checkCrush())
            crush();
    }
}

