package com.example.greenify.activity.walkthru;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.utils.widget.ImageFilterView;

import com.bumptech.glide.Glide;
import com.example.greenify.R;
import com.example.greenify.activity.main.MainActivity;
import com.example.greenify.util.ApplicationUtils;
import com.example.greenify.util.FirebaseAPIs;
import com.example.greenify.util.FirebaseCallback;
import com.example.greenify.util.NotificationHelper;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;
import java.util.UUID;

public class test extends AppCompatActivity {
    // Select Image locally
    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        if (uri != null) {
            ImageFilterView imageFilterView = findViewById(R.id.img_selected);
            imageFilterView.setImageURI(uri);

        }
    });

    // Use camera
    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();

            if (data != null) {
                if (data.hasExtra("data")) {
                    // Handle the result
                    Bitmap imageBitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");

                    ImageFilterView imageFilterView = findViewById(R.id.img_selected);

                    if (imageFilterView != null) {
                        imageFilterView.setImageBitmap(imageBitmap);
                    }

                } else if (data.getData() != null) {
                    // Use the URI
                    Uri imageUri = data.getData();

                    ImageFilterView imageFilterView = findViewById(R.id.img_selected);

                    if (imageFilterView != null) {
                        imageFilterView.setImageURI(imageUri);
                    }
                }
            } else {
                // Handle data is null
                Log.e("takePictureLauncher", "Data is null");
            }
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("TOKEN_F", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    // Log and toast
                    String msg = "User token" + token;
                    Log.d("TOKEN_S", msg);
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                });

        Button buttonSelectImg = findViewById(R.id.btn_select_img);

        buttonSelectImg.setOnClickListener((v) -> {
            ApplicationUtils.requestPermissions(this);
            NotificationHelper notificationHelper = ApplicationUtils.getNotificationHelper();
            notificationHelper.sendNotification(this, R.drawable.greenify_app_logo, "1", "test", "hello", R.color.dark_blue, new MainActivity());
//            pickImageLauncher.launch("image/*");
        });

        Button buttonOpenCamera = findViewById(R.id.btn_open_camera);

        buttonOpenCamera.setOnClickListener((v) -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureLauncher.launch(intent);
        });

        ImageFilterView imageFilterView = findViewById(R.id.img_selected);

        FirebaseAPIs firebaseAPIs = new FirebaseAPIs();
        firebaseAPIs.getMediaDownloadUrlFromFirebase(UUID.fromString("6c21c5b5-8a3c-4e0f-8329-394a8d63e0f8"), new FirebaseCallback() {
            @Override
            public void onSuccess(boolean success) {

            }

            @Override
            public void onSuccess(Uri uri) {
                Glide.with(test.this).load(uri).into(imageFilterView);
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }
}