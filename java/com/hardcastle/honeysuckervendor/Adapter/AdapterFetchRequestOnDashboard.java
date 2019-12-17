package com.hardcastle.honeysuckervendor.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hardcastle.honeysuckervendor.Model.UserRequest;
import com.hardcastle.honeysuckervendor.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by abhijeet on 10/24/2017.
 */

public class AdapterFetchRequestOnDashboard extends RecyclerView.Adapter<AdapterFetchRequestOnDashboard.ViewHolder>{

    private static final String TAG = "FetchRequestOnDashboard";
    private Context mContext;
    private ArrayList<UserRequest.DATum> mDatumList;
    private onUserClickListener mItemListener;
    private final String SEPTIC = "SEPTIC";
    private final String DRAINAGE = "DRAINAGE";
    private final String TOILET = "TOILET";

    public AdapterFetchRequestOnDashboard(Context context, ArrayList<UserRequest.DATum> datumList,  onUserClickListener itemListener) {
        mContext = context;
        mDatumList = datumList;
        mItemListener = itemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_holder_user_request, parent, false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        try {
            holder.tvCostInRupees.setText(mDatumList.get(position).getINFO().getRATE().toString().trim());
            holder.tvName.setText(mDatumList.get(position).getINFO().getUSERNAME().toString().trim());
            holder.tvPhoneNo.setText(mDatumList.get(position).getINFO().getMOBILE().toString().trim());
            holder.tvDate.setText(mDatumList.get(position).getINFO().getSERVICEDATE().toString().trim());
            holder.tvTime.setText(mDatumList.get(position).getINFO().getsERVICETIME().toString().trim());
            holder.btnAccept.setText(mContext.getResources().getString(R.string.accept));
            holder.btnReject.setText(mContext.getResources().getString(R.string.reject));
            holder.btnView.setText(mContext.getResources().getString(R.string.view));

            if (mDatumList.get(position).getSERVICE().getSERVICENAME().contains(SEPTIC)) {
                holder.ivSepticService.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_filled_small_septic_service));
            } else {
                holder.ivSepticService.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_small_septic_service));
            }

            if (mDatumList.get(position).getSERVICE().getSERVICENAME().contains(DRAINAGE)) {
                holder.ivDrainCleaning.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_filled_small_drain_cleanig));
            } else {
                holder.ivDrainCleaning.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_small_drain_cleaning));
            }

            if (mDatumList.get(position).getSERVICE().getSERVICENAME().contains(TOILET)) {
                holder.ivToiletCleaning.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_filled_small_toilet_cleanig));
            } else {
                holder.ivToiletCleaning.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_small_toilet_cleaning));
            }

            holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemListener.onUserRequestClick(view, position, "ACCEPT");
                    //mDatumList.remove(position);
                    //notifyItemRemoved(position);
                }
            });

            holder.btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemListener.onUserRequestClick(view, position, "REJECT");
                    //mDatumList.remove(position);
                    //notifyItemRemoved(position);

                }
            });

            holder.btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemListener.onUserRequestClick(view, position, "VIEW");
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

        @BindView(R.id.tvCostInRupees) TextView tvCostInRupees;
        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.tvPhoneNo) TextView tvPhoneNo;
        @BindView(R.id.tvDate) TextView tvDate;
        @BindView(R.id.tvTime) TextView tvTime;
        @BindView(R.id.btnAccept) Button btnAccept;
        @BindView(R.id.btnReject) Button btnReject;
        @BindView(R.id.btnView) Button btnView;
        @BindView(R.id.ivToiletCleaning) ImageView ivToiletCleaning;
        @BindView(R.id.ivDrainCleaning) ImageView ivDrainCleaning;
        @BindView(R.id.ivSepticService) ImageView ivSepticService;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.e(TAG, "onClick: You Click on "+(getAdapterPosition()+1)+" User request");
                    mItemListener.onUserRequestClick(v,getAdapterPosition());

                }
            });*/

        }

    }

    public interface onUserClickListener
    {
        void onUserRequestClick(View v, int position, String buttonName);
    }

}
