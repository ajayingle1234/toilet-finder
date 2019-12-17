package com.hardcastle.honeysuckervendor.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.hardcastle.honeysuckervendor.R;

/**
 * Created by abhijeet on 11/17/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseInstanceService";
    private SharedPreferences sharedPreferences;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        try {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.e(TAG, "onTokenRefresh: "+refreshedToken);
            sharedPreferences = getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getResources().getString(R.string.preference_device_token), refreshedToken.toString().trim());
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
