package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing;

import com.blue_unicorn.android_auth_lib.android.constants.LogIdentifier;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Fragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Frame;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import timber.log.Timber;

public class BaseDefragmentationProvider implements RxDefragmentationProvider {

    @Override
    public Flowable<Frame> defragment(Flowable<Fragment> fragments, int maxLen) {

        return fragments.scanWith(() -> new BaseFrameAccumulator(maxLen), (accumulator, fragment) -> {
            if (accumulator.isComplete()) {
                return new BaseFrameAccumulator(maxLen, fragment);
            }
            accumulator.addFragment(fragment);
            return accumulator;
        }).filter(FrameAccumulator::isComplete).map(FrameAccumulator::getAssembledFrame);
    }

    private FrameAccumulator frameAccumulator;

    @Override
    public Maybe<Frame> defragment(Fragment fragment, int maxLen) {
        Timber.d("Defragment fragment %s based on maxLength of %d", fragment.asBytes(), maxLen);
        return Single.just(fragment)
                .map(frag -> {
                    // TODO: change place of logs??
                    Timber.d("%s: %s", LogIdentifier.DIAG, LogIdentifier.RECEIVING_FRAME);
                    if (frameAccumulator == null || frameAccumulator.isComplete()) {
                        Timber.d("%s: %s", LogIdentifier.DIAG, LogIdentifier.START_DEFRAG);
                        frameAccumulator = new BaseFrameAccumulator(maxLen);
                    }
                    frameAccumulator.addFragment(fragment);
                    if (frameAccumulator.isComplete()) {
                        Timber.d("%s: %s", LogIdentifier.DIAG, LogIdentifier.STOP_DEFRAG);
                    }
                    return frameAccumulator;
                }).filter(FrameAccumulator::isComplete)
                .map(FrameAccumulator::getAssembledFrame);
    }

}
