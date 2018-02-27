package com.avantir.wpos.listeners;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.avantir.wpos.activity.BaseActivity;
import com.avantir.wpos.interfaces.ICommsListener;
import com.avantir.wpos.utils.ConstantUtils;

/**
 * Created by lekanomotayo on 12/02/2018.
 */
public class CommsListener implements ICommsListener {

    private Handler handler;
    int requestType;

    public CommsListener(Handler handler, int requestType){
        this.handler = handler;
        this.requestType = requestType;
    }

    @Override
    public void OnStatus(int paramInt, byte[] paramArrayOfByte) {
        switch (paramInt) {
            case ConstantUtils.INIT_COMMU:
                //ba.LOGD("PBOC Communication init: Initialization communication");
                //ba.freshProcessDialog("commu init...");
                handler.obtainMessage(BaseActivity.MSG_PROGRESS,"Initializing...").sendToTarget();
                break;
            case ConstantUtils.CONNECTING:
                //ba.LOGD("PBOC Communication connecting: Connecting to server");
                //ba.freshProcessDialog("commu connecting...");
                handler.obtainMessage(BaseActivity.MSG_PROGRESS,"Connecting...").sendToTarget();
                break;
            case ConstantUtils.SENDING:
                //ba.LOGD("PBOC Communication sending: Sending data");
                //ba.freshProcessDialog("commu send data...");
                handler.obtainMessage(BaseActivity.MSG_PROGRESS,"Sending...").sendToTarget();
                break;
            case ConstantUtils.RECVING:
                //ba.LOGD("PBOC Communication recving: Receiving data");
                //ba.freshProcessDialog("commu recv data...");
                handler.obtainMessage(BaseActivity.MSG_PROGRESS,"Receiving...").sendToTarget();
                break;
            case ConstantUtils.FINISH:
                //ba.LOGD("PBOC Communication finish: Communications to complete");
                //ba.freshProcessDialog("commu finish...");
                handler.obtainMessage(BaseActivity.MSG_PROGRESS,"comms finish...").sendToTarget();
                byte[] receiveData = new byte[paramArrayOfByte.length];
                System.arraycopy(paramArrayOfByte, 0, receiveData, 0, paramArrayOfByte.length);
                Message msg = handler.obtainMessage(BaseActivity.MSG_FINISH_COMMS);
                msg.obj = receiveData;
                Bundle bundle = new Bundle();
                bundle.putInt(ConstantUtils.NETWORK_REQ_TYPE, requestType);
                msg.setData(bundle);
                msg.sendToTarget();
                break;
            default:
                break;
        }

    }

    @Override
    public void OnError(int paramInt, String paramString) {
        //ba.LOGD("PBOC Communication error code:" + paramInt + " error:" + paramString);
        //ba.freshProcessDialog("commu finish...");
        handler.obtainMessage(BaseActivity.MSG_PROGRESS,"comms finish with error...").sendToTarget();

        Message msg = handler.obtainMessage(BaseActivity.MSG_FINISH_ERROR_COMMS, paramInt);
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantUtils.NETWORK_REQ_TYPE, requestType);
        msg.setData(bundle);
        msg.sendToTarget();
    }
}
