package org.sunshinelibrary.SunLoginService;

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
import org.sunshinelibrary.SunLoginService.utils.ConnectionUtils;
import org.sunshinelibrary.SunLoginService.utils.StringUtils;
import org.sunshinelibrary.SunLoginService.config.AccessToken;

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
                            try {
                                AccessToken.storeSomething(SunLoginService.this, jo);
                                System.out.println("succeed to create sharedpreference");
                            } catch (JSONException e) {
                                System.out.println("failed to create sp");
                                e.printStackTrace();
                            }
                            notifyAlreadyLogin(true);
                            return;
                        }else if(jo.getString("status").equals("401")){
                            popupLoginWindow();
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(SunLoginService.this,mPresenter.getErrorMessage(),Toast.LENGTH_SHORT).show();
                notifyAlreadyLogin(true);
            }
    }

    public class LoginTask extends AsyncTask<Object,Object,JSONObject>{

        @Override
        protected JSONObject doInBackground(Object... params) {

            mPresenter = new SignInPresenter(mSignInActivity);

            mSchoolStrings = mPresenter.loadSchools(mEmptySchoolStrings);
            mPresenter.setSchool(mSchoolStrings[0]);

            mPresenter.setAccountType(userInfo[0]);
            mPresenter.setGrade(userInfo[1]);
            //mPresenter.setClass(userInfo[2]);
            mPresenter.setName(userInfo[3]);
            mPresenter.setBirthday("2009-09-01");

            return mPresenter.authenticate();
        }

        @Override
        protected void onPostExecute(JSONObject jo){

            if(jo!=null){
                try {
                    if(!StringUtils.isEmpty(jo.getString("access_token"))) {
                        try {
                            AccessToken.storeAccessToken(SunLoginService.this,jo);
                            Log.i(TAG,"succeed to create sharedpreference");
                        } catch (JSONException e) {
                            Log.i(TAG,"failed to create sp");
                        }
                        notifyAlreadyLogin(true);
                    }else{
                        Log.i(TAG,mPresenter.getErrorMessage());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(SunLoginService.this,mPresenter.getErrorMessage(),Toast.LENGTH_SHORT).show();
            notifyAlreadyLogin(false);
        }

    }

    public class LoadSchoolTask extends AsyncTask<Object,Object,String>{
        @Override
        protected String doInBackground(Object... params){

            mPresenter = new SignInPresenter(mSignInActivity);
            mSchoolStrings = mPresenter.loadSchools(mEmptySchoolStrings);

            return mSchoolStrings[0];
        }
    }

    public void setCurrentActivity (CanclableObserver co, SignInActivity sa){
        mActivity = co;
        mSignInActivity = sa;
    }

    public void notifyAlreadyLogin(boolean a){
       try{
           mActivity.dismissDialog(a);
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
            notifyAlreadyLogin(true);
        }
    }

    public void doSignIn(String[] info){
        if(ConnectionUtils.isConnectbed(SunLoginService.this)){
            userInfo = info;
            new LoginTask().execute();
        }else{
            //TODO: add a button to reconnect wifi.
        }
    }

    public void doLoadSchool(){
        if(ConnectionUtils.isConnectbed(SunLoginService.this)){
           new LoadSchoolTask().execute();
        }

    }
}