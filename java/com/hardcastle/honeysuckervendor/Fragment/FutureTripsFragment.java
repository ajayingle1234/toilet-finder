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

import com.google.gson.Gson;
import com.hardcastle.honeysuckervendor.Activity.ViewRequestedServiceUserInformation;
import com.hardcastle.honeysuckervendor.Adapter.AdapterFetchFutureTrips;
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

/*
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FutureTripsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FutureTripsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FutureTripsFragment extends Fragment implements AdapterFetchFutureTrips.onUserClickListener,  ConnectivityReceiver.ConnectivityReceiverListener{

    private static final String TAG = "FutureTripsFragment";

    @BindView(R.id.recyclerViewFutureTrip) RecyclerView mRecyclerViewRequest;
    @BindView(R.id.tvNoServiceAvailable) TextView mTvNoServiceAvailable;

    private RecyclerView.Adapter mRVAdapter;
    private APIInterface mApiInterface;
    private GLOBAL mGlobalInstance;
    private SharedPreferences sharedPreferences;
    private ArrayList<UserRequest.DATum> mListUserRequest;
    private ArrayList<AvailableDriverDetails.DATum> mListAvailableDriverDetails;
    private VendorDetails vendorDetails;
    private static int position;

    //private OnFragmentInteractionListener mListener;

    public FutureTripsFragment() {
        // Required empty public constructor
    }

    public static FutureTripsFragment newInstance() {
        FutureTripsFragment fragment = new FutureTripsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_future_trips, container, false);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (ConnectivityReceiver.isConnected()) {
            fetchRequest();
        } else {
            GLOBAL.showMessage(getContext(), getResources().getString(R.string.check_internet_connection));
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
    public void onUserRequestClick(View v, int pos, String buttonText) {

        try {

            if (buttonText.equalsIgnoreCase("VIEW")) {
                Log.e(TAG, "onUserRequestClick: CLICK ON FUTURE VIEW");
                Log.e(TAG, "onUserRequestClick: "+ mListUserRequest.get(pos).getINFO().getsERVICEREQUESTID().toString().trim());
                Intent intent = new Intent(getActivity(), ViewRequestedServiceUserInformation.class);
                intent.putExtra("OBJECT_INFO", mListUserRequest.get(pos).getINFO());
                intent.putExtra("OBJECT_SERVICE", mListUserRequest.get(pos).getSERVICE());
                intent.putExtra("VENDOR_ID", vendorDetails.getDATA().get(0).getID().toString().trim());
                startActivity(intent);
            } else if (buttonText.equalsIgnoreCase("CANCEL")) {
                position = pos;
                Log.e(TAG, "onUserRequestClick: CLICK ON FUTURE CANCEL");
                Log.e(TAG, "onUserRequestClick: "+ mListUserRequest.get(pos).getINFO().getsERVICEREQUESTID().toString().trim());
                showDialogBox(getResources().getString(R.string.confirmation), getResources().getString(R.string.confirm_cancel_of_service), getResources().getString(R.string.yes), getResources().getString(R.string.no),"11"); // 11 - vendor cancel
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void fetchRequest() {

        try {
            mGlobalInstance.hashmapKeyValue.clear();

            Log.e(TAG, "fetchRequest: VENDOR_ID : "+ vendorDetails.getDATA().get(0).getID().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("VENDOR_ID", vendorDetails.getDATA().get(0).getID().toString().trim());

            Call<UserRequest> call = mApiInterface.fetchRequestForFutureTrips(mGlobalInstance.getInput(TAG, mGlobalInstance.hashmapKeyValue).toString());

            call.enqueue(new Callback<UserRequest>() {
                @Override
                public void onResponse(Call<UserRequest> call, Response<UserRequest> response) {

                    try {
                        if (response.isSuccessful()) {

                            mListUserRequest = response.body().getDATA();

                            if (response.body().getDATA().isEmpty()) {
                                Log.e(TAG, "onResponse: No Service Available");
                                mRecyclerViewRequest.setVisibility(View.GONE);
                                mTvNoServiceAvailable.setVisibility(View.VISIBLE);
                                mTvNoServiceAvailable.setText(getResources().getString(R.string.no_service_available));
                            } else {
                                Log.e(TAG, "onResponse: No. of Request :"+ mListUserRequest.size());
                                mRecyclerViewRequest.setVisibility(View.VISIBLE);
                                mTvNoServiceAvailable.setVisibility(View.GONE);
                                setAdapter(getContext(), mListUserRequest);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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
                cancelUserRequest(vendorDetails.getDATA().get(0).getID().toString().trim(), mListUserRequest.get(position).getINFO().getUSERID().toString().trim(), mListUserRequest.get(position).getINFO().getsERVICEREQUESTID().toString().trim(), action);
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

    void cancelUserRequest(String vendorId, String userId, String serviceRequestId, final String action) {

        if (ConnectivityReceiver.isConnected()) {

            try {
                mGlobalInstance.hashmapKeyValue.clear();

                Log.e(TAG, "assignRejectUserRequest: VENDOR_ID : " + vendorId);
                Log.e(TAG, "assignRejectUserRequest: USER_ID : " + userId);
                Log.e(TAG, "assignRejectUserRequest: SERVICE_REQUEST_ID : " + serviceRequestId);
                Log.e(TAG, "assignRejectUserRequest: STATUS : " + action); // 10 - Vendor Cancel

                mGlobalInstance.hashmapKeyValue.put("VENDOR_ID", vendorId);
                mGlobalInstance.hashmapKeyValue.put("USER_ID", userId);
                mGlobalInstance.hashmapKeyValue.put("SERVICE_REQUEST_ID", serviceRequestId);
                mGlobalInstance.hashmapKeyValue.put("STATUS", action); // 10 - Vendor Cancel

                Call<String> call = mApiInterface.cancelService(mGlobalInstance.getInput(TAG, mGlobalInstance.hashmapKeyValue).toString());

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        try {
                            if (response.isSuccessful()) {

                                if (response.body().contains("SUCCESS")) {
                                    fetchRequest();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }

                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            GLOBAL.showMessage(getContext(), getResources().getString(R.string.check_internet_connection));
        }

    }

    private void setAdapter(Context context, ArrayList<UserRequest.DATum> datumList) {
        try {
            mRVAdapter = new AdapterFetchFutureTrips(context, datumList, this);
            mRecyclerViewRequest.setAdapter(mRVAdapter);
            mRVAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        if (isConnected) {
            GLOBAL.showMessage(getContext(), getResources().getString(R.string.internet_available));
        } else {
            GLOBAL.showMessage(getContext(), getResources().getString(R.string.check_internet_connection));
        }
    }

}
