package com.avantir.wpos.interfaces;

/**
 * Created by lekanomotayo on 12/02/2018.
 */
public interface ICommsListener {

    void OnStatus(int var1, byte[] var2);

    void OnError(int var1, String var2);
}
