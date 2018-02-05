package com.xinhe.kakaxianjin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.xinhe.kakaxianjin.MyApplication;
import com.xinhe.kakaxianjin.R;
import com.xinhe.kakaxianjin.Utils.CheckUtil;
import com.xinhe.kakaxianjin.Utils.Constants;
import com.xinhe.kakaxianjin.Utils.DeviceUtil;
import com.xinhe.kakaxianjin.Utils.EncryptUtils;
import com.xinhe.kakaxianjin.Utils.ExceptionUtil;
import com.xinhe.kakaxianjin.Utils.LogcatUtil;
import com.xinhe.kakaxianjin.Utils.SPUtils;
import com.xinhe.kakaxianjin.bean.LoginMessage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.my_toolbar_tv)
    TextView myToolbarTv;
    @BindView(R.id.login_number)
    EditText loginNumber;
    @BindView(R.id.login_password)
    EditText loginPassword;
    @BindView(R.id.login_login)
    Button loginLogin;
    @BindView(R.id.forget_password_tv)
    TextView forgetPasswordTv;
    @BindView(R.id.redister_tv)
    TextView redisterTv;
    //手机号
    private String phone;
    //密码
    private String password;
    private KProgressHUD hd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            ButterKnife.bind(this);
            //初始化toobar
            initActionBar();
            //初始化
            setViews();
            //设置监听
            setListeners();
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }

    private void setListeners() {
        loginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()>5){
                    loginLogin.setClickable(true);
                    loginLogin.setBackgroundResource(R.drawable.login_btn_bg_selector);
                }else {
                    loginLogin.setClickable(false);
                    loginLogin.setBackgroundResource(R.drawable.login_btn_bg1);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setViews() {
        if (SPUtils.contains(this,"phone")) {
            loginNumber.setText((String)SPUtils.get(this,"phone",""));
        }
        loginLogin.setClickable(false);
    }

    public void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        myToolbarTv.setText("登录");
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @OnClick({R.id.login_login, R.id.forget_password_tv, R.id.redister_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_login:
                login();
                break;
            case R.id.forget_password_tv:
                startActivity(new Intent(this,ForgotPasswordActivity.class));
                break;
            case R.id.redister_tv:
                startActivity(new Intent(this,RegisterActivity.class));
                break;
        }
    }

    //登录
    private void login() {
        if(!DeviceUtil.IsNetWork(this)){
            Toast.makeText(this, "网络异常", Toast.LENGTH_LONG).show();
            return;
        }
        phone = loginNumber.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_LONG).show();
            return;
        }
        if (!CheckUtil.isMobile(phone)) {
            Toast.makeText(this, "手机号输入错误", Toast.LENGTH_LONG).show();
            return;
        }
        password = loginPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_LONG).show();
            return;
        }

        hd = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("登录中...")
                .setDimAmount(0.5f)
                .show();

        String password1 = EncryptUtils.encryptMD5ToString(password);
        Map<String,String> map=new HashMap<>();
        map.put("register_phone",phone);
        map.put("password",password1);
        map.put("terminal", Constants.channel);
        map.put("nid", Constants.channel1);
        JSONObject jsonObject=new JSONObject(map);



        OkGo.post(Constants.commonURL + Constants.login)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        LogcatUtil.printLogcat( s);
                        Gson gson = new Gson();
                        LoginMessage loginMessage = gson.fromJson(s, LoginMessage.class);

                        if (loginMessage.getError_code() == 0) {
                            String token = loginMessage.getData().getToken();
                            //缓存本地
                            SPUtils.put(LoginActivity.this,"token",token);
                            SPUtils.put(LoginActivity.this,"phone",phone);
                            //保存数据
                            MyApplication.isLogin = true;
                            MyApplication.token=token;
                            MyApplication.phone=phone;
                            // 发广播
                            Intent intent = new Intent();
                            intent.setAction(Constants.INTENT_EXTRA_LOGIN_SUCESS);
                            sendBroadcast(intent);

                            Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, loginMessage.getError_message(), Toast.LENGTH_LONG).show();

                        }
                        hd.dismiss();
                    }

                    @Override
                    public void onError(Call call, okhttp3.Response response, Exception e) {
                        Toast.makeText(LoginActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                        hd.dismiss();
                        super.onError(call, response, e);
                    }
                });

    }

}
