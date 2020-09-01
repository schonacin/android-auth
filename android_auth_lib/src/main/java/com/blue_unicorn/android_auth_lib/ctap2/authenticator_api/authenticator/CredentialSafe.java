package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.authenticator;

import android.content.Context;

import androidx.annotation.NonNull;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.authenticator.database.CredentialDatabase;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.authenticator.database.PublicKeyCredentialSource;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.AuthLibException;
import com.nexenio.rxandroidbleserver.service.value.ValueUtil;
import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.asymmetric.ec.RxECCryptoProvider;
import com.upokecenter.cbor.CBORObject;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;
import java.util.Arrays;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import timber.log.Timber;

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
            Timber.d("Generating Credential for rpEntityId %s and store in the credential store", rpEntityId);
            Timber.d("\tKeypairAlias: %s", credentialSource.getKeyPairAlias());
            Timber.d("\tId (hex): %s", ValueUtil.bytesToHex(credentialSource.getId()));
            Timber.d("\tId: %s", Arrays.toString(credentialSource.getId()));
            Timber.d("\tUser display name: %s", credentialSource.getUserDisplayName());
            return generateNewES256KeyPair(credentialSource.getKeyPairAlias())
                    .andThen(insertCredentialIntoDatabase(credentialSource))
                    .andThen(Single.just(credentialSource));
        });
    }

    private Completable insertCredentialIntoDatabase(PublicKeyCredentialSource credential) {
        Timber.d("Inserting credential %s into DB", credential.getKeyPairAlias());
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

    public Single<PublicKey> getPublicKeyByAlias(@NonNull String alias) {
        return rxCryptoProvider.getPublicKey(alias)
                .onErrorResumeNext(throwable -> Single.error(new AuthLibException("couldn't get public key by alias", throwable)));
    }

    public Single<PrivateKey> getPrivateKeyByAlias(@NonNull String alias) {
        return rxCryptoProvider.getPrivateKey(alias)
                .onErrorResumeNext(throwable -> Single.error(new AuthLibException("couldn't get private key by alias", throwable)));
    }

    public Single<KeyPair> getKeyPairByAlias(@NonNull String alias) {
        return rxCryptoProvider.getKeyPair(alias)
                .onErrorResumeNext(throwable -> Single.error(new AuthLibException("couldn't get key pair by alias", throwable)));
    }

}
