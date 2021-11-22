package com.ft.mapp.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class ObserveHorizontalScrollView extends HorizontalScrollView {
    public ObserveHorizontalScrollView(Context context) {
        super(context);
    }

    public ObserveHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObserveHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (listener!=null){
            listener.onScrollChange(l,t);
        }
        super.onScrollChanged(l,t, oldl, oldt);
    }

    private OnScrollChangedListener listener;

    public void setListener(OnScrollChangedListener listener) {
        this.listener = listener;
    }

    public interface OnScrollChangedListener{
        void onScrollChange(int l, int t);
    }

}
