package com.hardcastle.honeysuckervendor.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hardcastle.honeysuckervendor.Model.DriverProfile;
import com.hardcastle.honeysuckervendor.Model.VendorDetails;
import com.hardcastle.honeysuckervendor.R;
import com.hardcastle.honeysuckervendor.Receiver.ConnectivityReceiver;
import com.hardcastle.honeysuckervendor.Utils.APIInterface;
import com.hardcastle.honeysuckervendor.Utils.APIUtils;
import com.hardcastle.honeysuckervendor.Utils.GLOBAL;
import com.hardcastle.honeysuckervendor.Utils.HexagonMaskView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddModifyDriver extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    private static final String TAG = "AddModifyDriver";
    private static final int REQUEST_CAMERA = 11;
    private DriverProfile.DATum mDriverProfile;
    private String KEY;
    private APIInterface mApiInterface;
    private GLOBAL mGlobalInstance;
    private SharedPreferences sharedPreferences;
    private static Bitmap bmp;
    private String driverPhoto_base64, driverPhotoPath;
    private FileOutputStream fo;
    private ByteArrayOutputStream bytes;

    @BindView(R.id.ivDriverPhoto)HexagonMaskView mIvDriverPhoto;
    @BindView(R.id.tvNameLabel) TextView mTvNameLabel;
    @BindView(R.id.edtName) EditText mEdtName;
    @BindView(R.id.tvAddressLabel) TextView mTvAddressLabel;
    @BindView(R.id.edtAddress) EditText mEdtAddress;
    @BindView(R.id.tvEmailLabel) TextView mTvEmailLabel;
    @BindView(R.id.edtEmail) EditText mEdtEmail;
    @BindView(R.id.tvPhoneNoLabel) TextView mTvPhoneNoLabel;
    @BindView(R.id.edtPhoneNo) EditText mEdtPhoneNo;
    @BindView(R.id.tvVehicleNoLabel) TextView mTvVehicleNoLabel;
    @BindView(R.id.edtVehicleNo) EditText mEdtVehicleNo;
    @BindView(R.id.tvTankerCapacityLabel) TextView mTvTankerCapacityLabel;
    @BindView(R.id.edtTankerCapacity) EditText mEdtTankerCapacity;
    @BindView(R.id.tvlicenceLabel) TextView mTvlicenceLabel;
    @BindView(R.id.edtlicenceNo) EditText mEdtlicenceNo;
    @BindView(R.id.llPassword) LinearLayout mLLPassword;
    @BindView(R.id.btnSubmit) Button mBtnSubmit;
    @Nullable @BindView (R.id.tvPasswordLabel) TextView mTvPasswordLabel;
    @Nullable @BindView(R.id.edtPassword) EditText mEdtPassword;
    @BindView(R.id.tvEdit) TextView mTvEdit;
    @BindView(R.id.tvDriverProfile) TextView mTvDriverProfile;
    @BindViews({R.id.edtName,R.id.edtAddress,R.id.edtPhoneNo,R.id.edtEmail, R.id.edtVehicleNo, R.id.edtTankerCapacity, R.id.edtlicenceNo}) List<EditText> listEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_modify_driver);

        try {
            ButterKnife.bind(this);

            driverPhotoPath = Environment.getExternalStorageDirectory().toString() + "/HONEY SUCKER";
            File folder = new File(driverPhotoPath);
            bytes = new ByteArrayOutputStream();
            if (!folder.exists())
                folder.mkdirs();

            if (getIntent().getStringExtra("KEY").equalsIgnoreCase("MOD")) {
                KEY = getIntent().getStringExtra("KEY").toString().trim();
                mDriverProfile = getIntent().getExtras().getParcelable("OBJECT");
                driverPhoto_base64 = mDriverProfile.getdRIVERPHOTO().toString().trim();
                decodeBase64(driverPhoto_base64);
            }else {
                KEY = getIntent().getStringExtra("KEY").toString().trim();
                driverPhoto_base64 = "";
            }

            //driverPhoto_base64 = "";
            mApiInterface = APIUtils.getAPIInterface();
            mGlobalInstance = GLOBAL.getInstance();
            sharedPreferences = getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            mTvNameLabel.setText(getResources().getString(R.string.name));
            mTvAddressLabel.setText(getResources().getString(R.string.address));
            mTvEmailLabel.setText(getResources().getString(R.string.email));
            mTvPhoneNoLabel.setText(getResources().getString(R.string.phone));
            mTvPasswordLabel.setText(getResources().getString(R.string.password));
            mBtnSubmit.setText(getResources().getString(R.string.submit));

            if (KEY.equalsIgnoreCase("ADD")) {
                mTvEdit.setVisibility(View.GONE);
                mTvDriverProfile.setText(getResources().getString(R.string.add_driver));
                mLLPassword.setVisibility(View.VISIBLE);

            } else if (KEY.equalsIgnoreCase("MOD")) {
                mTvEdit.setVisibility(View.VISIBLE);
                mBtnSubmit.setVisibility(View.GONE);
                mLLPassword.setVisibility(View.GONE);
                mTvDriverProfile.setText(getResources().getString(R.string.driver_profile));
                mTvEdit.setText(getResources().getString(R.string.edit));
                mEdtName.setText(mDriverProfile.getdRIVERNAME().toString().trim());
                mEdtAddress.setText(mDriverProfile.getaDDRESS().toString().trim());
                mEdtEmail.setText(mDriverProfile.geteMAILID().toString().trim());
                mEdtPhoneNo.setText(mDriverProfile.getmOBILE().toString().trim());
                mEdtVehicleNo.setText(mDriverProfile.getvEHICLE_NUMBER().toString().trim());
                mEdtlicenceNo.setText(mDriverProfile.getdRIVER_LICENCE_NUMBER().toString().trim());
                mEdtTankerCapacity.setText(mDriverProfile.getvEHICLE_TANK_CAPACITY().toString().trim());
                //decodeBase64(driverPhoto_base64);
                ButterKnife.apply(listEditText, DISABLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        GLOBAL.getInstance().setConnectivityListener(this);
    }

    public void decodeBase64(String input)
    {
        try {
            byte[] decodedBytes = Base64.decode(input, 0);
            mIvDriverPhoto.setImageBitmap(BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnClick({R.id.tvEdit,R.id.btnSubmit,R.id.ivDriverPhoto})
    void editInformation(View view){

        try {
            switch (view.getId()) {

                case R.id.tvEdit :
                    ButterKnife.apply(listEditText, ENABLE);
                    mBtnSubmit.setVisibility(View.VISIBLE);
                    Toast.makeText(this, getResources().getString(R.string.edit_the_information), Toast.LENGTH_SHORT).show();
                    break;

                case R.id.btnSubmit :

                    if (checkAllField()) {
                        if (ConnectivityReceiver.isConnected()) {
                            if (KEY.equalsIgnoreCase("ADD")) {
                                addDriverInformation();

                            } else if (KEY.equalsIgnoreCase("MOD")) {
                                modifyDriverInformation();
                            }
                        } else {
                            GLOBAL.showMessage(getApplicationContext(), getResources().getString(R.string.check_internet_connection));
                        }
                    }
                    break;

                case R.id.ivDriverPhoto :
                    Intent intentTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intentTakePhoto, REQUEST_CAMERA);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void modifyDriverInformation() {

        mGlobalInstance.hashmapKeyValue.clear();

        try {
            Gson gson = new Gson();
            String json = sharedPreferences.getString(getResources().getString(R.string.preference_vendor_info), "");
            VendorDetails vendorDetails = gson.fromJson(json, VendorDetails.class);

            Log.e(TAG, "deleteDriver: VENDOR_ID : "+ vendorDetails.getDATA().get(0).getID().toString().trim());
            Log.e(TAG, "deleteDriver: DRIVER_ID : "+ mDriverProfile.getdRIVERID().toString().trim());

            mGlobalInstance.hashmapKeyValue.put("CONDITION", "MOD");
            mGlobalInstance.hashmapKeyValue.put("VENDOR_ID", vendorDetails.getDATA().get(0).getID().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("DRIVER_ID", mDriverProfile.getdRIVERID().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("DRIVER_PHOTO", driverPhoto_base64.toString().trim());
            mGlobalInstance.hashmapKeyValue.put("NAME", mEdtName.getText().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("MOBILE", mEdtPhoneNo.getText().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("EMAIL_ID", mEdtEmail.getText().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("ADDRESS", mEdtAddress.getText().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("VEHICLE_NUMBER", mEdtVehicleNo.getText().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("DRIVER_LICENCE_NUMBER", mEdtlicenceNo.getText().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("VEHICLE_TANK_CAPACITY", mEdtTankerCapacity.getText().toString().trim());

            Call<String> call = mApiInterface.deleteAndUpdateDriver(mGlobalInstance.getInput(TAG, mGlobalInstance.hashmapKeyValue).toString());

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if (response.isSuccessful()) {

                        if (response.body().contains("SUCCESS")) {
                            Log.e(TAG, "onResponse: Driver Information Updated Successfully");
                            String imageName = mEdtName.getText().toString().trim()+"_"+mEdtlicenceNo.getText().toString().trim() +".jpg";
                            Log.e("Take Image", "" + imageName);

                            driverPhotoPath = driverPhotoPath + "/" + imageName;

                            try {
                                fo = new FileOutputStream(driverPhotoPath);
                                fo.write(bytes.toByteArray());
                                fo.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(AddModifyDriver.this, getResources().getString(R.string.driver_info_updated_successfully), Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent();
                            setResult(2,intent);
                            finish();
                        } else {
                            Toast.makeText(AddModifyDriver.this, getResources().getString(R.string.mobile_no_already_exist), Toast.LENGTH_SHORT).show();

                        }

                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(AddModifyDriver.this, getResources().getString(R.string.something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void addDriverInformation() {

        mGlobalInstance.hashmapKeyValue.clear();

        try {
            Gson gson = new Gson();
            String json = sharedPreferences.getString(getResources().getString(R.string.preference_vendor_info), "");
            VendorDetails vendorDetails = gson.fromJson(json, VendorDetails.class);

            Log.e(TAG, "deleteDriver: VENDOR_ID : "+ vendorDetails.getDATA().get(0).getID().toString().trim());

            mGlobalInstance.hashmapKeyValue.put("VENDOR_ID", vendorDetails.getDATA().get(0).getID().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("TYPE", "D");
            mGlobalInstance.hashmapKeyValue.put("DRIVER_PHOTO", driverPhoto_base64.toString().trim());
            mGlobalInstance.hashmapKeyValue.put("NAME", mEdtName.getText().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("MOBILE", mEdtPhoneNo.getText().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("EMAIL_ID", mEdtEmail.getText().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("PWD", mEdtPassword.getText().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("ADDRESS", mEdtAddress.getText().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("VEHICLE_NUMBER", mEdtVehicleNo.getText().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("DRIVER_LICENCE_NUMBER", mEdtlicenceNo.getText().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("VEHICLE_TANK_CAPACITY", mEdtTankerCapacity.getText().toString().trim());

            Call<String> call = mApiInterface.addDriver(mGlobalInstance.getInput(TAG, mGlobalInstance.hashmapKeyValue).toString());

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if (response.isSuccessful()) {

                        if (response.body().contains("SUCCESS")) {
                            Log.e(TAG, "onResponse: New Driver Added Successfully");

                            String imageName = mEdtName.getText().toString().trim()+"_"+mEdtlicenceNo.getText().toString().trim() +".jpg";
                            Log.e("Take Image", "" + imageName);

                            driverPhotoPath = driverPhotoPath + "/" + imageName;

                            try {
                                fo = new FileOutputStream(driverPhotoPath);
                                fo.write(bytes.toByteArray());
                                fo.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(AddModifyDriver.this, getResources().getString(R.string.new_driver_added_successfully), Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent();
                            setResult(1,intent);
                            finish();
                        } else {
                            Toast.makeText(AddModifyDriver.this, getResources().getString(R.string.mobile_no_already_exist), Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(AddModifyDriver.this, getResources().getString(R.string.something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkAllField() {

        try {

            if (mEdtName.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, ""+getResources().getString(R.string.name_required_alert), Toast.LENGTH_SHORT).show();
                return false;
            } else if (mEdtAddress.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, ""+getResources().getString(R.string.address_required_alert), Toast.LENGTH_SHORT).show();
                return false;
            }  else if (mEdtEmail.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, ""+getResources().getString(R.string.email_required_alert), Toast.LENGTH_SHORT).show();
                return false;
            }  else if (mEdtPhoneNo.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, ""+getResources().getString(R.string.phone_required_alert), Toast.LENGTH_SHORT).show();
                return false;
            } else if (!isValidEmail(mEdtEmail.getText().toString().trim()) ) {
                Toast.makeText(this, ""+getResources().getString(R.string.check_valid_emailid), Toast.LENGTH_SHORT).show();
                return false;
            }else if (mEdtPhoneNo.getText().toString().trim().length() < 10 ) {
                Toast.makeText(this, ""+getResources().getString(R.string.phoneno_should_ten_digit), Toast.LENGTH_SHORT).show();
                return false;
            } else if (mEdtVehicleNo.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, ""+getResources().getString(R.string.vehicle_no_required_alert), Toast.LENGTH_SHORT).show();
                return false;
            } else if (mEdtVehicleNo.getText().toString().trim().length() < 8) {
                Toast.makeText(this, ""+getResources().getString(R.string.vehicle_no_validation_alert), Toast.LENGTH_SHORT).show();
                return false;
            }else if (mEdtTankerCapacity.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, ""+getResources().getString(R.string.tanker_capacity_required_alert), Toast.LENGTH_SHORT).show();
                return false;
            } else if (mEdtlicenceNo.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, ""+getResources().getString(R.string.licence_no_required_alert), Toast.LENGTH_SHORT).show();
                return false;
            } else if (mEdtlicenceNo.getText().toString().trim().length() != 15) {
                Toast.makeText(this, ""+getResources().getString(R.string.licence_no_validation_alert), Toast.LENGTH_SHORT).show();
                return false;
            }else if (driverPhoto_base64.toString().trim().isEmpty()) {
                Toast.makeText(this, ""+getResources().getString(R.string.driver_photo_required_alert), Toast.LENGTH_SHORT).show();
                return false;
            }

            if (KEY.equalsIgnoreCase("ADD")) {
                if (mEdtPassword.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, ""+getResources().getString(R.string.password_required_alert), Toast.LENGTH_SHORT).show();
                    return false;
                } else if (mEdtPassword.getText().toString().trim().length() < 6) {
                    Toast.makeText(this, ""+getResources().getString(R.string.password_validation_alert), Toast.LENGTH_SHORT).show();
                    return false;
                } else
                    return true;
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

    final ButterKnife.Action<View> DISABLE = new ButterKnife.Action<View>() {
        @Override public void apply(View view, int index) {
            view.setEnabled(false);
        }
    };

    final ButterKnife.Action<View> ENABLE = new ButterKnife.Action<View>() {
        @Override public void apply(View view, int index) {
            view.setEnabled(true);
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        if (isConnected) {
            GLOBAL.showMessage(getApplicationContext(), getResources().getString(R.string.internet_available));
        } else {
            GLOBAL.showMessage(getApplicationContext(), getResources().getString(R.string.check_internet_connection));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }
        }
    }

    private void onCaptureImageResult(Intent data) {

        try {

            bmp = (Bitmap) data.getExtras().get("data");
            //bytes = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] b = bytes.toByteArray();
            driverPhoto_base64 = Base64.encodeToString(b, Base64.DEFAULT);
            Log.e("TESTING", "onCaptureImageResult, Image Base 64 string : "+driverPhoto_base64);
            mIvDriverPhoto.setImageBitmap(bmp);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
