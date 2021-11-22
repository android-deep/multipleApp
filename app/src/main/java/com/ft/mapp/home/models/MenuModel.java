package com.ft.mapp.home.models;

import com.ft.mapp.widgets.PopupFunMenu;

public class MenuModel {
    private int iconRes;
    private String funTitle;
    private boolean isVipFun;
    private PopupFunMenu.MENU_ITEM menuType;

    public MenuModel(int iconRes, String funTitle, boolean isVipFun,PopupFunMenu.MENU_ITEM menuType) {
        this.iconRes = iconRes;
        this.funTitle = funTitle;
        this.isVipFun = isVipFun;
        this.menuType = menuType;
    }

    public PopupFunMenu.MENU_ITEM getMenuType() {
        return menuType;
    }

    public int getIconRes() {
        return iconRes;
    }

    public String getFunTitle() {
        return funTitle;
    }

    public boolean isVipFun() {
        return isVipFun;
    }
}
