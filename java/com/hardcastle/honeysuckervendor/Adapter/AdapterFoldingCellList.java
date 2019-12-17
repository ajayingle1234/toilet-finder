package com.hardcastle.honeysuckervendor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hardcastle.honeysuckervendor.Model.HistoryModel;
import com.hardcastle.honeysuckervendor.R;
import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;
import java.util.HashSet;

public class AdapterFoldingCellList extends ArrayAdapter<HistoryModel.DATum> {

    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private View.OnClickListener defaultRequestBtnClickListener;
    private final String SEPTIC = "SEPTIC";
    private final String DRAINAGE = "DRAINAGE";
    private final String TOILET = "TOILET";

    public AdapterFoldingCellList(Context context, ArrayList<HistoryModel.DATum> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FoldingCell cell = null;
        try {
            // get item for selected view
            HistoryModel.DATum item = getItem(position);


            // if cell is exists - reuse it, if not - create the new one from resource
            cell = (FoldingCell) convertView;

            ViewHolder viewHolder;

            if (cell == null) {

                viewHolder = new ViewHolder();
                LayoutInflater vi = LayoutInflater.from(getContext());
                cell = (FoldingCell) vi.inflate(R.layout.cell, parent, false);

                // binding view parts to view holder //title
                viewHolder.tvUserNameTitle = (TextView) cell.findViewById(R.id.tvUsernameTitle);
                viewHolder.tvUserPhoneNoTitle = (TextView) cell.findViewById(R.id.tvUserPhoneNoTitle);
                viewHolder.tvCostTitle = (TextView) cell.findViewById(R.id.tvServiceCostTitle);
                viewHolder.tvServiceDateTitle = (TextView) cell.findViewById(R.id.tvServiceDateTitle);
                viewHolder.tvServiceTimeTitle = (TextView) cell.findViewById(R.id.tvServiceTimeTitle);
                viewHolder.ivToiletTitle = (ImageView) cell.findViewById(R.id.ivToiletTitle);
                viewHolder.ivDrainTitle = (ImageView) cell.findViewById(R.id.ivDrainTitle);
                viewHolder.ivSepticTitle = (ImageView) cell.findViewById(R.id.ivSepticTitle);

                //content view binding
                viewHolder.tvUserName = (TextView) cell.findViewById(R.id.tvUsernameContent);
                viewHolder.tvUserPhoneNo = (TextView) cell.findViewById(R.id.tvUserPhoneNoContent);
                viewHolder.tvDriverName = (TextView) cell.findViewById(R.id.tvDriverNameContent);
                viewHolder.tvDriverPhoneNo = (TextView) cell.findViewById(R.id.tvDriverPhoneNoContent);
                viewHolder.tvServiceStatus = (TextView) cell.findViewById(R.id.tvStatusContent);
                viewHolder.tvPaymentStatus = (TextView) cell.findViewById(R.id.tvPaymentStatusContent);
                viewHolder.tvServiceAddress = (TextView) cell.findViewById(R.id.tvServiceAddressContent);
                viewHolder.tvServiceCost = (TextView) cell.findViewById(R.id.tvBillAmountContent);
                viewHolder.tvServiceDate = (TextView) cell.findViewById(R.id.tvServiceDateContent);
                viewHolder.tvServiceTime = (TextView) cell.findViewById(R.id.tvServiceTimeContent);
                viewHolder.tvRatings = (TextView) cell.findViewById(R.id.tvRatingContent);
                viewHolder.ivToilet = (ImageView) cell.findViewById(R.id.ivToiletContent);
                viewHolder.ivDrain = (ImageView) cell.findViewById(R.id.ivDrainContent);
                viewHolder.ivSeptic = (ImageView) cell.findViewById(R.id.ivSepticContent);

                cell.setTag(viewHolder);

            } else {

                // for existing cell set valid valid state(without animation)
                if (unfoldedIndexes.contains(position)) {
                    cell.unfold(true);
                } else {
                    cell.fold(true);
                }
                viewHolder = (ViewHolder) cell.getTag();
            }

            // bind data from selected element to view through view holder
            viewHolder.tvUserNameTitle.setText(item.getINFO().getUSERNAME().toString().trim());
            viewHolder.tvUserPhoneNoTitle.setText(item.getINFO().getUSERMOBILE().toString().trim());
            viewHolder.tvCostTitle.setText(getContext().getResources().getString(R.string.cost_button)+" "+item.getINFO().getRATE().toString().trim());
            viewHolder.tvServiceDateTitle.setText(item.getINFO().getSERVICEDATE().toString().trim());
            viewHolder.tvServiceTimeTitle.setText(item.getINFO().getSERVICETIME().toString().trim());

            viewHolder.tvUserName.setText(item.getINFO().getUSERNAME().toString().trim());
            viewHolder.tvUserPhoneNo.setText(item.getINFO().getUSERMOBILE().toString().trim());
            viewHolder.tvDriverName.setText(item.getINFO().getDRIVERNAME().toString().trim());
            viewHolder.tvDriverPhoneNo.setText(item.getINFO().getDRIVERMOBILE().toString().trim());
            viewHolder.tvServiceAddress.setText(item.getINFO().getSERVICEADDRESS().toString().trim());
            viewHolder.tvServiceCost.setText(getContext().getResources().getString(R.string.cost_button)+" "+item.getINFO().getRATE().toString().trim());
            viewHolder.tvServiceDate.setText(item.getINFO().getSERVICEDATE().toString().trim());
            viewHolder.tvServiceTime.setText(item.getINFO().getSERVICETIME().toString().trim());
            viewHolder.tvRatings.setText(item.getINFO().getRATINGS().toString().trim());

            if (item.getINFO().getSTATUS().equalsIgnoreCase("0")) {
                viewHolder.tvServiceStatus.setText("Rejected");
            } else if (item.getINFO().getSTATUS().equalsIgnoreCase("1")) {
                viewHolder.tvServiceStatus.setText("Pending");
            } else if (item.getINFO().getSTATUS().equalsIgnoreCase("2")) {
                viewHolder.tvServiceStatus.setText("Accepted");
            } else if (item.getINFO().getSTATUS().equalsIgnoreCase("3")) {
                viewHolder.tvServiceStatus.setText("On Going");
            } else if (item.getINFO().getSTATUS().equalsIgnoreCase("4")) {
                viewHolder.tvServiceStatus.setText("Completed");
            } else if (item.getINFO().getSTATUS().equalsIgnoreCase("6")) {
                viewHolder.tvServiceStatus.setText("Cancelled by Driver");
            } else if (item.getINFO().getSTATUS().equalsIgnoreCase("8")) {
                viewHolder.tvServiceStatus.setText("Cancelled by User");
            }else if (item.getINFO().getSTATUS().equalsIgnoreCase("10")) {
                viewHolder.tvServiceStatus.setText("Rejected by Driver");
            }

            if (item.getINFO().getpAYMENT_STATUS().equalsIgnoreCase("0")) {
                viewHolder.tvPaymentStatus.setText("Pending");
            } else if (item.getINFO().getpAYMENT_STATUS().equalsIgnoreCase("1")) {
                viewHolder.tvPaymentStatus.setText("Paid");
            }

            if (item.getSERVICE().getSERVICENAME().contains(SEPTIC)) {
                viewHolder.ivSepticTitle.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_filled_small_septic_service));
                viewHolder.ivSeptic.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_filled_small_septic_service));
            } else {
                viewHolder.ivSepticTitle.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_small_septic_service));
                viewHolder.ivSeptic.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_small_septic_service));
            }

            if (item.getSERVICE().getSERVICENAME().contains(DRAINAGE)) {
                viewHolder.ivDrainTitle.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_filled_small_drain_cleanig));
                viewHolder.ivDrain.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_filled_small_drain_cleanig));
            } else {
                viewHolder.ivDrainTitle.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_small_drain_cleaning));
                viewHolder.ivDrain.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_small_drain_cleaning));
            }

            if (item.getSERVICE().getSERVICENAME().contains(TOILET)) {
                viewHolder.ivToiletTitle.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_filled_small_toilet_cleanig));
                viewHolder.ivToilet.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_filled_small_toilet_cleanig));
            } else {
                viewHolder.ivToiletTitle.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_small_toilet_cleaning));
                viewHolder.ivToilet.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_small_toilet_cleaning));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cell;

    }

    // simple methods for register cell state changes
    public void registerToggle(int position) {
        try {
            if (unfoldedIndexes.contains(position))
                registerFold(position);
            else
                registerUnfold(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerFold(int position) {
        unfoldedIndexes.remove(position);
    }

    public void registerUnfold(int position) {
        unfoldedIndexes.add(position);
    }

    public View.OnClickListener getDefaultRequestBtnClickListener() {
        return defaultRequestBtnClickListener;
    }

    public void setDefaultRequestBtnClickListener(View.OnClickListener defaultRequestBtnClickListener) {
        this.defaultRequestBtnClickListener = defaultRequestBtnClickListener;
    }

    // View lookup cache
    private static class ViewHolder {

        //title
        TextView tvUserNameTitle;
        TextView tvUserPhoneNoTitle;
        TextView tvCostTitle;
        TextView tvServiceDateTitle;
        TextView tvServiceTimeTitle;
        ImageView ivToiletTitle, ivDrainTitle, ivSepticTitle;

        //content
        TextView tvUserName;
        TextView tvUserPhoneNo;
        TextView tvDriverName;
        TextView tvDriverPhoneNo;
        TextView tvServiceAddress;
        TextView tvServiceStatus;
        TextView tvPaymentStatus;
        TextView tvServiceCost;
        TextView tvServiceDate;
        TextView tvServiceTime;
        TextView tvRatings;
        ImageView ivToilet, ivDrain, ivSeptic;
    }
}
