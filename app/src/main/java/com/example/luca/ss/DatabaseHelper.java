package com.example.luca.ss;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseHelper{

    private static final String TABLE_NAME = "wallet";
    private static final String COL1 = "code";
    private static final String COL2 = "value";
    private static final String COL3 = "user";
    private static String USER;
    private static ResultSet set;
    private static final String myDriver = "com.mysql.jdbc.Driver";
    private static final String myUrl = "jdbc:mysql://90.147.42.36:3306/BD105129";
    private Context context;
    private static int size;

    public DatabaseHelper(Context context) {
        new AsyncGet().execute();
        new AsyncSize().execute();
        SharedPreferences prefs = context.getSharedPreferences("prefs",0);
        USER = prefs.getString("user","undefined");
        this.context=context;
    }

    public void addData(@NonNull Element element) {
        String query = "INSERT INTO "+TABLE_NAME+" (`"+ COL1 +"`,`"+ COL2 +"`,`"+ COL3 +"`) VALUES ('"+element.getCode()+"', '"+element.getValue()+"', '"+USER+"')";
        new AsyncAdd().execute(query);
    }

    public ArrayList<Element> getData(){
        ArrayList<Element> array = new ArrayList<>();
        try {
            while (set.next()) {
                Element temp = new Element(set.getString(COL1), set.getString(COL2), set.getString(COL3));
                array.add(temp);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return array;
    }

    public void updateRecycler(){
        new AsyncUpdateRecycler().execute();
    }

    public int size(){
        new AsyncSize().execute();
        return size;
    }

    private void displayMessage(String s){
        Toast.makeText(context,s,Toast.LENGTH_LONG).show();
    }



    static class AsyncGet extends AsyncTask<Void, Void, ResultSet> {

        @Override
        protected ResultSet doInBackground(Void ... strings) {
            String query = "SELECT "+ COL1 +","+ COL2 +", "+COL3+" FROM "+TABLE_NAME;
            ResultSet set = null;

            try {
                Class.forName(myDriver);
                Connection conn = DriverManager.getConnection(myUrl, "105129", "lucasisi99");
                Statement st = conn.createStatement();
                set = st.executeQuery(query);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return set;

        }

        @Override
        protected void onPostExecute(ResultSet set) {
            DatabaseHelper.set = set;
        }
    }
    class AsyncAdd extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String ... strings) {
            String query = strings[0];
            try {
                Class.forName(myDriver);
                Connection conn = DriverManager.getConnection(myUrl, "105129", "lucasisi99");
                Statement st = conn.createStatement();
                st.execute(query);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean set) {
            if (set){
                displayMessage("Aggiunto al database");
            } else {
                displayMessage("Non aggiunto al database");
            }
        }
    }
    class AsyncSize extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void ... voids) {
            String query = "SELECT count(*) as Count from "+TABLE_NAME;
            int count = -1;
            try {
                Class.forName(myDriver);
                Connection conn = DriverManager.getConnection(myUrl, "105129", "lucasisi99");
                Statement st = conn.createStatement();
                ResultSet set = st.executeQuery(query);
                set.next();
                count= set.getInt("Count");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return count;
        }

        @Override
        protected void onPostExecute(Integer set) {
            size=set;
        }
    }
    class AsyncUpdateRecycler extends AsyncTask<Void, Void, ResultSet> {

        @Override
        protected ResultSet doInBackground(Void ... strings) {
            String query = "SELECT "+ COL1 +","+ COL2 +", "+COL3+" FROM "+TABLE_NAME;
            ResultSet set = null;

            try {
                Class.forName(myDriver);
                Connection conn = DriverManager.getConnection(myUrl, "105129", "lucasisi99");
                Statement st = conn.createStatement();
                set = st.executeQuery(query);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return set;

        }

        @Override
        protected void onPostExecute(ResultSet set) {
            DatabaseHelper.set = set;
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.recyclerAdapter.setData(getData());
        }
    }

    public static void setUSER(String USER) {
        DatabaseHelper.USER = USER;
    }
}
