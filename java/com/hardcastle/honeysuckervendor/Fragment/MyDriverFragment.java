package com.hardcastle.honeysuckervendor.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
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
import com.google.gson.JsonSyntaxException;
import com.hardcastle.honeysuckervendor.Activity.AddModifyDriver;
import com.hardcastle.honeysuckervendor.Adapter.AdapterFetchDriverProfileOnDashboard;
import com.hardcastle.honeysuckervendor.Model.DriverProfile;
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
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyDriverFragment extends Fragment implements AdapterFetchDriverProfileOnDashboard.onDriverClickListener, ConnectivityReceiver.ConnectivityReceiverListener{

    private static final String TAG = "MyDriverFragment";
    private static final int ADD = 1;
    private static final int MODIFY = 2;

    @BindView(R.id.tvMyDriverLabel) TextView mTvMyDriverLabel;
    @BindView(R.id.tvAddDriver) TextView mTvAddDriver;
    @BindView(R.id.tvNoDriverAdded) TextView mTvNoDriverAdded;
    @BindView(R.id.recyclerViewFetchDriverProfile) RecyclerView mRecyclerViewFetchDriverProfile;

    private RecyclerView.Adapter mRVAdapter;
    private APIInterface mApiInterface;
    private GLOBAL mGlobalInstance;
    private SharedPreferences sharedPreferences;
    private ArrayList<DriverProfile.DATum> mListDriverProfile;
    private static String mDriverID;

    public MyDriverFragment() {
        // Required empty public constructor
    }

    public static MyDriverFragment newInstance() {
        MyDriverFragment fragment = new MyDriverFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_driver, container, false);
        try {
            ButterKnife.bind(this,view);

            mRecyclerViewFetchDriverProfile.setHasFixedSize(true);

            mRecyclerViewFetchDriverProfile.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
            mApiInterface = APIUtils.getAPIInterface();
            mGlobalInstance = GLOBAL.getInstance();
            sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
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
                fetchDriverProfile();
            } else {
                GLOBAL.showMessage(getContext(), getResources().getString(R.string.check_internet_connection));
            }

            mTvMyDriverLabel.setText(getResources().getString(R.string.title_my_drivers));
            mTvAddDriver.setText(getResources().getString(R.string.add_driver));
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

    void fetchDriverProfile() {

        try {
            mGlobalInstance.hashmapKeyValue.clear();

            Gson gson = new Gson();
            String json = sharedPreferences.getString(getResources().getString(R.string.preference_vendor_info), "");
            VendorDetails vendorDetails = gson.fromJson(json, VendorDetails.class);

            Log.e(TAG, "fetchRequest: VENDOR_ID : "+ vendorDetails.getDATA().get(0).getID().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("VENDOR_ID", vendorDetails.getDATA().get(0).getID().toString().trim());

            Call<DriverProfile> call = mApiInterface.fetchDriverProfile(mGlobalInstance.getInput(TAG, mGlobalInstance.hashmapKeyValue).toString());

            call.enqueue(new Callback<DriverProfile>() {
                @Override
                public void onResponse(Call<DriverProfile> call, Response<DriverProfile> response) {

                    try {
                        if (response.isSuccessful()) {

                            if (response.body().getSTATUS() == 1) {

                                if (response.body().getDATA().isEmpty()) {

                                    Log.e(TAG, "onResponse: No Service Available");
                                    mRecyclerViewFetchDriverProfile.setVisibility(View.GONE);
                                    mTvNoDriverAdded.setVisibility(View.VISIBLE);
                                    mTvNoDriverAdded.setText(getResources().getString(R.string.no_driver_added));
                                } else {
                                    mRecyclerViewFetchDriverProfile.setVisibility(View.VISIBLE);
                                    mTvNoDriverAdded.setVisibility(View.GONE);
                                    mListDriverProfile = response.body().getDATA();
                                    Log.e(TAG, "onResponse: No. of Request :" + mListDriverProfile.size());
                                    setAdapter(getContext(), mListDriverProfile);
                                }

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<DriverProfile> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void deleteDriver(String driverId) {

        mGlobalInstance.hashmapKeyValue.clear();

        try {
            Gson gson = new Gson();
            String json = sharedPreferences.getString(getResources().getString(R.string.preference_vendor_info), "");
            VendorDetails vendorDetails = gson.fromJson(json, VendorDetails.class);

            Log.e(TAG, "deleteDriver: VENDOR_ID : "+ vendorDetails.getDATA().get(0).getID().toString().trim());
            Log.e(TAG, "deleteDriver: DRIVER_ID : "+driverId);

            mGlobalInstance.hashmapKeyValue.put("CONDITION", "DEL");
            mGlobalInstance.hashmapKeyValue.put("VENDOR_ID", vendorDetails.getDATA().get(0).getID().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("DRIVER_ID", driverId);

            Call<String> call = mApiInterface.deleteAndUpdateDriver(mGlobalInstance.getInput(TAG, mGlobalInstance.hashmapKeyValue).toString());

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if (response.isSuccessful()) {
                        Log.e(TAG, "onResponse: Driver Deleted Successfully");
                        fetchDriverProfile();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAdapter(Context context, ArrayList<DriverProfile.DATum> datumList) {

        try {
            mRVAdapter = new AdapterFetchDriverProfileOnDashboard(context, datumList,this);
            mRecyclerViewFetchDriverProfile.setAdapter(mRVAdapter);
            mRVAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDialogBox(String title, String alertMessage, String btnPositiveName, String btnNegativeName) {

        MyCustomDialog builder = new MyCustomDialog(getActivity(), title, alertMessage);
        final AlertDialog dialog = builder.setNegativeButton(btnNegativeName, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }

        }).setPositiveButton(btnPositiveName, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteDriver(mDriverID);
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

    @Override
    public void onDriverClickToDelete(View v, String driverId) {

        try {
            Log.e(TAG, "onDriverClickToDelete: delete driver Driver ID : "+driverId);

            if (ConnectivityReceiver.isConnected()) {
                mDriverID = driverId;
                showDialogBox(getResources().getString(R.string.confirmation), getResources().getString(R.string.confirm_delete_driver), getResources().getString(R.string.yes), getResources().getString(R.string.no));
                //deleteDriver(driverId);
            } else {
                GLOBAL.showMessage(getContext(), getResources().getString(R.string.check_internet_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDriverClickToUpdate(View v, int position) {

        try {
            Intent intentAddDriver = new Intent(getActivity(), AddModifyDriver.class);
            intentAddDriver.putExtra("KEY", "MOD");
            intentAddDriver.putExtra("OBJECT", mListDriverProfile.get(position));
            startActivityForResult(intentAddDriver,MODIFY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDriverClickToCall(View v, String mobileNo) {

        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel: "+mobileNo));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.tvAddDriver)
    void addDriver() {
        try {
            Intent intentAddDriver = new Intent(getActivity(), AddModifyDriver.class);
            intentAddDriver.putExtra("KEY", "ADD");
            intentAddDriver.putExtra("OBJECT", "");
            startActivityForResult(intentAddDriver,ADD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            Log.e(TAG, "onActivityResult: ");
            fetchDriverProfile();
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
