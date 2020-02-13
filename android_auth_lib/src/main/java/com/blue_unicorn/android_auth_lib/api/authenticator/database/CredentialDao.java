package com.blue_unicorn.android_auth_lib.api.authenticator.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

/**
 * Inspired by/ Copied from:
 * <a href="https://github.com/duo-labs/android-webauthn-authenticator">library</a>* from duo-labs
 */
@Dao
public abstract class CredentialDao {

    @Query("SELECT * FROM credentials")
    public abstract List<PublicKeyCredentialSource> getAll();

    @Query("SELECT * FROM credentials")
    public abstract LiveData<List<PublicKeyCredentialSource>> getAllLive();

    @Query("SELECT * FROM credentials WHERE rpId = :rpId")
    public abstract List<PublicKeyCredentialSource> getAllByRpId(String rpId);

    @Query("SELECT * FROM credentials WHERE id = :id LIMIT 1")
    public abstract PublicKeyCredentialSource getById(byte[] id);

    @Query("SELECT * FROM credentials WHERE keyPairAlias = :alias LIMIT 1")
    public abstract PublicKeyCredentialSource getByAlias(String alias);

    @Query("DELETE FROM credentials")
    public abstract void deleteAll();

    @Insert
    public abstract void insert(PublicKeyCredentialSource credential);

    @Delete
    public abstract void delete(PublicKeyCredentialSource credential);

    @Update
    public abstract void update(PublicKeyCredentialSource credential);

    @Query("SELECT keyUseCounter FROM credentials WHERE roomUid = :uid LIMIT 1")
    public abstract int getUseCounter(int uid);

    @Transaction
    public int incrementUseCounter(PublicKeyCredentialSource credential) {
        int useCounter = getUseCounter(credential.roomUid);
        credential.keyUseCounter++;
        update(credential);
        return useCounter;
    }
}
