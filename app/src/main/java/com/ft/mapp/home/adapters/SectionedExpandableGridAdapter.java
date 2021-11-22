package com.ft.mapp.home.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.ft.mapp.R;
import com.ft.mapp.home.models.BrandItem;

import java.util.ArrayList;

public class SectionedExpandableGridAdapter extends RecyclerView.Adapter<SectionedExpandableGridAdapter.ViewHolder> {

    //data array
    private ArrayList<BrandItem> mDataArrayList;

    //context
    private final Context mContext;

    //listeners
    private final ItemClickListener mItemClickListener;

    //view type
//    private static final int VIEW_TYPE_SECTION = R.layout.layout_section;
    private static final int VIEW_TYPE_ITEM = R.layout.layout_item;

    private BrandItem mSelectBrandItem = null;

    public SectionedExpandableGridAdapter(Context context, ArrayList<BrandItem> mDataArrayList, ItemClickListener itemClickListener) {
        mContext = context;
        mItemClickListener = itemClickListener;
        this.mDataArrayList = mDataArrayList;
//        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                return isSection(position)?gridLayoutManager.getSpanCount():1;
//            }
//        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(viewType, parent, false), viewType);
    }

    public void setSelectBrandItem(BrandItem item) {
        if (item!=null){
            mItemClickListener.itemClicked(item);
        }
        mSelectBrandItem = item;
        notifyDataSetChanged();
    }

    public BrandItem getSelectBrandItem() {
        return mSelectBrandItem;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                final BrandItem item = mDataArrayList.get(position);
                if (item.equals(mSelectBrandItem)) {
                    holder.itemTextView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.theme_rect));
                } else {
                    holder.itemTextView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.white_rect));
//                    holder.itemTextView.setBackgroundColor(mContext.getResources().getColor(R.color.desktopColorA));
//                    holder.itemTextView.setBackgroundColor(mContext.getResources().getColor(R.color.desktopColorA));
                }
                holder.itemTextView.setText(item.getModel());
                holder.view.setOnClickListener(v -> {
                    mSelectBrandItem = item;
                    mItemClickListener.itemClicked(item);
                    notifyDataSetChanged();
                });
    }

    @Override
    public int getItemCount() {
        return mDataArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
//        if (isSection(position))
//            return VIEW_TYPE_SECTION;
//        else return VIEW_TYPE_ITEM;
        return VIEW_TYPE_ITEM;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        //common
        View view;
        int viewType;

        //for section
        TextView sectionTextView;
        ToggleButton sectionToggleButton;

        //for item
        TextView itemTextView;

        public ViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.view = view;
            if (viewType == VIEW_TYPE_ITEM) {
                itemTextView = view.findViewById(R.id.text_item);
            } else {
                sectionTextView = view.findViewById(R.id.text_section);
                sectionToggleButton = view.findViewById(R.id.toggle_button_section);
            }
        }
    }
}
