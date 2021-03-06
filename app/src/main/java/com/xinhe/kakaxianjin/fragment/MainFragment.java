package com.xinhe.kakaxianjin.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sunfusheng.marqueeview.MarqueeView;
import com.xinhe.kakaxianjin.MyApplication;
import com.xinhe.kakaxianjin.R;
import com.xinhe.kakaxianjin.Utils.Constants;
import com.xinhe.kakaxianjin.Utils.DeviceUtil;
import com.xinhe.kakaxianjin.Utils.ExceptionUtil;
import com.xinhe.kakaxianjin.Utils.LogcatUtil;
import com.xinhe.kakaxianjin.Utils.Utill;
import com.xinhe.kakaxianjin.activity.ChooseCreditcardActivity;
import com.xinhe.kakaxianjin.activity.GetCreditcardListActivity;
import com.xinhe.kakaxianjin.activity.IDVerifyActivity;
import com.xinhe.kakaxianjin.activity.LoginActivity;
import com.xinhe.kakaxianjin.activity.WebViewActivity;
import com.xinhe.kakaxianjin.activity.WebViewTitleActivity;
import com.xinhe.kakaxianjin.adapter.MainCreditProductAdapter;
import com.xinhe.kakaxianjin.bean.CardList;
import com.xinhe.kakaxianjin.bean.CreditProduct;
import com.xinhe.kakaxianjin.bean.DaiHuanMessage;
import com.xinhe.kakaxianjin.bean.StateMessage;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by tantan on 2018/1/15.
 */

public class MainFragment extends Fragment {

    @BindView(R.id.main_fragment_gv)
    GridView mainFragmentGv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    Unbinder unbinder;
    @BindView(R.id.main_fragment_top)
    LinearLayout mainFragmentTop;
    @BindView(R.id.main_fragment_rl1)
    LinearLayout mainFragmentRl1;
    @BindView(R.id.main_fragment_rl2)
    LinearLayout mainFragmentRl2;
    @BindView(R.id.main_fragment_rl3)
    LinearLayout mainFragmentRl3;
    @BindView(R.id.main_fragment_rl4)
    LinearLayout mainFragmentRl4;
    @BindView(R.id.main_activity_buttom_ll)
    LinearLayout mainActivityButtomLl;
    @BindView(R.id.main_fragment_messages_text)
    MarqueeView mainFragmentMessagesText;
    @BindView(R.id.main_fragment_ll1)
    LinearLayout mainFragmentLl1;
    @BindView(R.id.main_fragment_ll2)
    LinearLayout mainFragmentLl2;
    Unbinder unbinder1;
    private View view;
    private ArrayList<CreditProduct.CardListProduct> creditProducts = new ArrayList<>();
    private MainCreditProductAdapter adapter1;
    //申请人消息
    private ArrayList<String> messageList = new ArrayList<String>();
    private String[] phone = {"3", "5", "8", "7"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            view = View.inflate(getActivity(), R.layout.main_fragment, null);
            unbinder = ButterKnife.bind(this, view);

//            // 设置顶部控件不占据状态栏
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                LinearLayout barLl = (LinearLayout) view.findViewById(R.id.main_fragment_top);
//                barLl.setVisibility(View.VISIBLE);
//                LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) barLl.getLayoutParams();
//                ll.height = Utill.getStatusBarHeight(getContext());
//                ll.width = RelativeLayout.LayoutParams.MATCH_PARENT;
//                barLl.setLayoutParams(ll);
//            }

            //初始化刷新
            initRefresh();
            //初始化
            setViews();
            //设置监听
            setListeners();
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }

