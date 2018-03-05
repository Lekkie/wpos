package com.avantir.wpos.printer;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import com.avantir.wpos.WPOSApplication;
import com.avantir.wpos.dao.TransInfoDao;
import com.avantir.wpos.dialog.KeyPadDialog;
import com.avantir.wpos.model.TransInfo;
import com.avantir.wpos.utils.ConstantUtils;
import com.avantir.wpos.utils.StringUtil;
import sdk4.wangpos.libemvbinder.EmvCore;
import wangpos.sdk4.libbasebinder.Core;
import wangpos.sdk4.libbasebinder.Printer;

/**
 * Created by lekanomotayo on 03/03/2018.
 */
public class ReprintTransaction {

    static ReprintTransaction reprintTransaction;
    static Handler handler;

    private ReprintTransaction(){

    }

    public static ReprintTransaction  getInstance(){
        if(reprintTransaction == null)
            reprintTransaction = new ReprintTransaction();

        return reprintTransaction;
    }

    public void reprint(){
        try{
            handler = new EventHandler();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Printer mPrinter = new Printer(WPOSApplication.app);
                    TransInfoDao transInfoDao = new TransInfoDao(WPOSApplication.app);
                    TransInfo transInfo = transInfoDao.findLastTransaction();
                    if(transInfo != null){
                        PrintTransactionThread customerCopy = new PrintTransactionThread(mPrinter, handler, transInfo, true);
                        PrintTransactionThread merchantCopy = new PrintTransactionThread(mPrinter, handler, transInfo, false);
                    }
                }
            }).start();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }



    class EventHandler extends Handler {
        public EventHandler() {
        }

        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            //Bundle bd = null;
            byte[] data = null;
            //Log.i("EventHandler", "handleMessage: "+msg.what);
            switch (msg.what) {
                case ConstantUtils.MSG_FINISH_PRINT:
                    // go to main page
                    break;
                default:
                    break;
            }
        }
    }
}
