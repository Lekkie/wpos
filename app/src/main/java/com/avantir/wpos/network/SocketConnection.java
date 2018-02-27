package com.avantir.wpos.network;

import android.content.Context;
import com.avantir.wpos.exception.ConnectException;
import com.avantir.wpos.interfaces.Connection;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lekanomotayo on 12/02/2018.
 */
public class SocketConnection implements Connection {

    private static final byte HEAD_BYTES = 2;
    private Socket socket;
    private String ip;
    private int port;
    private int timeout;
    private Timer timer = null;

    public SocketConnection() {
    }

    public void init(Context context, String host, int port, int timeout) {
            //LogUtil.si(SocketConnection.class, "Gets the data initialization communication parameter from XML.");
            //LogUtil.si(SocketConnection.class, "The communication parameters are initialized directly from the parameter structure.");
            //LogUtil.si(SocketConnection.class, "setCommuParams : type = " + params.getType() + " ip = " + params.getIp() + " port = " + params.getPort() + " timeout = " + params.getTimeout());
            this.ip = host;
            this.port = port;
            this.timeout = timeout;

    }

    public void connect() throws ConnectException {
        try {
            this.socket = new Socket();
            //DebugHelper.d("SocketConnection", "------ <connect> [" + this.ip + " " + this.port + "] timeout(" + this.timeout + "s)------");
            this.socket.connect(new InetSocketAddress(this.ip, this.port), this.timeout * 1000);
            //DebugHelper.d("SocketConnection", "------ <connect> [" + this.ip + " " + this.port + "] success");
        } catch (IOException var2) {
            throw new ConnectException(1, "Connection error.");
        }
    }

    public void send(byte[] data) throws ConnectException {
        try {
            OutputStream os = this.socket.getOutputStream();
            byte[] out;
            out = new byte[2 + data.length];
            System.arraycopy(new byte[]{(byte)(data.length >> 8 & 255), (byte)(data.length & 255)}, 0, out, 0, 2);
            System.arraycopy(data, 0, out, 2, data.length);
            this.startTimer(os);
            os.write(out);

            this.stopTimer();
            //DebugHelper.d("SocketConnection", "------ <send> " + out.length + "(bytes) ------");
            //DebugHelper.dumpHex("<send>", data);

        } catch (IOException var4) {
            throw new ConnectException(2, "Sending an error.");
        }
    }

    public byte[] receive() throws ConnectException {
        try {
            InputStream is = this.socket.getInputStream();
            int total1;
                byte[] len1 = new byte[50];
                long buffer1 = System.currentTimeMillis();

                while(is.available() < 2) {
                    if(this.isTimeout(buffer1, this.timeout * 1000)) {
                        throw new ConnectException(4, "Receiving timeout.");
                    }

                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException var9) {
                        throw new ConnectException(5, "Receiving interrupt.");
                    }
                }

                is.read(len1, 0, 2);
                int total = len1[0] << 8 & '\uff00' | len1[1] & 255;
                //DebugHelper.d("SocketConnection", "------ <recv> " + total + "(bytes) to receive ------");
                buffer1 = System.currentTimeMillis();

                while(is.available() < total) {
                    if(this.isTimeout(buffer1, this.timeout * 1000)) {
                        if(is.available() > 0) {
                            len1 = new byte[is.available()];
                            is.read(len1);
                        }

                        throw new ConnectException(4, "Receiving timeout.");
                    }

                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException var8) {
                        throw new ConnectException(5, "Receiving interrupt.");
                    }
                }

                len1 = new byte[total];
                total1 = is.read(len1);
                //DebugHelper.d("SocketConnection", "------ <recv> receive " + total1 + "(bytes) ------");
                //DebugHelper.dumpHex("<recv>", len1);
                return len1;

        } catch (IOException var12) {
            throw new ConnectException(3, "Receiving IO error.");
        }
    }

    public void disconnect() throws ConnectException {
        try {
            if(this.socket.isConnected()) {
                this.socket.close();
            }

            this.socket = null;
            //DebugHelper.d("SocketConnection", "------ <disconnect> [" + this.ip + " " + this.port + "] ------");
        } catch (Exception var2) {
            throw new ConnectException(6, "Closing the link error.");
        }
    }

    private boolean isTimeout(long beginTime, int timeout) {
        return System.currentTimeMillis() - beginTime > (long)timeout;
    }

    private void startTimer(final OutputStream os) {
        //LogUtil.si(SocketConnection.class, "-----startTimer-----");
        if(this.timer == null) {
            this.timer = new Timer();
            //LogUtil.si(SocketConnection.class, "-----timer非null-----");
            this.timer.schedule(new TimerTask() {
                public void run() {
                    try {
                        os.close();
                    } catch (IOException var2) {
                        var2.printStackTrace();
                    }

                    SocketConnection.this.stopTimer();
                }
            }, 5000L);
        }

    }

    private void stopTimer() {
        //LogUtil.si(SocketConnection.class, "-----stopTimer-----");
        if(this.timer != null) {
            //LogUtil.si(SocketConnection.class, "-----timer非null-----");
            this.timer.cancel();
            this.timer = null;
        }

    }
}
