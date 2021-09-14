package com.rmw.photolibrary.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.rmw.photolibrary.R;
import com.rmw.photolibrary.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient googleSignInClient;
    private LoginViewModel loginViewModel;
    private String tokenId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        Button loginButton = findViewById(R.id.activity_login_loginButton);
        loginButton.setOnClickListener(this::signIn);
    }

    private void signIn(View v) {
        Intent intent = googleSignInClient.getSignInIntent();
        activityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
                    if (result.getResultCode() == RC_SIGN_IN) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            // Google Sign In was successful
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            Log.d("MSG", "firebaseAuthWithGoogle:" + account.getId());
                        } catch (ApiException e) {
                            // Google Sign In failed
                            Log.w("MSG", "Google sign in failed", e);
                        }
                    }
                    // Verify GoogleSignIn was successful and authenticate with Firebase
                    handleGoogleSignInResult();
                }
            });

    private void handleGoogleSignInResult() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(LoginActivity.this);
        if (account != null) {
            tokenId = account.getIdToken(); // get Google IdToken for Firebase authentication

            // Log the Firebase user in with their Google IdToken
            loginViewModel.login(tokenId);

            // get the Firebaser users Unique Id from userMutableLiveData and pass it to the next Activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            loginViewModel.getUserMutableLiveData().observe(this, FirebaseUser -> {
                if (FirebaseUser != null) {
                    intent.putExtra("uid", FirebaseUser.getUid()); // pass the Firebase users unique ID to the MainActivity
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}