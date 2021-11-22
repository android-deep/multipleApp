package com.ft.mapp.home.pipi;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class TouchGroup extends LinearLayout {
    private String TAG = TouchGroup.class.getName();
    public TouchGroup(Context context) {
        super(context);
    }

    public TouchGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private boolean fingerState = true;
    float DownX = 0,DownY=0;
    long timestart = 0;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                fingerState = true;
                DownX = ev.getX();//float DownX
                DownY = ev.getY();//float DownY
                timestart = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                    if (fingerState) {
                        if(System.currentTimeMillis() - timestart < 500){
                            click.click(ev.getRawX(), ev.getRawY());
                        }
                    }
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = ev.getX() - DownX;//X轴距离
                float moveY = ev.getY() - DownY;//y轴距离
                if(Math.abs(moveX) > 10 || Math.abs(moveY)> 10){
                    fingerState = false;
                }
                break;

        }
        return super.dispatchTouchEvent(ev);
    }

    public void setClick(Click click) {
        this.click = click;
    }

    Click click;
    public interface Click{
        void click(float x, float y);
    }
}
