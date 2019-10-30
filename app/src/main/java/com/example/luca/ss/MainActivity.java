package com.example.luca.ss;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity{

    Spinner spinner;
    TextView textView;
    String itemSelected;
    Button siButton;
    Switch appBarSwitch;
    boolean tVMode;
    String val,ip;
    SharedPreferences.Editor edit;
    SharedPreferences prefs;
    DatabaseHelper helper;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerAdapter;

//sono un cameriere figo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner= findViewById(R.id.spinner);
        textView = findViewById(R.id.textView);
        siButton =findViewById(R.id.button);
        recyclerView = findViewById(R.id.recyclerView);

        prefs = getSharedPreferences("prefs",0);
        edit = prefs.edit();

        ip= prefs.getString("ip","0.0.0.0");

        if (prefs.getBoolean("firstStart",true)){onFirstStart();}

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

        tVMode=false;

        spinner.setSelection(prefs.getInt("spinner",0));

        send("http://"+ip+"/");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);

        MenuItem db=menu.findItem(R.id.database_switch);
        if (prefs.getBoolean("dbOnStart",false)){
            helper.updateRecycler();
            db.setTitle("Nascondi Database");
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            db.setTitle("Mostra Database");
            recyclerView.setVisibility(View.INVISIBLE);
        }

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
            case R.id.database_switch:
                if (recyclerView.getVisibility()==View.VISIBLE){
                    recyclerView.setVisibility(View.INVISIBLE);
                    item.setTitle("Mostra database");
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerAdapter.setData(helper.getData());
                    helper.updateRecycler();
                    item.setTitle("Nascondi database");
                }
                break;
            case R.id.export_item:
                Utils.export(this);
                break;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
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
        new InvioDati().execute(url);
    }

    public String formatta(String s){
        boolean added = false;
        if (!s.equals( "FFFFFFFFFFFFFFFF"))
            helper.addData(new Element(s,Utils.format(s,false),prefs.getString("user","undefined")));
        return Utils.format(s,tVMode);
    }

    public void onTaskComplete(String result) {
        if (result == null){
        textView.setText("device not connected");

        }else {
            val=result;
            textView.setText(formatta(result));
            siButton.setEnabled(true);
        }
    }

    class InvioDati extends AsyncTask<String,Void,String> {

        private URL urlPagina;

        @Override
        protected String doInBackground(String... strings) {
            try {
                urlPagina = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection) urlPagina.openConnection();
                conn.setConnectTimeout(5000);

                InputStream risposta = conn.getInputStream();
                String dati = serverRead(risposta);
                return  dati;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            onTaskComplete(s);
        }

        private String serverRead(InputStream in){
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String nextLine = "";
                while ((nextLine = reader.readLine()) != null) {
                    sb.append(nextLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }
    }
}
