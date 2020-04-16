package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.authenticator;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.authenticator.database.PublicKeyCredentialSource;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.BaseMakeCredentialResponse;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.MakeCredentialResponse;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.AttestationStatement;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.PackedAttestationStatement;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.PublicKeyCredentialDescriptor;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.PublicKeyCredentialParameter;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes.CredentialExcludedException;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes.InvalidOptionException;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes.OperationDeniedException;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes.OtherException;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes.UnsupportedAlgorithmException;
import com.blue_unicorn.android_auth_lib.util.ArrayUtil;
import com.nexenio.rxkeystore.provider.asymmetric.RxAsymmetricCryptoProvider;

import java.security.PrivateKey;
import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

/**
 * Represents the authenticatorMakeCredential method.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#authenticatorMakeCredential">specification</a>
 */
public class MakeCredential {

    private CredentialSafe credentialSafe;
    private RxAsymmetricCryptoProvider rxCryptoProvider;
    private MakeCredentialRequest request;
    private AuthenticatorHelper helper;
    private Single<PublicKeyCredentialSource> generatedCredential;
    private Single<byte[]> authenticatorData;

    public MakeCredential(CredentialSafe credentialSafe, MakeCredentialRequest request) {
        this.credentialSafe = credentialSafe;
        this.rxCryptoProvider = this.credentialSafe.getRxCryptoProvider();
        this.request = request;
        this.helper = new AuthenticatorHelper(this.credentialSafe);
    }

    public Single<MakeCredentialRequest> operate() {
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

    public Single<MakeCredentialResponse> operateInner() {
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
                        .map(PublicKeyCredentialDescriptor::getId)
                        .switchMapSingle(this.credentialSafe::getCredentialSourceById)
                        .map(credentialSource -> (credentialSource != null && credentialSource.getRpId().equals(request.getRp().getId())))
                        .contains(true)
                        .flatMapCompletable(excluded -> Completable.fromAction(() -> request.setExcluded(excluded)));
            } else {
                return Completable.fromAction(() -> request.setExcluded(false));
            }
        });
    }

    private Completable checkForValidAlgorithm() {
        return Single.just(request.getPubKeyCredParams())
                .flatMapPublisher(Flowable::fromArray)
                .map(PublicKeyCredentialParameter::getAlg)
                .contains(Config.COSE_ALGORITHM_IDENTIFIER)
                .flatMapCompletable(isSupported -> {
                    if (isSupported) {
                        return Completable.complete();
                    } else {
                        return Completable.error(UnsupportedAlgorithmException::new);
                    }
                });
    }

    private Completable checkOptions() {
        return Completable.defer(() -> {
            if (request.getOptions() == null) {
                return Completable.complete();
            } else {
                return Single.just(request.getOptions())
                        .map(Map::keySet)
                        .flatMapPublisher(Flowable::fromIterable)
                        .flatMapCompletable(key -> {
                            // relevant options for makeCredential are rk and uv
                            // no need to process these because we will save credentials and verify the user regardless
                            // if up and plat are set however we return an exception, as they are not valid for makeCredential
                            if (key.equals("up") || key.equals("plat")) {
                                return Completable.error(InvalidOptionException::new);
                            } else {
                                return Completable.complete();
                            }
                        });
            }
        });
    }

    private Completable handleUserApproval() {
        return Completable.defer(() -> {
            if (!request.isApproved()) {
                return Completable.error(OperationDeniedException::new);
            } else if (request.isExcluded()) {
                return Completable.error(CredentialExcludedException::new);
            } else {
                return Completable.complete();
            }
        });
    }

    private Single<PublicKeyCredentialSource> getGeneratedCredential() {
        return Single.defer(() -> {
            if (this.generatedCredential == null) {
                this.generatedCredential =
                        credentialSafe.generateCredential(request.getRp().getId(), request.getUser().getId(), request.getUser().getName())
                                .cache();
            }
            return this.generatedCredential;
        });
    }

    private Single<byte[]> constructAttestedCredentialData() {
        return getGeneratedCredential()
                .flatMap(helper::constructAttestedCredentialData);
    }

    private Single<byte[]> getAuthenticatorData() {
        return Single.defer(() -> {
            if (this.authenticatorData == null) {
                this.authenticatorData =
                        Single.zip(AuthenticatorHelper.hashSha256(request.getRp().getId()), constructAttestedCredentialData(), helper::constructAuthenticatorData)
                                .flatMap(x -> x)
                                .cache();
            }
            return this.authenticatorData;
        });
    }

    private Single<byte[]> generateSignature() {
        return Single.defer(() -> {
            Single<byte[]> dataToSign = getAuthenticatorData()
                    .map(authData -> ArrayUtil.concatBytes(authData, request.getClientDataHash()));

            Single<PrivateKey> privateKey = getGeneratedCredential()
                    .map(PublicKeyCredentialSource::getKeyPairAlias)
                    .flatMap(credentialSafe::getPrivateKeyByAlias);

            return Single.zip(dataToSign, privateKey, rxCryptoProvider::sign)
                    .flatMap(x -> x);
        });
    }

    private Single<AttestationStatement> constructAttestationStatement() {
        return generateSignature()
                .map(PackedAttestationStatement::new);
    }

    private Single<MakeCredentialResponse> constructResponse() {
        return Single.zip(getAuthenticatorData(), constructAttestationStatement(), BaseMakeCredentialResponse::new);
    }
}
