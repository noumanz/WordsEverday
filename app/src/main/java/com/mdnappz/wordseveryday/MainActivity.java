package com.mdnappz.wordseveryday;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ListView startingList;
    private ArrayList<String> dates;
    private ArrayAdapter startingArrayAdapter;
    public HashMap<String, String> actualEntries;
    public static final int ENTRY_MODIFY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dates = new ArrayList<String>();
        actualEntries = new HashMap<String, String>();
        try {
            FileInputStream fis = openFileInput("entries.json");
            int read = -1;
            StringBuffer buffer = new StringBuffer();
            while ((read = fis.read()) != -1) {
                buffer.append((char) read);
            }
            String jsonString = buffer.toString();
            if (!jsonString.equals("")) {
                JSONObject data = new JSONObject(jsonString);
                Iterator<String> iter = data.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    dates.add(key);
                    actualEntries.put(key, data.getString(key));
                }
            }
            fis.close();
        } catch (FileNotFoundException fileError) {
            Toast.makeText(getApplicationContext(), "Creating New Diary", Toast.LENGTH_SHORT).show();
            writeJSON(actualEntries, "entries.json");
        } catch (IOException ioError) {
            Toast.makeText(getApplicationContext(), "IO Error", Toast.LENGTH_SHORT).show();
        } catch (JSONException jsonError) {
            Toast.makeText(getApplicationContext(), "JSON Error", Toast.LENGTH_SHORT).show();
            System.out.println("CRAPPY JSON ERROR");
            System.out.println(jsonError.toString());
        }

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
                intent.putExtra("thisEntryKey", dates.get(i));
                intent.putExtra("actualEntry", actualEntries.get(dates.get(i)));
                startActivityForResult(intent, ENTRY_MODIFY);
            }
        });

        addDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                int i = 2;
                String newDate = date;
                while (dates.contains(newDate)) {
                    newDate = date + " - " + i;
                    i++;
                }
                dates.add(newDate);
                actualEntries.put(newDate, "Nothing for now");
                startingArrayAdapter = new CustomAdapter(getApplicationContext(), R.layout.row , dates);

                writeJSON(actualEntries, "entries.json");

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
            actualEntries.put(key, entry);
            writeJSON(actualEntries, "entries.json");
            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
        }
    }

    protected void writeJSON(HashMap toWrite, String fileName){
        try {
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            JSONObject actualWrite = new JSONObject(toWrite);
            fos.write(actualWrite.toString().getBytes());
            fos.close();
        } catch (FileNotFoundException fileError) {
            Toast.makeText(getApplicationContext(), "File Creation Error", Toast.LENGTH_LONG).show();
        } catch (IOException e){
            Toast.makeText(getApplicationContext(), "Writing Error", Toast.LENGTH_LONG).show();
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

            String date = dates.get(position).split(" - ")[0];
            String title = dates.get(position).toUpperCase();

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
