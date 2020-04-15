package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.authenticator;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.authenticator.database.PublicKeyCredentialSource;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.exceptions.InvalidOptionException;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.exceptions.NoCredentialsException;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.exceptions.OperationDeniedException;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.exceptions.OtherException;
import com.blue_unicorn.android_auth_lib.ctap2.data.reponse.BaseGetAssertionResponse;
import com.blue_unicorn.android_auth_lib.ctap2.data.reponse.GetAssertionResponse;
import com.blue_unicorn.android_auth_lib.ctap2.data.request.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.ctap2.data.webauthn.BasePublicKeyCredentialDescriptor;
import com.blue_unicorn.android_auth_lib.ctap2.data.webauthn.PublicKeyCredentialDescriptor;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.AuthLibException;
import com.blue_unicorn.android_auth_lib.util.ArrayUtil;
import com.nexenio.rxkeystore.provider.asymmetric.RxAsymmetricCryptoProvider;

import java.security.PrivateKey;
import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

/**
 * Represents the authenticatorGetAssertion method.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#authenticatorGetAssertion">specification</a>
 */
public class GetAssertion {

    private CredentialSafe credentialSafe;
    private RxAsymmetricCryptoProvider rxCryptoProvider;
    private AuthenticatorHelper helper;
    private GetAssertionRequest request;
    private Single<PublicKeyCredentialSource> selectedCredential;
    private Single<byte[]> authenticatorData;

    public GetAssertion(CredentialSafe credentialSafe, GetAssertionRequest request) {
        this.credentialSafe = credentialSafe;
        this.rxCryptoProvider = this.credentialSafe.getRxCryptoProvider();
        this.request = request;
        this.helper = new AuthenticatorHelper(this.credentialSafe);
    }

    public Single<GetAssertionRequest> operate() {
        // 0. check if request is valid
        // 1. check credentials in allowList if Existent
        // 5. check options
        return validate()
                .andThen(this.checkForCredentials())
                .andThen(this.checkOptions())
                .andThen(Single.just(request));
    }

    // 2. - 4. & 6. are ignored as pinAuth and extensions are not supported

    public Single<GetAssertionResponse> operateInner() {
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

    private boolean isInAllowedCredentialIds(byte[] id) {
        if (request.getAllowList() == null) {
            // if the allow list is non existent, all credentials are valid
            return true;
        }
        for (PublicKeyCredentialDescriptor descriptor : request.getAllowList()) {
            if (descriptor.getId() == id) {
                return true;
            }
        }
        return false;
    }

    private Completable checkForCredentials() {
        return credentialSafe.getKeysForEntity(request.getRpId())
                .filter(credentialSource -> isInAllowedCredentialIds(credentialSource.id))
                .toList()
                .flatMapCompletable(credentials -> Completable.fromAction(() -> request.setSelectedCredentials(credentials)));
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
                            // relevant options for getAssertion are up and uv
                            // no need to process these because we will handle user presence and verification regardless
                            // if rk and plat are set however we return an exception, as there are not valid for getAssertion
                            if (key.equals("rk") || key.equals("plat")) {
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
            } else if (request.getSelectedCredentials() == null || request.getSelectedCredentials().isEmpty()) {
                return Completable.error(NoCredentialsException::new);
            } else {
                return Completable.complete();
            }
        });
    }

    private Single<PublicKeyCredentialSource> getSelectedCredential() {
        return Single.defer(() -> {
            if (this.selectedCredential == null) {
                this.selectedCredential =
                        Single.defer(() -> {
                            if (!request.getSelectedCredentials().isEmpty()) {
                                // TODO: do we handle getNextAssertion? taking the first credential for now
                                return Single.just(request.getSelectedCredentials().get(0));
                            } else {
                                return Single.error(new AuthLibException("no selected credentials"));
                            }
                        }).cache();
            }
            return this.selectedCredential;
        });
    }

    private Single<PublicKeyCredentialDescriptor> constructCredentialDescriptor() {
        return getSelectedCredential()
                .map(PublicKeyCredentialSource::getId)
                .map(BasePublicKeyCredentialDescriptor::new);
    }

    private Single<byte[]> getAuthenticatorData() {
        return Single.defer(() -> {
            if (this.authenticatorData == null) {
                this.authenticatorData =
                        AuthenticatorHelper.hashSha256(request.getRpId())
                                .flatMap(rpIdHash -> helper.constructAuthenticatorData(rpIdHash, null))
                                .cache();
            }
            return this.authenticatorData;
        });
    }

    private Single<byte[]> generateSignature() {
        return Single.defer(() -> {
            Single<byte[]> dataToSign = getAuthenticatorData()
                    .map(authData -> ArrayUtil.concatBytes(authData, request.getClientDataHash()));

            Single<PrivateKey> privateKey = getSelectedCredential()
                    .map(PublicKeyCredentialSource::getKeyPairAlias)
                    .flatMap(credentialSafe::getPrivateKeyByAlias);

            return Single.zip(dataToSign, privateKey, rxCryptoProvider::sign)
                    .flatMap(x -> x);
        });
    }

    private Single<GetAssertionResponse> constructResponse() {
        // TODO: add UserEntity??
        return Single.zip(constructCredentialDescriptor(), getAuthenticatorData(), generateSignature(), BaseGetAssertionResponse::new);
    }

}
