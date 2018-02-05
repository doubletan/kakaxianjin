package com.xinhe.kakaxianjin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xinhe.kakaxianjin.R;
import com.xinhe.kakaxianjin.bean.BankListMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tantan on 2018/1/27.
 */

public class BankListAdapter extends BaseAdapter<BankListMessage.DataProduct>{
    private Context context;
    private List<BankListMessage.DataProduct> data;

    public BankListAdapter(Context context, List<BankListMessage.DataProduct> data) {
        super(context, data);
        if (data == null) {
            this.data = new ArrayList<BankListMessage.DataProduct>();
        } else {
            this.data = data;
        }

        this.context=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取当前第position条列表项需要显示的数据
        BankListMessage.DataProduct cr = data.get(position);

        // 准备ViewHolder
        ViewHolder holder;

        // 判断convertView是否是重复使用的，如果不是，则convertView为null，需要从头加载布局等，否则，convertView是被重复使用的，则无须再次根据模板加载对象
        if (convertView == null) {
            // 加载模板得到View对象
            convertView = getLayoutInflater().inflate(R.layout.bank_list_item, null);
            // 创建新的ViewHolder
            holder = new ViewHolder();
            // 从模板对象中获取控件
            holder.t1 = (TextView) convertView.findViewById(R.id.bank_list_item_tv);
            holder.i1 = (ImageView) convertView.findViewById(R.id.bank_list_item_iv);
            // 将TextView封装到convertView中
            convertView.setTag(holder);
        } else {
            // 从convertView中获取之前封装的数据
            holder = (ViewHolder) convertView.getTag();
        }


        // 设置显示数据
        holder.t1.setText(cr.getBank_name());
        Glide.with(context).load(cr.getBank_logo()).crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.i1);
        // 返回由数据和模板组装成的列表项对象
        return convertView;
    }

    /**
     * View控件的持有者
     */
    class ViewHolder {
        TextView t1;
        ImageView i1;
    }
}
