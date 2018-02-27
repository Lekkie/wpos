package com.avantir.wpos.interfaces;

import android.content.Context;
import com.avantir.wpos.exception.ConnectException;

/**
 * Created by lekanomotayo on 12/02/2018.
 */
public interface Connection {

    void init(Context var1, String host, int port, int timeout) ;//throws Exception;

    void connect() throws ConnectException;

    void send(byte[] var1) throws ConnectException;

    byte[] receive() throws ConnectException;

    void disconnect() throws ConnectException;

}
