package com.avantir.wpos.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.*;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import com.avantir.wpos.interfaces.OnPinPadListener;
import com.avantir.wpos.R;
import com.avantir.wpos.utils.ByteUtil;
import com.avantir.wpos.utils.ConstantUtils;
import wangpos.sdk4.base.ICallbackListener;
import wangpos.sdk4.libbasebinder.Core;


public class KeyPadDialog {
    private Core mCore;
    Button btnb1, btnb2, btnb3, btnb4, btnb5, btnb6, btnb7, btnb8, btnb9, btnb0,
            btncancel, btnconfirm, btnclean,btnback;
    View view;
    Dialog dialog;

    Context mcontext = null;
    TextView msg_title;

    Handler mHandler = null;
    static KeyPadDialog keypad;
    private ICallbackListener callback;
    private OnPinPadListener pinListener;
    private boolean isOffLine = false;

    public static KeyPadDialog getInstance() {
        if(keypad == null ){
            keypad = new KeyPadDialog();
        }
        return keypad;
    }

    public KeyPadDialog() {
//        init();
    }

    public void showDialog(final Activity context, String Pan, final OnPinPadListener onPinPadListener){
        this.pinListener = onPinPadListener;
        mHandler = new EventHandler();
        new Thread() {
            @Override
            public void run() {
                mCore = new Core(context.getApplicationContext());
            }
        }.start();

        dialog = new Dialog(context, R.style.my_dialog);
        view = LayoutInflater.from(context).inflate(R.layout.layout_pin,null);
        msg_title = (TextView) view.findViewById(R.id.msg_title);
        msg_title.setVisibility(View.GONE);
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
        dialogWindow.setGravity( Gravity.CENTER);

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
        if (!context.isFinishing() || !context.isDestroyed())
        dialog.show();
        Log.v("button",btnb0.getY()+"---"+btnb0.getX()+"----"+btnb0.getPivotX()+"----"+btnb0.getPivotX());
        callback = new ICallbackListener.Stub(){
            @Override
            public int emvCoreCallback(int command, byte[] data, byte[] result, int[] resultlen) throws RemoteException {
                Log.e("dialog emvCoreCallback"," command:"+command+"\tdata"+data[0]+mHandler);
                if (command != Core.CALLBACK_PIN)
                    return -1;
                if (data[0] == Core.PIN_CMD_PREPARE) {
                    Log.e("PINPad", "pin pad init data len is " + data.length);

                    Message msg = new Message();
                    msg.what = 1;
                    Bundle bd = new Bundle();
                    bd.putByteArray("data", data);
                    msg.setData(bd);
                    Log.i("KeyPadDialog", "PIN_CMD_PREPARE: "+new String(data)+"---"+data[1]);
                    if (mHandler != null)
                        mHandler.sendMessage(msg);

                    try {
                        mCore.generatePINPrepareData(result, btnb1, btnb2, btnb3, btnb4, btnb5,
                                btnb6, btnb7, btnb8, btnb9, btnb0, btncancel,
                                btnconfirm, btnclean,btnback,  context);
                        resultlen[0]=113;
                    } catch (Exception e) {
                        Log.e("PINPad", "mReceiver RemoteException " + e.toString());
                    }
                } else if (data[0] == Core.PIN_CMD_UPDATE) {
                    result[0] = 0;
                    resultlen[0] = 1;

                    Message msg = new Message();
                    msg.what = 2;
                    Bundle bd = new Bundle();
                    bd.putByteArray("data", data);
                    Log.i("KeyPadDialog", "PIN_CMD_UPDATE: "+new String(data));
                    msg.setData(bd);
                    if (mHandler != null)
                        mHandler.sendMessage(msg);
                } else if (data[0] == Core.PIN_CMD_QUIT) {
                    Log.i("KeyPadDialog", "emvCoreCallback: "+ Core.PIN_CMD_QUIT +"---"+mHandler);
                    result[0] = 0;
                    resultlen[0] = 1;

                    Message msg = new Message();
                    msg.what = 3;
                    Bundle bd = new Bundle();
                    bd.putByteArray("data", data);
                    dialog.dismiss();
                    msg.setData(bd);
                    if (mHandler != null)
                        mHandler.sendMessage(msg);

                }
                return 0;
            }
        };

        ((TextView)view.findViewById(R.id.textView)).setText("");
        btnclean.setText(context.getResources().getString(R.string.cancel));
        ((TextView) view.findViewById(R.id.textView)).addTextChangedListener(new TextWatcher() {
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
        new PINThread(Pan).start();

        btnclean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.buttonexit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }



    public int showDialog(final Activity context, final int command, final byte[] data, final byte[] result, final int[] resultlen, final OnPinPadListener onPinPadListener){
        this.pinListener = onPinPadListener;
        mHandler = new EventHandler();
        new Thread() {
            @Override
            public void run() {
                mCore = new Core(context.getApplicationContext());
            }
        }.start();
        if(data[0]!=0x01&&dialog!=null&&dialog.isShowing())
        {
            if (command != Core.CALLBACK_PIN) {
                onPinPadListener.onUpDate();
                return -1;
            }
            if (data[0] == Core.PIN_CMD_PREPARE) {
                Log.e("PINPad", "pin pad init data len is " + data.length);

                Message msg = new Message();
                msg.what = 1;
                Bundle bd = new Bundle();
                bd.putByteArray("data", data);
                msg.setData(bd);
                if (mHandler != null)
                    mHandler.sendMessage(msg);

                try {
                    mCore.generatePINPrepareData(result, btnb1, btnb2, btnb3, btnb4, btnb5,
                            btnb6, btnb7, btnb8, btnb9, btnb0, btncancel,
                            btnconfirm, btnclean,btnback,  context);
                    resultlen[0]=113;
                    onPinPadListener.onUpDate();
                } catch (Exception e) {
                    Log.e("PINPad", "mReceiver RemoteException " + e.toString());
                    onPinPadListener.onUpDate();
                }
            } else if (data[0] == Core.PIN_CMD_UPDATE) {
                result[0] = 0;
                resultlen[0] = 1;

                Message msg = new Message();
                msg.what = 2;
                Bundle bd = new Bundle();
                bd.putByteArray("data", data);
                msg.setData(bd);
                onPinPadListener.onUpDate();
                if (mHandler != null)
                    mHandler.sendMessage(msg);

            } else if (data[0] == Core.PIN_CMD_QUIT) {
                result[0] = 0;
                resultlen[0] = 1;

                Message msg = new Message();
                msg.what = 3;
                Bundle bd = new Bundle();
                bd.putByteArray("data", data);
                msg.setData(bd);
                if (mHandler != null)
                    mHandler.sendMessage(msg);
            }
        }else {
            view = LayoutInflater.from(context).inflate(R.layout.layout_pin, null);
            dialog = new Dialog(context, R.style.my_dialog);
            msg_title = (TextView) view.findViewById(R.id.msg_title);
            msg_title.setVisibility(View.VISIBLE);
            if (data[1] == 01) {
                isOffLine = false;
                msg_title.setText("online pin");
            }else if (data[1] == 02) {
                isOffLine = true;
                msg_title.setText("offline pin,retry times:"+data[3]);
            }
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    if (command != Core.CALLBACK_PIN) {
                        onPinPadListener.onUpDate();
                        return;
                    }
                    if (data[0] == Core.PIN_CMD_PREPARE) {
                        Log.e("PINPad", "pin pad init data len is " + data.length);

                        Message msg = new Message();
                        msg.what = 1;
                        Bundle bd = new Bundle();
                        bd.putByteArray("data", data);
                        msg.setData(bd);
                        if (mHandler != null)
                            mHandler.sendMessage(msg);

                        try {
                            mCore.generatePINPrepareData(result, btnb1, btnb2, btnb3, btnb4, btnb5,
                                    btnb6, btnb7, btnb8, btnb9, btnb0, btncancel,
                                    btnconfirm, btnclean,btnback, context);
                            resultlen[0] = 113;
                            onPinPadListener.onUpDate();
                        } catch (Exception e) {
                            Log.e("PINPad", "mReceiver RemoteException " + e.toString());
                            onPinPadListener.onUpDate();
                        }
                    } else if (data[0] == Core.PIN_CMD_UPDATE) {
                        result[0] = 0;
                        resultlen[0] = 1;

                        Message msg = new Message();
                        msg.what = 2;
                        Bundle bd = new Bundle();
                        bd.putByteArray("data", data);
                        msg.setData(bd);
                        onPinPadListener.onUpDate();
                        if (mHandler != null)
                            mHandler.sendMessage(msg);

                    } else if (data[0] == Core.PIN_CMD_QUIT) {
                        result[0] = 0;
                        resultlen[0] = 1;

                        Message msg = new Message();
                        msg.what = 3;
                        Bundle bd = new Bundle();
                        bd.putByteArray("data", data);
                        msg.setData(bd);
                        if (mHandler != null)
                            mHandler.sendMessage(msg);
                    }
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
            ((TextView) view.findViewById(R.id.textView)).setText("");
            view.findViewById(R.id.buttonexit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            ((TextView) view.findViewById(R.id.textView)).addTextChangedListener(new TextWatcher() {
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
        }
            return 0;
        }


    public  class PINThread extends Thread {
        private String panNo = "";
        public PINThread(String pan){
            panNo = pan;
        }
        @Override
        public void run () {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            byte[] formatdata = new byte[8];
            int ret = -1;
            try {
                ret = mCore.startPinInput(ConstantUtils.PIN_TIMEOUT, ConstantUtils.APP_NAME, ConstantUtils.SUPPORT_PIN_BYPASS, ConstantUtils.MIN_PIN_LENGTH, ConstantUtils.MAX_PIN_LENGTH, ConstantUtils.PIN_BLOCK_FORMAT, formatdata, panNo.length(), panNo.getBytes("UTF-8"), callback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void init(){
        Looper.prepare();
        mHandler = new EventHandler();
//        new PINThread().start();
    }

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
                        //Pain PIN
                        //only for test mode
                        else if (data[2] == Core.PIN_QUIT_PAINUPLOAD) {
                            int pinlen = data[3];
                            Log.e("PINPad", "Pain pinlen is " + pinlen);
                            byte[] PINData = new byte[pinlen+1];
                            PINData[0] = data[1];
                            java.lang.System.arraycopy(data, 4, PINData, 1, pinlen);
                            String strpin = new String(PINData);
                        }
                        //Encrypt PIN
                        else if (data[2] == Core.PIN_QUIT_PINBLOCKUPLOAD) {
                            int pinlen = data[3];
                            Log.e("PINPad", "Encrypt pinlen is " + pinlen);
                            byte[] PINData = new byte[pinlen+1];
                            PINData[0] = data[1];
                            java.lang.System.arraycopy(data, 4, PINData, 1, pinlen);
                            String strpin = ByteUtil.bytes2HexString(PINData);
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
                        int errorCode = data[1];
                        String str = "";
                        if (errorCode == -5) {
                            str = "Timeout";
                        }else if (errorCode == -14) {
                            str = "no PAN";
                        }
                        pinListener.onError(errorCode,str);
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
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
}
