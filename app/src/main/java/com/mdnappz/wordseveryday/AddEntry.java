package com.mdnappz.wordseveryday;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddEntry extends AppCompatActivity {

    public static final int ENTRY_RESULT = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        Button saveButton = (Button)findViewById(R.id.saveButton);

        Bundle extras = getIntent().getExtras();
        final String actualKey = extras.getString("thisEntryKey");
        String actualEntry = extras.getString("actualEntry");

        EditText entryText = (EditText)findViewById(R.id.editedText);
        entryText.setText(actualEntry);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText entryText = (EditText)findViewById(R.id.editedText);
                Intent rIntent = new Intent();
                rIntent.putExtra("thisEntryReturn",entryText.getText().toString());
                rIntent.putExtra("thisEntryKeyReturn", actualKey);
                setResult(1, rIntent);
                finish();
            }
        });
    }
}
