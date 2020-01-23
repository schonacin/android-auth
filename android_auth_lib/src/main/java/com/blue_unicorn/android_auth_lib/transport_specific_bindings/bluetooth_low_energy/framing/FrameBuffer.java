package com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing;

import java.util.List;

public interface FrameBuffer {

    void addFragment(BaseFragment fragment);

    boolean isComplete();

    BaseFrame getFrame();

    List<BaseFragment> getFragments();
}
