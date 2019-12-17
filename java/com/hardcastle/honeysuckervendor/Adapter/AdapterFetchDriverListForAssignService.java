package com.hardcastle.honeysuckervendor.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hardcastle.honeysuckervendor.Model.AvailableDriverDetails;
import com.hardcastle.honeysuckervendor.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abhijeet on 1/11/2018.
 */

public class AdapterFetchDriverListForAssignService extends RecyclerView.Adapter<AdapterFetchDriverListForAssignService.ViewHolder>  {

    private static final String TAG = "FetchDriverList";
    private Context mContext;
    private ArrayList<AvailableDriverDetails.DATum> mlistAvailableDrivers;
    ArrayList<String> mListDistanceInKms;
    private onDriverClickListener mItemListener;

    public AdapterFetchDriverListForAssignService(Context context, ArrayList<AvailableDriverDetails.DATum> datumList, ArrayList<String> listDistanceInKms, onDriverClickListener itemListener) {
        mContext = context;
        mlistAvailableDrivers = datumList;
        mListDistanceInKms = listDistanceInKms;
        mItemListener = itemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_holder_fetch_driver_for_assign_service, parent, false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        try {
            holder.tvDriverNameLabel.setText(mContext.getResources().getString(R.string.name));
            holder.tvPhoneNoLabel.setText(mContext.getResources().getString(R.string.phone));
            holder.tvDriverName.setText(mlistAvailableDrivers.get(position).getDRIVERNAME().toString().trim());
            holder.tvPhoneNo.setText(mlistAvailableDrivers.get(position).getMOBILE().toString().trim());
            holder.tvDistanceInKm.setText(mListDistanceInKms.get(position).toString().trim());

            holder.btnAssign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.e(TAG, "onClick: Position : "+position);
                    Log.e(TAG, "onClick: Driver Name : "+ mlistAvailableDrivers.get((position)).getDRIVERNAME().toString().trim());
                    Log.e(TAG, "onClick: Driver ID : "+ mlistAvailableDrivers.get((position)).getDRIVERID().toString().trim());

                    mItemListener.onDriverClickToAssign(view, mlistAvailableDrivers.get((position)).getDRIVERID().toString().trim());
                    //Toast.makeText(mContext, "Driver Assigned Successfully", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mlistAvailableDrivers.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvDriverNameLabel) TextView tvDriverNameLabel;
        @BindView(R.id.tvDriverName) TextView tvDriverName;
        @BindView(R.id.tvPhoneNoLabel) TextView tvPhoneNoLabel;
        @BindView(R.id.tvDistanceInKm) TextView tvDistanceInKm;
        @BindView(R.id.tvPhoneNo) TextView tvPhoneNo;
        @BindView(R.id.btnAssign) Button btnAssign;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);
        }
    }

    public interface onDriverClickListener
    {
        void onDriverClickToAssign(View v, String driverId);
    }

}
