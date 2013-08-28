package org.sunshinelibrary.SunLoginService.api;

import android.content.Context;
import android.net.Uri;
import org.apache.http.NameValuePair;
import org.sunshinelibrary.SunLoginService.config.AccessToken;
import org.sunshinelibrary.SunLoginService.config.Configurations;

import java.util.ArrayList;

public class ApiClient {

    private Context mContext;
    private Configurations mConfigs;
    private String accessToken;

    public ApiClient(Context context, Configurations configs) {
        mContext = context;
        mConfigs = configs;
    }

    public String getSyncRequestUrl(String tableName, long lastUpdateTime) {
        return getApiServerUri().buildUpon().appendPath("api").appendPath(getApiPath(tableName))
            .appendQueryParameter("timestamp", String.valueOf(lastUpdateTime / 1000))
            .appendQueryParameter("access_token", getAccessToken()).build()
            .toString();
    }

    public String getApiServerHost() {
        return mConfigs.getString(Configurations.API_SERVER_ADDRESS);
    }

    private Uri getApiServerUri() {
        String host = getApiServerHost();
        return new Uri.Builder().scheme("http").encodedAuthority(host).build();
    }

    private String getAccessToken() {
        if (accessToken == null) {
            accessToken = AccessToken.getAccessToken(mContext);
        }
        return accessToken;
    }

    private String getApiPath(String tableName) {
        return tableName + ".json";
    }

    public Uri getDownloadUri(String type, long id) {
        return getApiServerUri().buildUpon().appendPath("download").appendPath(type).appendPath(String.valueOf(id))
            .appendQueryParameter("access_token", getAccessToken()).build();
    }

    public Uri getApkUpdateUri() {
        return getApiServerUri().buildUpon().appendPath("apks").appendPath("get_updates")
            .appendQueryParameter("access_token", getAccessToken()).build();
    }

    public Uri getAllSchoolsUri() {
        return getApiServerUri().buildUpon().appendPath("schools").appendEncodedPath("get_all.json").build();
    }

    public Uri getLoginUri() {
        return getApiServerUri().buildUpon().appendPath("machines").appendPath("sign_in.json").build();
    }

    public Uri getCheckLoginUri(String accessToken){
        return getApiServerUri().buildUpon().appendPath("machines").appendEncodedPath("check_token?access_token="+accessToken).build();
    }

    public Uri getUserRecordPostUri() {
        return getApiServerUri().buildUpon().appendPath("user_records").appendPath("batch_update.json").build();
    }
}
