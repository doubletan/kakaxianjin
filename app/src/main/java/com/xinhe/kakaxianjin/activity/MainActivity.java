package com.xinhe.kakaxianjin.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.xinhe.kakaxianjin.MyApplication;
import com.xinhe.kakaxianjin.R;
import com.xinhe.kakaxianjin.Utils.Constants;
import com.xinhe.kakaxianjin.Utils.ExceptionUtil;
import com.xinhe.kakaxianjin.Utils.LogcatUtil;
import com.xinhe.kakaxianjin.bean.CardList;
import com.xinhe.kakaxianjin.fragment.LoanFragment;
import com.xinhe.kakaxianjin.fragment.MainFragment;
import com.xinhe.kakaxianjin.fragment.MeFragment;
import com.xinhe.kakaxianjin.view.update.AppUpdateUtils;
import com.xinhe.kakaxianjin.view.update.CProgressDialogUtils;
import com.xinhe.kakaxianjin.view.update.OkGoUpdateHttpUtil;
import com.xinhe.kakaxianjin.view.update.UpdateAppBean;
import com.xinhe.kakaxianjin.view.update.UpdateAppManager;
import com.xinhe.kakaxianjin.view.update.UpdateCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

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

    /**
     * 更新
     */
    private int NewVersionCode=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        try {
//            // 4.4版本以上设置 全屏显示，状态栏在界面上方
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                // 透明状态栏
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                // 透明导航栏
//                // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            }
            getPermission();
            //设置控件
            setViews();
            //设置监听
            setListener();

            //更新
            update();
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

        //切换状态栏颜色
        changeStatebarColor(selectedIndex);
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
                //切换状态栏颜色
                changeStatebarColor(selectedIndex);
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }


    /**
     * 版本升级
     */
    public void update() {
        NewVersionCode = AppUpdateUtils.getVersionCode(this);
        Map<String, String> params = new HashMap<String, String>();
//        params.put("channel", Constants.channel1);
        params.put("versioncode", ""+NewVersionCode);
        new UpdateAppManager
                .Builder()
                .setActivity(this)
                //必须设置，实现httpManager接口的对象
                .setHttpManager(new OkGoUpdateHttpUtil())
                //必须设置，更新地址
                .setUpdateUrl(Constants.commonURL+Constants.update)
                //以下设置，都是可选
                .setPost(true)
                //不显示通知栏进度条
//                .dismissNotificationProgress()
                //是否忽略版本
//                .showIgnoreVersion()
                //添加自定义参数，默认version=1.0.0（app的versionName）；apkKey=唯一表示（在AndroidManifest.xml配置）
                .setParams(params)
                //设置点击升级后，消失对话框，默认点击升级后，对话框显示下载进度
                .hideDialogOnDownloading(false)
                //设置头部，不设置显示默认的图片，设置图片后自动识别主色调，然后为按钮，进度条设置颜色
                //为按钮，进度条设置颜色。
                //.setThemeColor(0xffffac5d)
                //设置apk下砸路径，默认是在下载到sd卡下/Download/1.0.0/test.apk
//                .setTargetPath(path)
                //设置appKey，默认从AndroidManifest.xml获取，如果，使用自定义参数，则此项无效
//                .setAppKey("ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f")
                .build()
                //检测是否有新版本
                .checkNewApp(new UpdateCallback() {
                    /**
                     * 解析json,自定义协议
                     *
                     * @param json 服务器返回的json
                     * @return UpdateAppBean
                     */
                    @Override
                    protected UpdateAppBean parseJson(String json) {

                        UpdateAppBean updateAppBean = new UpdateAppBean();

                        try {
                            JSONObject jsonObject1 = new JSONObject(json);
                            if (0==jsonObject1.getInt("error_code")){
                                JSONObject jsonObject = jsonObject1.getJSONObject("data").getJSONObject("version");
                                int size = jsonObject.getInt("size");
                                Double i = (double) size / 1024;
                                DecimalFormat df = new DecimalFormat("0.0");
                                String format = df.format(i);
                                int versioncode = jsonObject.getInt("versioncode");

                                String update="No";
                                if(versioncode>NewVersionCode){
                                    update="Yes";
                                }
                                updateAppBean
                                        //（必须）是否更新Yes,No
                                        .setUpdate(update)
                                        //（必须）新版本号，
                                        .setNewVersion(jsonObject.getString("versionname"))
                                        //（必须）下载地址
                                        .setApkFileUrl(jsonObject.getString("url"))
                                        //测试下载路径是重定向路径
//                                    .setApkFileUrl("http://openbox.mobilem.360.cn/index/d/sid/3282847")
                                        //（必须）更新内容
//                                    .setUpdateLog(jsonObject.optString("update_log"))
                                        //测试内容过度
//                                    .setUpdateLog("测试")
                                        .setUpdateLog(jsonObject.getString("updatecontent"))
//                                    .setUpdateLog("今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说\r\n")
                                        //大小，不设置不显示大小，可以不设置
                                        .setTargetSize(String.valueOf(format)+"M")
                                        //是否强制更新，可以不设置
                                        .setConstraint((jsonObject.getInt("is_force")==1)?true:false)
                                        //设置md5，可以不设置
                                        .setNewMd5(jsonObject.getString("md5"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return updateAppBean;
                    }

                    @Override
                    protected void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {
                        updateAppManager.showDialogFragment();
                    }
                    /**
                     * 网络请求之前
                     */
                    @Override
                    public void onBefore() {
                        CProgressDialogUtils.showProgressDialog(MainActivity.this);

                    }
                    /**
                     * 网路请求之后
                     */
                    @Override
                    public void onAfter() {
                        CProgressDialogUtils.cancelProgressDialog(MainActivity.this);

                    }
                });



    }


    private void changeStatebarColor(int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (0==position){
                window.setStatusBarColor(getResources().getColor(R.color.common_red));
            }else {
                window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            //底部导航栏
            //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
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
