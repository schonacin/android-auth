package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.framing.data;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.constants.Command;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.exceptions.InvalidCommandException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.exceptions.InvalidLengthException;

// implements concept of frames as described in https://fidoalliance.org/specs/fido-v2.0-ps-20190130/fido-client-to-authenticator-protocol-v2.0-ps-20190130.html#ble-framing
// represents request frames as well as response frames, as the only difference in the CTAP2 specs is the mnemonic of the CMD/STAT field
public class BaseFrame implements Frame {

    private byte CMDSTAT;
    private byte HLEN;
    private byte LLEN;
    private byte[] DATA;

    public BaseFrame(byte CMDSTAT, byte HLEN, byte LLEN, byte[] DATA) throws InvalidCommandException, InvalidLengthException {
        if (!Command.commands.contains(CMDSTAT))
            throw new InvalidCommandException("Invalid command error: frame command " + CMDSTAT + " is not specified");
        if (DATA.length != HLEN << 8 + LLEN)
            throw new InvalidLengthException("Invalid length error: frame DATA length " + DATA.length + " does not equal length specified in command parameters " + (HLEN << 8 + LLEN));

        setCMDSTAT(CMDSTAT);
        setHLEN(HLEN);
        setLLEN(LLEN);
        setDATA(DATA);
    }

    @Override
    public byte getCMDSTAT() {
        return CMDSTAT;
    }

    @Override
    public void setCMDSTAT(byte CMDSTAT) {
        this.CMDSTAT = CMDSTAT;
    }

    @Override
    public byte getHLEN() {
        return HLEN;
    }

    @Override
    public void setHLEN(byte HLEN) {
        this.HLEN = HLEN;
    }

    @Override
    public byte getLLEN() {
        return LLEN;
    }

    @Override
    public void setLLEN(byte LLEN) {
        this.LLEN = LLEN;
    }

    @Override
    public byte[] getDATA() {
        return DATA;
    }

    @Override
    public void setDATA(byte[] DATA) {
        this.DATA = DATA;
    }
}
