package com.avantir.wpos.services;

import android.content.Context;
import com.avantir.wpos.exception.ConnectException;
import com.avantir.wpos.interfaces.Connection;
import com.avantir.wpos.interfaces.ICommsListener;
import com.avantir.wpos.interfaces.SSLConnection;
import com.avantir.wpos.network.HttpSocketConnection;
import com.avantir.wpos.network.SSLSocketConnection;
import com.avantir.wpos.network.SocketConnection;

/**
 * Created by lekanomotayo on 12/02/2018.
 */
public class TcpComms {

    private static final String TAG = "basewin.sdk.commu";
    private static TcpComms instance = null;
    private ICommsListener commsListener = null;
    private Connection socket = null;
    private SSLConnection sslSocket = null;
    private Thread threadComm = null;
    private byte[] resp = null;
    String host;
    int port;
    int timeout;
    private boolean ifSSL = false;
    private String cer = null;
    private boolean ifContinueRecv = false;

    public TcpComms(String host, int port, int timeout, boolean ifSSL, String cer) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.ifSSL = ifSSL;
        this.cer = cer;
    }

    /*
    public static TcpComms getInstance(String host, int port, int timeout, boolean ifSSL, String cer) {
        if(instance == null) {
            instance = new TcpComms(host, port, timeout, ifSSL, cer);
        }
        return instance;
    }
    */



    public synchronized byte[] dataCommuBlocking(Context context, byte[] senddata) throws Exception{
        if((this.host == null || this.host.isEmpty()) && this.port == 0 && this.timeout == 0 && context == null) {
            //LogUtil.se(this.getClass(), "not set the callback listener or there is no way to get the communication parameters,can\'t continue.");
        } else {
            //LogUtil.si(Commu.class, "the parameter not null");
            //LogUtil.si(Commu.class, "commuType = " + this.commuType);
            //LogUtil.si(Commu.class, "ifSSL = " + this.ifSSL);
            if(!this.ifSSL) {
                //LogUtil.si(Comms.class, "The socket communication");
                this.socket = new SocketConnection();

                try {
                    this.notifyStatus(0, (byte[])null);
                    this.socket.init(context, host, port, timeout);
                } catch (Exception var6) {
                    this.notifyErrorCode(2, var6.getMessage());
                    return null;
                }

                this.dataCommuSocketBlocking(senddata);
                return resp;
            } else {
                //LogUtil.si(Commu.class, "SSL socket communication");
                sslSocket = new SSLSocketConnection();

                try {
                    this.notifyStatus(0, (byte[])null);
                    sslSocket.init(context, host, port, timeout, cer);
                } catch (Exception var5) {
                    this.notifyErrorCode(2, var5.getMessage());
                    return null;
                }

                this.dataCommuSocketSSLBlocking(senddata);
                return resp;
            }
        }
        return null;
    }


    public synchronized void dataCommu(Context context, byte[] senddata, ICommsListener commsListener) {
        if(commsListener == null && (this.host == null || this.host.isEmpty()) && this.port == 0 && this.timeout == 0 && context == null) {
            //LogUtil.se(this.getClass(), "not set the callback listener or there is no way to get the communication parameters,can\'t continue.");
        } else {
            this.commsListener = commsListener;
            if(this.threadComm != null) {
                this.threadComm.isAlive();
            }
            //LogUtil.si(Commu.class, "the parameter not null");
            //LogUtil.si(Commu.class, "commuType = " + this.commuType);
            //LogUtil.si(Commu.class, "ifSSL = " + this.ifSSL);
            if(!this.ifSSL) {
                //LogUtil.si(Comms.class, "The socket communication");
                this.socket = new SocketConnection();

                try {
                    this.notifyStatus(0, (byte[])null);
                    this.socket.init(context, host, port, timeout);
                } catch (Exception var6) {
                    this.notifyErrorCode(2, var6.getMessage());
                }

                this.dataCommuSocket(senddata);
            } else {
                //LogUtil.si(Commu.class, "SSL socket communication");
                sslSocket = new SSLSocketConnection();

                try {
                    this.notifyStatus(0, (byte[])null);
                    sslSocket.init(context, host, port, timeout, cer);
                } catch (Exception var5) {
                    this.notifyErrorCode(2, var5.getMessage());
                }

                this.dataCommuSocketSSL(senddata);
            }

        }
    }

    private void dataCommuSocketBlocking(final byte[] senddata) throws Exception{
        try {
            notifyStatus(1, (byte[])null);
            socket.connect();
            notifyStatus(2, (byte[])null);
            socket.send(senddata);
            notifyStatus(3, (byte[])null);
            recv();
        } catch (ConnectException var10) {
            throw var10;
        } finally {
            try {
                socket.disconnect();
            } catch (ConnectException var9) {
                throw var9;
            }
        }
    }

    private void dataCommuSocket(final byte[] senddata) {
        this.threadComm = new Thread(new Runnable() {
            public void run() {
                try {
                    notifyStatus(1, (byte[])null);
                    socket.connect();
                    notifyStatus(2, (byte[])null);
                    socket.send(senddata);
                    notifyStatus(3, (byte[])null);
                    recv();
                } catch (ConnectException var10) {
                    notifyErrorCode(var10.getErrorCode(), var10.getMessage());
                } catch (Exception var10) {
                    notifyErrorCode(3, var10.getMessage());
                } finally {
                    try {
                        socket.disconnect();
                    } catch (ConnectException var9) {
                        notifyErrorCode(var9.getErrorCode(), var9.getMessage());
                    }

                }
            }
        });
        this.threadComm.start();
    }

    private void recv() throws ConnectException {
        this.resp = this.socket.receive();
        this.notifyStatus(4, this.resp);
        if(this.ifContinueRecv) {
            this.ifContinueRecv = false;
            this.recv();
        }
    }

    private void dataCommuSocketSSLBlocking(final byte[] senddata) throws Exception{
        try {
            sslSocket.connect();
            sslSocket.send(senddata);
            recvSSL();
        } catch (ConnectException var10) {
            throw var10;
        } finally {
            try {
                sslSocket.disconnect();
            } catch (ConnectException var9) {
                throw var9;
            }

        }
    }


    private void dataCommuSocketSSL(final byte[] senddata) {
        this.threadComm = new Thread(new Runnable() {
            public void run() {
                try {
                    notifyStatus(1, (byte[])null);
                    sslSocket.connect();
                    notifyStatus(2, (byte[])null);
                    sslSocket.send(senddata);
                    notifyStatus(3, (byte[])null);
                    recvSSL();
                } catch (ConnectException var10) {
                    TcpComms.this.notifyErrorCode(var10.getErrorCode(), var10.getMessage());
                } finally {
                    try {
                        sslSocket.disconnect();
                    } catch (ConnectException var9) {
                        notifyErrorCode(var9.getErrorCode(), var9.getMessage());
                    }

                }
            }
        });
        this.threadComm.start();
    }

    private void recvSSL() throws ConnectException {
        resp = this.sslSocket.receive();
        notifyStatus(4, this.resp);
        if(this.ifContinueRecv) {
            this.ifContinueRecv = false;
            recvSSL();
        }

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
}
