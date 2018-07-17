package com.avantir.wpos.model;

/**
 * Created by lekanomotayo on 09/04/2018.
 */
public class RestResponse {

    protected String code;
    protected String message;

    // get error object

    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
