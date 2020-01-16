package com.blue_unicorn.android_auth_lib.framing;

import java.util.ArrayList;
import java.util.List;

// implements concept of frames as described in https://fidoalliance.org/specs/fido-v2.0-ps-20190130/fido-client-to-authenticator-protocol-v2.0-ps-20190130.html#ble-framing
// represents request frames as well as response frames, as the only difference in the CTAP2 specs is the mnemonic of the CMD/STAT field
public class Frame {
    private byte CMDSTAT;
    private byte HLEN;
    private byte LLEN;
    private byte[] DATA;

    public Frame(byte CMDSTAT, byte HLEN, byte LLEN, byte[] DATA) {
        if(DATA.length != HLEN << 8 + LLEN)
            return; // TODO: throw error: frame construction failed: malformed parameters
        this.CMDSTAT = CMDSTAT;
        this.HLEN = HLEN;
        this.LLEN = LLEN;
        this.DATA = DATA;
    }

    public byte getCMDSTAT() {
        return CMDSTAT;
    }

    public byte getHLEN() {
        return HLEN;
    }

    public byte getLLEN() {
        return LLEN;
    }

    public byte[] getDATA() {
        return DATA;
    }
}
