package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.authenticator.database;

import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.security.SecureRandom;

/**
 * Represents the credentials that are generated and stored in the database.
 * Inspired by/ copied from: <a href="https://github.com/duo-labs/android-webauthn-authenticator">library</a>* developed by duo-labs
 */
@Entity(tableName = "credentials", indices = {@Index("rpId")})
public class PublicKeyCredentialSource {
    public static final String type = "public-key";
    @Ignore
    private static final String KEYPAIR_PREFIX = "virgil-keypair-";
    @Ignore
    private static SecureRandom random;
    @PrimaryKey(autoGenerate = true)
    public int roomUid;
    public byte[] id;
    public String keyPairAlias;
    public String rpId;
    public byte[] userHandle;
    public String userDisplayName;
    public String otherUI;
    public int keyUseCounter;

    public PublicKeyCredentialSource(@NonNull String rpId, byte[] userHandle, String userDisplayName) {
        ensureRandomInitialized();
        this.id = new byte[32];
        this.userDisplayName = userDisplayName;
        PublicKeyCredentialSource.random.nextBytes(this.id);

        this.rpId = rpId;
        this.keyPairAlias = KEYPAIR_PREFIX + Base64.encodeToString(id, Base64.NO_WRAP);
        this.userHandle = userHandle;
        this.keyUseCounter = 1;
    }

    /**
     * Ensure the SecureRandom singleton has been initialized.
     */
    private void ensureRandomInitialized() {
        if (PublicKeyCredentialSource.random == null) {
            PublicKeyCredentialSource.random = new SecureRandom();
        }
    }

    public byte[] getId() {
        return id;
    }

    public String getKeyPairAlias() {
        return keyPairAlias;
    }

    public String getRpId() {
        return rpId;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public byte[] getUserHandle() {
        return userHandle;
    }
}
