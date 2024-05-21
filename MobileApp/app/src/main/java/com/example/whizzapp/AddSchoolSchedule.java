package com.example.whizzapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddSchoolSchedule extends AppCompatActivity {
    private ImageView backIcon;
    private MaterialCardView mondayButton, tuesdayButton, wednesdayButton, thursdayButton, fridayButton, sendButton;
    private TextView mondayFilename, tuesdayFilename, wednesdayFilename, thursdayFilename, fridayFilename;
    private String day;
    Map<String, Uri> scheduleImages = new HashMap<>();
    Map<String, TextView> textViewHolders = new HashMap<>();
    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_school_schedule);
        scheduleImages.put("monday", null);
        scheduleImages.put("tuesday", null);
        scheduleImages.put("wednesday", null);
        scheduleImages.put("thursday", null);
        scheduleImages.put("friday", null);
        mondayButton = findViewById(R.id.mondayButton);
        tuesdayButton = findViewById(R.id.tuesdayButton);
        wednesdayButton = findViewById(R.id.wednesdayButton);
        thursdayButton = findViewById(R.id.thursdayButton);
        fridayButton = findViewById(R.id.fridayButton);
        sendButton = findViewById(R.id.sendButton);
        mondayFilename = findViewById(R.id.mondayFilename);
        tuesdayFilename = findViewById(R.id.TuesdayFilename);
        wednesdayFilename = findViewById(R.id.WednesdayFilename);
        thursdayFilename = findViewById(R.id.ThursdayFilename);
        fridayFilename = findViewById(R.id.FridayFilename);
        textViewHolders.put("monday", mondayFilename);
        textViewHolders.put("tuesday", tuesdayFilename);
        textViewHolders.put("wednesday", wednesdayFilename);
        textViewHolders.put("thursday", thursdayFilename);
        textViewHolders.put("friday", fridayFilename);

        mondayButton.setOnClickListener(createButtonClickListener("monday"));
        tuesdayButton.setOnClickListener(createButtonClickListener("tuesday"));
        wednesdayButton.setOnClickListener(createButtonClickListener("wednesday"));
        thursdayButton.setOnClickListener(createButtonClickListener("thursday"));
        fridayButton.setOnClickListener(createButtonClickListener("friday"));

        backIcon = findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SchoolSchedule.class);
                startActivity(intent);
                finish();
            }
        });

        sendButton.setOnClickListener(v -> sendImages());
    }

    private View.OnClickListener createButtonClickListener(final String day) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddSchoolSchedule.this.day = day;
                pickMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        };
    }

    protected void onStart() {
        super.onStart();

        pickMediaLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    Log.d("PhotoPicker", "Selected URI: " + result);
                    scheduleImages.put(day, result);
                    Log.d("ScheduleImages", "Contents:");
                    for (Map.Entry<String, Uri> entry : scheduleImages.entrySet()) {
                        String day = entry.getKey();
                        Uri uri = entry.getValue();
                        Log.d("ScheduleImages", "Day: " + day + ", URI: " + uri);
                    }
                    String path = result.getPath();
                    String filename = path.substring(path.lastIndexOf("/") + 1);
                    filename = filename + ".jpg";
                    textViewHolders.get(day).setText(filename);
                    textViewHolders.get(day).setTextColor(getResources().getColor(R.color.black));
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            }
        });
    }

    private void sendImages() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userID = mAuth.getCurrentUser().getUid();

        for (Map.Entry<String, Uri> entry : scheduleImages.entrySet()) {
            String day = entry.getKey();
            Uri uri = entry.getValue();

            if (uri != null) {
                StorageReference imageRef = storageRef.child("images/" + userID + "/" + day +".jpg");

                UploadTask uploadTask = imageRef.putFile(uri);

                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddSchoolSchedule.this, "Zdjęcia zostały wysłane do bazy danych.", Toast.LENGTH_SHORT).show();
                            Log.d("FirebaseStorage", "Upload successful for day: " + day);
                        } else {
                            Toast.makeText(AddSchoolSchedule.this, "Błąd podczas wysyłania zdjęć do bazy danych.", Toast.LENGTH_SHORT).show();
                            Log.e("FirebaseStorage", "Upload failed for day: " + day, task.getException());
                        }
                    }
                });
            }
        }

        Intent intent = new Intent(getApplicationContext(), SchoolSchedule.class);
        startActivity(intent);
        finish();
    }
}