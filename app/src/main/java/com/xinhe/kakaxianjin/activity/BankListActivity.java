package com.xinhe.kakaxianjin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.xinhe.kakaxianjin.R;
import com.xinhe.kakaxianjin.Utils.ExceptionUtil;
import com.xinhe.kakaxianjin.adapter.BankListAdapter;
import com.xinhe.kakaxianjin.bean.BankListMessage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BankListActivity extends BaseActivity {

    @BindView(R.id.my_toolbar_tv)
    TextView myToolbarTv;
    @BindView(R.id.bank_list_lv)
    ListView bankListLv;
    private List<BankListMessage.DataProduct> banks;
    private BankListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_list);
        try {
            ButterKnife.bind(this);
            //初始化toobar
            initActionBar();
            //初始化
            setViews();
            //设置监听
            setListeners();
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }

    private void setListeners() {
        bankListLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent();
                intent.putExtra("bankName",banks.get(position).getBank_name());
                setResult(-1,intent);
                finish();
            }
        });
    }

    private void setViews() {
        Intent intent=getIntent();
        BankListMessage bank= (BankListMessage) intent.getSerializableExtra("banks");
        banks=bank.getData();
        adapter=new BankListAdapter(this,banks);
        bankListLv.setAdapter(adapter);
    }

    public void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        myToolbarTv.setText("选择银行");
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
