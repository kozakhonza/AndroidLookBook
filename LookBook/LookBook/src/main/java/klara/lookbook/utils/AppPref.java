package klara.lookbook.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPref {
    public static final String PREFS_NAME = "LookBookPrefFile";

    public static final String KEY_EMAIL = "key_email";
    public static final String KEY_PASSWORD = "key_pass";

    public static void put(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public static void put(Context context, String key, Boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    public static void put(Context context, String key, Integer value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }
    public static void put(Context context, String key, Float value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.commit();
    }
    public static String get(Context context, String key, String defValue) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
        return preferences.getString(key, defValue);
    }
    public static Boolean get(Context context, String key, Boolean defValue) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
        return preferences.getBoolean(key, defValue);
    }
    public static Integer get(Context context, String key, Integer defValue) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
        return preferences.getInt(key, defValue);
    }
    public static Float get(Context context, String key, Float defValue) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
        return preferences.getFloat(key, defValue);
    }
}
