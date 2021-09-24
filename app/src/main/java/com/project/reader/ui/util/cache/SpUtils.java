package com.project.reader.ui.util.cache;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public class SpUtils {
    //保存时间单位
    public static final int TIME_SECOND = 1;
    public static final int TIME_MINUTES = 60 * TIME_SECOND;
    public static final int TIME_HOUR = 60 * TIME_MINUTES;
    public static final int TIME_DAY = TIME_HOUR * 24;
    public static final int TIME_MAX = Integer.MAX_VALUE; // 不限制存放数据的数量
    public static final int DURATION_UNIT = 1000;

    private static final String fileName = "config";
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private static SpUtils INSTANCE = null;

    public static SpUtils getInstance(Context context) {
        if (null == INSTANCE) {
            INSTANCE = new SpUtils(context);
        }
        return INSTANCE;
    }

    private SpUtils(Context context) {
        sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        editor = sp.edit();
    }


    public void setString(String e, String value) {
        SpSaveModel<String> spSaveModel = new SpSaveModel<>(TIME_MAX, value, System.currentTimeMillis());
        String json = JSON.toJSONString(spSaveModel);
        editor.putString(e, json);
        editor.commit();
    }

    /**
     * @param e        存放的key
     * @param value    存放的value
     * @param saveTime 缓存时间
     */
    public void setString(String e, String value, int saveTime) {
        SpSaveModel<String> spSaveModel = new SpSaveModel<>(saveTime, value, System.currentTimeMillis());
        String json = JSON.toJSONString(spSaveModel);
        editor.putString(e, json);
        editor.commit();
    }

    /**
     * @param e        存放的key
     * @param defValue 该key不存在或者过期时，返回的默认值
     * @return
     */
    public String getString(String e, String defValue) {
        String json = sp.getString(e, "");
        if (!TextUtils.isEmpty(json)) {
            SpSaveModel<String> spSaveModel = JSON.parseObject(json, new TypeReference<SpSaveModel<String>>() {
            });
            if (isTimeOut(spSaveModel.getCurrentTime(), spSaveModel.getSaveTime())) {
                return spSaveModel.getValue();
            }
        }
        return defValue;
    }

    public void setInt(String e, int value) {
        SpSaveModel<Integer> spSaveModel = new SpSaveModel<>(TIME_MAX, value, System.currentTimeMillis());
        String json = JSON.toJSONString(spSaveModel);
        editor.putString(e, json);
        editor.commit();
    }

    public void setInt(String e, int value, int saveTime) {
        SpSaveModel<Integer> spSaveModel = new SpSaveModel<>(saveTime, value, System.currentTimeMillis());
        String json = JSON.toJSONString(spSaveModel);
        editor.putString(e, json);
        editor.commit();
    }

    public Integer getInt(String e, int defValue) {
        String json = sp.getString(e, "");
        if (!TextUtils.isEmpty(json)) {
            SpSaveModel<Integer> spSaveModel = JSON.parseObject(json, new TypeReference<SpSaveModel<Integer>>() {
            });
            if (isTimeOut(spSaveModel.getCurrentTime(), spSaveModel.getSaveTime())) {
                return spSaveModel.getValue();
            }
        }
        return defValue;
    }

    public void setBoolean(String e, boolean value) {
        SpSaveModel<Boolean> spSaveModel = new SpSaveModel<>(TIME_MAX, value, System.currentTimeMillis());
        String json = JSON.toJSONString(spSaveModel);
        editor.putString(e, json);
        editor.commit();
    }

    public void setBoolean(String e, boolean value, int saveTime) {
        SpSaveModel<Boolean> spSaveModel = new SpSaveModel<>(saveTime, value, System.currentTimeMillis());
        String json = JSON.toJSONString(spSaveModel);
        editor.putString(e, json);
        editor.commit();
    }

    public boolean getBoolean(String e, boolean defValue) {
        String json = sp.getString(e, "");
        if (!TextUtils.isEmpty(json)) {
            SpSaveModel<Boolean> spSaveModel = JSON.parseObject(json, new TypeReference<SpSaveModel<Boolean>>() {
            });
            if (isTimeOut(spSaveModel.getCurrentTime(), spSaveModel.getSaveTime())) {
                return spSaveModel.getValue();
            }
        }
        return defValue;
    }

    public void setLong(String e, long value) {
        SpSaveModel<Long> spSaveModel = new SpSaveModel<>(TIME_MAX, value, System.currentTimeMillis());
        String json = JSON.toJSONString(spSaveModel);
        editor.putString(e, json);
        editor.commit();
    }

    public void setLong(String e, long value, int saveTime) {
        SpSaveModel<Long> spSaveModel = new SpSaveModel<>(saveTime, value, System.currentTimeMillis());
        String json = JSON.toJSONString(spSaveModel);
        editor.putString(e, json);
        editor.commit();
    }

    public long getLong(String e, long defValue) {
        String json = sp.getString(e, "");
        if (!TextUtils.isEmpty(json)) {
            SpSaveModel<Long> spSaveModel = JSON.parseObject(json, new TypeReference<SpSaveModel<Long>>() {
            });
            if (isTimeOut(spSaveModel.getCurrentTime(), spSaveModel.getSaveTime())) {
                return spSaveModel.getValue();
            }
        }
        return defValue;
    }

    public boolean isTimeOut(long saveCurrentTime, int saveTime) {
        return (System.currentTimeMillis() - saveCurrentTime) / DURATION_UNIT < saveTime;
    }

    public void set(String e, Object value, int saveTime) {
        SpSaveModel<Object> spSaveModel = new SpSaveModel<>(saveTime, value, System.currentTimeMillis());
        String json = JSON.toJSONString(spSaveModel);
        editor.putString(e, json);
        editor.commit();
    }
}