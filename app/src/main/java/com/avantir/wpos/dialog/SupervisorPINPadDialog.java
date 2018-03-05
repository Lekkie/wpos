package com.avantir.wpos.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.*;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import com.avantir.wpos.R;
import com.avantir.wpos.interfaces.OnPinPadListener;
import com.avantir.wpos.utils.ByteUtil;
import com.avantir.wpos.utils.ConstantUtils;
import com.avantir.wpos.utils.StringUtil;
import wangpos.sdk4.base.ICallbackListener;
import wangpos.sdk4.libbasebinder.Core;


public class SupervisorPINPadDialog {


    Button btnb1, btnb2, btnb3, btnb4, btnb5, btnb6, btnb7, btnb8, btnb9, btnb0,
            btncancel, btnconfirm, btnclean,btnback;
    View view;
    Dialog dialog;

    Context mcontext = null;
    TextView msg_title;

    Handler mHandler = null;
    static SupervisorPINPadDialog keypad;

    //private OnPinPadListener pinListener;
    private boolean isOffLine = false;

    private static boolean firstTime = true;
    private static int maxPinRetry = 3;
    private static int currentPinRetry = 1;

    public static SupervisorPINPadDialog getInstance() {
        if(keypad == null ){
            keypad = new SupervisorPINPadDialog();
        }
        return keypad;
    }

    public SupervisorPINPadDialog() {
//        init();
    }

    public void clearPinRetries(){
        firstTime = true;
        maxPinRetry = 3;
        currentPinRetry = 1;
    }



