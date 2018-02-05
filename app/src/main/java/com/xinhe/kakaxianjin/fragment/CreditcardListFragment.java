package com.xinhe.kakaxianjin.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.xinhe.kakaxianjin.Utils.Utill;
import com.xinhe.kakaxianjin.activity.CardActivity;
import com.xinhe.kakaxianjin.activity.CreditcardActivity;
import com.xinhe.kakaxianjin.activity.IDVerifyActivity;
import com.xinhe.kakaxianjin.activity.LoginActivity;
import com.xinhe.kakaxianjin.activity.RegisterActivity;
import com.xinhe.kakaxianjin.adapter.BaseAdapter;
import com.xinhe.kakaxianjin.bean.CardList;
import com.xinhe.kakaxianjin.bean.DeleteCreditcardMessage;
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
import okhttp3.Response;

public class CreditcardListFragment extends Fragment {


    @BindView(R.id.creditcard_list_tv)
    TextView creditcardListTv;
    @BindView(R.id.creditcard_list_lv)
    ListView creditcardListLv;
    @BindView(R.id.creditcard_list_sv)
    ScrollView creditcardListSv;
    Unbinder unbinder;
    Unbinder unbinder1;
    private View view;
    private List<CardList.DataProduct.CreditCardProduct> cards = new ArrayList<>();
    private CreditcardListAdapter adapter;
    private KProgressHUD hd;
    private InnerReceiver receiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.fragment_creditcard_list, null);
        try {
            unbinder = ButterKnife.bind(this, view);
            //初始化
            setViews();

        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
        unbinder1 = ButterKnife.bind(this, view);
        return view;
    }

    private void setViews() {

        // 注册广播接收者
        receiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_EXTRA_CARD_LIST_CHANGE);
        getContext().registerReceiver(receiver, filter);

        List<CardList.DataProduct.CreditCardProduct> cards1 = MyApplication.cardList.getData().getCredit_card();
        if (null != cards1 && cards1.size() > 0) {
            cards.addAll(cards1);
            creditcardListSv.setVisibility(View.VISIBLE);
        } else {
            creditcardListSv.setVisibility(View.GONE);
        }
        adapter = new CreditcardListAdapter(getContext(), cards);
        creditcardListLv.setAdapter(adapter);
        Utill.resetHight(creditcardListLv);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        getContext().unregisterReceiver(receiver);
    }

    @OnClick({R.id.creditcard_list_tv, R.id.creditcard_list_ll1})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.creditcard_list_tv:
                getState();
                break;
            case R.id.creditcard_list_ll1:
                Intent intent = new Intent(getContext(), CreditcardActivity.class);
                intent.putExtra("count", 1);
                startActivity(intent);
                break;
        }
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
                    public void onSuccess(String s, Call call, Response response) {
                        LogcatUtil.printLogcat(s);
                        Gson gson = new Gson();
                        StateMessage stateMessage = gson.fromJson(s, StateMessage.class);

                        if (stateMessage.getError_code() == 0) {
                            if ("0".equals(stateMessage.getData().getId_card_status())) {
                                Intent intent = new Intent(getActivity(), IDVerifyActivity.class);
                                startActivity(intent);
                            } else if ("0".equals(stateMessage.getData().getDebit_card_status())) {
                                Intent intent = new Intent(getContext(), CardActivity.class);
                                intent.putExtra("count", 0);
                                startActivity(intent);
                            } else if ("0".equals(stateMessage.getData().getCredit_card_status())) {
                                Intent intent = new Intent(getContext(), CreditcardActivity.class);
                                intent.putExtra("count", 0);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getContext(), CreditcardActivity.class);
                                intent.putExtra("count", 1);
                                startActivity(intent);
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


    private class CreditcardListAdapter extends BaseAdapter<CardList.DataProduct.CreditCardProduct> {
        private final Context context;
        private List<CardList.DataProduct.CreditCardProduct> data;

        public CreditcardListAdapter(Context context, List<CardList.DataProduct.CreditCardProduct> data) {
            super(context, data);
            if (data == null) {
                this.data = new ArrayList<CardList.DataProduct.CreditCardProduct>();
            } else {
                this.data = data;
            }

            this.context = context;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // 获取当前第position条列表项需要显示的数据
            CardList.DataProduct.CreditCardProduct cr = data.get(position);

            // 准备ViewHolder
            ViewHolder holder;

            // 判断convertView是否是重复使用的，如果不是，则convertView为null，需要从头加载布局等，否则，convertView是被重复使用的，则无须再次根据模板加载对象
            if (convertView == null) {
                // 加载模板得到View对象
                convertView = getLayoutInflater().inflate(R.layout.creditcard_list_item, null);
                // 创建新的ViewHolder
                holder = new ViewHolder();
                // 从模板对象中获取控件
                holder.t1 = (TextView) convertView.findViewById(R.id.creditcard_list_item_tv1);
                holder.t2 = (TextView) convertView.findViewById(R.id.creditcard_list_item_tv3);
                holder.rl1 = (RelativeLayout) convertView.findViewById(R.id.creditcard_list_item_rl1);
                holder.i1 = (ImageView) convertView.findViewById(R.id.creditcard_list_item_iv);
                holder.i2 = (ImageView) convertView.findViewById(R.id.creditcard_list_item_iv1);
                // 将TextView封装到convertView中
                convertView.setTag(holder);
            } else {
                // 从convertView中获取之前封装的数据
                holder = (ViewHolder) convertView.getTag();
            }


            // 设置显示数据
            holder.t1.setText(cr.getBankNameTrans());
            holder.t2.setText(cr.getCredit_card());
            holder.rl1.setBackgroundResource(BankUtils.getBankBackground(cr.getBankNameTrans()));
            Glide.with(context).load(cr.getBank_logo()).crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.i1);
            holder.i2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(getContext(), R.style.CustomDialog).create();
                    alertDialog.setCancelable(false);
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                    Window window = alertDialog.getWindow();
                    window.setContentView(R.layout.dialog_two);
                    TextView tv1 = (TextView) window.findViewById(R.id.integral_exchange_tips1_tv);
                    tv1.setText("确定要删除该信用卡吗？");
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
                            delete(cr.getId(), position);
                            alertDialog.dismiss();
                        }
                    });
                }
            });
            // 返回由数据和模板组装成的列表项对象
            return convertView;
        }

        //删除
        private void delete(String id, int position) {
            if (!DeviceUtil.IsNetWork(context)) {
                Toast.makeText(context, "网络异常", Toast.LENGTH_LONG).show();
                return;
            }

            hd = KProgressHUD.create(getContext())
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("删除中...")
                    .setDimAmount(0.5f)
                    .show();

            Map<String, String> map = new HashMap<>();
            map.put("id", id);
            map.put("token", MyApplication.token);
            JSONObject jsonObject = new JSONObject(map);


            OkGo.post(Constants.commonURL + Constants.delBank)
                    .tag(this)
                    .upJson(jsonObject)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            LogcatUtil.printLogcat(s);
                            Gson gson = new Gson();
                            DeleteCreditcardMessage deleteCreditcardMessage = gson.fromJson(s, DeleteCreditcardMessage.class);

                            if (deleteCreditcardMessage.getError_code() == 0) {
                                Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                                cards.remove(position);
                                adapter.notifyDataSetChanged();
                                Utill.resetHight(creditcardListLv);
                                if (cards.size() > 0) {
                                    creditcardListSv.setVisibility(View.VISIBLE);
                                } else {
                                    creditcardListSv.setVisibility(View.GONE);
                                }
                            } else if (deleteCreditcardMessage.getError_code() == 2) {
                                //token过期，重新登录
                                startActivity(new Intent(getContext(), LoginActivity.class));
                                Toast.makeText(getContext(), deleteCreditcardMessage.getError_message(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), deleteCreditcardMessage.getError_message(), Toast.LENGTH_LONG).show();

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

        /**
         * View控件的持有者
         */
        class ViewHolder {
            TextView t1;
            TextView t2;
            RelativeLayout rl1;
            ImageView i1;
            ImageView i2;
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
        if (!DeviceUtil.IsNetWork(getContext())) {
            Toast.makeText(getContext(), "网络异常", Toast.LENGTH_LONG).show();
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
                            if (null != cardList.getData().getCredit_card() && cardList.getData().getCredit_card().size() > 0) {
                                cards.clear();
                                cards.addAll(cardList.getData().getCredit_card());
                                creditcardListSv.setVisibility(View.VISIBLE);
                                adapter.notifyDataSetChanged();
                                Utill.resetHight(creditcardListLv);
                            } else {
                                creditcardListSv.setVisibility(View.GONE);
                            }
                        } else if (cardList.getError_code() == 2) {
                            //token过期，重新登录
                            startActivity(new Intent(getContext(), LoginActivity.class));
                            Toast.makeText(getContext(), cardList.getError_message(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), cardList.getError_message(), Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Toast.makeText(getContext(), "请求失败", Toast.LENGTH_LONG).show();
                        super.onError(call, response, e);
                    }
                });

    }
}
