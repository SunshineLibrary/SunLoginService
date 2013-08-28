package org.sunshinelibrary.SunLoginService.utils;

import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * @author Yanan Guo
 * @version 2.0
 */

public class HttpUtils {

    private static final String TAG = "HttpUtils";
    public static final int CHECK = 0;
    public static final int LOGIN = 1;

    /**
     * Helper method for fetching http response as String. Response status should be 2xx. Otherwise will return null;
     */
    public static String getResponse(HttpClient httpClient, HttpUriRequest request, int type) {
        Log.v(TAG, String.format("Starting Request: %s [%s]", request.getURI(), request.getMethod()));
        StringWriter writer = new StringWriter();
        String result = null;

        switch (type){

            case CHECK:
                try {
                    HttpResponse response = httpClient.execute(request);

                    int statusCode = response.getStatusLine().getStatusCode();

                    if (statusCode < 200 || statusCode > 401)
                        return null;

                    InputStreamReader reader = new InputStreamReader(response.getEntity().getContent());

                    IOUtils.copyCharacterStream(reader, writer);

                    result = writer.toString();

                } catch (Exception e) {
                    Log.e(TAG, String.format("Failed Request: %s [%s]", request.getURI(), request.getMethod()), e);
                }
                break;

            case LOGIN:
                try {
                    HttpResponse response = httpClient.execute(request);

                    int statusCode = response.getStatusLine().getStatusCode();

                    if (statusCode < 200 || statusCode > 300)
                        return null;

                    InputStreamReader reader = new InputStreamReader(response.getEntity().getContent());

                    IOUtils.copyCharacterStream(reader, writer);

                    result = writer.toString();
                } catch (IOException e) {
                    Log.e(TAG, String.format("Failed Request: %s [%s]", request.getURI(), request.getMethod()), e);
                }
                break;

            default:
                break;
        }
        return result;
    }

}
