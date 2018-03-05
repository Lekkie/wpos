package com.avantir.wpos.listeners;

import android.os.Handler;
import com.avantir.wpos.activity.BaseActivity;
import com.avantir.wpos.interfaces.OnPinPadListener;
import com.avantir.wpos.utils.ConstantUtils;
import wangpos.sdk4.emv.ICallbackListener;

import java.util.concurrent.CountDownLatch;

/**
 * Created by lekanomotayo on 09/02/2018.
 */
public class EMVPINPadListener implements OnPinPadListener {

    Handler handler;
    //ICallbackListener iCallBackListener;
    //String tradeType;
    private CountDownLatch countDownLatch;

    public EMVPINPadListener(Handler handler, CountDownLatch countDownLatch){
        this.handler = handler;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void onUpDate() {
        countDownLatch.countDown();
    }

    @Override
    public void onCancel() {
        countDownLatch.countDown();
        //strTxt = "User canceled";
        //baseHandler.sendEmptyMessage(0);
        handler.obtainMessage(ConstantUtils.MSG_PROGRESS, "User canceled").sendToTarget();
    }

    @Override
    public void onByPass() {
        //strTxt = "ByPass";
        //baseHandler.sendEmptyMessage(0);
        handler.obtainMessage(ConstantUtils.MSG_PROGRESS, "ByPass").sendToTarget();
    }

    @Override
    public void onSuccess(String pin) {
        countDownLatch.countDown();
        //Log.d(TAG, "Pin==" + pin);
        if (pin != null&&"offLine".equals(pin)) {
            //isOffLine = true;
            //strTxt = "offLine success";
            //baseHandler.sendEmptyMessage(0);
            //handler.obtainMessage(ConstantUtils.MSG_PROGRESS, "offLine success").sendToTarget();
        }
    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        countDownLatch.countDown();
        //strTxt = "errorCode==" + errorCode+"\nerrorMsg=="+errorMsg;
        //baseHandler.sendEmptyMessage(0);
        handler.obtainMessage(ConstantUtils.MSG_ERROR, "errorCode==" + errorCode+"\nerrorMsg=="+errorMsg).sendToTarget();
    }

}
