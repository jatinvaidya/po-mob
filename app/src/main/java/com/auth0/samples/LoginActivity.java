package com.auth0.samples;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by lbalmaceda on 5/10/17.
 */

public class LoginActivity extends Activity {

    private static final String API_URL = "http://b7aa70f7.ngrok.io/api/v1/pos";
    private static final String AUDIENCE = "http://po-api.jv-techex.com";
    private static final String SCOPES = "openid offline_access po:read po:write po:delete";
    private static final String LOG_TAG = "PO-MOB";
    
    private String accessToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        Button callAPIWithTokenButton = findViewById(R.id.callAPIWithTokenButton);
        Button callAPIWithoutTokenButton = findViewById(R.id.callAPIWithoutTokenButton);
        Button loginWithTokenButton = findViewById(R.id.loginButton);
        callAPIWithTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAPI(true);
            }
        });
        callAPIWithoutTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAPI(false);
            }
        });
        loginWithTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void callAPI(boolean sendToken) {
        final Request.Builder reqBuilder = new Request.Builder()
                .get()
                .url(API_URL);
        if (sendToken) {
            if (accessToken == null) {
                Toast.makeText(LoginActivity.this, "Token not found. Log in first.", Toast.LENGTH_SHORT).show();
                return;
            }
            reqBuilder.addHeader("Authorization", "Bearer " + accessToken);
        }

        OkHttpClient client = new OkHttpClient();
        Request request = reqBuilder.build();
        Log.d(LOG_TAG, String.format("PO-API request: %s", request.toString()));
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(LOG_TAG, String.format("callAPI onFailure: %s", e.getMessage()), e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            try {
                                Log.d(LOG_TAG, String.format("PO-API response: %s", response.body().string()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(LoginActivity.this, "API call success!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "API call failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void login() {
        Auth0 auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);
        WebAuthProvider.init(auth0)
            .withScheme("demo")
            .withAudience(AUDIENCE)
            .withScope(SCOPES)
            .start(LoginActivity.this, new AuthCallback() {
                @Override
                public void onFailure(@NonNull final Dialog dialog) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.show();
                        }
                    });
                }
                @Override
                public void onFailure(final AuthenticationException exception) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onSuccess(@NonNull final Credentials credentials) {
                    accessToken = credentials.getAccessToken();

                    Log.d(LOG_TAG, "access_token: "+credentials.getAccessToken());
                    Log.d(LOG_TAG, "refresh_token: "+credentials.getRefreshToken());
                    Log.d(LOG_TAG, "id_token: "+credentials.getIdToken());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Log in: Success", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
    }
}
