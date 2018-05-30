package com.xinhe.kakaxianjin.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.xinhe.kakaxianjin.MyApplication;
import com.xinhe.kakaxianjin.R;
import com.xinhe.kakaxianjin.Utils.BankUtils;
import com.xinhe.kakaxianjin.Utils.Base64Utils;
import com.xinhe.kakaxianjin.Utils.BitmapUtils;
import com.xinhe.kakaxianjin.Utils.CheckUtil;
import com.xinhe.kakaxianjin.Utils.Constants;
import com.xinhe.kakaxianjin.Utils.DeviceUtil;
import com.xinhe.kakaxianjin.Utils.ExceptionUtil;
import com.xinhe.kakaxianjin.Utils.LogcatUtil;
import com.xinhe.kakaxianjin.bean.BankListMessage;
import com.xinhe.kakaxianjin.bean.BindCreditcardMessage;
import com.xinhe.kakaxianjin.bean.DiscernBankMessage;
import com.xinhe.kakaxianjin.bean.PersonalInformationMessage;
import com.xinhe.kakaxianjin.bean.StateMessage;
import com.xinhe.kakaxianjin.view.spaceedittext.SpaceEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class CreditcardActivity extends AppCompatActivity {

    @BindView(R.id.my_toolbar_tv)
    TextView myToolbarTv;
    @BindView(R.id.card_ll)
    LinearLayout cardLl;
    @BindView(R.id.creditcard_et1)
    SpaceEditText creditcardEt1;
    @BindView(R.id.creditcard_iv1)
    ImageView creditcardIv1;
    @BindView(R.id.creditcard_et2)
    EditText creditcardEt2;
    @BindView(R.id.creditcard_et3)
    EditText creditcardEt3;
    @BindView(R.id.creditcard_et4)
    EditText creditcardEt4;
    @BindView(R.id.creditcard_btn)
    Button creditcardBtn;
    @BindView(R.id.creditcard_name_tv1)
    TextView creditcardNameTv1;
    @BindView(R.id.creditcard_name_iv1)
    ImageView creditcardNameIv1;
    @BindView(R.id.creditcard_id_tv1)
    TextView creditcardIdTv1;
    @BindView(R.id.creditcard_bank_tv1)
    TextView creditcardBankTv1;
    @BindView(R.id.creditcard_bankrl)
    RelativeLayout creditcardBankrl;
    private File file;
    private Uri origUri;
    private Bitmap bitmap;
    private KProgressHUD hd;
    private String cardNumber;
    private String cvn;
    private String time;
    private String bindPhone;
    private String creditcardId;
    private String bankName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditcard);
        try {
            ButterKnife.bind(this);
            //初始化toobar
            initActionBar();
            //获取个人信息
            getData();
            //初始化
            setView();
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }

    //获取个人信息
    private void getData() {
        if (!DeviceUtil.IsNetWork(this)) {
            Toast.makeText(this, "网络异常", Toast.LENGTH_LONG).show();
            return;
        }

        hd = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("验证中...")
                .setDimAmount(0.5f)
                .show();

        Map<String, String> map = new HashMap<>();
        map.put("token", MyApplication.token);
        JSONObject jsonObject = new JSONObject(map);

        OkGo.<String>post(Constants.commonURL + Constants.getIdCard)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LogcatUtil.printLogcat(s);
                        Gson gson = new Gson();
                        PersonalInformationMessage personalInformationMessage = gson.fromJson(s, PersonalInformationMessage.class);

                        if (personalInformationMessage.getError_code() == 0) {
                            MyApplication.name=personalInformationMessage.getData().getReal_name();
                            creditcardNameTv1.setText(MyApplication.name);
                            MyApplication.ID=personalInformationMessage.getData().getId_card();
                            creditcardIdTv1.setText(MyApplication.ID);
                        } else if (personalInformationMessage.getError_code() == 2) {
                            //token过期，重新登录
                            startActivity(new Intent(CreditcardActivity.this, RegisterActivity.class));
                            Toast.makeText(CreditcardActivity.this, personalInformationMessage.getError_message(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(CreditcardActivity.this, personalInformationMessage.getError_message(), Toast.LENGTH_LONG).show();
                        }

                        hd.dismiss();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Toast.makeText(CreditcardActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                        hd.dismiss();
                        super.onError(call, response, e);
                    }
                });
    }

    private void setView() {

        Intent intent = getIntent();
        int count = intent.getIntExtra("count", 0);
        if (1 == count) {
            cardLl.setVisibility(View.GONE);
        }
        creditcardEt4.setText(MyApplication.phone);

    }

    public void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        myToolbarTv.setText("信息认证");
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick({R.id.creditcard_iv1, R.id.creditcard_btn, R.id.creditcard_name_iv1, R.id.creditcard_bankrl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.creditcard_iv1:
                //权限检查
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA
                            },
                            Constants.PERMISSION_CAMERA);
                    return;
                }
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                            },
                            Constants.PERMISSION_WRITE_EXTERNAL_STORAGE);
                    return;
                }
                takeForPicture();
                break;
            case R.id.creditcard_btn:
                submit();
                break;
            case R.id.creditcard_bankrl:
                getBankList();
                break;
            case R.id.creditcard_name_iv1:
                final AlertDialog alertDialog1 = new AlertDialog.Builder(this,R.style.CustomDialog).create();
                alertDialog1.setCancelable(false);
                alertDialog1.setCanceledOnTouchOutside(false);
                alertDialog1.show();
                Window window1 = alertDialog1.getWindow();
                window1.setContentView(R.layout.dialog_one);
                TextView tv11 = (TextView) window1.findViewById(R.id.integral_exchange_tips1_tv);
                tv11.setText("您添加的储蓄卡将作为结算卡，为了保障安全，此卡的身份信息需跟您的身份认证信息相一致。");
                RelativeLayout rl21 = (RelativeLayout) window1.findViewById(R.id.integral_exchange_tips1_rl1);
                rl21.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        alertDialog1.dismiss();
                    }
                });
                break;
        }
    }

    // 获取银行列表
    private void getBankList() {
        if (!DeviceUtil.IsNetWork(this)) {
            Toast.makeText(this, "网络异常", Toast.LENGTH_LONG).show();
            return;
        }

        hd = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("获取中...")
                .setDimAmount(0.5f)
                .show();

        Map<String, String> map = new HashMap<>();
        map.put("token", MyApplication.token);
        JSONObject jsonObject = new JSONObject(map);

        OkGo.<String>post(Constants.commonURL + Constants.getCbank)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LogcatUtil.printLogcat(s);
                        Gson gson = new Gson();
                        BankListMessage bankListMessage = gson.fromJson(s, BankListMessage.class);

                        if (bankListMessage.getError_code() == 0) {
                            startActivityForResult(new Intent(CreditcardActivity.this, BankListActivity.class).putExtra("banks",bankListMessage),Constants.CREDITCARDACTIVITY_TO_BANKLIST);
                        } else if (bankListMessage.getError_code() == 2) {
                            //token过期，重新登录
                            startActivity(new Intent(CreditcardActivity.this, RegisterActivity.class));
                            Toast.makeText(CreditcardActivity.this, bankListMessage.getError_message(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(CreditcardActivity.this, bankListMessage.getError_message(), Toast.LENGTH_LONG).show();
                        }

                        hd.dismiss();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Toast.makeText(CreditcardActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                        hd.dismiss();
                        super.onError(call, response, e);
                    }
                });
    }


    //提交信息
    private void submit() {
        if (!DeviceUtil.IsNetWork(this)) {
            Toast.makeText(this, "网络异常", Toast.LENGTH_LONG).show();
            return;
        }
        //卡号
        cardNumber = creditcardEt1.getText().toString().replace(" ","");
        if (TextUtils.isEmpty(cardNumber)) {
            Toast.makeText(this, "请输入储蓄卡号", Toast.LENGTH_LONG).show();
            return;
        }
        if (!BankUtils.checkBankCard(cardNumber)) {
            Toast.makeText(this, "卡号输入有误", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(bankName)){
            Toast.makeText(this, "请选择银行", Toast.LENGTH_LONG).show();
            return;
        }

        //CVN码
        cvn = creditcardEt2.getText().toString();
        if (TextUtils.isEmpty(cvn)) {
            Toast.makeText(this, "请输入卡背面末三位", Toast.LENGTH_LONG).show();
            return;
        }
        if (cvn.length() != 3) {
            Toast.makeText(this, "CVN码输入有误", Toast.LENGTH_LONG).show();
            return;
        }
        //有效期
        time = creditcardEt3.getText().toString();
        if (TextUtils.isEmpty(time)) {
            Toast.makeText(this, "请输入有效期", Toast.LENGTH_LONG).show();
            return;
        }
        if (time.length() != 4) {
            Toast.makeText(this, "有效期输入有误", Toast.LENGTH_LONG).show();
            return;
        }
        //手机号
        bindPhone = creditcardEt4.getText().toString();
        if (TextUtils.isEmpty(bindPhone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_LONG).show();
            return;
        }
        if (!CheckUtil.isMobile(bindPhone)) {
            Toast.makeText(this, "手机号输入有误", Toast.LENGTH_LONG).show();
            return;
        }

        hd = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("验证中...")
                .setDimAmount(0.5f)
                .show();
        Map<String, String> map = new HashMap<>();
        map.put("phoneNo", bindPhone);
        map.put("bank", bankName);
        map.put("cvn2", cvn);
        map.put("expired", time);
        map.put("credit_card", cardNumber);
        map.put("token", MyApplication.token);
        JSONObject jsonObject = new JSONObject(map);


        OkGo.<String>post(Constants.commonURL + Constants.frontTran)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LogcatUtil.printLogcat(s);
                        Gson gson = new Gson();
                        BindCreditcardMessage bindCreditcardMessage = gson.fromJson(s, BindCreditcardMessage.class);

                        if (bindCreditcardMessage.getError_code() == 0) {
                            String url = Constants.commonURL + Constants.frontTransTokens + "?credit_card=" + cardNumber + "&cvn2=" + cvn + "&expired=" + time + "&phoneNo=" + bindPhone + "&token=" + MyApplication.token + "&orderId=" + bindCreditcardMessage.getData().getOrderId() + "&txnTime=" + bindCreditcardMessage.getData().getTxnTime() + "&credit=" + "1" + "&bank=" + "交通银行";
                            startActivityForResult(new Intent(CreditcardActivity.this, WebViewTitleActivity.class).putExtra("url", url), Constants.CREDITCARDACTIVITY_TO_WEBVIEW);
                            Toast.makeText(CreditcardActivity.this, "验证成功", Toast.LENGTH_LONG).show();
                            creditcardId = bindCreditcardMessage.getData().getCredit_card_id();
                            // 发广播
                            // 发广播
                            Intent intent = new Intent();
                            intent.setAction(Constants.INTENT_EXTRA_CARD_LIST_CHANGE);
                            sendBroadcast(intent);
                        } else if (bindCreditcardMessage.getError_code() == 2) {
                            //token过期，重新登录
                            startActivity(new Intent(CreditcardActivity.this, RegisterActivity.class));
                            Toast.makeText(CreditcardActivity.this, bindCreditcardMessage.getError_message(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(CreditcardActivity.this, bindCreditcardMessage.getError_message(), Toast.LENGTH_LONG).show();
                        }

                        hd.dismiss();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Toast.makeText(CreditcardActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                        hd.dismiss();

                        super.onError(call, response, e);
                    }
                });

    }


    /**
     * 调用相机
     */
    private void takeForPicture() {

        try {
            String storageState = Environment.getExternalStorageState();
            File vFile;
            if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                vFile = new File(Environment.getExternalStorageDirectory().getPath()
                        + "/kakaxianjin/");//图片位置
                if (!vFile.exists()) {
                    vFile.mkdirs();
                }
            } else {
                Toast.makeText(CreditcardActivity.this, "未挂载sdcard", Toast.LENGTH_LONG).show();
                return;
            }

            String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new
                    Date()) + ".jpg";

            file = new File(vFile, fileName);
            //拍照所存路径
            origUri = Uri.fromFile(file);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Build.VERSION.SDK_INT > 23) {//7.0及以上
                origUri = FileProvider.getUriForFile(CreditcardActivity.this, Constants.fileprovider, new File(vFile, fileName));
                grantUriPermission(getPackageName(), origUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, origUri);
            } else {//7.0以下
                intent.putExtra(MediaStore.EXTRA_OUTPUT, origUri);
            }
            startActivityForResult(intent, Constants.REQUEST_CODE_TAKE_PICETURE);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 105:
                if (resultCode == RESULT_OK) {
                    getCardCode();
                } else {
                    //点击了file按钮，必须有一个返回值，否则会卡死
                    Toast.makeText(this, "拍照失败", Toast.LENGTH_LONG).show();
                }
                break;
            case 120:
                if (resultCode == RESULT_OK) {
                    getState();
                }
                break;
            case 122:
                if (resultCode == RESULT_OK) {
                    bankName=data.getStringExtra("bankName");
                    creditcardBankTv1.setText(bankName);
                }
                break;
        }
    }


    //获取状态
    private void getState() {

        if (!DeviceUtil.IsNetWork(this)) {
            Toast.makeText(this, "网络异常", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("token", MyApplication.token);
        map.put("credit_card_id", creditcardId);
        JSONObject jsonObject = new JSONObject(map);

        hd = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("获取数据中...")
                .setDimAmount(0.5f)
                .show();

        OkGo.post(Constants.commonURL + Constants.cardStatus)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LogcatUtil.printLogcat(s);
                        Gson gson = new Gson();

                        StateMessage stateMessage = gson.fromJson(s, StateMessage.class);

                        if (stateMessage.getError_code() == 0) {
                            // 发广播
                            Intent intent = new Intent();
                            intent.setAction(Constants.INTENT_EXTRA_CARD_LIST_CHANGE);
                            sendBroadcast(intent);

                            Toast.makeText(CreditcardActivity.this, "绑定成功", Toast.LENGTH_LONG).show();
                            finish();
                        } else if (stateMessage.getError_code() == 2) {
                            //token过期，重新登录
                            startActivity(new Intent(CreditcardActivity.this, RegisterActivity.class));
                            Toast.makeText(CreditcardActivity.this, stateMessage.getError_message(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(CreditcardActivity.this, stateMessage.getError_message(), Toast.LENGTH_LONG).show();
                        }

                        hd.dismiss();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Toast.makeText(CreditcardActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                        hd.dismiss();
                        super.onError(call, response, e);
                    }
                });

    }


    //获取卡号
    private void getCardCode() {
        if (origUri != null) {

            try {
                bitmap = BitmapUtils.getBitmapFormUri(CreditcardActivity.this, origUri);
            } catch (IOException e) {
                ExceptionUtil.handleException(e);
            }

            if (!DeviceUtil.IsNetWork(this)) {
                Toast.makeText(this, "网络异常", Toast.LENGTH_LONG).show();
                return;
            }

            hd = KProgressHUD.create(this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("识别中...")
                    .setDimAmount(0.5f)
                    .show();

            String base64 = Base64Utils.bitmapToBase64(bitmap);
            Map<String, String> map = new HashMap<>();
            map.put("bank_card", base64);
            map.put("token", MyApplication.token);
            JSONObject jsonObject = new JSONObject(map);


            OkGo.post(Constants.commonURL + Constants.fetchBank)
                    .tag(this)
                    .upJson(jsonObject)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            LogcatUtil.printLogcat(s);
                            Gson gson = new Gson();
                            DiscernBankMessage discernBankMessage = gson.fromJson(s, DiscernBankMessage.class);

                            if (discernBankMessage.getError_code() == 0) {
//                                Toast.makeText(CardActivity.this, "验证成功", Toast.LENGTH_LONG).show();
                                creditcardEt1.setText(discernBankMessage.getData().getCard_num());
                            } else if (discernBankMessage.getError_code() == 2) {
                                //token过期，重新登录
                                startActivity(new Intent(CreditcardActivity.this, LoginActivity.class));
                                Toast.makeText(CreditcardActivity.this, discernBankMessage.getError_message(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(CreditcardActivity.this, discernBankMessage.getError_message(), Toast.LENGTH_LONG).show();
                            }

                            hd.dismiss();
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            Toast.makeText(CreditcardActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                            hd.dismiss();
                            super.onError(call, response, e);
                        }
                    });

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "相机权限被拒绝", Toast.LENGTH_SHORT).show();
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

}
