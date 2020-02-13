package com.blue_unicorn.android_auth_lib.api.authenticator;

import android.content.Context;
import android.util.Pair;

import com.blue_unicorn.android_auth_lib.AuthLibException;
import com.blue_unicorn.android_auth_lib.api.authenticator.database.PublicKeyCredentialSource;
import com.blue_unicorn.android_auth_lib.fido.reponse.BaseGetAssertionResponse;
import com.blue_unicorn.android_auth_lib.fido.reponse.BaseGetInfoResponse;
import com.blue_unicorn.android_auth_lib.fido.request.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.fido.reponse.GetAssertionResponse;
import com.blue_unicorn.android_auth_lib.fido.request.GetInfoRequest;
import com.blue_unicorn.android_auth_lib.fido.reponse.GetInfoResponse;
import com.blue_unicorn.android_auth_lib.fido.request.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.fido.reponse.MakeCredentialResponse;

import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Single;

/**
 * Partly inspired by:
 * <a href="https://github.com/duo-labs/android-webauthn-authenticator">library</a>* from duo-labs
 */
public class BaseAuthenticatorAPI implements AuthenticatorAPI{

    private static final String TAG = "WebauthnAuthenticator";
    public static final int SHA_LENGTH = 32;
    public static final int AUTHENTICATOR_DATA_LENGTH = 141;

    private static final Pair<String, Long> ES256_COSE = new Pair<>("public-key", (long) -7);

    private CredentialSafe credentialSafe;


    public BaseAuthenticatorAPI(Context ctx, boolean authenticationRequired) {
        this.credentialSafe = new CredentialSafe(ctx, authenticationRequired);

    }

    public Single<MakeCredentialRequest> makeCredential(MakeCredentialRequest request) {
        return Single.defer(() -> {
            MakeCredential makeCredential = new MakeCredential(credentialSafe, request);
            return makeCredential.operate();
        });
    }

    public Single<MakeCredentialResponse> makeInternalCredential(MakeCredentialRequest request) {
        return Single.defer(() -> {
            MakeCredential makeCredential = new MakeCredential(credentialSafe, request);
            return makeCredential.operateInner();
        });
    }

    public Single<GetAssertionRequest> getAssertion(GetAssertionRequest request) {

        return Single.just(request);

    }

    public Single<GetAssertionResponse> getInternalAssertion(GetAssertionRequest request) {

        return Single.just(new BaseGetAssertionResponse());

    }

    public Single<GetInfoResponse> getInfo(GetInfoRequest request) {
        return buildOptions()
                .map(options -> new BaseGetInfoResponse(Config.versions, Config.aaguid, options, Config.maxMsgSize));
    }

    private Single<Map<String, Boolean>> buildOptions() {
        return Single.defer(() -> {
            Map<String, Boolean> options = new HashMap<>();
            options.put("plat", Config.plat);
            options.put("rk", Config.rk);
            options.put("up", Config.up);
            options.put("uv", Config.uv);
            return Single.just(options);
        });
    }

    private Single<byte[]> constructAttestedCredentialData(PublicKeyCredentialSource credentialSource) {
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

    private Single<byte[]> constructAuthenticatorData(byte[] rpIdHash, byte[] attestedCredentialData, int authCounter) {
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
            authData.putInt(authCounter);
            if (attestedCredentialData != null) {
                authData.put(attestedCredentialData);
            }
            return Single.just(authData.array());
        });
    }

}
