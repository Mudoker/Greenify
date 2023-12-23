package com.example.greenify.activity.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenify.R;
import com.example.greenify.activity.main.MainActivity;
import com.example.greenify.model.SettingModel;
import com.example.greenify.model.UserModel;
import com.example.greenify.util.ApplicationUtils;
import com.example.greenify.util.Environment;
import com.example.greenify.util.FirebaseAPIs;
import com.example.greenify.util.FirebaseCallback;
import com.example.greenify.util.SystemResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {

    private final SystemResponse SYSTEM_RESPONSE = Environment.getSystemResponse();

    // APIs
    private final FirebaseAPIs firebaseAPIs = new FirebaseAPIs();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_sign_up);

        // Hide system bars after setting content view
        Window window = getWindow();
        WindowInsetsController insetsController = window.getInsetsController();
        if (insetsController != null) {
            insetsController.hide(WindowInsets.Type.systemBars());
            insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        MaterialButton btnRegister = findViewById(R.id.reg_btn_register);

        Button btnLogin = findViewById(R.id.reg_btn_login);

        TextInputEditText userUsername = findViewById(R.id.reg_edt_username_input);

        TextInputEditText userEmail = findViewById(R.id.reg_edt_email_input);

        TextInputEditText userPassword = findViewById(R.id.reg_edt_pwd_input);

        btnLogin.setOnClickListener((v) -> {
            Intent intent = new Intent(this, AuthenticationActivity.class);
            startActivity(intent);
            finish();
        });

        btnRegister.setOnClickListener((v) -> {
            String email = Objects.requireNonNull(userEmail.getText()).toString();
            String password = Objects.requireNonNull(userPassword.getText()).toString();
            String username = Objects.requireNonNull(userUsername.getText()).toString();

            if (isValidField(username, userUsername, SYSTEM_RESPONSE.getINVALID_CREDENTIALS()))
                return;
            if (isValidField(email, userEmail, SYSTEM_RESPONSE.getINVALID_CREDENTIALS())) return;
            if (isValidField(password, userPassword, SYSTEM_RESPONSE.getINVALID_CREDENTIALS()))
                return;

            if (!ApplicationUtils.validateEmail(email)) {
                userEmail.setError("Invalid Email");
                return;
            }

            if (password.length() < 6) {
                userPassword.setError("Must be at least 6 characters");
                return;
            }

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                    String userid;
                    if (currentFirebaseUser != null) {
                        userid = currentFirebaseUser.getUid();
                    } else {
                        userid = UUID.randomUUID().toString();
                    }

                    UserModel user = new UserModel(userid, username, email, Environment.getDeviceToken());
                    UserModel.setUserSingleTon(user);
                    SettingModel settingModel = new SettingModel(user.getId(), 0, true, "m", 17.0);

                    firebaseAPIs.storeUserData(user, new FirebaseCallback() {
                        @Override
                        public void onSuccess(boolean success) {
                            ApplicationUtils.showDialog(SignUpActivity.this, "System Alert", "User Created Successfully");
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);

                            FirebaseAPIs firebaseAPIs = new FirebaseAPIs();
                            firebaseAPIs.getUserDataById(UserModel.getUserSingleTon().getId(), userModel -> {
                                startActivity(intent);
                                finish();
                            }, e -> {
                                Toast.makeText(SignUpActivity.this, "Failed getting data user", Toast.LENGTH_SHORT).show();
                            });
                        }

                        @Override
                        public void onSuccess(Uri uri) {
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("Initialize User", String.valueOf(e));
                            ApplicationUtils.showDialog(SignUpActivity.this, "System Alert", Environment.getSystemResponse().getBAD_REQUEST());
                        }
                    });

                    firebaseAPIs.storeSettingModelData(settingModel, new FirebaseCallback() {
                        @Override
                        public void onSuccess(boolean success) {
                        }

                        @Override
                        public void onSuccess(Uri uri) {
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("Initialize Setting", String.valueOf(e));
                        }
                    });
                } else {
                    ApplicationUtils.showDialog(SignUpActivity.this, "System Alert", SYSTEM_RESPONSE.getBAD_REQUEST());
                }
            });
        });
    }

    private boolean isValidField(String fieldValue, TextInputEditText editText, String errorMessage) {
        if (fieldValue.isEmpty()) {
            editText.setError(errorMessage);
            return true;
        }
        return false;
    }
}