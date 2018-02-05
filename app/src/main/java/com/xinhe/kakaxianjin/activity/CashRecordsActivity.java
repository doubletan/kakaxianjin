package com.xinhe.kakaxianjin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xinhe.kakaxianjin.MyApplication;
import com.xinhe.kakaxianjin.R;
import com.xinhe.kakaxianjin.Utils.Constants;
import com.xinhe.kakaxianjin.Utils.DeviceUtil;
import com.xinhe.kakaxianjin.Utils.ExceptionUtil;
import com.xinhe.kakaxianjin.Utils.LogcatUtil;
import com.xinhe.kakaxianjin.adapter.CashRecordsAdapter;
import com.xinhe.kakaxianjin.bean.CashRecords;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class CashRecordsActivity extends BaseActivity {


    @BindView(R.id.my_toolbar_tv)
    TextView myToolbarTv;
    @BindView(R.id.cash_records_lv)
    ListView cashRecordsLv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private List<CashRecords.DataProduct.ListProduct> dataProducts = new ArrayList<CashRecords.DataProduct.ListProduct>();
    private CashRecordsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_records);
        try {
            ButterKnife.bind(this);
            //初始化刷新
            initRefresh();
            //初始化toobar
            initActionBar();
            //初始化
            setViews();
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }

    private void setViews() {
        CashRecords cashRecords =MyApplication.cashRecords;
        List<CashRecords.DataProduct.ListProduct> dataProducts1 = cashRecords.getData().getList();
        if (null != dataProducts1 && dataProducts1.size() > 0) {
            dataProducts.addAll(dataProducts1);
            cashRecordsLv.setVisibility(View.VISIBLE);
        } else {
            cashRecordsLv.setVisibility(View.GONE);
        }
        adapter = new CashRecordsAdapter(this,dataProducts);
        cashRecordsLv.setAdapter(adapter);
    }

    public void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        myToolbarTv.setText("收款记录");
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initRefresh() {
        //设置 Header 为 Material样式
        refreshLayout.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(true).setShowBezierWave(false));
        refreshLayout.setEnableHeaderTranslationContent(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                if (DeviceUtil.IsNetWork(CashRecordsActivity.this) == false) {
                    Toast.makeText(CashRecordsActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    refreshlayout.finishRefresh();
                    return;
                }

                //获取数据
                Map<String, String> map = new HashMap<>();
                map.put("token", MyApplication.token);
                JSONObject jsonObject = new JSONObject(map);


                OkGo.<String>post(Constants.commonURL + Constants.queryList)
                        .tag(this)
                        .upJson(jsonObject)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, okhttp3.Response response) {
                                LogcatUtil.printLogcat(s);
                                Gson gson = new Gson();
                                CashRecords cashRecords = gson.fromJson(s, CashRecords.class);

                                if (cashRecords.getError_code() == 0) {
                                    dataProducts.clear();
                                    if (null != cashRecords.getData() && cashRecords.getData().getList().size() > 0) {
                                        dataProducts.addAll(cashRecords.getData().getList());
                                        cashRecordsLv.setVisibility(View.VISIBLE);
                                    } else {
                                        cashRecordsLv.setVisibility(View.GONE);
                                    }
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(CashRecordsActivity.this, "刷新成功", Toast.LENGTH_LONG).show();
                                    refreshlayout.finishRefresh();
                                } else if (cashRecords.getError_code() == 2) {
                                    //token过期，重新登录
                                    startActivity(new Intent(CashRecordsActivity.this, LoginActivity.class));
                                    Toast.makeText(CashRecordsActivity.this, cashRecords.getError_message(), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(CashRecordsActivity.this, cashRecords.getError_message(), Toast.LENGTH_LONG).show();
                                }
                                refreshlayout.finishRefresh();
                            }

                            @Override
                            public void onError(Call call, okhttp3.Response response, Exception e) {
                                Toast.makeText(CashRecordsActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                                refreshlayout.finishRefresh();
                                super.onError(call, response, e);
                            }
                        });
            }
        });
    }
}
