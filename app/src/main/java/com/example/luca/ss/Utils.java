package com.example.luca.ss;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import static java.lang.Integer.toBinaryString;

public class Utils {
    public static String format(String s, boolean explicit){
        String returnValue;
        if (!explicit) {
            Double tot = 0.0;
            for (int j = 0; j < 4; j++) {
                Integer ihex = Integer.parseInt(s.substring(2 * j, 2 + 2 * j), 16);
                String lastBits = toBinaryString(0x100 | ihex).substring(7, 9);
                Integer lastbitsvalue = Integer.parseInt(lastBits, 2);
                String firstbits = toBinaryString(0x100 | ihex).substring(5, 7);
                Integer firstbitsvalue = Integer.parseInt(firstbits, 2);
                double lastvalue = lastbitsvalue * Math.pow(2, (6 - (j * 2)));
                double firstvalue = firstbitsvalue * Math.pow(2, (14 - (2 * j)));
                tot = tot + lastvalue + firstvalue;
            }
            tot = tot / 100;
            returnValue = "€"+tot.toString();
        }else {
            returnValue= s.substring(0,8)+"\n"+s.substring(8,16);
        }
        return returnValue;
    }

    public static void export(Context context){
        DatabaseHelper helper = new DatabaseHelper(context);
        ArrayList<Element> data = helper.getData();
        StringBuilder sb = new StringBuilder();

        sb.append(helper.size());
        sb.append(" ");
        sb.append(16);
        sb.append(" ");
        sb.append(8);
        sb.append("\n");
        for (Element item : data){
            StringBuilder inputs = new StringBuilder();
            StringBuilder outputs = new StringBuilder();
            for (int i=0;i<4;i++){
                Integer ihex = Integer.parseInt(item.getCode().substring(2*i,2+2*i),16);
                String s = Integer.toBinaryString(ihex);
                while (s.length()!=8){
                    s="0"+s;
                }
                for (int x=0;x<2;x++){
                    outputs.append(s.charAt(x)+" ");
                }
                for (int x=4;x<8;x++){
                    inputs.append(s.charAt(x)+" ");
                }
            }
            sb.append(inputs.toString());
            sb.append("\n");
            sb.append(outputs.toString());
            sb.append("\n");

        }

        String string = sb.toString().replace("0","-1");
        Log.d("OUTPUT", string);
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("dump file", string);
        clipboard.setPrimaryClip(clip);
    }
}
