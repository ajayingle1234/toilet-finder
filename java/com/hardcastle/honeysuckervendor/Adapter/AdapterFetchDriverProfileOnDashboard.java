package com.hardcastle.honeysuckervendor.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hardcastle.honeysuckervendor.Model.DriverProfile;
import com.hardcastle.honeysuckervendor.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abhijeet on 10/30/2017.
 */

public class AdapterFetchDriverProfileOnDashboard extends RecyclerView.Adapter<AdapterFetchDriverProfileOnDashboard.ViewHolder> {

    private static final String TAG = "FetchDriverProfile";
    private Context mContext;
    private ArrayList<DriverProfile.DATum> mDatumList;
    private onDriverClickListener mItemListener;

    public AdapterFetchDriverProfileOnDashboard(Context context, ArrayList<DriverProfile.DATum> datumList, onDriverClickListener itemListener) {
        mContext = context;
        mDatumList = datumList;
        mItemListener = itemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_holder_fetch_driver_profile, parent, false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        try {
            holder.tvDriverNameLabel.setText(mContext.getResources().getString(R.string.name));
            holder.tvPhoneNoLabel.setText(mContext.getResources().getString(R.string.phone));
            holder.tvDriverName.setText(mDatumList.get(position).getdRIVERNAME().toString().trim());
            holder.tvPhoneNo.setText(mDatumList.get(position).getmOBILE().toString().trim());

            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.e(TAG, "onClick: Position to delete : "+position);
                    Log.e(TAG, "onClick: Driver Name : "+mDatumList.get((position)).getdRIVERNAME().toString().trim());
                    Log.e(TAG, "onClick: Driver ID : "+mDatumList.get((position)).getdRIVERID().toString().trim());

                    mItemListener.onDriverClickToDelete(view, mDatumList.get((position)).getdRIVERID().toString().trim());
                    //mDatumList.remove(position);
                    //notifyItemRemoved(position);
                    //Toast.makeText(mContext, "Driver Deleted Successfully", Toast.LENGTH_SHORT).show();
                }
            });

            holder.ivCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.e(TAG, "onClick: Position to Call : "+position);
                    Log.e(TAG, "onClick: Driver Name : "+mDatumList.get((position)).getdRIVERNAME().toString().trim());
                    Log.e(TAG, "onClick: Driver ID : "+mDatumList.get((position)).getdRIVERID().toString().trim());
                    Log.e(TAG, "onClick: Driver Mobile No : "+mDatumList.get((position)).getmOBILE().toString().trim());

                    mItemListener.onDriverClickToCall(view, mDatumList.get((position)).getmOBILE().toString().trim());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mDatumList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvDriverNameLabel) TextView tvDriverNameLabel;
        @BindView(R.id.tvDriverName) TextView tvDriverName;
        @BindView(R.id.tvPhoneNoLabel) TextView tvPhoneNoLabel;
        @BindView(R.id.tvPhoneNo) TextView tvPhoneNo;
        @BindView(R.id.ivCall) ImageView ivCall;
        @BindView(R.id.ivDelete) ImageView ivDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.e(TAG, "onClick: You Click on "+(getAdapterPosition()+1)+" User request");
                    mItemListener.onDriverClickToUpdate(v,getAdapterPosition());

                }
            });
        }
    }

    public interface onDriverClickListener
    {
        void onDriverClickToDelete(View v, String driverId);
        void onDriverClickToUpdate(View v, int position);
        void onDriverClickToCall(View v, String mobileNo);
    }

}
