package com.avantir.wpos.printer;

import android.graphics.Typeface;
import android.os.Handler;
import android.os.RemoteException;
import com.avantir.wpos.activity.BaseActivity;
import com.avantir.wpos.model.TransInfo;
import com.avantir.wpos.utils.*;
import wangpos.sdk4.libbasebinder.Printer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by lekanomotayo on 15/02/2018.
 */
public class PrintTransactionThread extends Thread {

    private Printer mPrinter;
    TransInfo transInfo;
    Handler handler;
    boolean customerCopy = false;

    public PrintTransactionThread(Printer mPrinter, Handler handler, TransInfo transInfo, boolean customerCopy){
        this.mPrinter = mPrinter;
        this.handler = handler;
        this.transInfo = transInfo;
        this.customerCopy = customerCopy;
    }


        @Override
        public void run () {
            //bthreadrunning = true;
            int datalen = 0;
            int result = 0;
            byte[] senddata = null;

            try {
                result = mPrinter.printInit();
                //clear print cache
                mPrinter.clearPrintDataCache();

                String receiptCopy = customerCopy ? "* Customer Copy *" : "* Merchant Copy *";

                String transDateTime = transInfo.getTransmissionDateTime();
                String transMonth = transDateTime.substring(0, 2);
                String transDay = transDateTime.substring(2, 4);
                String hr = transDateTime.substring(4, 6);
                String min = transDateTime.substring(6, 8);
                String sec = transDateTime.substring(8, 10);
                String currentYr = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                String transDate = currentYr + "-" + transMonth + "-" + transDay + " " + hr + ":" + min + ":" + sec;

                GlobalData globalData = GlobalData.getInstance();
                String stan = StringUtil.leftPad(String.valueOf(transInfo.getStan()), 6, '0'); // 32 - 6 = 26 // "000029"
                String retRefNo = StringUtil.leftPad(String.valueOf(transInfo.getRetRefNo()), 12, '0'); // 32 - 8 = 24 // 170920134009
                String amt = MoneyUtil.kobo2Naira(Long.parseLong(transInfo.getAmt()));
                String authMethod = transInfo.getAuthenticationMethod();
                String status = transInfo.getResponseCode();
                String statusText = "00".equalsIgnoreCase(status) ?  ConstantUtils.TRANSACTION_APPROVED : ConstantUtils.TRANSACTION_DECLINED;

                float lineSpacing = 1f;
                int bodyFontSize = 24; // 24
                Printer.Font bodyFont = Printer.Font.SANS_SERIF;

                mPrinter.setPrntString_TypeFace(Typeface.SANS_SERIF);
                result = mPrinter.printString("------------------------------------------",30, Printer.Align.CENTER,true,false);
                result = mPrinter.printString(receiptCopy, 26, Printer.Align.CENTER, false, false);
                result = mPrinter.printString("", 10, Printer.Align.LEFT, false, false);
                result = mPrinter.printString("Terminal No: " + transInfo.getTerminalId() + "\n", bodyFontSize, Printer.Align.LEFT, false, false);
                result = mPrinter.printString("Tran Date: " + transDate + "\n", bodyFontSize, Printer.Align.LEFT, false, false);
                result = mPrinter.printString("------------------------------------------\n",30, Printer.Align.CENTER,true,false);
                result = mPrinter.printString(statusText, 40, Printer.Align.CENTER, true, false);
                result = mPrinter.printString("------------------------------------------",30, Printer.Align.CENTER,true,false);
                result = mPrinter.print2StringInLine("Merchant: ", transInfo.getMerchantName(),lineSpacing, bodyFont, bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.print2StringInLine("Merchant Id: ", globalData.getMerchantId(),lineSpacing, bodyFont, bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.printString("Location: " + globalData.getMerchantLoc(),bodyFontSize, Printer.Align.LEFT, false, false);
                result = mPrinter.print2StringInLine("Acquirer Id: ", globalData.getAcquirerId(),lineSpacing,bodyFont,bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.print2StringInLine("Card PAN: ", transInfo.getMaskedPan(),lineSpacing,bodyFont,bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.print2StringInLine("Name: ", transInfo.getCardHolderName(),lineSpacing,bodyFont,bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.print2StringInLine("Card Type: ", transInfo.getCardTypeName(),lineSpacing,bodyFont,bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.print2StringInLine("Account Type: ", IsoMessageUtil.getAccountTypeName(transInfo.getAccountType()),lineSpacing,bodyFont,bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.print2StringInLine("ExpiryDate: ", transInfo.getExpDate(),lineSpacing,bodyFont,bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.print2StringInLine("STAN: ", stan,lineSpacing, bodyFont,bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.print2StringInLine("AuthNum: ", transInfo.getAuthNum(),lineSpacing,bodyFont,bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.print2StringInLine("Ref No: ", retRefNo,lineSpacing,bodyFont,bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.print2StringInLine("Auth Method: ", authMethod, lineSpacing,bodyFont,bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.print2StringInLine("Status: ", status,lineSpacing,bodyFont,bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.print2StringInLine("Amt: ", "NGN " + amt,lineSpacing,bodyFont,bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.printString("---------------",bodyFontSize, Printer.Align.RIGHT,false, false);
                result = mPrinter.print2StringInLine("Total: ", "NGN " + amt,lineSpacing,bodyFont,bodyFontSize, Printer.Align.LEFT,false, false, false);
                result = mPrinter.printString("---------------",bodyFontSize, Printer.Align.RIGHT,false, false);
                result = mPrinter.printString("Please retain your receipt\n", bodyFontSize, Printer.Align.LEFT, false, false);
                result = mPrinter.printString("Thank you", bodyFontSize, Printer.Align.LEFT, false, false);
                result = mPrinter.printString("               Powered by " + globalData.getPTSP(),bodyFontSize, Printer.Align.LEFT,false,false);
                result = mPrinter.printString("               Version: 1.0\n",bodyFontSize, Printer.Align.LEFT,false,false);
                result = mPrinter.printString("------------------------------------------\n\n",30, Printer.Align.CENTER,true,false);

                result = mPrinter.printPaper(50);// 100, 80
                result = mPrinter.printFinish();

            } catch (RemoteException e) {
                e.printStackTrace();
            }

            handler.obtainMessage(ConstantUtils.MSG_FINISH_PRINT, customerCopy).sendToTarget();
        }
}
