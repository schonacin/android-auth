package com.blue_unicorn.android_auth_lib.cbor;

class BaseCommand implements Command {

    private byte value;
    private String parameters;

    BaseCommand(byte value, String parameters) {
        this.value = value;
        this.parameters = parameters;
    }

    public byte getValue() {
        return value;
    }

    public String getParameters() {
        return parameters;
    }
}
