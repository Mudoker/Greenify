package com.example.greenify.activity.event;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenify.R;
import com.example.greenify.model.EventModel;
import com.example.greenify.model.UserModel;
import com.example.greenify.util.ApplicationUtils;
import com.example.greenify.util.Environment;
import com.example.greenify.util.FirebaseAPIs;
import com.example.greenify.util.FirebaseCallback;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class EventUpdateFormActivity extends AppCompatActivity {
    //Apis
    private final FirebaseAPIs firebaseAPIs = new FirebaseAPIs();

    private EventModel eventModel;

    private boolean isEdit;

    // Select Image locally
    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        if (uri != null) {
            ShapeableImageView shapeableImageView = findViewById(R.id.img_event);
            shapeableImageView.setImageURI(uri);
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

                    ShapeableImageView shapeableImageView = findViewById(R.id.img_event);
                    shapeableImageView.setImageBitmap(imageBitmap);
                } else if (data.getData() != null) {
                    // Use the URI
                    Uri imageUri = data.getData();
                    ShapeableImageView shapeableImageView = findViewById(R.id.img_event);
                    shapeableImageView.setImageURI(imageUri);
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
        setContentView(R.layout.activity_event_form);
        ApplicationUtils.configureWindowInsets(this);


        ShapeableImageView shapeableImageView = findViewById(R.id.img_event);

        shapeableImageView.setOnClickListener(v -> {
            // Create an AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(EventUpdateFormActivity.this);
            builder.setTitle("Choose an option");

            // Add options to the dialog
            builder.setItems(new CharSequence[]{"Choose File", "Take Image"}, (dialog, which) -> {
                switch (which) {
                    case 0:
                        pickImageLauncher.launch("image/*");
                        break;
                    case 1:
                        Intent intentTakePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        takePictureLauncher.launch(intentTakePicture);
                        break;
                }
            });
            // Show the dialog
            builder.show();
        });

        RadioGroup eventCategory = findViewById(R.id.radio_group);

        AtomicReference<String> selectedEventCategory = new AtomicReference<>("Park");

        eventCategory.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_park) {
                selectedEventCategory.set("Park");
            } else if (checkedId == R.id.radio_beach) {
                selectedEventCategory.set("Beach");
            } else if (checkedId == R.id.radio_street) {
                selectedEventCategory.set("Street");
            }
        });

        EditText eventTitle, eventDes, eventAddress, eventCity, eventZip;
        Button btnCreate;

        eventTitle = findViewById(R.id.edt_event_title);
        eventDes = findViewById(R.id.edt_event_des);
        eventAddress = findViewById(R.id.edt_event_address);
        eventCity = findViewById(R.id.edt_event_city);
        eventZip = findViewById(R.id.edt_event_zip);

        btnCreate = findViewById(R.id.btn_event_create);

        if (isEdit) {
            eventTitle.setText(eventModel.getTitle());
            eventDes.setText(eventModel.getDescription());
            String location = eventModel.getLocation();
            String[] locationParts = location.split(" ");

            if (locationParts.length >= 3) {
                eventAddress.setText(locationParts[0]);
                eventCity.setText(locationParts[1]);
                eventZip.setText(locationParts[2]);
            }

            String edit = "Edit";
            btnCreate.setText(edit);
        }

        btnCreate.setOnClickListener(v -> {
            // Validate event title
            String title = eventTitle.getText().toString().trim();
            if (title.isEmpty()) {
                eventTitle.setError("Field Empty");
                return;
            } else if (title.length() > 150) {
                eventTitle.setError("Maximum 150 characters");
                return;
            }

            // Validate event description
            String description = eventDes.getText().toString().trim();
            if (description.isEmpty()) {
                eventDes.setError("Field Empty");
                return;
            } else if (description.length() > 500) {
                eventDes.setError("Maximum 500 characters");
                return;
            }

            // Validate event description
            String address = eventAddress.getText().toString().trim();
            if (address.isEmpty()) {
                eventDes.setError("Field Empty");
                return;
            } else if (address.length() > 500) {
                eventDes.setError("Maximum 500 characters");
                return;
            }

            // Validate event description
            String city = eventCity.getText().toString().trim();
            if (city.isEmpty()) {
                eventDes.setError("Field Empty");
                return;
            } else if (city.length() > 500) {
                eventDes.setError("Maximum 500 characters");
                return;
            }

            // Validate ZIP code
            String zipCode = eventZip.getText().toString().trim();
            if (zipCode.isEmpty()) {
                eventZip.setError("Field Empty");
                return;
            } else {
                try {
                    int zip = Integer.parseInt(zipCode);
                    if (zip < 501 || zip > 99950) {
                        eventZip.setError("ZIP code should be between 00501 and 99950");
                        return;
                    }
                } catch (NumberFormatException e) {
                    eventZip.setError("Invalid ZIP code");
                    return;
                }
            }

            String location = address + " " + city + " " + zipCode;

            if (isEdit) {
                eventModel.setCategory(String.valueOf(selectedEventCategory));
                eventModel.setDescription(description);
                eventModel.setTitle(title);
                eventModel.setLocation(location);
            } else {
                eventModel = new EventModel(title, description, location, UserModel.getUserSingleTon().getId(), selectedEventCategory.toString());
            }

            firebaseAPIs.updateEventData(eventModel, new FirebaseCallback() {
                @Override
                public void onSuccess(boolean success) {
                    ApplicationUtils.showDialog(EventUpdateFormActivity.this, "System Alert", "Event Created Successfully");
                }

                @Override
                public void onSuccess(Uri uri) {
                }

                @Override
                public void onFailure(Exception e) {
                    ApplicationUtils.showDialog(EventUpdateFormActivity.this, "System Alert", Environment.getSystemResponse().getBAD_REQUEST());
                }
            });

            Bitmap bitmap = ((BitmapDrawable) shapeableImageView.getDrawable()).getBitmap();

            firebaseAPIs.storeMediaToFirebase(eventModel.getId(), bitmap, new FirebaseCallback() {
                @Override
                public void onSuccess(boolean success) {
                }

                @Override
                public void onSuccess(Uri uri) {
                }

                @Override
                public void onFailure(Exception e) {
                }
            });
        });

        TextView btnClose = findViewById(R.id.btn_close_view);
        btnClose.setOnClickListener((v) -> finish());
    }
}