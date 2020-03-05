package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data;

/*
 * Represents continuation fragments that form a frame together with an initialization fragment.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-ps-20190130/fido-client-to-authenticator-protocol-v2.0-ps-20190130.html#ble-framing-fragmentation">corresponding section of the CTAP2 specification</>.
 */
public interface ContinuationFragment extends Fragment {

    byte getSEQ();

    void setSEQ(byte SEQ);
}
