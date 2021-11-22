package com.ft.mapp.home.adapters;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ft.mapp.R;
import com.xqb.user.net.engine.VUiKit;
import com.ft.mapp.home.models.AppInfo;
import com.ft.mapp.widgets.LabelView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CloneAppListAdapter
        extends RecyclerView.Adapter<CloneAppListAdapter.ViewHolder> {

    private static final int TYPE_FOOTER = -2;
    private static final int TYPE_GROUP = 1;
    private static final int TYPE_HEADER = -1;
    private final View mFooterView;
    private LayoutInflater mInflater;
    private List<AppInfo> mAppList;
    private HashMap<Integer, String> mLetters;
    private ItemEventListener mItemEventListener;
    private ArrayList<Integer> groupPosition;
    private HashMap<String, Integer> letterIndex;
    private List<AppInfo> hotAppList;
    private AppHotCloneAdapter appHotCloneAdapter;

    public CloneAppListAdapter(Context context) {
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

    public void setList(List<AppInfo> models, HashMap<String, Integer> letters) {
        this.mAppList = models;
        if (this.mAppList == null) {
            return;
        }
        hotAppList = new ArrayList<>();
        AppInfo[] tempHotApps = new AppInfo[4];
        //热门应用
        for (AppInfo appInfo : mAppList) {
            //sort hot apps
            if (appInfo.packageName.equals("com.tencent.mm")) {
                tempHotApps[0] = appInfo;
            }
            if (appInfo.packageName.equals("com.tencent.mobileqq")) {
                tempHotApps[1] = appInfo;
            }
            if (appInfo.packageName.equals("com.ss.android.ugc.aweme")) {
                tempHotApps[2] = appInfo;
            }
            if (appInfo.packageName.equals("com.tencent.tmgp.sgame")) {
                tempHotApps[3] = appInfo;
            }
        }
        for (AppInfo tempHotApp : tempHotApps) {
            if (tempHotApp != null) {
                hotAppList.add(tempHotApp);
            }
        }

        int offsetPosition = 0;
        if (hotAppList != null && hotAppList.size() > 0) {
            appHotCloneAdapter = new AppHotCloneAdapter(hotAppList);
            appHotCloneAdapter.setOnItemEventListener((appInfo, position) -> mItemEventListener.onItemClick(appInfo, position));
            this.mAppList.add(0, null);
            offsetPosition = 1;
        }
        //分组选项
        if (letters != null) {
            groupPosition = new ArrayList<>();
            letterIndex = new HashMap<>();
            mLetters = new HashMap<>();

            for (Map.Entry<String, Integer> entry : letters.entrySet()) {
                int groupIndex = entry.getValue() + offsetPosition;
                groupPosition.add(groupIndex);
                mLetters.put(groupIndex, entry.getKey());
                letterIndex.put(entry.getKey(), groupIndex);
                AppInfo appInfo = new AppInfo();
                appInfo.firstLetter = entry.getKey();
                mAppList.add(groupIndex, appInfo);
                offsetPosition++;
            }
        }

        notifyDataSetChanged();
    }

    public HashMap<String, Integer> getLetterIndex() {
        return letterIndex;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new ViewHolder(mInflater.inflate(R.layout.layout_list_app_header, parent, false), viewType);
        }
        if (viewType == TYPE_FOOTER) {
            return new ViewHolder(mFooterView, viewType);
        }
        if (viewType == TYPE_GROUP) {
            return new ViewHolder(mInflater.inflate(R.layout.item_clone_app_list_group, parent, false), viewType);
        }
        return new ViewHolder(mInflater.inflate(R.layout.item_clone_app, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            if (hotAppList != null) {
                holder.lvHot.setAdapter(appHotCloneAdapter);
            }
            return;
        }
        if (getItemViewType(position) == TYPE_FOOTER) {
            return;
        }
        if (getItemViewType(position) == TYPE_GROUP) {
            holder.tvGroupName.setText(mLetters.get(position));
            return;
        }
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
        if (firstView == null) {
            firstView = holder.itemView;
        }
    }

    private View firstView;

    public View getFirstItemView() {
        if (appHotCloneAdapter==null||appHotCloneAdapter.getFirstView() == null) {
            return firstView;
        }
        return appHotCloneAdapter.getFirstView();
//        return firstView;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return mAppList == null ? 1 : mAppList.size() + 1;
    }

    public AppInfo getItem(int index) {
        return mAppList.get(index);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && hotAppList != null && hotAppList.size() > 0) {
            return TYPE_HEADER;
        }
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        if (groupPosition != null && groupPosition.contains(position)) {
            return TYPE_GROUP;
        }
        return super.getItemViewType(position);
    }

    public interface ItemEventListener {
        void onItemClick(AppInfo appData, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iconView;
        private TextView nameView;
        private LabelView labelView;
        private TextView imageButton;
        private TextView tvGroupName;
        private ListView lvHot;

        ViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == TYPE_FOOTER) {
                return;
            }
            if (viewType == TYPE_HEADER) {
                lvHot = itemView.findViewById(R.id.list_app_header_lv);
                return;
            }
            if (viewType == TYPE_GROUP) {
                tvGroupName = itemView.findViewById(R.id.item_clone_app_list_tv_group_name);
            } else {
                iconView = itemView.findViewById(R.id.item_app_icon);
                nameView = itemView.findViewById(R.id.item_app_name);
                labelView = itemView.findViewById(R.id.item_app_clone_count);
                imageButton = itemView.findViewById(R.id.btn_add);
            }
        }
    }

}
