package com.yang.milesinterview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.uber.sdk.android.core.auth.AccessTokenManager;
import com.uber.sdk.android.core.auth.AuthenticationError;
import com.uber.sdk.android.core.auth.LoginButton;
import com.uber.sdk.android.core.auth.LoginCallback;
import com.uber.sdk.android.core.auth.LoginManager;
import com.uber.sdk.core.auth.AccessToken;
import com.uber.sdk.core.auth.AccessTokenStorage;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.core.client.SessionConfiguration;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "A0B8Tsq8GeTx99JB1gxwg94spxcDtFhO";
    //    private static final Set<Scope> GENERAL_SCOPES = Sets.newHashSet(Scope.HISTORY, Scope.PROFILE);
    private static final String REDIRECT_URI = "com.yang.milesinterview.uberauth://redirect";
    //    http://localhost
    private static final int CUSTOM_BUTTON_REQUEST_CODE = 1113;


    private Button myButton;
    private AccessTokenStorage accessTokenStorage;
    private LoginManager loginManager;
    private SessionConfiguration configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configuration = new SessionConfiguration.Builder()
                .setClientId(CLIENT_ID) //This is necessary
                .setRedirectUri(REDIRECT_URI) //This is necessary if you'll be using implicit grant
                .setEnvironment(SessionConfiguration.Environment.SANDBOX) //Useful for testing your app in the sandbox environment
                .setScopes(Arrays.asList(Scope.PROFILE, Scope.RIDE_WIDGETS)) //Your scopes for authentication here
                .build();

        accessTokenStorage = new AccessTokenManager(this);

        //Use a custom button with an onClickListener to call the LoginManager directly
        loginManager = new LoginManager(accessTokenStorage,
                new SampleLoginCallback(),
                configuration,
                CUSTOM_BUTTON_REQUEST_CODE);

        myButton = (Button) findViewById(R.id.my_button);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginManager.login(MainActivity.this);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Yang", String.format("onActivityResult requestCode:[%s] resultCode [%s]",
                requestCode, resultCode));

        //Allow each a chance to catch it.
        loginManager.onActivityResult(this, requestCode, resultCode, data);
    }


    private class SampleLoginCallback implements LoginCallback {

        @Override
        public void onLoginCancel() {
            Toast.makeText(MainActivity.this, R.string.user_cancels_message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onLoginError(@NonNull AuthenticationError error) {
            Toast.makeText(MainActivity.this,
                    getString(R.string.login_error_message, error.name()), Toast.LENGTH_LONG)
                    .show();

            Log.d("Yang", "Error");
        }

        @Override
        public void onLoginSuccess(@NonNull AccessToken accessToken) {
//        loadProfileInfo();
            Toast.makeText(MainActivity.this,
                    "Success", Toast.LENGTH_LONG)
                    .show();
            Log.d("Yang", "Success");
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(intent);
        }

        @Override
        public void onAuthorizationCodeReceived(@NonNull String authorizationCode) {
            Toast.makeText(MainActivity.this, getString(R.string.authorization_code_message, authorizationCode),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (loginManager.isAuthenticated()) {
            Toast.makeText(this, "onResume(),isAuthenticated()", Toast.LENGTH_LONG).show();
        }
    }

}
