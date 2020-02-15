package com.blue_unicorn.android_auth_lib.api.authenticator;

import com.blue_unicorn.android_auth_lib.AuthLibException;
import com.blue_unicorn.android_auth_lib.api.authenticator.database.PublicKeyCredentialSource;

import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.MessageDigest;

import io.reactivex.rxjava3.core.Single;

/**
 * Provides functions that are used in multiple Authenticator API methods.
 * Inspired by/ copied from <a href="https://github.com/duo-labs/android-webauthn-authenticator">this library</a>* developed by duo-labs.
 */
final class AuthenticatorHelper {

    private CredentialSafe credentialSafe;

    AuthenticatorHelper(CredentialSafe credentialSafe) {
        this.credentialSafe = credentialSafe;
    }

    Single<byte[]> hashSha256(String data) {
        return Single.defer(() -> {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(data.getBytes());
            byte[] hash = messageDigest.digest();
            return Single.just(hash);
        }).onErrorResumeNext(throwable -> Single.error(new AuthLibException("couldn't hash data", throwable)));
    }

    Single<byte[]> constructAttestedCredentialData(PublicKeyCredentialSource credentialSource) {
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

    Single<byte[]> constructAuthenticatorData(byte[] rpIdHash, byte[] attestedCredentialData) {
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
