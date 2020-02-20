package com.blue_unicorn.android_auth_lib.api.authenticator;

import com.blue_unicorn.android_auth_lib.AuthLibException;
import com.blue_unicorn.android_auth_lib.api.authenticator.database.PublicKeyCredentialSource;
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

import java.security.PrivateKey;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

/**
 * Represents the authenticatorGetAssertion method.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#authenticatorGetAssertion">specification</a>
 */
public class GetAssertion {

    private CredentialSafe credentialSafe;
    private RxAsymmetricCryptoProvider cryptoProvider;
    private AuthenticatorHelper helper;
    private GetAssertionRequest request;

    public GetAssertion(CredentialSafe credentialSafe, GetAssertionRequest request) {
        this.credentialSafe = credentialSafe;
        this.cryptoProvider = this.credentialSafe.getCryptoProvider();
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
        return Completable.complete();
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

    private Single<PublicKeyCredentialSource> selectedCredential;

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
                .flatMap(credentialSource -> Single.just(new BasePublicKeyCredentialDescriptor("public-key", credentialSource.id)));
    }

    private Single<byte[]> authenticatorData;

    private Single<byte[]> getAuthenticatorData() {
        return Single.defer(() -> {
            if (this.authenticatorData == null) {
                this.authenticatorData =
                        helper.hashSha256(request.getRpId())
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
                    .flatMap(credentialSource -> credentialSafe.getPrivateKeyByAlias(credentialSource.keyPairAlias));

            return Single.zip(dataToSign, privateKey, cryptoProvider::sign)
                    .flatMap(x -> x);
        });
    }

    private Single<GetAssertionResponse> constructResponse() {
        return Single.zip(constructCredentialDescriptor(), getAuthenticatorData(), generateSignature(), BaseGetAssertionResponse::new);
    }

}
