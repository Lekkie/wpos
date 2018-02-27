package com.avantir.wpos.listeners;

import android.os.Handler;
import android.os.RemoteException;
import com.avantir.wpos.interfaces.OnPinPadListener;
import com.avantir.wpos.model.TransInfo;
import com.avantir.wpos.services.EMVManager;
import wangpos.sdk4.emv.ICallbackListener;

/**
 * Created by lekanomotayo on 09/02/2018.
 */
public class PINPadListener implements OnPinPadListener {

    Handler handler;
    ICallbackListener iCallBackListener;
    String tradeType;
    TransInfo transInfo;

    public PINPadListener(Handler handler, ICallbackListener iCallBackListener, TransInfo transInfo, String tradeType){
        this.handler = handler;
        this.iCallBackListener = iCallBackListener;
        this.tradeType = tradeType;
        this.transInfo = transInfo;
    }

    @Override
    public void onUpDate() {
    }

    @Override
    public void onCancel() {
        //strTxt = "User canceled";
        //handler.sendEmptyMessage(0);
        handler.obtainMessage(0, "User canceled").sendToTarget();
    }

    @Override
    public void onByPass() {
        //strTxt = "ByPass";
        //handler.sendEmptyMessage(0);
        handler.obtainMessage(0, "ByPass").sendToTarget();
        if ("07".equals(tradeType)) {
            try {
                int result = EMVManager.EMV_TransProcess(transInfo, iCallBackListener);
                //Log.d(TAG, "result==" + result);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else {//MAG
            handler.sendEmptyMessage(2);
        }
    }

    @Override
    public void onSuccess(String pin) {
        //Log.d(TAG, "Pin==" + pin);
        if ("07".equals(tradeType)) {
            try {
                int result = EMVManager.EMV_TransProcess(transInfo, iCallBackListener);
                //Log.d(TAG, "result==" + result);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else {//MAG
            handler.sendEmptyMessage(2);
        }
    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        //Log.e(TAG, "errorCode==" + errorCode+"\nerrorMsg=="+errorMsg);
    }

}