        unbinder1 = ButterKnife.bind(this, view);
        return view;
    }

    private void setListeners() {
        mainFragmentGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (MyApplication.isLogin) {
                    Intent intent = new Intent(getContext(), WebViewActivity.class);
                    intent.putExtra("url", creditProducts.get(position).getClink());
                    intent.putExtra("title", creditProducts.get(position).getCname());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void setViews() {
        mainFragmentGv.setFocusable(false);

        //申请人消息滚动
        getMessageLists();
        mainFragmentMessagesText.startWithList(messageList);

//        //申请人数
//        int l = (int)((System.currentTimeMillis()/1000)%86400)/2;
//        mainFragmentApplyCount.setText("今日已有"+l+"人申请成功");

        //新品
        if (null != MyApplication.creditProduct) {
            creditProducts = MyApplication.creditProduct.getCardList();
        } else {
            refreshLayout.autoRefresh();
        }
        adapter1 = new MainCreditProductAdapter(getContext(), creditProducts);
        mainFragmentGv.setAdapter(adapter1);
        Utill.resetGridViewHight(mainFragmentGv);
    }

    //随机申请人消息
    private void getMessageLists() {

        messageList.clear();
        for (int i=0;i<20;i++){
            String phone1 = "1" + phone[new Random().nextInt(4)] + new Random().nextInt(10) + "****" + (new Random().nextInt(8999) + 1000);
            int money = new Random().nextInt(19500) + 500;
            messageList.add(phone1+"通过信用卡取现"+money+".0元");
        }
    }

    private void initRefresh() {
        //设置 Header 为 Material样式
        refreshLayout.setRefreshHeader(new MaterialHeader(getContext()).setShowBezierWave(true).setShowBezierWave(false));
        refreshLayout.setEnableHeaderTranslationContent(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                if (DeviceUtil.IsNetWork(getContext()) == false) {
                    Toast.makeText(getContext(), "网络异常", Toast.LENGTH_SHORT).show();
                    refreshlayout.finishRefresh();
                    return;
                }

                //申请人消息滚动
                getMessageLists();
                mainFragmentMessagesText.startWithList(messageList);

//                //申请人数
//                int l = (int)((System.currentTimeMillis()/1000)%86400)/2;
//                mainFragmentApplyCount.setText("今日已有"+l+"人申请成功");


                //获取数据
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String URL = Constants.URL;
                        String nameSpace = Constants.nameSpace;
                        String method_Name = "CreditCardList";
                        String SOAP_ACTION = nameSpace + method_Name;
                        SoapObject rpc = new SoapObject(nameSpace, method_Name);
                        rpc.addProperty("channel", Constants.qudao);
                        HttpTransportSE transport = new HttpTransportSE(URL);
                        transport.debug = true;
                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        envelope.bodyOut = rpc;
                        envelope.dotNet = true;
                        envelope.setOutputSoapObject(rpc);
                        try {
                            transport.call(SOAP_ACTION, envelope);
                            SoapObject object = (SoapObject) envelope.bodyIn;
                            final String str = object.getProperty("CreditCardListResult").toString();

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!TextUtils.isEmpty(str) && !str.startsWith("1,") && !str.startsWith("2,")) {
                                        Gson gson = new Gson();
                                        CreditProduct creditProduct = gson.fromJson(str, CreditProduct.class);
                                        MyApplication.creditProduct = creditProduct;
                                        creditProducts.clear();
                                        creditProducts.addAll(creditProduct.getCardList());
                                        adapter1.notifyDataSetChanged();
                                        Utill.resetGridViewHight(mainFragmentGv);
                                        //刷新停止
                                        refreshlayout.finishRefresh();
                                    } else {
                                        Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } catch (final Exception e) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ExceptionUtil.handleException(e);
                                    Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                }).start();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.main_fragment_ll1, R.id.main_fragment_ll2,R.id.main_fragment_rl1, R.id.main_fragment_rl2, R.id.main_fragment_rl3, R.id.main_fragment_rl4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_fragment_ll1:
                if (MyApplication.isLogin) {
                    getCardList();
                } else {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.main_fragment_ll2:
                if (MyApplication.isLogin) {
                    getState();
                } else {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.main_fragment_rl1:
                if (MyApplication.isLogin) {
                    Intent intent = new Intent(getContext(), WebViewTitleActivity.class);
                    intent.putExtra("url", Constants.progress);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.main_fragment_rl2:
                if (MyApplication.isLogin) {
                    Intent intent1 = new Intent(getContext(), GetCreditcardListActivity.class);
                    intent1.putExtra("url", Constants.card);
                    startActivity(intent1);
                } else {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }

                break;
            case R.id.main_fragment_rl3:
                if (MyApplication.isLogin) {
                    Intent intent2 = new Intent(getContext(), WebViewTitleActivity.class);
                    intent2.putExtra("url", Constants.gonglue);
                    startActivity(intent2);
                } else {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }

                break;
            case R.id.main_fragment_rl4:
                if (MyApplication.isLogin) {
                    Intent intent2 = new Intent(getContext(), WebViewTitleActivity.class);
                    intent2.putExtra("url", Constants.jifen);
                    startActivity(intent2);
                } else {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }

                break;
        }
    }

    //获取信用卡、储蓄卡列表
    private void getCardList() {
        if (!DeviceUtil.IsNetWork(getContext())) {
            Toast.makeText(getContext(), "网络异常", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("token", MyApplication.token);
        JSONObject jsonObject = new JSONObject(map);

        KProgressHUD hd = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("获取数据中...")
                .setDimAmount(0.5f)
                .show();

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
//                            Toast.makeText(getContext(),"获取成功",Toast.LENGTH_LONG).show();
                            MyApplication.cardList = cardList;
                            startActivity(new Intent(getContext(), ChooseCreditcardActivity.class));
                        } else if (cardList.getError_code() == 2) {
                            //token过期，重新登录
                            startActivity(new Intent(getContext(), LoginActivity.class));
                            Toast.makeText(getContext(), cardList.getError_message(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), cardList.getError_message(), Toast.LENGTH_LONG).show();
                        }

                        hd.dismiss();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Toast.makeText(getContext(), "请求失败", Toast.LENGTH_LONG).show();
                        hd.dismiss();
                        super.onError(call, response, e);
                    }
                });

    }


    //获取状态
    private void getState() {

        if (!DeviceUtil.IsNetWork(getContext())) {
            Toast.makeText(getContext(), "网络异常", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("token", MyApplication.token);
        JSONObject jsonObject = new JSONObject(map);

        KProgressHUD hd = KProgressHUD.create(getContext())
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
                                Intent intent = new Intent(getContext(), IDVerifyActivity.class);
                                intent.putExtra("count", 1);
                                startActivity(intent);
                            } else {
                                getDaiHuanUrl();
                            }
                        } else if (stateMessage.getError_code() == 2) {
                            //token过期，重新登录
                            startActivity(new Intent(getContext(), LoginActivity.class));
                            Toast.makeText(getContext(), stateMessage.getError_message(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), stateMessage.getError_message(), Toast.LENGTH_LONG).show();
                        }

                        hd.dismiss();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Toast.makeText(getContext(), "请求失败", Toast.LENGTH_LONG).show();
                        hd.dismiss();
                        super.onError(call, response, e);
                    }
                });

    }

    //获取贷款链接
    private void getDaiHuanUrl() {
        if (!DeviceUtil.IsNetWork(getContext())) {
            Toast.makeText(getContext(), "网络异常", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("token", MyApplication.token);
        JSONObject jsonObject = new JSONObject(map);

        KProgressHUD hd = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("获取数据中...")
                .setDimAmount(0.5f)
                .show();

        OkGo.post(Constants.commonURL + Constants.fetch)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LogcatUtil.printLogcat(s);
                        Gson gson = new Gson();
                        DaiHuanMessage message = gson.fromJson(s, DaiHuanMessage.class);

                        if (message.getError_code() == 0) {
                            Intent intent1 = new Intent(getContext(), WebViewActivity.class);
                            intent1.putExtra("url", message.getData().getUrl());
                            intent1.putExtra("title", "");
                            startActivity(intent1);
                        } else if (message.getError_code() == 2) {
                            //token过期，重新登录
                            startActivity(new Intent(getContext(), LoginActivity.class));
                            Toast.makeText(getContext(), message.getError_message(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), message.getError_message(), Toast.LENGTH_LONG).show();
                        }

                        hd.dismiss();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Toast.makeText(getContext(), "请求失败", Toast.LENGTH_LONG).show();
                        hd.dismiss();
                        super.onError(call, response, e);
                    }
                });
    }


    @Override
    public void onResume() {
        super.onResume();
        mainFragmentMessagesText.startFlipping();
    }

    @Override
    public void onPause() {
        super.onPause();
        mainFragmentMessagesText.stopFlipping();
    }
}
