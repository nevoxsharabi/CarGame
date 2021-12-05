package com.example.cargame;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Comparator;

public class ScoreBoard extends Application {
    private ArrayList<Record> recordsArray;
    private static ScoreBoard scoreBoard;

    public ScoreBoard() {
        scoreBoard = this;
    }

    public static ScoreBoard getInstance() {
        if (scoreBoard == null) {
            scoreBoard = new ScoreBoard();
        }
        return scoreBoard;
    }

    public void addRecordToRecordsArray(Context context, Record record) {
        if (recordsArray.size() < 10) {
            recordsArray.add(record);
        } else {
            for (Record recordInArray : recordsArray) {
                if (record.getCoins() > recordInArray.getCoins()) {
                    recordsArray.remove(recordsArray.size() - 1);
                    recordsArray.add(record);
                    break;
                }
            }
        }
        //sort Array
        recordsArray.sort(new CoinsSorter());
        saveRecordsArrayToSharedPreferences(context);
    }

    private void saveRecordsArrayToSharedPreferences(Context context) {
        String json = new Gson().toJson(this.recordsArray);
        MySharedPreferences.getInstance(context).putStringSP("RECORDS_ARRAY", json);
    }

    public ArrayList<Record> getRecordsArray() {
        return recordsArray;
    }

    public void loadRecordsArrayFromSharedPreferences(Context context) {

        String recordsArrayString = MySharedPreferences.getInstance(context).getStringSP("RECORDS_ARRAY", "NULL");
        Log.i("info", "____________" + recordsArrayString + "______________________");
        if (recordsArrayString.equals("NULL")) {
            this.recordsArray = new ArrayList<Record>();
        } else {
            this.recordsArray = new Gson().fromJson(recordsArrayString, new TypeToken<ArrayList<Record>>() {
            }.getType());
        }
    }

    public class CoinsSorter implements Comparator<Record> {

        @Override
        public int compare(Record o1, Record o2) {
            if (o1.getCoins() == o2.getCoins())
                return 0;
            return o1.getCoins() < o2.getCoins() ? 1 : -1;
        }
    }
}