package com.ft.mapp.home.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.ft.mapp.R;
import com.ft.mapp.home.models.AppInfo;
import com.ft.mapp.widgets.LabelView;
import com.xqb.user.net.engine.VUiKit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchAppListAdapter
        extends RecyclerView.Adapter<SearchAppListAdapter.ViewHolder> {

    private final View mFooterView;
    private LayoutInflater mInflater;
    private List<AppInfo> mAppList;
    private ItemEventListener mItemEventListener;

    public SearchAppListAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        mFooterView = new View(context);
        StaggeredGridLayoutManager.LayoutParams params =
                new StaggeredGridLayoutManager.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, VUiKit.dpToPx(context, 60)
                );
        params.setFullSpan(true);
        mFooterView.setLayoutParams(params);

    }

    public void setOnItemClickListener(ItemEventListener mItemEventListener) {
        this.mItemEventListener = mItemEventListener;
    }

    public List<AppInfo> getList() {
        return mAppList;
    }

    public void setList(List<AppInfo> models) {
        this.mAppList = models;
        if (this.mAppList == null) {
            return;
        }

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_clone_app, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AppInfo info = mAppList.get(position);
        holder.iconView.setImageDrawable(info.icon);
        holder.nameView.setText(info.name);
        if (info.cloneCount > 0) {
            holder.labelView.setVisibility(View.VISIBLE);
            holder.labelView.setText(info.cloneCount + "");
        } else {
            holder.labelView.setVisibility(View.INVISIBLE);
        }

        holder.imageButton.setOnClickListener(v -> {
            mItemEventListener.onItemClick(info, position);
        });

        holder.itemView.setOnClickListener(v -> {
            mItemEventListener.onItemClick(info, position);
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
//        return mAppList == null ? 1 : mAppList.size() + 1;
        return mAppList.size();
    }

    public AppInfo getItem(int index) {
        return mAppList.get(index);
    }

    public interface ItemEventListener {
        void onItemClick(AppInfo appData, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iconView;
        private TextView nameView;
        private LabelView labelView;
        private TextView imageButton;

        ViewHolder(View itemView, int viewType) {
            super(itemView);
            iconView = itemView.findViewById(R.id.item_app_icon);
            nameView = itemView.findViewById(R.id.item_app_name);
            labelView = itemView.findViewById(R.id.item_app_clone_count);
            imageButton = itemView.findViewById(R.id.btn_add);
        }
    }

}
