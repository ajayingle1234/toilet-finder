package com.hardcastle.honeysuckervendor.Utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hardcastle.honeysuckervendor.R;

/**
 * Created by abhijeet on 2/8/2018.
 */

public class MyCustomDialog extends AlertDialog.Builder {

    public MyCustomDialog(Context context, String title, String message) {
        super(context);

        try {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View viewDialog = inflater.inflate(R.layout.dialog_simple, null, false);

            TextView titleTextView = (TextView)viewDialog.findViewById(R.id.title);
            titleTextView.setText(title);
            TextView messageTextView = (TextView)viewDialog.findViewById(R.id.message);
            messageTextView.setText(message);

            this.setCancelable(false);

            this.setView(viewDialog);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
