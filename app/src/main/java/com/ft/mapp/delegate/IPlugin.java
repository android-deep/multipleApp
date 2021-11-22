package com.ft.mapp.delegate;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.ft.mapp.BuildConfig;
import com.ft.mapp.R;
import com.ft.mapp.home.HomeActivity;
import com.ft.mapp.utils.AppSharePref;
import com.ft.mapp.utils.SizeUtils;
import com.ft.mapp.utils.UIUtils;
import com.fun.vbox.client.core.VCore;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class IPlugin {
    private static final String TAG = "IPlugin";
    private static final String VIEWS_TAG = "IPlugin:Content:Views";
    private AtomicBoolean maximal = new AtomicBoolean(false);//默认是最小化并可以拖拽
    private ViewGroup parent;
    private Rect rect;
    private AtomicBoolean creating = new AtomicBoolean(false);
    private AtomicBoolean showing = new AtomicBoolean(false);
    private PopupWindow window;
    private FrameLayout content;
    private Bundle saveHierarchyState;
    private DragImageView icon;
    private AnimatorSet mScaleSet;

    /**
     * 创建view
     *
     * @param activity
     */
    public void create(final Activity activity) {
        Log.d(TAG, "create():activity = " + activity);
        if (!onPreCheck(activity)) {
            return;
        }
        if (activity.isFinishing()) {
            return;
        }
        creating.set(true);
        activity.getWindow().getDecorView().post(() -> {
            if (activity.isFinishing()) {
                return;
            }
            prepare(activity);
        });
    }

    /**
     * 初始化view
     *
     * @param activity
     */
    private void prepare(Activity activity) {
        if (!AppSharePref.getInstance(activity).getBoolean(AppSharePref.KEY_TIK_PLUGIN_ENABLE)) {
            return;
        }

        Log.d(TAG, "prepare():activity = " + activity);
        this.parent = activity.findViewById(android.R.id.content);
        if (null == parent) {
            return;
        }
        this.rect = new Rect();
        this.parent.getLocalVisibleRect(rect);
        Log.d(TAG, "getLocalVisibleRect() = " + rect);
        //初始化视图
        this.content = new FrameLayout(activity);
        this.content.setBackgroundColor(Color.parseColor("#7f000000"));
        this.content.setOnClickListener(v -> {
            Log.d("IPlugin", "content.onClick()");
            minimize();
        });
        View user = onCreateView(activity, content);
        if (user.getParent() != null) {
            throw new IllegalStateException("The specified child already has a parent. " +
                    "You must call removeView() on the child's parent first.");
        }
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        this.content.addView(user, params);
        if (null != saveHierarchyState) {
            this.content.restoreHierarchyState(saveHierarchyState.getSparseParcelableArray(VIEWS_TAG));
        }
        //初始化window设置
        this.window = new PopupWindow(activity);
        ColorDrawable drawable = new ColorDrawable(Color.TRANSPARENT);
        this.window.setBackgroundDrawable(drawable);
        this.window.setClippingEnabled(false);
        //拖动图标
        this.icon = new DragImageView(activity, this.window);
        this.icon.setImageDrawable(onCreateIcon(activity));
        //如果是最小化点击放大否则无响应
        this.icon.setOnClickListener(v -> {
            Log.d(TAG, "minimize():isMinimal = " + maximal.get());
            if (!maximal.get()) {
                maximize();
            }
            if (mScaleSet != null) {
                mScaleSet.cancel();
            }
            if (!AppSharePref.getInstance(activity).getBoolean(AppSharePref.KEY_TIKTOK_ANIMATOR_SHOW, false)) {
                AppSharePref.getInstance(activity).putBoolean(AppSharePref.KEY_TIKTOK_ANIMATOR_SHOW, true);
            }
        });
        creating.set(false);
        //显示交给用户控制
        if (showing.get()) {
            show();
        }
    }

    /**
     * 返回宽度
     *
     * @return
     */
    private int getWidth() {
        return rect.width();
    }

    /**
     * 返回高度
     *
     * @return
     */
    private int getHeight() {
        return rect.height();
    }

    /**
     * 最小化
     */
    public final void minimize() {
        Log.d(TAG, "minimize():maximal = " + maximal.get());
        maximal.set(false);
        if (null == icon || null == window || null == parent) {
            return;
        }
        if (window.isShowing()) {
            window.dismiss();
        }
        window.setContentView(icon);
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, parent.getResources().getDisplayMetrics());
        window.setWidth(size);
        window.setHeight(size);
        try {
            window.showAtLocation(parent, Gravity.NO_GRAVITY, 0, getHeight() / 2);
        } catch (Exception e) {
        }
    }

    /**
     * 最大化
     */
    public final void maximize() {
        Log.d(TAG, "maximize():maximal = " + maximal.get());
        maximal.set(true);
        if (null == content || null == window || null == parent) {
            return;
        }
        if (window.isShowing()) {
            window.dismiss();
        }
        ViewGroup.LayoutParams params = content.getLayoutParams();
        if (null == params) {
            params = new ViewGroup.LayoutParams(getWidth(), getHeight());
            content.setLayoutParams(params);
        }
        window.setContentView(content);
        window.setWidth(getWidth());
        window.setHeight(getHeight());
        window.showAtLocation(parent, Gravity.NO_GRAVITY, 0, 0);
    }

    /**
     * 默认show接口
     */
    public final void show() {
        show(!maximal.get());
    }

    /**
     * 展示win
     *
     * @param minimal
     */
    public final void show(boolean minimal) {
        if (!AppSharePref.getInstance(null).getBoolean(AppSharePref.KEY_TIK_PLUGIN_ENABLE)) {
            minimize();
            return;
        }
        showing.set(true);
        if (null == window) {
            return;
        }
        if (window.isShowing() && minimal == !maximal.get()) {
            return;
        }
        if (minimal) {
            minimize();
        } else {
            maximize();
        }
    }

    /**
     * 隐藏win
     */
    public final void dismiss() {
        Log.d(TAG, "dismiss():maximal = " + maximal.get());
        showing.set(false);
        if (null == window) {
            return;
        }
        if (!window.isShowing()) {
            window.dismiss();
        }
    }

    /**
     * 判断是否满足条件
     *
     * @param activity
     * @return
     */
    public boolean onPreCheck(Activity activity) {
        return true;
    }

    /**
     * 抽象函数，子类实现
     *
     * @param activity
     * @param container
     * @return
     */
    public abstract View onCreateView(Activity activity, ViewGroup container);

    /**
     * 最小化icon显示
     *
     * @param context
     * @return
     */
    public Drawable onCreateIcon(Context context) {
        Log.i("TikTok","createIcon");
        return VCore.get().getContext().getResources().getDrawable(R.drawable.icon_float_watermark);
    }

    /**
     * 销毁view,释放资源
     *
     * @param activity
     */
    public final void destroy(Activity activity) {
        if (!onPreCheck(activity)) {
            return;
        }
        activity.getWindow().getDecorView().post(() -> {
            dismiss();
            if (null != content) {
                saveHierarchyState = new Bundle();
                SparseArray<Parcelable> state = new SparseArray<>();
                content.saveHierarchyState(state);
                saveHierarchyState.putSparseParcelableArray(VIEWS_TAG, state);
            }
            onDestroy();
        });
    }

    /**
     * 回调给插件
     */
    public void onDestroy() {
    }

    /**
     * 在act已经启动后显示插件
     *
     * @param activity
     */
    public final void showNow(Activity activity) {
        if (!onPreCheck(activity)) {
            return;
        }
        if (null == window && !creating.get()) {
            create(activity);
        }
        show();
    }

    private void startIconAnimator() {
        if (mScaleSet == null) {
            ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(icon, View.SCALE_X, 0.8f, 1);
            ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(icon, View.SCALE_Y, 0.8f, 1);
            scaleXAnimator.setRepeatCount(ValueAnimator.INFINITE);
            scaleXAnimator.setRepeatMode(ValueAnimator.REVERSE);
            scaleYAnimator.setRepeatCount(ValueAnimator.INFINITE);
            scaleYAnimator.setRepeatMode(ValueAnimator.REVERSE);
            mScaleSet = new AnimatorSet();
            mScaleSet.setDuration(500);
            mScaleSet.playTogether(scaleXAnimator, scaleYAnimator);
        }
        mScaleSet.start();
    }

    /**
     * 可拖拽到image view自定义
     */
    @SuppressLint("AppCompatCustomView")
    static final class DragImageView extends ImageView {
        private final PopupWindow window;
        private float downX;
        private float downY;

        public DragImageView(Context context, PopupWindow window) {
            super(context);
            this.window = window;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            Log.d(TAG, "onTouchEvent():event = " + event + ", enable = " + isEnabled());
            super.onTouchEvent(event);
            if (isEnabled()) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        final int xOff = (int) (event.getRawX() - downX);
                        final int yOff = (int) (event.getRawY() - downY);
                        Log.d(TAG, "xOff = " + xOff + ", yOff = " + yOff + ", window = " + window);
                        if (null != window) {
                            window.update(xOff, yOff, -1, -1, true);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        setPressed(false);
                        break;
                }
                return true;
            }
            return false;
        }
    }
}
