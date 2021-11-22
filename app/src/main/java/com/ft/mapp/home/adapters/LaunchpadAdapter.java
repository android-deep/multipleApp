package com.ft.mapp.home.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ft.mapp.R;
import com.ft.mapp.home.AppDetailActivity;
import com.ft.mapp.home.models.AppData;
import com.ft.mapp.home.models.FakeAppInfo;
import com.ft.mapp.home.models.MultiplePackageAppData;
import com.ft.mapp.home.models.PackageAppData;
import com.ft.mapp.utils.CommonUtil;
import com.ft.mapp.utils.SizeUtils;
import com.ft.mapp.utils.ToastUtil;
import com.ft.mapp.utils.UIUtils;
import com.ft.mapp.widgets.LauncherIconView;
import com.ft.mapp.widgets.rance.library.ButtonData;
import com.ft.mapp.widgets.rance.library.SectorMenuButton;
import com.xqb.user.net.engine.VUiKit;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

/**
 *
 */
public class LaunchpadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mInflater;
    private List<AppData> mList;
    private OnAppClickListener mAppClickListener;
    private OnAppClickListener mMoreClickListener;
    private Context mContext;

    private boolean hasHeader;

    private int itemHeight;

    public LaunchpadAdapter(Context context) {
        this(context, false);
        itemHeight = (int) (UIUtils.getScreenWidth(context) * 120f / 334f) - SizeUtils.dip2px(8);
    }

    public LaunchpadAdapter(Context context, boolean hasHeader) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.hasHeader = hasHeader;
    }

    public boolean isHeader(int position) {
        return position == 0 && hasHeader;
    }

    public void add(AppData model) {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        int insertPos = mList.size();
//        if (insertPos < 0) {
//            insertPos = 0;
//        }
        mList.add(insertPos, model);
        int currentItemCount = getItemCount();
        notifyItemInserted(currentItemCount);
        notifyItemRangeChanged(insertPos, currentItemCount + 1);
//        mList.add(model);
//        notifyDataSetChanged();
    }

    public void replace(int index, AppData data) {
        mList.set(index, data);
        notifyItemChanged(index);
    }

    public void remove(AppData data) {
        if (mList.remove(data)) {
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_TYPE_NORMAL.ordinal()) {
            return new ViewHolder(mInflater.inflate(R.layout.item_launcher_app, parent, false));
        } else {
            return new HeaderViewHolder(mInflater.inflate(R.layout.layout_launch_header, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            AppData data = mList.get(position);
            viewHolder.iconView.setImageDrawable(CommonUtil.getAppFakeIcon(data));
            viewHolder.nameView.setText(CommonUtil.getAppFakeName(data));
            if (data instanceof MultiplePackageAppData) {
                MultiplePackageAppData multipleData = (MultiplePackageAppData) data;
                String name = CommonUtil.getAppFakeName(data);
                if (name.equals(data.getName())) {
                    name = name + "-" + (multipleData.userId + 1);
                    viewHolder.nameView.setText(name);
                } else {
                    viewHolder.nameView.setText(name);
                }

            }
//            if (data.isFirstOpen()) {
//                viewHolder.viewFirst.setVisibility(View.VISIBLE);
//            } else {
//                viewHolder.viewFirst.setVisibility(View.INVISIBLE);
//            }
            holder.itemView.setOnClickListener(v -> {
                if (mAppClickListener != null) {
                    mAppClickListener.onAppClick(holder.itemView, viewHolder.moreIv, position, data);
                }
            });
            viewHolder.moreIv.setOnClickListener(v -> {
                if (mMoreClickListener != null) {
                    mMoreClickListener.onAppClick(viewHolder.itemView, viewHolder.moreIv, position, data);
                } else {
                    gotoAppDetail(data);
                }
            });

            if (data.isLoading()) {
                startLoadingAnimation(viewHolder.iconView);
            } else {
                viewHolder.iconView.setProgress(100, false);
            }
            ViewGroup.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
            layoutParams.height = itemHeight;
            viewHolder.itemView.requestLayout();
            initCenterSectorMenuButton(viewHolder.btnMenu);
        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerVh = (HeaderViewHolder) holder;
            headerVh.ivAd.setOnClickListener(v -> {
                ToastUtil.show(holder.itemView.getContext(), "AD CLICK");
            });
        }
        if (position == 0) {
            firstView = holder.itemView;
        }
    }

    private View firstView;

    public View getFirstView() {
        return firstView;
    }

    private void startLoadingAnimation(LauncherIconView iconView) {
        iconView.setProgress(40, true);
        VUiKit.postDelayed(900, () -> iconView.setProgress(100, true));
    }

    private void initCenterSectorMenuButton(SectorMenuButton sectorMenuButton) {
        final List<ButtonData> buttonDatas = new ArrayList<>();
        int[] menus = new int[]{R.drawable.ic_app_menu, R.drawable.icon_vip_remove_ad, R.drawable.icon_vip_all, R.drawable.icon_vip_sim_location};
        for (int i = 0; i < menus.length; i++) {
            ButtonData buttonData = ButtonData.buildIconButton(mContext, menus[i], 0);
//            buttonData.setBackgroundColorId(mContext, R.color.colorAccent);
            buttonDatas.add(buttonData);
        }
        sectorMenuButton.setButtonDatas(buttonDatas);
//        setListener(sectorMenuButton);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && hasHeader) {
            return ITEM_TYPE.ITEM_TYPE_HEADER.ordinal();
        }
        return ITEM_TYPE.ITEM_TYPE_NORMAL.ordinal();
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public List<AppData> getList() {
        return mList;
    }

    public void setList(List<AppData> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public void setAppClickListener(OnAppClickListener clickListener) {
        this.mAppClickListener = clickListener;
    }

    public void setMoreClickListener(OnAppClickListener clickListener) {
        this.mMoreClickListener = clickListener;
    }

    public void moveItem(int pos, int targetPos) {
        AppData model = mList.remove(pos);
        mList.add(targetPos, model);
        notifyItemMoved(pos, targetPos);
    }

    public void refresh(AppData model) {
        int index = mList.indexOf(model);
        if (index >= 0) {
            notifyItemChanged(index);
        }
    }

    public interface OnAppClickListener {
        void onAppClick(View view, View targetView, int position, AppData model);
    }

    public enum ITEM_TYPE {
        ITEM_TYPE_NORMAL,
        ITEM_TYPE_HEADER
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public int color;
        LauncherIconView iconView;
        TextView nameView;
        AppCompatImageView moreIv;
        View viewFirst;
        SectorMenuButton btnMenu;

        ViewHolder(View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.item_app_icon);
            nameView = itemView.findViewById(R.id.item_app_name);
            moreIv = itemView.findViewById(R.id.item_app_more_iv);
            btnMenu = itemView.findViewById(R.id.item_launch_menu_btn);
            viewFirst = itemView.findViewById(R.id.item_app_first_load);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        FrameLayout layoutAd;
        ImageView ivAd;

        HeaderViewHolder(View itemView) {
            super(itemView);
            ivAd = itemView.findViewById(R.id.launch_header_iv_ad);
            layoutAd = itemView.findViewById(R.id.launch_header_layout_ad);
        }
    }

    private void gotoAppDetail(AppData data) {
        try {
            if (data instanceof PackageAppData) {
                PackageAppData appData = (PackageAppData) data;
                appData.isFirstOpen = false;
                AppDetailActivity
                        .gotoAppDetail(mContext, appData);
            } else if (data instanceof MultiplePackageAppData) {
                MultiplePackageAppData multipleData = (MultiplePackageAppData) data;
                multipleData.isFirstOpen = false;
                AppDetailActivity.gotoAppDetail(mContext, multipleData);
            } else {
                AppDetailActivity.gotoAppDetail(mContext, data);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
