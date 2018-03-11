package com.avantir.wpos.listeners;

import android.os.Handler;
import android.os.RemoteException;
import com.avantir.wpos.dialog.KeyPadDialog;
import com.avantir.wpos.interfaces.OnPinPadListener;
import com.avantir.wpos.model.TransInfo;
import com.avantir.wpos.services.EMVManager;
import com.avantir.wpos.utils.ConstantUtils;
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
        //handler.obtainMessage(0, "User canceled").sendToTarget();
        handler.obtainMessage(ConstantUtils.MSG_ERROR, ConstantUtils.TRANSACTION_DECLINED).sendToTarget();
        handler.obtainMessage(ConstantUtils.MSG_INFO, "User canceled").sendToTarget();
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
                handler.obtainMessage(ConstantUtils.MSG_ERROR, ConstantUtils.TRANSACTION_DECLINED).sendToTarget();
                handler.obtainMessage(ConstantUtils.MSG_INFO, "").sendToTarget();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else {//MAG
            handler.obtainMessage(ConstantUtils.MSG_ERROR, ConstantUtils.TRANSACTION_DECLINED).sendToTarget();
            handler.obtainMessage(ConstantUtils.MSG_INFO, "").sendToTarget();
        }
    }

    @Override
    public void onSuccess(String pin) {
        //Log.d(TAG, "Pin==" + pin);
        if ("07".equals(tradeType)) {
            try {
                int result = EMVManager.EMV_TransProcess(transInfo, iCallBackListener);
                //Log.d(TAG, "result==" + result);
                if(result == -4){
                    // User canceled
                    handler.obtainMessage(ConstantUtils.MSG_ERROR, ConstantUtils.TRANSACTION_DECLINED).sendToTarget();
                    handler.obtainMessage(ConstantUtils.MSG_INFO, "User canceled").sendToTarget();
                }
                else if(result == 5){ // timeout
                    // Timeout, go to main menu or display error with decline or timeout msg?
                    KeyPadDialog.getInstance().dismissDialog();
                    handler.obtainMessage(ConstantUtils.MSG_ERROR, ConstantUtils.TRANSACTION_DECLINED).sendToTarget();
                    handler.obtainMessage(ConstantUtils.MSG_INFO, "Timed out").sendToTarget();
                }
                else{
                    KeyPadDialog.getInstance().dismissDialog();
                    handler.obtainMessage(ConstantUtils.MSG_ERROR, ConstantUtils.TRANSACTION_DECLINED).sendToTarget();
                    handler.obtainMessage(ConstantUtils.MSG_INFO, "Aborted").sendToTarget();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else {//MAG
            handler.obtainMessage(ConstantUtils.MSG_START_COMMS, pin).sendToTarget();
        }
    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        //Log.e(TAG, "errorCode==" + errorCode+"\nerrorMsg=="+errorMsg);
        if(errorCode == -4){
            // User canceled
            handler.obtainMessage(ConstantUtils.MSG_ERROR, ConstantUtils.TRANSACTION_DECLINED).sendToTarget();
            handler.obtainMessage(ConstantUtils.MSG_INFO, "User canceled").sendToTarget();
        }
        else if(errorCode == 5){ // timeout
            // Timeout, go to main menu or display error with decline or timeout msg?
            KeyPadDialog.getInstance().dismissDialog();
            handler.obtainMessage(ConstantUtils.MSG_ERROR, ConstantUtils.TRANSACTION_DECLINED).sendToTarget();
            handler.obtainMessage(ConstantUtils.MSG_INFO, "Timed out").sendToTarget();
        }
        else{
            KeyPadDialog.getInstance().dismissDialog();
            handler.obtainMessage(ConstantUtils.MSG_ERROR, ConstantUtils.TRANSACTION_DECLINED).sendToTarget();
            handler.obtainMessage(ConstantUtils.MSG_INFO, "Aborted").sendToTarget();
        }
    }

}
