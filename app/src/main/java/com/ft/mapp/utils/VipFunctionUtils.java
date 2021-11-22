package com.ft.mapp.utils;

import com.luck.picture.lib.tools.SPUtils;

import jonathanfinerty.once.Once;

public class VipFunctionUtils {

    public static final String FUNCTION_MOCK_LOCATION = "function_mock_location";
    public static final String FUNCTION_MOCK_DEVICE = "function_mock_device";
    public static final String FUNCTION_MOCK_CUSTOM_DEVICE = "function_mock_custom_device";
    public static final String FUNCTION_ADD_LIMIT = "function_add_limit";
    public static final String DEFAULT = "function_default";

    public static void markFunction(String functionType) {
//        int funCount = SPUtils.getInstance().getInt(functionType);
//        if (funCount == -1) {
//            SPUtils.getInstance().put(functionType, 1);
//        } else if (funCount > 1) {
//            Once.markDone(functionType);
//            SPUtils.getInstance().put(functionType, -1);
//        } else {
//            SPUtils.getInstance().put(functionType, ++funCount);
//        }
    }

}
