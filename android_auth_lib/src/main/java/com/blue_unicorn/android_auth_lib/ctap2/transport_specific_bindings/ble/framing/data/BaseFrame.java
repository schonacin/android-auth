package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.constants.Command;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.InvalidCommandException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.InvalidLengthException;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class BaseFrame implements Frame {

    private byte CMDSTAT;
    private byte HLEN;
    private byte LLEN;
    private byte[] DATA;

    // TODO: this could be done with the Builder Pattern in order to avoid confusion
    // This constructor is used to generate a Message BaseFrame, hence the 0x83 bit (change to constant)
    public BaseFrame(byte[] DATA) throws InvalidCommandException, InvalidLengthException {
        this((byte)0x83,(byte)(DATA.length >> 8), (byte)(DATA.length & 0xff), DATA);
    }

    public BaseFrame(byte CMDSTAT, byte HLEN, byte LLEN, byte[] DATA) throws InvalidCommandException, InvalidLengthException {
        if (!Command.commands.contains(CMDSTAT))
            throw new InvalidCommandException("Invalid command error: frame command " + CMDSTAT + " is not specified");
        if (DATA.length != (((HLEN & 0xff) << 8) + (LLEN & 0xff)))
            throw new InvalidLengthException("Invalid length error: frame DATA length " + DATA.length + " does not equal length specified in command parameters " + (((HLEN & 0xff) << 8) + (LLEN & 0xff)));

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseFrame baseFrame = (BaseFrame) o;
        return getCMDSTAT() == baseFrame.getCMDSTAT() &&
                getHLEN() == baseFrame.getHLEN() &&
                getLLEN() == baseFrame.getLLEN() &&
                Arrays.equals(getDATA(), baseFrame.getDATA());
    }

    @NotNull
    @Override
    public String toString() {
        return "BaseFrame{" +
                "CMDSTAT=" + CMDSTAT +
                ", HLEN=" + HLEN +
                ", LLEN=" + LLEN +
                ", DATA=" + Arrays.toString(DATA) +
                '}';
    }
}
