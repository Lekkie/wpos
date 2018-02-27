package com.avantir.wpos.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.avantir.wpos.R;
import com.avantir.wpos.utils.KeyUtils;
import wangpos.sdk4.libkeymanagerbinder.Key;

/**
 * Created by lekanomotayo on 30/01/2018.
 */
public class LoadBDKActivity extends BaseActivity {

    private TextView key1Text, key2Text;
    private Bundle bundle;
    private String bdk = "";
    private String kcv = "";
    private String ksn = "";
    Key mKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_input_keys);

        //findViewById(R.id.titleBackImage).setVisibility(View.GONE);
        //findViewById(R.id.titleSettingsImage).setVisibility(View.GONE);

        new Thread() {
            @Override
            public void run() {
                mKey = new Key(getApplicationContext());
            }
        }.start();

        this.findViewById(R.id.titleBackImage).setOnClickListener(this);
        findViewById(R.id.titleSettingsImage).setVisibility(View.GONE);

        super.onCreate(savedInstanceState);
    }

    protected void initView() {
        //Top Title bar
        ImageView titleBackImage = (ImageView) findViewById(R.id.titleBackImage);
        titleBackImage.setOnClickListener(this);
        TextView titleNameText = (TextView) findViewById(R.id.titleNameText);
        titleNameText.setText("Load BDK");

        key1Text = (TextView) findViewById(R.id.key1);
        key2Text = (TextView) findViewById(R.id.key2);

        //Button btn_num00 = (Button) findViewById(R.id.btn_num00);
        //btn_num00.setOnClickListener(this);
        Button btn_num0 = (Button) findViewById(R.id.btn_num0);
        btn_num0.setOnClickListener(this);
        Button btn_num1 = (Button) findViewById(R.id.btn_num1);
        btn_num1.setOnClickListener(this);
        Button btn_num2 = (Button) findViewById(R.id.btn_num2);
        btn_num2.setOnClickListener(this);
        Button btn_num3 = (Button) findViewById(R.id.btn_num3);
        btn_num3.setOnClickListener(this);
        Button btn_num4 = (Button) findViewById(R.id.btn_num4);
        btn_num4.setOnClickListener(this);
        Button btn_num5 = (Button) findViewById(R.id.btn_num5);
        btn_num5.setOnClickListener(this);
        Button btn_num6 = (Button) findViewById(R.id.btn_num6);
        btn_num6.setOnClickListener(this);
        Button btn_num7 = (Button) findViewById(R.id.btn_num7);
        btn_num7.setOnClickListener(this);
        Button btn_num8 = (Button) findViewById(R.id.btn_num8);
        btn_num8.setOnClickListener(this);
        Button btn_num9 = (Button) findViewById(R.id.btn_num9);
        btn_num9.setOnClickListener(this);
        Button btn_num_a = (Button) findViewById(R.id.btn_num_a);
        btn_num_a.setOnClickListener(this);
        Button btn_num_b = (Button) findViewById(R.id.btn_num_b);
        btn_num_b.setOnClickListener(this);
        Button btn_num_c = (Button) findViewById(R.id.btn_num_c);
        btn_num_c.setOnClickListener(this);
        Button btn_num_d = (Button) findViewById(R.id.btn_num_d);
        btn_num_d.setOnClickListener(this);
        Button btn_num_e = (Button) findViewById(R.id.btn_num_e);
        btn_num_e.setOnClickListener(this);
        Button btn_num_f = (Button) findViewById(R.id.btn_num_f);
        btn_num_f.setOnClickListener(this);

        //Clear
        ImageView btn_num_clear = (ImageView) findViewById(R.id.btn_num_clear);
        btn_num_clear.setOnClickListener(this);

        //Delete
        ImageView btn_num_delete = (ImageView) findViewById(R.id.btn_num_delete);
        btn_num_delete.setOnClickListener(this);

        //Load
        Button generate_btn = (Button) findViewById(R.id.generate_btn);
        generate_btn.setOnClickListener(this);
    }

    protected void initData() {
        bundle = getIntent().getExtras();
        if(bundle == null)
        {
            bundle = new Bundle();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.titleBackImage:
                finish();
                //finishAppActivity();
                skipActivityAnim(-1);
                break;

            case R.id.btn_num0:
            case R.id.btn_num1:
            case R.id.btn_num2:
            case R.id.btn_num3:
            case R.id.btn_num4:
            case R.id.btn_num5:
            case R.id.btn_num6:
            case R.id.btn_num7:
            case R.id.btn_num8:
            case R.id.btn_num9:
            case R.id.btn_num_a:
            case R.id.btn_num_b:
            case R.id.btn_num_c:
            case R.id.btn_num_d:
            case R.id.btn_num_e:
            case R.id.btn_num_f:
                if(bdk != null && bdk.length() < 32){
                    bdk = bdk + ((Button) v).getText().toString();
                    inputMoneySetText();
                }
                break;

            case R.id.btn_num_delete:
                if(!bdk.isEmpty()){
                    bdk = bdk.substring(0, bdk.length() - 1);
                    inputMoneySetText();
                }
                break;

            case R.id.btn_num_clear:
                bdk = "";
                key1Text.setText("");
                key2Text.setText("");
                break;
            case R.id.generate_btn:
                int res = loadCTMK();
                if(res == 0){
                    finish();
                    //finishAppActivity();
                    skipActivityAnim(-1); //go back
                }
                break;
            default:
                break;
        }
    }



    private void inputMoneySetText()
    {
        if(bdk.length() < 17)
            key2Text.setText(bdk);
        else
            key1Text.setText(bdk.substring(16));
    }


    private int loadCTMK(){
        bdk = "DBEECACCB4210977ACE73A1D873CA59F"; // NIBSS test BDK
        if(bdk != null && bdk.length() == 32){
            try {
                int res = KeyUtils.saveBDK(mKey, bdk, kcv, ksn);
                if(res == 0)
                    showToast("load dukpt Key Success!");
                else
                    showToast("load protect Key error!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            showToast("Invalid key length!");
        }
        return 1;
    }


}
