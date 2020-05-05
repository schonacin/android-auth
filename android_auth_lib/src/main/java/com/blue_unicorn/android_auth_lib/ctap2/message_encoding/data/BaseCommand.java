package com.blue_unicorn.android_auth_lib.ctap2.message_encoding.data;

import com.blue_unicorn.android_auth_lib.ctap2.constants.CommandValue;

public class BaseCommand implements Command {

    private int value;
    private String parameters;

    public BaseCommand(@CommandValue int value, String parameters) {
        this.value = value;
        this.parameters = parameters;
    }

    public int getValue() {
        return value;
    }

    public String getParameters() {
        return parameters;
    }
}
