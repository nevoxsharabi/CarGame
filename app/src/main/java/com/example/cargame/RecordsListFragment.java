package com.example.cargame;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class RecordsListFragment extends Fragment {


    private ListView listViewLeaderboardCoins;
    private AppCompatActivity activity;
    private CallbackList callbackList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_records, container, false);

        listViewLeaderboardCoins = view.findViewById(R.id.LV_Leaderboard);

        StringBuffer listViewLeaderboardCoinsString = new StringBuffer();

        for (Record record : Leaderboard.getInstance().getRecordsArray()) {
            listViewLeaderboardCoinsString.append(record.getCoins() + "\n");
        }

        ArrayList<String> stringArrayList = new ArrayList<String>();

        for (Record record : Leaderboard.getInstance().getRecordsArray()) {
            stringArrayList.add("Coins: " + record.getCoins() + "    Distance: " + record.getDistance());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, stringArrayList);

        listViewLeaderboardCoins.setAdapter(arrayAdapter);

       listViewLeaderboardCoins.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (callbackList != null) {
                    ArrayList<Record> records = Leaderboard.getInstance().getRecordsArray();
                    double latitude = records.get(position).getLatitude();
                    double longitude = records.get(position).getLongitude();
                    callbackList.setMapLocation(latitude, longitude);
                }
            }
        });
        return view;
    }

    public void setCallbackList(CallbackList callbackList) {
        this.callbackList = callbackList;
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }
}