package com.blue_unicorn.android_auth_lib;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.blue_unicorn.android_auth_lib.api.authenticator.CredentialSafe;
import com.blue_unicorn.android_auth_lib.api.authenticator.database.PublicKeyCredentialSource;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.reactivex.rxjava3.core.Completable;

public class CredentialSafeTest {

    private CredentialSafe credentialSafe;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        this.credentialSafe = new CredentialSafe(context);

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
        PublicKeyCredentialSource credential =
                credentialSafe.generateCredential("haha.io", new byte[]{0x56, 0x34}, "me")
                        .test()
                        .assertNoErrors()
                        .assertValueCount(1)
                        .values()
                        .get(0);

        assertTrue(credential.getId().length > 0);
        assertTrue(credential.getKeyPairAlias().length() > 0);
        assertThat(credential.getUserDisplayName(), is("me"));
    }

    @Test
    public void credential_existsInDatabase() {
        PublicKeyCredentialSource credential =
                credentialSafe.generateCredential("haha.io", new byte[]{0x56, 0x34}, "me")
                        .map(PublicKeyCredentialSource::getId)
                        .flatMap(id -> credentialSafe.getCredentialSourceById(id))
                        .test()
                        .assertNoErrors()
                        .assertValueCount(1)
                        .values()
                        .get(0);

        assertTrue(credential.getId().length > 0);
        assertTrue(credential.getKeyPairAlias().length() > 0);
        assertThat(credential.getUserDisplayName(), is("me"));
    }

    @Test
    public void credential_existsInKeystore() {
        String alias =
                credentialSafe.generateCredential("haha.io", new byte[]{0x56, 0x34}, "me")
                        .map(PublicKeyCredentialSource::getKeyPairAlias)
                        .flatMapPublisher(a -> credentialSafe.getRxKeyStore().getAliases())
                        .firstOrError()
                        .test()
                        .assertNoErrors()
                        .assertValueCount(1)
                        .values()
                        .get(0);

        assertTrue(alias.length() > 0);
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

    @Test
    public void keyPair_DoesNotRequireVerification() {
        credentialSafe.generateCredential("haha.io", new byte[]{0x56, 0x34}, "me")
                .map(PublicKeyCredentialSource::getKeyPairAlias)
                .flatMap(credentialSafe::keyRequiresVerification)
                .test()
                .assertValue(false)
                .assertNoErrors();
    }

}
