package com.xinhe.kakaxianjin.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.xinhe.kakaxianjin.MyApplication;
import com.xinhe.kakaxianjin.R;
import com.xinhe.kakaxianjin.Utils.DeviceUtil;
import com.xinhe.kakaxianjin.Utils.SPUtils;
import com.xinhe.kakaxianjin.biz.GetCreditProduct;

import java.lang.ref.WeakReference;

public class LaunchActivity extends BaseActivity {


    private SwitchHandler mHandler= new SwitchHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        //获取数据
        getData();
        setView();
    }

    private void getData() {
        if(DeviceUtil.IsNetWork(this)){
            //执行相关操作
            new GetCreditProduct(this).execute();
        }else {
            Toast.makeText(this,"网络异常",Toast.LENGTH_LONG).show();
        }
    }

    private void setView() {
        if (SPUtils.contains(this,"token")){
            //保存数据
            MyApplication.isLogin = true;
            String token = (String) SPUtils.get(LaunchActivity.this, "token", "");
            MyApplication.token=token;
            String phone = (String) SPUtils.get(LaunchActivity.this, "phone", "");
            MyApplication.phone=phone;
        };
        boolean isFirst = SPUtils.contains(this, "isFirst");
        if (isFirst){
            mHandler.sendEmptyMessageDelayed(2,2000);
        }else {
            mHandler.sendEmptyMessageDelayed(1,2000);
        }
    }

    private class SwitchHandler extends Handler {
        private WeakReference<LaunchActivity> mWeakReference;

        SwitchHandler(LaunchActivity activity) {
            mWeakReference = new WeakReference<LaunchActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LaunchActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what){
                    case 1:
                        Intent intent=new Intent(LaunchActivity.this,GuideActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        Intent intent1=new Intent(activity,MainActivity.class);
                        startActivity(intent1);
                        break;
                }
                finish();
            }

        }
    }

    @Override
    public void onBackPressed() {
       //这里不处理，防止用户启动时按返回
    }
}
