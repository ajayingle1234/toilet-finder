package com.hardcastle.honeysuckervendor.Services;

/**
 * Created by hardcastle on 1/12/17.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hardcastle.honeysuckervendor.Activity.AssignDriverActivity;
import com.hardcastle.honeysuckervendor.Activity.Dashboard;
import com.hardcastle.honeysuckervendor.Fragment.DriverListForAssignServiceFragment;
import com.hardcastle.honeysuckervendor.R;
import com.hardcastle.honeysuckervendor.Utils.MyNotificationManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * Created by Sanket on 02/01/2017.
 */

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "VENDOR_FIREBASE";
    String message, title;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

        try {
            JSONObject json = new JSONObject(remoteMessage.getData().toString());
            message = json.getString("message");

        } catch (Exception e) {
            e.printStackTrace();
        }


        Log.d("msg", "onMessageReceived: " + remoteMessage.getData().get("message"));

        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                //sendPushNotification(json);
                createNotification(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

        //createNotification("message");

    }


    //this method will display the notification
    //We are passing the JSONObject that is received from
    //firebase cloud messaging
    private void sendPushNotification(JSONObject json) {
        //optionally we can display the json into log

        SharedPreferences sharedPreferences;

        Log.e(TAG, "Notification JSON " + json.toString());

        sharedPreferences = getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        String title = null;
        String message = null;
        //String imageUrl = null;
        String status = null;
        String service_request_id = null;

        try {
            JSONObject data = json.getJSONObject("data");

            Log.e("JsonData", data.toString());

            title = data.getString("title");
            message = data.getString("message");
            //imageUrl = data.getString("image");

            status = data.getJSONObject("payload").getString("STATUS");
            service_request_id = data.getJSONObject("payload").getString("SERVICE_REQUEST_ID");

            Log.e(TAG, "sendPushNotification: SERVICE_REQUEST_ID : " + service_request_id);

            //creating MyNotificationManager object
            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());

            // 0 - Reject, 1 - Pending, 2 - Accept, 3 - On going, 4 - Completed, 6 - Cancel by Driver, 7 - On the way, 8 - Canceled by User
            //creating an intent for the notification
            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("STATUS", status);
            intent.putExtra("SERVICE_REQUEST_ID", service_request_id);

            //if (imageUrl.equals("") || imageUrl.equalsIgnoreCase("null")) {
            //displaying small notification
            mNotificationManager.showBigNotification(title, message, "", intent);
            //mNotificationManager.showSmallNotification(title, message, intent);
            //} else {
            //if there is an image
            //displaying a big notification
            //mNotificationManager.showBigNotification(title, message, imageUrl, intent);
            //}

            SharedPreferences.Editor editor = sharedPreferences.edit();

            if (!service_request_id.equalsIgnoreCase("") && !service_request_id.equalsIgnoreCase(null)) {
                if (sharedPreferences.contains(getResources().getString(R.string.preference_assigned_userrequest))) {
                    Gson gson = new Gson();
                    String assignedUserRequestJson = sharedPreferences.getString(getResources().getString(R.string.preference_assigned_userrequest), null);

                    Log.e(TAG, " In sendPushNotification: ***********************");
                    Type type = new TypeToken<ArrayList<String>>() {
                    }.getType();
                    if (gson.fromJson(assignedUserRequestJson, type) != null) {

                        Log.e(TAG, " In sendPushNotification: shared prefernce check ***********************");
                        ArrayList<String> assignedUserReuest = gson.fromJson(assignedUserRequestJson, type);
                        if (assignedUserReuest.contains(service_request_id)) {
                            assignedUserReuest.remove(service_request_id);
                            assignedUserRequestJson = gson.toJson(assignedUserReuest);
                            Log.e(TAG, "sendPushNotification: Assigned User Request JSON : " + assignedUserReuest);
                            editor.putString(getResources().getString(R.string.preference_assigned_userrequest), assignedUserRequestJson);
                            editor.apply();
                            stopService(new Intent(MyFireBaseMessagingService.this, UserRequestAssignedCheckTimeService.class));
                            Log.e(TAG, "sendPushNotification: Remove service request id");
                        }

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void createNotification(JSONObject json) {

        String status = null;
        String service_request_id = null;
        SharedPreferences sharedPreferences = null;

        try {

            sharedPreferences = getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);

            JSONObject data = json.getJSONObject("data");

            Log.e("JsonData", data.toString());

            title = data.getString("title");
            message = data.getString("message");
            status = data.getJSONObject("payload").getString("STATUS");
            service_request_id = data.getJSONObject("payload").getString("SERVICE_REQUEST_ID");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "sendPushNotification: SERVICE_REQUEST_ID : " + service_request_id);

        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("STATUS", status);
        intent.putExtra("SERVICE_REQUEST_ID", service_request_id);
        PendingIntent resultIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(notificationSoundURI)
                .setContentIntent(resultIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, mNotificationBuilder.build());

        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (!service_request_id.equalsIgnoreCase("") && !service_request_id.equalsIgnoreCase(null)) {
            if (sharedPreferences.contains(getResources().getString(R.string.preference_assigned_userrequest))) {
                Gson gson = new Gson();
                String assignedUserRequestJson = sharedPreferences.getString(getResources().getString(R.string.preference_assigned_userrequest), null);

                Log.e(TAG, " In sendPushNotification: ***********************");
                Type type = new TypeToken<ArrayList<String>>() {}.getType();

                if (gson.fromJson(assignedUserRequestJson, type) != null) {

                    Log.e(TAG, " In sendPushNotification: shared prefernce check ***********************");
                    ArrayList<String> assignedUserReuest = gson.fromJson(assignedUserRequestJson, type);
                    if (assignedUserReuest.contains(service_request_id)) {
                        assignedUserReuest.remove(service_request_id);
                        assignedUserRequestJson = gson.toJson(assignedUserReuest);
                        Log.e(TAG, "sendPushNotification: Assigned User Request JSON : " + assignedUserReuest);
                        editor.putString(getResources().getString(R.string.preference_assigned_userrequest), assignedUserRequestJson);
                        editor.apply();
                        Intent broadcastIntent = new Intent();

                        if (sharedPreferences.getString(getResources().getString(R.string.preference_from), null).equalsIgnoreCase("ACTIVITY")) {
                            Log.e(TAG, "createNotification: FROM : "+sharedPreferences.getString(getResources().getString(R.string.preference_from), null));
                            broadcastIntent.setAction(AssignDriverActivity.mBroadcastStringAction);
                        } else if (sharedPreferences.getString(getResources().getString(R.string.preference_from), null).equalsIgnoreCase("FRAGMENT")){
                            Log.e(TAG, "createNotification: FROM : "+sharedPreferences.getString(getResources().getString(R.string.preference_from), null));
                            broadcastIntent.setAction(DriverListForAssignServiceFragment.mBroadcastStringAction);
                        }
                        sendBroadcast(broadcastIntent);

                        stopService(new Intent(MyFireBaseMessagingService.this, UserRequestAssignedCheckTimeService.class));
                        Log.e(TAG, "sendPushNotification: Remove service request id");
                    }

                }
            }
        }

    }

}

