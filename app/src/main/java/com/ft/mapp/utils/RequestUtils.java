package com.ft.mapp.utils;

import java.util.HashMap;
import java.util.Map;

public class RequestUtils {

    public static Map<String,String> Split(String urlparam){
        Map<String,String> map = new HashMap<>();
        String[] param =  urlparam.split("&");
        for(String keyvalue:param){
            String[] pair = keyvalue.split("=");
            if(pair.length==2){
                map.put(pair[0], pair[1]);
            }
        }
        return map;
    }
}
