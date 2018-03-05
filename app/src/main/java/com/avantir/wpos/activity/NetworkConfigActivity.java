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

public class NetworkConfigActivity extends BaseActivity implements  View.OnFocusChangeListener{

    private String TAG = "NetworkConfigActivity";

    String host = "";
    String ip = "";
    int port = 0;
    int timeout = 0;
    boolean isSSL = false;
    GlobalData globalData;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ctms_config);
        findViewById(R.id.titleSettingsImage).setVisibility(View.GONE);
        super.onCreate(savedInstanceState);
    }

    //@Override
    protected void initView() {
        //Top title bar
        ((ImageView) findViewById(R.id.titleBackImage)).setOnClickListener(this);
        ((EditText) findViewById(R.id.hostnameText)).setOnFocusChangeListener(this);
        ((EditText) findViewById(R.id.hostIPText)).setOnFocusChangeListener(this);
        ((EditText) findViewById(R.id.portText)).setOnFocusChangeListener(this);
        ((EditText) findViewById(R.id.timeoutText)).setOnFocusChangeListener(this);
        ((Button) findViewById(R.id.cancel_host_btn)).setOnClickListener(this);
        ((Button) findViewById(R.id.save_host_btn)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.host_page)).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        globalData = GlobalData.getInstance();

        EditText hostnameEditText = (EditText) findViewById(R.id.hostnameText);
        hostnameEditText.setText(globalData.getCTMSHost(), TextView.BufferType.EDITABLE);
        EditText hostIPEditText = (EditText) findViewById(R.id.hostIPText);
        hostIPEditText.setText(globalData.getCTMSIP(), TextView.BufferType.EDITABLE);
        EditText portEditText = (EditText) findViewById(R.id.portText);
        portEditText.setText(String.valueOf(globalData.getCTMSPort()), TextView.BufferType.EDITABLE);
        EditText timeoutEditText = (EditText) findViewById(R.id.timeoutText);
        timeoutEditText.setText(String.valueOf(globalData.getCTMSTimeout()), TextView.BufferType.EDITABLE);
        CheckBox sslCheckbox = (CheckBox) findViewById(R.id.sslText);
        sslCheckbox.setChecked(globalData.getIfCTMSSSL());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.titleBackImage:
                finish();
                skipActivityAnim(-1);
                break;
            case R.id.host_page:
                InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                break;
            case R.id.cancel_host_btn:
                finish();
                skipActivityAnim(-1);
                break;
            case R.id.save_host_btn:
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
            host = ((EditText) findViewById(R.id.hostnameText)).getText().toString();
            ip = ((EditText) findViewById(R.id.hostIPText)).getText().toString();
            port = Integer.parseInt(((EditText) findViewById(R.id.portText)).getText().toString());
            timeout = Integer.parseInt(((EditText) findViewById(R.id.timeoutText)).getText().toString());
            isSSL = ((CheckBox) findViewById(R.id.sslText)).isChecked();

            globalData.setCTMSHost(host);
            globalData.setCTMSIP(ip);
            globalData.setCTMSPort(port);
            globalData.setCTMSTimeout(timeout);
            globalData.setIfCTMSSSL(isSSL);
            showToast("Saved TMS config!");
            finish();
            skipActivityAnim(-1);
        }
        catch(Exception ex){
            showToast("Error saving TMS config!");
        }
    }

}
