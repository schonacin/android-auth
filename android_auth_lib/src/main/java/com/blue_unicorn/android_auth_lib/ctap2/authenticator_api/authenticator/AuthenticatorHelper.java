package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.authenticator;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.authenticator.database.PublicKeyCredentialSource;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.AuthLibException;
import com.upokecenter.cbor.CBORObject;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;

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

    static Single<byte[]> hashSha256(String data) {
        return Single.fromCallable(() -> {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(data.getBytes());
            return messageDigest.digest();
        }).onErrorResumeNext(throwable -> Single.error(new AuthLibException("couldn't hash data", throwable)));
    }

    private static byte[] toUnsignedFixedLength(byte[] arr, int fixedLength) {
        byte[] fixed = new byte[fixedLength];
        int offset = fixedLength - arr.length;
        int srcPos = Math.max(-offset, 0);
        int dstPos = Math.max(offset, 0);
        int copyLength = Math.min(arr.length, fixedLength);
        System.arraycopy(arr, srcPos, fixed, dstPos, copyLength);
        return fixed;
    }

    private static Single<byte[]> encodePointstoBytes(byte[] x, byte[] y) {
        return Single.fromCallable(() -> {
            CBORObject object = CBORObject.NewMap();
            object.Add(1, 2);
            object.Add(3, -7);
            object.Add(-1, 1);
            object.Add(-2, x);
            object.Add(-3, y);

            return object.EncodeToBytes();
        }).onErrorResumeNext(throwable -> Single.error(new AuthLibException("couldn't serialize to cbor", throwable)));
    }

    private Single<byte[]> coseEncodePublicKey(PublicKey publicKey) {
        return Single.defer(() -> {
            ECPublicKey ecPublicKey = (ECPublicKey) publicKey;
            ECPoint point = ecPublicKey.getW();
            // ECPoint coordinates are *unsigned* values that span the range [0, 2**32). The getAffine
            // methods return BigInteger objects, which are signed. toByteArray will output a byte array
            // containing the two's complement representation of the value, outputting only as many
            // bytes as necessary to do so. We want an unsigned byte array of length 32, but when we
            // call toByteArray, we could get:
            // 1) A 33-byte array, if the point's unsigned representation has a high 1 bit.
            //    toByteArray will prepend a zero byte to keep the value positive.
            // 2) A <32-byte array, if the point's unsigned representation has 9 or more high zero
            //    bits.
            // Due to this, we need to either chop off the high zero byte or prepend zero bytes
            // until we have a 32-length byte array.
            byte[] xVariableLength = point.getAffineX().toByteArray();
            byte[] yVariableLength = point.getAffineY().toByteArray();

            byte[] x = toUnsignedFixedLength(xVariableLength, 32);

            byte[] y = toUnsignedFixedLength(yVariableLength, 32);

            return encodePointstoBytes(x, y);
        });
    }

    Single<byte[]> constructAttestedCredentialData(PublicKeyCredentialSource credentialSource) {
        // | AAGUID | L | credentialId | credentialPublicKey |
        // |   16   | 2 |      32      |          n          |
        // total size: 50+n
        return this.credentialSafe.getPublicKeyByAlias(credentialSource.getKeyPairAlias())
                .flatMap(this::coseEncodePublicKey)
                .map(encodedPublicKey -> {
                    ByteBuffer credentialData = ByteBuffer.allocate(16 + 2 + credentialSource.getId().length + encodedPublicKey.length);

                    credentialData.put(Config.AAGUID);
                    credentialData.putShort((short) credentialSource.getId().length); // L
                    credentialData.put(credentialSource.getId()); // credentialId
                    credentialData.put(encodedPublicKey);
                    return credentialData.array();
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
