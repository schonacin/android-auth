package com.blue_unicorn.android_auth_lib.api.authenticator;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyInfo;
import android.security.keystore.KeyProperties;
import androidx.annotation.NonNull;

import com.blue_unicorn.android_auth_lib.AuthLibException;
import com.blue_unicorn.android_auth_lib.api.authenticator.database.CredentialDatabase;
import com.nexenio.rxkeystore.RxKeyStore;
import com.upokecenter.cbor.CBORObject;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECPoint;

import com.blue_unicorn.android_auth_lib.api.authenticator.database.PublicKeyCredentialSource;
import hu.akarnokd.rxjava3.bridge.RxJavaBridge;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

/**
 * Inspired by
 * <a href="https://github.com/duo-labs/android-webauthn-authenticator">library</a>* from duo-labs
 */
public class CredentialSafe {

    private static final String KEYSTORE_TYPE = "AndroidKeyStore";
    private static final String CURVE_NAME = "secp256r1";
    private RxKeyStore rxKeyStore;
    private boolean authenticationRequired;
    private CredentialDatabase db;

    public CredentialSafe(Context ctx) {
        this(ctx, true);
    }

    CredentialSafe(Context ctx, boolean authenticationRequired) {
        this.rxKeyStore = new RxKeyStore(KEYSTORE_TYPE);
        this.authenticationRequired = authenticationRequired;
        this.db = CredentialDatabase.getDatabase(ctx);
    }

    boolean supportsUserVerification() {
        return this.authenticationRequired;
    }

    RxKeyStore getRxKeyStore() {
        return this.rxKeyStore;
    }

    private Single<KeyPair> generateNewES256KeyPair(String alias) {
        return Single.defer(() -> {
            KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_SIGN)
                    .setAlgorithmParameterSpec(new ECGenParameterSpec(CURVE_NAME))
                    .setDigests(KeyProperties.DIGEST_SHA256)
                    .setUserAuthenticationRequired(this.authenticationRequired) // fingerprint or similar
                    .build();

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, KEYSTORE_TYPE);
            keyPairGenerator.initialize(spec);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            return Single.just(keyPair);
        }).onErrorResumeNext(throwable -> Single.error(new AuthLibException("couldn't generate key pair!", throwable)));
    }

    public Single<PublicKeyCredentialSource> generateCredential(@NonNull String rpEntityId, byte[] userHandle, String userDisplayName) {
        return Single.defer(() -> {
            PublicKeyCredentialSource credentialSource = new PublicKeyCredentialSource(rpEntityId, userHandle, userDisplayName);
            return generateNewES256KeyPair(credentialSource.keyPairAlias)
                    .doOnSuccess(keyPair -> db.credentialDao().insert(credentialSource))
                    .map(x -> credentialSource);
        });
    }

    public void deleteCredential(PublicKeyCredentialSource credentialSource) {
        db.credentialDao().delete(credentialSource);
    }

    public Flowable<PublicKeyCredentialSource> getKeysForEntity(@NonNull String rpEntityId) {
        return Single.defer(() -> Single.just(db.credentialDao().getAllByRpId(rpEntityId)))
                .flatMapPublisher(Flowable::fromIterable);
    }

    Single<PublicKeyCredentialSource> getCredentialSourceById(@NonNull byte[] id) {
        return Single.defer(() -> Single.just(db.credentialDao().getById(id)));
    }

    private Single<PublicKey> getPublicKeyByAlias(@NonNull String alias) {
        return rxKeyStore.getCertificate(alias)
                .as(RxJavaBridge.toV3Single())
                .map(Certificate::getPublicKey)
                .onErrorResumeNext(throwable -> Single.error(new AuthLibException("couldn't get public key by alias", throwable)));
    }

    private Single<PrivateKey> getPrivateKeyByAlias(@NonNull String alias) {
        return rxKeyStore.getKey(alias)
                .as(RxJavaBridge.toV3Single())
                .cast(PrivateKey.class)
                .onErrorResumeNext(throwable -> Single.error(new AuthLibException("couldn't get private key by alias", throwable)));
    }

    Single<KeyPair> getKeyPairByAlias(@NonNull String alias) {
        return Single.zip(getPublicKeyByAlias(alias), getPrivateKeyByAlias(alias), KeyPair::new);
    }

    public Single<Boolean> keyRequiresVerification(@NonNull String alias) {
        return getPrivateKeyByAlias(alias)
                .flatMap(privateKey -> {
                    KeyFactory factory;
                    KeyInfo keyInfo;

                    factory = KeyFactory.getInstance(privateKey.getAlgorithm(), KEYSTORE_TYPE);
                    keyInfo = factory.getKeySpec(privateKey, KeyInfo.class);
                    return Single.just(keyInfo.isUserAuthenticationRequired());
                }).onErrorResumeNext(throwable -> Single.error(new AuthLibException("Couldn't retrieve key", throwable)));
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
        return Single.defer(() -> {
            //ByteArrayOutputStream baos = new ByteArrayOutputStream();

            CBORObject object = CBORObject.NewMap();
            object.Add(1, 2);
            object.Add(3, -7);
            object.Add(-1, 1);
            object.Add(-2, x);
            object.Add(-3, y);

            byte[] encodedResponse = object.EncodeToBytes();
            return Single.just(encodedResponse);
        }).onErrorResumeNext(throwable -> Single.error(new AuthLibException("couldn't serialize to cbor", throwable)));
    }

    static Single<byte[]> coseEncodePublicKey(PublicKey publicKey) {
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

            if (x.length != 32 || y.length != 32) {
                return Single.error(new AuthLibException());
            }

            return encodePointstoBytes(x,y);
        });
    }

    int incrementCredentialUseCounter(PublicKeyCredentialSource credential) {
        return db.credentialDao().incrementUseCounter(credential);
    }

}
