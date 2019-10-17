package com.example.luca.ss;

import android.support.annotation.Nullable;

public class Element {
    private String code;
    private String value;

    public Element(String code,String value){
        this.code = code;
        this.value=value;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Element){
            Element item = (Element) obj;
            if (item.getCode().equals(getCode())){
                return true;
            }
        }
        return false;
    }

    public String getCode() {
        return code;
    }
    public String getValue() {
        return value;
    }
}
