package com.hardcastle.honeysuckervendor.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hardcastle.honeysuckervendor.Activity.AssignDriverActivity;
import com.hardcastle.honeysuckervendor.Activity.Dashboard;
import com.hardcastle.honeysuckervendor.Fragment.DriverListForAssignServiceFragment;
import com.hardcastle.honeysuckervendor.R;
import com.hardcastle.honeysuckervendor.Utils.APIInterface;
import com.hardcastle.honeysuckervendor.Utils.APIUtils;
import com.hardcastle.honeysuckervendor.Utils.GLOBAL;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by abhijeet on 3/6/2018.
 */

public class UserRequestAssignedCheckTimeService extends Service {

    private static final String TAG = "CheckTimeSERVICE";
    private APIInterface mApiInterface;
    private GLOBAL mGlobalInstance;
    private SharedPreferences sharedPreferences;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private int currentNotificationID = 0;
    private String from;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            mApiInterface = APIUtils.getAPIInterface();
            mGlobalInstance = GLOBAL.getInstance();
            sharedPreferences = getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            from = intent.getStringExtra("FROM").toString().trim();
            Log.e(TAG, "onStartCommand: From : "+from);
            editor.putString(getResources().getString(R.string.preference_from), from);
            editor.apply();
            startTrackingTimer(intent.getStringExtra("VENDOR_ID").toString().trim(), intent.getStringExtra("USER_ID").toString().trim(), intent.getStringExtra("DRIVER_ID").toString().trim(), intent.getStringExtra("SERVICE_REQUEST_ID").toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void startTrackingTimer(final String vendorID, final String userID, final String driverID, final String serviceRequestID) {

        Log.e(TAG, "startTrackingTimer: ");
        final long period = 420000;
        final Timer timerTrackDriver;
        try {
            timerTrackDriver = new Timer();
            timerTrackDriver.schedule(new TimerTask() {
                @Override
                public void run() {
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    if (!serviceRequestID.equalsIgnoreCase("") && !serviceRequestID.equalsIgnoreCase(null)) {
                        if (sharedPreferences.contains(getResources().getString(R.string.preference_assigned_userrequest))) {

                            Gson gson = new Gson();
                            String assignedUserRequestJson = sharedPreferences.getString(getResources().getString(R.string.preference_assigned_userrequest), null);

                            Type type = new TypeToken<ArrayList<String>>() {}.getType();
                            if (gson.fromJson(assignedUserRequestJson, type) != null) {

                                Log.e(TAG, " In startTrackingTimer: shared prefernce check ***********************");
                                ArrayList<String> assignedUserReuest = gson.fromJson(assignedUserRequestJson, type);
                                if (assignedUserReuest.contains(serviceRequestID)) {
                                    assignedUserReuest.remove(serviceRequestID);
                                    assignedUserRequestJson = gson.toJson(assignedUserReuest);
                                    Log.e(TAG, "sendPushNotification: Assigned User Request JSON : " + assignedUserReuest);
                                    editor.putString(getResources().getString(R.string.preference_assigned_userrequest), assignedUserRequestJson);
                                    editor.apply();
                                    Log.e("TESTING", "run: " + String.valueOf(period));
                                    Intent broadcastIntent = new Intent();

                                    if (from.equalsIgnoreCase("ACTIVITY")) {
                                        Log.e(TAG, "run: FROM : "+from);
                                        broadcastIntent.setAction(AssignDriverActivity.mBroadcastStringAction);
                                    } else if (from.equalsIgnoreCase("FRAGMENT")){
                                        Log.e(TAG, "run: FROM : "+from);
                                        broadcastIntent.setAction(DriverListForAssignServiceFragment.mBroadcastStringAction);
                                    }
                                    sendBroadcast(broadcastIntent);
                                    timerTrackDriver.cancel();
                                    driverNotRespondingToService(vendorID, userID, driverID, serviceRequestID);
                                }
                            }
                        }
                    }

                }
            }, period, period);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void driverNotRespondingToService(String vendorID, String userID, String driverID, String serviceRequestID) {

        try {
            Log.e(TAG, "driverNotRespondingToService, VENDOR_ID : " + vendorID.toString().trim());
            Log.e(TAG, "driverNotRespondingToService, USER_ID : " + userID.toString().trim());
            Log.e(TAG, "driverNotRespondingToService, DRIVER_ID : " + driverID.toString().trim());
            Log.e(TAG, "driverNotRespondingToService, SERVICE_REQUEST_ID : " + serviceRequestID.toString().trim());

            mGlobalInstance.hashmapKeyValue.clear();

            mGlobalInstance.hashmapKeyValue.put("VENDOR_ID", vendorID.toString().trim());
            mGlobalInstance.hashmapKeyValue.put("USER_ID", userID.toString().trim());
            mGlobalInstance.hashmapKeyValue.put("DRIVER_ID", driverID.toString().trim());
            mGlobalInstance.hashmapKeyValue.put("SERVICE_REQUEST_ID", serviceRequestID.toString().trim());
            mGlobalInstance.hashmapKeyValue.put("STATUS", "9");


            Call<String> call = mApiInterface.driverNotResponding(mGlobalInstance.getInput(TAG, mGlobalInstance.hashmapKeyValue).toString());

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    Log.e(TAG, "onResponse: ");

                    if (response.isSuccessful()) {

                        Log.e(TAG, "onResponse: Successfull");

                        try {
                            JSONObject jsonObject = new JSONObject(response.body());

                            if (jsonObject.getString("MESSAGE").equalsIgnoreCase("SUCCESS")) {

                                Log.e(TAG, "onResponse: SUCCESS ");

                            } else {
                                Log.e(TAG, "onResponse: Something went wrong");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else {
                        Log.e(TAG, "onResponse: Not Successfull");
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setDataForSimpleNotification(String serviceRequestID) {

        Log.e(TAG, "setDataForSimpleNotification: ");
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                .setContentTitle("TITLE")
                .setContentText(serviceRequestID);
        sendNotification();
    }

    private void sendNotification() {

        try {
            Log.e(TAG, "sendNotification: ");
            Intent notificationIntent = new Intent(this, Dashboard.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            notificationBuilder.setContentIntent(contentIntent);
            Notification notification = notificationBuilder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.defaults |= Notification.DEFAULT_SOUND;

            currentNotificationID++;
            int notificationId = currentNotificationID;
            if (notificationId == Integer.MAX_VALUE - 1)
                notificationId = 0;

            notificationManager.notify(notificationId, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
