package com.example.greenify.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.greenify.R;
import com.example.greenify.activity.adapter.ProfileEnrolEventAdapter;
import com.example.greenify.activity.adapter.ProfileHostEventAdapter;
import com.example.greenify.activity.event.EventDetailActivity;
import com.example.greenify.model.UserModel;
import com.example.greenify.util.ApplicationUtils;
import com.example.greenify.util.FirebaseAPIs;
import com.example.greenify.util.FirebaseCallback;

import java.util.Objects;

public class ProfileFragment extends Fragment implements ProfileHostEventAdapter.OnItemClickListener {
    // Store data
    FirebaseAPIs firebaseAPIs = new FirebaseAPIs();

    ImageFilterView userAva;

    // Adapter

    ProfileHostEventAdapter profileEventHostAdapter;
    ProfileEnrolEventAdapter profileEventEnrolAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        userAva = rootView.findViewById(R.id.profile_img_user_ava);

        // Select Image locally
        final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                userAva.setImageURI(uri);

                Bitmap bitmap = ApplicationUtils.getBitmapFromUri(requireContext(), uri);

                if (bitmap == null) {
                    ApplicationUtils.showDialog(requireContext(), "System Alert", "Failed to upload image");
                    return;
                }
                firebaseAPIs.storeMediaToFirebase(UserModel.getUserSingleTon().getId(), bitmap, new FirebaseCallback() {
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
            }
        });

        // Use camera
        final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();

                if (data != null) {
                    if (data.hasExtra("data")) {
                        // Handle the result
                        Bitmap imageBitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");

                        if (userAva != null) {
                            userAva.setImageBitmap(imageBitmap);
                        }

                        if (imageBitmap == null) {
                            ApplicationUtils.showDialog(requireContext(), "System Alert", "Failed to upload image");
                            return;
                        }

                        firebaseAPIs.storeMediaToFirebase(UserModel.getUserSingleTon().getId(), imageBitmap, new FirebaseCallback() {
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

                    } else if (data.getData() != null) {
                        // Use the URI
                        Uri imageUri = data.getData();

                        if (userAva != null) {
                            userAva.setImageURI(imageUri);
                        }

                        Bitmap bitmap = ApplicationUtils.getBitmapFromUri(requireContext(), imageUri);

                        if (bitmap == null) {
                            ApplicationUtils.showDialog(requireContext(), "System Alert", "Failed to upload image");
                            return;
                        }
                        firebaseAPIs.storeMediaToFirebase(UserModel.getUserSingleTon().getId(), bitmap, new FirebaseCallback() {
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
                    }
                } else {
                    // Handle data is null
                    Log.e("takePictureLauncher", "Data is null");
                }
            }
        });

        userAva.setOnClickListener(v -> {
            // Create an AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Choose an option");

            // Add options to the dialog
            builder.setItems(new CharSequence[]{"Choose File", "Take Image"}, (dialog, which) -> {
                switch (which) {
                    case 0:
                        pickImageLauncher.launch("image/*");
                        break;
                    case 1:
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        takePictureLauncher.launch(intent);
                        break;
                }
            });

            // Show the dialog
            builder.show();
        });

        // Load data from firebase
        RecyclerView profileEventHostRecyclerView = rootView.findViewById(R.id.profile_list_host_event);
        RecyclerView profileEventEnrolRecyclerView = rootView.findViewById(R.id.profile_list_enrol_event);


        LinearLayoutManager profileEventHostLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        profileEventHostRecyclerView.setLayoutManager(profileEventHostLayoutManager);

        profileEventHostAdapter = new ProfileHostEventAdapter(UserModel.getUserSingleTon().getHostedEvents());
        profileEventHostRecyclerView.setAdapter(profileEventHostAdapter);

        profileEventHostRecyclerView.setVisibility(View.VISIBLE);

        LinearLayoutManager profileEventEnrolLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        profileEventEnrolRecyclerView.setLayoutManager(profileEventEnrolLayoutManager);

        profileEventEnrolAdapter = new ProfileEnrolEventAdapter(UserModel.getUserSingleTon().getJoinedEvents());
        profileEventEnrolRecyclerView.setAdapter(profileEventEnrolAdapter);

        TextView emptyText1 = rootView.findViewById(R.id.profile_txt_host_empty);

        if (profileEventHostAdapter.getItemCount() > 0) {
            emptyText1.setVisibility(View.GONE);
            profileEventHostRecyclerView.setVisibility(View.VISIBLE);
        } else {
            emptyText1.setVisibility(View.VISIBLE);
            profileEventHostRecyclerView.setVisibility(View.GONE);
        }

        TextView emptyText2 = rootView.findViewById(R.id.profile_txt_enrol_empty);

        if (profileEventEnrolAdapter.getItemCount() > 0) {
            emptyText2.setVisibility(View.GONE);
            profileEventEnrolRecyclerView.setVisibility(View.VISIBLE);
        } else {
            emptyText2.setVisibility(View.VISIBLE);
            profileEventEnrolRecyclerView.setVisibility(View.GONE);
        }

        TextView joinedEventNum = rootView.findViewById(R.id.profile_txt_user_total_act_num);
        TextView hostedEventNum = rootView.findViewById(R.id.profile_txt_user_total_host_num);
        TextView points = rootView.findViewById(R.id.profile_txt_user_total_point_num);
        TextView username = rootView.findViewById(R.id.profile_txt_user_username);
        TextView email = rootView.findViewById(R.id.profile_txt_user_email);

        try {
            int joinedNum = UserModel.getUserSingleTon().getJoinedEvents().size();
            int hostedNum = UserModel.getUserSingleTon().getHostedEvents().size();
            joinedEventNum.setText(String.valueOf(joinedNum));
            hostedEventNum.setText(String.valueOf(hostedNum));

            int totalPoints = (joinedNum + hostedNum) * 100;
            points.setText(String.valueOf(totalPoints));
        } catch (NullPointerException e) {
            // Handle the case where any of the accessed objects is null
            e.printStackTrace(); // Log the exception if needed
            joinedEventNum.setText("0");
            hostedEventNum.setText("0");
            points.setText("0");
        }

        username.setText(UserModel.getUserSingleTon().getUsername());
        email.setText(UserModel.getUserSingleTon().getEmail());

        firebaseAPIs.getMediaDownloadUrlFromFirebase(UserModel.getUserSingleTon().getId(), new FirebaseCallback() {
            @Override
            public void onSuccess(boolean success) {
            }

            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ProfileFragment.this).load(uri).into(userAva);
            }

            @Override
            public void onFailure(Exception e) {
            }
        });

        profileEventHostAdapter.setEventList(UserModel.getUserSingleTon().getHostedEvents());
        profileEventEnrolAdapter.setEventList(UserModel.getUserSingleTon().getJoinedEvents());

        profileEventHostAdapter.setOnItemClickListener(this::onItemClick);

        SwipeRefreshLayout swipeRefreshLayout = rootView.findViewById(R.id.profile_swipe_view);

        // Set up the refresh listener
        swipeRefreshLayout.setOnRefreshListener(this::fetchData);

        return rootView;
    }

    public void fetchData() {
        profileEventHostAdapter.setEventList(UserModel.getUserSingleTon().getHostedEvents());
        profileEventEnrolAdapter.setEventList(UserModel.getUserSingleTon().getJoinedEvents());

        // User Avatar
        firebaseAPIs.getMediaDownloadUrlFromFirebase(UserModel.getUserSingleTon().getId(), new FirebaseCallback() {
            @Override
            public void onSuccess(boolean success) {
            }

            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ProfileFragment.this).load(uri).into(userAva);
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    @Override
    public void onItemClick(String selectedEvent) {
        Intent intent = new Intent(requireContext(), EventDetailActivity.class);

        firebaseAPIs.getEventDataById(selectedEvent, eventModel -> {
            intent.putExtra("EVENT_MODEL", eventModel);
            intent.putExtra("OWNER", "true");
            startActivity(intent);
        }, e -> {
            Toast.makeText(requireContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
        });
    }
}