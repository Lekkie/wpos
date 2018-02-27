package com.avantir.wpos.exception;

/**
 * Created by lekanomotayo on 12/02/2018.
 */
public class ConnectException extends Exception {

    public static final int RESULT_CONNECT_ERROR = 1;
    public static final int RESULT_SEND_ERROR = 2;
    public static final int RESULT_RECEIVE_IOEXCEPTION = 3;
    public static final int RESULT_RECEIVE_TIMEOUT = 4;
    public static final int RESULT_RECEIVE_INTERRUPT = 5;
    public static final int RESULT_DISCONNECT_ERROR = 6;
    private static final long serialVersionUID = -877134969485619651L;
    private int errorCode = 0;

    public ConnectException(int errorCode) {
        this.errorCode = errorCode;
    }

    public ConnectException(int errorCode, String detialMessage) {
        super(detialMessage);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}
