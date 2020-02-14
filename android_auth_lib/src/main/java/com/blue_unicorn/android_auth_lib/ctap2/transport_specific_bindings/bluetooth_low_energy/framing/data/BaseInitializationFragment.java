package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.framing.data;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.constants.Command;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.constants.Keepalive;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.exceptions.InvalidCommandException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.exceptions.InvalidLengthException;

public class BaseInitializationFragment extends BaseFragment implements InitializationFragment {

    private byte CMD;
    private byte HLEN;
    private byte LLEN;

    public BaseInitializationFragment(byte CMD, byte HLEN, byte LLEN, byte[] DATA) throws InvalidCommandException, InvalidLengthException {
        super(DATA);
        setCMD(CMD);
        setHLEN(HLEN);
        setLLEN(LLEN);

        if (!Command.commands.contains(getCMD()) && !Keepalive.status.contains(getCMD()))
            throw new InvalidCommandException("Invalid command error: initialization fragment command " + getCMD() + " is not specified");
        if (getDATA().length < getHLEN() << 8 + getLLEN())
            throw new InvalidLengthException("Invalid length error: initialization fragment DATA length " + getDATA().length + " is smaller than length specified in command parameters " + (getHLEN() << 8 + getLLEN()));
    }

    public BaseInitializationFragment(byte[] rawFragment) throws InvalidCommandException, InvalidLengthException {
        super(rawFragment, 3);
        setCMD(rawFragment[0]);
        setHLEN(rawFragment[1]);
        setLLEN(rawFragment[2]);

        if (!Command.commands.contains(getCMD()) && !Keepalive.status.contains(getCMD()))
            throw new InvalidCommandException("Invalid command error: initialization fragment command " + getCMD() + " is not specified");
        if (getDATA().length < getHLEN() << 8 + getLLEN())
            throw new InvalidLengthException("Invalid length error: initialization fragment DATA length " + getDATA().length + " is smaller than length specified in command parameters " + (getHLEN() << 8 + getLLEN()));
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
