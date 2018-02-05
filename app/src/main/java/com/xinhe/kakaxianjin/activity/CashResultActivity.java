package com.xinhe.kakaxianjin.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
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
import com.xinhe.kakaxianjin.Utils.ExceptionUtil;
import com.xinhe.kakaxianjin.Utils.LogcatUtil;
import com.xinhe.kakaxianjin.bean.CashResultMessage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class CashResultActivity extends AppCompatActivity {

    @BindView(R.id.my_toolbar_tv)
    TextView myToolbarTv;
    @BindView(R.id.cash_result_time_tv1)
    TextView cashResultTimeTv1;
    @BindView(R.id.cash_result_count1_tv1)
    TextView cashResultCount1Tv1;
    @BindView(R.id.cash_result_count2_tv1)
    TextView cashResultCount2Tv1;
    @BindView(R.id.cash_result_credit_tv1)
    TextView cashResultCreditTv1;
    @BindView(R.id.cash_result_card_tv1)
    TextView cashResultCardTv1;
    @BindView(R.id.cash_result_time1_tv1)
    TextView cashResultTime1Tv1;
    @BindView(R.id.cash_result_btn)
    Button cashResultBtn;
    private KProgressHUD hd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_result);
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

    private void setView() {

    }

    public void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        myToolbarTv.setText("收款结果");
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //获取交易信息
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

        OkGo.<String>post(Constants.commonURL + Constants.queryComplete)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LogcatUtil.printLogcat(s);
                        Gson gson = new Gson();
                        CashResultMessage cashResultMessage = gson.fromJson(s, CashResultMessage.class);

                        if (cashResultMessage.getError_code() == 0) {
                            CashResultMessage.DataProduct data = cashResultMessage.getData();
                            cashResultTimeTv1.setText(data.getCreated_time());
                            cashResultCount1Tv1.setText(data.getTransAmt());
                            cashResultCount2Tv1.setText(data.getTxnAmtDF());
                            cashResultCreditTv1.setText(data.getBankNameTrans()+"(尾号"+data.getBankNoTrans().substring(data.getBankNoTrans().length()-5)+")");
                            cashResultCardTv1.setText(data.getDecredit_bank()+"(尾号"+data.getDecredit_card().substring(data.getDecredit_card().length()-5)+")");
                        } else if (cashResultMessage.getError_code() == 2) {
                            //token过期，重新登录
                            startActivity(new Intent(CashResultActivity.this, RegisterActivity.class));
                            Toast.makeText(CashResultActivity.this, cashResultMessage.getError_message(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(CashResultActivity.this, cashResultMessage.getError_message(), Toast.LENGTH_LONG).show();
                        }

                        hd.dismiss();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Toast.makeText(CashResultActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                        hd.dismiss();
                        super.onError(call, response, e);
                    }
                });
    }

    @OnClick({R.id.cash_result_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cash_result_btn:
                final AlertDialog alertDialog1 = new AlertDialog.Builder(CashResultActivity.this,R.style.CustomDialog).create();
                alertDialog1.setCancelable(false);
                alertDialog1.setCanceledOnTouchOutside(false);
                alertDialog1.show();
                Window window1 = alertDialog1.getWindow();
                window1.setContentView(R.layout.dialog_two);
                TextView tv11 = (TextView) window1.findViewById(R.id.integral_exchange_tips1_tv);
                tv11.setText("交易完成，是否需要继续交易？");
                RelativeLayout rl21 = (RelativeLayout) window1.findViewById(R.id.integral_exchange_tips1_rl1);
                RelativeLayout rl31 = (RelativeLayout) window1.findViewById(R.id.integral_exchange_tips1_rl2);
                TextView tv1 = (TextView) window1.findViewById(R.id.integral_exchange_tips1_tv1);
                tv1.setText("否");
                TextView tv2 = (TextView) window1.findViewById(R.id.integral_exchange_tips1_tv2);
                tv2.setText("是");
                rl21.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        setResult(-1);
                        finish();
                        alertDialog1.dismiss();
                    }
                });
                rl31.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 跳银联
                        finish();
                        alertDialog1.dismiss();
                    }
                });
        }
    }
}
