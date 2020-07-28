package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.authenticator;

import com.blue_unicorn.android_auth_lib.android.constants.ExtensionValue;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.authenticator.database.PublicKeyCredentialSource;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.BaseGetAssertionResponse;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.GetAssertionResponse;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.BasePublicKeyCredentialDescriptor;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.BasePublicKeyCredentialUserEntity;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.PublicKeyCredentialDescriptor;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.PublicKeyCredentialUserEntity;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.AuthLibException;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes.InvalidOptionException;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes.MissingParameterException;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes.NoCredentialsException;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes.OperationDeniedException;
import com.blue_unicorn.android_auth_lib.ctap2.message_encoding.CborSerializer;
import com.blue_unicorn.android_auth_lib.util.ArrayUtil;
import com.nexenio.rxandroidbleserver.service.value.ValueUtil;
import com.nexenio.rxkeystore.provider.asymmetric.RxAsymmetricCryptoProvider;

import java.security.PrivateKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import timber.log.Timber;

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
                .andThen(this.handleExtensions())
                .andThen(Single.just(request));
    }

    // 2. - 4 are ignored as pinAuth is not supported
    // TODO: or throw respective errors when present: would need changes in data classes however

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
                return Completable.error(MissingParameterException::new);
            }
        });
    }

    private boolean isInAllowedCredentialIds(byte[] id) {
        Timber.d("Checking credential id %s for existence in allow list", ValueUtil.bytesToHex(id));
        if (request.getAllowList() == null) {
            // if the allow list is non existent, all credentials are valid
            Timber.d("\tAllow list is null, any key is valid!");
            return true;
        }
        Timber.d("\tAllow list size: %s", request.getAllowList().size());
        Timber.d("\tComparing each entry with credential id in allow list");
        for (PublicKeyCredentialDescriptor descriptor : request.getAllowList()) {
            Timber.d("\t\tComparing %s with %s", ValueUtil.bytesToHex(descriptor.getId()), ValueUtil.bytesToHex(id));
            if (Arrays.equals(descriptor.getId(), id)) {
                Timber.d("\t\tId found!");
                return true;
            }
        }
        return false;
    }

    private Completable checkForCredentials() {
        Timber.d("Select credentials to use for get assertion based on rp id and credentials source id");
        Timber.d("\tRpId: %s", request.getRpId());
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

    private Completable handleExtensions() {
        return Completable.defer(() -> {
            if (request.getExtensions() == null) {
                return Completable.complete();
            } else {
                return Single.just(request.getExtensions())
                        .map(Map::keySet)
                        .flatMapPublisher(Flowable::fromIterable)
                        .flatMapCompletable(key -> {
                            if (key.equals(ExtensionValue.CONTINUOUS_AUTHENTICATION)) {
                                request.setContinuousFreshness(request.getExtensions().get(ExtensionValue.CONTINUOUS_AUTHENTICATION));
                            }
                            return Completable.complete();
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

    private Single<PublicKeyCredentialUserEntity> getUserEntity() {
        return getSelectedCredential()
                .map(PublicKeyCredentialSource::getUserHandle)
                .map(BasePublicKeyCredentialUserEntity::new);
    }

    private Single<byte[]> constructExtensionField() {
        return Single.defer(() -> {
            if (request.getContinuousFreshness() == null) {
                return Single.just(new byte[]{});
            } else {
                Map<String, Integer> extensions = new HashMap<>();
                extensions.put(ExtensionValue.CONTINUOUS_AUTHENTICATION, request.getContinuousFreshness());
                return Single.just(extensions)
                        .flatMap(CborSerializer::serializeOther);
            }
        });
    }

    private Single<byte[]> constructAttestedCredentialData() {
        // empty in getAssertion
        return Single.fromCallable(() -> new byte[]{});
    }

    private Single<byte[]> getAuthenticatorData() {
        return Single.defer(() -> {
            if (this.authenticatorData == null) {
                this.authenticatorData =
                        Single.zip(AuthenticatorHelper.hashSha256(request.getRpId()), constructAttestedCredentialData(), constructExtensionField(), helper::constructAuthenticatorData)
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

            Single<PrivateKey> privateKey = getSelectedCredential()
                    .map(PublicKeyCredentialSource::getKeyPairAlias)
                    .flatMap(credentialSafe::getPrivateKeyByAlias);

            return Single.zip(dataToSign, privateKey, rxCryptoProvider::sign)
                    .flatMap(x -> x);
        });
    }

    private Single<GetAssertionResponse> constructResponse() {
        return Single.zip(constructCredentialDescriptor(), getAuthenticatorData(), generateSignature(), getUserEntity(), BaseGetAssertionResponse::new);
    }

}
