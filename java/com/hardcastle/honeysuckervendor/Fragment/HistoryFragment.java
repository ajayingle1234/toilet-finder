package com.hardcastle.honeysuckervendor.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hardcastle.honeysuckervendor.Adapter.AdapterFoldingCellList;
import com.hardcastle.honeysuckervendor.Model.HistoryModel;
import com.hardcastle.honeysuckervendor.Model.VendorDetails;
import com.hardcastle.honeysuckervendor.R;
import com.hardcastle.honeysuckervendor.Receiver.ConnectivityReceiver;
import com.hardcastle.honeysuckervendor.Utils.APIInterface;
import com.hardcastle.honeysuckervendor.Utils.APIUtils;
import com.hardcastle.honeysuckervendor.Utils.GLOBAL;
import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener{

    // TODO: Rename parameter arguments, choose names that match
    private static final String TAG = "HistoryFragment";

    @BindView(R.id.tvHistoryLabel) TextView mTvHistoryLabel;
    @BindView(R.id.tvNoServiceAvailable) TextView mTvNoServiceAvailable;
    @BindView(R.id.mainListView) ListView mListView;

    private APIInterface mApiInterface;
    private GLOBAL mGlobalInstance;
    private SharedPreferences sharedPreferences;
    private ArrayList<HistoryModel.DATum> mListHistory;
    private VendorDetails vendorDetails;

    public HistoryFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        try {
            ButterKnife.bind(this,view);

            mApiInterface = APIUtils.getAPIInterface();
            mGlobalInstance = GLOBAL.getInstance();
            sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);

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
            if (ConnectivityReceiver.isConnected()) {
                fetchRequest();
            } else {
                GLOBAL.showMessage(getContext(), getResources().getString(R.string.check_internet_connection));
            }

            mTvHistoryLabel.setText(getResources().getString(R.string.title_history));
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

    void fetchRequest() {

        try {
            mGlobalInstance.hashmapKeyValue.clear();

            Log.e(TAG, "fetchRequest: VENDOR_ID : "+ vendorDetails.getDATA().get(0).getID().toString().trim());
            mGlobalInstance.hashmapKeyValue.put("VENDOR_ID", vendorDetails.getDATA().get(0).getID().toString().trim());

            Call<HistoryModel> call = mApiInterface.getHistory(mGlobalInstance.getInput(TAG, mGlobalInstance.hashmapKeyValue).toString());

            call.enqueue(new Callback<HistoryModel>() {
                @Override
                public void onResponse(Call<HistoryModel> call, Response<HistoryModel> response) {

                    try {
                        if (response.isSuccessful()) {

                            mListHistory = response.body().getDATA();

                            if (response.body().getDATA().isEmpty()) {
                                Log.e(TAG, "onResponse: No Service Available");
                                mListView.setVisibility(View.GONE);
                                mTvNoServiceAvailable.setVisibility(View.VISIBLE);
                                mTvNoServiceAvailable.setText(getResources().getString(R.string.no_service_available));
                            } else {
                                Log.e(TAG, "onResponse: No. of Request :"+ mListHistory.size());
                                mListView.setVisibility(View.VISIBLE);
                                mTvNoServiceAvailable.setVisibility(View.GONE);
                                setAdapter(getContext(), mListHistory);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<HistoryModel> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setAdapter(Context context, ArrayList<HistoryModel.DATum> datumList) {

        try {
            final AdapterFoldingCellList adapter = new AdapterFoldingCellList(context,datumList);
            mListView.setAdapter(adapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                    // toggle clicked cell state
                    ((FoldingCell) view).toggle(false);
                    // register in adapter that state for selected cell is toggled
                    adapter.registerToggle(pos);
                }
            });
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
