package com.ft.mapp.home.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ft.mapp.R;
import com.ft.mapp.home.models.AppInfo;
import com.ft.mapp.widgets.LabelView;

import java.util.List;

public class AppHotCloneAdapter extends BaseAdapter {
    private List<AppInfo> infoList;

    public AppHotCloneAdapter(List<AppInfo> infos) {
        this.infoList = infos;
    }

    @Override
    public int getCount() {
        return infoList.size();
    }

    @Override
    public AppInfo getItem(int i) {
        return infoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh;
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_clone_app, viewGroup, false);
            vh = new ViewHolder(view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }

        AppInfo info = infoList.get(i);
        vh.iconView.setImageDrawable(info.icon);
        vh.nameView.setText(info.name);
        if (info.cloneCount > 0) {
            vh.labelView.setVisibility(View.VISIBLE);
            vh.labelView.setText(info.cloneCount + "");
        } else {
            vh.labelView.setVisibility(View.INVISIBLE);
        }

        vh.imageButton.setOnClickListener(v -> {
            l.onItemClick(info, i);
        });

        view.setOnClickListener(v -> {
            l.onItemClick(info, i);
        });
        if (i==0&&firstView == null) {
            firstView = view;
        }
        return view;
    }

    private View firstView;

    public View getFirstView() {
        return firstView;
    }

    OnItemEventListener l;

    public void setOnItemEventListener(OnItemEventListener l) {
        this.l = l;
    }

    public interface OnItemEventListener {
        void onItemClick(AppInfo appInfo, int position);
    }

    class ViewHolder {
        private ImageView iconView;
        private TextView nameView;
        private LabelView labelView;
        private TextView imageButton;

        ViewHolder(View itemView) {
            iconView = itemView.findViewById(R.id.item_app_icon);
            nameView = itemView.findViewById(R.id.item_app_name);
            labelView = itemView.findViewById(R.id.item_app_clone_count);
            imageButton = itemView.findViewById(R.id.btn_add);

        }
    }

}
