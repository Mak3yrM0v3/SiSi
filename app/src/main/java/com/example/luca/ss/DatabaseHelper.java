package com.example.luca.ss;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseHelper implements  DatabaseConnected{

    private static final String TABLE_NAME = "wallet";
    private static final String COL2 = "code";
    private static final String COL3 = "value";
    private static final String COL4 = "user";
    private static String USER;
    private static ResultSet set;

    public DatabaseHelper(Context context) {
        new DatabaseConnector(this).execute("SELECT "+COL2+",value FROM "+TABLE_NAME,"get");
        SharedPreferences prefs = context.getSharedPreferences("prefs",0);
        USER = prefs.getString("user","undefined");
    }

    public boolean AddData(Element element) {
        if (!exists(element)) {
            String query = "INSERT INTO "+TABLE_NAME+" (`"+COL2+"`,`"+COL3+"`,`"+COL4+"`) VALUES ('"+element.getCode()+"', '"+element.getValue()+"', '"+USER+"')";

            new DatabaseConnector(this).execute(query,"add");
            return true;
        }
        return false;
    }

    public ArrayList<Element> getData(){
        String query = "SELECT "+COL2+","+COL3+" FROM "+TABLE_NAME;
        ArrayList<Element> array = new ArrayList<>();
        new DatabaseConnector(this).execute(query,"get");
        try {
            while (set.next()) {
                Element temp = new Element(set.getString(COL2), set.getString(COL3));
                array.add(temp);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return array;
    }

    public int size(){
        return getData().size();
    }

    private Boolean exists(Element element) {
        for (Element item : getData()){
            if (item.equals(element)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onConnection(ResultSet set) {
        this.set = set;
    }
}
