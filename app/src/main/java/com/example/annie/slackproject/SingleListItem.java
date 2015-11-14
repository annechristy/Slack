package com.example.annie.slackproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SingleListItem extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.single_list_item);

        System.out.println("SINGLE LIST ITEM CLASS ACCESSED");

        TextView txtProfile = (TextView) findViewById(R.id.profile);

        Intent i = getIntent();
        // getting attached intent data
        String testString = i.getStringExtra("profile");
        // displaying selected product name
        txtProfile.setText(testString);

    }
}
