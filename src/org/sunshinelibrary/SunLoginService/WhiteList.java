package org.sunshinelibrary.SunLoginService;

import android.content.Intent;
import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: solomon
 * Date: 13-8-21
 * Time: 上午11:26
 */

public class WhiteList {

    private JSONArray mJsonArrayOrigin;
    private JSONArray mJsonArrayNow;
    private JSONArray mArray;
    private Context mContext;
    private static final String TAG = "WhiteListRelated";

    public WhiteList(JSONArray arrayOrigin, JSONArray arrayNow, Context context){

        mJsonArrayOrigin = arrayOrigin;
        mJsonArrayNow = arrayNow;
        mContext = context;

    }

    public WhiteList(JSONArray array){

        mArray = array;

    }

    private boolean isLengthEqual(){

        return mJsonArrayOrigin.length()==mJsonArrayNow.length();

    }

    public boolean isChanged(){

        if(!isLengthEqual()){
            return true;
        }

        // If these two ArrayLists is lengthEqual, check their fist item.
        ArrayList<String> mArrayListOrigin = getOrderedPackages(mJsonArrayOrigin);
        ArrayList<String> mArrayListNow = getOrderedPackages(mJsonArrayNow);

        return !mArrayListOrigin.get(0).equals(mArrayListNow.get(0));
    }

    public ArrayList<String> getOrderedPackages(JSONArray array){

        ArrayList<String> list = new ArrayList<String>();

        for(int i=0;i<array.length();i++){
            try {
                list.add(array.getString(i));
            } catch (JSONException e) {
                Log.e(TAG,"failed to get package name from JsonArray");
            }
        }

        Collections.sort(list,APP_NAME_COMPARATOR);
        System.out.println("ordered list"+list);

        return list;
    }

    private Collator sCollator = Collator.getInstance();

    public Comparator<String> APP_NAME_COMPARATOR
            = new Comparator<String>() {
        public int compare(String a, String b) {
            return sCollator.compare(a, b);
        }
    };

    public void informChanged(){
        Intent intent  = new Intent("org.sunshinelibrary.launcher.WHITE_LIST_CHANGED");
        intent.putStringArrayListExtra("remove",getOrderedPackages(mJsonArrayOrigin));
        intent.putStringArrayListExtra("add",getOrderedPackages(mJsonArrayNow));
        mContext.sendBroadcast(intent);
    }

}