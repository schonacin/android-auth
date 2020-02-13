package com.blue_unicorn.android_auth_lib.api.authenticator;

import com.blue_unicorn.android_auth_lib.api.authenticator.database.PublicKeyCredentialSource;
import com.blue_unicorn.android_auth_lib.api.exceptions.CredentialExcludedException;
import com.blue_unicorn.android_auth_lib.api.exceptions.OperationDeniedException;
import com.blue_unicorn.android_auth_lib.api.exceptions.OtherException;
import com.blue_unicorn.android_auth_lib.api.exceptions.UnsupportedAlgorithmException;
import com.blue_unicorn.android_auth_lib.fido.reponse.BaseMakeCredentialResponse;
import com.blue_unicorn.android_auth_lib.fido.request.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.fido.reponse.MakeCredentialResponse;
import com.blue_unicorn.android_auth_lib.fido.webauthn.PackedAttestationStatement;
import com.blue_unicorn.android_auth_lib.util.ArrayUtil;
import com.nexenio.rxkeystore.provider.asymmetric.RxAsymmetricCryptoProvider;

import hu.akarnokd.rxjava3.bridge.RxJavaBridge;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

class MakeCredential {

    private CredentialSafe credentialSafe;
    private RxAsymmetricCryptoProvider cryptoProvider;
    private MakeCredentialRequest request;
    private AuthenticatorHelper helper;

    //TODO maybe make this a singleton
    MakeCredential(CredentialSafe credentialSafe, MakeCredentialRequest request) {
        this.credentialSafe = credentialSafe;
        this.cryptoProvider = this.credentialSafe.getCryptoProvider();
        this.request = request;
        this.helper = new AuthenticatorHelper(this.credentialSafe);
    }

    Single<MakeCredentialRequest> operate() {
        // 0. check if request is valid
        // 1. check if excludeList parameter is present
        // 2. check for valid algorithm
        // 3. check options
        return validate()
                .andThen(this.setExcludeListParameter())
                .andThen(this.checkForValidAlgorithm())
                .andThen(this.checkOptions())
                .andThen(Single.just(request));
    }

        // we skip 4. - 7. (extensions & pinAuth are not supported)

    Single<MakeCredentialResponse> operateInner() {
        // 8. handle user confirmation & whether credential was excluded
        // 9. - 11. create key pair return response
        return handleUserApproval()
                .andThen(this.constructResponse());
    }

    private Completable validate() {
        return Completable.defer(() -> {
            if (request.isValid()) {
                return Completable.complete();
            } else {
                return Completable.error(OtherException::new);
            }
        });
    }

    private Completable setExcludeListParameter() {
        return Completable.defer(() -> {
            if (request.getExcludeList() != null) {
                return Single.just(request.getExcludeList())
                        .flatMapPublisher(Flowable::fromIterable)
                        .switchMapSingle(descriptor -> this.credentialSafe.getCredentialSourceById(descriptor.getId()))
                        .map(credentialSource -> (credentialSource != null && credentialSource.rpId.equals(request.getRp().getId())))
                        .contains(true)
                        .flatMapCompletable(excluded -> {
                            request.setExcluded(excluded);
                            return Completable.complete();
                        });
            } else {
                return Completable.fromAction(() -> request.setExcluded(false));
            }
        });
    }

    private Completable checkForValidAlgorithm() {
        return Single.just(request.getPubKeyCredParams())
                .flatMapPublisher(Flowable::fromArray)
                .map(map -> map.get("alg"))
                .cast(Double.class)
                .map(Double::intValue)
                .contains(-7)
                .flatMapCompletable(isSupported -> {
                    if (isSupported) {
                        return Completable.complete();
                    } else {
                        return Completable.error(UnsupportedAlgorithmException::new);
                    }
                });
    }

    private Completable checkOptions() {
        return Completable.complete();
    }

    private Completable handleUserApproval() {
        return Completable.defer(() -> {
            if(!request.isApproved()) {
                return Completable.error(OperationDeniedException::new);
            } else if(request.isExcluded()) {
                return Completable.error(CredentialExcludedException::new);
            } else {
                return Completable.complete();
            }
        });
    }

    private Single<MakeCredentialResponse> constructResponse() {
        return generateCredential()
                .flatMap(credential -> Single.zip(helper.hashSha256(request.getRp().getId()), helper.constructAttestedCredentialData(credential), helper::constructAuthenticatorData)
                        .flatMap(x -> x)
                        // TODO create Wrapper for nexenios library which handles the conversions
                        .flatMap(authData -> Single.zip(Single.just(ArrayUtil.concatBytes(authData, request.getClientDataHash())), credentialSafe.getPrivateKeyByAlias(credential.keyPairAlias), cryptoProvider::sign)
                                .flatMap(single -> single.as(RxJavaBridge.toV3Single()))
                                .map(PackedAttestationStatement::new)
                                .map(attStmt -> new BaseMakeCredentialResponse(authData, attStmt))));
    }

    private Single<PublicKeyCredentialSource> generateCredential() {
        return credentialSafe.generateCredential(request.getRp().getId(), request.getUser().getId(), request.getUser().getName());
    }
}
