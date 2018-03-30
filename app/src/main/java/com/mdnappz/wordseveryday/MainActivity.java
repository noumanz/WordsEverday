package com.mdnappz.wordseveryday;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ListView startingList;
    private ArrayList<String> dates, titles;
    private ArrayList<Integer> ids;
    private ArrayAdapter startingArrayAdapter;
    public HashMap<Integer, String> actualEntries;
    public static final int ENTRY_MODIFY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dates = new ArrayList<String>();
        titles = new ArrayList<String>();
        ids = new ArrayList<Integer>();
        actualEntries = new HashMap<Integer, String>();
        updateTitlesAndDates();

        Button addDate = (Button) findViewById(R.id.addDate);
        startingList = (ListView) findViewById(R.id.startingList);
        startingArrayAdapter = new CustomAdapter(this, R.layout.row, dates);

        if (startingList != null) {
            startingList.setAdapter(startingArrayAdapter);
        }

        startingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), AddEntry.class);
                intent.putExtra("thisEntryKey", titles.get(i));
                intent.putExtra("actualEntry", actualEntries.get(ids.get(i)));
                intent.putExtra("thisEntryID", ids.get(i));
                startActivityForResult(intent, ENTRY_MODIFY);
            }
        });

        addDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                writeToTable("", "", date, 0, true);
                updateTitlesAndDates();

                startingArrayAdapter = new CustomAdapter(getApplicationContext(), R.layout.row , dates);

                if (startingList != null) {
                    startingList.setAdapter(startingArrayAdapter);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENTRY_MODIFY && resultCode == 1 && data.hasExtra("thisEntryReturn") && data.hasExtra("thisEntryKeyReturn")){
            Bundle extras = data.getExtras();
            String key = extras.getString("thisEntryKeyReturn");
            String entry = extras.getString("thisEntryReturn");
            int id = extras.getInt("thisEntryID");
            actualEntries.put(id, entry);
            writeToTable(key, entry, "", id , false);
            updateTitlesAndDates();
            if (startingList != null) {
                startingList.setAdapter(startingArrayAdapter);
            }
            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
        }
    }

    protected void writeToTable(String title, String entry, String date, int id, boolean isNew){
        SQLiteDatabase mydb = openOrCreateDatabase("entriesDB",MODE_PRIVATE,null);
        ContentValues values = new ContentValues();
        values.put("Title", title);
        values.put("Entry", entry);
        existTheTable(mydb);
        if (isNew){
            values.put("Date", date);
            mydb.insert("EntriesTable", null, values);
        } else {
            mydb.update("EntriesTable", values, "ID="+id, null);
        }
    }

    protected void existTheTable(SQLiteDatabase mydb){
        if (mydb != null){
            mydb.execSQL("CREATE TABLE IF NOT EXISTS EntriesTable(ID INTEGER PRIMARY KEY AUTOINCREMENT, Title VARCHAR, Entry VARCHAR, Date VARCHAR)");
        }
    }

    protected void updateTitlesAndDates(){
        SQLiteDatabase mydb = openOrCreateDatabase("entriesDB",MODE_PRIVATE,null);
        if (mydb != null){
            titles = new ArrayList<String>();
            dates = new ArrayList<String>();
            ids = new ArrayList<Integer>();
            actualEntries = new HashMap<>();
            existTheTable(mydb);
            Cursor resultSet = mydb.rawQuery("Select Title, Date, ID, Entry from EntriesTable ORDER BY ID desc LIMIT 10",null);
            try {
                while(resultSet.moveToNext()) {
                    titles.add(resultSet.getString(0));
                    ids.add(resultSet.getInt(2));
                    actualEntries.put(resultSet.getInt(2), resultSet.getString(3));
                    dates.add(resultSet.getString(1));
                }
            } finally {
                resultSet.close();
            }
        }
    }

    public class CustomAdapter extends ArrayAdapter<String>{
        public CustomAdapter(Context context, int resId, ArrayList<String> data){
            super(context, resId, data);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View customizedView = convertView;

            if (customizedView == null) {
                LayoutInflater inflater = getLayoutInflater();
                customizedView = inflater.inflate(R.layout.row, null);
            }
            String date = dates.get(position);
            String title = titles.get(position);

            TextView titleText = (TextView)customizedView.findViewById(R.id.titleText);
            TextView dateText = (TextView)customizedView.findViewById(R.id.dateText);
            if (titleText != null){
                titleText.setText(title);
            }
            if (dateText != null){
                dateText.setText(date);
            }

            return customizedView;

        }
    }
}
