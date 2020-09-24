package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.authenticator;

import android.content.Context;

import androidx.annotation.NonNull;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.authenticator.database.CredentialDatabase;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.authenticator.database.PublicKeyCredentialSource;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.AuthLibException;
import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.asymmetric.ec.RxECCryptoProvider;
import com.upokecenter.cbor.CBORObject;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

/**
 * Handles the generation and retrieval of credentials via a specific database and the keystore.
 * Inspired by <a href="https://github.com/duo-labs/android-webauthn-authenticator">this library</a>* developed by duo-labs
 */
public class CredentialSafe {

    private static final String KEYSTORE_TYPE = "AndroidKeyStore";
    private RxKeyStore rxKeyStore;
    private RxECCryptoProvider rxCryptoProvider;
    private boolean authenticationRequired;
    private CredentialDatabase db;
    private Context context;

    public CredentialSafe(Context context, boolean authenticationRequired) {
        this.rxKeyStore = new RxKeyStore(KEYSTORE_TYPE);
        this.rxCryptoProvider = new RxECCryptoProvider(this.rxKeyStore);
        this.authenticationRequired = authenticationRequired;
        this.context = context;
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

            return encodePointstoBytes(x, y);
        });
    }

    boolean supportsUserVerification() {
        return this.authenticationRequired;
    }

    public RxKeyStore getRxKeyStore() {
        return this.rxKeyStore;
    }

    RxECCryptoProvider getRxCryptoProvider() {
        return this.rxCryptoProvider;
    }

    private Single<CredentialDatabase> getInitializedDatabase() {
        return Single.fromCallable(() -> {
            if (this.db == null) {
                this.db = CredentialDatabase.getDatabase(context);
            }
            return this.db;
        }).onErrorResumeNext(throwable -> Single.error(new AuthLibException("couldn't initialize database", throwable)));
    }

    private Completable generateNewES256KeyPair(String alias) {
        return rxCryptoProvider.generateKeyPair(alias, context)
                .flatMapCompletable(keyPair -> rxCryptoProvider.setKeyPair(alias, keyPair))
                .onErrorResumeNext(throwable -> Completable.error(new AuthLibException("couldn't generate key pair!", throwable)));
    }

    public Single<PublicKeyCredentialSource> generateCredential(@NonNull String rpEntityId, byte[] userHandle, String userDisplayName) {
        return Single.defer(() -> {
            PublicKeyCredentialSource credentialSource = new PublicKeyCredentialSource(rpEntityId, userHandle, userDisplayName);
            return generateNewES256KeyPair(credentialSource.getKeyPairAlias())
                    .andThen(insertCredentialIntoDatabase(credentialSource))
                    .andThen(Single.just(credentialSource));
        });
    }

    private Completable insertCredentialIntoDatabase(PublicKeyCredentialSource credential) {
        return getInitializedDatabase()
                .flatMapCompletable(database -> Completable.fromAction(() -> database.credentialDao().insert(credential)));
    }

    public Completable deleteCredential(PublicKeyCredentialSource credentialSource) {
        return rxKeyStore.deleteEntry(credentialSource.getKeyPairAlias())
                .andThen(getInitializedDatabase())
                .flatMapCompletable(database -> Completable.fromAction(() -> database.credentialDao().delete(credentialSource)));
    }

    public Completable deleteAllCredentials() {
        return getInitializedDatabase()
                .flatMapCompletable(database -> Completable.fromAction(() -> database.credentialDao().deleteAll()));
    }

    public Flowable<PublicKeyCredentialSource> getKeysForEntity(@NonNull String rpEntityId) {
        return getInitializedDatabase()
                .map(database -> database.credentialDao().getAllByRpId(rpEntityId))
                .flatMapPublisher(Flowable::fromIterable);
    }

    public Single<PublicKeyCredentialSource> getCredentialSourceById(@NonNull byte[] id) {
        return getInitializedDatabase()
                .map(database -> database.credentialDao().getById(id));
    }

    public Single<PublicKeyCredentialSource> getCredentialSourceByAlias(@NonNull String alias) {
        return getInitializedDatabase()
                .map(database -> database.credentialDao().getByAlias(alias));
    }

    private Single<PublicKey> getPublicKeyByAlias(@NonNull String alias) {
        return rxCryptoProvider.getPublicKey(alias)
                .onErrorResumeNext(throwable -> Single.error(new AuthLibException("couldn't get public key by alias", throwable)));
    }

    Single<PrivateKey> getPrivateKeyByAlias(@NonNull String alias) {
        return rxCryptoProvider.getPrivateKey(alias)
                .onErrorResumeNext(throwable -> Single.error(new AuthLibException("couldn't get private key by alias", throwable)));
    }

    Single<KeyPair> getKeyPairByAlias(@NonNull String alias) {
        return rxCryptoProvider.getKeyPair(alias)
                .onErrorResumeNext(throwable -> Single.error(new AuthLibException("couldn't get key pair by alias", throwable)));
    }

}
