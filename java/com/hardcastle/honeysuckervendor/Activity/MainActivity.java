package com.hardcastle.honeysuckervendor.Activity;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.hardcastle.honeysuckervendor.Fragment.LoginFragment;
import com.hardcastle.honeysuckervendor.Fragment.NewPasswordFragment;
import com.hardcastle.honeysuckervendor.Fragment.OtpFragment;
import com.hardcastle.honeysuckervendor.R;
import com.hardcastle.honeysuckervendor.Receiver.ConnectivityReceiver;
import com.hardcastle.honeysuckervendor.Utils.APIInterface;
import com.hardcastle.honeysuckervendor.Utils.APIUtils;
import com.hardcastle.honeysuckervendor.Utils.GLOBAL;

import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnLoginFragmentInteractionListener, OtpFragment.OnOTPFragmentInteractionListener,
                                                               NewPasswordFragment.OnNewPasswordFragmentInteractionListener, ConnectivityReceiver.ConnectivityReceiverListener{

    @BindView(R.id.tvAccountLabel) TextView mTvAccountLabel;
    @BindView(R.id.tvLoginAccountLabel) TextView mTvLoginAccountLabel;
    @BindView(R.id.btnLogin) Button mBtnLogin;
    @BindView(R.id.container) FrameLayout frameLayout;

    private static final String TAG = "MainActivity";
    private SharedPreferences sharedPreferences;
    private APIInterface mApiInterface;
    private GLOBAL mGlobalInstance;
    private static final int REQUEST_CHECK_SETTINGS = 0x1, PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String SMS_GATEWAY_URL = "http://smsalerts.ozonesms.com/api/otp.php?authkey=192009AIqxtxBZKu5a532922"; // http://cloud.smsozone.com/secure/sendsms.php?authkey=BOMet4TBDw
    private BottomSheetDialogFragment bottomSheetDialogFragment;
    private FragmentTransaction fragmentTransaction;
    private OtpFragment otpFragment;
    private NewPasswordFragment newPasswordFragment;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mApiInterface = APIUtils.getAPIInterface();
        mGlobalInstance = GLOBAL.getInstance();
       // fragmentTransaction = getFragmentManager().beginTransaction();
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mTvAccountLabel.setText(getResources().getString(R.string.account));
        mTvLoginAccountLabel.setText(getResources().getString(R.string.login_account));
        mBtnLogin.setText(getResources().getString(R.string.login_label));

        checkPermissions();

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

    @OnClick(R.id.btnLogin)
    void doLogin() {
        try {
            bottomSheetDialogFragment = new LoginFragment();
            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void openOTPFragment(final String mobileNo) {

        try {
            final int randomNo = generateRandomNumber(1000,9999);
            new SendOTPTask().execute(mobileNo.toString().trim(), String.valueOf(randomNo));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void openNewPasswordFragment() {

        try {
            Log.e(TAG, "In openNewPasswordFragment: ");
            newPasswordFragment = NewPasswordFragment.newInstance("V",sharedPreferences.getString(getResources().getString(R.string.preference_mobile), null));
            fragmentTransaction = getFragmentManager().beginTransaction();
            //fragmentTransaction.addToBackStack(null);
            Fragment otpFragment = getFragmentManager().findFragmentByTag("OTPFragment");
            fragmentTransaction.remove(otpFragment);
            fragmentTransaction.add(R.id.container, newPasswordFragment,"NewPassFragment");
            fragmentTransaction.commit();
            Log.e(TAG, "Out openNewPasswordFragment: ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void phoneNoVerified() {

        // Not Used Here to verify phone no...used in EditVendorDetailsActivity
    }

    @Override
    public void setNewPassword(String type, String mobileNo, String newPassword) {

        try {
            mGlobalInstance.hashmapKeyValue.clear();

            mGlobalInstance.hashmapKeyValue.put("TYPE", type);
            mGlobalInstance.hashmapKeyValue.put("PHONE", mobileNo);
            mGlobalInstance.hashmapKeyValue.put("PASSWORD", newPassword);

            Call<String> call = mApiInterface.setNewPasswordToAccount(mGlobalInstance.getInput(TAG, mGlobalInstance.hashmapKeyValue).toString());

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    try {
                        if (response.isSuccessful()) {

                            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                            Log.e(TAG, "onResponse: Password Change Successfully");
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.password_changed_successfully), Toast.LENGTH_SHORT).show();
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

    }

    private int generateRandomNumber(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;

    }

    class SendOTPTask extends AsyncTask<String, Void, Void> {

        String status, mobileNo, randomNo;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            //super.onPreExecute();

            Log.e("Message", "Inside SendOTPTask onPreExecute()");

            Log.e("Message", "Outside SendOTPTask onPreExecute()");
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                mobileNo = params[0];
                mobileNo = URLEncoder.encode(mobileNo, "UTF-8");

                randomNo = params[1];
                randomNo = URLEncoder.encode(randomNo, "UTF-8");

                //status = mGlobalInstance.makeServiceCall(SMS_GATEWAY_URL + "&to=" + mobileNo + "&message=" + randomNo, GLOBAL.GET);
                //&mobile=919822128000&message=Your%20OTP%20is%20080808&sender=THREES&otp=080808
                status = mGlobalInstance.makeServiceCall(SMS_GATEWAY_URL + "&mobile=91" + mobileNo + "&message=" + randomNo + "&sender=THREES&otp=" + randomNo, GLOBAL.GET);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result)
        {
            // TODO Auto-generated method stub

            try {
                Log.e("Message", "Inside SendOTPTask onPostExecute()");
                Log.e("SMSGateway", "SMSGateway Response : "+status);
                bottomSheetDialogFragment.dismiss();
                otpFragment = OtpFragment.newInstance(mobileNo, String.valueOf(randomNo), "login");
                fragmentTransaction = getFragmentManager().beginTransaction();
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.add(R.id.container, otpFragment,"OTPFragment");
                fragmentTransaction.commit();
                Log.e("Message", "Outside SendOTPTask onPostExecute()");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public String[] getRequiredPermissions() {
        String[] permissions = null;
        try {
            permissions = getPackageManager().getPackageInfo(getPackageName(),
                    PackageManager.GET_PERMISSIONS).requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (permissions == null) {
            return new String[0];
        } else {
            return permissions.clone();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        String[] ungrantedPermissions = requiredPermissionsStillNeeded();
        if (ungrantedPermissions.length == 0) {
            //Toast.makeText(this, "Permissions already granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermissions(ungrantedPermissions, PERMISSION_REQUEST_CODE);
        }
    }

    @TargetApi(23)
    private String[] requiredPermissionsStillNeeded() {

        Set<String> permissions = new HashSet<String>();
        for (String permission : getRequiredPermissions()) {
            permissions.add(permission);
        }
        for (Iterator<String> i = permissions.iterator(); i.hasNext();) {
            String permission = i.next();
            if (ActivityCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_GRANTED) {
                i.remove();
            } else {
            }
        }
        return permissions.toArray(new String[permissions.size()]);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            checkPermissions();
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

    private void displayLocationSettings(Context context) {

        Log.e(TAG, "displayLocationSettings: " );
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
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
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
}
