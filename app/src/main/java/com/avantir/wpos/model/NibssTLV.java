package com.avantir.wpos.model;

/**
 * Created by lekanomotayo on 21/02/2018.
 */
public class NibssTLV {

    String tag;
    int len;
    String value;

    public NibssTLV(String tag, int len, String value){
        this.tag = tag;
        this.len = len;
        this.value = value;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
