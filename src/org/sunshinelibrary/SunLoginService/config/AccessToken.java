package org.sunshinelibrary.SunLoginService.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sunshinelibrary.SunLoginService.SignInPresenter;
import org.sunshinelibrary.SunLoginService.SunLoginService;
import org.sunshinelibrary.SunLoginService.WhiteList;
import org.sunshinelibrary.SunLoginService.utils.StringUtils;
import org.sunshinelibrary.support.api.UserInfo;
import android.content.Intent;

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

    public static final void storeAccessToken(Context context, JSONObject jo) throws JSONException {

        mAccessToken = jo.getString("access_token");

        JSONObject jsonNow = jo;
        JSONArray allowNow = jsonNow.getJSONObject("user_info").getJSONArray("user_allowed_apps");

        try{
            Context mContext = context.createPackageContext
                    ("org.sunshinelibrary.SunLoginService", Context.MODE_MULTI_PROCESS);

            SharedPreferences preferences = mContext.getSharedPreferences(UserInfo.SP_NAME,
                    Context.MODE_MULTI_PROCESS);
            SharedPreferences.Editor editor = preferences.edit();

            JSONObject jsonOrigin = new JSONObject(preferences.getString("USER_INFO", ""));

            if(!jsonOrigin.equals("")){

                JSONArray allowOrigin = jsonOrigin.getJSONObject("user_info").getJSONArray("user_allowed_apps");

                mWhiteList = new WhiteList(allowOrigin,allowNow,context);

                if(mWhiteList.isChanged()){
                    mWhiteList.informChanged();
                    System.out.println("whitelist changed");
                    editor.putString(UserInfo.EXTRA_ACCESS_TOKEN, mAccessToken);
                    editor.putString("USER_INFO",jsonNow.toString());
                    editor.commit();
                    return;
                }else{
                    System.out.println("whitelist stayed still");
                    editor.putString(UserInfo.EXTRA_ACCESS_TOKEN, mAccessToken);
                    editor.putString("USER_INFO",jsonNow.toString());
                    editor.commit();
                    return;
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        SharedPreferences preferences = context.getSharedPreferences(UserInfo.SP_NAME,Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(UserInfo.EXTRA_ACCESS_TOKEN, mAccessToken);
        editor.putString("USER_INFO",jsonNow.toString());
        editor.commit();

        mWhiteList = new WhiteList(allowNow);
        Intent intent  = new Intent("org.sunshinelibrary.launcher.WHITE_LIST_CHANGED");
        intent.putStringArrayListExtra("add",mWhiteList.getOrderedPackages(allowNow));
        context.sendBroadcast(intent);

    }

    public static final void storeSomething(Context context,JSONObject jo) throws JSONException {


        JSONObject jsonNow = jo;

        JSONArray allowNow = jsonNow.getJSONObject("user_info").getJSONArray("user_allowed_apps");

        try{
            Context mContext = context.createPackageContext
                    ("org.sunshinelibrary.SunLoginService", Context.MODE_MULTI_PROCESS);

            SharedPreferences preferences = mContext.getSharedPreferences(UserInfo.SP_NAME,
                    Context.MODE_MULTI_PROCESS);
            SharedPreferences.Editor editor = preferences.edit();

            JSONObject jsonOrigin = new JSONObject(preferences.getString("USER_INFO", ""));

            if(!jsonOrigin.equals("")){

                JSONArray allowOrigin = jsonOrigin.getJSONArray("user_allowed_apps");

                mWhiteList = new WhiteList(allowOrigin,allowNow,context);

                if(mWhiteList.isChanged()){
                    mWhiteList.informChanged();
                    System.out.println("whitelist changed");
                    editor.putString(UserInfo.EXTRA_ACCESS_TOKEN, mAccessToken);
                    editor.putString("USER_INFO",jsonNow.toString());
                    editor.commit();
                    return;
                }else{
                    System.out.println("whitelist stayed still");
                    editor.putString(UserInfo.EXTRA_ACCESS_TOKEN, mAccessToken);
                    editor.putString("USER_INFO",jsonNow.toString());
                    editor.commit();
                    return;
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        SharedPreferences preferences = context.getSharedPreferences(UserInfo.SP_NAME,Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("USER_INFO",jsonNow.toString());
        editor.commit();

        mWhiteList = new WhiteList(allowNow);
        Intent intent  = new Intent("org.sunshinelibrary.launcher.WHITE_LIST_CHANGED");
        intent.putStringArrayListExtra("add",mWhiteList.getOrderedPackages(allowNow));
        context.sendBroadcast(intent);
    }

    public static final void retrieveAccessToken(Context context) {

        SharedPreferences preferences = context.getSharedPreferences(UserInfo.SP_NAME,
                Context.MODE_WORLD_READABLE);
        try{
            mAccessToken = preferences.getString(UserInfo.EXTRA_ACCESS_TOKEN, "");
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
