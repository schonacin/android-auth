package com.blue_unicorn.android_auth_lib.api.authenticator;

import com.blue_unicorn.android_auth_lib.AuthLibException;
import com.blue_unicorn.android_auth_lib.api.authenticator.database.PublicKeyCredentialSource;
import com.blue_unicorn.android_auth_lib.fido.reponse.BaseMakeCredentialResponse;
import com.blue_unicorn.android_auth_lib.fido.request.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.fido.reponse.MakeCredentialResponse;
import com.blue_unicorn.android_auth_lib.fido.webauthn.PackedAttestationStatement;
import com.blue_unicorn.android_auth_lib.util.ArrayUtil;
import com.nexenio.rxkeystore.provider.asymmetric.RxAsymmetricCryptoProvider;

import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.MessageDigest;

import hu.akarnokd.rxjava3.bridge.RxJavaBridge;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

class MakeCredential {

    private CredentialSafe credentialSafe;
    private RxAsymmetricCryptoProvider cryptoProvider;
    private MakeCredentialRequest request;

    //TODO maybe make this a singleton
    MakeCredential(CredentialSafe credentialSafe, MakeCredentialRequest request) {
        this.credentialSafe = credentialSafe;
        this.cryptoProvider = this.credentialSafe.getCryptoProvider();
        this.request = request;
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
        return handleUserApproval(this.request)
                .andThen(this.constructResponse());
    }

    private Completable validate() {
        return Completable.defer(() -> {
            if (request.isValid()) {
                return Completable.complete();
            } else {
                return Completable.error(AuthLibException::new);
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
                        .skipWhile(excluded -> !excluded)
                        .first(false)
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
                .map(value -> value == -7)
                .skipWhile(isValid -> !isValid)
                .firstOrError()
                .flatMapCompletable(val -> Completable.complete());
    }

    private Completable checkOptions() {
        return Completable.complete();
    }

    private Completable handleUserApproval(MakeCredentialRequest request) {
        return Completable.defer(() -> {
            if(!request.isApproved()) {
                return Completable.error(new AuthLibException("user did not approve!"));
            } else if(request.isExcluded()) {
                return Completable.error(new AuthLibException("credential is excluded"));
            } else {
                return Completable.complete();
            }
        });
    }

    private Single<MakeCredentialResponse> constructResponse() {
        return generateCredential()
                .flatMap(credential -> Single.zip(hashSha256(request.getRp().getId()), constructAttestedCredentialData(credential), this::constructAuthenticatorData)
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

    private Single<byte[]> hashSha256(String data) {
        return Single.defer(() -> {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(data.getBytes());
            byte[] hash = messageDigest.digest();
            return Single.just(hash);
        }).onErrorResumeNext(throwable -> Single.error(new AuthLibException("couldn't hash data", throwable)));
    }

    private Single<byte[]> constructAttestedCredentialData(PublicKeyCredentialSource credentialSource) {
        // | AAGUID | L | credentialId | credentialPublicKey |
        // |   16   | 2 |      32      |          n          |
        // total size: 50+n
        return this.credentialSafe.getKeyPairByAlias(credentialSource.keyPairAlias)
                .map(KeyPair::getPublic)
                .flatMap(CredentialSafe::coseEncodePublicKey)
                .flatMap(encodedPublicKey -> {
                    ByteBuffer credentialData = ByteBuffer.allocate(16 + 2 + credentialSource.id.length + encodedPublicKey.length);

                    // AAGUID will be 16 bytes of zeroes
                    credentialData.position(16);
                    credentialData.putShort((short) credentialSource.id.length); // L
                    credentialData.put(credentialSource.id); // credentialId
                    credentialData.put(encodedPublicKey);
                    return Single.just(credentialData.array());
                });
    }

    private Single<byte[]> constructAuthenticatorData(byte[] rpIdHash, byte[] attestedCredentialData) {
        return Single.defer(() -> {
            if (rpIdHash.length != 32) {
                return Single.error(new AuthLibException("rpIdHash must be a 32-byte SHA-256 hash"));
            }

            byte flags = 0x00;
            flags |= 0x01; // user present
            if (this.credentialSafe.supportsUserVerification()) {
                flags |= (0x01 << 2); // user verified
            }
            if (attestedCredentialData != null) {
                flags |= (0x01 << 6); // attested credential data included
            }

            // 32-byte hash + 1-byte flags + 4 bytes signCount = 37 bytes
            ByteBuffer authData = ByteBuffer.allocate(37 +
                    (attestedCredentialData == null ? 0 : attestedCredentialData.length));

            authData.put(rpIdHash);
            authData.put(flags);
            authData.putInt(0);
            if (attestedCredentialData != null) {
                authData.put(attestedCredentialData);
            }
            return Single.just(authData.array());
        });
    }

}
