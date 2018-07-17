package com.avantir.wpos.network;

import android.content.Context;
import com.avantir.wpos.interfaces.*;
import com.avantir.wpos.interfaces.Connection;
import com.squareup.okhttp.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by lekanomotayo on 19/02/2018.
 */
public class HttpSocketConnection {

    private OkHttpClient httpclient = null;
    private static final MediaType MediaTypeJSON = MediaType.parse("application/json; charset=utf-8");
    private String receiveMsg = null;

    public void dataCommu(String url, int httpMethod, HashMap<String, String> headers, String body, int timeout, ICommsListener commsListener) {
        try {
            httpclient = new OkHttpClient();
            httpclient.setConnectTimeout(10, TimeUnit.SECONDS);
            httpclient.setWriteTimeout(5, TimeUnit.SECONDS);
            httpclient.setReadTimeout(timeout, TimeUnit.SECONDS);

            Request.Builder builder = new Request.Builder();
            if(headers != null && headers.size() > 0){
                Iterator it = headers.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> pair = (Map.Entry)it.next();
                    builder.addHeader(pair.getKey(), pair.getValue());
                }
            }

            Request request = null;
            if(httpMethod == 1){ // POST
                request = builder.url(url)
                        .post(RequestBody.create(MediaTypeJSON, body)).build();
            }
            else { //Default isGET
                request = builder.url(url)
                        .get()
                        .build();
            }


            Response response = httpclient.newCall(request).execute();

            //LogUtil.si(HttpPostCommu.class, "" + this.httpResponse.getStatusLine().getStatusCode());
           // if(response.code() == 200) {
                System.gc();
                InputStreamReader e = new InputStreamReader(response.body().byteStream());
                BufferedReader in = new BufferedReader(e);
                new StringBuffer();
                String temp = "";
                this.receiveMsg = "";

                while((temp = in.readLine()) != null) {
                    this.receiveMsg = this.receiveMsg + temp;
                    //LogUtil.si(HttpPostCommu.class, "The length of the string = " + temp.length());
                }

                in.close();
                e.close();
                response = null;
                request = null;
                System.gc();
                //LogUtil.si(HttpPostCommu.class, "result:" + this.receiveMsg);
                commsListener.OnStatus(4, this.receiveMsg.getBytes());
            //}
        } catch (Exception var7) {
            commsListener.OnError(-1, var7.getMessage());
        }
    }




    public String dataCommuBlocking(String url, int httpMethod, HashMap<String, String> headers, String body, int timeout) throws Exception {
        try {
            httpclient = new OkHttpClient();
            httpclient.setConnectTimeout(10, TimeUnit.SECONDS);
            httpclient.setWriteTimeout(5, TimeUnit.SECONDS);
            httpclient.setReadTimeout(timeout, TimeUnit.SECONDS);

            Request.Builder builder = new Request.Builder();
            if(headers != null && headers.size() > 0){
                Iterator it = headers.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> pair = (Map.Entry)it.next();
                    builder.addHeader(pair.getKey(), pair.getValue());
                }
            }

            Request request = null;
            if(httpMethod == 1){ // POST
                request = new Request.Builder().url(url)
                        .post(RequestBody.create(MediaTypeJSON, body)).build();
            }
            else { //Default isGET
                request = builder.url(url)
                        .get()
                        .build();
            }


            Response response = httpclient.newCall(request).execute();

            //LogUtil.si(HttpPostCommu.class, "" + this.httpResponse.getStatusLine().getStatusCode());
            //if(response.code() == 200) {
                System.gc();
                InputStreamReader e = new InputStreamReader(response.body().byteStream());
                BufferedReader in = new BufferedReader(e);
                new StringBuffer();
                String temp = "";
                this.receiveMsg = "";

                while((temp = in.readLine()) != null) {
                    this.receiveMsg = this.receiveMsg + temp;
                    //LogUtil.si(HttpPostCommu.class, "The length of the string = " + temp.length());
                }

                in.close();
                e.close();
                response = null;
                request = null;
                System.gc();
                //LogUtil.si(HttpPostCommu.class, "result:" + this.receiveMsg);
                return  this.receiveMsg;
            //}
        } catch (Exception var7) {
            throw var7;
        }
    }

}
