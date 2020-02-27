package com.blue_unicorn.android_auth_lib;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.blue_unicorn.android_auth_lib.api.authenticator.CredentialSafe;
import com.blue_unicorn.android_auth_lib.api.authenticator.database.PublicKeyCredentialSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.reactivex.rxjava3.core.Completable;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CredentialSafeTest {

    private CredentialSafe credentialSafe;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        this.credentialSafe = new CredentialSafe(context, true);

        resetKeystoreAndDatabase()
                .test()
                .assertComplete();
    }

    @After
    public void cleanUp() {
        resetKeystoreAndDatabase()
                .test()
                .assertComplete();
    }

    private Completable resetKeystoreAndDatabase() {
        return credentialSafe.getRxKeyStore().deleteAllEntries()
                .andThen(credentialSafe.deleteAllCredentials());
    }

    @Test
    public void credential_isCreated() {
        credentialSafe.generateCredential("haha.io", new byte[]{0x56, 0x34}, "me")
                .test()
                .assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0, credentialSource -> {
                    assertTrue(credentialSource.getId().length > 0);
                    assertTrue(credentialSource.getKeyPairAlias().length() > 0);
                    assertThat(credentialSource.getUserDisplayName(), is("me"));
                    return true;
                });
    }

    @Test
    public void credential_existsInDatabase() {
        credentialSafe.generateCredential("haha.io", new byte[]{0x56, 0x34}, "me")
                .map(PublicKeyCredentialSource::getId)
                .flatMap(id -> credentialSafe.getCredentialSourceById(id))
                .test()
                .assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0, credentialSource -> {
                    assertTrue(credentialSource.getId().length > 0);
                    assertTrue(credentialSource.getKeyPairAlias().length() > 0);
                    assertThat(credentialSource.getUserDisplayName(), is("me"));
                    return true;
                });
    }

    @Test
    public void credential_existsInKeystore() {
        credentialSafe.generateCredential("haha.io", new byte[]{0x56, 0x34}, "me")
                .map(PublicKeyCredentialSource::getKeyPairAlias)
                .flatMapPublisher(a -> credentialSafe.getRxKeyStore().getAliases())
                .firstOrError()
                .test()
                .assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0, alias -> {
                    assertTrue(alias.length() > 0);
                    return true;
                });
    }

    @Test
    public void credential_IsSuccessfullyDeleted() {
        credentialSafe.generateCredential("haha.io", new byte[]{0x56, 0x34}, "me")
                .flatMapCompletable(credentialSafe::deleteCredential)
                .andThen(credentialSafe.getRxKeyStore().getAliases())
                .test()
                .assertValueCount(0)
                .assertNoErrors();
    }

}
