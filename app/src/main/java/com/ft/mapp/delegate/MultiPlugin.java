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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.ft.mapp.BuildConfig;
import com.ft.mapp.R;
import com.ft.mapp.home.HomeActivity;
import com.ft.mapp.utils.AppSharePref;
import com.ft.mapp.utils.SizeUtils;
import com.ft.mapp.utils.UIUtils;
import com.ft.mapp.widgets.DragImageView;
import com.fun.vbox.client.core.VCore;

import java.util.concurrent.atomic.AtomicBoolean;

public class MultiPlugin {
    private static final String TAG = "MultiPlugin";
    private static final String VIEWS_TAG = "IPlugin:Content:Views";
    private AtomicBoolean maximal = new AtomicBoolean(false);//默认是最小化并可以拖拽
    private ViewGroup parent;
    private Rect rect;
    private AtomicBoolean creating = new AtomicBoolean(false);
    private AtomicBoolean showing = new AtomicBoolean(false);
    //    private PopupWindow window;
    private FrameLayout content;
    private Bundle saveHierarchyState;
    private DragImageView icon;
    private AnimatorSet mScaleSet;
    private int size = 0;

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
     */
    private void prepare(Activity activity) {
//        if (!AppSharePref.getInstance(activity).getBoolean(AppSharePref.KEY_TIK_PLUGIN_ENABLE)) {
//            return;
//        }

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
        View user = onCreateView(activity, content);
        if (user.getParent() != null) {
            throw new IllegalStateException("The specified child already has a parent. " +
                    "You must call removeView() on the child's parent first.");
        }
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        this.content.addView(user, params);
        parent.addView(this.content);
        if (null != saveHierarchyState) {
            this.content.restoreHierarchyState(saveHierarchyState.getSparseParcelableArray(VIEWS_TAG));
        }
        creating.set(false);
        //显示交给用户控制
        if (showing.get()) {
            show();
        }
    }

    /**
     * 返回宽度
     */
    private int getWidth() {
        return rect.width();
    }

    /**
     * 返回高度
     */
    private int getHeight() {
        return rect.height();
    }

    /**
     * 最大化
     */
    public final void maximize() {

    }

    /**
     * 默认show接口
     */
    public final void show() {
        showing.set(true);
        Log.d(TAG, "maximize():maximal = " + maximal.get());
        maximal.set(true);
        if (null == content || null == parent) {
            return;
        }
        ViewGroup.LayoutParams params = content.getLayoutParams();
        if (null == params) {
            params = new ViewGroup.LayoutParams(getWidth(), getHeight());
            content.setLayoutParams(params);
        }
    }

    /**
     * 隐藏win
     */
    public final void dismiss() {
        Log.d(TAG, "dismiss():maximal = " + maximal.get());
        showing.set(false);
    }

    /**
     * 判断是否满足条件
     */
    public boolean onPreCheck(Activity activity) {
        return true;
    }

    /**
     * 抽象函数，子类实现
     */
    public View onCreateView(Activity activity, ViewGroup container) {
        View contentView = LayoutInflater.from(VCore.get().getContext()).inflate(R.layout.layout_multi_plugin, null);
        ConstraintLayout layoutContainer = contentView.findViewById(R.id.multi_plugin_layout_container);
        //拖动图标
        this.icon = new DragImageView(activity);
        this.icon.setImageDrawable(onCreateIcon(activity));
        size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36f, parent.getResources().getDisplayMetrics());
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(size, size);
        params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.verticalBias = 0.3f;
        layoutContainer.addView(icon, params);
        return contentView;
    }

    /**
     * 最小化icon显示
     */
    public Drawable onCreateIcon(Context context) {
        return VCore.get().getContext().getResources().getDrawable(R.drawable.icon_float_home);
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
     */
    public final void showNow(Activity activity) {
        if (!onPreCheck(activity)) {
            return;
        }
        if (!creating.get()) {
            create(activity);
        }
        show();
    }

    private void startIconAnimator(float srcX, float tarX) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(icon, "translationX", srcX, tarX);
        animator.setDuration(500);
        animator.setInterpolator(new BounceInterpolator());
        animator.start();
    }

    /**
     * 可拖拽到image view自定义
     */
    @SuppressLint("AppCompatCustomView")
    final class DragImageView extends ImageView {
        private float downX;
        private float downY;
        private float downRawX;
        private float downRawY;

        private float maxWidth;

        public DragImageView(Context context) {
            super(context);
            maxWidth = UIUtils.getScreenWidth(getContext());
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
                        downRawX = event.getRawX();
                        downRawY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        setX(event.getRawX() - size);
                        setY(event.getRawY() - size);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        setPressed(false);
//                        Log.i("Plugin -----------","downRawX = "+downRawX+",downRawY="+downRawY+"-"+"currentX = "+event.getRawX()+",currentY="+event.getRawY());
                        if (Math.abs(downRawX - event.getRawX()) < 2f && Math.abs(downRawY - event.getRawY()) < 2) {
                            backHome();
                        }
                        int targetX;
                        if (event.getRawX() > maxWidth / 2f) {
                            targetX = (int) (maxWidth - SizeUtils.dip2px(36f));
                        } else {
                            targetX = 0;
                        }
                        startIconAnimator(getX(), targetX);

                        break;
                }
                return true;
            }
            return false;
        }
    }

    private void backHome() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName(BuildConfig.APPLICATION_ID, HomeActivity.class.getName());
        intent.setComponent(cn);
        parent.getContext().startActivity(intent);
    }
}

