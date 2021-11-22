package com.ft.mapp.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ft.mapp.R;
import com.ft.mapp.engine.WrapContentGridLayoutManager;
import com.ft.mapp.home.adapters.MenuAdapter;
import com.ft.mapp.home.models.MenuModel;
import com.yalantis.ucrop.util.ScreenUtils;

import java.util.ArrayList;

public class FunMenu extends FrameLayout implements MenuAdapter.OnFunClickListener{

    private Context mContext;
    private View mPopView;

    private View midLine;

    private RecyclerView rvFun;

    private PopupFunMenu.OnItemClickListener mOnItemClickListener;
    private ArrayList<MenuModel> functions;
    private ArrayList<MenuModel> extraFun;
    private MenuAdapter menuAdapter;

    private boolean showMulti = false;

    @Override
    public void onFunClick(PopupFunMenu.MENU_ITEM menu_item) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onClick(menu_item);
        }
        this.setVisibility(View.GONE);
    }

    public enum MENU_ITEM {
        SETTING, MULTI, SHORTCUT, DELETE, FAKE, CLEAR
    }

    private int maxWidth;
    private int maxHeight;

    public FunMenu(@NonNull Context context) {
        this(context,null);
    }

    public FunMenu(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public FunMenu(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPopView = LayoutInflater.from(getContext()).inflate(R.layout.popup_menu,this,true);
        midLine = mPopView.findViewById(R.id.menu_view_midline);
        rvFun = mPopView.findViewById(R.id.menu_recycler_view);
        mPopView.setOnClickListener(v -> setVisibility(GONE));
        this.mPopView.getViewTreeObserver().addOnPreDrawListener(() -> {
            maxWidth = mPopView.getMeasuredWidth();
            maxHeight = mPopView.getMeasuredHeight();
            return true;
        });

        generateFunction();
    }

    private void generateFunction() {
        functions = new ArrayList<>();
        functions.add(new MenuModel(R.drawable.icon_menu_detail, "应用详情", true, PopupFunMenu.MENU_ITEM.SETTING));
        functions.add(new MenuModel(R.drawable.icon_menu_shortcut, "添置桌面", false, PopupFunMenu.MENU_ITEM.SHORTCUT));

        functions.add(new MenuModel(R.drawable.icon_menu_delete, "删除应用", false, PopupFunMenu.MENU_ITEM.DELETE));

        extraFun = new ArrayList<>();
        extraFun.add(new MenuModel(R.drawable.icon_menu_fake, "伪装应用", true, PopupFunMenu.MENU_ITEM.FAKE));
        extraFun.add(new MenuModel(R.drawable.icon_menu_multiple, "分身多开", true, PopupFunMenu.MENU_ITEM.MULTI));
        extraFun.add(new MenuModel(R.drawable.icon_menu_clear, "恢复应用", false, PopupFunMenu.MENU_ITEM.CLEAR));

        menuAdapter = new MenuAdapter(functions);
        menuAdapter.setOnFunClickListener(this);
        WrapContentGridLayoutManager layoutManager = new WrapContentGridLayoutManager(mContext, 4, menuAdapter, rvFun);
        rvFun.setLayoutManager(layoutManager);
        rvFun.setAdapter(menuAdapter);
    }

    public void showLocation(View view,int windowWidth,int windowHeight) {
//        showAsDropDown(view, dip2px(mContext, -50), dip2px(mContext, -50));
//        showAsDropDown(view, 15, 15, Gravity.CENTER);
//        Rect rect = new Rect();
        int[] location = new int[2];
        view.getLocationInWindow(location);
        int viewX = location[0];
        int viewY = location[1];

        if (maxWidth==0||maxHeight==0){
            return;
        }
        int x = viewX - maxWidth / 2;
        int targetX = x<0?15:x;
        if (targetX>= windowWidth){
            targetX = windowWidth-15;
        }
        int targetY = viewY+view.getMeasuredHeight();
        if (targetY>=windowHeight){
            targetY = maxHeight+viewY;
        }

//        setX(targetX);
//        setY(targetY);


    }

    public void showMultiMenu(boolean isShow) {
        if (showMulti==isShow){
            return;
        }
        showMulti = isShow;
        if (isShow) {
            midLine.setVisibility(View.VISIBLE);
            functions.addAll(2,extraFun);
            menuAdapter.notifyDataSetChanged();
//            mMultiLine.setVisibility(View.VISIBLE);
        } else {
            midLine.setVisibility(View.GONE);
            functions.removeAll(extraFun);
            menuAdapter.notifyDataSetChanged();
//            mMultiLine.setVisibility(View.GONE);
        }
    }

    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public interface OnItemClickListener {
        void onClick(PopupFunMenu.MENU_ITEM item);
    }

    public void setOnItemClickListener(PopupFunMenu.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

}
