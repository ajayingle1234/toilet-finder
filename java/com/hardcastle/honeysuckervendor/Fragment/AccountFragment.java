package com.hardcastle.honeysuckervendor.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hardcastle.honeysuckervendor.Activity.BaseActivity;
import com.hardcastle.honeysuckervendor.Activity.EditVendorDetails;
import com.hardcastle.honeysuckervendor.Activity.MainActivity;
import com.hardcastle.honeysuckervendor.Activity.SlidingPanelDemoActivity;
import com.hardcastle.honeysuckervendor.Adapter.AdapterCustomExpandableList;
import com.hardcastle.honeysuckervendor.Model.VendorDetails;
import com.hardcastle.honeysuckervendor.R;
import com.hardcastle.honeysuckervendor.Receiver.ConnectivityReceiver;
import com.hardcastle.honeysuckervendor.Utils.APIInterface;
import com.hardcastle.honeysuckervendor.Utils.APIUtils;
import com.hardcastle.honeysuckervendor.Utils.GLOBAL;
import com.hardcastle.honeysuckervendor.Utils.MyCustomDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountFragment.OnLanguageFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment implements ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener,
                                                         ExpandableListView.OnGroupExpandListener, ConnectivityReceiver.ConnectivityReceiverListener{

    private static final String TAG = "AccountFragment";
    private static final int REQUEST_CODE_EDIT = 1;
    private VendorDetails vendorDetails;
    private SharedPreferences sharedPreferences;
    private int lastExpandedPosition = -1;
    private AdapterCustomExpandableList mExpandableListAdapter;
    private List<String> mListDataHeader;
    private HashMap<String, List<String>> mHashmapDataChild;
    private APIInterface mApiInterface;
    private GLOBAL mGlobalInstance;

    @BindView(R.id.tvName) TextView mTvName;
    @BindView(R.id.tvPhoneNo) TextView mTvPhoneNo;
    @BindView(R.id.tvEmail) TextView mTvEmail;
    @BindView(R.id.tvEdit) TextView mTvEdit;
    @BindView(R.id.tvEnvironmentalCreditsLabel) TextView mTvEnvironmentalCreditsLabel;
    @BindView(R.id.tvEnvironmentalCredits) TextView mTvEnvironmentalCredits;
    @BindView(R.id.tvLogoutLabel) TextView mTvLogoutLabel;
    @BindView(R.id.elvHeaderInfo) ExpandableListView mExpandableListView;

    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);
        try {
            ButterKnife.bind(this,view);

            prepareList();

            mApiInterface = APIUtils.getAPIInterface();
            mGlobalInstance = GLOBAL.getInstance();

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

        try {
            Log.e(TAG, "onStart: ");

            mExpandableListAdapter = new AdapterCustomExpandableList(getContext(), mListDataHeader, mHashmapDataChild);
            mExpandableListView.setAdapter(mExpandableListAdapter);
            mExpandableListAdapter.notifyDataSetChanged();

            mExpandableListView.setOnGroupExpandListener(this);
            mExpandableListView.setOnGroupClickListener(this);
            mExpandableListView.setOnChildClickListener(this);

            mTvName.setText(vendorDetails.getDATA().get(0).getNAME().toString().trim());
            mTvPhoneNo.setText(vendorDetails.getDATA().get(0).getMOBILE().toString().trim());
            mTvEmail.setText(vendorDetails.getDATA().get(0).getEMAIL().toString().trim());
            mTvEnvironmentalCredits.setText(vendorDetails.getDATA().get(0).getENVIRONMENTCREDITS().toString().trim());
            mTvEnvironmentalCreditsLabel.setText(getResources().getString(R.string.environment_credits));
            mTvEdit.setText(getResources().getString(R.string.edit));
            mTvLogoutLabel.setText(getResources().getString(R.string.logout));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // register connection status listener
        GLOBAL.getInstance().setConnectivityListener(this);
    }

    @OnClick(R.id.tvEdit)
    void updateInformation() {

        try {
            Intent intentEditVendorDetails = new Intent(getActivity(), EditVendorDetails.class);
            startActivityForResult(intentEditVendorDetails, REQUEST_CODE_EDIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (resultCode == Activity.RESULT_OK) {

                Log.e(TAG, "onActivityResult: ");
                Gson gson = new Gson();
                String json = data.getStringExtra("UPDATED_INFO");
                vendorDetails = gson.fromJson(json, VendorDetails.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.ivLogout, R.id.tvLogoutLabel, R.id.rlLogout})
    void logOut() {
        showDialogBox(getResources().getString(R.string.confirmation), getResources().getString(R.string.confirm_logout), getResources().getString(R.string.yes), getResources().getString(R.string.no));
    }

    public void logoutUser() {

        try {
            Log.e(TAG, "submitNewVendorInformation: ");

            mGlobalInstance.hashmapKeyValue.clear();

            mGlobalInstance.hashmapKeyValue.put("TYPE", "V");
            mGlobalInstance.hashmapKeyValue.put("UID", vendorDetails.getDATA().get(0).getID().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("LOGIN_STATUS", "0");

            Call<VendorDetails> call = mApiInterface.updateVendorInformation(mGlobalInstance.getInput(TAG, mGlobalInstance.hashmapKeyValue).toString());

            call.enqueue(new Callback<VendorDetails>() {
                @Override
                public void onResponse(Call<VendorDetails> call, Response<VendorDetails> response) {

                    Log.e(TAG, "onResponse: ");

                    try {
                        if (response.isSuccessful()) {

                            Log.e(TAG, "onResponse: successfull");

                            // After logout redirect user to Loing Activity
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            // Closing all the Activities
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NO_HISTORY);

                            /*// Add new Flag to start new Activity
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/

                            // Staring Login Activity
                            startActivity(intent);

                            getActivity().finish();

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

    public void showDialogBox(String title, String alertMessage, String btnPositiveName, String btnNegativeName) {

        MyCustomDialog builder = new MyCustomDialog(getActivity(), title, alertMessage);
        final AlertDialog dialog = builder.setNegativeButton(btnNegativeName, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }

        }).setPositiveButton(btnPositiveName, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getResources().getString(R.string.preference_is_login), false);
                editor.remove(getResources().getString(R.string.preference_vendor_info));
                editor.apply();

                logoutUser();

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
    public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {

        try {
            if (groupPosition == 0) {

                if (childPosition == 0) {
                        Intent intent = new Intent(getActivity(), SlidingPanelDemoActivity.class);
                        startActivity(intent);
                }

                if (childPosition == 1) {
                    Intent intent = new Intent(getActivity(), BaseActivity.class);
                    intent.putExtra("FRAGMENT_NAME","LANGUAGE");
                    startActivity(intent);
                }
            }

            if (groupPosition == 2) {

                if (childPosition == 0) {
                    Intent intent = new Intent(getActivity(), BaseActivity.class);
                    intent.putExtra("FRAGMENT_NAME","FAQ");
                    startActivity(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {

        try {
            if (groupPosition == 1) {
                Intent intent = new Intent(getActivity(), BaseActivity.class);
                intent.putExtra("FRAGMENT_NAME","ABOUT_US");
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void onGroupExpand(int groupPosition) {

        try {
            if (lastExpandedPosition != -1
                    && groupPosition != lastExpandedPosition) {
                mExpandableListView.collapseGroup(lastExpandedPosition);
            }
            lastExpandedPosition = groupPosition;
            mExpandableListAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareList() {

        try {
            mListDataHeader = new ArrayList<String>();
            mHashmapDataChild = new HashMap<String, List<String>>();

            //Adding Title to List
            mListDataHeader.add(getResources().getString(R.string.my_account));// Account
            mListDataHeader.add(getResources().getString(R.string.about_us));// About us
            mListDataHeader.add(getResources().getString(R.string.help));// Help

            //Adding Child data
            List<String> arrayListAboutUs = new ArrayList<String>();

            List<String> arrayListAccount = new ArrayList<String>();
            arrayListAccount.add(getResources().getString(R.string.manage_address));
            arrayListAccount.add(getResources().getString(R.string.language_label));

            List<String> arrayListHelp = new ArrayList<String>();
            arrayListHelp.add(getResources().getString(R.string.faqs));

            //Add Child According to Title
            mHashmapDataChild.put(mListDataHeader.get(0), arrayListAccount);
            mHashmapDataChild.put(mListDataHeader.get(1), arrayListAboutUs);
            mHashmapDataChild.put(mListDataHeader.get(2), arrayListHelp);
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
