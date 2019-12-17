package com.hardcastle.honeysuckervendor.Activity;

import android.annotation.TargetApi;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hardcastle.honeysuckervendor.Fragment.OtpFragment;
import com.hardcastle.honeysuckervendor.Model.VendorDetails;
import com.hardcastle.honeysuckervendor.R;
import com.hardcastle.honeysuckervendor.Receiver.ConnectivityReceiver;
import com.hardcastle.honeysuckervendor.Utils.APIInterface;
import com.hardcastle.honeysuckervendor.Utils.APIUtils;
import com.hardcastle.honeysuckervendor.Utils.GLOBAL;
import com.hardcastle.honeysuckervendor.Utils.MyCustomDialog;

import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditVendorDetails extends AppCompatActivity implements OtpFragment.OnOTPFragmentInteractionListener, ConnectivityReceiver.ConnectivityReceiverListener{

    private static final String TAG = "EditVendorDetails";
    private List<VendorDetails.DATum> mListVendorDetails;
    private FragmentTransaction fragmentTransaction;
    private SharedPreferences sharedPreferences;
    private VendorDetails vendorDetails;
    private APIInterface mApiInterface;
    private GLOBAL mGlobalInstance;
    public static final String SMS_GATEWAY_URL = "http://smsalerts.ozonesms.com/api/otp.php?authkey=192009AIqxtxBZKu5a532922"; // http://cloud.smsozone.com/secure/sendsms.php?authkey=BOMet4TBDw
    private static final int PERMISSION_REQUEST_CODE = 1;

    @BindView(R.id.tvUpdateInformation) TextView mTvUpdateInformation;
    @BindView(R.id.tvVerify) TextView mTvVerify;
    @BindView(R.id.btnSubmit) Button mBtnSubmit;
    @BindView(R.id.edtPhoneNo) EditText mEdtPhoneNo;
    @BindView(R.id.edtEmail) EditText mEdtEmail;
    @BindView(R.id.edtName) EditText mEdtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vendor_details);

        try {
            ButterKnife.bind(this);

            mApiInterface = APIUtils.getAPIInterface();
            mGlobalInstance = GLOBAL.getInstance();
            sharedPreferences = getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);

            Gson gson = new Gson();
            String json = sharedPreferences.getString(getResources().getString(R.string.preference_vendor_info), "");
            vendorDetails = gson.fromJson(json, VendorDetails.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            mTvUpdateInformation.setText(getResources().getString(R.string.update_information));
            mTvVerify.setText(getResources().getString(R.string.verified));
            mBtnSubmit.setText(getResources().getString(R.string.submit));

            mEdtPhoneNo.setText(vendorDetails.getDATA().get(0).getMOBILE().toString().trim());
            mEdtEmail.setText(vendorDetails.getDATA().get(0).getEMAIL().toString().trim());
            mEdtName.setText(vendorDetails.getDATA().get(0).getNAME().toString().trim());
            checkPermissions();
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

    @OnClick({R.id.ivBack, R.id.tvVerify, R.id.btnSubmit})
    void onClick(View view) {

        try {
            switch (view.getId()) {

                case R.id.ivBack:
                    finish();
                    break;

                case R.id.tvVerify:
                    if (mEdtPhoneNo.getText().toString().trim().length() == 10) {
                        openOTPFragment(mEdtPhoneNo.getText().toString().trim());
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.phoneno_should_ten_digit), Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.btnSubmit:

                    if (ConnectivityReceiver.isConnected()) {
                        if (checkAllField()) {
                            showDialogBox(getResources().getString(R.string.confirmation), getResources().getString(R.string.confirm_new_information), getResources().getString(R.string.yes), getResources().getString(R.string.no));
                            //submitNewVendorInformation();
                        }
                    } else {
                        GLOBAL.showMessage(getApplicationContext(), getResources().getString(R.string.check_internet_connection));
                    }
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnTextChanged(value = R.id.edtPhoneNo, callback = OnTextChanged.Callback.BEFORE_TEXT_CHANGED)
    void beforeTextChangedPhoneNo(CharSequence s, int start, int count, int after) {
        Log.e(TAG, "beforeTextChangedPhoneNo: PHONE NUMBER");
    }

    @OnTextChanged(value = R.id.edtPhoneNo, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChangedPassword(Editable editable) {

        Log.e(TAG, "afterTextChangedPassword: PHONE NUMBER");
    }

    @OnTextChanged(value = R.id.edtPhoneNo, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void onTextChangedPhoneNo(CharSequence s, int start, int before, int count) {

        try {
            Log.e(TAG, "onTextChangedPhoneNo: PHONE NUMBER");
            if (mEdtPhoneNo.getText().toString().trim().length() < 10) {
                //ClickFlag = true;
                mTvVerify.setText(getResources().getString(R.string.verify));
                mTvVerify.setTextColor(getResources().getColor(R.color.colorOrange));
            }
            if (mEdtPhoneNo.getText().toString().trim().equalsIgnoreCase(vendorDetails.getDATA().get(0).getMOBILE().toString().trim())) {
                mTvVerify.setText(getResources().getString(R.string.verified));
                mTvVerify.setTextColor(Color.GREEN);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showDialogBox(String title, String alertMessage, String btnPositiveName, String btnNegativeName) {

        MyCustomDialog builder = new MyCustomDialog(EditVendorDetails.this, title, alertMessage);
        final AlertDialog dialog = builder.setNegativeButton(btnNegativeName, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }

        }).setPositiveButton(btnPositiveName, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                submitNewVendorInformation();
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
    public void openNewPasswordFragment() {
        // Not Used Here to verify phone no and open NewPasswordFragment...used in MainActivity
    }

    @Override
    public void phoneNoVerified() {

        try {
            mTvVerify.setText(getResources().getString(R.string.verified));
            mTvVerify.setTextColor(getResources().getColor(R.color.colorTextPrimary));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openOTPFragment(String mobileNo) {

        try {
            final int randomNo = generateRandomNumber(1000,9999);
            new SendOTPTask().execute(mobileNo.toString().trim(), String.valueOf(randomNo));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submitNewVendorInformation() {

        try {
            Log.e(TAG, "submitNewVendorInformation: ");

            mGlobalInstance.hashmapKeyValue.clear();

            mGlobalInstance.hashmapKeyValue.put("TYPE", "V");
            mGlobalInstance.hashmapKeyValue.put("UID", vendorDetails.getDATA().get(0).getID().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("MOBILE", mEdtPhoneNo.getText().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("EMAIL_ID", mEdtEmail.getText().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("NAME", mEdtName.getText().toString().trim());

            Call<VendorDetails> call = mApiInterface.updateVendorInformation(mGlobalInstance.getInput(TAG, mGlobalInstance.hashmapKeyValue).toString());

            call.enqueue(new Callback<VendorDetails>() {
                @Override
                public void onResponse(Call<VendorDetails> call, Response<VendorDetails> response) {

                    Log.e(TAG, "onResponse: ");

                    try {
                        if (response.isSuccessful()) {

                            Log.e(TAG, "onResponse: successfull");

                            mListVendorDetails = response.body().getDATA();
                            vendorDetails = response.body();
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
                            String userDetail = gson.toJson(vendorDetails);
                            editor.putString(getResources().getString(R.string.preference_vendor_info), userDetail);
                            editor.apply();

                            Intent intent = new Intent();
                            intent.putExtra("UPDATED_INFO",userDetail);
                            setResult(RESULT_OK, intent);
                            finish();
                            //}
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<VendorDetails> call, Throwable t) {

                    Log.e(TAG, "onFailure: " + t.toString());
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
                OtpFragment otpFragment = OtpFragment.newInstance(mobileNo, String.valueOf(randomNo), "edit");
                fragmentTransaction = getFragmentManager().beginTransaction();
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.add(R.id.container, otpFragment, "OTPFragment");
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

    private boolean checkAllField() {

        try {
            Log.e(TAG, "checkAllField: ");
            if (mEdtName.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "" + getResources().getString(R.string.name_required_alert), Toast.LENGTH_SHORT).show();
                return false;
            } else if (mEdtEmail.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "" + getResources().getString(R.string.email_required_alert), Toast.LENGTH_SHORT).show();
                return false;
            } else if (!isValidEmail(mEdtEmail.getText().toString().trim()) ) {
                Toast.makeText(this, ""+getResources().getString(R.string.check_valid_emailid), Toast.LENGTH_SHORT).show();
                return false;
            }else if (mEdtPhoneNo.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "" + getResources().getString(R.string.phone_required_alert), Toast.LENGTH_SHORT).show();
                return false;
            }else if (mEdtPhoneNo.getText().toString().trim().length() != 10) {
                Toast.makeText(this, "" + getResources().getString(R.string.phoneno_should_ten_digit), Toast.LENGTH_SHORT).show();
                return false;
            } else if (mTvVerify.getText().toString().trim().equalsIgnoreCase(getResources().getString(R.string.verify))) {
                Toast.makeText(this, "" + getResources().getString(R.string.mobile_no_verification_alert), Toast.LENGTH_SHORT).show();
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;

    }

    public static boolean isValidEmail(String emailAddress)
    {
        String emailRegEx;
        Pattern pattern;
        // Regex for a valid email address
        emailRegEx = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$";
        // Compare the regex with the email address
        pattern = Pattern.compile(emailRegEx);
        Matcher matcher = pattern.matcher(emailAddress);
        if (!matcher.find()) {
            return false;
        }
        return true;
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
