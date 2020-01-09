package com.blue_unicorn.android_auth_lib.cbor;

import com.blue_unicorn.android_auth_lib.fido.RequestObject;

import io.reactivex.rxjava3.core.Observable;

public interface AuthInputMapper {

    Observable<RequestObject> mapRespectiveCommand();

}
