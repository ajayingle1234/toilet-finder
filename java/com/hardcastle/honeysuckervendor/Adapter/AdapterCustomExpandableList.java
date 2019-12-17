package com.hardcastle.honeysuckervendor.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hardcastle.honeysuckervendor.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by abhijeet on 1/6/2017.
 */
public class AdapterCustomExpandableList extends BaseExpandableListAdapter{

    private Context mContext;
    private List<String> mListDataHeader;
    private HashMap<String, List<String>> mHashmapDataChild;
    private Resources resources;

    public AdapterCustomExpandableList(Context context, List<String> listDataHeader, HashMap<String, List<String>> hashmapDataChild) {

        mContext = context;
        mListDataHeader = listDataHeader;
        mHashmapDataChild = hashmapDataChild;
        resources = mContext.getResources();
    }

    @Override
    public int getGroupCount() {
        return mListDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mHashmapDataChild.get(mListDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mListDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mHashmapDataChild.get(mListDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        try {
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.layout_expandablelist_title, null);
            }

            TextView tvGroupTitle = (TextView) convertView.findViewById(R.id.tvGroupTitle);
            TextView tvGroupSubtitle = (TextView)convertView.findViewById(R.id.tvGroupSubtitle);
            ImageView ivGroupArrow = (ImageView)convertView.findViewById(R.id.ivGroupArrow);

            if (groupPosition == 0) {

                tvGroupSubtitle.setText(resources.getString(R.string.manage_address)+","+resources.getString(R.string.language_label));
                tvGroupSubtitle.setVisibility(View.VISIBLE);
                tvGroupTitle.setText(resources.getString(R.string.my_account));

                if(isExpanded) {
                    ivGroupArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_up_big_arrow));
                }
                else if(!isExpanded){
                    ivGroupArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_down_big_arrow));
                }

            }else if (groupPosition == 1) {
                tvGroupTitle.setText(resources.getString(R.string.about_us));
                tvGroupSubtitle.setText("");
                tvGroupSubtitle.setVisibility(View.INVISIBLE);
                ivGroupArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_right_big_arrow));

            }else if (groupPosition == 2) {
                tvGroupTitle.setText(resources.getString(R.string.help));
                tvGroupSubtitle.setVisibility(View.VISIBLE);
                tvGroupSubtitle.setText(resources.getString(R.string.faqs));

                if(isExpanded) {
                    ivGroupArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_up_big_arrow));
                }
                else if(!isExpanded){
                    ivGroupArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_down_big_arrow));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        try {
            String childText = (String) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.layout_expandablelist_child, null);
            }

            TextView tvChildName = (TextView) convertView.findViewById(R.id.tvChildName);
            ImageView ivChildArrow = (ImageView)convertView.findViewById(R.id.ivChildArrow);
            ImageView ivChildIcon = (ImageView)convertView.findViewById(R.id.ivChildIcon);
            View view = convertView.findViewById(R.id.divider);

            tvChildName.setText(childText);

            if (groupPosition == 0) {

                if (childPosition == 0) {
                    tvChildName.setText(resources.getString(R.string.manage_address));
                    ivChildArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_right_small_arrow));
                    ivChildIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_manage_address_submenu));
                    view.setVisibility(View.INVISIBLE);
                }
                if (childPosition == 1) {
                    tvChildName.setText(resources.getString(R.string.language_label));
                    ivChildArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_right_small_arrow));
                    ivChildIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_language_submenu));
                    view.setVisibility(View.VISIBLE);
                }

            }

            else if (groupPosition == 2) {

                if (childPosition == 0) {
                    tvChildName.setText(resources.getString(R.string.faqs));
                    ivChildArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_right_small_arrow));
                    ivChildIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_faq_submenu));
                    view.setVisibility(View.INVISIBLE);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
