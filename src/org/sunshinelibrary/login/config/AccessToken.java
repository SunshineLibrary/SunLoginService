package org.sunshinelibrary.login.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.Intent;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.sunshinelibrary.login.WhiteList;
import org.sunshinelibrary.login.utils.StringUtils;

/**
 * @author Yanan Guo
 * @version 2.0
 */

public class AccessToken {

    public static final String ACCOUNT_TYPE_STUDENT = "student";
    public static final String ACCOUNT_TYPE_TEACHER = "teacher";

    private static String mAccessToken = StringUtils.EMPTY_STRING;
    private static String mAccountType = StringUtils.EMPTY_STRING;
    private static String mUserName = StringUtils.EMPTY_STRING;
    private static String mUserBirthday = StringUtils.EMPTY_STRING;
    private static String mUserSchool = StringUtils.EMPTY_STRING;
    private static String mUserGrade = StringUtils.EMPTY_STRING;
    private static String mUserClass = StringUtils.EMPTY_STRING;
    private static final String TAG = "AccessToken";

    private static WhiteList mWhiteList;


    public static final String getAccessToken(Context context) {
        if (StringUtils.isEmpty(mAccessToken)) {
            retrieveAccessToken(context);
        }
        return mAccessToken;
    }

    public static final String getAccountType(Context context) {
        if (StringUtils.isEmpty(mAccountType)) {
            retrieveAccessToken(context);
        }
        return mAccountType;
    }

    public static final String getUserName(Context context) {
        if (StringUtils.isEmpty(mUserName)) {
            retrieveAccessToken(context);
        }
        return mUserName;
    }

    public static final String getUserBirthday(Context context) {
        if (StringUtils.isEmpty(mUserBirthday)) {
            retrieveAccessToken(context);
        }
        return mUserBirthday;
    }

    public static final String getUserSchool(Context context) {
        if (StringUtils.isEmpty(mUserSchool)) {
            retrieveAccessToken(context);
        }
        return mUserSchool;
    }

    public static final String getUserGrade(Context context) {
        if (StringUtils.isEmpty(mUserGrade)) {
            retrieveAccessToken(context);
        }
        return mUserGrade;
    }

    public static final String getUserClass(Context context) {
        if (StringUtils.isEmpty(mUserClass)) {
            retrieveAccessToken(context);
        }
        return mUserClass;
    }

    public static final void storeAccessToken(Context context, JSONObject jo, boolean a) throws JSONException {

        if (a){
            mAccessToken = jo.getString("access_token");
        }

        JSONObject jsonNow = jo;
        JSONArray allowNow = jsonNow.getJSONObject("user_info").getJSONArray("user_allowed_apps");

        try{
            Context mContext = context.createPackageContext
                    ("org.sunshinelibrary.login", Context.MODE_MULTI_PROCESS);

            SharedPreferences preferences = mContext.getSharedPreferences("LOGIN",
                    Context.MODE_MULTI_PROCESS);
            SharedPreferences.Editor editor = preferences.edit();

            JSONObject jsonOrigin = new JSONObject(preferences.getString("USER_INFO", ""));

            if(!jsonOrigin.equals("")){

                JSONArray allowOrigin = jsonOrigin.getJSONArray("user_allowed_apps");

                mWhiteList = new WhiteList(allowOrigin,allowNow,context);

                if(mWhiteList.isChanged()){
                    mWhiteList.informChanged();
                    Log.i(TAG, "Whitelist changed");
                    if(!StringUtils.isEmpty(mAccessToken)){
                        editor.putString("ACCESS_TOKEN", mAccessToken);
                    }
                    editor.putString("USER_INFO",jsonNow.getJSONObject("user_info").toString());
                    editor.commit();
                    return;
                }else{
                    Log.i(TAG,"Whitelist stayed still");
                    if(!StringUtils.isEmpty(mAccessToken)){
                        editor.putString("ACCESS_TOKEN", mAccessToken);
                    }
                    editor.putString("USER_INFO",jsonNow.getJSONObject("user_info").toString());
                    editor.commit();
                    return;
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        SharedPreferences preferences = context.getSharedPreferences("LOGIN",Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = preferences.edit();
        if(!StringUtils.isEmpty(mAccessToken)){
            editor.putString("ACCESS_TOKEN", mAccessToken);
        }
        editor.putString("USER_INFO",jsonNow.getJSONObject("user_info").toString());
        editor.commit();

        mWhiteList = new WhiteList(null, allowNow,  context);
        Intent intent  = new Intent("org.sunshinelibrary.launcher.WHITE_LIST_CHANGED");
        intent.putStringArrayListExtra("add",mWhiteList.getOrderedPackages(allowNow));
        context.sendBroadcast(intent);
    }

    public static final void clearPreference(Context context){

        context.getSharedPreferences("LOGIN",Context.MODE_MULTI_PROCESS).edit().clear().commit();

    }

    public static final void retrieveAccessToken(Context context) {

        SharedPreferences preferences = context.getSharedPreferences("LOGIN",
                Context.MODE_WORLD_READABLE);
        try{
            mAccessToken = preferences.getString("ACCESS_TOKEN", "");
           /* mUserName = preferences.getString(UserInfo.EXTRA_USER_NAME, "");
            mAccountType = preferences.getString(UserInfo.EXTRA_USER_TYPE, "");
            mUserBirthday = preferences.getString(UserInfo.EXTRA_USER_BIRTHDAY, "");
            mUserSchool = preferences.getString(UserInfo.EXTRA_USER_SCHOOL, "");
            mUserGrade = preferences.getString(UserInfo.EXTRA_USER_GRADE, "");
            mUserClass = preferences.getString(UserInfo.EXTRA_USER_CLASS, "");*/
        }catch (Exception e){
            e.printStackTrace();

        }
    }
}