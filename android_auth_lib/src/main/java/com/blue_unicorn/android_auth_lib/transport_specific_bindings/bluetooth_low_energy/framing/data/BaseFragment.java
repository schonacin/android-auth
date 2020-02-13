package com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing.data;

public abstract class BaseFragment implements Fragment {

    private byte[] DATA;

    public BaseFragment(byte[] DATA) {
        this.DATA = DATA;
    }

    public BaseFragment(byte[] rawFragment, int offset) {
        this.DATA = new byte[rawFragment.length - offset];
        System.arraycopy(rawFragment, offset, this.DATA, 0, this.DATA.length);
    }

    @Override
    public byte[] getDATA() {
        return DATA;
    }
}
