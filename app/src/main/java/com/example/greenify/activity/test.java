package com.example.greenify.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.utils.widget.ImageFilterView;

import com.example.greenify.R;
import com.example.greenify.util.ApplicationUtils;

import java.util.Objects;

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

        Button buttonSelectImg = findViewById(R.id.btn_select_img);

        buttonSelectImg.setOnClickListener((v) -> {
            ApplicationUtils.requestPermissions(this);
            pickImageLauncher.launch("image/*");
        });

        Button buttonOpenCamera = findViewById(R.id.btn_open_camera);

        buttonOpenCamera.setOnClickListener((v) -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureLauncher.launch(intent);
        });
    }
}