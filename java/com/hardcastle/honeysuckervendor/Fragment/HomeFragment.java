package com.hardcastle.honeysuckervendor.Fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hardcastle.honeysuckervendor.Activity.ViewRequestedServiceUserInformation;
import com.hardcastle.honeysuckervendor.Adapter.AdapterFetchRequestOnDashboard;
import com.hardcastle.honeysuckervendor.Model.AvailableDriverDetails;
import com.hardcastle.honeysuckervendor.Model.UserRequest;
import com.hardcastle.honeysuckervendor.Model.VendorDetails;
import com.hardcastle.honeysuckervendor.R;
import com.hardcastle.honeysuckervendor.Receiver.ConnectivityReceiver;
import com.hardcastle.honeysuckervendor.Utils.APIInterface;
import com.hardcastle.honeysuckervendor.Utils.APIUtils;
import com.hardcastle.honeysuckervendor.Utils.GLOBAL;
import com.hardcastle.honeysuckervendor.Utils.MyCustomDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements AdapterFetchRequestOnDashboard.onUserClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = "HomeFragment";

    @BindView(R.id.tvUserListLabel) TextView mTvUserListLabel;
    @BindView(R.id.tvNoServiceAvailable) TextView mTvNoServiceAvailable;
    @BindView(R.id.recyclerViewRequest) RecyclerView mRecyclerViewRequest;

    private RecyclerView.Adapter mRVAdapter;
    private APIInterface mApiInterface;
    private GLOBAL mGlobalInstance;
    private SharedPreferences sharedPreferences;
    private ArrayList<UserRequest.DATum> mListUserRequest;
    private ArrayList<AvailableDriverDetails.DATum> mListAvailableDriverDetails;
    private VendorDetails vendorDetails;
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private AlertDialog enableNotificationListenerAlertDialog;
    private static int position;

    public HomeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        try {
            ButterKnife.bind(this,view);

            mRecyclerViewRequest.setHasFixedSize(true);

            mRecyclerViewRequest.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
            mApiInterface = APIUtils.getAPIInterface();
            mGlobalInstance = GLOBAL.getInstance();
            sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);

            Gson gson = new Gson();
            String json = sharedPreferences.getString(getResources().getString(R.string.preference_vendor_info), "");
            vendorDetails = gson.fromJson(json, VendorDetails.class);

            if(!isNotificationServiceEnabled()){
                enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
                enableNotificationListenerAlertDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            if (ConnectivityReceiver.isConnected()) {
                fetchRequest();
            } else {
                GLOBAL.showMessage(getContext(), getResources().getString(R.string.check_internet_connection));
            }
            mTvUserListLabel.setText(getResources().getString(R.string.user_list));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e(TAG, "onResume: ");
        // register connection status listener
        GLOBAL.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        if (isConnected) {
            GLOBAL.showMessage(getContext(), getResources().getString(R.string.internet_available));
        } else {
            GLOBAL.showMessage(getContext(), getResources().getString(R.string.check_internet_connection));
        }
    }

    @Override
    public void onUserRequestClick(View v, int pos, String buttonName) {

        try {
            Log.e(TAG, "onClick: You Click on "+(position+1)+" User request");

            if (buttonName.equalsIgnoreCase("ACCEPT")) {
                position = pos;
                showDialogBox(getResources().getString(R.string.confirmation), getResources().getString(R.string.confirm_acceptance_of_service), getResources().getString(R.string.yes), getResources().getString(R.string.no), "2");
            } else if (buttonName.equalsIgnoreCase("REJECT")) {
                position = pos;
                showDialogBox(getResources().getString(R.string.confirmation), getResources().getString(R.string.confirm_rejection_of_service), getResources().getString(R.string.yes), getResources().getString(R.string.no),"0");
            } else if (buttonName.equalsIgnoreCase("VIEW")) {
                Log.e(TAG, "onUserRequestClick: "+ mListUserRequest.get(pos).getINFO().getRATE().toString().trim());
                Intent intent = new Intent(getActivity(), ViewRequestedServiceUserInformation.class);
                intent.putExtra("OBJECT_INFO", mListUserRequest.get(pos).getINFO());
                intent.putExtra("OBJECT_SERVICE", mListUserRequest.get(pos).getSERVICE());
                intent.putExtra("VENDOR_ID", vendorDetails.getDATA().get(0).getID().toString().trim());
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void acceptRejectUserRequest(String vendorId, String userId, String serviceRequestId, final String action) {

        if (ConnectivityReceiver.isConnected()) {

            try {
                mGlobalInstance.hashmapKeyValue.clear();

                Log.e(TAG, "assignRejectUserRequest: VENDOR_ID : " + vendorId);
                Log.e(TAG, "assignRejectUserRequest: USER_ID : " + userId);
                Log.e(TAG, "assignRejectUserRequest: SERVICE_REQUEST_ID : " + serviceRequestId);
                Log.e(TAG, "assignRejectUserRequest: STATUS : " + action); // 2 - Accept ,   0 - Reject

                mGlobalInstance.hashmapKeyValue.put("VENDOR_ID", vendorId);
                mGlobalInstance.hashmapKeyValue.put("USER_ID", userId);
                mGlobalInstance.hashmapKeyValue.put("SERVICE_REQUEST_ID", serviceRequestId);
                mGlobalInstance.hashmapKeyValue.put("STATUS", action); // 2 - Accept ,   0 - Reject

                Call<AvailableDriverDetails> call = mApiInterface.acceptOrReject(mGlobalInstance.getInput(TAG, mGlobalInstance.hashmapKeyValue).toString());

                call.enqueue(new Callback<AvailableDriverDetails>() {
                    @Override
                    public void onResponse(Call<AvailableDriverDetails> call, Response<AvailableDriverDetails> response) {

                        try {
                            if (response.isSuccessful()) {

                                if (action.toString().trim().equalsIgnoreCase("2")) {
                                    fetchRequest();
                                } else {
                                    fetchRequest();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<AvailableDriverDetails> call, Throwable t) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            GLOBAL.showMessage(getContext(), getResources().getString(R.string.check_internet_connection));
        }

    }

    public void showDialogBox(String title, String alertMessage, String btnPositiveName, String btnNegativeName, final String action) {

        MyCustomDialog builder = new MyCustomDialog(getActivity(), title, alertMessage);
        final AlertDialog dialog = builder.setNegativeButton(btnNegativeName, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }

        }).setPositiveButton(btnPositiveName, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mRVAdapter.notifyItemRemoved(position);
                acceptRejectUserRequest(vendorDetails.getDATA().get(0).getID().toString().trim(), mListUserRequest.get(position).getINFO().getUSERID().toString().trim(), mListUserRequest.get(position).getINFO().getsERVICEREQUESTID().toString().trim(), action); // 2 means accept and  0 means reject
            }

        }).create();

        //2. now setup to change color of the button
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorOrange));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorOrange));
            }
        });

        dialog.show();
    }

    void fetchRequest() {

        try {
            mGlobalInstance.hashmapKeyValue.clear();

            Log.e(TAG, "fetchRequest: VENDOR_ID : "+ vendorDetails.getDATA().get(0).getID().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("VENDOR_ID", vendorDetails.getDATA().get(0).getID().toString().trim());

            Call<UserRequest> call = mApiInterface.fetchRequestOnDashboard(mGlobalInstance.getInput(TAG, mGlobalInstance.hashmapKeyValue).toString());

            call.enqueue(new Callback<UserRequest>() {
                @Override
                public void onResponse(Call<UserRequest> call, Response<UserRequest> response) {

                    if (response.isSuccessful()) {

                        try {

                            if (response.body().getSTATUS() == 1) {

                                if (response.body().getDATA().isEmpty()) {
                                    Log.e(TAG, "onResponse: No Service Available");
                                    mRecyclerViewRequest.setVisibility(View.GONE);
                                    mTvNoServiceAvailable.setVisibility(View.VISIBLE);
                                    mTvNoServiceAvailable.setText(getResources().getString(R.string.no_service_available));

                                } else {
                                    mRecyclerViewRequest.setVisibility(View.VISIBLE);
                                    mTvNoServiceAvailable.setVisibility(View.GONE);
                                    mListUserRequest = response.body().getDATA();
                                    Log.e(TAG, "onResponse: No. of Request :" + mListUserRequest.size());
                                    setAdapter(getContext(), mListUserRequest);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(Call<UserRequest> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setAdapter(Context context, ArrayList<UserRequest.DATum> datumList) {
        try {
            mRVAdapter = new AdapterFetchRequestOnDashboard(context, datumList, this);
            mRecyclerViewRequest.setAdapter(mRVAdapter);
            mRVAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isNotificationServiceEnabled(){
        try {
            String pkgName = getActivity().getPackageName();
            final String flat = Settings.Secure.getString(getActivity().getContentResolver(),
                    ENABLED_NOTIFICATION_LISTENERS);
            if (!TextUtils.isEmpty(flat)) {
                final String[] names = flat.split(":");
                for (int i = 0; i < names.length; i++) {
                    final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                    if (cn != null) {
                        if (TextUtils.equals(pkgName, cn.getPackageName())) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private AlertDialog buildNotificationServiceAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.notification_listener_service);
        alertDialogBuilder.setMessage(R.string.notification_listener_service_explanation);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    }
                });
        /*alertDialogBuilder.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // If you choose to not enable the notification listener
                        // the app. will not work as expected
                    }
                });*/
        return(alertDialogBuilder.create());
    }

}
