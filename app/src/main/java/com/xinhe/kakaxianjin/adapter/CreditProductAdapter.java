package com.xinhe.kakaxianjin.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xinhe.kakaxianjin.R;
import com.xinhe.kakaxianjin.Utils.Constants;
import com.xinhe.kakaxianjin.bean.CreditProduct;

import java.util.ArrayList;

/**
 * Created by tantan on 2017/7/11.
 */

public class CreditProductAdapter extends BaseQuickAdapter<CreditProduct.CardListProduct,BaseViewHolder> {


    public CreditProductAdapter(ArrayList<CreditProduct.CardListProduct> products) {
        super(R.layout.main_fragment_item, products);
    }


    @Override
    protected void convert(BaseViewHolder helper, CreditProduct.CardListProduct item) {
        helper.setText(R.id.main_fragment_item_tv1,item.getCname());
        helper.setText(R.id.main_fragment_item_tv2,item.getJianjie());
        Glide.with(mContext).load(Constants.piURL+item.getLogo()).crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into((ImageView) helper.getView(R.id.main_fragment_item_iv));
    }

}
