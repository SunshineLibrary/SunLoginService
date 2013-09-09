package org.sunshinelibrary.login;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: solomon
 * Date: 13-7-30
 * Time: 下午4:33
 */

public class BootCompletedReceiver extends BroadcastReceiver {

    static final String ACTION_BOOTUP = "android.intent.action.BOOT_COMPLETED";
    static final String ACTION_RECONNECT = "android.net.conn.CONNECTIVITY_CHANGE";

    static final int BOOT_UP = 0;
    static final int RECONNECT_WIFI = 1;

    @Override
    public void onReceive (Context context, Intent intent){

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(intent.getAction().equals(ACTION_BOOTUP)) {
            startLoginService(context, BOOT_UP);
        }

        if(intent.getAction().equals(ACTION_RECONNECT) && intent.getBooleanExtra(ConnectivityManager.CONNECTIVITY_ACTION,cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) )  {
            startLoginService(context, RECONNECT_WIFI);
        }
    }

    private void startLoginService(Context context, int situation) {

        Intent i = new Intent(context,SunLoginService.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(i);

        switch (situation){
            case BOOT_UP :
                Toast.makeText(context, "检测到机器重启，启动登录服务", Toast.LENGTH_SHORT).show();
                break;
            case RECONNECT_WIFI :
                Toast.makeText(context, "检测到断网重连，启动登录服务", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}