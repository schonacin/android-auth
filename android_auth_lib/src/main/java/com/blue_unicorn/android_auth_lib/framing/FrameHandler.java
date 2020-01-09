package com.blue_unicorn.android_auth_lib.framing;

import io.reactivex.rxjava3.core.*;
import hu.akarnokd.rxjava3.operators.*;

public class FrameHandler {

    private int mtu;
    private int frameSize;//remaining?
    private int bufferSize = 4096;


    public FrameHandler(int mtu, int frameSize) {
        this.mtu = mtu;
        this.frameSize = frameSize;
    }

    public FrameHandler(int mtu, int frameSize, int BUFFER_SIZE) {
        this.mtu = mtu;
        this.frameSize = frameSize;
        this.bufferSize = BUFFER_SIZE;
    }

    // TODO: this is shit, make it better (it's stateful 'n' stuff)
    private boolean isLastFragmentOfFrame(Fragment frag) {
        if(frag instanceof ContinuationFragment)
            frameSize -= ((ContinuationFragment) frag).DATA.length;
        else if(frag instanceof InitializationFragment)
            frameSize = ((InitializationFragment) frag).HLEN << 8 + ((InitializationFragment) frag).LLEN - ((InitializationFragment) frag).DATA.length;
        //else
            // TODO: return error code / exception ?
        return frameSize <= 0;
    }

    // TODO: call in defragment() or not?
    /*public Flowable<Fragment> toFragments(Flowable<byte[]> rawFragments) {
        return rawFragments.map();
    }*/

    // implements defragmentation as described in https://fidoalliance.org/specs/fido-v2.0-ps-20190130/fido-client-to-authenticator-protocol-v2.0-ps-20190130.html#ble-framing-fragmentation
    // assumptions made:
    // - first fragment is always initialization fragment
    // - continuation fragments must be in correct order (sequence numbers would not make sense then)
    // - if there is sequence number wraparound, continuation fragments with same sequence number are in correct order among each other
    public Flowable<Frame> defragment(Flowable<Fragment> fragments, int mtu) {

        return fragments

                // divide fragments into windows of fragments containing all fragments of a frame
                .compose(FlowableTransformers.windowUntil(this::isLastFragmentOfFrame))

                // collect fragments of each window into a frame
                .concatMap(frameWindow -> frameWindow.collect(() -> new Frame(mtu), Frame::addFragment).toFlowable());
    }

    // TODO: implement fragmentation
    /*public Flowable<Fragment> fragment(Flowable<Frame> frames) {
        return frames.concatMap(frame -> Flowable.just(frame.getFragments(mtu)));
    }*/

    public void updateMtu(int mtu) {
        this.mtu = mtu;
        // TODO: this could break stuff, handle it
        //  e. g. update only after currently performed fragmentation/defragmentation is finished
    }
}
