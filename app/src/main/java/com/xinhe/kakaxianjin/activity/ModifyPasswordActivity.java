package com.xinhe.kakaxianjin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.xinhe.kakaxianjin.Utils.Constants;
import com.xinhe.kakaxianjin.Utils.DeviceUtil;
import com.xinhe.kakaxianjin.Utils.EncryptUtils;
import com.xinhe.kakaxianjin.Utils.ExceptionUtil;
import com.xinhe.kakaxianjin.Utils.LogcatUtil;
import com.xinhe.kakaxianjin.bean.ModifyPasswordMessage;
import com.xinhe.kakaxianjin.bean.RegisterMessage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.xinhe.kakaxianjin.MyApplication.phone;

public class ModifyPasswordActivity extends BaseActivity {

    @BindView(R.id.my_toolbar_tv)
    TextView myToolbarTv;
    @BindView(R.id.modify_old_password)
    EditText modifyOldPassword;
    @BindView(R.id.modify_new_password)
    EditText modifyNewPassword;
    @BindView(R.id.modify_new_repassword)
    EditText modifyNewRepassword;
    @BindView(R.id.modify_password_btn)
    Button modifyPasswordBtn;
    private String oldPassword;
    private String newPassword;
    private String newRepassword;
    private KProgressHUD hd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        try {
            ButterKnife.bind(this);
            //初始化toobar
            initActionBar();
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }

    public void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        myToolbarTv.setText("修改密码");
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick(R.id.modify_password_btn)
    public void onViewClicked() {
        modify();
    }

    //修改密码
    private void modify() {
        if (!DeviceUtil.IsNetWork(this)) {
            Toast.makeText(this, "网络异常", Toast.LENGTH_LONG).show();
            return;
        }
        oldPassword = modifyOldPassword.getText().toString().trim();
        if (TextUtils.isEmpty(oldPassword)) {
            Toast.makeText(this, "请输入原密码", Toast.LENGTH_LONG).show();
            return;
        }
        if (oldPassword.length()<6) {
            Toast.makeText(this, "旧密码至少为6位", Toast.LENGTH_LONG).show();
            return;
        }
        newPassword = modifyNewPassword.getText().toString().trim();
        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "请输入新密码", Toast.LENGTH_LONG).show();
            return;
        }
        if (newPassword.length()<6) {
            Toast.makeText(this, "新密码至少为6位", Toast.LENGTH_LONG).show();
            return;
        }
        newRepassword = modifyNewRepassword.getText().toString().trim();
        if (TextUtils.isEmpty(newRepassword)) {
            Toast.makeText(this, "请确认密码", Toast.LENGTH_LONG).show();
            return;
        }
        if (!newPassword.equals(newRepassword)) {
            Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_LONG).show();
            return;
        }

        hd = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("修改中...")
                .setDimAmount(0.5f)
                .show();

        String oldPassword1 = EncryptUtils.encryptMD5ToString(oldPassword);
        String newPassword1 = EncryptUtils.encryptMD5ToString(newPassword);
        String newRepassword1 = EncryptUtils.encryptMD5ToString(newRepassword);
        Map<String, String> map = new HashMap<>();
        map.put("token", MyApplication.token);
        map.put("password", oldPassword1);
        map.put("newPassword", newPassword1);
        map.put("repNewPassword", newRepassword1);
        JSONObject jsonObject = new JSONObject(map);


        OkGo.post(Constants.commonURL + Constants.setPassword)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LogcatUtil.printLogcat(s);
                        Gson gson = new Gson();
                        ModifyPasswordMessage modifyPasswordMessage = gson.fromJson(s, ModifyPasswordMessage.class);

                        if (modifyPasswordMessage.getError_code() == 0) {
                            Toast.makeText(ModifyPasswordActivity.this, "修改成功，请重新登录", Toast.LENGTH_SHORT).show();
                            // 发广播
                            Intent intent = new Intent();
                            intent.setAction(Constants.INTENT_EXTRA_EXIT);
                            sendBroadcast(intent);

                            startActivity(new Intent(ModifyPasswordActivity.this,LoginActivity.class));
                            setResult(-1);
                            finish();
                        } else {
                            Toast.makeText(ModifyPasswordActivity.this, modifyPasswordMessage.getError_message(), Toast.LENGTH_LONG).show();

                        }
                        hd.dismiss();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Toast.makeText(ModifyPasswordActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                        hd.dismiss();
                        super.onError(call, response, e);
                    }
                });

    }

}
