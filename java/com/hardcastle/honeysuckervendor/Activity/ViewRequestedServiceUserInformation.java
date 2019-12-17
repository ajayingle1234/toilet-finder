package com.hardcastle.honeysuckervendor.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.hardcastle.honeysuckervendor.Model.AvailableDriverDetails;
import com.hardcastle.honeysuckervendor.Model.UserRequest;
import com.hardcastle.honeysuckervendor.R;
import com.hardcastle.honeysuckervendor.Receiver.ConnectivityReceiver;
import com.hardcastle.honeysuckervendor.Utils.APIInterface;
import com.hardcastle.honeysuckervendor.Utils.APIUtils;
import com.hardcastle.honeysuckervendor.Utils.GLOBAL;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ViewRequestedServiceUserInformation extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapReadyCallback, ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = "RequestAcceptReject";
    private final String SEPTIC = "SEPTIC";
    private final String DRAINAGE = "DRAINAGE";
    private final String TOILET = "TOILET";
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    @BindView(R.id.ivSepticService) ImageView mIvSepticService;
    @BindView(R.id.ivDrainCleaning) ImageView mIvDrainCleaning;
    @BindView(R.id.ivToiletCleaning) ImageView mIvToiletCleaning;
    @BindView(R.id.slidingPanelLayout) SlidingUpPanelLayout mSlidingUpPanelLayout;
    @BindViews({R.id.tvCostInRupees, R.id.tvUsername, R.id.tvPhoneNo, R.id.tvAddress, R.id.tvDateTime}) List<TextView> mListTextView;

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private ArrayList<String> mListValues;
    private UserRequest.DATum.INFO mUserRequestInfo;
    private UserRequest.DATum.SERVICE mUserRequestService;
    private ArrayList<AvailableDriverDetails.DATum> mListAvailableDriverDetails;
    private String mVendorID;
    private GoogleApiClient mGoogleApiClient;
    private APIInterface mApiInterface;
    private GLOBAL mGlobalInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_request_accept_reject);

        buildGoogleApiClient();

        try {
            ButterKnife.bind(this);
            mApiInterface = APIUtils.getAPIInterface();
            mGlobalInstance = GLOBAL.getInstance();

            getDetailsInformationOfUserRequest();
            setMapAndSlidingPanel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            setDetailsInformationOfUserRequest();
            mGoogleApiClient.connect();
            ButterKnife.apply(mListTextView,SET_TEXT);

            if (!isLocationEnabled(getApplicationContext())) {
                Toast.makeText(ViewRequestedServiceUserInformation.this, getResources().getString(R.string.enable_location), Toast.LENGTH_LONG).show();
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

        // register connection status listener
        GLOBAL.getInstance().setConnectivityListener(this);
    }

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
        Log.e(TAG, "onConnectionSuspended: ");
        mGoogleApiClient.connect();
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
        Log.e(TAG, "onMapReady: ");

        mMap = googleMap;

        try {
            LatLng newLatLngTemp = new LatLng(Double.parseDouble(mUserRequestInfo.getLATTITUDE().toString().trim()), Double.parseDouble(mUserRequestInfo.getLONGITUDE().toString().trim()));

            CameraPosition cameraPosition = new CameraPosition.Builder().target(newLatLngTemp).zoom(14f).build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            MarkerOptions options = new MarkerOptions();
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker_orange));
            options.position(newLatLngTemp);
            mMap.addMarker(options);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.llPhoneCall, R.id.ivPhoneNoIcon, R.id.tvPhoneNo})
    void callUser() {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+mUserRequestInfo.getMOBILE().toString().trim()));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        try {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMapAndSlidingPanel() {
        try {
            mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mMapFragment.getMapAsync(this);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;

            mSlidingUpPanelLayout.setPanelHeight((height / 4));
            mSlidingUpPanelLayout.setTouchEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDetailsInformationOfUserRequest() {
        try {
            mUserRequestInfo = getIntent().getExtras().getParcelable("OBJECT_INFO");
            mUserRequestService = getIntent().getExtras().getParcelable("OBJECT_SERVICE");
            mVendorID = getIntent().getStringExtra("VENDOR_ID");
            mListValues = new ArrayList<String>();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDetailsInformationOfUserRequest() {
        try {
            mListValues.add(mUserRequestInfo.getRATE().toString().trim());
            mListValues.add(mUserRequestInfo.getUSERNAME().toString().trim());
            mListValues.add(mUserRequestInfo.getMOBILE().toString().trim());
            mListValues.add(mUserRequestInfo.getsERVICEADDRESS().toString().trim());
            mListValues.add(mUserRequestInfo.getSERVICEDATE().toString().trim()+"  "+mUserRequestInfo.getsERVICETIME().toString().trim());

            if (mUserRequestService.getSERVICENAME().contains(SEPTIC)) {
                mIvSepticService.setImageDrawable(getResources().getDrawable(R.drawable.ic_filled_small_septic_service));
            } else {
                mIvSepticService.setImageDrawable(getResources().getDrawable(R.drawable.ic_small_septic_service));
            }

            if (mUserRequestService.getSERVICENAME().contains(DRAINAGE)) {
                mIvDrainCleaning.setImageDrawable(getResources().getDrawable(R.drawable.ic_filled_small_drain_cleanig));
            } else {
                mIvDrainCleaning.setImageDrawable(getResources().getDrawable(R.drawable.ic_small_drain_cleaning));
            }

            if (mUserRequestService.getSERVICENAME().contains(TOILET)) {
                mIvToiletCleaning.setImageDrawable(getResources().getDrawable(R.drawable.ic_filled_small_toilet_cleanig));
            } else {
                mIvToiletCleaning.setImageDrawable(getResources().getDrawable(R.drawable.ic_small_toilet_cleaning));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final ButterKnife.Action<TextView> SET_TEXT = new ButterKnife.Action<TextView>() {
        @Override public void apply(TextView textView, int index) {
            textView.setText(mListValues.get(index));
        }
    };

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
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
                            status.startResolutionForResult(ViewRequestedServiceUserInformation.this, REQUEST_CHECK_SETTINGS);
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
