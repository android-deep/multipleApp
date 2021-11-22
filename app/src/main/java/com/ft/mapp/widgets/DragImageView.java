package com.ft.mapp.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.ft.mapp.utils.UIUtils;
import com.yalantis.ucrop.util.ScreenUtils;

@SuppressLint("AppCompatCustomView")
public class DragImageView extends ImageView {
    public DragImageView(Context context) {
        super(context, null);
    }

    public DragImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DragImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private float downX;
    private float downY;

    private float top;
    private float bottom;

    public void setMaxVertical(float top, float bottom) {
        this.top = top;
        this.bottom = bottom;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (Math.abs(event.getRawY() - downY) > 5) {
                float targetY = event.getRawY() - getMeasuredHeight() / 2f-50;
                if (targetY <= top) {
                    targetY = top;
                } else if (targetY >= bottom) {
                    targetY = bottom;
                }
                setY(targetY);
                return true;
            }
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getRawX();
                downY = event.getRawY();
                return true;
            case MotionEvent.ACTION_UP:
                if (Math.abs(downX - event.getRawX()) < 2f && Math.abs(downY - event.getRawY()) < 2) {
                    if (listener != null) {
                        listener.onClick(DragImageView.this);
                    }
                }
                return true;
        }
        return super.onTouchEvent(event);

    }


    private OnClickListener listener;

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        listener = l;
    }
}
