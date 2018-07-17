package com.avantir.wpos.services;

import android.content.Context;
import com.avantir.wpos.exception.ConnectException;
import com.avantir.wpos.interfaces.Connection;
import com.avantir.wpos.interfaces.ICommsListener;
import com.avantir.wpos.interfaces.SSLConnection;
import com.avantir.wpos.network.HttpSocketConnection;
import com.avantir.wpos.network.SSLSocketConnection;
import com.avantir.wpos.network.SocketConnection;

import java.util.HashMap;

/**
 * Created by lekanomotayo on 12/02/2018.
 */
public class HttpComms {

    private static final String TAG = "basewin.sdk.commu";
    private static HttpComms instance = null;
    private ICommsListener commsListener = null;
    private HttpSocketConnection httpSocketConnection = null;
    private Thread threadComm = null;
    private byte[] resp = null;
    String host;
    int port;
    int timeout;
    private boolean ifSSL = false;
    private String cer = null;
    private boolean ifContinueRecv = false;

    public HttpComms(String host, int port, int timeout, boolean ifSSL, String cer) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.ifSSL = ifSSL;
        this.cer = cer;
    }

    public static HttpComms getInstance(String host, int port, int timeout, boolean ifSSL, String cer) {
        if(instance == null) {
            instance = new HttpComms(host, port, timeout, ifSSL, cer);
        }
        return instance;
    }



    public synchronized byte[] dataCommuBlocking(Context context, String uri, int httpMethod, HashMap headers, String senddata) throws Exception{
        if((this.host == null || this.host.isEmpty())  && this.port == 0&& this.timeout == 0 && context == null) {
            //LogUtil.se(this.getClass(), "not set the callback listener or there is no way to get the communication parameters,can\'t continue.");
        } else {
            //LogUtil.si(Commu.class, "the parameter not null");
            //LogUtil.si(Commu.class, "commuType = " + this.commuType);
            //LogUtil.si(Commu.class, "ifSSL = " + this.ifSSL);

            this.httpSocketConnection = new HttpSocketConnection();
            if(uri == null) {
                this.notifyErrorCode(2, "The URL or http parameter hasn\'t been set. ");
                return null;
            }
            return this.dataCommuHttpBlocking(uri, httpMethod, headers, senddata).getBytes();
        }
        return null;
    }


    public synchronized void dataCommu(Context context, String uri, int httpMethod, HashMap headers, String senddata, ICommsListener commsListener) {
        if((this.host == null || this.host.isEmpty())  && this.port == 0&& this.timeout == 0 && context == null) {
            //LogUtil.se(this.getClass(), "not set the callback listener or there is no way to get the communication parameters,can\'t continue.");
        } else {
            this.commsListener = commsListener;
            if(this.threadComm != null) {
                this.threadComm.isAlive();
            }
            //LogUtil.si(Commu.class, "the parameter not null");
            //LogUtil.si(Commu.class, "commuType = " + this.commuType);
            //LogUtil.si(Commu.class, "ifSSL = " + this.ifSSL);
            this.httpSocketConnection = new HttpSocketConnection();
            if(uri == null) {
                this.notifyErrorCode(2, "The URL or http parameter hasn\'t been set. ");
                return;
            }
            this.dataCommuHttp(uri, httpMethod, headers, senddata);
        }
    }

    private void dataCommuHttp(final String uri, final int httpMethod, final HashMap headers, final String body) {
        this.threadComm = new Thread(new Runnable() {
            public void run() {
                String scheme = ifSSL ? "https" : "http";
                String url = scheme + "://" + host + ":" + port + uri;
                httpSocketConnection.dataCommu(url, httpMethod, headers, body, timeout, commsListener);
            }
        });
        this.threadComm.start();
    }

    private String dataCommuHttpBlocking(String uri, int httpMethod, HashMap headers, String body) throws Exception{
        String scheme = ifSSL ? "https" : "http";
        String url = scheme + "://" + host + ":" + port + uri;
        return httpSocketConnection.dataCommuBlocking(url, httpMethod, headers, body, timeout);
    }

    private synchronized void notifyStatus(int code, byte[] data) {
        if(this.commsListener != null) {
            this.commsListener.OnStatus(code, data);
        }

    }

    private synchronized void notifyErrorCode(int code, String msg) {
        if(this.commsListener != null) {
            this.commsListener.OnError(code, msg);
        }

    }

    public void forceStopThread() {
        if(this.threadComm != null && this.threadComm.isAlive()) {
            this.threadComm.stop();
            this.threadComm = null;
        }

    }

    public void continueRecv() {
        this.ifContinueRecv = true;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isIfSSL() {
        return ifSSL;
    }

    public void setIfSSL(boolean ifSSL) {
        this.ifSSL = ifSSL;
    }

    public String getCer() {
        return cer;
    }

    public void setCer(String cer) {
        this.cer = cer;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
