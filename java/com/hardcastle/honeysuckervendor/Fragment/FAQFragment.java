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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFAQFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FAQFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FAQFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFAQFragmentInteractionListener mListener;

    @BindView(R.id.tvFAQ) TextView mTvFAQ;

    public FAQFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FAQFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FAQFragment newInstance(String param1, String param2) {
        FAQFragment fragment = new FAQFragment();
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
        View view =  inflater.inflate(R.layout.fragment_faq, container, false);

        ButterKnife.bind(this,view);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFAQFragmentInteraction(uri);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mTvFAQ.setText(getResources().getString(R.string.faqs));
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
            if (context instanceof OnFAQFragmentInteractionListener) {
                mListener = (OnFAQFragmentInteractionListener) context;
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
            if (activity instanceof OnFAQFragmentInteractionListener) {
                mListener = (OnFAQFragmentInteractionListener) activity;
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
    public interface OnFAQFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFAQFragmentInteraction(Uri uri);
    }
}
