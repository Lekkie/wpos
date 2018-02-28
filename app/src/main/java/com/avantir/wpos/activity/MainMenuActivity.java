package com.avantir.wpos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Toast;
import com.avantir.wpos.R;
import com.avantir.wpos.utils.ConstantUtils;
import com.avantir.wpos.utils.GlobalData;
import wangpos.sdk4.libbasebinder.Printer;
import wangpos.sdk4.libkeymanagerbinder.Key;

/**
 * Created by lekanomotayo on 28/02/2018.
 */
public class MainMenuActivity extends BaseActivity {

    private Key mKey;
    private Printer mPrinter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setActionBar();

        new Thread() {
            @Override
            public void run() {
                mKey = new Key(getApplicationContext());
                mPrinter = new Printer(getApplicationContext());
            }
        }.start();


        findViewById(R.id.titleBackImage).setVisibility(View.GONE);
        this.findViewById(R.id.purchase_btn).setOnClickListener(this);
        this.findViewById(R.id.titleSettingsImage).setOnClickListener(this);
    }


    protected void initView() {
        setTitle("Main");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.titleSettingsImage:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.purchase_btn:
                if(performTransactionChecks()){
                    Intent purchaseIntent = new Intent(this, InputMoneyActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt(ConstantUtils.TRAN_TYPE, ConstantUtils.PURCHASE);
                    purchaseIntent.putExtras(bundle);
                    startActivity(purchaseIntent);
                }
                break;
        }
    }

    public void setActionBar() {
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    private boolean performTransactionChecks(){
        try{
            GlobalData globalData = GlobalData.getInstance();
            // if keys are loaded (CTMK, TMK, TSK, TPK, IPEK Track 2, IPEK EMV)
            int ret = -1;
            try {
                ret = mKey.checkKeyExist(ConstantUtils.APP_NAME, Key.KEY_REQUEST_TLK);
                if (ret != 0 || !globalData.getLocalMasterKeyLoadedFlag()) {
                    Toast.makeText(this, "Ensure TLK is loaded", Toast.LENGTH_LONG).show();
                    return false;
                }
                ret = mKey.checkKeyExist(ConstantUtils.APP_NAME, Key.KEY_REQUEST_TMK);
                if (ret != 0 || !globalData.getTMKKeyFlag()) {
                    Toast.makeText(this, "Ensure TMK is loaded", Toast.LENGTH_LONG).show();
                    return false;
                }
                ret = mKey.checkKeyExist(ConstantUtils.APP_NAME, Key.KEY_REQUEST_MAK);
                if (ret != 0 || !globalData.getMacKeyFlag()) {
                    Toast.makeText(this, "Ensure MAK is loaded", Toast.LENGTH_LONG).show();
                    return false;
                }
                ret = mKey.checkKeyExist(ConstantUtils.APP_NAME, Key.KEY_REQUEST_PEK);
                if (ret != 0 || !globalData.getPinKeyFlag()) {
                    Toast.makeText(this, "Ensure PEK is loaded", Toast.LENGTH_LONG).show();
                    return false;
                }

                if(!(globalData.getKeysLoadedFlag())){
                    Toast.makeText(this, "Ensure all Keys are loaded", Toast.LENGTH_LONG).show();
                    return false;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error while checking loaded keys", Toast.LENGTH_LONG).show();
                return false;
            }


            // if EMV params are laoded (AID, CAPK, EMV, Term Params)
            if(!(globalData.getLogin() && globalData.getAIDLoadedFlag()
                    && globalData.getCAPKLoadedFlag() && globalData.getEMVParamsLoadedFlag()
                    && globalData.getTerminalParamsLoadedFlag())){
                Toast.makeText(this, "Ensure Terminal Parameters are loaded", Toast.LENGTH_LONG).show();
                return false;
            }


            // if there is paper in printer
            int[] status = new int[1];
            ret = -1;
            try {
                ret = mPrinter.getPrinterStatus(status);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if(ret != 0 || status[0] != 0){
                Toast.makeText(this, "Check Printer (paper)", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }

        return true;
    }




}
