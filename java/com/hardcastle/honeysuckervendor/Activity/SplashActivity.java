package com.hardcastle.honeysuckervendor.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.hardcastle.honeysuckervendor.R;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashActivity extends AppCompatActivity {

    private ImageView mImgViewLogo;
    private static final String TAG = "SPLASH";
    private SharedPreferences sharedPreferences;
    private String language_String;
    private ImageView image_1,image_2,image_3,image_4,image_5;
    Animation animation1, animation2, animation3, animation4,animation5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        try {
            //mImgViewLogo = (ImageView) findViewById(R.id.ivHSLogo);
            animation1 = AnimationUtils.loadAnimation(this, R.anim.disk_drop);
            animation2 = AnimationUtils.loadAnimation(this, R.anim.disk_drop);
            animation3 = AnimationUtils.loadAnimation(this, R.anim.disk_drop);
            animation4 = AnimationUtils.loadAnimation(this, R.anim.disk_drop);
            animation5 = AnimationUtils.loadAnimation(this,R.anim.disk_drop);

            image_1 = (ImageView)findViewById(R.id.one_image);
            image_2 = (ImageView)findViewById(R.id.two_image);
            image_3 = (ImageView)findViewById(R.id.three_image);
            image_4 = (ImageView)findViewById(R.id.four_image);
            image_5 = (ImageView)findViewById(R.id.five_image);

            sharedPreferences = getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            language_String = sharedPreferences.getString(getResources().getString(R.string.preference_language),"ENGLISH");

            Log.e(TAG, "onCreate, Language Selected : "+language_String);
            Configuration config = new Configuration();

            if (language_String.equalsIgnoreCase("ENGLISH")) {
                Log.e(TAG, "onCreate, Language Selected : "+language_String);
                config.locale = Locale.ENGLISH;
                this.getApplicationContext().getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            } else{
                Log.e(TAG, "onCreate, Language Selected : "+language_String);
                config.locale = Locale.FRENCH;
                this.getApplicationContext().getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            }

            /*new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (sharedPreferences.getBoolean(getResources().getString(R.string.preference_is_language_selected), false)) {

                        if (sharedPreferences.getBoolean(getResources().getString(R.string.preference_is_login), false)) {
                            Intent intent = new Intent(SplashActivity.this, Dashboard.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Intent intent = new Intent(SplashActivity.this, SelectLanguage.class);
                        startActivity(intent);
                        finish();
                    }

                }
            }, 2000);*/

            setAnimation();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setAnimation() {

        image_1.setVisibility(View.VISIBLE);
        image_1.startAnimation(animation1);

        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                image_2.setVisibility(View.VISIBLE);
                image_2.startAnimation(animation2);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                image_3.setVisibility(View.VISIBLE);
                image_3.startAnimation(animation3);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animation3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                image_4.setVisibility(View.VISIBLE);
                image_4.startAnimation(animation4);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animation4.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                image_5.setVisibility(View.VISIBLE);
                image_5.startAnimation(animation5);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animation5.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                try {
                    if (sharedPreferences.getBoolean(getResources().getString(R.string.preference_is_language_selected), false)) {
                        if (sharedPreferences.getBoolean(getResources().getString(R.string.preference_is_login), false)) {
                            Intent intent = new Intent(SplashActivity.this, Dashboard.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }else{
                        Intent intent = new Intent(SplashActivity.this, SelectLanguage.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
