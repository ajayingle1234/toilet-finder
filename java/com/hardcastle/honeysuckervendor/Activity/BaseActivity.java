package com.hardcastle.honeysuckervendor.Activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.hardcastle.honeysuckervendor.Fragment.AboutUsFragment;
import com.hardcastle.honeysuckervendor.Fragment.FAQFragment;
import com.hardcastle.honeysuckervendor.Fragment.LanguageFragment;
import com.hardcastle.honeysuckervendor.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BaseActivity extends AppCompatActivity implements LanguageFragment.OnLanguageFragmentInteractionListener, FAQFragment.OnFAQFragmentInteractionListener,
                                                               AboutUsFragment.OnAboutUsFragmentInteractionListener{

    private String mFragmentName;
    private Fragment selectedFragment;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        try {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            mFragmentName = getIntent().getStringExtra("FRAGMENT_NAME");

            if (mFragmentName.equalsIgnoreCase("LANGUAGE")) {
                selectedFragment = LanguageFragment.newInstance("","");
                fragmentTransaction.replace(R.id.container, selectedFragment);
                fragmentTransaction.commit();
            } else if (mFragmentName.equalsIgnoreCase("ABOUT_US")) {
                selectedFragment = AboutUsFragment.newInstance("","");
                fragmentTransaction.replace(R.id.container, selectedFragment);
                fragmentTransaction.commit();
            } else if (mFragmentName.equalsIgnoreCase("FAQ")) {
                selectedFragment = FAQFragment.newInstance("","");
                fragmentTransaction.replace(R.id.container, selectedFragment);
                fragmentTransaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void setLanguage() {

    }

    @Override
    public void onFAQFragmentInteraction(Uri uri) {

    }

    @Override
    public void onAboutUsFragmentInteraction(Uri uri) {

    }
}
