package com.xinhe.kakaxianjin.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.xinhe.kakaxianjin.MyApplication;
import com.xinhe.kakaxianjin.R;
import com.xinhe.kakaxianjin.Utils.ExceptionUtil;
import com.xinhe.kakaxianjin.Utils.SPUtils;

import cn.bingoogolapple.bgabanner.BGABanner;

public class GuideActivity extends BaseActivity {

    private BGABanner mBackgroundBanner;
    private long mLastBackTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        try {
            //初适化
            setView();
            //设置监听
            setListener();
            //配置图片
            processLogic();
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }

    private void setView() {
        mBackgroundBanner = (BGABanner) findViewById(R.id.banner_guide_background);
    }
    private void setListener() {
        /**
         * 设置进入按钮和跳过按钮控件资源 id 及其点击事件
         * 如果进入按钮和跳过按钮有一个不存在的话就传 0
         * 在 BGABanner 里已经帮开发者处理了防止重复点击事件
         * 在 BGABanner 里已经帮开发者处理了「跳过按钮」和「进入按钮」的显示与隐藏
         */
        mBackgroundBanner.setEnterSkipViewIdAndDelegate(R.id.btn_guide_enter, R.id.tv_guide_skip, new BGABanner.GuideDelegate() {
            @Override
            public void onClickEnterOrSkip() {
                Intent intent=new Intent(GuideActivity.this, MainActivity.class);
                startActivity(intent);
                SPUtils.put(GuideActivity.this,"isFirst","1");
                finish();
            }
        });
    }
    private void processLogic() {
        // 设置数据源
        mBackgroundBanner.setData(R.mipmap.lod_01, R.mipmap.lod_02, R.mipmap.lod_03);

    }

    @Override
    public void onBackPressed() {
        // finish while click back key 2 times during 1s.
        if ((System.currentTimeMillis() - mLastBackTime) < 2000) {
            finish();
            MyApplication.getApp().onTerminate();
        } else {
            mLastBackTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
        }
    }

}
