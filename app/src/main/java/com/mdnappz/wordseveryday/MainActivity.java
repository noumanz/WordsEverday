package com.mdnappz.wordseveryday;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DrawableUtils;
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
        dates = new ArrayList<>();
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
            Toast.makeText(getApplicationContext(), "Creating Error?", Toast.LENGTH_SHORT).show();
            try {
                FileOutputStream fos = openFileOutput("entries.json", Context.MODE_PRIVATE);
            } catch (FileNotFoundException fileError2) {
                Toast.makeText(getApplicationContext(), "Creating Error... ABORT", Toast.LENGTH_LONG).show();
            }
        } catch (IOException ioError) {
            Toast.makeText(getApplicationContext(), "IO Error", Toast.LENGTH_SHORT).show();
        } catch (JSONException jsonError) {
            Toast.makeText(getApplicationContext(), "JSON Error", Toast.LENGTH_SHORT).show();
            System.out.println("CRAPPY JSON ERROR");
            System.out.println(jsonError.toString());
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            String key = extras.getString("thisEntryKey");
            String entry = extras.getString("thisEntry");
            actualEntries.put(key, entry);
        }

        Button addDate = (Button) findViewById(R.id.addDate);
        startingList = (ListView) findViewById(R.id.startingList);
        startingArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dates)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundResource(R.drawable.dark_gradient);
                return view;
            }
        };

        if (startingList != null) {
            startingList.setAdapter(startingArrayAdapter);
        }

        startingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), "You Touched " + dates.get(i), Toast.LENGTH_SHORT).show();
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
                startingArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.row, dates)
                {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent){
                        View view = super.getView(position, convertView, parent);
                        TextView tv = (TextView) view.findViewById(android.R.id.text1);
                        tv.setTextColor(Color.WHITE);
                        tv.setBackgroundResource(R.drawable.dark_gradient);
                        return view;
                    }
                };

                if (startingList != null) {
                    startingList.setAdapter(startingArrayAdapter);
                }
            }
        });


    }
}
