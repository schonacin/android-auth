package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.constants.Command;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.constants.Keepalive;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.InvalidCommandException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.InvalidLengthException;

public class BaseInitializationFragment extends BaseFragment implements InitializationFragment {

    private byte CMD;
    private byte HLEN;
    private byte LLEN;

    public BaseInitializationFragment(byte CMD, byte HLEN, byte LLEN, byte[] DATA) throws InvalidCommandException, InvalidLengthException {
        this(CMD, HLEN, LLEN, DATA, 0);
    }

    public BaseInitializationFragment(byte[] rawFragment) throws InvalidCommandException, InvalidLengthException {
        this(rawFragment[0], rawFragment[1], rawFragment[2], rawFragment, 3);
    }

    private BaseInitializationFragment(byte CMD, byte HLEN, byte LLEN, byte[] DATA, int offset) throws InvalidCommandException, InvalidLengthException {
        super(DATA);
        setCMD(CMD);
        setHLEN(HLEN);
        setLLEN(LLEN);

        if (!Command.commands.contains(getCMD()) && !Keepalive.status.contains(getCMD()))
            throw new InvalidCommandException("Invalid command error: initialization fragment command " + getCMD() + " is not specified");
        if (getDATA().length > (((getHLEN() & 0xff) << 8) + (getLLEN() & 0xff)))
            throw new InvalidLengthException("Invalid length error: initialization fragment DATA length " + getDATA().length + " is greater than length specified in command parameters " + (((getHLEN() & 0xff) << 8) + (getLLEN() & 0xff)));
    }

    @Override
    public byte getCMD() {
        return CMD;
    }

    @Override
    public void setCMD(byte CMD) {
        this.CMD = CMD;
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
}
