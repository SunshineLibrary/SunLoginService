package org.sunshinelibrary.login.broadcastreciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.sunshinelibrary.login.utils.LogUtil;


/**
 * Copyright (c) 2014 Guanghe.tv All right reserved.
 * --------------------<-.->-----------------------
 * Author:      Nono(陈凯)
 * CreateDate:  14/12/24
 * Description: exp...
 * Version:     1.0.0
 */
public class MediaMountStatusReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        String action = intent.getAction();
        LogUtil.log("MediaMountReciver action = " + action);
        if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
            LogUtil.log("ACTION_MEDIA_MOUNTED SD入侵!!!!!");
            //

        }else if (Intent.ACTION_MEDIA_UNMOUNTED.equals(action)){
            LogUtil.log("ACTION_MEDIA_UNMOUNTED SD拔出!!!!!");
        }
    }
}


