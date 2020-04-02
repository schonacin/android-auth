package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data;

/*
 * Represents initialization fragment that forms a frame together with continuation fragments.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-ps-20190130/fido-client-to-authenticator-protocol-v2.0-ps-20190130.html#ble-framing-fragmentation">corresponding section of the CTAP2 specification</>.
 */
public interface InitializationFragment extends Fragment {

    byte getCMD();

    void setCMD(byte CMD);

    byte getHLEN();

    void setHLEN(byte HLEN);

    byte getLLEN();

    void setLLEN(byte LLEN);
}
