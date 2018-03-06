package com.avantir.wpos.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.avantir.wpos.R;
import com.avantir.wpos.utils.GlobalData;

/**
 * Created by lekanomotayo on 24/01/2018.
 */

public class CallHomeConfigActivity extends BaseActivity implements  View.OnFocusChangeListener{

    private String TAG = "CallHomeConfigActivity";

    int timeout = 0;
    GlobalData globalData;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_callhome_config);

        findViewById(R.id.titleBackImage).setVisibility(View.GONE);
        findViewById(R.id.titleSettingsImage).setVisibility(View.GONE);

        super.onCreate(savedInstanceState);
    }

    //@Override
    protected void initView() {
        //Top title bar
        ((ImageView) findViewById(R.id.titleBackImage)).setOnClickListener(this);
        ((EditText) findViewById(R.id.callhomeTimeoutText)).setOnFocusChangeListener(this);
        ((Button) findViewById(R.id.cancel_callhome_btn)).setOnClickListener(this);
        ((Button) findViewById(R.id.save_callhome_btn)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.callhome_page)).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        globalData = GlobalData.getInstance();

        EditText timeoutEditText = (EditText) findViewById(R.id.callhomeTimeoutText);
        timeoutEditText.setText(String.valueOf(globalData.getCallHomePeriodInMin()), TextView.BufferType.EDITABLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.titleBackImage:
                finish();
                skipActivityAnim(-1);
                break;
            case R.id.callhome_page:
                InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                break;
            case R.id.cancel_callhome_btn:
                finish();
                skipActivityAnim(-1);
                break;
            case R.id.save_callhome_btn:
                saveHostData();
                break;
            default:
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

    private void saveHostData(){
        try{
            timeout = Integer.parseInt(((EditText) findViewById(R.id.callhomeTimeoutText)).getText().toString());

            globalData.setCallHomePeriodInMin(timeout);
            showToast("Saved CallHome timeout!");
            finish();
            skipActivityAnim(-1);
        }
        catch(Exception ex){
            showToast("Error saving CallHome config!");
        }
    }

}
