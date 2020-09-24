package com.blue_unicorn.android_auth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.blue_unicorn.android_auth_lib.android.FidoAuthService;
import com.blue_unicorn.android_auth_lib.android.FidoAuthServiceBinder;
import com.google.android.material.snackbar.Snackbar;
import com.tbruyelle.rxpermissions3.RxPermissions;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;

    private boolean mBound;
    private FidoAuthService fidoAuthService;

    private RxPermissions rxPermissions;
    private ConstraintLayout constraintLayout;
    private ToggleButton bindFidoAuthServiceToggleButton;
    private Snackbar errorSnackbar;
    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            fidoAuthService = ((FidoAuthServiceBinder) service).getService();
            mBound = true;

            fidoAuthService.getErrors().observe(MainActivity.this, throwable -> {
                showTemporaryMessage(throwable);
                performTroubleshooting(throwable);
            });
        }

        public void onServiceDisconnected(ComponentName className) {
            fidoAuthService = null;
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        rxPermissions = new RxPermissions(this);
        constraintLayout = findViewById(R.id.coordinatorLayout);
        bindFidoAuthServiceToggleButton = findViewById(R.id.advertiseServicesToggleButton);
        errorSnackbar = Snackbar.make(constraintLayout, R.string.error_unknown, Snackbar.LENGTH_SHORT);

        bindFidoAuthServiceToggleButton.setOnClickListener(v -> toggleServiceConnection());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    private void toggleServiceConnection() {
        if (!mBound) {
            Intent intent = new Intent(this, FidoAuthService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            mBound = true;
        } else {
            unbindService(mConnection);
            mBound = false;
        }
    }

    private void checkPermissions() {
        int bluetoothPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
        int bluetoothAdminPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN);
        if (bluetoothPermission != PackageManager.PERMISSION_GRANTED || bluetoothAdminPermission != PackageManager.PERMISSION_GRANTED) {
            showMissingPermissionError();
        }
    }

    private void showMissingPermissionError() {
        errorSnackbar.dismiss();
        errorSnackbar = Snackbar.make(constraintLayout, R.string.error_missing_permissions, Snackbar.LENGTH_INDEFINITE);
        errorSnackbar.setAction(R.string.action_grant_permission, v -> requestMissingPermissions());
        errorSnackbar.show();
    }

    private void requestMissingPermissions() {
        rxPermissions.request(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)
                .subscribe(permissionsGranted -> {
                    if (!permissionsGranted) {
                        showMissingPermissionError();
                    }
                });
    }

    private void checkBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            showBluetoothDisabledError();
        }
    }

    private void showBluetoothDisabledError() {
        errorSnackbar.dismiss();
        errorSnackbar = Snackbar.make(constraintLayout, R.string.error_bluetooth_disabled, Snackbar.LENGTH_INDEFINITE);
        errorSnackbar.setAction(R.string.action_enable, v -> enableBluetooth());
        errorSnackbar.show();
    }

    private void enableBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
    }

    private void performTroubleshooting(@NonNull Throwable throwable) {
        checkPermissions();
        checkBluetoothEnabled();
    }

    private void showTemporaryMessage(@NonNull Throwable throwable) {
        if (throwable.getMessage() != null) {
            showTemporaryMessage(throwable.getMessage());
        } else {
            showTemporaryMessage(throwable.getClass().getSimpleName());
        }
    }

    private void showTemporaryMessage(@NonNull String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
