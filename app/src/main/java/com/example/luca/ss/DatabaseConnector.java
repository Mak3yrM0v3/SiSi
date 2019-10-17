package com.example.luca.ss;

import android.content.Context;
import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnector extends AsyncTask<String, Void, ResultSet> {

    private DatabaseConnected dbc;

    public DatabaseConnector(DatabaseHelper helper){
        dbc = helper;
    }

    @Override
    protected ResultSet doInBackground(String ... strings) {
        String myDriver = "com.mysql.jdbc.Driver";
        String myUrl = "jdbc:mysql://90.147.42.36:3306/BD105129";
        String query = strings[0];
        ResultSet set = null;

            try {
                Class.forName(myDriver);
                Connection conn = DriverManager.getConnection(myUrl, "105129", "lucasisi99");
                Statement st = conn.createStatement();
                if (strings[1].equals("get")) {
                    set = st.executeQuery(query);
                } else if (strings[1].equals("add")) {
                    st.execute(query);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        return set;
    }

    @Override
    protected void onPostExecute(ResultSet set) {
        dbc.onConnection(set);
    }
}