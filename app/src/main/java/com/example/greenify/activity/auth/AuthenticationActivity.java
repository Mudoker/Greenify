package com.example.greenify.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.greenify.R;
import com.example.greenify.activity.main.MainActivity;
import com.example.greenify.model.UserModel;
import com.example.greenify.util.ApplicationUtils;
import com.example.greenify.util.Environment;
import com.example.greenify.util.FirebaseAPIs;
import com.example.greenify.util.SystemResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class AuthenticationActivity extends AppCompatActivity {

//    private final NotificationHelper notificationHelper = ApplicationUtils.getNotificationHelper();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final SystemResponse SYSTEM_RESPONSE = Environment.getSystemResponse();

    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();

            Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount googleSignInAccount = googleSignInAccountTask.getResult(ApiException.class);
                AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);

                firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ApplicationUtils.showDialog(AuthenticationActivity.this, "Authentication", SYSTEM_RESPONSE.getSuccess());

                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }

        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_authentication);
        // Hide system bars after setting content view
        Window window = getWindow();
        WindowInsetsController insetsController = window.getInsetsController();
        if (insetsController != null) {
            insetsController.hide(WindowInsets.Type.systemBars());
            insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }

        Button btnLogin = findViewById(R.id.auth_btn_login);
        TextView btnRegister = findViewById(R.id.auth_btn_sign_up);

        TextView btnNeedHelp = findViewById(R.id.auth_btn_need_help);
        TextView btnForgotPwd = findViewById(R.id.auth_btn_forgot_pwd);

        TextInputEditText userEmail = findViewById(R.id.auth_edt_email_input);
        TextInputEditText userPassword = findViewById(R.id.auth_edt_pwd_input);

        CardView btnGoogleSignIn = findViewById(R.id.auth_card_login_google);

        btnNeedHelp.setOnClickListener((v) -> ApplicationUtils.sendEmail(this));

        btnRegister.setOnClickListener((v) -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });

        btnLogin.setOnClickListener((v) -> {
            String email = Objects.requireNonNull(userEmail.getText()).toString();
            String pwd = Objects.requireNonNull(userPassword.getText()).toString();

            if (email.equals("")) {
                userEmail.setError("Empty Field");
                return;
            }

            if (pwd.equals("")) {
                userPassword.setError("Empty Field");
                return;
            }

            firebaseAuth.signInWithEmailAndPassword(email, pwd).addOnSuccessListener(authResult -> {
                ApplicationUtils.showDialog(AuthenticationActivity.this, "Authentication", SYSTEM_RESPONSE.getSuccess());

                Objects.requireNonNull(authResult.getUser()).getUid();

                FirebaseAPIs firebaseAPIs = new FirebaseAPIs();
                firebaseAPIs.getUserDataById(authResult.getUser().getUid(), userModel -> {
                    UserModel.setUserSingleTon(userModel);
                    Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();


                }, e -> ApplicationUtils.showDialog(AuthenticationActivity.this, "Authentication Failed", SYSTEM_RESPONSE.getBAD_REQUEST()));
            });
        });

        btnForgotPwd.setOnClickListener((v) -> {
            String email = Objects.requireNonNull(userEmail.getText()).toString();
            if (email.equals("")) {
                userEmail.setError("Empty Field");
                return;
            }

            if (!ApplicationUtils.validateEmail(email)) {
                userEmail.setError("Invalid Email");
            }

            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ApplicationUtils.showDialog(AuthenticationActivity.this, "Reset Email", SYSTEM_RESPONSE.getSendToMailBox());
                } else {
                    ApplicationUtils.showDialog(AuthenticationActivity.this, "Reset Email", SYSTEM_RESPONSE.getBAD_REQUEST());
                }
            });
        });

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.oauth_client_id)).requestEmail().build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, signInOptions);

        btnGoogleSignIn.setOnClickListener((v) -> {
            Intent intent = googleSignInClient.getSignInIntent();

            googleSignInLauncher.launch(intent);
        });
    }
}