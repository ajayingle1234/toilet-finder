package com.hardcastle.honeysuckervendor.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.hardcastle.honeysuckervendor.Model.VendorDetails;
import com.hardcastle.honeysuckervendor.R;
import com.hardcastle.honeysuckervendor.Receiver.ConnectivityReceiver;
import com.hardcastle.honeysuckervendor.Utils.APIInterface;
import com.hardcastle.honeysuckervendor.Utils.APIUtils;
import com.hardcastle.honeysuckervendor.Utils.AppUtils;
import com.hardcastle.honeysuckervendor.Utils.FetchAddressIntentService;
import com.hardcastle.honeysuckervendor.Utils.GLOBAL;
import com.hardcastle.honeysuckervendor.Utils.GeocodingLocation;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VendorManageAddressActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapReadyCallback , ConnectivityReceiver.ConnectivityReceiverListener/*,GoogleMap.OnMapClickListener*/ {

    @BindView(R.id.slidingPanelLayout) SlidingUpPanelLayout mSlidingUpPanelLayout;
    @BindView(R.id.tilAddress) TextInputLayout mTILAddress;
    @BindView(R.id.tilHouseFlatNo) TextInputLayout mTILHouseFlatNo;
    @BindView(R.id.tilLandmark) TextInputLayout mTILLandmark;
    @BindView(R.id.edtAddress) EditText mEdtAddress;
    @BindView(R.id.edtHouseFlatNo) EditText mEdtHouseFlatNo;
    @BindView(R.id.edtLandmark) EditText mEdtLandmark;
    @BindView(R.id.btnSaveAndProceed) Button mBtnSaveAndProceed;
    @BindView(R.id.ivSearchButton) ImageView mIvSearchButton;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private SharedPreferences sharedPreferences;
    private Gson mGson;
    private String mVendorInfo;
    private VendorDetails mVendorDetailsObj;
    private List<VendorDetails.DATum> mListVendorDetails;
    private APIInterface mApiInterface;
    private GLOBAL mGlobalInstance;

    Geocoder geocoder;

    private SupportMapFragment mMapFragment;

    private GoogleMap mMap;

    private GoogleApiClient mGoogleApiClient;
    private LatLng mCenterLatLong;
    private Location mLocation;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static String TAG = "MAP LOCATION";
    Context mContext;

    private AddressResultReceiver mResultReceiver;

    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStateOutput;
    private String Lat, Lng;
    private String LatAddress,LngAddress;
    private String locationAddress,locationfromAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_manage_address);

        Log.e(TAG, "onCreate");
        mContext = this;
        ButterKnife.bind(this);
        mApiInterface = APIUtils.getAPIInterface();
        mGlobalInstance = GLOBAL.getInstance();
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        geocoder = new Geocoder(this, Locale.getDefault());

        mGson = new Gson();
        mVendorInfo = sharedPreferences.getString(getResources().getString(R.string.preference_vendor_info), "");
        mVendorDetailsObj = mGson.fromJson(mVendorInfo, VendorDetails.class);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mResultReceiver = new AddressResultReceiver(new Handler());

        buildGoogleApiClient();
        collapseMap();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        mSlidingUpPanelLayout.setPanelHeight((height / 3));
        mSlidingUpPanelLayout.setTouchEnabled(true);
        //mSlidingUpPanelLayout.setAnchorPoint(0.75f);


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

    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        GLOBAL.getInstance().setConnectivityListener(this);
    }

    @OnClick(R.id.btnSaveAndProceed)
    void saveAndProceed() {
        if (ConnectivityReceiver.isConnected()) {
            submitNewVendorInformation();
        } else {
            GLOBAL.showMessage(getApplicationContext(), getResources().getString(R.string.check_internet_connection));
        }
    }

    @OnClick(R.id.ivSearchButton)
    void searchPlaces(){

        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(VendorManageAddressActivity.this);
            startActivityForResult(intent, 1);
        }
        catch (GooglePlayServicesRepairableException e) {}
        catch (GooglePlayServicesNotAvailableException e) { }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                GeocodingLocation locationAddress = new GeocodingLocation();
                locationAddress.getAddressFromLocation(place.getAddress().toString(),
                        getApplicationContext(), new GeocoderHandler());

                mEdtAddress.setText(place.getAddress());
                Log.e("Tag", "Place: " + place.getAddress() + place.getPhoneNumber());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.e("Tag", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            Log.e("Address",locationAddress);
            if(locationAddress!=null){

                StringTokenizer st = new StringTokenizer(locationAddress,",");
                while (st.hasMoreTokens()) {
                    Lat = st.nextToken();
                    Lng = st.nextToken();
                }

                Location location = new Location("");
                location.setLatitude(Double.parseDouble(Lat));
                location.setLongitude(Double.parseDouble(Lng));
                try {
                    changeMap(location);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(VendorManageAddressActivity.this, "null", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class FetchLocationFromAddressHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationfromAddress = bundle.getString("address");
                    break;
                default:
                    locationfromAddress = null;
            }
            Log.e("Address",locationfromAddress);
            if(locationfromAddress!=null){

                StringTokenizer st = new StringTokenizer(locationfromAddress,",");
                while (st.hasMoreTokens()) {
                    LatAddress = st.nextToken();
                    LngAddress = st.nextToken();
                }

                Log.e("Locfromaddress",LatAddress + " " +LngAddress);

            }else{
                Toast.makeText(VendorManageAddressActivity.this, "null", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String fetchLocation(Double Lat, Double Lng) throws IOException {
        Log.e(TAG, "fetchLocation");
        List<Address> addresses;
        addresses = geocoder.getFromLocation(Lat, Lng, 1);

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();

        return address;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "OnMapReady");
        mMap = googleMap;

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.e("Camera postion change" + "", cameraPosition + "");
                mCenterLatLong = cameraPosition.target;

                mMap.clear();

                try {

                    Location mLocation = new Location("");
                    mLocation.setLatitude(Double.parseDouble(mVendorDetailsObj.getDATA().get(0).getLATITUDE().toString().trim()));
                    mLocation.setLongitude(Double.parseDouble(mVendorDetailsObj.getDATA().get(0).getLONGITUDE().toString().trim()));

                    startIntentService(mLocation);

                    Log.e(TAG, "Address onMapReady :" + fetchLocation(Double.parseDouble(mVendorDetailsObj.getDATA().get(0).getLATITUDE().toString().trim()), Double.parseDouble(mVendorDetailsObj.getDATA().get(0).getLONGITUDE().toString().trim())));
                    GeocodingLocation locationAddress = new GeocodingLocation();

                    locationAddress.getAddressFromLocation(fetchLocation(mCenterLatLong.latitude,mCenterLatLong.longitude).toString(),
                            getApplicationContext(), new FetchLocationFromAddressHandler());

                    mEdtAddress.setText(fetchLocation(mCenterLatLong.latitude,mCenterLatLong.longitude));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.e(TAG, "onConnected");
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            try {

                changeMap(mLastLocation);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e(TAG, "ON connected");

        } else
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        try {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Connection Failed");
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.e(TAG, "onLocationChanged");

        try {
            //if (location != null) {
                changeMap(location);
                //mLocation = location;
            //}

            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: ");
        // Connect the client.
        mGoogleApiClient.connect();

        if (!isLocationEnabled(getApplicationContext())) {
            Toast.makeText(VendorManageAddressActivity.this, getResources().getString(R.string.enable_location), Toast.LENGTH_LONG).show();
            displayLocationSettings(getApplicationContext());
        }

    }

    @Override
    public void onStop() {

        super.onStop();
        Log.e(TAG, "onStop: ");
        mGoogleApiClient.disconnect();
    }


    private void changeMap(Location location) throws IOException {

        Log.e(TAG, "Reaching map" + mMap);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // check if map is created successfully or not
        if (mMap != null) {

            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;

            latLong = new LatLng(Double.parseDouble(mVendorDetailsObj.getDATA().get(0).getLATITUDE().toString().trim()), Double.parseDouble(mVendorDetailsObj.getDATA().get(0).getLONGITUDE().toString().trim()));

            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLong).zoom(19f).tilt(70).build();

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            mLocation = location;
            Log.e(TAG, "Address Change map:" + fetchLocation(location.getLatitude(), location.getLongitude()));
            mEdtAddress.setText(fetchLocation(location.getLatitude(), location.getLongitude()));

            startIntentService(location);

        } else {
            Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    private void collapseMap() {

        Log.e(TAG, "collapseMap: ");
        if (mMap != null) {

            mMap.animateCamera(CameraUpdateFactory.zoomTo(11f), 1000, null);
        }
    }

    private void expandMap() {

        Log.e(TAG, "expandMap: ");
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14f), 1000, null);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        if (isConnected) {
            GLOBAL.showMessage(getApplicationContext(), getResources().getString(R.string.internet_available));
        } else {
            GLOBAL.showMessage(getApplicationContext(), getResources().getString(R.string.check_internet_connection));
        }
    }

    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY);
            mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA);
            mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY);
            mStateOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);

            displayAddressOutput();

            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {

            }
        }
    }

    private void displayAddressOutput() {

        try {
            if (mAreaOutput != null)
                Toast.makeText(VendorManageAddressActivity.this, mAddressOutput, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void startIntentService(Location location) {

        Log.e(TAG, "startIntentService: ");
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.LOCATION_LATITUDE, location.getLatitude());
        intent.putExtra(AppUtils.LocationConstants.LOCATION_LONGITUDE, location.getLongitude());

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

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
                            status.startResolutionForResult(VendorManageAddressActivity.this, REQUEST_CHECK_SETTINGS);
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

    public void submitNewVendorInformation() {

        Log.e(TAG, "submitNewVendorInformation: ");

        mGlobalInstance.hashmapKeyValue.clear();

        mGlobalInstance.hashmapKeyValue.put("TYPE", "V");
        mGlobalInstance.hashmapKeyValue.put("UID", mVendorDetailsObj.getDATA().get(0).getID().toString().trim());
        mGlobalInstance.hashmapKeyValue.put("ADDRESS", mEdtAddress.getText().toString().trim());
        mGlobalInstance.hashmapKeyValue.put("LATITUDE", String.valueOf(mLocation.getLatitude()));
        mGlobalInstance.hashmapKeyValue.put("LONGITUDE", String.valueOf(mLocation.getLongitude()));

        Call<VendorDetails> call = mApiInterface.updateVendorInformation(mGlobalInstance.getInput(TAG, mGlobalInstance.hashmapKeyValue).toString());

        call.enqueue(new Callback<VendorDetails>() {
            @Override
            public void onResponse(Call<VendorDetails> call, Response<VendorDetails> response) {

                Log.e(TAG, "onResponse: ");

                if (response.isSuccessful()) {

                    Log.e(TAG, "onResponse: successfull");

                    mListVendorDetails = response.body().getDATA();
                    mVendorDetailsObj = response.body();
                    //for (VendorDetails.DATum daTum : mListVendorDetails) {

                    Log.e(TAG, "onResponse: ID : " + mListVendorDetails.get(0).getID().toString().trim());
                    Log.e(TAG, "onResponse: NAME : " + mListVendorDetails.get(0).getNAME().toString().trim());
                    Log.e(TAG, "onResponse: MOBILE : " + mListVendorDetails.get(0).getMOBILE().toString().trim());
                    Log.e(TAG, "onResponse: EMAIL : " + mListVendorDetails.get(0).getEMAIL().toString().trim());
                    Log.e(TAG, "onResponse: ADDRESS : " + mListVendorDetails.get(0).getADDRESS().toString().trim());
                    Log.e(TAG, "onResponse: LATITUDE : " + mListVendorDetails.get(0).getLATITUDE().toString().trim());
                    Log.e(TAG, "onResponse: LONGITUDE : " + mListVendorDetails.get(0).getLONGITUDE().toString().trim());
                    Log.e(TAG, "onResponse: ENVIRONMENT CREDITS : " + mListVendorDetails.get(0).getENVIRONMENTCREDITS().toString().trim());

                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    Gson gson = new Gson();
                    String userDetail = gson.toJson(mVendorDetailsObj);
                    editor.putString(getResources().getString(R.string.preference_vendor_info), userDetail);
                    editor.apply();

                    //}
                }

            }

            @Override
            public void onFailure(Call<VendorDetails> call, Throwable t) {

                Log.e(TAG, "onFailure: " + t.toString());
            }
        });
    }

}
