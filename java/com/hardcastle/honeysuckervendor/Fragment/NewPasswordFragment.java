package com.hardcastle.honeysuckervendor.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hardcastle.honeysuckervendor.R;
import com.hardcastle.honeysuckervendor.Receiver.ConnectivityReceiver;
import com.hardcastle.honeysuckervendor.Utils.GLOBAL;
import com.hardcastle.honeysuckervendor.Utils.MyCustomDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewPasswordFragment.OnNewPasswordFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class NewPasswordFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "TYPE";
    private static final String ARG_PARAM2 = "MOBILE_NO";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Unbinder unbinder;
    private OnNewPasswordFragmentInteractionListener mListener;

    @BindView(R.id.tvSetPasswordLabel) TextView mTvSetPasswordLabel;
    @BindView(R.id.tvEnterPasswordLabel) TextView mTvEnterPasswordLabel;
    @BindView(R.id.edtNewPassword) EditText mEdtNewPassword;
    @BindView(R.id.btnSaveAndProceed) Button mBtnSaveAndProceed;

    public NewPasswordFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewPasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewPasswordFragment newInstance(String param1, String param2) {
        NewPasswordFragment fragment = null;
        try {
            fragment = new NewPasswordFragment();
            Bundle args = new Bundle();
            args.putString(ARG_PARAM1, param1);
            args.putString(ARG_PARAM2, param2);
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
                mParam1 = getArguments().getString(ARG_PARAM1);
                mParam2 = getArguments().getString(ARG_PARAM2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_new_password, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            mTvSetPasswordLabel.setText(getActivity().getResources().getString(R.string.set_password_label));
            mTvEnterPasswordLabel.setText(getActivity().getResources().getString(R.string.enter_password_label));
            mBtnSaveAndProceed.setText(getActivity().getResources().getString(R.string.save_proceed));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnTextChanged(value = R.id.edtNewPassword, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChangedPhoneNo(Editable editable) { }

    @OnTextChanged(value = R.id.edtNewPassword, callback = OnTextChanged.Callback.BEFORE_TEXT_CHANGED)
    void beforeTextChangedPassword(CharSequence s, int start, int count, int after) { }

    @OnTextChanged(value = R.id.edtNewPassword, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void onTextChangedPhoneNo(CharSequence s, int start, int before, int count) {

        try {
            if (mEdtNewPassword.getText().toString().trim().length()>=6 && mEdtNewPassword.getText().toString().trim().length()<=15) {
                mBtnSaveAndProceed.setEnabled(true);
                mBtnSaveAndProceed.setBackgroundResource(R.drawable.blue_button);
            } else {
                mBtnSaveAndProceed.setEnabled(false);
                mBtnSaveAndProceed.setBackgroundResource(R.drawable.blue_button_light);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    @OnClick(R.id.btnSaveAndProceed)
    void onSaveNewPassword() {

        try {
            if (mListener != null) {

                if (mEdtNewPassword.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getActivity(), "" + getResources().getString(R.string.password_required_alert), Toast.LENGTH_SHORT).show();
                } else if (mEdtNewPassword.getText().toString().trim().length() < 6) {
                    Toast.makeText(getActivity(), "" + getResources().getString(R.string.password_validation_alert), Toast.LENGTH_SHORT).show();
                } else {
                    if (ConnectivityReceiver.isConnected()) {
                        showDialogBox(getResources().getString(R.string.confirmation), getResources().getString(R.string.confirm_password), getResources().getString(R.string.yes), getResources().getString(R.string.no));
                    } else {
                        GLOBAL.showMessage(getActivity(), getResources().getString(R.string.check_internet_connection));
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDialogBox(String title, String alertMessage, String btnPositiveName, String btnNegativeName) {

        MyCustomDialog builder = new MyCustomDialog(getActivity(), title, alertMessage);
        final AlertDialog dialog = builder.setNegativeButton(btnNegativeName, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }

        }).setPositiveButton(btnPositiveName, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mListener.setNewPassword(mParam1,mParam2,mEdtNewPassword.getText().toString().trim());
                getActivity().getFragmentManager().beginTransaction().remove(NewPasswordFragment.this).commit();
            }

        }).create();

        //2. now setup to change color of the button
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorOrange));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorOrange));
            }
        });

        dialog.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (context instanceof OnNewPasswordFragmentInteractionListener) {
                mListener = (OnNewPasswordFragmentInteractionListener) context;
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
            if (activity instanceof OnNewPasswordFragmentInteractionListener) {
                mListener = (OnNewPasswordFragmentInteractionListener) activity;
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

    public interface OnNewPasswordFragmentInteractionListener {
        // TODO: Update argument type and name
        void setNewPassword(String type, String mobileNo, String newPassword);
    }
}
