package com.blue_unicorn.android_auth_lib.api.authenticator;

import com.blue_unicorn.android_auth_lib.api.exceptions.NoCredentialsException;
import com.blue_unicorn.android_auth_lib.api.exceptions.OperationDeniedException;
import com.blue_unicorn.android_auth_lib.api.exceptions.OtherException;
import com.blue_unicorn.android_auth_lib.fido.reponse.BaseGetAssertionResponse;
import com.blue_unicorn.android_auth_lib.fido.reponse.GetAssertionResponse;
import com.blue_unicorn.android_auth_lib.fido.request.GetAssertionRequest;

import com.blue_unicorn.android_auth_lib.fido.webauthn.BasePublicKeyCredentialDescriptor;
import com.blue_unicorn.android_auth_lib.fido.webauthn.PublicKeyCredentialDescriptor;
import com.blue_unicorn.android_auth_lib.util.ArrayUtil;
import com.nexenio.rxkeystore.provider.asymmetric.RxAsymmetricCryptoProvider;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

class GetAssertion {

    private CredentialSafe credentialSafe;
    private RxAsymmetricCryptoProvider cryptoProvider;
    private AuthenticatorHelper helper;
    private GetAssertionRequest request;

    //TODO maybe make this a singleton
    GetAssertion(CredentialSafe credentialSafe, GetAssertionRequest request) {
        this.credentialSafe = credentialSafe;
        this.cryptoProvider = this.credentialSafe.getCryptoProvider();
        this.request = request;
        this.helper = new AuthenticatorHelper(this.credentialSafe);
    }

    Single<GetAssertionRequest> operate() {
        // 0. check if request is valid
        // 1. check credentials in allowList if Existent
        // 5. check options
        return validate()
                .andThen(this.checkForCredentials())
                .andThen(this.checkOptions())
                .andThen(Single.just(request));
    }

        // 2. - 4. & 6. is ignored as we do pinAuth and extensions are not supported

    Single<GetAssertionResponse> operateInner() {
        // 7. - 8. handle user approval and whether credentials could be found
        // 9. - 12. construct response
        return handleUserApproval()
                .andThen(constructResponse());
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

    private Flowable<byte[]> getAllowedIds() {
        // TODO error handling
        return Single.just(request.getAllowList())
                .flatMapPublisher(Flowable::fromIterable)
                .map(PublicKeyCredentialDescriptor::getId);
    }

    private boolean isInAllowedCredentialIds(byte[] id) {
        if (request.getAllowList() == null) {
            // if the allow list is non existent, all credentials are valid
            return true;
        }
        for (PublicKeyCredentialDescriptor descriptor: request.getAllowList()) {
            if (descriptor.getId() == id) {
                return true;
            }
        }
        return false;
    }

    private Completable checkForCredentials() {
        // TODO: should we handle the Maybe<>?
        return credentialSafe.getKeysForEntity(request.getRpId())
                .filter(credentialSource -> isInAllowedCredentialIds(credentialSource.id))
                .firstElement()
                .flatMapCompletable(credential -> Completable.fromAction(() -> request.setSelectedCredential(credential)));
    }

    private Completable checkOptions() {
        return Completable.complete();
    }

    private Completable handleUserApproval() {
        return Completable.defer(() -> {
            if(!request.isApproved()) {
                return Completable.error(OperationDeniedException::new);
            } else if(request.getSelectedCredential() == null) {
                return Completable.error(NoCredentialsException::new);
            } else {
                return Completable.complete();
            }
        });
    }

    private Single<byte[]> constructAuthenticatorData() {
        return helper.hashSha256(request.getRpId())
                .flatMap(rpIdHash -> helper.constructAuthenticatorData(rpIdHash, null));
    }

    private Single<PublicKeyCredentialDescriptor> constructCredentialDescriptor() {
        return Single.just(new BasePublicKeyCredentialDescriptor("public-key", request.getSelectedCredential().id));
    }

    private Single<GetAssertionResponse> constructResponse() {
        return constructAuthenticatorData()
                .flatMap(authData -> Single.zip(Single.just(ArrayUtil.concatBytes(authData, request.getClientDataHash())), this.credentialSafe.getPrivateKeyByAlias(request.getSelectedCredential().keyPairAlias), cryptoProvider::sign)
                        .flatMap(x -> x)
                        .flatMap(sig -> constructCredentialDescriptor()
                        .map(descriptor -> new BaseGetAssertionResponse(descriptor, authData, sig))));
    }

}
