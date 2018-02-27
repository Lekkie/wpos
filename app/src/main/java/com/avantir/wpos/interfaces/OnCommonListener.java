package com.avantir.wpos.interfaces;

/**
 * Created by Tommy on 2015/12/3.
 */
public interface OnCommonListener {

    void onProgress(String progress);

    void onError(int errorCode, String errorMsg);
}
