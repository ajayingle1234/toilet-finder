package com.hardcastle.honeysuckervendor.Fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hardcastle.honeysuckervendor.R;
import com.hardcastle.honeysuckervendor.Receiver.ConnectivityReceiver;
import com.hardcastle.honeysuckervendor.Utils.GLOBAL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnAboutUsFragmentInteractionListener mListener;

    @BindView(R.id.tvAboutUs) TextView mTvAboutUs;

    public AboutUsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AboutUsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutUsFragment newInstance(String param1, String param2) {
        AboutUsFragment fragment = new AboutUsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_about_us, container, false);

        ButterKnife.bind(this,view);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onAboutUsFragmentInteraction(uri);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mTvAboutUs.setText(getResources().getString(R.string.about_us));
    }

    @Override
    public void onResume() {
        super.onResume();

        // register connection status listener
        GLOBAL.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (context instanceof OnAboutUsFragmentInteractionListener) {
                mListener = (OnAboutUsFragmentInteractionListener) context;
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement OnLanguageFragmentInteractionListener");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            if (activity instanceof OnAboutUsFragmentInteractionListener) {
                mListener = (OnAboutUsFragmentInteractionListener) activity;
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

    @OnClick({R.id.ivBack})
    void onClick(View view) {
        if (mListener != null) {

            switch (view.getId()) {

                case R.id.ivBack :
                    getActivity().finish();
                    break;
            }

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnAboutUsFragmentInteractionListener {
        // TODO: Update argument type and name
        void onAboutUsFragmentInteraction(Uri uri);
    }
}
