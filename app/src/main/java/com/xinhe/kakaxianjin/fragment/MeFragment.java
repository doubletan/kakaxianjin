package com.xinhe.kakaxianjin.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
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
import com.xinhe.kakaxianjin.Utils.Constants;
import com.xinhe.kakaxianjin.Utils.DeviceUtil;
import com.xinhe.kakaxianjin.Utils.ExceptionUtil;
import com.xinhe.kakaxianjin.Utils.LogcatUtil;
import com.xinhe.kakaxianjin.Utils.SPUtils;
import com.xinhe.kakaxianjin.activity.CardManageActivity;
import com.xinhe.kakaxianjin.activity.CashRecordsActivity;
import com.xinhe.kakaxianjin.activity.LoginActivity;
import com.xinhe.kakaxianjin.activity.PersonalInformationActivity;
import com.xinhe.kakaxianjin.activity.WebViewTitleActivity;
import com.xinhe.kakaxianjin.bean.CardList;
import com.xinhe.kakaxianjin.bean.CashRecords;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * Created by tantan on 2018/1/15.
 */

public class MeFragment extends Fragment {
    @BindView(R.id.me_fragment_top)
    LinearLayout meFragmentTop;
    @BindView(R.id.me_fragment_login_tv)
    TextView meFragmentLoginTv;
    @BindView(R.id.me_fragment_number_tv)
    TextView meFragmentNumberTv;
    @BindView(R.id.me_fragment_rl)
    RelativeLayout meFragmentRl;
    @BindView(R.id.me_fragment_record_iv)
    ImageView meFragmentRecordIv;
    @BindView(R.id.me_fragment_rl1)
    RelativeLayout meFragmentRl1;
    @BindView(R.id.me_fragment_rl2)
    RelativeLayout meFragmentRl2;
    @BindView(R.id.me_fragment_rl3)
    RelativeLayout meFragmentRl3;
    @BindView(R.id.me_fragment_rl4)
    RelativeLayout meFragmentRl4;
    @BindView(R.id.me_fragment_quit_btn)
    Button meFragmentQuitBtn;
    Unbinder unbinder;
    private View view;
    private InnerReceiver receiver;
    private KProgressHUD hd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.me_fragment, null);
        try {
            unbinder = ButterKnife.bind(this, view);
            //初始化
            setView();
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
        return view;
    }

    private void setView() {
        // 注册广播接收者
        receiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_EXTRA_LOGIN_SUCESS);
        filter.addAction(Constants.INTENT_EXTRA_EXIT);
        getContext().registerReceiver(receiver, filter);

        if (MyApplication.isLogin) {
            login();
        } else {
            exit();
        }
    }

    //登陆后的设置
    private void login() {
        meFragmentLoginTv.setVisibility(View.GONE);
        meFragmentRl.setVisibility(View.VISIBLE);
        meFragmentNumberTv.setText(MyApplication.phone);
        meFragmentQuitBtn.setVisibility(View.VISIBLE);
    }

    //退出登陆后的设置
    private void exit() {
        MyApplication.isLogin = false;
        SPUtils.remove(getContext(), "token");
        meFragmentLoginTv.setVisibility(View.VISIBLE);
        meFragmentRl.setVisibility(View.GONE);
        meFragmentNumberTv.setText(MyApplication.phone);
        meFragmentQuitBtn.setVisibility(View.GONE);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        getContext().unregisterReceiver(receiver);
    }

    @OnClick({R.id.me_fragment_login_tv, R.id.me_fragment_quit_btn, R.id.me_fragment_rl1, R.id.me_fragment_rl2, R.id.me_fragment_rl3, R.id.me_fragment_rl4, R.id.me_fragment_rl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.me_fragment_login_tv:
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.me_fragment_quit_btn:
                final AlertDialog alertDialog = new AlertDialog.Builder(getContext(),R.style.CustomDialog).create();
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
                Window window = alertDialog.getWindow();
                window.setContentView(R.layout.dialog_two);
                TextView tv1 = (TextView) window.findViewById(R.id.integral_exchange_tips1_tv);
                tv1.setText("确定要退出登录吗？");
                RelativeLayout rl2 = (RelativeLayout) window.findViewById(R.id.integral_exchange_tips1_rl1);
                RelativeLayout rl3 = (RelativeLayout) window.findViewById(R.id.integral_exchange_tips1_rl2);
                rl2.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                rl3.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        exit();
                        alertDialog.dismiss();
                    }
                });
                break;
            case R.id.me_fragment_rl1:
                if (!MyApplication.isLogin){
                    Intent intent1 = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent1);
                }else {
                    getCashRecords();
                }
                break;
            case R.id.me_fragment_rl2:
                if (!MyApplication.isLogin){
                    Intent intent1 = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent1);
                }else {
                    getCardList();
                }
                break;
            case R.id.me_fragment_rl3:
                //权限检查
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CALL_PHONE
                            },
                            Constants.PERMISSION_CALL_PHONE);
                    return;
                }

                final AlertDialog alertDialog1 = new AlertDialog.Builder(getContext(),R.style.CustomDialog).create();
                alertDialog1.setCancelable(false);
                alertDialog1.setCanceledOnTouchOutside(false);
                alertDialog1.show();
                Window window1 = alertDialog1.getWindow();
                window1.setContentView(R.layout.dialog_two);
                TextView tv11 = (TextView) window1.findViewById(R.id.integral_exchange_tips1_tv);
                tv11.setText("确定要拨打客服电话吗？");
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
                        // 发送请求，打电话
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:15301048004"));
                        startActivity(intent);
                        alertDialog1.dismiss();
                    }
                });
                break;
            case R.id.me_fragment_rl4:
                Intent intent2 = new Intent(getContext(), WebViewTitleActivity.class);
                intent2.putExtra("url", Constants.problems);
                startActivity(intent2);
                break;
            case R.id.me_fragment_rl:
                Intent intent3 = new Intent(getContext(), PersonalInformationActivity.class);
                startActivity(intent3);
                break;
        }
    }


    //获取信用卡、储蓄卡列表
    private void getCardList() {
        if(!DeviceUtil.IsNetWork(getContext())){
            Toast.makeText(getContext(), "网络异常", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String,String> map=new HashMap<>();
        map.put("token",MyApplication.token);
        JSONObject jsonObject=new JSONObject(map);

        hd = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("获取中...")
                .setDimAmount(0.5f)
                .show();

        OkGo.post(Constants.commonURL + Constants.getBankCard)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        LogcatUtil.printLogcat( s);
                        Gson gson = new Gson();
                        CardList cardList = gson.fromJson(s, CardList.class);

                        if (cardList.getError_code() == 0) {
//                            Toast.makeText(getContext(),"获取成功",Toast.LENGTH_LONG).show();
                            MyApplication.cardList=cardList;
                            startActivity(new Intent(getContext(), CardManageActivity.class));
                        }else if(cardList.getError_code() == 2){
                            //token过期，重新登录
                            startActivity(new Intent(getContext(), LoginActivity.class));
                            Toast.makeText(getContext(), cardList.getError_message(), Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(getContext(), cardList.getError_message(), Toast.LENGTH_LONG).show();
                        }

                        hd.dismiss();
                    }

                    @Override
                    public void onError(Call call, okhttp3.Response response, Exception e) {
                        Toast.makeText(getContext(), "请求失败", Toast.LENGTH_LONG).show();
                        hd.dismiss();
                        super.onError(call, response, e);
                    }
                });

    }

    //获取取现记录
    private void getCashRecords() {
        if(!DeviceUtil.IsNetWork(getContext())){
            Toast.makeText(getContext(), "网络异常", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String,String> map=new HashMap<>();
        map.put("token",MyApplication.token);
        JSONObject jsonObject=new JSONObject(map);

        hd = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("获取中...")
                .setDimAmount(0.5f)
                .show();

        OkGo.<String>post(Constants.commonURL + Constants.queryList)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        LogcatUtil.printLogcat( s);
                        Gson gson = new Gson();
                        CashRecords cashRecords = gson.fromJson(s, CashRecords.class);

                        if (cashRecords.getError_code() == 0) {
//                            Toast.makeText(getContext(),"获取成功",Toast.LENGTH_LONG).show();
                            MyApplication.cashRecords=cashRecords;
                            Intent intent=new Intent(getContext(), CashRecordsActivity.class);
                            startActivity(intent);
                        }else if(cashRecords.getError_code() == 2){
                            //token过期，重新登录
                            startActivity(new Intent(getContext(), LoginActivity.class));
                            Toast.makeText(getContext(), cashRecords.getError_message(), Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(getContext(), cashRecords.getError_message(), Toast.LENGTH_LONG).show();
                        }

                        hd.dismiss();
                    }

                    @Override
                    public void onError(Call call, okhttp3.Response response, Exception e) {
                        Toast.makeText(getContext(), "请求失败", Toast.LENGTH_LONG).show();
                        hd.dismiss();
                        super.onError(call, response, e);
                    }
                });
    }


    // 广播接收者
    private class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取Intent中的Action
            String action = intent.getAction();
            // 判断Action
            if (Constants.INTENT_EXTRA_LOGIN_SUCESS.equals(action)) {
                login();
            }else if (Constants.INTENT_EXTRA_EXIT.equals(action)){
                exit();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 104:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(getContext(), "获取拨打电话权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
