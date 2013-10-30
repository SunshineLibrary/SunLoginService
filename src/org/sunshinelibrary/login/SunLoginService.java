package org.sunshinelibrary.login;

/**
 * Created with IntelliJ IDEA.
 * User: solomon
 * Date: 13-7-30
 * Time: 下午3:27
 */

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Binder;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import org.sunshinelibrary.login.utils.ConnectionUtils;
import org.sunshinelibrary.login.utils.StringUtils;
import org.sunshinelibrary.login.config.AccessToken;

public class SunLoginService extends Service {

    SignInPresenter mPresenter;
    CanclableObserver mActivity;
    SignInActivity mSignInActivity;
    String[] mSchoolStrings;
    String[] mEmptySchoolStrings;
    String[] userInfo;
    private static final String TAG = "SunLoginService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        intent = new Intent();
        intent.setClass(SunLoginService.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        return START_STICKY;
    }

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        SunLoginService getService() {
            return SunLoginService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class CheckLoginTask extends AsyncTask<Object, Object, JSONObject>{

        @Override
        protected void onPreExecute (){
            mPresenter = new SignInPresenter(mSignInActivity);

            String accessToken = AccessToken.getAccessToken(SunLoginService.this);
            Log.i(TAG, "accessToken = " + accessToken);

            if(accessToken.equals("")) {
                Log.i(TAG, "Couldn't find any UserInfo for checking login");
                popupLoginWindow();
                this.cancel(true);
            }

            mPresenter.setAccessToken(accessToken);
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
                return mPresenter.checkLogin();
        }

        @Override
        protected void onPostExecute(JSONObject jo) {

            if(jo!=null){
                try {
                    if(jo.getString("status").equals("200")){
                        Log.i(TAG,"statuscode"+jo.getString("status"));
                        try {
                            AccessToken.storeAccessToken(SunLoginService.this, jo, false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        notifyAlreadyLogin("success");
                        return;
                    }else if(jo.getString("status").equals("401")){
                        popupLoginWindow();
                        AccessToken.clearPreference(SunLoginService.this);

                        //insure that sunapps won't be showed on Launcher when user logged out
                        Intent intent = new Intent("org.sunshinelibrary.launcher.ONLY_KEEP_MIN_WHITELIST");
                        sendBroadcast(intent);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(SunLoginService.this,mPresenter.getErrorMessage(),Toast.LENGTH_SHORT).show();
            notifyAlreadyLogin("failure_oncheck");
            return;
        }
    }

    public class LoginTask extends AsyncTask<Object,Object,JSONObject>{

        @Override
        protected JSONObject doInBackground(Object... params) {
            mPresenter = new SignInPresenter(mSignInActivity);
            mSchoolStrings = mPresenter.loadSchools(mEmptySchoolStrings);
            if(mSchoolStrings!=null){
                mPresenter.setSchool(mSchoolStrings[0]);
                Log.i(TAG,mSchoolStrings[0]);
                mPresenter.setAccountType(userInfo[0]);
                mPresenter.setGrade(userInfo[1]);
                mPresenter.setClass(userInfo[2]);
                mPresenter.setName(userInfo[3]);
                return mPresenter.authenticate();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jo){

            if(jo!=null){
                try {
                    if(!StringUtils.isEmpty(jo.getString("access_token"))) {
                        try {
                            AccessToken.storeAccessToken(SunLoginService.this,jo,true);
                            Log.i(TAG,"succeed to create sharedpreference");
                        } catch (JSONException e) {
                            Log.i(TAG,"failed to create sp");
                        }
                        notifyAlreadyLogin("success");
                        return;
                    }else{
                        Log.i(TAG,mPresenter.getErrorMessage());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(SunLoginService.this,mPresenter.getErrorMessage(),Toast.LENGTH_SHORT).show();
            notifyAlreadyLogin("failure_onlogin");
            return;
        }
    }

   /* public class LoadSchoolTask extends AsyncTask<Object,Object,String>{
        @Override
        protected String doInBackground(Object... params){
            mPresenter = new SignInPresenter(mSignInActivity);

            mSchoolStrings = mPresenter.loadSchools(mEmptySchoolStrings);
            return mSchoolStrings[0];
        }
    }*/

    public void setCurrentActivity (CanclableObserver co, SignInActivity sa){
        mActivity = co;
        mSignInActivity = sa;
    }

    public void notifyAlreadyLogin(String situation){
       try{
           mActivity.dismissDialog(situation);
       }catch (Exception e){
           Log.e(TAG,"activity not found");
       }
       this.stopSelf();
    }

    public void popupLoginWindow(){
        try{
            mActivity.displayLoginWindow();
        }catch (Exception e){
            Log.e(TAG,"activity not found");
        }
    }

    public void doCheckSignIn(){
        if(ConnectionUtils.isConnectbed(SunLoginService.this)){
            new CheckLoginTask().execute();
        }else{
            notifyAlreadyLogin("no network");
        }
    }

    public void doSignIn(String[] info){
        if(ConnectionUtils.isConnectbed(SunLoginService.this)){
            userInfo = info;
            new LoginTask().execute();
        }else{
            notifyAlreadyLogin("no network");
        }
    }

   /* public void doLoadSchool(){
        if(ConnectionUtils.isConnectbed(SunLoginService.this)){
           new LoadSchoolTask().execute();
        }else{
            Toast.makeText(SunLoginService.this,"无网络连接，请稍候重试",Toast.LENGTH_LONG).show();
            notifyAlreadyLogin("no network");
        }
    }*/
}