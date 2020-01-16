package com.blue_unicorn.android_auth_lib.framing;

import io.reactivex.rxjava3.core.*;
import hu.akarnokd.rxjava3.operators.*;

// TODO: merge with FrameBuffer?
public class FragmentationHandler {

    private int mtu;
    private int remainingFrameSize;

    public FragmentationHandler(int mtu, int frameSize) {
        this.mtu = mtu;
        this.remainingFrameSize = frameSize;
    }

    // TODO: this is shit, make it better (it's stateful 'n' stuff)
    private boolean isLastFragmentOfFrame(Fragment frag) {
        if (frag instanceof ContinuationFragment)
            remainingFrameSize -= ((ContinuationFragment) frag).DATA.length;
        else if (frag instanceof InitializationFragment)
            remainingFrameSize = ((InitializationFragment) frag).HLEN << 8 + ((InitializationFragment) frag).LLEN - ((InitializationFragment) frag).DATA.length;
        //else
        // TODO: return error code / exception ?
        return remainingFrameSize <= 0;
    }

    // implements defragmentation as described in https://fidoalliance.org/specs/fido-v2.0-ps-20190130/fido-client-to-authenticator-protocol-v2.0-ps-20190130.html#ble-framing-fragmentation
    public Single<Frame> defragment(Flowable<Fragment> fragments, int mtu) {

        // divide fragments into windows of fragments containing all fragments of a frame
        // TODO: oursource isLastFragmentOfFrame to place where defragment is called and remove this, return Single<Frame> instead of Flowable (subscribe every time new frame begins)
        return fragments.compose(FlowableTransformers.windowUntil(this::isLastFragmentOfFrame))

                // collect fragments of each window into a frame
                .concatMap(frameWindow -> frameWindow.collect(() -> new FrameBuffer(this.mtu), FrameBuffer::addFragment)
                        .map(frameBuffer -> frameBuffer.getFrame())
                        .toSingle());

    }

    public Flowable<Fragment> fragment(Flowable<Frame> frames) {
        return frames.concatMap(frame -> Flowable.fromIterable(new FrameBuffer(mtu, frame).getFragments()));
    }

    public void updateMtu(int mtu) {
        this.mtu = mtu;
        // TODO: this could break stuff, handle it
        //  e. g. update only after currently performed fragmentation/defragmentation is finished
    }
}
