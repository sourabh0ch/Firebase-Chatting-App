package com.easy.easychat.Utills;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefrenceUtil {
    private static final String IS_LOGIN = "isLogIn";

    private static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setIsLogIn(Context ctx, boolean isLogIn) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(IS_LOGIN, isLogIn);
        editor.commit();

    }

    public static boolean isLogIn(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(IS_LOGIN, false);
    }
}
