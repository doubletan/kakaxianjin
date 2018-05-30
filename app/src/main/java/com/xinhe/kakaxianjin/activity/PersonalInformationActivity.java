package com.xinhe.kakaxianjin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xinhe.kakaxianjin.MyApplication;
import com.xinhe.kakaxianjin.R;
import com.xinhe.kakaxianjin.Utils.Constants;
import com.xinhe.kakaxianjin.Utils.ExceptionUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonalInformationActivity extends BaseActivity {

    @BindView(R.id.my_toolbar_tv)
    TextView myToolbarTv;
    @BindView(R.id.forgot_password_tv2)
    TextView forgotPasswordTv2;
    @BindView(R.id.forgot_password_rl3)
    RelativeLayout forgotPasswordRl3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
        try {
            ButterKnife.bind(this);
            //初始化toobar
            initActionBar();
            //初始化
            setViews();
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }

    private void setViews() {
        if (!TextUtils.isEmpty(MyApplication.phone)){
            forgotPasswordTv2.setText(MyApplication.phone);
        }
    }

    public void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        myToolbarTv.setText("个人信息");
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick(R.id.forgot_password_rl3)
    public void onViewClicked() {
        startActivityForResult(new Intent(this, ModifyPasswordActivity.class), Constants.PERSONALINFORMATIONACTIVITY_TO_MODIFYPASSWORD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 121:
                if (resultCode==-1){
                    finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
