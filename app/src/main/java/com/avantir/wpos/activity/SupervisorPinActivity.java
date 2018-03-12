package com.avantir.wpos.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.avantir.wpos.R;
import com.avantir.wpos.utils.ConstantUtils;
import com.avantir.wpos.utils.GlobalData;
import com.avantir.wpos.utils.StringUtil;

/**
 * Created by lekanomotayo on 24/01/2018.
 */

public class SupervisorPinActivity extends BaseActivity implements  View.OnFocusChangeListener{

    private String TAG = "SupervisorPinActivity";

    private Bundle bundle;
    private int nextActivity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_supervisor_pin);

        findViewById(R.id.titleBackImage).setVisibility(View.GONE);
        findViewById(R.id.titleSettingsImage).setVisibility(View.GONE);

        super.onCreate(savedInstanceState);
    }

    //@Override
    protected void initView() {
        //Top title bar
        ((ImageView) findViewById(R.id.titleBackImage)).setOnClickListener(this);
        ((EditText) findViewById(R.id.supervisorPINText)).setOnFocusChangeListener(this);
        ((Button) findViewById(R.id.cancel_supervisor_pin_btn)).setOnClickListener(this);
        ((Button) findViewById(R.id.ok_supervisor_pin_btn)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.supervisor_pin_page)).setOnClickListener(this);
    }

    @Override
    protected void initData() {

        bundle = getIntent().getExtras();
        if(bundle == null)
        {
            bundle = new Bundle();
        }

        nextActivity = bundle.getInt(ConstantUtils.NEXT_ACTIVITY);
        //EditText supervisorPINEditText = (EditText) findViewById(R.id.supervisorPINText);
        //supervisorPINEditText.setText(String.valueOf(globalData.getCallHomePeriodInMin()), TextView.BufferType.EDITABLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.titleBackImage:
                onBack();
                break;
            case R.id.supervisor_pin_page:
                InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                break;
            case R.id.cancel_supervisor_pin_btn:
                displayDialog("Authentication Failed", ConstantUtils.MSG_BACK);
                //back();
                break;
            case R.id.ok_supervisor_pin_btn:
                verifyPIN();
                break;
            default:
                break;
        }
    }


    @Override
    protected void handleMessage(Message msg) {
        Log.i(PayActivity.class.getSimpleName(), "handleMessage: " + msg.what);
        switch (msg.what) {
            case ConstantUtils.MSG_BACK:
                onBack();
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus){
        //if(!hasFocus) {
        //    InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        //}
    }

    private void verifyPIN(){
        try{
            String pin = ((EditText) findViewById(R.id.supervisorPINText)).getText().toString();
            String verifyPin = globalData.getSupervisorPIN();
            if(pin.equalsIgnoreCase(verifyPin)){
                if(nextActivity == ConstantUtils.REFUND_ACTIVITY){
                    Intent intent = new Intent(this, TranSequenceNrActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(nextActivity == ConstantUtils.REPRINT_ACTIVITY){
                    startActivity(new Intent(this, ReprintActivity.class));
                    finish();
                }
                else if(nextActivity == ConstantUtils.EOD_ACTIVITY){
                    startActivity(new Intent(this, EoDActivity.class));
                    finish();
                }
            }
            else if(StringUtil.isEmpty(pin)){
                if(nextActivity == ConstantUtils.REFUND_ACTIVITY){
                    displayDialog("Authentication Invalid");
                }
                else{
                    displayDialog("Authentication Failed", ConstantUtils.MSG_BACK);
                }
            }
            else{
                displayDialog("Authentication Failed", ConstantUtils.MSG_BACK);
            }
        }
        catch(Exception ex){
            displayDialog("Authentication Error!", ConstantUtils.MSG_BACK);
        }
    }

    protected void onBack(){
        //finish();
        //skipActivityAnim(-1);
        startActivity(new Intent(this, MainMenuActivity.class));
        finish();
    }



    GlobalData globalData = GlobalData.getInstance();

    CountDownTimer countDownTimer = new CountDownTimer(globalData.getPageTimerInSec() * 1000, globalData.getPageTimerInSec() * 1000) {

        public void onTick(long millisUntilFinished) {
            //System.out.println("Tick Tock...");
        }

        public void onFinish() {
            onBack();
        }

    }.start();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            restartTimer();
        }
        return super.onTouchEvent(event);
    }


    private void restartTimer(){
        countDownTimer.cancel();
        countDownTimer.start();
        System.out.println("Starting timer again");
    }


}
