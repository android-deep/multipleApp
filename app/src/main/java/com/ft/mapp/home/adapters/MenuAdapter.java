package com.ft.mapp.home.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ft.mapp.R;
import com.ft.mapp.home.models.MenuModel;
import com.ft.mapp.widgets.PopupFunMenu;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private List<MenuModel> funs;

    public MenuAdapter(List<MenuModel> funs) {
        this.funs = funs;
    }

    private int itemHeight = -1;

    public int getItemHeight() {
        return itemHeight;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_app, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuModel menuModel = funs.get(position);
        holder.ivIcon.setImageResource(menuModel.getIconRes());
        holder.ivVip.setVisibility(menuModel.isVipFun() ? View.VISIBLE : View.INVISIBLE);
        holder.tvTitle.setText(menuModel.getFunTitle());
        holder.itemView.setOnClickListener(v -> {
            if (onFunClickListener!=null){
                onFunClickListener.onFunClick(menuModel.getMenuType());
            }
        });
    }

    @Override
    public int getItemCount() {
        return funs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
        ImageView ivVip;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.menu_iv_icon);
            ivVip = itemView.findViewById(R.id.menu_iv_vip_label);
            tvTitle = itemView.findViewById(R.id.menu_tv_title);
            if (itemHeight < 0) {
                itemView.getViewTreeObserver().addOnPreDrawListener(() -> {
                    itemHeight = itemView.getMeasuredHeight();
                    return true;
                });
            }
        }
    }

    private OnFunClickListener onFunClickListener;

    public void setOnFunClickListener(OnFunClickListener onFunClickListener) {
        this.onFunClickListener = onFunClickListener;
    }

    public interface OnFunClickListener {
        void onFunClick(PopupFunMenu.MENU_ITEM menu_item);
    }

}
