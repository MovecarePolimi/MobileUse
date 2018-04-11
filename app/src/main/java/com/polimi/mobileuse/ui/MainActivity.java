package com.polimi.mobileuse.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.polimi.mobileuse.R;
import com.polimi.mobileuse.applicationLogic.service.InitializationService;
import com.polimi.mobileuse.dao.preferences.LoginPreferences;
import com.polimi.mobileuse.logic.battery.BatteryChecker;
import com.polimi.mobileuse.ui.fragment.PermissionErrorFragment;
import com.polimi.mobileuse.ui.fragment.PermissionFragment;
import com.polimi.mobileuse.ui.interfaces.PermissionDialogListener;


import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements PermissionDialogListener {

    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 12345;
    private static final String[] permissionsArray = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.CALL_PHONE,

            Manifest.permission.READ_SMS,

            Manifest.permission.READ_CONTACTS,
            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
    };

    private DialogFragment permissionFragment;
    private DialogFragment permissionErrorFragment;
    private Context context;
    private static MainActivity ins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = getApplicationContext();
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
            startInitService();
        }
    }

    // Called once the activity comes to foreground
    @Override
    protected void onResume() {
        super.onResume();

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
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
                startInitService();
            }

        }
    }

    private void startInitService() {
        setAppRunningStrings();

        startService(new Intent(this, InitializationService.class));

    }

    private void setAppRunningStrings(){

        TextView userTV = findViewById(R.id.userTV);
        TextView movecareTV = findViewById(R.id.movecareTV);

        LoginPreferences logPrefs = new LoginPreferences();
        String username = logPrefs.getVariable(context, LoginPreferences.Variable.USERNAME);
        String helloString = "Hello "+username;
        userTV.setText(helloString);

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
                TextView textView = findViewById(R.id.movecareTV);
                textView.setText(s);
                if(warning){
                    textView.setTextColor(Color.RED);
                    textView.setTypeface(null, Typeface.BOLD);
                }

            }
        });
    }

}
