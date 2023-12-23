package com.example.greenify.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.greenify.R;
import com.example.greenify.activity.walkthru.WalkThruActivity;
import com.example.greenify.model.UserModel;
import com.example.greenify.util.FirebaseAPIs;
import com.example.greenify.util.FirebaseCallback;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SettingFragment extends Fragment {

    private final FirebaseAPIs firebaseAPIs = new FirebaseAPIs();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        TextInputEditText userName, pwd, phone;
        userName = rootView.findViewById(R.id.setting_edt_account_username);
        pwd = rootView.findViewById(R.id.auth_edt_pwd_input);
        phone = rootView.findViewById(R.id.setting_edt_account_phone);

        Button confirmButton, btnLogout;
        confirmButton = rootView.findViewById(R.id.setting_btn_confirm);
        btnLogout = rootView.findViewById(R.id.setting_btn_logout);

        btnLogout.setOnClickListener((v) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Confirmation")
                    .setMessage("Are you sure you want to proceed?")
                    .setPositiveButton("Proceed", (dialogInterface, i) -> {
                        firebaseAuth.signOut();
                        Intent intent = new Intent(requireContext(), WalkThruActivity.class);
                        startActivity(intent);

                        requireActivity().finish();
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
        });

        confirmButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Confirmation")
                    .setMessage("Are you sure you want to proceed?")
                    .setPositiveButton("Proceed", (dialogInterface, i) -> {
                        String username = userName != null ? Objects.requireNonNull(userName.getText()).toString() : "";
                        String password = pwd != null ? Objects.requireNonNull(pwd.getText()).toString() : "";
                        String phoneNum = phone != null ? Objects.requireNonNull(phone.getText()).toString() : "";

                        UserModel.getUserSingleTon().setUsername(username);
                        UserModel.getUserSingleTon().setPhone(phoneNum);

                        firebaseAPIs.updateUserData(UserModel.getUserSingleTon(), new FirebaseCallback() {
                            @Override
                            public void onSuccess(boolean success) {
                                Objects.requireNonNull(firebaseAuth.getCurrentUser()).updatePassword(password);
                                Toast.makeText(requireContext(), "Update Successfully", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(Uri uri) {
                            }

                            @Override
                            public void onFailure(Exception e) {
                            }
                        });
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
        });

        return rootView;
    }
}