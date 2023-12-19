package com.example.greenify.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenify.R;
import com.example.greenify.util.ApplicationUtils;
import com.example.greenify.util.Environment;
import com.example.greenify.util.SystemResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private final SystemResponse SYSTEM_RESPONSE = Environment.getSystemResponse();

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

            if (password.length() <= 6) {
                userPassword.setError("Must be at least 6 characters");
                return;
            }

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("REGISTER_S", "createUserWithEmail:success");
                    ApplicationUtils.showDialog(SignUpActivity.this, "System Alert", "Register Successfully");
                } else {
                    Log.w("REGISTER_F", "createUserWithEmail:failure", task.getException());
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