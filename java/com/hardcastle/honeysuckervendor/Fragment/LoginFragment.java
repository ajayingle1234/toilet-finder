package com.hardcastle.honeysuckervendor.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import com.google.gson.Gson;
import com.hardcastle.honeysuckervendor.Activity.Dashboard;
import com.hardcastle.honeysuckervendor.Model.VendorDetails;
import com.hardcastle.honeysuckervendor.R;
import com.hardcastle.honeysuckervendor.Receiver.ConnectivityReceiver;
import com.hardcastle.honeysuckervendor.Utils.APIInterface;
import com.hardcastle.honeysuckervendor.Utils.APIUtils;
import com.hardcastle.honeysuckervendor.Utils.GLOBAL;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends BottomSheetDialogFragment implements ConnectivityReceiver.ConnectivityReceiverListener{

    private static final String TAG = "LoginFragment";
    private RelativeLayout mRlPasswordField;

    @BindView(R.id.edtPhoneNo) EditText mEdtPhoneNo;
    @BindView(R.id.edtPassword) EditText mEdtPassword;
    @BindView(R.id.tvPhoneNoLabel) TextView mTvPhoneNoLabel;
    @BindView(R.id.tvPhoneNo) TextView mTvPhoneNo;
    @BindView(R.id.tvForgot) TextView mTvForgot;
    @BindView(R.id.btnContinue) Button mBtnContinue;
    @BindView(R.id.tilPasswordLabel) TextInputLayout mTILPassword;
    @BindView(R.id.tilPhoneNoLabel) TextInputLayout mTILPhoneNo;

    private APIInterface mApiInterface;
    private GLOBAL mGlobalInstance;
    private SharedPreferences sharedPreferences;
    private OnLoginFragmentInteractionListener mListener;
    private static final int REQUEST_CHECK_SETTINGS = 0x1, PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Log.i(TAG, "onCreateDialog: ");
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onViewCreated(View contentView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(contentView, savedInstanceState);

        Log.i(TAG, "onViewCreated: ");
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {

        super.setupDialog(dialog, style);

        try {
            View contentView = View.inflate(getContext(), R.layout.fragment_login_bottomsheet, null);
            dialog.setContentView(contentView);

            mRlPasswordField = (RelativeLayout) contentView.findViewById(R.id.rlPasswordField);

            ButterKnife.bind(this, contentView);

            Log.i(TAG, "setupDialog: ");

            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
            CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
            if (behavior != null && behavior instanceof BottomSheetBehavior) {
                ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            }

            mApiInterface = APIUtils.getAPIInterface();
            mGlobalInstance = GLOBAL.getInstance();
            sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (context instanceof OnLoginFragmentInteractionListener) {
                mListener = (OnLoginFragmentInteractionListener) context;
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement OnLoginFragmentInteractionListener");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            if (activity instanceof OnLoginFragmentInteractionListener) {
                mListener = (OnLoginFragmentInteractionListener) activity;
            } else {
                throw new RuntimeException(activity.toString()
                        + " must implement OnLanguageFragmentInteractionListener");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Your callback initialization here
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            mTvPhoneNoLabel.setText(getResources().getString(R.string.phone));
            mTvForgot.setText(getResources().getString(R.string.forgot));
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

    @OnTextChanged(value = R.id.edtPhoneNo, callback = OnTextChanged.Callback.BEFORE_TEXT_CHANGED)
    void beforeTextChangedPhoneNo(CharSequence s, int start, int count, int after) {

    }

    @OnTextChanged(value = R.id.edtPassword, callback = OnTextChanged.Callback.BEFORE_TEXT_CHANGED)
    void beforeTextChangedPassword(CharSequence s, int start, int count, int after) {

    }

    @OnTextChanged(value = R.id.edtPhoneNo, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void onTextChangedPhoneNo(CharSequence s, int start, int before, int count) {

        try {
            if (mBtnContinue.getText().toString().equalsIgnoreCase(getContext().getResources().getString(R.string.continue_txt))) {
                if (mEdtPhoneNo.getText().length() == 10) {
                    mBtnContinue.setEnabled(true);
                    mBtnContinue.setBackgroundResource(R.drawable.blue_button);
                } else if (mEdtPhoneNo.getText().length() < 10) {
                    mBtnContinue.setBackgroundResource(R.drawable.blue_button_light);
                    mBtnContinue.setEnabled(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnTextChanged(value = R.id.edtPassword, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void onTextChangedPassword(CharSequence s, int start, int before, int count) {

        try {
            if (mBtnContinue.getText().toString().equalsIgnoreCase(getContext().getResources().getString(R.string.login_label))) {
                if (mEdtPassword.getText().toString().trim().length() > 0) {
                    mBtnContinue.setEnabled(true);
                    mBtnContinue.setBackgroundResource(R.drawable.blue_button);
                } else if (TextUtils.isEmpty(mEdtPassword.getText().toString().trim())) {
                    mBtnContinue.setBackgroundResource(R.drawable.blue_button_light);
                    mBtnContinue.setEnabled(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnTextChanged(value = R.id.edtPhoneNo, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChangedPhoneNo(Editable editable) {

        try {
            if (mBtnContinue.getText().toString().equalsIgnoreCase(getContext().getResources().getString(R.string.login_label))) {
                mBtnContinue.setEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnTextChanged(value = R.id.edtPassword, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChangedPassword(Editable editable) {

    }

    @OnClick(R.id.btnContinue)
    void checkLogin() {

        try {
            if (ConnectivityReceiver.isConnected()) {
                if (mBtnContinue.getText().toString().equalsIgnoreCase(getContext().getResources().getString(R.string.continue_txt))) {
                    checkUser();
                } else if (mBtnContinue.getText().toString().equalsIgnoreCase(getContext().getResources().getString(R.string.login_label))) {
                    if (mEdtPassword.getText().toString().trim().isEmpty()) {
                        Toast.makeText(getActivity(), "Please enter the password", Toast.LENGTH_SHORT).show();
                    } else {
                        doLogin();
                    }

                }
            } else {
                GLOBAL.showMessage(getContext(), getResources().getString(R.string.check_internet_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void checkUser() {

        try {
            mGlobalInstance.hashmapKeyValue.clear();

            mGlobalInstance.hashmapKeyValue.put("TYPE", "V");
            mGlobalInstance.hashmapKeyValue.put("PHONE", mEdtPhoneNo.getText().toString().trim());

            Call<String> call = mApiInterface.checkUserExistence(mGlobalInstance.getInput(TAG, mGlobalInstance.hashmapKeyValue).toString());

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    Log.e(TAG, "onResponse: Response Code : "+response.code());
                    try {
                        if (response.isSuccessful()) {

                            if (response.body().contains("FAILURE")){
                                Toast.makeText(getActivity(), "Mobile number is not registered...contact to admin", Toast.LENGTH_SHORT).show();
                            } else {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(getResources().getString(R.string.preference_mobile), mEdtPhoneNo.getText().toString().trim());
                                editor.apply();
                                mTvPhoneNoLabel.setVisibility(View.VISIBLE);
                                mTvPhoneNo.setVisibility(View.VISIBLE);
                                mTvPhoneNo.setText(mEdtPhoneNo.getText().toString().trim());
                                mTILPhoneNo.setVisibility(View.GONE);
                                mRlPasswordField.setVisibility(View.VISIBLE);
                                mBtnContinue.setBackgroundResource(R.drawable.blue_button_light);
                                mBtnContinue.setText(getContext().getResources().getString(R.string.login_label));

                                displayLocationSettings(getContext());
                            }
                        }
                        else {
                            Log.e(TAG, "User Not Exist");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    Log.e(TAG, "onFailure: " + t.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void doLogin() {

        //try {
            mGlobalInstance.hashmapKeyValue.clear();

            Log.e(TAG, "doLogin: DEVICE TOKEN : "+sharedPreferences.getString(getResources().getString(R.string.preference_device_token), ""));
            mGlobalInstance.hashmapKeyValue.put("TYPE", "V");
            mGlobalInstance.hashmapKeyValue.put("PHONE", mEdtPhoneNo.getText().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("PASSWORD", mEdtPassword.getText().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("DEVICE_ID", sharedPreferences.getString(getResources().getString(R.string.preference_device_token), ""));

            Call<VendorDetails> call = mApiInterface.getUserDetails(mGlobalInstance.getInput(TAG, mGlobalInstance.hashmapKeyValue).toString());

            call.enqueue(new Callback<VendorDetails>() {
                @Override
                public void onResponse(Call<VendorDetails> call, Response<VendorDetails> response) {

                    //try {
                        if (response.isSuccessful()) {

                            if (response.body().getSTATUS() == 1) {
                                VendorDetails vendorDetails = response.body();

                                List<VendorDetails.DATum> datumList = vendorDetails.getDATA();

                                for (VendorDetails.DATum daTum : datumList) {

                                    Log.e(TAG, "onResponse: ID : " + daTum.getID().toString().trim());
                                    Log.e(TAG, "onResponse: NAME : " + daTum.getNAME().toString().trim());
                                    Log.e(TAG, "onResponse: MOBILE : " + daTum.getMOBILE().toString().trim());
                                    Log.e(TAG, "onResponse: EMAIL : " + daTum.getEMAIL().toString().trim());
                                    Log.e(TAG, "onResponse: ADDRESS : " + daTum.getADDRESS().toString().trim());
                                    Log.e(TAG, "onResponse: LATITUDE : " + daTum.getLATITUDE().toString().trim());
                                    Log.e(TAG, "onResponse: LONGITUDE : " + daTum.getLONGITUDE().toString().trim());
                                    Log.e(TAG, "onResponse: ENVIRONMENT CREDITS : " + daTum.getENVIRONMENTCREDITS().toString().trim());

                                    SharedPreferences.Editor editor = sharedPreferences.edit();

                                    Gson gson = new Gson();
                                    String userDetail = gson.toJson(vendorDetails);
                                    editor.putString(getResources().getString(R.string.preference_vendor_info), userDetail);
                                    editor.putBoolean(getResources().getString(R.string.preference_is_login), true);
                                    editor.apply();

                                    Intent intentDashboard = new Intent(getActivity(), Dashboard.class);
                                    startActivity(intentDashboard);
                                    getActivity().finish();
                                }
                            } else {
                                Log.e(TAG, "onResponse: Something went wrong testing");
                                Toast.makeText(getActivity(), "Something went wrong testing", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            Log.e(TAG, "Password is wrong");
                        }
                    /*} catch (Exception e) {
                        e.printStackTrace();
                    }*/

                }

                @Override
                public void onFailure(Call<VendorDetails> call, Throwable t) {

                    Log.e(TAG, "onFailure: " + t.toString());
                }
            });
        /*} catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    // TODO: Rename method, update argument and hook method into UI event
    @OnClick(R.id.tvForgot)
    void forgetPassword() {
        try {
            Log.e(TAG, "forgetPassword: ");
            if (mListener != null) {
                mListener.openOTPFragment(mEdtPhoneNo.getText().toString().trim());
            }
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
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
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

    public interface OnLoginFragmentInteractionListener {
        // TODO: Update argument type and name
        void openOTPFragment(String mobileNo);
    }

}
