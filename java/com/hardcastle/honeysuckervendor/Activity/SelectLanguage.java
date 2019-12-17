package com.hardcastle.honeysuckervendor.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hardcastle.honeysuckervendor.R;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SelectLanguage extends AppCompatActivity {

    private static final String TAG = "SelectLanguage";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @BindView(R.id.tvLanguage) TextView mTvLanguage;
    @BindView(R.id.tvSelectLanguage) TextView mTvSelectLanguage;
    @BindView(R.id.btnEnglish) Button mBtnEnglish;
    @BindView(R.id.btnHindi) Button mBtnHindi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);

        ButterKnife.bind(this);
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnClick({R.id.btnEnglish, R.id.btnHindi})
    void selectLanguage(View view) {

        switch (view.getId()) {

            case R.id.btnEnglish :
                Log.e(TAG, "selectLanguage: ENGLISH");
                Configuration config = new Configuration();
                config.locale = Locale.ENGLISH;
                editor.putBoolean(getResources().getString(R.string.preference_is_language_selected),true);
                editor.putString(getResources().getString(R.string.preference_language),"ENGLISH");
                editor.apply();
                this.getApplicationContext().getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                goToMainActivity();
                break;

            case R.id.btnHindi :
                Log.e(TAG, "selectLanguage: HINDI");
                Configuration config1 = new Configuration();
                config1.locale = Locale.FRENCH;
                editor.putString(getResources().getString(R.string.preference_language),"FRENCH");
                editor.putBoolean(getResources().getString(R.string.preference_is_language_selected),true);
                editor.apply();
                this.getApplicationContext().getResources().updateConfiguration(config1, getResources().getDisplayMetrics());
                goToMainActivity();
                break;

        }

    }

    void goToMainActivity() {
        try {
            Intent intent = new Intent(SelectLanguage.this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
