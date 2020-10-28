package com.blue_unicorn.android_auth_lib.android.constants;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
        LogIdentifier.DIAG,
        LogIdentifier.START_BLUETOOTH_ADV,
        LogIdentifier.STOP_BLUETOOTH_ADV,
        LogIdentifier.START_FRAG,
        LogIdentifier.STOP_FRAG,
        LogIdentifier.RECEIVING_FRAME,
        LogIdentifier.START_DECODE,
        LogIdentifier.STOP_DECODE,
        LogIdentifier.START_OPERATION,
        LogIdentifier.STOP_OPERATION,
        LogIdentifier.START_USER_INTERACTION,
        LogIdentifier.STOP_USER_INTERACTION,
        LogIdentifier.START_ENCODE,
        LogIdentifier.STOP_ENCODE,
        LogIdentifier.START_DEFRAG,
        LogIdentifier.STOP_DEFRAG,
        LogIdentifier.SENDING_FRAME
})

public @interface LogIdentifier {
    String DIAG = "DIAG";
    String START_BLUETOOTH_ADV = "START_ADV";
    String STOP_BLUETOOTH_ADV = "STOP_ADV";
    String START_FRAG = "START_FRAG";
    String STOP_FRAG = "STOP_FRAG";
    String RECEIVING_FRAME = "RECEVING_FRAME";
    String START_DECODE = "START_DECODE";
    String STOP_DECODE = "STOP_DECODE";
    String START_OPERATION = "START_OPERATION";
    String STOP_OPERATION = "STOP_OPERATION";
    String START_USER_INTERACTION = "START_USER";
    String STOP_USER_INTERACTION = "STOP_USER";
    String START_ENCODE = "START_ENCODE";
    String STOP_ENCODE = "STOP_ENCODE";
    String START_DEFRAG = "START_DEFRAG";
    String STOP_DEFRAG = "STOP_DEFRAG";
    String SENDING_FRAME = "SENDING FRAME";
}
