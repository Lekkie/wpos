package com.avantir.wpos.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import com.avantir.wpos.R;
import com.avantir.wpos.WPOSApplication;
import com.avantir.wpos.interfaces.OnTraditionListener;
import com.avantir.wpos.utils.ConstantUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by lekanomotayo on 30/01/2018.
 */
public abstract class BaseActivity extends Activity implements View.OnClickListener, OnTraditionListener{
    private static final String TAG = "BaseActivity";


    protected BaseHandler baseHandler = new BaseHandler(this);

    private static final int CLEARLOG = 2;
    private StringBuffer sb = new StringBuffer("");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }



    protected void initView() {
    }

    protected void initData() {
    }


    /**
     * 隐藏输入法键盘
     * @param view
     */
    protected void hideSoftKeyBoard(View view) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        try {
            Class<EditText> cls = EditText.class;
            Method method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);// 4.0的是setShowSoftInputOnFocus，4.2的是setSoftInputShownOnFocus
            method.setAccessible(false);
            method.invoke(view, false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示输入法键盘
     * @param view
     */
    protected void showSoftKeyBoard(View view) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        try {
            Class<EditText> cls = EditText.class;
            Method method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);// 4.0的是setShowSoftInputOnFocus，4.2的是setSoftInputShownOnFocus
            method.setAccessible(true);
            method.invoke(view, true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * optimize soft input
     * @param view
     */
    protected  void optimizSoftKeyBoard(View view)
    {
        hideSoftKeyBoard(view);
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                {
                    showSoftKeyBoard(view);
                }
                else
                {
                    hideSoftKeyBoard(view);
                }
            }
        });
    }

    protected void displayDialog(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(msg)
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                return;
                            }
                        });
        builder.show();
    }

    @Override
    public void onProgress(String progress) {
        baseHandler.obtainMessage(ConstantUtils.MSG_PROGRESS, progress).sendToTarget();
    }

    /**
     * Hide the progress bar
     */
    public void hideProgress() {
        baseHandler.obtainMessage(ConstantUtils.Hide_Progress).sendToTarget();
    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        baseHandler.obtainMessage(ConstantUtils.MSG_ERROR, errorMsg).sendToTarget();
    }

    @Override
    protected void onStop() {
        baseHandler.removeMessages(ConstantUtils.MSG_BACK);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        baseHandler.removeCallbacksAndMessages(null);
    }

    /**
     * Activity animation - back/forward
     *
     * @param skipDirection Greater than 0 means forward, and vice versa
     */
    protected void skipActivityAnim(int skipDirection) {
        if (skipDirection > 0) {
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        } else {
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }

    protected void onBack() {
        if (!isFinishing() && !isDestroyed())
            finish();
    }

    protected void showToast(String msg) {
        //setHint(msg);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(Context context, String toastString) {
        Toast.makeText(context, toastString, Toast.LENGTH_LONG).show();
    }

    //Exit the App call method
    public void finishAppActivity() {
        for (int i = 0; i < WPOSApplication.activityList.size(); i++) {
            Activity ac = WPOSApplication.activityList.get(i);
            if (ac != null) {
                ac.finish();
            }
            ac = null;
        }
        WPOSApplication.activityList.clear();
    }


    /**
     * show logs
     *
     * @param msg
     */
    public void LOGD(String msg) {
        Message message = new Message();
        message.what = ConstantUtils.SHOWLOG;
        message.obj = msg;
        baseHandler.sendMessage(message);
    }

    /**
     * clear logs
     */
    protected void ClearLog() {
        Message message = new Message();
        message.what = CLEARLOG;
        baseHandler.sendMessage(message);
    }





    protected void handleMessage(Message msg) {
    }




    protected class BaseHandler extends Handler {

        WeakReference<BaseActivity> mActivity;

        BaseHandler(BaseActivity activity) {
            mActivity = new WeakReference<BaseActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseActivity theActivity = mActivity.get();
            Log.i(TAG, "handleMessage: what" + msg.what);

            switch (msg.what) {
                case ConstantUtils.MSG_BACK:
                    onBack();
                    break;
                case ConstantUtils.ShowToastFlag:
                    showToast(theActivity, msg.obj.toString());
                    break;
                case ConstantUtils.SHOWLOG:
                    sb.append(msg.obj + "\n");
                    Log.i(TAG, "handleMessage: getMessage - " + sb);
                    break;
                default:
                    BaseActivity.this.handleMessage(msg);
                    break;
            }
        }
    }


}
