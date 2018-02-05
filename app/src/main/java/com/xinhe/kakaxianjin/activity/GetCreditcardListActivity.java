package com.xinhe.kakaxianjin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.xinhe.kakaxianjin.MyApplication;
import com.xinhe.kakaxianjin.R;
import com.xinhe.kakaxianjin.Utils.Constants;
import com.xinhe.kakaxianjin.Utils.DeviceUtil;
import com.xinhe.kakaxianjin.Utils.ExceptionUtil;
import com.xinhe.kakaxianjin.adapter.CreditProductAdapter1;
import com.xinhe.kakaxianjin.bean.CreditProduct;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.lzy.okgo.OkGo.getContext;

public class GetCreditcardListActivity extends AppCompatActivity {

    @BindView(R.id.my_toolbar_tv)
    TextView myToolbarTv;
    @BindView(R.id.get_creditcard_list_lv)
    ListView getCreditcardListLv;
    private KProgressHUD hd;
    private ArrayList<CreditProduct.CardListProduct> creditProducts=new ArrayList<>();
    private CreditProductAdapter1 adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_creditcard_list);
        try {
            ButterKnife.bind(this);
            //初始化toobar
            initActionBar();
            //初始化
            setViews();
            //监听
            setListeners();
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }

    private void setViews() {
        //产品
        if (null!= MyApplication.creditProduct){
            creditProducts.addAll(MyApplication.creditProduct.getCardList());
        }else {
            getProduct();
        }

        adapter = new CreditProductAdapter1(getContext(),creditProducts);
        getCreditcardListLv.setAdapter(adapter);
    }

    public void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        myToolbarTv.setText("办卡");
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void setListeners() {

        getCreditcardListLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    private void getProduct() {
        if (DeviceUtil.IsNetWork(getContext()) == false) {
            Toast.makeText(getContext(), "网络未连接", Toast.LENGTH_SHORT).show();
            return;
        }
        hd = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("获取中...")
                .setDimAmount(0.5f)
                .show();

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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(str) && !str.startsWith("1,") && !str.startsWith("2,")) {
                                Gson gson = new Gson();
                                CreditProduct creditProduct = gson.fromJson(str, CreditProduct.class);
                                creditProducts.clear();
                                creditProducts.addAll(creditProduct.getCardList());
                                adapter.notifyDataSetChanged();
                                MyApplication.creditProduct=creditProduct;
                            } else {
                                Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                            }
                            hd.dismiss();
                        }
                    });

                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ExceptionUtil.handleException(e);
                            hd.dismiss();
                            Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();
    }
}
