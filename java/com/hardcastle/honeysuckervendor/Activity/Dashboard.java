package com.hardcastle.honeysuckervendor.Activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.hardcastle.honeysuckervendor.Fragment.AccountFragment;
import com.hardcastle.honeysuckervendor.Fragment.HistoryFragment;
import com.hardcastle.honeysuckervendor.Fragment.HomeFragment;
import com.hardcastle.honeysuckervendor.Fragment.MyDriverFragment;
import com.hardcastle.honeysuckervendor.Fragment.TripsFragment;
import com.hardcastle.honeysuckervendor.R;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Dashboard extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "Dashboard";
    private BottomNavigationView navigation;
    private int selectedItemId;
    private MenuItem mHome, mTrips, mHistory, mMyDrivers, mAccount;
    static boolean active = false;
    private Fragment selectedFragment = null;
    private FragmentTransaction transaction;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            //Fragment selectedFragment = null;

            try {
                switch (item.getItemId()) {

                    case R.id.navigation_home:
                        selectedItemId = item.getItemId();
                        selectedFragment = HomeFragment.newInstance();
                        break;

                    case R.id.navigation_trips:
                        selectedItemId = item.getItemId();
                        selectedFragment = TripsFragment.newInstance();
                        break;

                    case R.id.navigation_history:
                        selectedItemId = item.getItemId();
                        selectedFragment = HistoryFragment.newInstance();
                        break;

                    case R.id.navigation_my_drivers:
                        selectedItemId = item.getItemId();
                        selectedFragment = MyDriverFragment.newInstance();
                        break;

                    case R.id.navigation_account:
                        selectedItemId = item.getItemId();
                        selectedFragment = AccountFragment.newInstance();
                        break;
                }

                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

       /* if (active) {
            finishAffinity();
        }*/

        Log.e(TAG, "onCreate: ");
        try {
            mHome = (MenuItem) findViewById(R.id.navigation_home);
            mHistory = (MenuItem) findViewById(R.id.navigation_history);
            mTrips = (MenuItem) findViewById(R.id.navigation_trips);
            mAccount = (MenuItem) findViewById(R.id.navigation_account);
            mMyDrivers = (MenuItem) findViewById(R.id.navigation_my_drivers);

            navigation = (BottomNavigationView) findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, HomeFragment.newInstance());
            transaction.commit();

            // 0 - Reject, 1 - Pending, 2 - Accept, 3 - On going, 4 - Completed, 6 - Cancel by Driver, 7 - On the way, 8 - Canceled by User
            if (Build.VERSION.SDK_INT >= 23) {
                checkPermissions();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*if(!isNotificationServiceEnabled()){
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
            enableNotificationListenerAlertDialog.show();
        }*/

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: ");

        //active = true;

        try {
            navigation.getMenu().removeItem(R.id.navigation_account);
            navigation.getMenu().removeItem(R.id.navigation_history);
            navigation.getMenu().removeItem(R.id.navigation_my_drivers);
            navigation.getMenu().removeItem(R.id.navigation_trips);
            navigation.getMenu().removeItem(R.id.navigation_home);
            navigation.inflateMenu(R.menu.bottom_navigation_items);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            disableShiftMode(navigation);
            navigation.setSelectedItemId(selectedItemId);

            // not working - not getting expected output
            /*if (getIntent().getStringExtra("STATUS").equalsIgnoreCase(null) || getIntent().getStringExtra("STATUS").equalsIgnoreCase("")) {
                Log.e(TAG, "onStart: Intent value is null");
            } else {
                Log.e(TAG, "onStart: Intent is not null");
                if (getIntent().getStringExtra("STATUS").equalsIgnoreCase("0")) {

                    Log.e(TAG, "onStart: Go to trips fragment");
                    selectedItemId = mTrips.getItemId();
                    selectedFragment = TripsFragment.newInstance("", "");
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, selectedFragment);
                    transaction.commit();
                    //navigation.setSelectedItemId(selectedItemId);
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e(TAG, "onResume: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.e(TAG, "onRestart: ");
    }

    @Override
    protected void onStop() {
        super.onStop();

        //active = false;
    }

    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }

    public String[] getRequiredPermissions() {
        String[] permissions = null;
        try {
            permissions = getPackageManager().getPackageInfo(getPackageName(),
                    PackageManager.GET_PERMISSIONS).requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (permissions == null) {
            return new String[0];
        } else {
            return permissions.clone();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        String[] ungrantedPermissions = requiredPermissionsStillNeeded();
        if (ungrantedPermissions.length == 0) {
            Toast.makeText(this, "Permissions already granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermissions(ungrantedPermissions, PERMISSION_REQUEST_CODE);
        }
    }

    @TargetApi(23)
    private String[] requiredPermissionsStillNeeded() {

        Set<String> permissions = new HashSet<String>();
        for (String permission : getRequiredPermissions()) {
            permissions.add(permission);
        }
        for (Iterator<String> i = permissions.iterator(); i.hasNext();) {
            String permission = i.next();
            if (ActivityCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_GRANTED) {
                i.remove();
            } else {
            }
        }
        return permissions.toArray(new String[permissions.size()]);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            checkPermissions();
        }
    }

}
