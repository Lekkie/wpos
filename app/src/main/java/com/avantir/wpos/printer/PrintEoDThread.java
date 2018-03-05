package com.avantir.wpos.printer;

import android.graphics.Typeface;
import android.os.Handler;
import android.os.RemoteException;
import com.avantir.wpos.model.TransInfo;
import com.avantir.wpos.utils.*;
import wangpos.sdk4.libbasebinder.Printer;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by lekanomotayo on 15/02/2018.
 */
public class PrintEoDThread extends Thread {

    private Printer mPrinter;
    HashMap<String, TransInfo> matched;
    HashMap<String, TransInfo> notMatched;
    Handler handler;

    public PrintEoDThread(Printer mPrinter, Handler handler, HashMap<String, TransInfo> matched, HashMap<String, TransInfo> notMatched){
        this.mPrinter = mPrinter;
        this.handler = handler;
        this.matched = matched;
        this.notMatched = notMatched;
    }


        @Override
        public void run () {
            int datalen = 0;
            int result = 0;
            byte[] senddata = null;

            try {
                result = mPrinter.printInit();
                //clear print cache
                mPrinter.clearPrintDataCache();
                float lineSpacing = 1f;
                int bodyFontSize = 24; // 24
                Printer.Font bodyFont = Printer.Font.SANS_SERIF;
                mPrinter.setPrntString_TypeFace(Typeface.SANS_SERIF);


                GlobalData globalData = GlobalData.getInstance();
                TransInfo tInfo = (matched != null && matched.size() > 0) ? ((TransInfo) matched.values().toArray()[0]) : null;
                tInfo = (tInfo == null && notMatched != null && notMatched.size() > 0) ? ((TransInfo) notMatched.values().toArray()[0]) : tInfo;
                String transDateTime = tInfo.getTransmissionDateTime();
                String transMonth = transDateTime.substring(0, 2);
                String transDay = transDateTime.substring(2, 4);
                String currentYr = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                String transDate = transDay + "/" + transMonth + "/" + currentYr;

                result = mPrinter.printString("Address", bodyFontSize, Printer.Align.LEFT, false, false);
                result = mPrinter.printString(globalData.getMerchantLoc(),bodyFontSize, Printer.Align.LEFT, false, false);
                result = mPrinter.printString("------------------------------------------",30, Printer.Align.CENTER,true,false);
                result = mPrinter.printString("Terminal Id", bodyFontSize, Printer.Align.LEFT, false, false);
                result = mPrinter.printString(globalData.getTerminalId() + "\n", bodyFontSize, Printer.Align.LEFT, false, false);
                result = mPrinter.printString("------------------------------------------",30, Printer.Align.CENTER,true,false);
                result = mPrinter.printString("Day: " + transDate + "\n\n", bodyFontSize, Printer.Align.LEFT, false, false);

                result = mPrinter.printString("Time     |Amount       |Resp|State |",bodyFontSize, Printer.Align.LEFT,false,false);
                //result = mPrinter.printString("Time  |Amount       |Resp|State|Matched",bodyFontSize, Printer.Align.CENTER,true,false);
                result = mPrinter.printString("------------------------------------------",30, Printer.Align.CENTER,true,false);

                int totalTrans = 0;
                int totalPassedTrans = 0;
                int totalFailedTrans = 0;
                long totalApprovedAmt = 0;

                totalTrans = matched.size();
                for(Map.Entry<String, TransInfo> entry : matched.entrySet()) {
                    //String key = entry.getKey();
                    TransInfo transInfo = entry.getValue();
                    String time = transInfo.getTransmissionDateTime().substring(4, 9);
                    time = time.substring(0, 2) + ":" + time.substring(2, 4);
                    time = StringUtil.rightPad(time, 6, ' ');
                    String amtStr = transInfo.getAmt();
                    long amtLong = Long.valueOf(amtStr);
                    String amt = MoneyUtil.kobo2Naira(amtLong);
                    amt = StringUtil.rightPad(amt, 13, ' ');
                    totalApprovedAmt += amtLong;
                    String respCode = transInfo.getResponseCode();
                    respCode = StringUtil.rightPad(respCode, 4, ' ');
                    boolean pass = "00".equalsIgnoreCase(respCode);
                    if(pass)
                        totalPassedTrans++;
                    String state = pass ? "Pass" : "Fail";
                    state = StringUtil.rightPad(state, 5, ' ');
                    //result = mPrinter.printString(time + "    |" + amt + "    |" + respCode + "  |" + state + "   |Matched",bodyFontSize, Printer.Align.CENTER,true,false);
                    result = mPrinter.printString(time + "    |" + amt + "    |" + respCode + "  |" + state + "   |",bodyFontSize, Printer.Align.LEFT,false,false);
                }

                /*
                totalTrans += notMatched.size();
                totalFailedTrans = notMatched.size();
                for(Map.Entry<String, TransInfo> entry : notMatched.entrySet()) {
                    String key = entry.getKey();
                    TransInfo transInfo = entry.getValue();
                    String time = transInfo.getTransmissionDateTime().substring(3, 8);
                    time = time.substring(0, 2) + ":" + time.substring(2, 4);
                    time = StringUtil.rightPad(time, 6, ' ');
                    long amtLong = Long.getLong(transInfo.getAmt());
                    String amt = MoneyUtil.kobo2Naira(amtLong);
                    amt = StringUtil.rightPad(amt, 13, ' ');
                    String respCode = transInfo.getResponseCode();
                    respCode = StringUtil.rightPad(respCode, 4, ' ');
                    String state = "00".equalsIgnoreCase(respCode) ? "Pass" : "Fail";
                    state = StringUtil.rightPad(state, 5, ' ');
                    result = mPrinter.printString(time + "    |" + amt + "    |" + respCode + "  |" + state + "   |Not Matched",bodyFontSize, Printer.Align.LEFT,false,false);
                }
                */

                totalFailedTrans = totalTrans - totalPassedTrans;
                result = mPrinter.printString("------------------------------------------",30, Printer.Align.CENTER,true,false);
                result = mPrinter.printString("Summary",bodyFontSize, Printer.Align.LEFT,true,false);
                result = mPrinter.printString("Total Transactions: " + totalTrans,bodyFontSize, Printer.Align.LEFT,true,false);
                result = mPrinter.printString("Total Passed Transactions: " + totalPassedTrans,bodyFontSize, Printer.Align.LEFT,true,false);
                result = mPrinter.printString("Total Failed Transactions: " + totalFailedTrans,bodyFontSize, Printer.Align.LEFT,true,false);
                result = mPrinter.printString("Total Approved Amount: " + MoneyUtil.kobo2Naira(totalApprovedAmt),bodyFontSize, Printer.Align.LEFT,true,false);

                result = mPrinter.printPaper(100 + totalTrans);// 100, 80
                result = mPrinter.printFinish();

            } catch (RemoteException e) {
                e.printStackTrace();
            }

            handler.sendEmptyMessage(ConstantUtils.MSG_FINISH_PRINT);
        }
}
