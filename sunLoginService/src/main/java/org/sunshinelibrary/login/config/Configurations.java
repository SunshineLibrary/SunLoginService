package org.sunshinelibrary.login.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.sunshinelibrary.login.R;

/**
 * @author Bowen Sun
 * @version 1.0
 */
public class Configurations {

    public static String API_SERVER_ADDRESS = "api_server_address";

    private SharedPreferences preference;
    private Context context;

    public Configurations(Context context) {
        this.context = context;
        this.preference = context.getApplicationContext().getSharedPreferences("API_SERVER_ADDRESS",Context.MODE_WORLD_READABLE);
    }

    public String getString(String key) {
        return preference.getString(key, getDefaultString(key));
    }

    public synchronized void putString(String key, String value) {
        SharedPreferences.Editor editor = preference.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getDefaultString(String key) {
        if (key.equals(API_SERVER_ADDRESS))
            return context.getString(R.string.default_api_server_address);
        return "";
    }
}
