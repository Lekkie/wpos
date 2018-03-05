package com.avantir.wpos.printer;

import android.graphics.Typeface;
import android.os.Handler;
import android.os.RemoteException;
import com.avantir.wpos.model.TransInfo;
import com.avantir.wpos.utils.*;
import wangpos.sdk4.libbasebinder.Printer;

import java.util.Calendar;

/**
 * Created by lekanomotayo on 15/02/2018.
 */
public class PrintConfigThread extends Thread {

    private Printer mPrinter;
    Handler handler;

    public PrintConfigThread(Printer mPrinter, Handler handler){
        this.mPrinter = mPrinter;
        this.handler = handler;
    }


        @Override
        public void run () {

            int result = 0;
            try {
                result = mPrinter.printInit();
                //clear print cache
                mPrinter.clearPrintDataCache();

                GlobalData globalData = GlobalData.getInstance();

                float lineSpacing = 1f;
                int bodyFontSize = 24; // 24
                Printer.Font bodyFont = Printer.Font.SANS_SERIF;

                mPrinter.setPrntString_TypeFace(Typeface.SANS_SERIF);
                result = mPrinter.printString("------------------------------------------",30, Printer.Align.CENTER,true,false);
                result = mPrinter.print2StringInLine("Merchant Id: ", globalData.getMerchantId(),lineSpacing, bodyFont, bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.print2StringInLine("Terminal Id: ", globalData.getTerminalId(),lineSpacing, bodyFont, bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.print2StringInLine("Acquirer Id: ", globalData.getAcquirerId(),lineSpacing,bodyFont,bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.print2StringInLine("PTSP: ", globalData.getPTSP(),lineSpacing,bodyFont,bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.print2StringInLine("TMS Hostname: ", globalData.getCTMSHost(),lineSpacing,bodyFont,bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.print2StringInLine("TMS IP: ", globalData.getCTMSIP(),lineSpacing,bodyFont,bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.print2StringInLine("TMS Port: ", String.valueOf(globalData.getCTMSPort()),lineSpacing,bodyFont,bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.print2StringInLine("TMS Timeout: ", String.valueOf(globalData.getCTMSTimeout()),lineSpacing,bodyFont,bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.print2StringInLine("TMS SSL: ", String.valueOf(globalData.getIfCTMSSSL()),lineSpacing,bodyFont,bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.print2StringInLine("Callhome  Period (In Min): ", String.valueOf(globalData.getCallHomePeriodInMin()),lineSpacing,bodyFont,bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.printString("------------------------------------------\n\n",30, Printer.Align.CENTER,true,false);

                result = mPrinter.printPaper(50);
                result = mPrinter.printFinish();

            } catch (RemoteException e) {
                e.printStackTrace();
            }

            handler.obtainMessage(ConstantUtils.MSG_FINISH_PRINT).sendToTarget();
        }
}
