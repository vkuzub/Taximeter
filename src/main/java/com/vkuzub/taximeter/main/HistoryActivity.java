package com.vkuzub.taximeter.main;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.vkuzub.taximeter.R;
import com.vkuzub.taximeter.database.DBHelper;
import com.vkuzub.taximeter.model.Trip;

import java.util.ArrayList;

/**
 * Created by Vyacheslav on 28.10.2014.
 */
public class HistoryActivity extends Activity {

    private ListView lvMain;

    private DBHelper dbHelper;
    private ArrayList<Trip> tripArrayList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        getActionBar().setDisplayHomeAsUpEnabled(true);
        initWidgets();

        //запрос бд и заполнение
        dbHelper = new DBHelper(this, null);
        tripArrayList = readDataFromDB();
        fillList(tripArrayList);
    }

    private void fillList(ArrayList<Trip> list) {
        ArrayAdapter<Trip> adapter = new ArrayAdapter<Trip>(this, android.R.layout.simple_list_item_1, list);
        lvMain.setAdapter(adapter);
    }

    private ArrayList<Trip> readDataFromDB() {
        ArrayList<Trip> trips = new ArrayList<Trip>();
        String date, sum, distance;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DBHelper.tableName, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {

            int colDate = cursor.getColumnIndex("date");
            int colSum = cursor.getColumnIndex("sum");
            int colDistance = cursor.getColumnIndex("distance");

            do {
                date = cursor.getString(colDate);
                sum = cursor.getString(colSum);
                distance = cursor.getString(colDistance);

                trips.add(new Trip(date, sum, distance));

            } while (cursor.moveToNext());

        }

        cursor.close();
        db.close();
        dbHelper.close();

        return trips;
    }

    private void initWidgets() {
        lvMain = (ListView) findViewById(R.id.lvHistory);
    }

}