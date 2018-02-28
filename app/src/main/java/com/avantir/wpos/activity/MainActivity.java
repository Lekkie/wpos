package com.avantir.wpos.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Toast;
import com.avantir.wpos.R;
import com.avantir.wpos.utils.ConstantUtils;
import com.avantir.wpos.utils.GlobalData;
import com.avantir.wpos.utils.IsoMessageUtil;
import wangpos.sdk4.libbasebinder.Printer;
import wangpos.sdk4.libkeymanagerbinder.Key;

public class MainActivity extends Activity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //initView();

        boolean firstLaunch = GlobalData.getInstance().getIfFirstLaunch();
        if(firstLaunch){
            Intent downloadTermParamsIntent = new Intent(this, DownloadTermParamsActivity.class);
            startActivity(downloadTermParamsIntent);
        }
        else{
            Intent mainMenu = new Intent(this, MainMenuActivity.class);
            startActivity(mainMenu);
            finish();
        }

    }



}
