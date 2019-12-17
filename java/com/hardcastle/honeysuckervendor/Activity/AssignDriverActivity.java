package com.hardcastle.honeysuckervendor.Activity;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hardcastle.honeysuckervendor.Fragment.DriverListForAssignServiceFragment;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AssignDriverActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = "AssignDriver";
    public static final String mBroadcastStringAction = "com.hardcastle.honeysuckervendor.Receiver";
    private FragmentTransaction fragmentTransaction;
    private String mDriverId;
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<AvailableDriverDetails.DATum> mListAvailableDrivers;
    private UserRequest.DATum.INFO mUserRequestInfo;
    private UserRequest.DATum.SERVICE mUserRequestService;
    private APIInterface mApiInterface;
    private GLOBAL mGlobalInstance;
    private SharedPreferences sharedPreferences;
    private List<Marker> mListMarkerDirver;
    private Marker markerDriver;
    private ArrayList<String> mListValues;
    private ArrayList<AssignedUserRequest> mListAssignedUserRequest;
    private ArrayList<LatLongClass> mListLatLongClass;
    private ArrayList<String> mListDistanceInKms;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private IntentFilter mIntentFilter;
    private ProgressDialog dialog;

    @BindView(R.id.tvPhoneNo) TextView mTvPhoneNo;
    @BindView(R.id.btnAssign) TextView mBtnAssign;
    @BindView(R.id.slidingPanelLayout) SlidingUpPanelLayout mSlidingUpPanelLayout;
    @BindViews({R.id.tvRatings, R.id.tvDriverName, R.id.tvPhoneNo, R.id.tvVehicleNo, R.id.tvStatus})
    List<TextView> mListTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_driver);

        try {
            buildGoogleApiClient();

            ButterKnife.bind(this);

            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(mBroadcastStringAction);

            mListMarkerDirver = new ArrayList<Marker>();
            mListLatLongClass = new ArrayList<LatLongClass>();
            mListDistanceInKms = new ArrayList<String>();
            mApiInterface = APIUtils.getAPIInterface();
            mGlobalInstance = GLOBAL.getInstance();
            sharedPreferences = getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            mListValues = new ArrayList<String>();
            mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mMapFragment.getMapAsync(this);
            dialog = new ProgressDialog(AssignDriverActivity.this);

            getDetailsInformationOfUserRequestAndAvailableDrivers();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        mGoogleApiClient.connect();

        try {
            mBtnAssign.setText(getResources().getString(R.string.assign));

            if (!isLocationEnabled(getApplicationContext())) {
                Toast.makeText(AssignDriverActivity.this, getResources().getString(R.string.enable_location), Toast.LENGTH_LONG).show();
                displayLocationSettings(getApplicationContext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            // register connection status listener
            GLOBAL.getInstance().setConnectivityListener(this);
            registerReceiver(mReceiver, mIntentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                if (intent.getAction().equals(mBroadcastStringAction)) {
                    Log.e(TAG, "onReceive: Brodcast receiver of AssignDriverActivity");
                    if (dialog.isShowing())
                        dialog.dismiss();

                    Intent stopIntent = new Intent(AssignDriverActivity.this,
                            UserRequestAssignedCheckTimeService.class);
                    stopService(stopIntent);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();

        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "onConnected: ");
    }

    @Override
    public void onConnectionSuspended(int i) {
        try {
            Log.e(TAG, "onConnectionSuspended: ");
            mGoogleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "onLocationChanged: ");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            mMap = googleMap;
            markUserLocationOnMap(mMap);
            markAvailableDriverLocationOnMap(mMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Log.e(TAG, "onMarkerClick: ");

        try {
            if (mListMarkerDirver.contains(marker)) {

                Log.e(TAG, "onMarkerClick: On driver");
                int position = 0;
                boolean flag = false;
                String title = marker.getTitle().toString().trim();
                mListValues.clear();

                for (int i = 0; i < mListAvailableDrivers.size(); i++) {

                    if (title.equalsIgnoreCase(mListAvailableDrivers.get(i).getDRIVERNAME().toString().trim())) {

                        position = i;
                        flag = true;
                        Log.e(TAG, "onMarkerClick: position : " + position + "   i=" + i);

                        if (flag) {
                            mDriverId = mListAvailableDrivers.get(i).getDRIVERID().toString().trim();
                            Log.e(TAG, "onMarkerClick: DriverNAme : " + mListAvailableDrivers.get(position).getDRIVERNAME().toString().trim());
                            Log.e(TAG, "onMarkerClick: Mobile No : " + mListAvailableDrivers.get(position).getMOBILE().toString().trim());
                            Log.e(TAG, "onMarkerClick: Vehicle No : " + mListAvailableDrivers.get(position).getVEHICLENUMBER().toString().trim());
                            Log.e(TAG, "onMarkerClick: Ratings : " + mListAvailableDrivers.get(position).getRATINGS().toString().trim());

                            mListValues.add(mListAvailableDrivers.get(position).getRATINGS().toString().trim());
                            mListValues.add(mListAvailableDrivers.get(position).getDRIVERNAME().toString().trim());
                            mListValues.add(mListAvailableDrivers.get(position).getMOBILE().toString().trim());
                            mListValues.add(mListAvailableDrivers.get(position).getVEHICLENUMBER().toString().trim());

                            if (mListAvailableDrivers.get(position).getWORKSTATUS().toString().trim().equalsIgnoreCase("0")) {
                                mListValues.add("Free");
                            } else  {
                                mListValues.add("Busy");
                            } /*else if (mListAvailableDrivers.get(position).getWORKSTATUS().toString().trim().equalsIgnoreCase("2")) {
                                mListValues.add("Not Available...Already assigned more than 3 services");
                            }*/

                            ButterKnife.apply(mListTextView, SET_TEXT);
                            setSlidingPanel();
                            break;
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @OnClick(R.id.fabListOfDriver)
    void listOfDriver() {

        try {
            Log.e(TAG, "listOfDriver: Open DriverListForAssignServiceFragment");
            Log.e(TAG, "listOfDriver: Size " + mListAvailableDrivers.size());
            DriverListForAssignServiceFragment driverListForAssignServiceFragment = DriverListForAssignServiceFragment.newInstance(mUserRequestInfo, mUserRequestService, mListAvailableDrivers);
            fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.add(R.id.container, driverListForAssignServiceFragment, "DRIVER_LIST");
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.llPhoneCall, R.id.ivPhoneNoIcon, R.id.tvPhoneNo})
    void callUser() {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + mTvPhoneNo.getText().toString().trim()));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnAssign)
    void assignDriver() {

        try {
            if (ConnectivityReceiver.isConnected()) {
                int hours = differenceBetweenTwoTime(mUserRequestInfo.getsERVICETIME().toString().trim());

                if (hours < 2) {
                    showDialogBox(getResources().getString(R.string.confirmation), getResources().getString(R.string.confirm_assign_driver), getResources().getString(R.string.yes), getResources().getString(R.string.no));
                } else {
                    showDialogBoxALERT(getResources().getString(R.string.alert), getResources().getString(R.string.cannot_assign_service_to_driver_before_five_hours), getResources().getString(R.string.ok));
                }
            } else {
                GLOBAL.showMessage(getApplicationContext(), getResources().getString(R.string.check_internet_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int differenceBetweenTwoTime(String serviceTime) {

        int hours = 0;
        try {
            Log.e(TAG, "differenceBetweenTwoTime: ");
            Calendar calander = Calendar.getInstance();
            SimpleDateFormat simpledateformat = new SimpleDateFormat("hh:mm a");
            String dateString = simpledateformat.format(calander.getTime());
            Date timeSystem = null, timeService = null;
            hours = 0;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hours;

    }

    void assinedDriverToService() {

        try {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(getResources().getString(R.string.preference_vendor_info), "");
        final VendorDetails vendorDetails = gson.fromJson(json, VendorDetails.class);
        final String driverID = mDriverId;
        Log.e(TAG, "assinedDriverToService, VENDOR_ID : " + vendorDetails.getDATA().get(0).getID().toString().trim());
        Log.e(TAG, "assinedDriverToService, USER_ID : " + mUserRequestInfo.getUSERID().toString().trim());
        Log.e(TAG, "assinedDriverToService, DRIVER_ID : " + mDriverId);
        Log.e(TAG, "assinedDriverToService, SERVICE_REQUEST_ID : " + mUserRequestInfo.getsERVICEREQUESTID().toString().trim());

        mGlobalInstance.hashmapKeyValue.clear();

        mGlobalInstance.hashmapKeyValue.put("VENDOR_ID", vendorDetails.getDATA().get(0).getID().toString().trim());
        mGlobalInstance.hashmapKeyValue.put("USER_ID", mUserRequestInfo.getUSERID().toString().trim());
        mGlobalInstance.hashmapKeyValue.put("DRIVER_ID", mDriverId);
        mGlobalInstance.hashmapKeyValue.put("SERVICE_REQUEST_ID", mUserRequestInfo.getsERVICEREQUESTID().toString().trim());

        Call<AssignedUserRequest> call = mApiInterface.assignDriverToSpecificTrip(mGlobalInstance.getInput(TAG, mGlobalInstance.hashmapKeyValue).toString());

        call.enqueue(new Callback<AssignedUserRequest>() {
            @Override
            public void onResponse(Call<AssignedUserRequest> call, Response<AssignedUserRequest> response) {

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
                                Log.e(TAG, "onResponse: Assigned User Request JSON : " + assignedUserRequestJson1);
                                editor.putString(getResources().getString(R.string.preference_assigned_userrequest), assignedUserRequestJson1);
                                editor.apply();
                            }

                        }

                        dialog.setMessage(getResources().getString(R.string.waiting_for_driver));
                        dialog.show();
                        dialog.setCancelable(false);

                        // use this to start and trigger a service
                        Intent i= new Intent(AssignDriverActivity.this, UserRequestAssignedCheckTimeService.class);
                        i.putExtra("FROM", "ACTIVITY");
                        i.putExtra("VENDOR_ID", vendorDetails.getDATA().get(0).getID().toString().trim());
                        i.putExtra("USER_ID", mUserRequestInfo.getUSERID().toString().trim());
                        i.putExtra("DRIVER_ID", driverID);
                        i.putExtra("SERVICE_REQUEST_ID", mUserRequestInfo.getsERVICEREQUESTID().toString().trim());
                        startService(i);

                        Log.e(TAG, "onResponse: Driver Assigned Successfully");
                        Toast.makeText(AssignDriverActivity.this, "Driver Assigned Successfully", Toast.LENGTH_SHORT).show();
                        //finish();

                    } else {
                        Toast.makeText(AssignDriverActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onResponse: Something went wrong");
                    }

                } else {

                    Toast.makeText(AssignDriverActivity.this, "Not Successfull", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onResponse: Not Successfull");
                }
            }

            @Override
            public void onFailure(Call<AssignedUserRequest> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(AssignDriverActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
            }
        });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showDialogBox(String title, String alertMessage, String btnPositiveName, String btnNegativeName) {

        MyCustomDialog builder = new MyCustomDialog(AssignDriverActivity.this, title, alertMessage);

        final AlertDialog dialog = builder.setNegativeButton(btnNegativeName, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }

        }).setPositiveButton(btnPositiveName, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                assinedDriverToService();
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

        MyCustomDialog builder = new MyCustomDialog(AssignDriverActivity.this, title, alertMessage);
        final AlertDialog dialog = builder.setPositiveButton(btnPositiveName, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
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

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void setSlidingPanel() {

        try {
            Log.e(TAG, "setSlidingPanel: ");
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;

            mSlidingUpPanelLayout.setPanelHeight((height / 4));
            mSlidingUpPanelLayout.setTouchEnabled(true);

            mSlidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                @Override
                public void onPanelSlide(View panel, float slideOffset) {

                    Log.e(TAG, "onPanelSlide, offset " + slideOffset);
                }

                @Override
                public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                    if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                        expandMap();
                        Log.e(TAG, "onPanelCollapsed");
                    } else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                        collapseMap();
                        Log.e(TAG, "onPanelExpanded");
                    } else if (newState == SlidingUpPanelLayout.PanelState.ANCHORED) {
                        mSlidingUpPanelLayout.setOverlayed(true);
                        Log.e(TAG, "onPanelAnchored");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void collapseMap() {

        try {
            Log.e(TAG, "collapseMap: ");
            if (mMap != null) {

                mMap.animateCamera(CameraUpdateFactory.zoomTo(13f), 1000, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void expandMap() {

        try {
            Log.e(TAG, "expandMap: ");
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 1000, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDetailsInformationOfUserRequestAndAvailableDrivers() {
        try {
            mUserRequestInfo = getIntent().getExtras().getParcelable("OBJECT_INFO");
            mUserRequestService = getIntent().getExtras().getParcelable("OBJECT_SERVICE");
            mListAvailableDrivers = getIntent().getParcelableArrayListExtra("AVAILABLE_DRIVERS");
            Log.e(TAG, "getDetailsInformationOfUserRequestAndAvailableDrivers: Available Driver Size : " + mListAvailableDrivers.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void markUserLocationOnMap(GoogleMap map) {

        try {
            LatLng newLatLngTemp = new LatLng(Double.parseDouble(mUserRequestInfo.getLATTITUDE().toString().trim()), Double.parseDouble(mUserRequestInfo.getLONGITUDE().toString().trim()));

            CameraPosition cameraPosition = new CameraPosition.Builder().target(newLatLngTemp).zoom(14f).build();

            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            MarkerOptions options = new MarkerOptions();
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker_orange));
            options.position(newLatLngTemp);
            map.addMarker(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void markAvailableDriverLocationOnMap(GoogleMap map) {

        try {
            LatLng newLatLngTemp;
            map.setOnMarkerClickListener(this);

            for (int i = 0; i < mListAvailableDrivers.size(); i++) {

                Log.e(TAG, "markAvailableDriverLocationOnMap :" + i + ". ID : " + mListAvailableDrivers.get(i).getDRIVERID().toString().trim() + " NAME : " + mListAvailableDrivers.get(i).getDRIVERNAME().toString().trim() + " LAT : " + mListAvailableDrivers.get(i).getLATITUDE().toString().trim() + "  LONG : " + mListAvailableDrivers.get(i).getLONGITUDE().toString().trim());

                newLatLngTemp = new LatLng(Double.parseDouble(mListAvailableDrivers.get(i).getLATITUDE().toString().trim()), Double.parseDouble(mListAvailableDrivers.get(i).getLONGITUDE().toString().trim()));

                markerDriver = map.addMarker(new MarkerOptions()
                        .position(newLatLngTemp)
                        .title(mListAvailableDrivers.get(i).getDRIVERNAME().toString().trim())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_truck)));

                mListMarkerDirver.add(markerDriver);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    final ButterKnife.Action<TextView> SET_TEXT = new ButterKnife.Action<TextView>() {
        @Override
        public void apply(TextView textView, int index) {
            textView.setText(mListValues.get(index));
        }
    };


    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }


    private void displayLocationSettings(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(AssignDriverActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        if (isConnected) {
            GLOBAL.showMessage(getApplicationContext(), getResources().getString(R.string.internet_available));
        } else {
            GLOBAL.showMessage(getApplicationContext(), getResources().getString(R.string.check_internet_connection));
        }
    }
}
