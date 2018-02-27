package com.avantir.wpos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.avantir.wpos.R;

/**
 * Created by lekanomotayo on 30/01/2018.
 */
public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);
        this.findViewById(R.id.setup_mgt_host_btn).setOnClickListener(this);
        this.findViewById(R.id.download_terminal_params_btn).setOnClickListener(this);
        this.findViewById(R.id.titleBackImage).setOnClickListener(this);
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
            case R.id.download_terminal_params_btn:
                Intent downloadTermParamsIntent = new Intent(this, DownloadTermParamsActivity.class);
                startActivity(downloadTermParamsIntent);
                break;
            case R.id.setup_mgt_host_btn:
                Intent hostConfigIntent = new Intent(this, HostConfigActivity.class);
                startActivity(hostConfigIntent);
                break;
        }
    }
}
