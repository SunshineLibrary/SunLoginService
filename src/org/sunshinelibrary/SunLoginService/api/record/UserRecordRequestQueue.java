package org.sunshinelibrary.SunLoginService.api.record;

import android.content.Context;
import org.sunshinelibrary.SunLoginService.utils.DatabaseQueue;
import org.sunshinelibrary.SunLoginService.utils.JSONSerializable;

public class UserRecordRequestQueue extends DatabaseQueue<UserRecordRequest> {

    public UserRecordRequestQueue(Context context, JSONSerializable.Factory<UserRecordRequest> installRecordFactory) {
        super(context, "user_record_request_queue", installRecordFactory);
    }
}
