package com.avantir.wpos.listeners;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import com.avantir.wpos.dialog.KeyPadDialog;
import com.avantir.wpos.model.TransInfo;
import com.avantir.wpos.services.EMVManager;
import com.avantir.wpos.utils.ByteUtil;
import com.avantir.wpos.utils.ConstantUtils;
import com.avantir.wpos.utils.MoneyUtil;
import wangpos.sdk4.emv.ICallbackListener;

import java.util.concurrent.CountDownLatch;

/**
 * Created by lekanomotayo on 01/03/2018.
 */
public class CallbackListener {

    private static CallbackListener callbackListener;

    private ICallbackListener iCallBackListener;
    private CountDownLatch countDownLatch = null;

    public static CallbackListener getInstance(){
        if(callbackListener == null)
            callbackListener = new CallbackListener();
        return callbackListener;
    }

    public ICallbackListener getICallbackListener(final Context context, final Handler handler, final long orderAmount, final TransInfo transInfo){

        iCallBackListener = new ICallbackListener.Stub() {

                @Override
                public int emvCoreCallback(final int command, final byte[] data, final byte[] result, final int[] resultlen) throws RemoteException {

                    countDownLatch = new CountDownLatch(1);
                    //Log.d(TAG, "emvCoreCallback。command==" + command);
                    switch (command) {
                        case 2818: //Core.CALLBACK_PIN
                            Log.i("iCallbackListener", "Core.CALLBACK_PIN");
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    KeyPadDialog.getInstance().showDialog((Activity) context, command, data, result, resultlen, transInfo.getCardNo(), new EMVPINPadListener(handler, countDownLatch));
                                }
                            });
                            break;
                        case 2821://Core.CALLBACK_ONLINE
                            Log.i("iCallbackListener", "Core.CALLBACK_ONLINE");
                            //strTxt = "getCardInformation …";
                            //baseHandler.sendEmptyMessage(0);
                            handler.obtainMessage(ConstantUtils.MSG_PROGRESS, "Sending transaction online").sendToTarget();
                            int ret = EMVManager.EMV_OnlineProc(result, resultlen,countDownLatch,handler, transInfo);
                            Log.i("iCallbackListener", "Core.CALLBACK_ONLINE, ret = " + ret);
                            break;
                        case 2823:
                            //strTxt = "OffLine pin check success";
                            //baseHandler.sendEmptyMessage(0);
                            handler.obtainMessage(ConstantUtils.MSG_PROGRESS, "PIN Check succesfull").sendToTarget();
                            countDownLatch.countDown();
                            break;
                        case 2817://Core.CALLBACK_NOTIFY
                            Log.i("iCallbackListener", "Core.CALLBACK_NOTIFY");
                            //app select
                            break;
                        case 2819://Core.CALLBACK_AMOUNT //Set amount
                            String amt = MoneyUtil.kobo2Naira(orderAmount);
                            result[0] = 0;
                            byte[] tmp = ByteUtil.int2Bytes((int) (int) MoneyUtil.naira2Kobo(Double.parseDouble(amt)));
                            System.arraycopy(tmp, 0, result, 1, 4);
                            resultlen[0] = 9;
                            countDownLatch.countDown();
                            break;
                    }
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }

            };
        return iCallBackListener;
    }


}
