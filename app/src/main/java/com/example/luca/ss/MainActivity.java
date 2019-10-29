package com.example.luca.ss;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements TaskCompleted{

    Spinner spinner;
    TextView textView;
    String itemSelected;
    Button siButton, setIpButton;
    Switch appBarSwitch;
    EditText ipEditText;
    boolean tVMode;
    String val,ip;
    SharedPreferences.Editor edit;
    SharedPreferences prefs;
    DatabaseHelper helper;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerAdapter;
    boolean firstStart;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner= findViewById(R.id.spinner);
        textView = findViewById(R.id.textView);
        siButton =findViewById(R.id.button);
        setIpButton = findViewById(R.id.set_ip);
        ipEditText = findViewById(R.id.ip_editText);
        recyclerView = findViewById(R.id.recyclerView);

        prefs = getSharedPreferences("prefs",0);
        edit = prefs.edit();

        ip= prefs.getString("ip","0.0.0.0");
        firstStart=prefs.getBoolean("firstStart",true);

        if (firstStart){onFirstStart();}

        helper = new DatabaseHelper(this);
        recyclerAdapter = new RecyclerViewAdapter(helper.getData());
        recyclerView.setAdapter(recyclerAdapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.spinner_items,R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemSelected = parent.getItemAtPosition(position).toString();
                edit.putInt("spinner",position);
                edit.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        siButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send("http://"+ip+"/W="+itemSelected);
            }
        });
        setIpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIp(v);
            }
        });
        tVMode=false;

        spinner.setSelection(prefs.getInt("spinner",0));

        send("http://"+ip+"/");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.app_bar_switch);
        appBarSwitch = item.getActionView().findViewById(R.id.my_switch);
        appBarSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tVMode=isChecked;
                onTaskComplete(val);
            }
        });
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh:
                send("http://"+ip+"/");
                break;
            case R.id.about:
                startActivity(new Intent(this,AboutActvity.class));
                break;
            case R.id.set_ip:
                ipEditText.setText(prefs.getString("ip",""));
                ipEditText.setVisibility(View.VISIBLE);
                setIpButton.setVisibility(View.VISIBLE);
                break;
            case R.id.database_switch:
                if (recyclerView.getVisibility()==View.VISIBLE){
                    recyclerView.setVisibility(View.INVISIBLE);
                    item.setTitle("show database");
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerAdapter.setData(helper.getData());
                    item.setTitle("hide database");
                }
                break;
            case R.id.export_item:
                Utils.export(this);
        }
        return true;
    }

    public void onFirstStart(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.first_start_prompt, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.first_start_edittext);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("user",userInput.getText().toString());
                                editor.apply();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart",false);
        editor.apply();
    }

    public void send(String url){
        textView.setText("connecting...");
        siButton.setEnabled(false);
        new InvioDati(this).execute(url);
    }

    public void setIp(View view){
        ip=ipEditText.getText().toString();
        edit.putString("ip",ip);
        edit.apply();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);

        setIpButton.setVisibility(View.INVISIBLE);
        ipEditText.setVisibility(View.INVISIBLE);
        Toast.makeText(this,"new ip: "+ip,Toast.LENGTH_LONG).show();
    }

    public String formatta(String s){
        boolean added = false;
        if (!s.equals( "FFFFFFFFFFFFFFFF"))
            helper.AddData(new Element(s,Utils.format(s,false)));
        return Utils.format(s,tVMode);
    }

    @Override
    public void onTaskComplete(String result) {
        if (result == null){
        textView.setText("device not connected");

        }else {
            Log.d("PROVA", result);

            val=result;
            textView.setText(formatta(result));
            siButton.setEnabled(true);
        }
    }
}
