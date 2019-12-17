package com.hardcastle.honeysuckervendor.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hardcastle.honeysuckervendor.Activity.AssignDriverActivity;
import com.hardcastle.honeysuckervendor.Activity.ViewRequestedServiceUserInformation;
import com.hardcastle.honeysuckervendor.Adapter.AdapterFetchTodaysTrips;
import com.hardcastle.honeysuckervendor.Model.AvailableDriverDetails;
import com.hardcastle.honeysuckervendor.Model.UserRequest;
import com.hardcastle.honeysuckervendor.Model.VendorDetails;
import com.hardcastle.honeysuckervendor.R;
import com.hardcastle.honeysuckervendor.Receiver.ConnectivityReceiver;
import com.hardcastle.honeysuckervendor.Utils.APIInterface;
import com.hardcastle.honeysuckervendor.Utils.APIUtils;
import com.hardcastle.honeysuckervendor.Utils.GLOBAL;
import com.hardcastle.honeysuckervendor.Utils.MyCustomDialog;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodaysTripFragment extends Fragment implements AdapterFetchTodaysTrips.onUserClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = "TodaysTripFragment";

    @BindView(R.id.recyclerViewTodaysTrip) RecyclerView mRecyclerViewRequest;
    @BindView(R.id.tvNoServiceAvailable) TextView mTvNoServiceAvailable;

    private RecyclerView.Adapter mRVAdapter;
    private APIInterface mApiInterface;
    private GLOBAL mGlobalInstance;
    private SharedPreferences sharedPreferences;
    private ArrayList<UserRequest.DATum> mListUserRequest;
    private ArrayList<AvailableDriverDetails.DATum> mListAvailableDriverDetails;
    private VendorDetails vendorDetails;
    private static int position;

    public TodaysTripFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TodaysTripFragment newInstance() {
        TodaysTripFragment fragment = new TodaysTripFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        Log.e(TAG, "onCreate: ****************************");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_todays_trip, container, false);
        try {
            ButterKnife.bind(this, view);

            mRecyclerViewRequest.setHasFixedSize(true);

            mRecyclerViewRequest.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
            mApiInterface = APIUtils.getAPIInterface();
            mGlobalInstance = GLOBAL.getInstance();
            sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);

            mListUserRequest = new ArrayList<UserRequest.DATum>();
            Gson gson = new Gson();
            String json = sharedPreferences.getString(getResources().getString(R.string.preference_vendor_info), "");
            vendorDetails = gson.fromJson(json, VendorDetails.class);

            Log.e(TAG, "onCreateView: ****************************");
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
                Log.e(TAG, "onStart: **************************" );
                fetchRequest();
            } else {
                GLOBAL.showMessage(getContext(), getResources().getString(R.string.check_internet_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e(TAG, "onResume: ***************************");
        // register connection status listener
        GLOBAL.getInstance().setConnectivityListener(this);
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
            Log.e(TAG, "onClick: You Click on " + (pos + 1) + " User request");
            Gson gson = new Gson();
            String assignedUserReuestJson = sharedPreferences.getString(getResources().getString(R.string.preference_assigned_userrequest), null);
            position = pos;
            if (buttonName.equalsIgnoreCase("ASSIGN")) {

                Log.e(TAG, "onUserRequestClick: Click On Assign");
                Type type = new TypeToken<ArrayList<String>>() {}.getType();

                if (gson.fromJson(assignedUserReuestJson, type) != null) {

                    Log.e(TAG, "onUserRequestClick: Shared preference is not null");
                    ArrayList<String> assignedUserReuest = gson.fromJson(assignedUserReuestJson, type);
                    Log.e(TAG, "onUserRequestClick: ALREADY ASSIGNED SERVICE SIZE : " + assignedUserReuest.size());

                    if (assignedUserReuest.contains(mListUserRequest.get(position).getINFO().getsERVICEREQUESTID().toString().trim())) {
                        Toast.makeText(getActivity(), "Already assigned", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onUserRequestClick: Already assigned");
                    } else {
                        assignRejectUserRequest(vendorDetails.getDATA().get(0).getID().toString().trim(), mListUserRequest.get(pos).getINFO().getUSERID().toString().trim(), mListUserRequest.get(pos).getINFO().getsERVICEREQUESTID().toString().trim(), "2", pos); // 2 means accept
                    }

                } else {

                    Log.e(TAG, "onUserRequestClick: Shared preference is null");
                    assignRejectUserRequest(vendorDetails.getDATA().get(0).getID().toString().trim(), mListUserRequest.get(position).getINFO().getUSERID().toString().trim(), mListUserRequest.get(position).getINFO().getsERVICEREQUESTID().toString().trim(), "2", position); // 2 means accept

                }

            } else if (buttonName.equalsIgnoreCase("REJECT")) {
                Log.e(TAG, "onUserRequestClick: REJECT");
                //position = pos;
                showDialogBox(getResources().getString(R.string.confirmation), getResources().getString(R.string.confirm_rejection_of_service), getResources().getString(R.string.yes), getResources().getString(R.string.no),"0");
                //assignRejectUserRequest(vendorDetails.getDATA().get(0).getID().toString().trim(), mListUserRequest.get(position).getINFO().getUSERID().toString().trim(), mListUserRequest.get(position).getINFO().getsERVICEREQUESTID().toString().trim(), "0", position); // 0 means reject

            } else if (buttonName.equalsIgnoreCase("VIEW")) {
                Log.e(TAG, "onUserRequestClick: " + mListUserRequest.get(position).getINFO().getRATE().toString().trim());
                Intent intent = new Intent(getActivity(), ViewRequestedServiceUserInformation.class);
                intent.putExtra("OBJECT_INFO", mListUserRequest.get(position).getINFO());
                intent.putExtra("OBJECT_SERVICE", mListUserRequest.get(position).getSERVICE());
                intent.putExtra("VENDOR_ID", vendorDetails.getDATA().get(0).getID().toString().trim());
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                assignRejectUserRequest(vendorDetails.getDATA().get(0).getID().toString().trim(), mListUserRequest.get(position).getINFO().getUSERID().toString().trim(), mListUserRequest.get(position).getINFO().getsERVICEREQUESTID().toString().trim(), "0", position); // 0 means reject

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

    void assignRejectUserRequest(String vendorId, String userId, String serviceRequestId, final String action, final int position) {

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
                mGlobalInstance.hashmapKeyValue.put("STATUS", action);

                Call<AvailableDriverDetails> call = mApiInterface.getAvailableDriverDetailsToAssignUserRequest(mGlobalInstance.getInput(TAG, mGlobalInstance.hashmapKeyValue).toString());

                call.enqueue(new Callback<AvailableDriverDetails>() {
                    @Override
                    public void onResponse(Call<AvailableDriverDetails> call, Response<AvailableDriverDetails> response) {

                        if (response.isSuccessful()) {

                            Log.e(TAG, "onResponse: ASSIGN OR REJECT");
                            if (response.body().getMESSAGE().equalsIgnoreCase("SUCCESS")) {

                                if (action.toString().trim().equalsIgnoreCase("2")) {
                                    mListAvailableDriverDetails = response.body().getDATA();
                                    Log.e(TAG, "onResponse: No. of Available Driver :" + mListAvailableDriverDetails.size());
                                    Intent intentAssignDriver = new Intent(getActivity(), AssignDriverActivity.class);
                                    intentAssignDriver.putExtra("OBJECT_INFO", mListUserRequest.get(position).getINFO());
                                    intentAssignDriver.putExtra("OBJECT_SERVICE", mListUserRequest.get(position).getSERVICE());
                                    intentAssignDriver.putParcelableArrayListExtra("AVAILABLE_DRIVERS", mListAvailableDriverDetails);
                                    startActivity(intentAssignDriver);
                                } else {
                                    Log.e(TAG, "onResponse: JSON : " + response.body().toString());
                                    Log.e(TAG, "onResponse: REJECT");
                                    fetchRequest();
                                }
                            } else {
                                Log.e(TAG, "onResponse: FAILURE");
                            }

                        } else {

                            Log.e(TAG, "onResponse: NOT SUCCESSFULL");
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

    void fetchRequest() {

        try {
            Log.e(TAG, "fetchRequest: ***************************");
            mGlobalInstance.hashmapKeyValue.clear();
            mListUserRequest.clear();

            Log.e(TAG, "fetchRequest: VENDOR_ID : " + vendorDetails.getDATA().get(0).getID().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("VENDOR_ID", vendorDetails.getDATA().get(0).getID().toString().trim());

            Call<UserRequest> call = mApiInterface.fetchRequestForTodaysTrips(mGlobalInstance.getInput(TAG, mGlobalInstance.hashmapKeyValue).toString());

            call.enqueue(new Callback<UserRequest>() {
                @Override
                public void onResponse(Call<UserRequest> call, Response<UserRequest> response) {

                    Log.e(TAG, "In onResponse: ");

                        if (response.isSuccessful()) {

                            Log.e(TAG, "onResponse: IsSuccessfull" );
                            if (response.body().getMESSAGE().equalsIgnoreCase("SUCCESS")) {

                                Log.e(TAG, "onResponse: Success");

                                if (response.body().getDATA().isEmpty()) {
                                    Log.e(TAG, "onResponse: No Service Available");
                                    mRecyclerViewRequest.setVisibility(View.GONE);
                                    mTvNoServiceAvailable.setVisibility(View.VISIBLE);
                                    mTvNoServiceAvailable.setText(getResources().getString(R.string.no_service_available));

                                } else {

                                    Log.e(TAG, "onResponse: response data is not empty");
                                    mListUserRequest = response.body().getDATA();

                                    /*Gson gson1 = new Gson();
                                    String todaysTripRequest = sharedPreferences.getString(getResources().getString(R.string.preference_todays_trip), null);

                                    SharedPreferences.Editor editor = sharedPreferences.edit();

                                    ArrayList<ArrayList<UserRequest.DATum>> listtodaysTripRequest = new ArrayList<>();

                                    Type type = new TypeToken<ArrayList<UserRequest.DATum>>() {}.getType();

                                    Gson gson = new Gson();
                                    String todaysTripRequestJson1;

                                    if (gson1.fromJson(todaysTripRequest, type) == null) {

                                        listtodaysTripRequest.add(mListUserRequest);
                                        todaysTripRequestJson1 = gson.toJson(listtodaysTripRequest);
                                        Log.e(TAG, "onResponse: Assigned User Request JSON : " + todaysTripRequestJson1);
                                        editor.putString(getResources().getString(R.string.preference_assigned_userrequest), todaysTripRequestJson1);
                                        editor.apply();

                                    }*/

                                    /*SharedPreferences.Editor editor = sharedPreferences.edit();

                                    if (sharedPreferences.contains(getResources().getString(R.string.preference_assigned_userrequest)))
                                    {
                                        Gson gson = new Gson();
                                        String assignedUserRequestJson = sharedPreferences.getString(getResources().getString(R.string.preference_assigned_userrequest), null);

                                        Log.e(TAG, "onResponse: No. of Request :" + mListUserRequest.size());

                                        Type type = new TypeToken<ArrayList<String>>() {}.getType();

                                        if (gson.fromJson(assignedUserRequestJson, type) != null) {

                                            ArrayList<String> assignedUserReuest = gson.fromJson(assignedUserRequestJson, type);

                                            Log.e(TAG, "fetchRequest: ALREADY ASSIGNED SERVICE SIZE : " + assignedUserReuest.size());
                                            if (assignedUserReuest.size() == 50) {

                                                editor.remove(getResources().getString(R.string.preference_assigned_userrequest));
                                                editor.apply();
                                            }

                                        }

                                    } else {
                                        editor.putString(getResources().getString(R.string.preference_assigned_userrequest), null);
                                        editor.apply();
                                    }*/

                                    mRecyclerViewRequest.setVisibility(View.VISIBLE);
                                    mTvNoServiceAvailable.setVisibility(View.GONE);
                                    setAdapter(getContext(), mListUserRequest);
                                }
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
            mRVAdapter = new AdapterFetchTodaysTrips(context, datumList, this);
            mRecyclerViewRequest.setAdapter(mRVAdapter);
            mRVAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
