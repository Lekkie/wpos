package com.avantir.wpos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.avantir.wpos.R;

/**
 * Created by lekanomotayo on 30/01/2018.
 */
public class AdminActivity extends BaseActivity {

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
                Intent intent2 = new Intent(this, PrintConfigActivity.class);
                startActivity(intent2);
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
}
