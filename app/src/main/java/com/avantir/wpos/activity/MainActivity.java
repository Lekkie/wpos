package com.avantir.wpos.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
//import android.view.View;
//import com.avantir.wpos.R;
import com.avantir.wpos.activity.admin.DownloadTermParamsActivity;
//import com.avantir.wpos.utils.ConstantUtils;
import com.avantir.wpos.utils.GlobalData;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final GlobalData globalData = GlobalData.getInstance();
        boolean firstLaunch = globalData.getIfFirstLaunch();
        boolean demoMode = globalData.isDemoMode();

        if(firstLaunch && !demoMode){

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            globalData.setDemoMode(false);
                            Intent downloadTermParamsIntent = new Intent(getBaseContext(), DownloadTermParamsActivity.class);
                            startActivity(downloadTermParamsIntent);
                            finish();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            globalData.setDemoMode(true);
                            Intent mainMenu2 = new Intent(getBaseContext(), MainMenuActivity.class);
                            startActivity(mainMenu2);
                            finish();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Transaction Mode").setPositiveButton("Live", dialogClickListener)
                    .setNegativeButton("Demo", dialogClickListener).show();

        }
        else{
            Intent mainMenu = new Intent(this, MainMenuActivity.class);
            startActivity(mainMenu);
            finish();
        }
    }

}
