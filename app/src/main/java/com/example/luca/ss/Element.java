package com.example.luca.ss;

import android.support.annotation.Nullable;

public class Element {
    private String code;
    private String value;
    private String user;

    public Element(String code,String value){
        this.code = code;
        this.value=value;
        this.user=null;
    }
    public Element(String code){
        this.code = code;
        this.value=null;
        this.user=null;
    }
    public Element(String code,String value,String user){
        this.code = code;
        this.value=value;
        this.user=user;
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
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
