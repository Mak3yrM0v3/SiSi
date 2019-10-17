package com.example.luca.ss;

import java.sql.ResultSet;
import java.sql.Statement;

public interface DatabaseConnected {
    public void onConnection(ResultSet set);
}
