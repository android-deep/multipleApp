package com.xqb.user.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;


public class GsonUtil {

    public static String getGsonString(Object object) {
        if (object == null) {
            return "";
        }
        Gson gson = new Gson();
        try {
            return gson.toJson(object);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static <T> T gson2Bean(String gsonString, Class<T> cls) {
        if (TextUtils.isEmpty(gsonString)) {
            return null;
        }
        Gson gson = new Gson();
        try {
            return gson.fromJson(gsonString, cls);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T gson2Bean(String gsonString, Type type) {
        if (TextUtils.isEmpty(gsonString)) {
            return null;
        }
        Gson gson = new Gson();
        try {
            return gson.fromJson(gsonString, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> List<T> gson2List(String gsonString, Class<T> cls) {
        if (TextUtils.isEmpty(gsonString)) {
            return null;
        }
        Gson gson = new Gson();
        try {
            return gson.fromJson(gsonString, new ListOfJson<T>(cls));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void saveBean2Json(Object object, String path) {
        if (object == null) {
            return;
        }
        Gson gson = new Gson();
        FileWriter writer = null;
        try {
            writer = new FileWriter(path);
            gson.toJson(object, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static <T> T loadJson2Bean(String path, Class<T> cls) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Gson gson = new Gson();
        FileReader reader = null;
        try {
            reader = new FileReader(path);
            return gson.fromJson(reader, cls);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static <T> List<T> loadJson2List(String path, Class<T> cls) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Gson gson = new Gson();
        FileReader reader = null;
        try {
            reader = new FileReader(path);
            return gson.fromJson(reader, new ListOfJson<T>(cls));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
