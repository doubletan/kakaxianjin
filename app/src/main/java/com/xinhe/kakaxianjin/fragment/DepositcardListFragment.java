package com.xinhe.kakaxianjin.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.xinhe.kakaxianjin.activity.CardActivity;
import com.xinhe.kakaxianjin.activity.CreditcardActivity;
import com.xinhe.kakaxianjin.activity.IDVerifyActivity;
import com.xinhe.kakaxianjin.activity.LoginActivity;
import com.xinhe.kakaxianjin.activity.RegisterActivity;
import com.xinhe.kakaxianjin.adapter.DecreditcardListAdapter;
import com.xinhe.kakaxianjin.bean.CardList;
import com.xinhe.kakaxianjin.bean.StateMessage;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;


public class DepositcardListFragment extends Fragment {

    @BindView(R.id.depositcard_list_tv)
    TextView depositcardListTv;
    @BindView(R.id.depositcard_list_ll)
    LinearLayout depositcardListLl;
    @BindView(R.id.depositcard_list_lv)
    ListView depositcardListLv;
    Unbinder unbinder;
    private View view;
    private List<CardList.DataProduct.DecreditCardProduct> cards=new ArrayList<>();
    private DecreditcardListAdapter adapter;
    private InnerReceiver receiver;
    private KProgressHUD hd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.fragment_depositcard_list, null);
        try {
            unbinder = ButterKnife.bind(this, view);
            //初始化
            setViews();

        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
        return view;
    }

    private void setViews() {

        // 注册广播接收者
        receiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_EXTRA_CARD_LIST_CHANGE);
        getContext().registerReceiver(receiver, filter);

        List<CardList.DataProduct.DecreditCardProduct> cards1 = MyApplication.cardList.getData().getDecredit_card();
        if (null != cards1 && cards1.size() > 0) {
            cards.addAll(cards1);
            depositcardListLv.setVisibility(View.VISIBLE);
        }else {
            depositcardListLv.setVisibility(View.GONE);
        }
        adapter=new DecreditcardListAdapter(getContext(),cards);
        depositcardListLv.setAdapter(adapter);
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
        if(!DeviceUtil.IsNetWork(getContext())){
            Toast.makeText(getContext(), "网络异常", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String,String> map=new HashMap<>();
        map.put("token",MyApplication.token);
        JSONObject jsonObject=new JSONObject(map);


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
                            MyApplication.cardList=cardList;
                            if (null != cardList.getData().getDecredit_card() && cardList.getData().getDecredit_card().size() > 0) {
                                cards.clear();
                                cards.addAll(cardList.getData().getDecredit_card());
                                depositcardListLv.setVisibility(View.VISIBLE);
                                adapter.notifyDataSetChanged();
                            }else {
                                depositcardListLv.setVisibility(View.GONE);
                            }
                        }else if(cardList.getError_code() == 2){
                            //token过期，重新登录
                            startActivity(new Intent(getContext(), LoginActivity.class));
                            Toast.makeText(getContext(), cardList.getError_message(), Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(getContext(), cardList.getError_message(), Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onError(Call call, okhttp3.Response response, Exception e) {
                        Toast.makeText(getContext(), "请求失败", Toast.LENGTH_LONG).show();
                        super.onError(call, response, e);
                    }
                });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        getContext().unregisterReceiver(receiver);
    }

    @OnClick(R.id.depositcard_list_tv)
    public void onViewClicked() {
        Intent intent=new Intent(getContext(),CardActivity.class);
        intent.putExtra("count",1);
        startActivity(intent);
        //        getState();
    }

    //获取状态
    private void getState() {

        if(!DeviceUtil.IsNetWork(getContext())){
            Toast.makeText(getContext(), "网络异常", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String,String> map=new HashMap<>();
        map.put("token",MyApplication.token);
        JSONObject jsonObject=new JSONObject(map);

        hd = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("加载中...")
                .setDimAmount(0.5f)
                .show();

        OkGo.post(Constants.commonURL + Constants.getStatus)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        LogcatUtil.printLogcat( s);
                        Gson gson = new Gson();
                        StateMessage stateMessage = gson.fromJson(s, StateMessage.class);

                        if (stateMessage.getError_code() == 0) {
                            if ("0".equals(stateMessage.getData().getId_card_status())){
                                Intent intent=new Intent(getActivity(),IDVerifyActivity.class);
                                startActivity(intent);
                            }else if ("0".equals(stateMessage.getData().getDebit_card_status())){
                                Intent intent=new Intent(getContext(),CardActivity.class);
                                intent.putExtra("count",0);
                                startActivity(intent);
                            }else if ("0".equals(stateMessage.getData().getCredit_card_status())){
                                Intent intent=new Intent(getContext(),CreditcardActivity.class);
                                intent.putExtra("count",0);
                                startActivity(intent);
                            }else {
                                Intent intent=new Intent(getContext(),CardActivity.class);
                                intent.putExtra("count",1);
                                startActivity(intent);
                            }
                        }else if(stateMessage.getError_code() == 2){
                            //token过期，重新登录
                            startActivity(new Intent(getContext(), LoginActivity.class));
                            Toast.makeText(getContext(), stateMessage.getError_message(), Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(getContext(), stateMessage.getError_message(), Toast.LENGTH_LONG).show();
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
}
