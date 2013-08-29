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

    private ArrayList<String> mArrayListOrigin;
    private ArrayList<String> mArrayListNow;
    private Context mContext;
    private static final String TAG = "WhiteListRelated";
    private static final String WHITE_LIST = "org.sunshinelibrary.launcher.WHITE_LIST_CHANGED";

    public WhiteList(JSONArray arrayOrigin, JSONArray arrayNow, Context context){
        mArrayListOrigin = getOrderedPackages(arrayOrigin);
        mArrayListNow = getOrderedPackages(arrayNow);
        mContext = context;
    }

    public boolean isChanged(){
        boolean changed = (mArrayListNow.size() != mArrayListOrigin.size());

        if(!changed)
            changed = mArrayListNow.containsAll(mArrayListOrigin);
        return changed;
    }

    public ArrayList<String> getOrderedPackages(JSONArray array){

        ArrayList<String> list = new ArrayList<String>();
        if (array == null) {
            return list;
        }

        for(int i=0;i<array.length();++i){
            try {
                String s = array.getString(i);
                if (!list.contains(s)){
                    list.add(s);
                }
            } catch (JSONException e) {
                Log.e(TAG,"failed to get package name from JsonArray");
            }
        }

        Collections.sort(list,APP_NAME_COMPARATOR);
        Log.i(TAG, "ordered list"+list);

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
        Intent intent  = new Intent(WHITE_LIST);

        ArrayList<String> remove = new ArrayList<String>();
        remove.addAll(mArrayListOrigin);
        remove.removeAll(mArrayListNow);

        ArrayList<String> add = new ArrayList<String>();
        add.addAll(mArrayListNow);
        add.removeAll(mArrayListOrigin);


        intent.putStringArrayListExtra("remove",remove);
        intent.putStringArrayListExtra("add",add);
        mContext.sendBroadcast(intent);
    }

}