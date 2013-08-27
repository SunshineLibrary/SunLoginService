package org.sunshinelibrary.SunLoginService.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * @author Bowen Sun
 * @version 1.0
 */
public class ConnectionUtils {

    private static ConnectivityManager cm;
    private static String TAG = "ConnectionUtils";
    private static WifiManager wm;

    public static boolean isConnectbed(Context context) {
        initConnectivityManager(context);
        if (cm != null && cm.getActiveNetworkInfo() != null) {
            if(cm.getActiveNetworkInfo().isConnected()){
                return true;
            } else {
//                wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
//                if(wm.setWifiEnabled(true)){
//                    if(wm.reconnect()){
//                        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//                        if(cm.getActiveNetworkInfo().isConnected()){
//                            return true;
//                        }else{
//                            return false;
//                        }
//                    }else{
//                        return false;
//                    }
//                }else{
//                    return false;
//                }
                return false;

            }
        }else{
           return false;
        }
    }

    private static void initConnectivityManager(Context context) {
        if (cm == null) {
            cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
    }
}