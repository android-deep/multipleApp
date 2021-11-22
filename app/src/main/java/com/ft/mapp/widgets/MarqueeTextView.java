package com.ft.mapp.widgets;

import android.content.Context;

import androidx.appcompat.widget.AppCompatTextView;

import android.graphics.Rect;
import android.util.AttributeSet;

public class MarqueeTextView extends AppCompatTextView {

    private boolean isStop = false;

    public MarqueeTextView(Context context) {
        super(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isFocused() {
        if (this.isStop) {
            return super.isFocused();
        }
        return true;
    }

    public void stopScroll() {
        this.isStop = true;
    }

    public void start() {
        this.isStop = false;
    }

    protected void onDetachedFromWindow() {
        stopScroll();
        super.onDetachedFromWindow();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (hasWindowFocus) {
            start();
            super.onWindowFocusChanged(hasWindowFocus);
        }
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (focused) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
        }
    }
}