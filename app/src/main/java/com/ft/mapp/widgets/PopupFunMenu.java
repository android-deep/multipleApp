package com.ft.mapp.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.RecyclerView;

import com.ft.mapp.R;
import com.ft.mapp.engine.WrapContentGridLayoutManager;
import com.ft.mapp.home.adapters.MenuAdapter;
import com.ft.mapp.home.models.MenuModel;
import com.ft.mapp.utils.SizeUtils;
import com.ft.mapp.utils.UIUtils;
import com.ft.mapp.widgets.luckly_popupwindow.utils.PopouBackView;
import com.ft.mapp.widgets.luckly_popupwindow.utils.PopupWindowUtils;

import java.util.ArrayList;

public class PopupFunMenu extends PopupWindow implements MenuAdapter.OnFunClickListener {

    private Context mContext;
    private View mPopView;

    private View midLine;

    private RecyclerView rvFun;
    private View viewTriangle;
    private View viewTriangleBottom;

    private OnItemClickListener mOnItemClickListener;
    private ArrayList<MenuModel> functions;
    private ArrayList<MenuModel> extraFun;
    private MenuAdapter menuAdapter;


    //三角形的宽度
    private int mTriangleWidth = 40;
    //三角形的高度
    private int mTrianleHeight = 30;
    //圆角的半径
    private int mRadius = 20;
    //自绘制三角形圆角背景
    private PopouBackView mPopouBackView;

    private boolean showMulti = false;

    @Override
    public void onFunClick(MENU_ITEM menu_item) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onClick(menu_item);
        }
        dismiss();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        showing = false;
    }

    public enum MENU_ITEM {
        SETTING, MULTI, SHORTCUT, DELETE, FAKE, CLEAR
    }

    private int maxWidth;
    private int maxHeight;

    public PopupFunMenu(Context context) {
        super(context);
        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        mPopView = inflater.inflate(R.layout.popup_menu, null);
        this.setContentView(mPopView);
        this.setWidth(LayoutParams.WRAP_CONTENT);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);

//        mMultiLine = mPopView.findViewById(R.id.menu_view_divide);

        midLine = mPopView.findViewById(R.id.menu_view_midline);
        rvFun = mPopView.findViewById(R.id.menu_recycler_view);
        viewTriangle = mPopView.findViewById(R.id.menu_view_triangle);
        viewTriangleBottom = mPopView.findViewById(R.id.menu_view_triangle_bottom);
        mPopView.setOnClickListener(v -> dismiss());
        this.mPopView.getViewTreeObserver().addOnPreDrawListener(() -> {
            maxWidth = mPopView.getMeasuredWidth();
            maxHeight = mPopView.getMeasuredHeight();
            startShow();
            return true;
        });

        generateFunction();
    }

    private void generateFunction() {
        //基础功能
        functions = new ArrayList<>();
        functions.add(new MenuModel(R.drawable.icon_menu_detail, "应用详情", true, MENU_ITEM.SETTING));
        functions.add(new MenuModel(R.drawable.icon_menu_shortcut, "添置桌面", false, MENU_ITEM.SHORTCUT));
        functions.add(new MenuModel(R.drawable.icon_menu_delete, "删除应用", false, MENU_ITEM.DELETE));

        //额外功能
        extraFun = new ArrayList<>();
        extraFun.add(new MenuModel(R.drawable.icon_menu_fake, "伪装应用", true, MENU_ITEM.FAKE));
        extraFun.add(new MenuModel(R.drawable.icon_menu_multiple, "分身多开", true, MENU_ITEM.MULTI));
        extraFun.add(new MenuModel(R.drawable.icon_menu_clear, "恢复应用", false, MENU_ITEM.CLEAR));

        menuAdapter = new MenuAdapter(functions);
        menuAdapter.setOnFunClickListener(this);
        WrapContentGridLayoutManager layoutManager = new WrapContentGridLayoutManager(mContext, 4, menuAdapter, rvFun);
        rvFun.setLayoutManager(layoutManager);
        rvFun.setAdapter(menuAdapter);
    }

    private boolean showing = false;
    private View parentView;
    private View targetView;

    public void showLocation(View parentView, View view, View targetView) {
//        showAsDropDown(view, dip2px(mContext, -50), dip2px(mContext, -50));
//        if (maxHeight==0||maxWidth==0){
//            return;
//        }
        this.parentView = parentView;
        this.targetView = targetView;
        if (maxWidth >= 0) {
            startShow();
        }
    }

    private int targetX;
    private int targetY;

    private void startShow() {
        float screenWidth = UIUtils.getScreenWidth(mContext);
        float screenHeight = UIUtils.getScreenHeight(mContext)- SizeUtils.dip2px(70);
        int[] location = new int[2];
        targetView.getLocationInWindow(location);
        targetX = location[0] - maxWidth / 2;
        if (targetX < 0) {
            targetX = 15;
        } else if (targetX + maxWidth > screenWidth) {
            targetX = (int) (screenWidth - maxWidth - 15);
        }

        targetY = location[1] + targetView.getMeasuredHeight();
        if (targetY + maxHeight > screenHeight) {
            targetY = location[1] - maxHeight;
            viewTriangleBottom.setVisibility(View.VISIBLE);
            viewTriangle.setVisibility(View.GONE);
        } else {
            viewTriangleBottom.setVisibility(View.GONE);
            viewTriangle.setVisibility(View.VISIBLE);
        }

        if (maxWidth > 0) {
            int[] contentPosition = PopupWindowUtils.calculatePopupWindowPos(mPopView, targetView, mTrianleHeight, maxWidth);
            int[] centerPosition = PopupWindowUtils.getPositionViewCenterPos(targetView);
            viewTriangle.setX(centerPosition[0] - targetX - 20);
            viewTriangleBottom.setX(centerPosition[0] - targetX - 20);
        }
        if (showing) {
            update(targetX, targetY, maxWidth, maxHeight);
            if (showMulti){
                midLine.setVisibility(View.VISIBLE);
            }else{
                midLine.setVisibility(View.GONE);
            }
        } else {
            showAtLocation(parentView, Gravity.TOP | Gravity.START, targetX, targetY);
            showing = true;
        }
    }

    public void showMultiMenu(boolean isShow) {
        if (showMulti == isShow) {
            return;
        }
        showMulti = isShow;
        if (isShow) {
            midLine.setVisibility(View.VISIBLE);
            functions.addAll(2, extraFun);
            menuAdapter.notifyDataSetChanged();
//            mMultiLine.setVisibility(View.VISIBLE);
        } else {
            midLine.setVisibility(View.GONE);
            functions.removeAll(extraFun);
            menuAdapter.notifyDataSetChanged();
//            mMultiLine.setVisibility(View.GONE);
        }
        this.mPopView.getViewTreeObserver().addOnPreDrawListener(() -> {
            maxWidth = mPopView.getMeasuredWidth();
            maxHeight = mPopView.getMeasuredHeight();
            startShow();
            return true;
        });
    }

    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public interface OnItemClickListener {
        void onClick(MENU_ITEM item);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

}