    public int showDialog(final Activity context, final Intent intent){
        //this.pinListener = onPinPadListener;
        //mHandler = new EventHandler();

        view = LayoutInflater.from(context).inflate(R.layout.layout_pin, null);
        dialog = new Dialog(context, R.style.my_dialog);
        msg_title = (TextView) view.findViewById(R.id.msg_title);
        msg_title.setVisibility(View.VISIBLE);
        msg_title.setText("Enter Supervisor PIN");

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                System.out.println();
            }
        });


        btnb1 = (Button) view.findViewById(R.id.button1);
        btnb2 = (Button) view.findViewById(R.id.button2);
        btnb3 = (Button) view.findViewById(R.id.button3);
        btnb4 = (Button) view.findViewById(R.id.button4);
        btnb5 = (Button) view.findViewById(R.id.button5);
        btnb6 = (Button) view.findViewById(R.id.button6);
        btnb7 = (Button) view.findViewById(R.id.button7);
        btnb8 = (Button) view.findViewById(R.id.button8);
        btnb9 = (Button) view.findViewById(R.id.button9);
        btnb0 = (Button) view.findViewById(R.id.button0);
        btncancel = (Button) view.findViewById(R.id.buttoncan);
        btnconfirm = (Button) view.findViewById(R.id.buttonconfirm);
        btnclean = (Button) view.findViewById(R.id.buttonclean);
        btnback = (Button) view.findViewById(R.id.buttonback);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        Log.v("button", btnb0.getY() + "---" + btnb0.getX() + "----" + btnb0.getPivotX() + "----" + btnb0.getPivotX());
        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        view.measure(0, 0);
        lp.height = view.getMeasuredHeight();
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
//        view.setVisibility(View.INVISIBLE);
        dialog.setContentView(view);
        Log.i("", "showDialog: context.isDestroyed()"+context.isDestroyed()+"context.isFinishing()"+context.isFinishing());
        if (!context.isFinishing() || !context.isDestroyed())
            dialog.show();
        Log.v("button show", "-----");
        TextView textView = ((TextView) view.findViewById(R.id.textView));
        textView.setText("");


        view.findViewById(R.id.buttonexit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()==0)
                    btnclean.setText(context.getResources().getString(R.string.cancel));
                else
                    btnclean.setText(context.getResources().getString(R.string.clear));
            }
        });


        btnconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pin = ((TextView)view).getText().toString();
                if(StringUtil.isEmpty(pin))
                    context.startActivity(intent);
                else{
                    msg_title.setText("Authentication Failed");
                    //msg_title.setText("INCORRECT PIN (" + currentPinRetry++ + "/" + maxPinRetry + ")");
                }
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        btnclean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TextView)view).setText("");
            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pin = ((TextView)view).getText().toString();
                ((TextView)view).setText(pin);
                //((TextView)view.findViewById(R.id.textView)).setText(stars);
            }
        });


        return 0;
    }


    /*
    class EventHandler extends Handler {
        public EventHandler() {
        }

        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            Bundle bd = null;
            byte[] data = null;
            Log.i("EventHandler", "handleMessage: "+msg.what);
            switch (msg.what) {
                case 1:
                    // PIN input process start, secure chip generated random key sequence need display.
                    bd = msg.getData();
                    data = bd.getByteArray("data");
                    String displaynumber = null;
                    displaynumber = "" + (data[4] - 0x30);
                    btnb1.setText(displaynumber);

                    displaynumber = "" + (data[5] - 0x30);
                    btnb2.setText(displaynumber);

                    displaynumber = "" + (data[6] - 0x30);
                    btnb3.setText(displaynumber);

                    displaynumber = "" + (data[7] - 0x30);
                    btnb4.setText(displaynumber);

                    displaynumber = "" + (data[8] - 0x30);
                    btnb5.setText(displaynumber);

                    displaynumber = "" + (data[9] - 0x30);
                    btnb6.setText(displaynumber);

                    displaynumber = "" + (data[10] - 0x30);
                    btnb7.setText(displaynumber);

                    displaynumber = "" + (data[11] - 0x30);
                    btnb8.setText(displaynumber);

                    displaynumber = "" + (data[12] - 0x30);
                    btnb9.setText(displaynumber);

                    displaynumber = "" + (data[13] - 0x30);
                    btnb0.setText(displaynumber);
                    view.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    // User input, need show corresponding amount of stars *
                    bd = msg.getData();
                    data = bd.getByteArray("data");
                    int count = data[1];
                    String stars = "";
                    for (int i = 0; i < count; i++) {
                        stars += "*";
                    }
                    ((TextView)view.findViewById(R.id.textView)).setText(stars);
                    break;
                case 3:
                    bd = msg.getData();
                    data = bd.getByteArray("data");
                    //Success
                    if (data[1] == Core.PIN_QUIT_SUCCESS) {
                        //No PIN upload
                        if (data[2] == Core.PIN_QUIT_NOUPLOAD) {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            if (isOffLine) {
                                pinListener.onSuccess("offLine");
                                Log.d("PINPad", "offline PIN inputed");
                            }else {
                                Log.e("PINPad", "No PIN inputed");
                            }
                        }
                        //Plain PIN
                        //only for test mode
                        else if (data[2] == Core.PIN_QUIT_PAINUPLOAD) {
                            int pinlen = data[3];
                            Log.e("PINPad", "Pain pinlen is " + pinlen);
                            byte[] PINData = new byte[pinlen+1];
                            PINData[0] = data[1];
                            System.arraycopy(data, 4, PINData, 1, pinlen);
                            String strpin = new String(PINData);
                        }
                        //Encrypt PIN
                        else if (data[2] == Core.PIN_QUIT_PINBLOCKUPLOAD) {
                            int pinlen = data[3];
                            Log.e("PINPad", "Encrypt pinlen is " + pinlen);
                            byte[] pinData = new byte[pinlen+1];
                            pinData[0] = data[1];
                            System.arraycopy(data, 4, pinData, 1, pinlen);
                            String strpin = ByteUtil.bytes2HexString(pinData);
                            pinListener.onSuccess(strpin);
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    }else if (data[1] == Core.PIN_QUIT_CANCEL) {//User canceled
                        pinListener.onCancel();
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }else if (data[1] == Core.PIN_QUIT_BYPASS) {//bypass
                        pinListener.onByPass();
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }else if (data[1] == Core.PIN_QUIT_TIMEOUT||data[1] == Core.PIN_QUIT_ERRORPAN) {

                        // if pin pad has text, clear text and dont close otherwise close.
                        int errorCode = data[1];
                        String str = "";
                        if (errorCode == -5) {
                            str = "Timeout";
                            String pinData = ((TextView)view.findViewById(R.id.textView)).getText().toString();
                            if(StringUtil.isEmpty(pinData)){
                                pinListener.onError(errorCode,str);
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                            else{
                                // it timeout and it has data, then clear
                                ((TextView)view.findViewById(R.id.textView)).setText("");
                                pinListener.onError(errorCode,str);
                            }
                        }else if (errorCode == -14) {
                            str = "no PAN";
                            pinListener.onError(errorCode,str);
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    }else {//others Error
                        pinListener.onError(-100,"others Error");
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }
    */


    private void RestoreKeyPad() {
        btnb1.setText("1");
        btnb2.setText("2");
        btnb3.setText("3");
        btnb4.setText("4");
        btnb5.setText("5");
        btnb6.setText("6");
        btnb7.setText("7");
        btnb8.setText("8");
        btnb9.setText("9");
        btnb0.setText("0");
    }

    public void dismissDialog(){
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }


    public static int getMaxPinRetry() {
        return maxPinRetry;
    }

    public static void setMaxPinRetry(int maxPinRetry) {
        SupervisorPINPadDialog.maxPinRetry = maxPinRetry;
    }

    public static int getCurrentPinRetry() {
        return currentPinRetry;
    }

    public static void setCurrentPinRetry(int currentPinRetry) {
        SupervisorPINPadDialog.currentPinRetry = currentPinRetry;
    }
}
