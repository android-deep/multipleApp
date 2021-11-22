package com.ft.mapp.home.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.ft.mapp.R;
import com.ft.mapp.home.models.PluginInfo;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class AppPluginAdapter extends RecyclerView.Adapter<AppPluginAdapter.ViewHolder> {

    private List<PluginInfo> mPluginList;
    private OnItemOperateListener mClickListener;
    private Context mContext;

    public AppPluginAdapter(Context context) {
        this.mContext = context;

    }

    public List<PluginInfo> getList() {
        return mPluginList;
    }

    public void setList(List<PluginInfo> models) {
        this.mPluginList = models;
        notifyDataSetChanged();
    }

    public void updateAddress(String address) {
        for (PluginInfo info : mPluginList) {
            if (info.name.equals(mContext.getString(R.string.plugin_location))) {
                info.desc = address;
                notifyItemChanged(mPluginList.indexOf(info));
                break;
            }
        }
    }

    public void updateDeviceInfo(String simDeviceInfo) {
        for (PluginInfo info : mPluginList) {
            if (info.name.equals(mContext.getString(R.string.menu_mock_phone))) {
                info.desc = simDeviceInfo;
                notifyItemChanged(mPluginList.indexOf(info));
                break;
            }
        }
    }

    public void add(PluginInfo info) {
        if (mPluginList == null) {
            mPluginList = new ArrayList<>();
        }
        mPluginList.add(info);
        notifyDataSetChanged();
    }

    public void clear() {
        if (mPluginList != null) {
            mPluginList.clear();
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AppPluginAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_app_plugin, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PluginInfo info = mPluginList.get(position);
        if (info.switchControl) {
            holder.control.setVisibility(View.VISIBLE);
            holder.control.setChecked(info.isOpen);
        } else {
            holder.control.setVisibility(View.GONE);
        }
        holder.iconView.setImageResource(info.iconId);
        holder.nameView.setText(info.name);
        holder.descTV.setText(info.desc);
        if (info.name.equals(mContext.getString(R.string.plugin_location))){
            holder.descTipsTV.setVisibility(View.VISIBLE);
        }else{
            holder.descTipsTV.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(v -> {
            mClickListener.onItemClick(info, position);
        });
        holder.control.setOnCheckedChangeListener((btn, checked) -> {
            info.isOpen = checked;
            if (mClickListener != null) {
                mClickListener.onItemSwitch(info, checked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPluginList == null ? 0 : mPluginList.size();
    }

    public void setOnItemClickListener(OnItemOperateListener listener) {
        mClickListener = listener;
    }

    public interface OnItemOperateListener {

        void onItemClick(PluginInfo pluginInfo, int position);

        void onItemSwitch(PluginInfo pluginInfo, boolean isOpen);

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iconView;
        private TextView nameView;
        private TextView descTV;
        private TextView descTipsTV;
        private Switch control;

        ViewHolder(View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.plugin_icon_iv);
            nameView = itemView.findViewById(R.id.plugin_function_tv);
            descTV = itemView.findViewById(R.id.plugin_desc_tv);
            descTipsTV = itemView.findViewById(R.id.plugin_desc_tv_tips);
            control = itemView.findViewById(R.id.plugin_switch);
        }
    }
}
