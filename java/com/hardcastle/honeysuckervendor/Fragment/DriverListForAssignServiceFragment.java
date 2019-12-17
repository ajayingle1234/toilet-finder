package com.hardcastle.honeysuckervendor.Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.hardcastle.honeysuckervendor.Adapter.AdapterFetchDriverListForAssignService;
import com.hardcastle.honeysuckervendor.Model.AssignedUserRequest;
import com.hardcastle.honeysuckervendor.Model.AvailableDriverDetails;
import com.hardcastle.honeysuckervendor.Model.LatLongClass;
import com.hardcastle.honeysuckervendor.Model.UserRequest;
import com.hardcastle.honeysuckervendor.Model.VendorDetails;
import com.hardcastle.honeysuckervendor.R;
import com.hardcastle.honeysuckervendor.Receiver.ConnectivityReceiver;
import com.hardcastle.honeysuckervendor.Services.UserRequestAssignedCheckTimeService;
import com.hardcastle.honeysuckervendor.Utils.APIInterface;
import com.hardcastle.honeysuckervendor.Utils.APIUtils;
import com.hardcastle.honeysuckervendor.Utils.GLOBAL;
import com.hardcastle.honeysuckervendor.Utils.MyCustomDialog;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverListForAssignServiceFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener, AdapterFetchDriverListForAssignService.onDriverClickListener {

    private static final String TAG = "DriverList";
    public static final String mBroadcastStringAction = "com.hardcastle.honeysuckervendor.Receiver";
    private static final String ARG_PARAM1 = "user_request_info";
    private static final String ARG_PARAM2 = "user_request_service";
    private static final String ARG_PARAM3 = "list_of_drivers";
    private ArrayList<AvailableDriverDetails.DATum> mListAvailableDrivers;
    private UserRequest.DATum.INFO mUserRequestInfo;
    private UserRequest.DATum.SERVICE mUserRequestService;
    private APIInterface mApiInterface;
    private GLOBAL mGlobalInstance;
    private SharedPreferences sharedPreferences;
    private ArrayList<LatLongClass> mListLatLongClass;
    private ArrayList<String> mListDistanceInKms;
    private RecyclerView.Adapter mRVAdapter;
    private static String mDriverID;
    private IntentFilter mIntentFilter;
    private ProgressDialog dialog;

    @BindView(R.id.tvDriverList)
    TextView mTvDriverList;
    @BindView(R.id.recyclerViewFetchDriverList)
    RecyclerView mRecyclerViewFetchDriverList;

    public DriverListForAssignServiceFragment() {
        // Required empty public constructor
    }

    public static DriverListForAssignServiceFragment newInstance(UserRequest.DATum.INFO userRequestInfo, UserRequest.DATum.SERVICE userRequestService, ArrayList<AvailableDriverDetails.DATum> listAvailableDrivers) {
        DriverListForAssignServiceFragment fragment = null;
        try {
            Log.e(TAG, "newInstance: ");
            fragment = new DriverListForAssignServiceFragment();
            Bundle args = new Bundle();
            args.putParcelable(ARG_PARAM1, userRequestInfo);
            args.putParcelable(ARG_PARAM2, userRequestService);
            args.putParcelableArrayList(ARG_PARAM3, listAvailableDrivers);
            fragment.setArguments(args);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Log.e(TAG, "onCreate: ");
            mListLatLongClass = new ArrayList<LatLongClass>();
            mListDistanceInKms = new ArrayList<String>();

            if (getArguments() != null) {
                mUserRequestInfo = getArguments().getParcelable(ARG_PARAM1);
                mUserRequestService = getArguments().getParcelable(ARG_PARAM2);
                mListAvailableDrivers = getArguments().getParcelableArrayList(ARG_PARAM3);
            }

            Log.e(TAG, "onCreate: Size Of Available Driver List : " + mListAvailableDrivers.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver_list_for_assign, container, false);
        try {
            ButterKnife.bind(this, view);

            Log.e(TAG, "onCreateView: ");
            mRecyclerViewFetchDriverList.setHasFixedSize(true);

            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(mBroadcastStringAction);
            dialog = new ProgressDialog(getActivity());
            mRecyclerViewFetchDriverList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

            mApiInterface = APIUtils.getAPIInterface();
            mGlobalInstance = GLOBAL.getInstance();
            sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            mListLatLongClass.clear();

            mListLatLongClass.add(0, new LatLongClass(mUserRequestInfo.getLATTITUDE().toString().trim(), mUserRequestInfo.getLONGITUDE().toString().trim()));

            Log.e(TAG, "onStart: Size Of Available Driver List : " + mListAvailableDrivers.size());

            for (int i = 0; i < mListAvailableDrivers.size(); i++) {
                mListLatLongClass.add(new LatLongClass(mListAvailableDrivers.get(i).getLATITUDE().toString().trim(), mListAvailableDrivers.get(i).getLONGITUDE().toString().trim()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            Log.e(TAG, "onStart: ");
            mTvDriverList.setText(getResources().getString(R.string.driver_list));
            findDistanceBetweenUserAndDriver(mListLatLongClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            // register connection status listener
            GLOBAL.getInstance().setConnectivityListener(this);
            getActivity().registerReceiver(mReceiver, mIntentFilter);
            setAdapter(getActivity(), mListAvailableDrivers, mListDistanceInKms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                if (intent.getAction().equals(mBroadcastStringAction)) {

                    Log.e(TAG, "onReceive: Brodcast receiver of DriverlistForAssignService");
                    if (dialog.isShowing())
                        dialog.dismiss();

                    Intent stopIntent = new Intent(getActivity(), UserRequestAssignedCheckTimeService.class);
                    getActivity().stopService(stopIntent);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        if (isConnected) {
            GLOBAL.showMessage(getActivity(), getResources().getString(R.string.internet_available));
        } else {
            GLOBAL.showMessage(getActivity(), getResources().getString(R.string.check_internet_connection));
        }
    }

    private void findDistanceBetweenUserAndDriver(ArrayList<LatLongClass> listLatLongClass) {

        try {
            mListDistanceInKms.clear();

            Location startPoint = new Location("locationA");
            startPoint.setLatitude(Double.parseDouble(listLatLongClass.get(0).getLatitude().toString().trim()));
            startPoint.setLongitude(Double.parseDouble(listLatLongClass.get(0).getLongitude().toString().trim()));

            Log.e(TAG, "findDistanceBetweenUserAndDriver: Service Lat :" + listLatLongClass.get(0).getLatitude().toString().trim() + " Long : " + listLatLongClass.get(0).getLongitude().toString().trim());

            Location endPoint;
            double distance;

            for (int i = 1; i < listLatLongClass.size(); i++) {
                endPoint = new Location("locationA");
                endPoint.setLatitude(Double.parseDouble(listLatLongClass.get(i).getLatitude().toString().trim()));
                endPoint.setLongitude(Double.parseDouble(listLatLongClass.get(i).getLongitude().toString().trim()));
                Log.e(TAG, "findDistanceBetweenUserAndDriver: Driver Lat :" + listLatLongClass.get(i).getLatitude().toString().trim() + " Long : " + listLatLongClass.get(i).getLongitude().toString().trim());
                distance = startPoint.distanceTo(endPoint);
                distance = distance * 0.001;
                Log.e(TAG, "findDistanceBetweenUserAndDriver: " + distance);
                mListDistanceInKms.add(String.valueOf(new DecimalFormat("##.##").format(distance))); // String.valueOf(distance)
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDriverClickToAssign(View v, String driverId) {

        try {
            if (ConnectivityReceiver.isConnected()) {

                int hours = differenceBetweenTwoTime(mUserRequestInfo.getsERVICETIME().toString().trim());
                Log.e(TAG, "onDriverClickToAssign: ");
                if (hours < 2) {
                    mDriverID = driverId;
                    showDialogBox(getResources().getString(R.string.confirmation), getResources().getString(R.string.confirm_assign_driver), getResources().getString(R.string.yes), getResources().getString(R.string.no));
                } else {
                    showDialogBoxALERT(getResources().getString(R.string.alert), getResources().getString(R.string.cannot_assign_service_to_driver_before_five_hours), getResources().getString(R.string.ok));
                }
            } else {
                GLOBAL.showMessage(getActivity(), getResources().getString(R.string.check_internet_connection));
            }
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
                assignDriver(mDriverID);
            }

        }).create();

        //2. now setup to change color of the button
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorOrange));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorOrange));
            }
        });

        dialog.show();
    }

    public void showDialogBoxALERT(String title, String alertMessage, String btnPositiveName) {

        MyCustomDialog builder = new MyCustomDialog(getActivity(), title, alertMessage);
        final AlertDialog dialog = builder.setPositiveButton(btnPositiveName, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getActivity().finish();
            }

        }).create();

        //2. now setup to change color of the button
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorOrange));
            }
        });

        dialog.show();
    }

    private int differenceBetweenTwoTime(String serviceTime) {

        Log.e(TAG, "differenceBetweenTwoTime: ");
        Calendar calander = Calendar.getInstance();
        SimpleDateFormat simpledateformat = new SimpleDateFormat("hh:mm a");
        String dateString = simpledateformat.format(calander.getTime());
        Date timeSystem = null, timeService = null;
        int hours = 0;
        try {
            Log.e(TAG, "differenceBetweenTwoTime: STEP 0");
            try {
                Log.e(TAG, "differenceBetweenTwoTime: STEP 1");
                timeService = simpledateformat.parse(serviceTime.toString().trim());
                timeSystem = simpledateformat.parse(dateString.toString().trim());
                Log.e(TAG, "differenceBetweenTwoTime: STEP 2");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.e(TAG, "differenceBetweenTwoTime: STEP 3");
            long difference = timeService.getTime() - timeSystem.getTime();
            int days = (int) (difference / (1000 * 60 * 60 * 24));
            hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
            int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
            hours = (hours < 0 ? -hours : hours);

            Log.e(TAG, "differenceBetweenTwoTime, SERVICE TIME : " + serviceTime.toString().trim());
            Log.e(TAG, "differenceBetweenTwoTime, SYSTEM TIME : " + dateString.toString().trim());
            Log.e(TAG, "differenceBetweenTwoTime: DIFFERENCE BETWEEN TWO TIME IN HOUR : " + hours + "  MINUTES : " + min);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return hours;

    }

    void assignDriver(String driverId) {

        if (ConnectivityReceiver.isConnected()) {

            try {
                Gson gson = new Gson();
                String json = sharedPreferences.getString(getResources().getString(R.string.preference_vendor_info), "");
                final VendorDetails vendorDetails = gson.fromJson(json, VendorDetails.class);

                Log.e(TAG, "assignDriver, VENDOR_ID : " + vendorDetails.getDATA().get(0).getID().toString().trim());
                Log.e(TAG, "assignDriver, USER_ID : " + mUserRequestInfo.getUSERID().toString().trim());
                Log.e(TAG, "assignDriver, DRIVER_ID : " + driverId);
                Log.e(TAG, "assignDriver, SERVICE_REQUEST_ID : " + mUserRequestInfo.getsERVICEREQUESTID().toString().trim());

                mGlobalInstance.hashmapKeyValue.clear();

                mGlobalInstance.hashmapKeyValue.put("VENDOR_ID", vendorDetails.getDATA().get(0).getID().toString().trim());
                mGlobalInstance.hashmapKeyValue.put("USER_ID", mUserRequestInfo.getUSERID().toString().trim());
                mGlobalInstance.hashmapKeyValue.put("DRIVER_ID", driverId);
                mGlobalInstance.hashmapKeyValue.put("SERVICE_REQUEST_ID", mUserRequestInfo.getsERVICEREQUESTID().toString().trim());

                Call<AssignedUserRequest> call = mApiInterface.assignDriverToSpecificTrip(mGlobalInstance.getInput(TAG, mGlobalInstance.hashmapKeyValue).toString());

                call.enqueue(new Callback<AssignedUserRequest>() {
                    @Override
                    public void onResponse(Call<AssignedUserRequest> call, Response<AssignedUserRequest> response) {

                        Log.e(TAG, "onResponse: Response Code : " + response.code());

                        if (response.isSuccessful()) {

                            if (response.body().getSTATUS() == 1) {

                                Log.e(TAG, "onResponse: SUCCESS ");

                                Gson gson1 = new Gson();
                                String assignedUserRequestJson = sharedPreferences.getString(getResources().getString(R.string.preference_assigned_userrequest), null);

                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                ArrayList<String> listAlreadyAssignedUserRequest = new ArrayList<String>();

                                Type type = new TypeToken<ArrayList<String>>() {}.getType();

                                Gson gson = new Gson();
                                String assignedUserRequestJson1;

                                if (gson1.fromJson(assignedUserRequestJson, type) == null) {
                                    listAlreadyAssignedUserRequest.add(mUserRequestInfo.getsERVICEREQUESTID().toString().trim());
                                    assignedUserRequestJson1 = gson.toJson(listAlreadyAssignedUserRequest);
                                    Log.e(TAG, "onResponse: Assigned User Request JSON : " + assignedUserRequestJson1);
                                    editor.putString(getResources().getString(R.string.preference_assigned_userrequest), assignedUserRequestJson1);
                                    editor.apply();
                                } else {
                                    listAlreadyAssignedUserRequest = gson1.fromJson(assignedUserRequestJson, type);

                                    if (!listAlreadyAssignedUserRequest.contains(mUserRequestInfo.getsERVICEREQUESTID().toString().trim())) {
                                        listAlreadyAssignedUserRequest.add(mUserRequestInfo.getsERVICEREQUESTID().toString().trim());
                                        assignedUserRequestJson1 = gson.toJson(listAlreadyAssignedUserRequest);
                                        Log.e(TAG, "onResponse: Assigned User Request JSON : " + assignedUserRequestJson1 + "  Size : " + listAlreadyAssignedUserRequest.size());
                                        editor.putString(getResources().getString(R.string.preference_assigned_userrequest), assignedUserRequestJson1);
                                        editor.apply();
                                    }

                                }

                                dialog.setMessage(getResources().getString(R.string.waiting_for_driver));
                                dialog.show();
                                dialog.setCancelable(false);

                                // use this to start and trigger a service
                                Intent i = new Intent(getActivity(), UserRequestAssignedCheckTimeService.class);
                                i.putExtra("FROM", "FRAGMENT");
                                i.putExtra("VENDOR_ID", vendorDetails.getDATA().get(0).getID().toString().trim());
                                i.putExtra("USER_ID", mUserRequestInfo.getUSERID().toString().trim());
                                i.putExtra("DRIVER_ID", mDriverID);
                                i.putExtra("SERVICE_REQUEST_ID", mUserRequestInfo.getsERVICEREQUESTID().toString().trim());
                                getActivity().startService(i);

                                Log.e(TAG, "onResponse: Driver Assigned Successfully");
                                Toast.makeText(getActivity(), "Driver Assigned Successfully", Toast.LENGTH_SHORT).show();
                                //getActivity().finish();

                            } else {
                                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onResponse: Something went wrong");
                            }

                        } else {

                            Toast.makeText(getActivity(), "Not Successfull", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onResponse: Not Successfull");
                        }
                    }

                    @Override
                    public void onFailure(Call<AssignedUserRequest> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            GLOBAL.showMessage(getActivity(), getResources().getString(R.string.check_internet_connection));
        }
    }

    private void setAdapter(Context context, ArrayList<AvailableDriverDetails.DATum> datumList, ArrayList<String> listDistanceInKms) {

        try {
            mRVAdapter = new AdapterFetchDriverListForAssignService(context, datumList, listDistanceInKms, this);
            mRecyclerViewFetchDriverList.setAdapter(mRVAdapter);
            mRVAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
