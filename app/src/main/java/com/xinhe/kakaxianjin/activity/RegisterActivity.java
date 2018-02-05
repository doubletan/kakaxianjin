package com.xinhe.kakaxianjin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.xinhe.kakaxianjin.bean.MessageCode;
import com.xinhe.kakaxianjin.bean.RegisterMessage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.my_toolbar_tv)
    TextView myToolbarTv;
    @BindView(R.id.register_number)
    EditText registerNumber;
    @BindView(R.id.register_code)
    EditText registerCode;
    @BindView(R.id.register_password)
    EditText registerPassword;
    @BindView(R.id.register_repassword)
    EditText registerRepassword;
    @BindView(R.id.register_code_tv)
    TextView registerCodeTv;
    @BindView(R.id.register_btn)
    Button registerBtn;
    @BindView(R.id.register_cb)
    CheckBox registerCb;
    @BindView(R.id.register_agreement)
    TextView registerAgreement;
    //读秒计时
    private TimeCount time;
    //输入的手机号
    private String phone;
    //是否发送验证码
    private boolean isSend;
    //输入的验证码
    private String code;
    private String password;
    private String repassword;
    private KProgressHUD hd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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
        registerCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    registerBtn.setClickable(true);
                    registerBtn.setBackgroundResource(R.drawable.login_btn_bg_selector);
                } else {
                    registerBtn.setClickable(false);
                    registerBtn.setBackgroundResource(R.drawable.login_btn_bg1);
                }
            }
        });
    }


    private void setViews() {
        // 构造CountDownTimer对象
        time = new TimeCount(60000, 1000);
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

    @OnClick({R.id.register_code_tv, R.id.register_btn, R.id.register_agreement})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.register_code_tv:
                sendCode();
                break;
            case R.id.register_btn:
//                SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
//                pDialog.getProgressHelper().setBarColor(Color.parseColor("#ff6666"));
//                pDialog.setTitleText("登录中...");
//                pDialog.setCancelable(false);
//                pDialog.show();
                register();
                break;
            case R.id.register_agreement:
                Intent intent = new Intent(this, WebViewTitleActivity.class);
                intent.putExtra("url", Constants.registerAgreement);
                startActivity(intent);
                break;
        }
    }


    // 定义一个倒计时的内部类
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            registerCodeTv.setText("重新发送");
            registerCodeTv.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            registerCodeTv.setClickable(false);
            registerCodeTv.setText(millisUntilFinished / 1000 + "秒");
        }
    }


    //注册
    private void register() {
        if (!DeviceUtil.IsNetWork(this)) {
            Toast.makeText(this, "网络异常", Toast.LENGTH_LONG).show();
            return;
        }
        phone = registerNumber.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_LONG).show();
            return;
        }
        if (!CheckUtil.isMobile(phone)) {
            Toast.makeText(this, "手机号输入错误", Toast.LENGTH_LONG).show();
            return;
        }
        if (!isSend) {
            Toast.makeText(this, "请获取验证码", Toast.LENGTH_LONG).show();
            return;
        }
        code = registerCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_LONG).show();
            return;
        }
        password = registerPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请设置密码", Toast.LENGTH_LONG).show();
            return;
        }
        if (password.length()<6) {
            Toast.makeText(this, "密码至少设置6位", Toast.LENGTH_LONG).show();
            return;
        }
        repassword = registerRepassword.getText().toString().trim();
        if (TextUtils.isEmpty(repassword)) {
            Toast.makeText(this, "请确认密码", Toast.LENGTH_LONG).show();
            return;
        }
        if (!password.equals(repassword)) {
            Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_LONG).show();
            return;
        }

        hd = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("注册中...")
                .setDimAmount(0.5f)
                .show();

        String password1 = EncryptUtils.encryptMD5ToString(password);
        String repassword1 = EncryptUtils.encryptMD5ToString(repassword);
        Map<String, String> map = new HashMap<>();
        map.put("register_phone", phone);
        map.put("code", code);
        map.put("password", password1);
        map.put("repassword", repassword1);
        map.put("terminal", Constants.channel);
        map.put("nid", Constants.channel1);
        JSONObject jsonObject = new JSONObject(map);


        OkGo.post(Constants.commonURL + Constants.register)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LogcatUtil.printLogcat(s);
                        Gson gson = new Gson();
                        RegisterMessage registerMessage = gson.fromJson(s, RegisterMessage.class);

                        if (registerMessage.getError_code() == 0) {
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, registerMessage.getError_message(), Toast.LENGTH_LONG).show();

                        }
                        hd.dismiss();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Toast.makeText(RegisterActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                        hd.dismiss();
                        super.onError(call, response, e);
                    }
                });

    }

    //发送验证码
    private void sendCode() {
        if (!DeviceUtil.IsNetWork(this)) {
            Toast.makeText(this, "网络异常", Toast.LENGTH_LONG).show();
            return;
        }
        phone = registerNumber.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_LONG).show();
            return;
        }
        if (!CheckUtil.isMobile(phone)) {
            Toast.makeText(this, "手机号输入错误", Toast.LENGTH_LONG).show();
            return;
        }


        hd = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("发送中...")
                .setDimAmount(0.5f)
                .show();

        Map<String, String> map = new HashMap<>();
        map.put("register_phone", phone);
        JSONObject jsonObject = new JSONObject(map);


        OkGo.post(Constants.commonURL + Constants.sendCode)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LogcatUtil.printLogcat(s);
                        Gson gson = new Gson();
                        MessageCode messageCode = gson.fromJson(s, MessageCode.class);

                        if (messageCode.getError_code() == 0) {
                            isSend = true;
                            // 开始计时
                            time.start();
                            Toast.makeText(RegisterActivity.this, "验证码发送成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, messageCode.getError_message(), Toast.LENGTH_LONG).show();

                        }
                        hd.dismiss();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Toast.makeText(RegisterActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                        hd.dismiss();
                        super.onError(call, response, e);
                    }
                });
    }
}
