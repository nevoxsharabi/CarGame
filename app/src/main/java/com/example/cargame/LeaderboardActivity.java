package com.example.cargame;

import androidx.appcompat.app.AppCompatActivity;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity {
    private RecordsListFragment listFragment;
    private MapsFragment mapsFragment;
    private CallbackList callbackList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderbord);
        listFragment = new RecordsListFragment();
        mapsFragment = new MapsFragment();
        callbackList = new CallbackList() {
            @Override
            public void setMapLocation(double latitude, double longitude) {
                mapsFragment.changeMap(latitude, longitude);
            }
        };
        getSupportFragmentManager().beginTransaction().add(R.id.ScoreBoardFrame, listFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.mapFrame, mapsFragment).commit();
        listFragment.setActivity(this);
        listFragment.setCallbackList(callbackList);

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(intent);
        finish();
    }

}