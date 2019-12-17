package com.hardcastle.honeysuckervendor.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.hardcastle.honeysuckervendor.R;
import com.hardcastle.honeysuckervendor.Receiver.SmsReceiver;
import com.hardcastle.honeysuckervendor.Utils.SmsListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class OtpFragment extends Fragment {

    private static final String TAG = "OtpFragment";
    private static final String ARG_PARAM1 = "MOBILE";
    private static final String ARG_PARAM2 = "RANDOM_NO";
    private static final String ARG_PARAM3 = "FROM";
    private String PARAM_MOBILE_NO, PARAM_RANDOM_NO, PARAM_FROM;
    private Unbinder unbinder;
    private OnOTPFragmentInteractionListener mListener;
    private CountDownTimer countDownTimer;
    private final long startTime = 120 * 1000;
    private final long interval = 1 * 1000;
    private Context context;

    @BindView(R.id.btnVerifyAndProceed) Button mBtnVerifyAndProceed;
    @BindView(R.id.pinview) Pinview mPinview;
    @BindView(R.id.tvOTPSentTo) TextView mTvOTPSentTo;
    @BindView(R.id.tvVerifyDetails) TextView mTvVerifyDetails;
    @BindView(R.id.tvEnterOTP) TextView mTvEnterOTP;
    @BindView(R.id.tvTimer) TextView mTvTimer;

    public OtpFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static OtpFragment newInstance(String param1, String param2, String param3) {
        OtpFragment fragment = null;
        try {
            fragment = new OtpFragment();
            Bundle args = new Bundle();
            args.putString(ARG_PARAM1, param1);
            args.putString(ARG_PARAM2, param2);
            args.putString(ARG_PARAM3, param3);
            fragment.setArguments(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (getArguments() != null) {
                PARAM_MOBILE_NO = getArguments().getString(ARG_PARAM1);
                PARAM_RANDOM_NO = getArguments().getString(ARG_PARAM2);
                PARAM_FROM = getArguments().getString(ARG_PARAM3);
                Log.e("OtpFragment", "onCreate: Mobile No : "+ PARAM_MOBILE_NO +"  Random No. : "+ PARAM_RANDOM_NO+"  From : "+ PARAM_FROM);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_otp, container, false);
        try {
            ButterKnife.bind(this, view);
            Toast.makeText(getActivity(), PARAM_RANDOM_NO, Toast.LENGTH_LONG).show();

            SmsReceiver.bindListener(new SmsListener() {
                @Override
                public void messageReceived(String messageText) {
                    Log.e("SMS",messageText);
                    mPinview.setValue(messageText);

                    if (mPinview.getPinLength() == 4) {
                        countDownTimer.cancel();
                        mTvTimer.setVisibility(View.GONE);
                    }

                    mPinview.setPinViewEventListener(new Pinview.PinViewEventListener() {
                        @Override
                        public void onDataEntered(Pinview pinview, boolean fromUser) {
                            Toast.makeText(getActivity(), pinview.getValue(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            mTvOTPSentTo.setText(getActivity().getResources().getString(R.string.otp_sent_to)+" "+ PARAM_MOBILE_NO);
            mTvEnterOTP.setText(getActivity().getResources().getString(R.string.enter_otp));
            mTvVerifyDetails.setText(getActivity().getResources().getString(R.string.verify_details));
            mBtnVerifyAndProceed.setText(getActivity().getResources().getString(R.string.verify_and_proceed));

            countDownTimer = new MyCountDownTimer(startTime, interval);
            countDownTimer.start();
            mTvTimer.setText(mTvTimer.getText() + String.valueOf(startTime / 1000));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //unbinder.unbind();
    }

    // TODO: Rename method, update argument and hook method into UI event
    @OnClick(R.id.btnVerifyAndProceed)
    void onClick() {
        //if (mListener != null) {
        try {
            if(PARAM_RANDOM_NO.equalsIgnoreCase(mPinview.getValue())){
                Log.e(TAG, "onClick: CLICK ON VERIFY PROCEED");
                if (PARAM_FROM.equalsIgnoreCase("login")) {
                    Log.e(TAG, "open New Password Fragment ");
                    mListener.openNewPasswordFragment();
                } else if (PARAM_FROM.equalsIgnoreCase("edit")) {
                    Log.e(TAG, "Number Verified");
                    mListener.phoneNoVerified();
                    getActivity().getFragmentManager().beginTransaction().remove(OtpFragment.this).commit();
                }

            } else {

                Toast.makeText(getActivity(), "Please enter valid OTP", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (context instanceof OnOTPFragmentInteractionListener) {
                 mListener = (OnOTPFragmentInteractionListener) context;
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
            if (activity instanceof OnOTPFragmentInteractionListener) {
                mListener = (OnOTPFragmentInteractionListener) activity;
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
        //mListener = null;
    }

    private class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime,interval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            try {
                mTvTimer.setText("Please wait " +millisUntilFinished / 1000 +" seconds for OTP");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFinish() {
            try {
                mTvTimer.setText("Resend OTP");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnOTPFragmentInteractionListener {
        // TODO: Update argument type and name
        void openNewPasswordFragment();
        void phoneNoVerified();
    }

}
