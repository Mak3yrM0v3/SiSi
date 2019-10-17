package com.example.luca.ss;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActvity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        DatabaseHelper helper = new DatabaseHelper(this);

        TextView databaseSize = findViewById(R.id.database_size_textview);
        String s = Integer.toString(helper.size());
        databaseSize.setText(s);

        SharedPreferences prefs = getSharedPreferences("prefs",0);

        TextView user = findViewById(R.id.textView_user);
        user.setText("user: "+prefs.getString("user","undefined"));
    }
}
