package com.blue_unicorn.android_auth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.blue_unicorn.android_auth_lib.android.FidoAuthService;
import com.blue_unicorn.android_auth_lib.android.FidoAuthServiceBinder;
import com.blue_unicorn.android_auth_lib.android.constants.IntentAction;
import com.blue_unicorn.android_auth_lib.android.constants.IntentExtra;
import com.google.android.material.snackbar.Snackbar;
import com.tbruyelle.rxpermissions3.RxPermissions;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;

    private boolean mBound;
    private static final byte[] RAW_MAKE_CREDENTIAL_REQUEST = Base64.decode("AaUBWCDMVG/Vi0CDgAtgnuEKnn1oM9yeVFNAkbx+pfadNopNfAKiYmlka3dlYmF1dGhuLmlvZG5hbWVrd2ViYXV0aG4uaW8Do2JpZEq3mQEAAAAAAAAAZG5hbWVkVXNlcmtkaXNwbGF5TmFtZWR1c2VyBIqiY2FsZyZkdHlwZWpwdWJsaWMta2V5omNhbGc4ImR0eXBlanB1YmxpYy1rZXmiY2FsZzgjZHR5cGVqcHVibGljLWtleaJjYWxnOQEAZHR5cGVqcHVibGljLWtleaJjYWxnOQEBZHR5cGVqcHVibGljLWtleaJjYWxnOQECZHR5cGVqcHVibGljLWtleaJjYWxnOCRkdHlwZWpwdWJsaWMta2V5omNhbGc4JWR0eXBlanB1YmxpYy1rZXmiY2FsZzgmZHR5cGVqcHVibGljLWtleaJjYWxnJ2R0eXBlanB1YmxpYy1rZXkFgA==", Base64.DEFAULT);
    private static final byte[] RAW_GET_ASSERTION_REQUEST_WITH_EXTENSION_PARAMETER = Base64.decode("AqQBa3dlYmF1dGhuLmlvAlggaHE0loIi7BcgLkJQX47SsWriLxa7BbiMJdueYCZF8UEEoXgZY29udGludW91c19hdXRoZW50aWNhdGlvbhknEAWhYnV29Q==", Base64.DEFAULT);

    public boolean getAssertionContinuous = false;
    private Button bindGetInfoMakeCredentialButton;
    public ToggleButton bindGetAssertionContinuousButton;
    private ToggleButton bindFidoAuthServiceToggleButton;

    private FidoAuthService fidoAuthService;

    private AuthenticatorZ authenticatorZ;

    private RxPermissions rxPermissions;
    private ConstraintLayout constraintLayout;
    private Snackbar errorSnackbar;
    private ContAuthMockClient mockClient;
    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            fidoAuthService = ((FidoAuthServiceBinder) service).getService();
            mockClient = new ContAuthMockClient(new Handler(Looper.getMainLooper()), fidoAuthService, MainActivity.this);
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
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        rxPermissions = new RxPermissions(this);
        constraintLayout = findViewById(R.id.coordinatorLayout);
        bindFidoAuthServiceToggleButton = findViewById(R.id.advertiseServicesToggleButton);
        bindGetInfoMakeCredentialButton = findViewById(R.id.getInfoMakeCredentialButton);
        bindGetAssertionContinuousButton = findViewById(R.id.ContinuousGetAssertionButton);
        errorSnackbar = Snackbar.make(constraintLayout, R.string.error_unknown, Snackbar.LENGTH_SHORT);
        authenticatorZ = new AuthenticatorZ(this);

        bindFidoAuthServiceToggleButton.setOnClickListener(v -> toggleServiceConnection());
        bindGetInfoMakeCredentialButton.setOnClickListener(v -> mockClient.sendGetInfoAndMakeCredential());
        bindGetAssertionContinuousButton.setOnClickListener(v -> toggleGetAssertionContinuous());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction() == null) {
            return;
        }
        Timber.d("New intent in MainActivity with action %s", intent.getAction());
        // TODO: find a way to close the notification within its action or via JobIntentService
        fidoAuthService.closeNotification();
        @IntentAction String intentAction = intent.getAction();
        switch (intentAction) {
            case IntentAction.CTAP_APPROVE_NOTIFICATION:
                fidoAuthService.handleUserInteraction(true);
                break;
            case IntentAction.CTAP_DECLINE_NOTIFICATION:
                fidoAuthService.handleUserInteraction(false);
                break;
            case IntentAction.CTAP_APPROVE_NOTIFICATION_FOR_AUTHENTICATION:
            case IntentAction.CTAP_PERFORM_AUTHENTICATION:
                // TODO: Put call to custom authentication here and call handleCustomAuthentication with result
                Timber.d("AUTHENTICATION REQUESTED VIA INTENT");
                Timber.d(String.valueOf(intent.getExtras()));
                Integer freshness = intent.getExtras() == null ? null : (Integer)intent.getExtras().get(IntentExtra.AUTHENTICATION_FRESHNESS);
                boolean authenticated;
                if (freshness != null) {
                    authenticated = authenticatorZ.isInAuthenticationInterval(freshness);
                } else {
                    authenticated = authenticatorZ.authenticate();
                }
                fidoAuthService.handleUserInteraction(authenticated);
                break;
            case IntentAction.CHECK_FOR_CONTINUOUS_AUTHENTICATION:
                fidoAuthService.handleUserInteraction(authenticatorZ.hasContinuousAuthenticationSupport());
                break;
            default:
                break;
        }
    }

    private void toggleServiceConnection() {
        if (!mBound) {
            Intent intent = new Intent(this, FidoAuthService.class);
            intent.putExtra(IntentExtra.ACTIVITY_CLASS, MainActivity.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            findViewById(R.id.frameLayout).setVisibility(View.VISIBLE);
            authenticatorZ.initiateAuthentication();
            mBound = true;
        } else {
            unbindService(mConnection);
            findViewById(R.id.frameLayout).setVisibility(View.INVISIBLE);
            mBound = false;
        }
    }

    private void toggleGetAssertionContinuous() {
        if (!getAssertionContinuous) {
            Timber.d("Starting to send getAssertion continuously");
            mockClient.startGetAssertionContinuous(9000);
            getAssertionContinuous = true;
        } else {
            Timber.d("Stopped Continuous getAssertions");
            mockClient.stopGetAssertionContinuous();
            getAssertionContinuous = false;
        }
    }

    private void checkPermissions() {
        int bluetoothPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
        int bluetoothAdminPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN);
        int bluetoothCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (bluetoothPermission != PackageManager.PERMISSION_GRANTED
                || bluetoothAdminPermission != PackageManager.PERMISSION_GRANTED
                || bluetoothCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
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

    public boolean isBound() {
        return mBound;
    }

    public FidoAuthService getFidoAuthService() {
        return fidoAuthService;
    }
}
