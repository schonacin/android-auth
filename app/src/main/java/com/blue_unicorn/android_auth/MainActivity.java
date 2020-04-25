package com.blue_unicorn.android_auth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.blue_unicorn.android_auth_lib.android.FidoAuthService;
import com.google.android.material.snackbar.Snackbar;
import com.tbruyelle.rxpermissions3.RxPermissions;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    // Don't attempt to unbind from the service unless the client has received some
    // information about the service's state.
    private boolean mShouldUnbind;

    private ServiceConnection mConnection = new FidoAuthServiceConnection();

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;

    private ViewModel viewModel;
    private RxPermissions rxPermissions;
    private ConstraintLayout constraintLayout;
    private ToggleButton advertiseServicesToggleButton;
    private Snackbar errorSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("START");
        setContentView(R.layout.activity_main);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        rxPermissions = new RxPermissions(this);
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        constraintLayout = findViewById(R.id.coordinatorLayout);
        advertiseServicesToggleButton = findViewById(R.id.advertiseServicesToggleButton);
        advertiseServicesToggleButton.setOnClickListener(v -> viewModel.toggleAdvertisingServices());

        errorSnackbar = Snackbar.make(constraintLayout, R.string.error_unknown, Snackbar.LENGTH_SHORT);

        // TODO: make this reactive
        //bindToFidoAuthService();

        viewModel.isAdvertisingService().observe(this, isAdvertisingService -> {
            if (isAdvertisingService) {
                onServiceAdvertisingStarted();
            } else {
                onServiceAdvertisingStopped();
            }
        });

        viewModel.getErrors().observe(this, throwable -> {
            showTemporaryMessage(throwable);
            performTroubleshooting(throwable);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unbindFromFidoAuthService();
    }

    private void onServiceAdvertisingStarted() {
        advertiseServicesToggleButton.setChecked(true);
    }

    private void onServiceAdvertisingStopped() {
        advertiseServicesToggleButton.setChecked(false);
    }


    // Fido Auth Service

    private void bindToFidoAuthService() {
        // Attempts to establish a connection with the service.  We use an
        // explicit class name because we want a specific service
        // implementation that we know will be running in our own process
        // (and thus won't be supporting component replacement by other
        // applications).
        if (bindService(new Intent(MainActivity.this, FidoAuthService.class),
                mConnection, Context.BIND_AUTO_CREATE)) {
            mShouldUnbind = true;
        } else {
            Timber.e("Error: The requested service doesn't exist, or this client isn't allowed access to it.");
        }
    }

    private void unbindFromFidoAuthService() {
        if (mShouldUnbind) {
            // Release information about the service's state.
            unbindService(mConnection);
            mShouldUnbind = false;
        }
    }


    // Permissions

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

    @SuppressLint("CheckResult")
    private void requestMissingPermissions() {
        rxPermissions.request(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)
                .subscribe(permissionsGranted -> {
                    if (!permissionsGranted) {
                        showMissingPermissionError();
                    }
                });
    }


    // Bluetooth

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


    // Util

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
