package org.sunshinelibrary.SunLoginService.api.record;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.sunshinelibrary.SunLoginService.utils.JSONSerializable;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Bowen Sun
 * @version 1.0
 *
 */
public class UserRecordRequest implements Parcelable, JSONSerializable {

    private static final String TAG = "UserRecordRequest";

    private JSONObject mAsJson;

    public UserRecordRequest(JSONObject asJson) {
        mAsJson = asJson;
    }

    public static UserRecordRequest newFromString(String jsonString) {
        try {
            return new UserRecordRequest(new JSONObject(jsonString));
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse: " + jsonString, e);
            return null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAsJson.toString());
    }

    public static final Creator<UserRecordRequest> CREATOR = new Creator<UserRecordRequest>() {
        @Override
        public UserRecordRequest createFromParcel(Parcel source) {
            String jsonString = source.readString();
            try {
                return new UserRecordRequest(new JSONObject(jsonString));
            } catch (JSONException e) {
                Log.e(TAG, "Failed to parse: " + jsonString, e);
                return null;
            }
        }

        @Override
        public UserRecordRequest[] newArray(int size) {
            return new UserRecordRequest[size];
        }
    };

    @Override
    public JSONObject toJSON() {
        return mAsJson;
    }

    @Override
    public String toString() {
        return mAsJson.toString();
    }
}
