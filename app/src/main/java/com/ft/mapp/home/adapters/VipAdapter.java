package com.ft.mapp.home.adapters;

import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ft.mapp.R;
import com.xqb.user.bean.VipProductInfo;

import java.util.List;

public class VipAdapter extends BaseAdapter {

    private List<VipProductInfo.Product> products;

    public VipAdapter(List<VipProductInfo.Product> products) {
        this.products = products;
    }

    public void choose(int position) {
        if (products.get(position).isChosen){
            return;
        }
        for (int i = 0; i < products.size(); i++) {
            products.get(i).isChosen = position == i;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return products == null ? 0 : products.size();
    }

    @Override
    public VipProductInfo.Product getItem(int i) {
        return products.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh;
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_vip_level, viewGroup, false);
            vh = new ViewHolder(view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }

        VipProductInfo.Product product = products.get(i);
        if (TextUtils.isEmpty(product.activity)) {
            vh.tvActivity.setVisibility(View.GONE);
        } else {
            vh.tvActivity.setVisibility(View.VISIBLE);
            vh.tvActivity.setText(product.activity);
        }

        if (TextUtils.isEmpty(product.prePrice)) {
            vh.tvPrePrice.setVisibility(View.GONE);
        } else {
            vh.tvPrePrice.setVisibility(View.VISIBLE);
            String prePrice = "原价￥"+product.prePrice;
            vh.tvPrePrice.setText(prePrice);
            vh.tvPrePrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if (product.isChosen) {
            vh.layoutContainer.setBackgroundResource(R.drawable.bg_vip_level_sel);
        } else {
            vh.layoutContainer.setBackgroundResource(R.drawable.bg_vip_level);
        }

        String price = "￥"+product.price;
        vh.tvPrice.setText(price);
        vh.tvTitle.setText(product.title);
        vh.tvDesc.setText(product.desc);

        return view;
    }

    class ViewHolder {
        RelativeLayout layoutContainer;
        TextView tvTitle;
        TextView tvDesc;
        TextView tvActivity;
        TextView tvPrice;
        TextView tvPrePrice;

        private ViewHolder(View view) {
            tvActivity = view.findViewById(R.id.item_vip_level_tv_activity);
            tvTitle = view.findViewById(R.id.item_vip_level_tv_title);
            tvDesc = view.findViewById(R.id.item_vip_level_tv_desc);
            tvPrice = view.findViewById(R.id.item_vip_level_tv_price);
            tvPrePrice = view.findViewById(R.id.item_vip_level_tv_pre_price);
            layoutContainer = view.findViewById(R.id.item_vip_level_layout_container);
        }

    }

}
