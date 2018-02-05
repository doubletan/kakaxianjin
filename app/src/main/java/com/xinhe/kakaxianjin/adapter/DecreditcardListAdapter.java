package com.xinhe.kakaxianjin.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xinhe.kakaxianjin.R;
import com.xinhe.kakaxianjin.Utils.BankUtils;
import com.xinhe.kakaxianjin.activity.CardActivity;
import com.xinhe.kakaxianjin.bean.CardList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tantan on 2018/1/25.
 */

public class DecreditcardListAdapter extends BaseAdapter<CardList.DataProduct.DecreditCardProduct>{
    private final Context context;
    private List<CardList.DataProduct.DecreditCardProduct> data;

    public DecreditcardListAdapter(Context context, List<CardList.DataProduct.DecreditCardProduct> data) {
        super(context, data);
        if (data == null) {
            this.data = new ArrayList<CardList.DataProduct.DecreditCardProduct>();
        } else {
            this.data = data;
        }

        this.context=context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取当前第position条列表项需要显示的数据
        CardList.DataProduct.DecreditCardProduct cr = data.get(position);

        // 准备ViewHolder
        ViewHolder holder;

        // 判断convertView是否是重复使用的，如果不是，则convertView为null，需要从头加载布局等，否则，convertView是被重复使用的，则无须再次根据模板加载对象
        if (convertView == null) {
            // 加载模板得到View对象
            convertView = getLayoutInflater().inflate(R.layout.depositcard_list_item, null);
            // 创建新的ViewHolder
            holder = new ViewHolder();
            // 从模板对象中获取控件
            holder.t1 = (TextView) convertView.findViewById(R.id.depositcard_list_item_tv1);
            holder.t2 = (TextView) convertView.findViewById(R.id.depositcard_list_item_tv3);
            holder.t3 = (TextView) convertView.findViewById(R.id.depositcard_list_item_tv4);
            holder.rl1 = (RelativeLayout) convertView.findViewById(R.id.depositcard_list_item_rl1);
            holder.i1 = (ImageView) convertView.findViewById(R.id.depositcard_list_item_iv);
            // 将TextView封装到convertView中
            convertView.setTag(holder);
        } else {
            // 从convertView中获取之前封装的数据
            holder = (ViewHolder) convertView.getTag();
        }


        // 设置显示数据
        holder.t1.setText(cr.getBankNameTrans());
        holder.t2.setText(cr.getDecredit_card());
        holder.rl1.setBackgroundResource(BankUtils.getBankBackground(cr.getBankNameTrans()));
        Glide.with(context).load(cr.getBank_logo()).crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into( holder.i1);
        holder.t3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,CardActivity.class);
                intent.putExtra("count",1);
                context.startActivity(intent);
            }
        });
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
        RelativeLayout rl1;
        ImageView i1;
    }

}
