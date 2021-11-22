package com.ft.mapp.engine;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ft.mapp.home.adapters.MenuAdapter;

public class WrapContentGridLayoutManager extends GridLayoutManager {

    private MenuAdapter adapter;
    private RecyclerView rv;

    public WrapContentGridLayoutManager(Context context, int spanCount, MenuAdapter adapter, RecyclerView rv) {
        super(context, spanCount);
        this.adapter = adapter;
        this.rv = rv;
    }

    @Override
    public void onMeasure(@NonNull RecyclerView.Recycler recycler, @NonNull RecyclerView.State state, int widthSpec, int heightSpec) {
        try {
            if (adapter != null && adapter.getItemHeight() > 0) {
                int measureWidth = View.MeasureSpec.getSize(widthSpec);
                int line = adapter.getItemCount() / getSpanCount();
                if (adapter.getItemCount() % getSpanCount() > 0) {
                    line++;
                }
                int measureHeight = adapter.getItemHeight() * line + rv.getPaddingTop() + rv.getPaddingBottom();
                setMeasuredDimension(measureWidth, measureHeight);
            } else {
                super.onMeasure(recycler, state, widthSpec, heightSpec);
            }

        } catch (Exception e) {
            super.onMeasure(recycler, state, widthSpec, heightSpec);
        }

    }
}
