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
        String actualTitle = extras.getString("thisEntryKey");
        String actualEntry = extras.getString("actualEntry");
        final int entryId = extras.getInt("thisEntryID");

        EditText entryText = (EditText)findViewById(R.id.editedText);
        entryText.setText(actualEntry);
        EditText titleText = (EditText)findViewById(R.id.titleText);
        titleText.setText(actualTitle);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText entryText = (EditText)findViewById(R.id.editedText);
                EditText titleText = (EditText)findViewById(R.id.titleText);
                Intent rIntent = new Intent();
                rIntent.putExtra("thisEntryReturn", entryText.getText().toString());
                rIntent.putExtra("thisEntryKeyReturn", titleText.getText().toString());
                rIntent.putExtra("thisEntryID", entryId);
                setResult(1, rIntent);
                finish();
            }
        });
    }
}
