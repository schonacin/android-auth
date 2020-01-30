package com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing.data;

import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.constants.Command;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.constants.Keepalive;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.exceptions.InvalidCommandException;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.exceptions.InvalidLengthException;

public class BaseInitializationFragment extends BaseFragment implements InitializationFragment {

    private byte CMD;
    private byte HLEN;
    private byte LLEN;

    public BaseInitializationFragment(byte CMD, byte HLEN, byte LLEN, byte[] DATA) throws InvalidCommandException, InvalidLengthException {
        super(DATA);
        this.CMD = CMD;
        this.HLEN = HLEN;
        this.LLEN = LLEN;

        if(!Command.commands.contains(CMD) && !Keepalive.status.contains(CMD))
            throw new InvalidCommandException("Invalid command error: initialization fragment command " + CMD + " is not specified");
        if(super.getDATA().length < HLEN << 8 + LLEN)
            throw new InvalidLengthException("Invalid length error: initialization fragment DATA length " + super.getDATA().length + " is smaller than length specified in command parameters " + (HLEN << 8 + LLEN));
    }

    public BaseInitializationFragment(byte[] rawFragment) throws InvalidCommandException, InvalidLengthException {
        super(rawFragment, 3);
        this.CMD = rawFragment[0];
        this.HLEN = rawFragment[1];
        this.LLEN = rawFragment[2];

        if(!Command.commands.contains(CMD) && !Keepalive.status.contains(CMD))
            throw new InvalidCommandException("Invalid command error: initialization fragment command " + CMD + " is not specified");
        if(super.getDATA().length < HLEN << 8 + LLEN)
            throw new InvalidLengthException("Invalid length error: initialization fragment DATA length " + super.getDATA().length + " is smaller than length specified in command parameters " + (HLEN << 8 + LLEN));
    }

    public byte getCMD() {
        return CMD;
    }

    public byte getHLEN() {
        return HLEN;
    }

    public byte getLLEN() {
        return LLEN;
    }
}
