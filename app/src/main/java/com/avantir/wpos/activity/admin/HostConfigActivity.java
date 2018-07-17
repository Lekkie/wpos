package com.avantir.wpos.activity.admin;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.avantir.wpos.R;
import com.avantir.wpos.activity.BaseActivity;
import com.avantir.wpos.utils.GlobalData;

/**
 * Created by lekanomotayo on 24/01/2018.
 */

public class HostConfigActivity extends BaseActivity implements  View.OnFocusChangeListener{

    private String TAG = "HostConfigActivity";

    private TextView statusText;
    private String info;
    private Bundle bundle;

    String host = "";
    int port = 0;
    int timeout = 0;
    boolean isSSL = false;
    boolean isDemoMode = false;
    String demoResponseCode = "00";
    GlobalData globalData;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_host_config);

        findViewById(R.id.titleBackImage).setVisibility(View.GONE);
        findViewById(R.id.titleSettingsImage).setVisibility(View.GONE);

        super.onCreate(savedInstanceState);
    }

    //@Override
    protected void initView() {
        //Top title bar
        ((ImageView) findViewById(R.id.titleBackImage)).setOnClickListener(this);
        ((EditText) findViewById(R.id.hostText)).setOnFocusChangeListener(this);
        ((EditText) findViewById(R.id.portText)).setOnFocusChangeListener(this);
        ((EditText) findViewById(R.id.timeoutText)).setOnFocusChangeListener(this);
        ((EditText) findViewById(R.id.demoResponseCodeText)).setOnFocusChangeListener(this);
        ((Button) findViewById(R.id.cancel_host_btn)).setOnClickListener(this);
        ((Button) findViewById(R.id.save_host_btn)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.host_page)).setOnClickListener(this);

        ((CheckBox) findViewById(R.id.demoText)).setOnClickListener(this);

        if(!((CheckBox) findViewById(R.id.demoText)).isChecked()){
            ((EditText) findViewById(R.id.demoResponseCodeText)).setEnabled(false);
        }
    }

    @Override
    protected void initData() {
        globalData = GlobalData.getInstance();

        EditText hostEditText = (EditText) findViewById(R.id.hostText);
        hostEditText.setText(globalData.getTMSHost(), TextView.BufferType.EDITABLE);
        EditText portEditText = (EditText) findViewById(R.id.portText);
        portEditText.setText(String.valueOf(globalData.getTMSPort()), TextView.BufferType.EDITABLE);
        EditText timeoutEditText = (EditText) findViewById(R.id.timeoutText);
        timeoutEditText.setText(String.valueOf(globalData.getTMSTimeout()), TextView.BufferType.EDITABLE);
        CheckBox sslCheckbox = (CheckBox) findViewById(R.id.sslText);
        sslCheckbox.setChecked(globalData.getIfTMSSSL());
        EditText demoResponseCodeEditText = (EditText) findViewById(R.id.demoResponseCodeText);
        demoResponseCodeEditText.setText(String.valueOf(globalData.getDemoResponseCode()), TextView.BufferType.EDITABLE);
        CheckBox demoCheckbox = (CheckBox) findViewById(R.id.demoText);
        demoCheckbox.setChecked(globalData.isDemoMode());
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
            case R.id.demoText:
                ((EditText) findViewById(R.id.demoResponseCodeText)).setEnabled(((CheckBox) findViewById(R.id.demoText)).isChecked());
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
            host = ((EditText) findViewById(R.id.hostText)).getText().toString();
            port = Integer.parseInt(((EditText) findViewById(R.id.portText)).getText().toString());
            timeout = Integer.parseInt(((EditText) findViewById(R.id.timeoutText)).getText().toString());
            isSSL = ((CheckBox) findViewById(R.id.sslText)).isChecked();
            demoResponseCode = ((EditText) findViewById(R.id.demoResponseCodeText)).getText().toString();
            isDemoMode = ((CheckBox) findViewById(R.id.demoText)).isChecked();

            globalData.setTMSHost(host);
            globalData.setTMSPort(port);
            globalData.setDemoMode(isDemoMode);
            globalData.setDemoResponseCode(demoResponseCode);
            showToast("Saved host config!");
            finish();
            skipActivityAnim(-1);
        }
        catch(Exception ex){
            showToast("Error saving host config!");
        }
    }

}
