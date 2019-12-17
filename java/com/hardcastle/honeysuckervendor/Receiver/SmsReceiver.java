package com.hardcastle.honeysuckervendor.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.hardcastle.honeysuckervendor.Utils.SmsListener;


/**
 * Created by Ravi on 09/07/15.
 */
public class SmsReceiver extends BroadcastReceiver {
    private static SmsListener mListener;
    private static final String TAG = SmsReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = null;
        Object[] pdusObj = new Object[0];
        try {
            bundle = intent.getExtras();

            pdusObj = (Object[]) bundle.get("pdus");

            for(int i=0;i<pdusObj.length;i++){

                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);

                String sender = smsMessage.getDisplayOriginatingAddress();
                //You must check here if the sender is your provider and not another one with same text.

                String messageBody = smsMessage.getMessageBody();

                //Pass on the text to our listener.
                mListener.messageReceived(messageBody);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (bundle != null) {
                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    String senderAddress = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    Log.e(TAG, "Received SMS: " + message + ", Sender: " + senderAddress);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}
