package org.sunshinelibrary.SunLoginService;

import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import org.sunshinelibrary.SunLoginService.api.ApiClient;
import org.sunshinelibrary.SunLoginService.api.ApiClientFactory;
import org.sunshinelibrary.SunLoginService.utils.HttpUtils;
import org.sunshinelibrary.SunLoginService.utils.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sunshinelibrary.support.utils.JSONUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yanan Guo
 * @version 2.0
 */

public class SignInPresenter {

    private static final String TAG = "SignInPresenter";

    private SignInActivity mSignInActivity;
    private ApiClient apiClient;
    private String machineId = StringUtils.EMPTY_STRING;

    private String mSchoolId = StringUtils.EMPTY_STRING;
    private String mAccountType = StringUtils.EMPTY_STRING;
    private String mBirthday = StringUtils.EMPTY_STRING;
    private String mGrade = StringUtils.EMPTY_STRING;
    private String mName = StringUtils.EMPTY_STRING;
    private String errMessage = StringUtils.EMPTY_STRING;
    private String mAccessToken = StringUtils.EMPTY_STRING;

    private String[] schoolStrings;
    private Map<String, String> schoolIds;

    public SignInPresenter(SignInActivity signInActivity) {
        mSignInActivity = signInActivity;
        apiClient = ApiClientFactory.newApiClient(signInActivity);

        machineId = Settings.Secure.getString(mSignInActivity.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (machineId == null) {
            machineId = StringUtils.EMPTY_STRING;
        }
    }

    public JSONObject checkLogin() {
        HttpGet request = new HttpGet(apiClient.getCheckLoginUri(mAccessToken).toString());
        String str = HttpUtils.getResponse(new DefaultHttpClient(),request,HttpUtils.CHECK);
        JSONObject response = JSONUtils.parse(str);

        try {
            if ("200".equals(response.getString("status"))||"401".equals(response.getString("status"))) {
                return response;
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse status.", e);
            errMessage =  mSignInActivity.getString(R.string.auth_failure);
        }
        return null;
    }

    public JSONObject authenticate() {
        HttpPost post = getPostMessage();
        String str = HttpUtils.getResponse(new DefaultHttpClient(), post,HttpUtils.LOGIN);
        JSONObject response = JSONUtils.parse(str);
        try {
            if ("200".equals(response.getString("status"))) {
                return response;
            } else {
                errMessage = response.getString("message");
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse status.", e);
            errMessage =  mSignInActivity.getString(R.string.auth_failure);
        }
        return null;
    }

    public void setSchool(String schoolName) {
        mSchoolId = (schoolIds != null) ? schoolIds.get(schoolName) : StringUtils.EMPTY_STRING;
        mSchoolId = (mSchoolId != null) ? mSchoolId : StringUtils.EMPTY_STRING;
    }
    
    public String getSchool() {
    	return mSchoolId;
    }

    public void setAccountType(String accountType) {
        mAccountType = accountType;
    }
    
    public String getAccountType() {
    	return mAccountType;
    }

    public void setGrade(String grade) {
        mGrade = grade;
    }
    
    public String getGrade() {
    	return mGrade;
    }

    public void setName(String name) {
        mName = name;
    }
    
    public String getName() {
        return mName;
    }

    public void setBirthday(String birthday) {
        mBirthday = birthday;
    }
    
    public String getBirthday() {
    	return mBirthday;
    }

    public String getErrorMessage() {
        return errMessage;
    }

    public void setAccessToken(String accessToken){
        mAccessToken = accessToken;
    }

    public String[] loadSchools(String[] emptySchoolStrings) {
        HttpGet request = new HttpGet(apiClient.getAllSchoolsUri().toString());
        String str = HttpUtils.getResponse(new DefaultHttpClient(), request,HttpUtils.LOGIN);
        JSONArray jsonArr = JSONUtils.parseArray(str);
        try {
            schoolStrings = new String[jsonArr.length()];
            schoolIds = new HashMap<String, String>();
            String name, id;
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject row = jsonArr.getJSONObject(i);
                id = row.getString("uuid");
                name = row.getString("name");
                schoolIds.put(name, id);
                schoolStrings[i] = name;
            }
        } catch (JSONException e) {
            Log.e(TAG, "Unable to parse school list: " + jsonArr);
        }
        if (schoolStrings != null && schoolStrings.length != 0)
            return schoolStrings;
        else
            return emptySchoolStrings;
    }

    private HttpPost getPostMessage() {
        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("machine_id", machineId));
        postParameters.add(new BasicNameValuePair("os_type", "android"));
        postParameters.add(new BasicNameValuePair("os_version", Build.VERSION.RELEASE));
        postParameters.add(new BasicNameValuePair("name", mName));
        postParameters.add(new BasicNameValuePair("user_type", mAccountType));
        postParameters.add(new BasicNameValuePair("school_id", mSchoolId));
        postParameters.add(new BasicNameValuePair("grade", mGrade));
        postParameters.add(new BasicNameValuePair("birthday", mBirthday));

        HttpPost post = new HttpPost(apiClient.getLoginUri().toString());
        try {
            post.setEntity(new UrlEncodedFormEntity(postParameters, "utf8"));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Failed to create post message for authentication.", e);
        }
        return post;
    }
}

