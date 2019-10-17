package com.example.luca.ss;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class InvioDati extends AsyncTask<String,Void,String> {

    private URL urlPagina;
    private Context context;
    private TaskCompleted callBack;

    public InvioDati(Context context){
        this.context=context;
        callBack = (TaskCompleted) context;
    }

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
        callBack.onTaskComplete(s);
    }

    private String serverRead(InputStream in){
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in));) {
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
