package com.xinhe.kakaxianjin.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xinhe.kakaxianjin.MyApplication;
import com.xinhe.kakaxianjin.R;
import com.xinhe.kakaxianjin.Utils.Constants;
import com.xinhe.kakaxianjin.Utils.ExceptionUtil;
import com.xinhe.kakaxianjin.fragment.LoanFragment;
import com.xinhe.kakaxianjin.fragment.MainFragment;
import com.xinhe.kakaxianjin.fragment.MeFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.main_activity_fragment_container)
    LinearLayout mainActivityFragmentContainer;
    @BindView(R.id.main_activity_buttom_btn1)
    Button mainActivityButtomBtn1;
    @BindView(R.id.main_activity_buttom_rl1)
    RelativeLayout mainActivityButtomRl1;
    @BindView(R.id.main_activity_buttom_btn2)
    Button mainActivityButtomBtn2;
    @BindView(R.id.main_activity_buttom_rl2)
    RelativeLayout mainActivityButtomRl2;
    @BindView(R.id.main_activity_buttom_btn3)
    Button mainActivityButtomBtn3;
    @BindView(R.id.main_activity_buttom_rl3)
    RelativeLayout mainActivityButtomRl3;
    @BindView(R.id.main_activity_buttom_ll)
    LinearLayout mainActivityButtomLl;

    //按钮集合
    private Button[] btnArray = new Button[3];
    //主页面
    private MainFragment main;
    //贷款页面
    private LoanFragment loan;
    //我的页面
    private MeFragment me;
    //fragment的集合
    private Fragment[] fragments;
    //导航按钮选择位置
    private int selectedIndex;
    //导航按钮当前位置
    private int currentIndex = 0;
    private long mLastBackTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        try {
            getPermission();
            //设置控件
            setViews();
            //设置监听
            setListener();
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }

    private void getPermission() {
        //权限检查
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE
                    },
                    Constants.PERMISSION_READ_PHONE_STATE);
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    Constants.PERMISSION_WRITE_EXTERNAL_STORAGE);
            return;
        }
    }

    private void setViews() {
        btnArray[0] = (Button) findViewById(R.id.main_activity_buttom_btn1);
        btnArray[1] = (Button) findViewById(R.id.main_activity_buttom_btn2);
        btnArray[2] = (Button) findViewById(R.id.main_activity_buttom_btn3);
        btnArray[0].setSelected(true);

        main = new MainFragment();
        loan = new LoanFragment();
        me = new MeFragment();
        fragments = new Fragment[]{main, loan, me};
        // 一开始，显示第一个fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.main_activity_fragment_container, main);
        transaction.show(main );
        transaction.commit();
    }

    private void setListener() {

    }

    @OnClick({R.id.main_activity_buttom_rl1, R.id.main_activity_buttom_rl2, R.id.main_activity_buttom_rl3})
    public void onclick(View view) {
        try {
            switch (view.getId()) {
                case R.id.main_activity_buttom_rl1:
                    selectedIndex = 0;
                    break;
                case R.id.main_activity_buttom_rl2:
                    selectedIndex = 1;
                    break;
                case R.id.main_activity_buttom_rl3:
                    selectedIndex = 2;
                    break;
            }
            if (!MyApplication.isLogin&&selectedIndex == 1){
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return;
            }
            // 判断单击是不是当前的
            if (selectedIndex != currentIndex) {
                // 不是当前的
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                // 当前hide
                transaction.hide(fragments[currentIndex]);
                // show你选中

                if (!fragments[selectedIndex].isAdded()) {
                    // 以前没添加过
                    transaction.add(R.id.main_activity_fragment_container, fragments[selectedIndex]);
                }
                // 事务
                transaction.show(fragments[selectedIndex]);
                transaction.commit();

                btnArray[currentIndex].setSelected(false);
                btnArray[selectedIndex].setSelected(true);
                currentIndex = selectedIndex;
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 102:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "获取手机状态权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
            case 103:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "手机储存权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
