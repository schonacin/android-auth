package com.blue_unicorn.android_auth;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class AuthenticatorZ {

    private Context context;
    private Activity activity;
    private SeekBar freshnessSeekBar;
    private TextView seekbarValueField;
    private Switch authSwitch;
    private boolean continuousAuthenticationSupport;
    private boolean isAuthenticating;

    public AuthenticatorZ(Activity activity) {
        this.activity = activity;
        continuousAuthenticationSupport = true;
    }

    void initiateAuthentication() {
        freshnessSeekBar = activity.findViewById(R.id.seekBar);
        seekbarValueField = activity.findViewById(R.id.seekbarTextField);
        authSwitch = activity.findViewById(R.id.switch1);
        seekbarValueField.setText(Integer.toString(0));
        isAuthenticating = false;
        freshnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekbarValueField.setText(String.valueOf(i*200));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        authSwitch.setOnClickListener(v -> toggleAuthenticated());
    }

    private void toggleAuthenticated() {
        isAuthenticating = !isAuthenticating;
    }

    boolean isInAuthenticationInterval(int freshness) {
        return freshness > Integer.parseInt(seekbarValueField.getText().toString()) && isAuthenticating;
    }

    boolean authenticate() {
        return isAuthenticating;
    }

    public boolean hasContinuousAuthenticationSupport() {
        return continuousAuthenticationSupport;
    }
}
