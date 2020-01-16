package com.blue_unicorn.android_auth_lib.framing;

import io.reactivex.rxjava3.core.*;

// TODO: merge with FrameBuffer and make FrameBuffer reactive?
// assumption:
// mtu does not change in between transmission of fragments of the same frame
// TODO: fix code so that mtu change does not break stuff
public class FragmentationHandler {

    private int mtu;
    private int remainingFrameSize;

    public FragmentationHandler(int mtu, int frameSize) {
        this.mtu = mtu;
        this.remainingFrameSize = frameSize;
    }

    public Single<Frame> defragment(Flowable<Fragment> fragments, int mtu) {
        return fragments.collect(() -> new FrameBuffer(this.mtu), FrameBuffer::addFragment)
                        .map(FrameBuffer::getFrame);
    }

    // TODO: decide for a backpressure strategy
    public Flowable<Fragment> fragment(Single<Frame> frame) {
        return frame.flatMapObservable(f -> Observable.fromIterable(new FrameBuffer(mtu, f).getFragments()))
                .toFlowable();
    }
}
