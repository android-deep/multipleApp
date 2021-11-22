package com.ft.mapp.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class NoScrollViewPager extends ViewPager {

    private boolean scrollEnable = false;

    public NoScrollViewPager(@NonNull Context context) {
        super(context);
    }

    public NoScrollViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollEnable(boolean enable) {
        this.scrollEnable = enable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (scrollEnable) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (scrollEnable) {
            return super.onTouchEvent(ev);
        } else {
            return true;
        }
    }
}
