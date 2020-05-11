package com.blue_unicorn.android_auth_lib.android.layers;

import android.content.Context;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.APIHandler;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.BaseAPIHandler;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.FidoObject;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.RequestObject;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.ResponseObject;
import com.blue_unicorn.android_auth_lib.ctap2.message_encoding.BaseCborHandler;
import com.blue_unicorn.android_auth_lib.ctap2.message_encoding.CborHandler;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.BaseFragmentationProvider;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.RxFragmentationProvider;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.BaseFrame;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Fragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Frame;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

public class APILayer {

    private CborHandler cborHandler;
    private APIHandler apiHandler;
    private RxFragmentationProvider fragmentationProvider;

    private ResponseLayer responseLayer;



    APILayer(Context context) {
        this.cborHandler = new BaseCborHandler();
        this.apiHandler = new BaseAPIHandler(context);
        this.fragmentationProvider = new BaseFragmentationProvider();
        this.responseLayer = new ResponseLayer();
    }

    public void buildNewRequestChain(byte[] input) {
        SingleObserver<FidoObject> apiSubscriber = new SingleObserver<FidoObject>() {
            @Override
            public void onSubscribe(Disposable d){
            }

            @Override
            public void onSuccess(FidoObject result){
                if (result instanceof ResponseObject)
                    buildResponseChainWithoutUserInteraction((ResponseObject) result);
                else
                    buildNotification((RequestObject) result);
            }

            @Override
            public void onError(Throwable t) {
                ErrorLayer.handleErrors(t);
            }
        };

        cborHandler.decode(input)
                .flatMap(apiHandler::callAPI)
                .subscribe(apiSubscriber);
    }

    private void buildResponseChainWithoutUserInteraction(ResponseObject response) {
        // this chain will be built if there is no user interaction required
        cborHandler.encode(response)
                .map(BaseFrame::new)
                .cast(Frame.class)
                .flatMapPublisher(frame -> fragmentationProvider.fragment(Single.just(frame), getMaxLength()))
                .map(Fragment::asBytes)
                .subscribe(responseLayer.createNewResponseSubscriber());
    }

    private void buildResponseChainAfterUserInteraction(RequestObject request) {
        // this chain is called after the user has interacted with the device
        // this function will be called from outside, i. e. a new intent on a service
        apiHandler.updateAPI(request)
                .flatMap(cborHandler::encode)
                .map(BaseFrame::new)
                .cast(Frame.class)
                .flatMapPublisher(frame -> fragmentationProvider.fragment(Single.just(frame), getMaxLength()))
                .map(Fragment::asBytes)
                .subscribe(responseLayer.createNewResponseSubscriber());
    }

    private void buildNotification(RequestObject request) {

    }

    private int getMaxLength() {
        return 20;
    }

}
