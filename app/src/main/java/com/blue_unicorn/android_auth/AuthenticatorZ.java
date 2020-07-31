package com.blue_unicorn.android_auth;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class AuthenticatorZ {

    Context context;
    Activity activity;
    private SeekBar freshnessSeekBar;
    private TextView seekbarValueField;

    public AuthenticatorZ(Activity mainActivity) {
        this.activity = mainActivity;
    }

    void initiateAuthentication() {
        freshnessSeekBar = activity.findViewById(R.id.seekBar);
        seekbarValueField = activity.findViewById(R.id.seekbarTextField);
        seekbarValueField.setText(Integer.toString(0));
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
    }

    boolean isInAuthenticationInterval(int freshness) {
        return freshness > Integer.parseInt(seekbarValueField.getText().toString()) && activity.findViewById(R.id.switch1).isActivated();
    }

    boolean authenticate() {
        return activity.findViewById(R.id.switch1).isActivated();
    }
}
