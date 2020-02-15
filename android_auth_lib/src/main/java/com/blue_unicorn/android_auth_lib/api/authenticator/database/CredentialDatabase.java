package com.blue_unicorn.android_auth_lib.api.authenticator.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Inspired by/ copied from: <a href="https://github.com/duo-labs/android-webauthn-authenticator">library</a>* developed by duo-labs
 */
@Database(entities = {PublicKeyCredentialSource.class}, version = 3, exportSchema = false)
public abstract class CredentialDatabase extends RoomDatabase {
    private static CredentialDatabase INSTANCE;
    private static final String CREDENTIAL_DB_NAME = "credentialmetadata";

    public static CredentialDatabase getDatabase(Context ctx) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(ctx.getApplicationContext(), CredentialDatabase.class, CREDENTIAL_DB_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    public abstract CredentialDao credentialDao();
}
