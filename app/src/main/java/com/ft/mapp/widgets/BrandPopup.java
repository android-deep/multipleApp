package com.ft.mapp.widgets;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ft.mapp.R;
import com.ft.mapp.utils.UIUtils;

import java.util.ArrayList;


public class BrandPopup extends PopupWindow {

    private ListView lvBrand;
    private Context mContext;
    private ArrayList<String> brands;

    private BrandChooseListener brandChooseListener;

    public BrandPopup(@NonNull Context context, ArrayList<String> brands) {
        this.brands = brands;
        this.mContext = context;
        initViews();
    }

    private void initViews() {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_brand, null, false);
        setContentView(contentView);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);
        lvBrand = contentView.findViewById(R.id.brand_list_view);
        lvBrand.setAdapter(new BrandAdapter());
        lvBrand.setOnItemClickListener((parent, view, position, id) -> {
            brandChooseListener.onBrandChosen(brands.get(position));
            dismiss();
        });
    }

    class BrandAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return brands.size();
        }

        @Override
        public String getItem(int position) {
            return brands.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_brand, parent, false);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            vh.tvBrand.setText(brands.get(position));

            return convertView;
        }
    }

    private static class ViewHolder {
        View view;
        TextView tvBrand;

        ViewHolder(View view) {
            this.view = view;
            tvBrand = view.findViewById(R.id.text_item);
        }
    }

    public void setBrandChooseListener(BrandChooseListener brandChooseListener) {
        this.brandChooseListener = brandChooseListener;
    }

    public interface BrandChooseListener{
        void onBrandChosen(String brand);
    }

}
