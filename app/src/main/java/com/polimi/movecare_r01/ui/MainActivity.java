package com.polimi.movecare_r01.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import com.polimi.movecare_r01.R;
import com.polimi.movecare_r01.applicationLogic.service.InitializationService;
import com.polimi.movecare_r01.logic.battery.BatteryChecker;
import com.polimi.movecare_r01.ui.fragment.PermissionErrorFragment;
import com.polimi.movecare_r01.ui.fragment.PermissionFragment;
import com.polimi.movecare_r01.ui.interfaces.PermissionDialogListener;


import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements PermissionDialogListener {

    private static final String SIM_SERIAL_NUMBER = "simSerialNumber";
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 12345;
    private static final String[] permissionsArray = {
            /* Permission Group "Phone", which includes READ_CALL_LOG, CALL_PHONE*/
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.CALL_PHONE,

            /* Permission Group "SMS" */
            Manifest.permission.READ_SMS,

            /* Permission Group "Location", which includes ACCESS_COURSE_LOCATION */
            //Manifest.permission.ACCESS_FINE_LOCATION,

            Manifest.permission.READ_CONTACTS,
            /*Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE*/
            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
    };

    private String simSerialNumber;
    //private DialogFragment initFragment;
    private DialogFragment permissionFragment;
    private DialogFragment permissionErrorFragment;

    private static MainActivity ins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ins = this;

        boolean permissionRequestNeeded = false;

        for (String s : permissionsArray) {
            if (ContextCompat.checkSelfPermission(this, s)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionRequestNeeded = true;
                break;
            }
        }

        if (permissionRequestNeeded) {
            permissionFragment = new PermissionFragment();
            permissionFragment.setCancelable(false);
            permissionFragment.show(getSupportFragmentManager(), "permission");
        } else {
            startLoginActivity();
        }
    }

    // Called once the activity comes to foreground
    @Override
    protected void onResume() {
        super.onResume();

        // CHECK TOKEN
        // If token is valid, then user logged and authenticated
        // else start login activity
        startInitService();

    }

    @Override
    public void onDestroy() {
        ins = null;
        super.onDestroy();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        permissionFragment.dismiss();

        ActivityCompat.requestPermissions(this, permissionsArray,
                ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        if (permissionFragment != null) {
            permissionFragment.dismiss();
        }

        if (permissionErrorFragment != null) {
            permissionErrorFragment.dismiss();
        }

        this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == ASK_MULTIPLE_PERMISSION_REQUEST_CODE) {
            boolean denied = false;

            for (int permissionResult : grantResults) {
                if (permissionResult == PackageManager.PERMISSION_DENIED) {
                    denied = true;
                    break;
                }
            }

            if (denied) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        permissionErrorFragment = new PermissionErrorFragment();
                        permissionErrorFragment.setCancelable(false);
                        permissionErrorFragment.show(getSupportFragmentManager(), "permissionError");
                    }
                }, 0);
            } else {
                startLoginActivity();
            }

        }
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void startInitService() {
        setSimSerialNumber();
        setAppRunningStrings();

        Intent initIntent = new Intent(this, InitializationService.class);
        initIntent.putExtra(SIM_SERIAL_NUMBER, simSerialNumber);
        startService(initIntent);

    }

    private void setSimSerialNumber() {
        if (simSerialNumber == null) {
            TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            simSerialNumber = tMgr.getSimSerialNumber();

            if(simSerialNumber == null){
                Log.e(this.getClass().getSimpleName(), "PHONE NUMBER STILL NULL");
            }
        }
    }

    private void setAppRunningStrings(){

        TextView userTV = (TextView) findViewById(R.id.userTV);
        TextView movecareTV = (TextView) findViewById(R.id.movecareTV);

        userTV.setText(getText(R.string.hello)+" "+simSerialNumber);

        BatteryChecker batteryChecker;
        try {
            batteryChecker = new BatteryChecker(this);
        } catch (Exception e) {
            Log.e("MAIN", "Exception caught: cannot read battery level or charging state");
            e.printStackTrace();
            movecareTV.setText("---");
            return;
        }

        if (batteryChecker.isCriticalLevel()){
            Log.e("MAIN", "Battery critic level, App finish");
            this.finish();
        }

        if(batteryChecker.isUnderThreshold()){
            updateUIStrings(getString(R.string.recharge_message), true);
        } else{
            movecareTV.setText(getString(R.string.app_running));
        }

    }

    public static MainActivity getInst()
    {
        return ins;
    }

    public void updateUIStrings(final String s, final boolean warning) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textView = (TextView) findViewById(R.id.movecareTV);
                textView.setText(s);
                if(warning){
                    textView.setTextColor(Color.RED);
                    textView.setTypeface(null, Typeface.BOLD);
                }

            }
        });
    }

    /*
    // contains code for reading (sharedpreferences) user data
    private void retrieveUserData(){
        // Intent retrieveIntent = new Intent(this, RetrieveUserDataService.class);
        // startService(retrieveIntent);
    }

    private void showWaitingDialog(){
        initFragment = new InitializationFragment();
        initFragment.setCancelable(false);
        initFragment.show(getSupportFragmentManager(), "init");
    }

    private void dismissWaitingDialog(){
        initFragment.dismiss();
    }*/
}
