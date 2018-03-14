package com.avantir.wpos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.avantir.wpos.R;
import com.avantir.wpos.dao.TransInfoDao;
import com.avantir.wpos.model.TransInfo;
import com.avantir.wpos.utils.ConstantUtils;
import com.avantir.wpos.utils.GlobalData;
import com.avantir.wpos.utils.StringUtil;

/**
 * Created by lekanomotayo on 24/01/2018.
 */

public class TranSequenceNrActivity extends BaseActivity implements  View.OnFocusChangeListener{

    private String TAG = "TranSequenceNrActivity";

    int timeout = 0;
    GlobalData globalData;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_trans_seq_nr);

        findViewById(R.id.titleBackImage).setVisibility(View.GONE);
        findViewById(R.id.titleSettingsImage).setVisibility(View.GONE);

        super.onCreate(savedInstanceState);
    }

    //@Override
    protected void initView() {
        //Top title bar
        ((ImageView) findViewById(R.id.titleBackImage)).setOnClickListener(this);
        ((EditText) findViewById(R.id.transSeqNrText)).setOnFocusChangeListener(this);
        ((Button) findViewById(R.id.cancel_trans_seq_nr_btn)).setOnClickListener(this);
        ((Button) findViewById(R.id.next_trans_seq_nr_btn)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.trans_seq_nr_page)).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        globalData = GlobalData.getInstance();

        //EditText timeoutEditText = (EditText) findViewById(R.id.transSeqNrText);
        //timeoutEditText.setText(String.valueOf(globalData.getCallHomePeriodInMin()), TextView.BufferType.EDITABLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.titleBackImage:
                finish();
                skipActivityAnim(-1);
                break;
            case R.id.trans_seq_nr_page:
                InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                break;
            case R.id.cancel_trans_seq_nr_btn:
                finish();
                skipActivityAnim(-1);
                break;
            case R.id.next_trans_seq_nr_btn:
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
            long seqNrLong = Integer.parseInt(((EditText) findViewById(R.id.transSeqNrText)).getText().toString());

            TransInfoDao transInfoDao = new TransInfoDao(this);
            String seqNr = StringUtil.leftPad(String.valueOf(seqNrLong), 6, '0'); // 32 - 6 = 26 // "000029"
            TransInfo transInfo = transInfoDao.findByStan(String.valueOf(seqNr));

            //seqNr = StringUtil.leftPad(String.valueOf(seqNrLong), 12, '0'); // 32 - 6 = 26 // "000029"
            //transInfo = transInfo == null ? transInfoDao.findByRetRefNo(String.valueOf(seqNr)) : transInfo;

            if(transInfo == null)
                throw new Exception();

            Intent intent = new Intent(this, InsertCardActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt(ConstantUtils.TRAN_TYPE, ConstantUtils.REFUND);
            bundle.putSerializable(ConstantUtils.TRANS_INFO, transInfo);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }
        catch(NumberFormatException ex){
            displayDialog("Invalid sequence number!");
        }
        catch(Exception ex){
            displayDialog("Cannot find Transaction Sequence Number!");
        }
    }

    private void back(){
        //finish();
        //skipActivityAnim(-1);
        startActivity(new Intent(this, MainMenuActivity.class));
        finish();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        back();
    }

}
