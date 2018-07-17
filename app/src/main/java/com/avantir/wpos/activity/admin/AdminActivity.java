package com.avantir.wpos.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import com.avantir.wpos.R;
import com.avantir.wpos.activity.BaseActivity;
import com.avantir.wpos.activity.MainMenuActivity;
import com.avantir.wpos.utils.ConstantUtils;
import wangpos.sdk4.libbasebinder.Printer;

/**
 * Created by lekanomotayo on 30/01/2018.
 */
public class AdminActivity extends BaseActivity {


    private Printer mPrinter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_admin);

        findViewById(R.id.titleBackImage).setVisibility(View.GONE);
        findViewById(R.id.titleSettingsImage).setVisibility(View.GONE);

        this.findViewById(R.id.call_home_btn).setOnClickListener(this);
        this.findViewById(R.id.print_config_btn).setOnClickListener(this);
        this.findViewById(R.id.network_param_btn).setOnClickListener(this);
        this.findViewById(R.id.download_keys_btn).setOnClickListener(this);
        this.findViewById(R.id.setup_mgt_host_btn).setOnClickListener(this);
        this.findViewById(R.id.download_terminal_params_btn).setOnClickListener(this);
        //this.findViewById(R.id.titleBackImage).setOnClickListener(this);

        new Thread() {
            @Override
            public void run() {
                mPrinter = new Printer(getApplicationContext());
            }
        }.start();

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.titleBackImage:
                finish();
                //finishAppActivity();
                skipActivityAnim(-1);
                break;
            case R.id.call_home_btn:
                Intent intent1 = new Intent(this, CallHomeConfigActivity.class);
                startActivity(intent1);
                break;
            case R.id.print_config_btn:
                if(performPrinterChecks()) {
                    Intent intent2 = new Intent(this, PrintConfigActivity.class);
                    startActivity(intent2);
                }
                break;
            case R.id.network_param_btn:
                Intent intent3 = new Intent(this, NetworkConfigActivity.class);
                startActivity(intent3);
                break;
            case R.id.download_keys_btn:
                Intent intentKeys = new Intent(this, DownloadKeysActivity.class);
                startActivity(intentKeys);
                break;
            case R.id.setup_mgt_host_btn:
                Intent hostConfigIntent = new Intent(this, HostConfigActivity.class);
                startActivity(hostConfigIntent);
                break;
            case R.id.download_terminal_params_btn:
                Intent downloadTermParamsIntent = new Intent(this, DownloadTermParamsActivity.class);
                startActivity(downloadTermParamsIntent);
                break;
        }
    }

    private boolean performPrinterChecks(){

        int ret = -1;
        // if there is paper in printer
        int[] status = new int[1];
        ret = -1;
        try {
            ret = mPrinter.getPrinterStatus(status);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if(ret != 0 || status[0] != 0){
            //Toast.makeText(this, "Check Printer (paper)", Toast.LENGTH_LONG).show();
            displayDialog("Printer not ready", ConstantUtils.MSG_BACK);
            return false;
        }
        return true;
    }


    protected void onBack(){
        //finish();
        //skipActivityAnim(-1);
        startActivity(new Intent(this, MainMenuActivity.class));
        finish();
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        onBack();
    }
}
