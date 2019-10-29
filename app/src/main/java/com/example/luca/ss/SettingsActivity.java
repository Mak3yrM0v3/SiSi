package com.example.luca.ss;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    EditText ipEditText, userEditText, codeEdit, valueEdit;
    Button ipButton, userButton, addButton;
    Switch dbSwitch;
    SharedPreferences prefs;
    SharedPreferences.Editor edit;
    DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ipEditText=findViewById(R.id.ipSetting);
        userEditText=findViewById(R.id.userSettings);
        ipButton=findViewById(R.id.setIp);
        userButton=findViewById(R.id.setUser);
        dbSwitch=findViewById(R.id.dbSwitch);
        valueEdit=findViewById(R.id.newValue);
        codeEdit=findViewById(R.id.newCode);
        addButton=findViewById(R.id.addButton);

        prefs=getSharedPreferences("prefs",0);

        helper = new DatabaseHelper(this);

        init();
    }

    private void init(){
        ipEditText.setText(prefs.getString("ip","0.0.0.0"));
        userEditText.setText(prefs.getString("user","undefined"));
        dbSwitch.setChecked(prefs.getBoolean("dbOnStart",false));

        ipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setIp(view);
            }
        });
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUser(view);
            }
        });
        dbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                edit=prefs.edit();
                edit.putBoolean("dbOnStart",b);
                edit.apply();
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });
    }

    public void add(){
        Element element = new Element(codeEdit.getText().toString(),
                    valueEdit.getText().toString(),
                    prefs.getString("user", "undefined"));
        helper.addData(element);
    }

    public void setIp(View view){
        edit=prefs.edit();
        String ip=ipEditText.getText().toString();
        edit.putString("ip",ip);
        edit.apply();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        Toast.makeText(this,"new ip: "+ip,Toast.LENGTH_LONG).show();
    }

    public void setUser(View view){
        edit=prefs.edit();
        String user=userEditText.getText().toString();
        edit.putString("user",user);
        edit.apply();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        Toast.makeText(this,"new user: "+user,Toast.LENGTH_LONG).show();
    }
}
