package com.hardcastle.honeysuckervendor.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.hardcastle.honeysuckervendor.R;
import com.hardcastle.honeysuckervendor.Receiver.ConnectivityReceiver;
import com.hardcastle.honeysuckervendor.Utils.GLOBAL;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnLanguageFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LanguageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LanguageFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener{

    private static final String TAG = "LanguageFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private OnLanguageFragmentInteractionListener mListener;
    private Configuration config;

    @BindView(R.id.tvLanguage) TextView mTvLanguage;
    @BindView(R.id.chkEnglish) CheckBox mChkEnglish;
    @BindView(R.id.chkHindi) CheckBox mChkHindi;

    public LanguageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LanguageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LanguageFragment newInstance(String param1, String param2) {
        LanguageFragment fragment = new LanguageFragment();
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
        sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_language, container, false);

        try {
            ButterKnife.bind(this,view);

            if (sharedPreferences.getString(getResources().getString(R.string.preference_language), "").equalsIgnoreCase("ENGLISH")) {
                mChkEnglish.setChecked(true);
                mChkHindi.setChecked(false);

            } else if (sharedPreferences.getString(getResources().getString(R.string.preference_language), "").equalsIgnoreCase("FRENCH")) {
                mChkHindi.setChecked(true);
                mChkEnglish.setChecked(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mTvLanguage.setText(getResources().getString(R.string.choose_language));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
        // register connection status listener
        GLOBAL.getInstance().setConnectivityListener(this);
    }

    // TODO: Rename method, update argument and hook method into UI event
    @OnClick({R.id.btnContinue, R.id.ivBack})
    void onClick(View view) {
        try {
            if (mListener != null) {

                switch (view.getId()) {

                    case R.id.btnContinue :
                        Log.e(TAG, "onClick: CONTINUE");
                        this.getActivity().getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                        editor.apply();
                        getActivity().finish();
                        break;

                    case R.id.ivBack :
                        getActivity().finish();
                        break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.chkEnglish, R.id.chkHindi})
    void selectLanguage(CheckBox checkBox) {

        try {
            Log.e(TAG, "selectLanguage: ");
            switch (checkBox.getId()) {

                case R.id.chkEnglish:
                    Log.e(TAG, "selectLanguage: Selected Language : ENGLISH");
                    mChkEnglish.setChecked(true);
                    mChkHindi.setChecked(false);
                    editor.putString(getResources().getString(R.string.preference_language),"ENGLISH");
                    config = new Configuration();
                    config.locale = Locale.ENGLISH;
                    //this.getActivity().getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                    break;

                case R.id.chkHindi:
                    Log.e(TAG, "selectLanguage: Selected Language : HINDI");
                    mChkHindi.setChecked(true);
                    mChkEnglish.setChecked(false);
                    editor.putString(getResources().getString(R.string.preference_language),"FRENCH");
                    config = new Configuration();
                    config.locale = Locale.FRENCH;
                    //this.getActivity().getResources().updateConfiguration(config1, getResources().getDisplayMetrics());
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (context instanceof OnLanguageFragmentInteractionListener) {
                mListener = (OnLanguageFragmentInteractionListener) context;
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
            if (activity instanceof OnLanguageFragmentInteractionListener) {
                mListener = (OnLanguageFragmentInteractionListener) activity;
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
    public interface OnLanguageFragmentInteractionListener {
        // TODO: Update argument type and name
        void setLanguage();
    }
}
