package com.xinhe.kakaxianjin.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.xinhe.kakaxianjin.MyApplication;
import com.xinhe.kakaxianjin.R;
import com.xinhe.kakaxianjin.Utils.BankUtils;
import com.xinhe.kakaxianjin.Utils.Constants;
import com.xinhe.kakaxianjin.Utils.DeviceUtil;
import com.xinhe.kakaxianjin.Utils.ExceptionUtil;
import com.xinhe.kakaxianjin.Utils.LogcatUtil;
import com.xinhe.kakaxianjin.Utils.PopupWindowUtil;
import com.xinhe.kakaxianjin.bean.CardList;
import com.xinhe.kakaxianjin.bean.LoanCodeMessage;
import com.xinhe.kakaxianjin.bean.MessageCode;
import com.xinhe.kakaxianjin.bean.StateMessage;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class ChooseCreditcardActivity extends BaseActivity {

    @BindView(R.id.my_toolbar_tv)
    TextView myToolbarTv;
    @BindView(R.id.choose_creditcard_vp)
    ViewPager chooseCreditcardVp;
    @BindView(R.id.choose_creditcard_dots_ll)
    LinearLayout chooseCreditcardDotsLl;
    @BindView(R.id.choose_creditcard_et)
    EditText chooseCreditcardEt;
    @BindView(R.id.choose_creditcard_rate_tv1)
    TextView chooseCreditcardRateTv1;
    @BindView(R.id.choose_creditcard_rate_tv2)
    TextView chooseCreditcardRateTv2;
    @BindView(R.id.choose_creditcard_card_tv1)
    TextView chooseCreditcardCardTv1;
    @BindView(R.id.choose_creditcard_card_tv2)
    TextView chooseCreditcardCardTv2;
    @BindView(R.id.choose_creditcard_card_tv3)
    TextView chooseCreditcardCardTv3;
    @BindView(R.id.choose_creditcard_phone_tv1)
    TextView chooseCreditcardPhoneTv1;
    @BindView(R.id.choose_creditcard_phone_tv2)
    TextView chooseCreditcardPhoneTt2;
    @BindView(R.id.choose_creditcard_code_tv1)
    TextView chooseCreditcardCodeTv1;
    @BindView(R.id.choose_creditcard_code_et2)
    EditText chooseCreditcardCodeEt2;
    @BindView(R.id.choose_creditcard_code_tv3)
    TextView chooseCreditcardCodeTv3;
    @BindView(R.id.choose_creditcard_phone_ll)
    LinearLayout chooseCreditcardPhoneLl;
    @BindView(R.id.choose_creditcard_btn)
    Button chooseCreditcardBtn;
    @BindView(R.id.choose_creditcard_cb)
    CheckBox chooseCreditcardCb;
    @BindView(R.id.choose_creditcard_agreement)
    TextView chooseCreditcardAgreement;
    private MyAdapger vpAdapter;
    //滚动点集合
    private ArrayList<View> dots;
    //记录点的当前位置
    private int currentIndex = 0;

    List<CardList.DataProduct.CreditCardProduct> creditCardProducts = new ArrayList<CardList.DataProduct.CreditCardProduct>();
    List<CardList.DataProduct.DecreditCardProduct> decreditCardProducts = new ArrayList<CardList.DataProduct.DecreditCardProduct>();
    private KProgressHUD hd;
    //费率选择
    private int rateChoose;
    //    绑定的手机号
    private String phone;
    //选择的信用卡
    private CardList.DataProduct.CreditCardProduct chooseCreditcard;
    //取现额度
    private String count;
    //验证码
    private boolean isSend;
    //读秒计时
    private TimeCount time;
    //验证码
    private String code;
    private LoanCodeMessage loanCodeMessage;
    private InnerReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_creditcard);
        try {
            ButterKnife.bind(this);
            //初始化toobar
            initActionBar();
            getData();
            //初始化
            setViews();
            //设置监听
            setListeners();
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }

    //获取数据
    private void getData() {

        //信用卡
        creditCardProducts.clear();
        if (null != MyApplication.cardList.getData().getCredit_card() && MyApplication.cardList.getData().getCredit_card().size() > 0) {
            creditCardProducts.addAll(MyApplication.cardList.getData().getCredit_card());
            chooseCreditcard = creditCardProducts.get(0);
        }
        creditCardProducts.add(new CardList.DataProduct.CreditCardProduct());


        //费率默认选择
        if (null != chooseCreditcard && chooseCreditcard.getSupport() == 2) {
            chooseCreditcardRateTv1.setText("费率0.60%，加急费：3元");
            rateChoose = 2;
            chooseCreditcardPhoneLl.setVisibility(View.GONE);
        } else {
            chooseCreditcardRateTv1.setText("费率0.48%，加急费：3元");
            rateChoose = 1;
            if (null!= chooseCreditcard){
                chooseCreditcardPhoneLl.setVisibility(View.VISIBLE);
            }else {
                chooseCreditcardPhoneLl.setVisibility(View.GONE);
            }
        }

        //手机号
        if (null!=chooseCreditcard){
            chooseCreditcardPhoneTt2.setText(chooseCreditcard.getPhoneNo());
        }


        //储蓄卡
        decreditCardProducts.clear();
        if (null != MyApplication.cardList.getData().getDecredit_card() && MyApplication.cardList.getData().getDecredit_card().size() > 0) {
            decreditCardProducts.addAll(MyApplication.cardList.getData().getDecredit_card());
            //卡号不可以点击
            chooseCreditcardCardTv2.setClickable(false);
            //更换可见
            chooseCreditcardCardTv3.setVisibility(View.VISIBLE);
            chooseCreditcardCardTv2.setText(decreditCardProducts.get(0).getDecredit_card());
        }

    }

    private void setListeners() {
        //信用卡滑动页
        chooseCreditcardVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //指示点
                dots.get(currentIndex).setBackgroundResource(R.drawable.banner_point_disabled);// 设置上次选中的圆点为不选中
                dots.get(position).setBackgroundResource(R.drawable.banner_point_enabled);// 设置当前选中的圆点为选中
                currentIndex = position;
                //选择的信用卡
                if (creditCardProducts.size() == position + 1) {
                    chooseCreditcard = null;
                } else {
                    chooseCreditcard = creditCardProducts.get(position);
                }
                //费率默认选择
                if (null != chooseCreditcard && chooseCreditcard.getSupport() == 2) {
                    chooseCreditcardRateTv1.setText("费率0.60%，加急费：3元");
                    rateChoose = 2;
                    chooseCreditcardPhoneLl.setVisibility(View.GONE);
                } else {
                    chooseCreditcardRateTv1.setText("费率0.48%，加急费：3元");
                    rateChoose = 1;
                    if (null!= chooseCreditcard){
                        chooseCreditcardPhoneLl.setVisibility(View.VISIBLE);
                    }else {
                        chooseCreditcardPhoneLl.setVisibility(View.GONE);
                    }
                }

                //手机号
                if (null!=chooseCreditcard){
                    chooseCreditcardPhoneTt2.setText(chooseCreditcard.getPhoneNo());
                }

                //回复发送状态
                time.cancel();
                time=new TimeCount(60000, 1000);
                chooseCreditcardCodeTv3.setText("发送验证码");
                chooseCreditcardCodeTv3.setClickable(true);
                isSend=false;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //单选框
        chooseCreditcardCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chooseCreditcardBtn.setClickable(true);
                    chooseCreditcardBtn.setBackgroundResource(R.drawable.main_fragment_button_bg);
                } else {
                    chooseCreditcardBtn.setClickable(false);
                    chooseCreditcardBtn.setBackgroundResource(R.drawable.main_fragment_button_bg1);
                }
            }
        });
    }

    private void setViews() {
        // 注册广播接收者
        receiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_EXTRA_CARD_LIST_CHANGE);
        registerReceiver(receiver, filter);


        // 构造CountDownTimer对象
        time = new TimeCount(60000, 1000);
        // 获得圆点
        getDotList();
        // 设置第一个圆点为选中状态
        dots.get(0).setBackgroundResource(R.drawable.banner_point_enabled);
        vpAdapter = new MyAdapger();
        chooseCreditcardVp.setAdapter(vpAdapter);// 配置pager页
    }

    public void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        myToolbarTv.setText("信用卡收款");
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick({R.id.choose_creditcard_rate_tv2, R.id.choose_creditcard_card_tv3, R.id.choose_creditcard_card_tv2, R.id.choose_creditcard_btn, R.id.choose_creditcard_agreement, R.id.choose_creditcard_code_tv3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.choose_creditcard_rate_tv2:
                showMyDialog();
                break;
            case R.id.choose_creditcard_card_tv2:
                getState();
                break;
            case R.id.choose_creditcard_card_tv3:
                Intent intent = new Intent(ChooseCreditcardActivity.this, CardActivity.class);
                intent.putExtra("count", 1);
                startActivity(intent);
                break;
            case R.id.choose_creditcard_btn:
                if(!DeviceUtil.IsNetWork(this)){
                    Toast.makeText(this, "网络异常", Toast.LENGTH_LONG).show();
                    return;
                }
                if (creditCardProducts.size()==1){
                    getState();
                    return;
                }
                if (null==chooseCreditcard){
                    Toast.makeText(this, "请选择收款信用卡", Toast.LENGTH_LONG).show();
                    return;
                }
                count = chooseCreditcardEt.getText().toString();
                if (TextUtils.isEmpty(count)){
                    Toast.makeText(this, "请输入收款金额", Toast.LENGTH_LONG).show();
                    return;
                }
                if (Integer.parseInt(count)<500||Integer.parseInt(count)>20000) {
                    Toast.makeText(this, "金额请在500-20000之间", Toast.LENGTH_LONG).show();
                    return;
                }
                if (Integer.parseInt(count)>1000&&Integer.parseInt(count)%100==0) {
                    final AlertDialog alertDialog1 = new AlertDialog.Builder(this,R.style.CustomDialog).create();
                    alertDialog1.setCancelable(false);
                    alertDialog1.setCanceledOnTouchOutside(false);
                    alertDialog1.show();
                    Window window1 = alertDialog1.getWindow();
                    window1.setContentView(R.layout.dialog_one);
                    TextView tv11 = (TextView) window1.findViewById(R.id.integral_exchange_tips1_tv);
                    tv11.setText("大额整数额度将被视为套现行为，请修改额度");
                    RelativeLayout rl21 = (RelativeLayout) window1.findViewById(R.id.integral_exchange_tips1_rl1);
                    rl21.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            alertDialog1.dismiss();
                        }
                    });
                    return;
                }
                if (1==rateChoose){
                    submit1();
                }else {
                    submit2();
                }
                break;
            case R.id.choose_creditcard_agreement:
                Intent intent1 = new Intent(this, WebViewTitleActivity.class);
                intent1.putExtra("url", Constants.loanAgreement);
                startActivity(intent1);
                break;
            case R.id.choose_creditcard_code_tv3:
                sendCode();
                break;
        }
    }

    //发送验证码
    private void sendCode() {
        if (!DeviceUtil.IsNetWork(this)) {
            Toast.makeText(this, "网络异常", Toast.LENGTH_LONG).show();
            return;
        }

        if (null==chooseCreditcard){
            Toast.makeText(this, "请选择收款信用卡", Toast.LENGTH_LONG).show();
            return;
        }
        count = chooseCreditcardEt.getText().toString();
        if (TextUtils.isEmpty(count)){
            Toast.makeText(this, "请输入收款金额", Toast.LENGTH_LONG).show();
            return;
        }
        if (Integer.parseInt(count)<500||Integer.parseInt(count)>20000) {
            Toast.makeText(this, "金额请在500-20000之间", Toast.LENGTH_LONG).show();
            return;
        }
        if (Integer.parseInt(count)>1000&&Integer.parseInt(count)%100==0) {
            final AlertDialog alertDialog1 = new AlertDialog.Builder(this,R.style.CustomDialog).create();
            alertDialog1.setCancelable(false);
            alertDialog1.setCanceledOnTouchOutside(false);
            alertDialog1.show();
            Window window1 = alertDialog1.getWindow();
            window1.setContentView(R.layout.dialog_one);
            TextView tv11 = (TextView) window1.findViewById(R.id.integral_exchange_tips1_tv);
            tv11.setText("大额整数金额将被视为套现行为，请修改金额");
            RelativeLayout rl21 = (RelativeLayout) window1.findViewById(R.id.integral_exchange_tips1_rl1);
            rl21.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    alertDialog1.dismiss();
                }
            });
            return;
        }

        hd = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("发送中...")
                .setDimAmount(0.5f)
                .show();

        Map<String, String> map = new HashMap<>();
        map.put("txnAmt", count);
        map.put("token", MyApplication.token);
        map.put("credit_card_id", chooseCreditcard.getId());
        JSONObject jsonObject = new JSONObject(map);


        OkGo.post(Constants.commonURL + Constants.SmsConsume)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LogcatUtil.printLogcat(s);
                        Gson gson = new Gson();
                        loanCodeMessage = gson.fromJson(s, LoanCodeMessage.class);

                        if (loanCodeMessage.getError_code() == 0) {
                            isSend = true;
                            // 开始计时
                            time.start();
                            Toast.makeText(ChooseCreditcardActivity.this, "验证码发送成功", Toast.LENGTH_LONG).show();
                        } else if(loanCodeMessage.getError_code() == 2){
                            //token过期，重新登录
                            startActivity(new Intent(ChooseCreditcardActivity.this, LoginActivity.class));
                            Toast.makeText(ChooseCreditcardActivity.this, loanCodeMessage.getError_message(), Toast.LENGTH_LONG).show();
                        }else if(loanCodeMessage.getError_code() == 10025){
                            goYinLian();
                            Toast.makeText(ChooseCreditcardActivity.this, loanCodeMessage.getError_message(), Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(ChooseCreditcardActivity.this, loanCodeMessage.getError_message(), Toast.LENGTH_LONG).show();
                        }
                        hd.dismiss();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Toast.makeText(ChooseCreditcardActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                        hd.dismiss();
                        super.onError(call, response, e);
                    }
                });
    }

    //无积分提交交易订单
    private void submit1() {
        if (chooseCreditcardPhoneLl.getVisibility()==View.GONE){
            chooseCreditcardPhoneLl.setVisibility(View.VISIBLE);
            return;
        }

        if (!isSend) {
            Toast.makeText(this, "请获取验证码", Toast.LENGTH_LONG).show();
            return;
        }
        code = chooseCreditcardCodeEt2.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_LONG).show();
            return;
        }
        if (code.length()>6||code.length()<4) {
            Toast.makeText(this, "验证码输入有误", Toast.LENGTH_LONG).show();
            return;
        }
        if (!(count).equals(loanCodeMessage.getData().getTxnAmt())){
            final AlertDialog alertDialog1 = new AlertDialog.Builder(this,R.style.CustomDialog).create();
            alertDialog1.setCancelable(false);
            alertDialog1.setCanceledOnTouchOutside(false);
            alertDialog1.show();
            Window window1 = alertDialog1.getWindow();
            window1.setContentView(R.layout.dialog_one);
            TextView tv11 = (TextView) window1.findViewById(R.id.integral_exchange_tips1_tv);
            tv11.setText("金额有变动，若要修改请重新获取验证码！");
            RelativeLayout rl21 = (RelativeLayout) window1.findViewById(R.id.integral_exchange_tips1_rl1);
            rl21.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    alertDialog1.dismiss();
                }
            });
            return;
        }


        hd = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("交易中...")
                .setDimAmount(0.5f)
                .show();

        Map<String,String> map=new HashMap<>();
        map.put("smsCode",code);
        map.put("orderId",loanCodeMessage.getData().getOrderId());
        map.put("txnTime",loanCodeMessage.getData().getTxnTime());
        map.put("txnAmtTrans",count);
        map.put("token",MyApplication.token);
        map.put("credit_card_id",chooseCreditcard.getId());
        JSONObject jsonObject=new JSONObject(map);



        OkGo.post(Constants.commonURL + Constants.open)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        LogcatUtil.printLogcat( s);
                        Gson gson = new Gson();
                        MessageCode loginMessage = gson.fromJson(s,MessageCode.class);

                        if (loginMessage.getError_code() == 0) {
                            //跳转到结果页
                            startActivityForResult(new Intent(ChooseCreditcardActivity.this,CashResultActivity.class),Constants.CHOOSECREDITCARDACTIVITY_TO_CASHRESULT);
                        } else {
                            Toast.makeText(ChooseCreditcardActivity.this, loginMessage.getError_message(), Toast.LENGTH_LONG).show();

                        }
                        hd.dismiss();
                    }

                    @Override
                    public void onError(Call call, okhttp3.Response response, Exception e) {
                        Toast.makeText(ChooseCreditcardActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                        hd.dismiss();
                        super.onError(call, response, e);
                    }
                });
    }
    //有积分提交交易订单
    private void submit2() {

        hd = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("交易中...")
                .setDimAmount(0.5f)
                .show();

        Map<String,String> map=new HashMap<>();
        map.put("txnAmtTrans",count);
        map.put("token",MyApplication.token);
        map.put("credit_card_id",chooseCreditcard.getId());
        JSONObject jsonObject=new JSONObject(map);



        OkGo.post(Constants.commonURL + Constants.openJifen)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        LogcatUtil.printLogcat( s);
                        Gson gson = new Gson();
                        MessageCode loginMessage = gson.fromJson(s,MessageCode.class);

                        if (loginMessage.getError_code() == 0) {
                            //跳转到结果页
                            startActivityForResult(new Intent(ChooseCreditcardActivity.this,CashResultActivity.class),Constants.CHOOSECREDITCARDACTIVITY_TO_CASHRESULT);
                        } else {
                            Toast.makeText(ChooseCreditcardActivity.this, loginMessage.getError_message(), Toast.LENGTH_LONG).show();

                        }
                        hd.dismiss();
                    }

                    @Override
                    public void onError(Call call, okhttp3.Response response, Exception e) {
                        Toast.makeText(ChooseCreditcardActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                        hd.dismiss();
                        super.onError(call, response, e);
                    }
                });
    }

    //利率
    private void showMyDialog() {
        View rootView = getLayoutInflater().inflate(R.layout.activity_choose_creditcard, null);
        PopupWindowUtil.showPopupWindow(this, rootView, "费率0.48%（无信用卡积分）", "费率0.60%（有信用卡积分）", "取消",
                new PopupWindowUtil.onPupupWindowOnClickListener() {
                    @Override
                    public void onFirstButtonClick() {
                        if (chooseCreditcard!=null&&2==chooseCreditcard.getSupport()){
                            final AlertDialog alertDialog1 = new AlertDialog.Builder(ChooseCreditcardActivity.this,R.style.CustomDialog).create();
                            alertDialog1.setCancelable(false);
                            alertDialog1.setCanceledOnTouchOutside(false);
                            alertDialog1.show();
                            Window window1 = alertDialog1.getWindow();
                            window1.setContentView(R.layout.dialog_two);
                            TextView tv11 = (TextView) window1.findViewById(R.id.integral_exchange_tips1_tv);
                            tv11.setText("此费率需去银联验证，确定要去银联吗？");
                            RelativeLayout rl21 = (RelativeLayout) window1.findViewById(R.id.integral_exchange_tips1_rl1);
                            RelativeLayout rl31 = (RelativeLayout) window1.findViewById(R.id.integral_exchange_tips1_rl2);
                            rl21.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    alertDialog1.dismiss();
                                }
                            });
                            rl31.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // 跳银联
                                    goYinLian();
                                    alertDialog1.dismiss();
                                }
                            });
                        }else {
                            chooseCreditcardRateTv1.setText("费率0.48%，加急费：3元");
                            rateChoose = 1;
                            if (null!= chooseCreditcard){
                                chooseCreditcardPhoneLl.setVisibility(View.VISIBLE);
                            }else {
                                chooseCreditcardPhoneLl.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onSecondButtonClick() {
                        if (chooseCreditcard!=null&&1==chooseCreditcard.getSupport()){
                            Toast.makeText(ChooseCreditcardActivity.this,"该银行暂不支持此费率",Toast.LENGTH_LONG).show();
                        }else {
                            chooseCreditcardRateTv1.setText("费率0.60%，加急费：3元");
                            rateChoose = 2;
                            //手机号不显示
                            chooseCreditcardPhoneLl.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancleButtonClick() {

                    }
                });
    }

    //跳转银联
    private void goYinLian() {
        String url = Constants.commonURL + Constants.frontTransTokens1 + "?credit_card_id=" + chooseCreditcard.getId() + "&token=" + MyApplication.token + "&credit=" + "1";
        startActivityForResult(new Intent(ChooseCreditcardActivity.this, WebViewTitleActivity.class).putExtra("url", url), Constants.CHOOSECREDITCARDACTIVITY_TO_WEBVIEW);
    }


    // 定义一个倒计时的内部类
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            chooseCreditcardCodeTv3.setText("重新发送");
            chooseCreditcardCodeTv3.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            chooseCreditcardCodeTv3.setClickable(false);
            chooseCreditcardCodeTv3.setText(millisUntilFinished / 1000 + "秒");
        }
    }

    // ViewPager的适配器
    private class MyAdapger extends PagerAdapter {

        @Override
        public int getCount() {
            return creditCardProducts.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(ChooseCreditcardActivity.this).inflate(R.layout.choose_creditcard_item, null);
            CardList.DataProduct.CreditCardProduct creditCardProduct = creditCardProducts.get(position);
            RelativeLayout rl1 = (RelativeLayout) view.findViewById(R.id.choose_creditcard_item_rl1);
            RelativeLayout rl2 = (RelativeLayout) view.findViewById(R.id.choose_creditcard_item_rl2);
            if (position == creditCardProducts.size() - 1) {
                rl1.setVisibility(View.GONE);
                rl2.setVisibility(View.VISIBLE);

            } else {
                rl1.setVisibility(View.VISIBLE);
                rl2.setVisibility(View.GONE);
                rl1.setBackgroundResource(BankUtils.getBankBackground(creditCardProduct.getBankNameTrans()));
            }

            ImageView iv1 = (ImageView) view.findViewById(R.id.choose_creditcard_item_iv);
            TextView tv1 = (TextView) view.findViewById(R.id.choose_creditcard_item_tv1);
            TextView tv3 = (TextView) view.findViewById(R.id.choose_creditcard_item_tv3);

            ImageView iv2 = (ImageView) view.findViewById(R.id.choose_creditcard_item_iv1);

            tv1.setText(creditCardProduct.getBankNameTrans());
            tv3.setText(creditCardProduct.getCredit_card());

            Glide.with(ChooseCreditcardActivity.this).load(creditCardProduct.getBank_logo()).crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(iv1);

            iv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (creditCardProducts.size() > 1) {
                        Intent intent = new Intent(ChooseCreditcardActivity.this, CreditcardActivity.class);
                        intent.putExtra("count", 1);
                        startActivity(intent);
                    } else {
                        getState();
                    }

                }
            });
            // 如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
            ViewParent vp = view.getParent();
            if (vp != null) {
                ViewGroup parent = (ViewGroup) vp;
                parent.removeView(view);
            }
            container.addView(view);
            return view;
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
        JSONObject jsonObject = new JSONObject(map);

        hd = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("获取数据中...")
                .setDimAmount(0.5f)
                .show();

        OkGo.post(Constants.commonURL + Constants.getStatus)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LogcatUtil.printLogcat(s);
                        Gson gson = new Gson();
                        StateMessage stateMessage = gson.fromJson(s, StateMessage.class);

                        if (stateMessage.getError_code() == 0) {
                            if ("0".equals(stateMessage.getData().getId_card_status())) {
                                Intent intent = new Intent(ChooseCreditcardActivity.this, IDVerifyActivity.class);
                                startActivity(intent);
                            } else if ("0".equals(stateMessage.getData().getDebit_card_status())) {
                                Intent intent = new Intent(ChooseCreditcardActivity.this, CardActivity.class);
                                intent.putExtra("count", 0);
                                startActivity(intent);
                            } else if ("0".equals(stateMessage.getData().getCredit_card_status())) {
                                Intent intent = new Intent(ChooseCreditcardActivity.this, CreditcardActivity.class);
                                intent.putExtra("count", 0);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(ChooseCreditcardActivity.this, CreditcardActivity.class);
                                intent.putExtra("count", 1);
                                startActivity(intent);
                            }
                        } else if (stateMessage.getError_code() == 2) {
                            //token过期，重新登录
                            startActivity(new Intent(ChooseCreditcardActivity.this, LoginActivity.class));
                            Toast.makeText(ChooseCreditcardActivity.this, stateMessage.getError_message(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ChooseCreditcardActivity.this, stateMessage.getError_message(), Toast.LENGTH_LONG).show();
                        }

                        hd.dismiss();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Toast.makeText(ChooseCreditcardActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                        super.onError(call, response, e);
                    }
                });

    }

    //获取单个信用卡的绑定状态
    private void getState1() {

        if (!DeviceUtil.IsNetWork(this)) {
            Toast.makeText(this, "网络异常", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("token", MyApplication.token);
        map.put("credit_card_id", chooseCreditcard.getId());
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

                            Toast.makeText(ChooseCreditcardActivity.this, "绑定成功", Toast.LENGTH_LONG).show();
                        } else if (stateMessage.getError_code() == 2) {
                            //token过期，重新登录
                            startActivity(new Intent(ChooseCreditcardActivity.this, RegisterActivity.class));
                            Toast.makeText(ChooseCreditcardActivity.this, stateMessage.getError_message(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ChooseCreditcardActivity.this, stateMessage.getError_message(), Toast.LENGTH_LONG).show();
                        }

                        hd.dismiss();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Toast.makeText(ChooseCreditcardActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                        hd.dismiss();
                        super.onError(call, response, e);
                    }
                });

    }

    //指示点
    private void getDotList() {
        chooseCreditcardDotsLl.removeAllViews();
        dots = new ArrayList<View>();
        // 循环图片数组，创建对应数量的dot
        for (int i = 0; i < creditCardProducts.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.choose_creditcard_dot, null);// 加载布局
            View dot = view.findViewById(R.id.main_dot);// 得到布局中的dot点组件
            // 收集dot
            dots.add(dot);
            // 把布局添加到线性布局
            chooseCreditcardDotsLl.addView(view);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 123:
                if (resultCode == RESULT_OK) {
                    getState1();
                }
                break;
            case 124:
                if (resultCode == RESULT_OK) {
                    finish();
                }
                break;
        }
    }


    // 广播接收者
    private class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取Intent中的Action
            String action = intent.getAction();
            // 判断Action
            if (Constants.INTENT_EXTRA_CARD_LIST_CHANGE.equals(action)) {
                getCardList();
            }
        }
    }

    //获取信用卡、储蓄卡列表
    private void getCardList() {
        if (!DeviceUtil.IsNetWork(this)) {
            Toast.makeText(this, "网络异常", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("token", MyApplication.token);
        JSONObject jsonObject = new JSONObject(map);


        OkGo.post(Constants.commonURL + Constants.getBankCard)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LogcatUtil.printLogcat(s);
                        Gson gson = new Gson();
                        CardList cardList = gson.fromJson(s, CardList.class);

                        if (cardList.getError_code() == 0) {
                            MyApplication.cardList = cardList;
                            getData();

                            // 获得圆点
                            getDotList();
                            // 设置第一个圆点为选中状态
                            dots.get(0).setBackgroundResource(R.drawable.banner_point_enabled);
                            //viewpager刷新无效果，先使用此方法刷新
                            vpAdapter=null;
                            vpAdapter = new MyAdapger();
                            chooseCreditcardVp.setAdapter(vpAdapter);// 配置pager页
                        }else {
                            Toast.makeText(ChooseCreditcardActivity.this, cardList.getError_message(), Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Toast.makeText(ChooseCreditcardActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                        super.onError(call, response, e);
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
