package com.xinhe.kakaxianjin.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xinhe.kakaxianjin.R;
import com.xinhe.kakaxianjin.bean.CashRecords;
import com.xinhe.kakaxianjin.bean.CreditProduct;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tantan on 2018/1/24.
 */

public class CashRecordsAdapter extends BaseAdapter<CashRecords.DataProduct.ListProduct> {

    private  Context context;
    private List<CashRecords.DataProduct.ListProduct> data;

    public CashRecordsAdapter(Context context, List<CashRecords.DataProduct.ListProduct> data) {
        super(context, data);
        if (data == null) {
            this.data = new ArrayList<CashRecords.DataProduct.ListProduct>();
        } else {
            this.data = data;
        }

        this.context=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取当前第position条列表项需要显示的数据
        CashRecords.DataProduct.ListProduct cr = data.get(position);

        // 准备ViewHolder
        ViewHolder holder;

        // 判断convertView是否是重复使用的，如果不是，则convertView为null，需要从头加载布局等，否则，convertView是被重复使用的，则无须再次根据模板加载对象
        if (convertView == null) {
            // 加载模板得到View对象
            convertView = getLayoutInflater().inflate(R.layout.cash_records_item, null);
            // 创建新的ViewHolder
            holder = new ViewHolder();
            // 从模板对象中获取控件
            holder.t1 = (TextView) convertView.findViewById(R.id.cash_records_item_tv3);
            holder.t2 = (TextView) convertView.findViewById(R.id.cash_records_item_tv1);
            holder.t3 = (TextView) convertView.findViewById(R.id.cash_records_item_tv2);
            holder.t4 = (TextView) convertView.findViewById(R.id.cash_records_item_tv6);
            holder.t5 = (TextView) convertView.findViewById(R.id.cash_records_item_tv7);
            holder.t6 = (TextView) convertView.findViewById(R.id.cash_records_item_tv10);
            holder.t7 = (TextView) convertView.findViewById(R.id.cash_records_item_tv11);
            holder.i1 = (ImageView) convertView.findViewById(R.id.cash_records_item_iv1);
            holder.i2 = (ImageView) convertView.findViewById(R.id.cash_records_item_iv2);
            // 将TextView封装到convertView中
            convertView.setTag(holder);
        } else {
            // 从convertView中获取之前封装的数据
            holder = (ViewHolder) convertView.getTag();
        }

        if (0==cr.getIs_success()){
            holder.t1.setTextColor(Color.parseColor("#fb6467"));
            holder.t1.setText("订单失败");
        }else if (1==cr.getIs_success()){
            holder.t1.setTextColor(Color.parseColor("#51b532"));
            holder.t1.setText("订单成功");
        }else if (2==cr.getIs_success()){
            holder.t1.setTextColor(Color.parseColor("#f5ba53"));
            holder.t1.setText("申请中");
        }

        // 设置显示数据
        holder.t2.setText("时  间："+cr.getCreated_time());
        holder.t3.setText("订单号："+cr.getOrder_id());
        holder.t4.setText(cr.getBankNoTrans());
        holder.t5.setText(cr.getTransAmt());
        holder.t6.setText(cr.getDecredit_card());
        holder.t7.setText(cr.getTxnAmtDF());
        Glide.with(context).load(cr.getBank_logo()).crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.i1);
        Glide.with(context).load(cr.getDecredit_bank_logo()).crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.i2);
        // 返回由数据和模板组装成的列表项对象
        return convertView;
    }

    /**
     * View控件的持有者
     */
    class ViewHolder {
        TextView t1;
        TextView t2;
        TextView t3;
        TextView t4;
        TextView t5;
        TextView t6;
        TextView t7;
        ImageView i1;
        ImageView i2;
    }


}
