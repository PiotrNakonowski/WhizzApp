package com.example.whizzapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddEvent extends AppCompatActivity {
    private MaterialCardView inputFrameEventTitle;
    private EditText eventTitle;
    private MaterialCardView inputFrameEventDescription;
    private EditText eventDescription;
    private ImageView backIcon;
    private MaterialCardView addEventButton;
    private MaterialCardView addPhotoButton;
    private MaterialCardView inputFramePhoto;
    private TextView addPhotoText;
    private EditText maxAttendanceNumberInput;
    private FirebaseAuth mAuth;
    private Uri uri;
    private String fileName;

    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        inputFrameEventTitle = findViewById(R.id.inputframeEventTitle);
        inputFrameEventDescription = findViewById(R.id.inputframeEventDescription);
        backIcon = findViewById(R.id.back_icon);
        addEventButton = findViewById(R.id.sendButton);
        addPhotoButton = findViewById(R.id.addPhotoButton);
        addPhotoText = findViewById(R.id.addPhotoText);
        eventTitle = findViewById(R.id.eventTitle);
        eventDescription = findViewById(R.id.eventDescription);
        inputFramePhoto = findViewById(R.id.inputframePhoto);
        maxAttendanceNumberInput = findViewById(R.id.maxAttendanceNumberText);
        mAuth = FirebaseAuth.getInstance();

      /*  backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ToDoList.class);
                startActivity(intent);
                finish();
            }
        });*/

        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMediaLauncher.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
            }
        });

        addEventButton.setOnClickListener(v -> addEvent());
    }

    protected void onStart() {
        super.onStart();
        pickMediaLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    uri = result;
                    Log.d("PhotoPicker", "Selected URI: " + result);
                    String path = result.getPath();
                    fileName = path.substring(path.lastIndexOf(":") + 1);
                    fileName = fileName + ".jpg";
                    addPhotoText.setText(fileName);
                    addPhotoText.setTextColor(getResources().getColor(R.color.black));
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }

            }
        });
    }

    protected void sendImages(Uri uri, String fileName, OnImageUploadListener listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference imageRef = storageRef.child("EventImages/" + fileName);
        UploadTask uploadTask = imageRef.putFile(uri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uriTemp -> {
                String downloadUrl = uriTemp.toString();
                Log.d("Firebase", "Download URL: " + downloadUrl);
                if (listener != null) {
                    listener.onImageUploadSuccess(downloadUrl);
                }
            });
        }).addOnFailureListener(exception -> {
            Log.e("Firebase", "Image upload failed: " + exception.getMessage());
            if (listener != null) {
                listener.onImageUploadFailure(exception.getMessage());
            }
        });
    }

    private void addEvent() {
        String title = eventTitle.getText().toString().trim();
        String description = eventDescription.getText().toString().trim();
        long maxAttendanceNumber = Long.parseLong(maxAttendanceNumberInput.getText().toString().trim());
        long attendance = 0;

        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        clearAllEditTextFocus();

        if (title.isEmpty()) {
            eventTitle.setError("Pole Tytuł jest wymagane!");
            eventTitle.requestFocus();
            inputFrameEventTitle.setStrokeColor(getResources().getColor(R.color.error));
            return;
        }

        if (description.isEmpty()) {
            eventDescription.setError("Pole Opis jest wymagane!");
            eventDescription.requestFocus();
            inputFrameEventDescription.setStrokeColor(getResources().getColor(R.color.error));
            return;
        }


        sendImages(uri, fileName, new OnImageUploadListener() {
            @Override
            public void onImageUploadSuccess(String downloadUrl) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference eventCollectionRef = db.collection("events");

                Log.d("Firebase", "Image upload successful");

                Map<String, Object> eventData = new HashMap<>();
                eventData.put("Title", title);
                eventData.put("Description", description);
                eventData.put("PhotoUrl", downloadUrl);
                eventData.put("Attendance", attendance);
                eventData.put("MaxAttendance", maxAttendanceNumber);
                eventCollectionRef.add(eventData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddEvent.this, "Nowe wydarzenie zostało utworzone!", Toast.LENGTH_SHORT).show();
                        Log.d("Firestore", "Dane zostały pomyślnie dodane do bazy danych");
                        Intent intent = new Intent(getApplicationContext(), Events.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(e -> {
                    Log.e("Firestore", "Błąd podczas dodawania danych do bazy danych: " + e.getMessage());
                    Toast.makeText(AddEvent.this, "Wystąpił błąd podczad dodawania wydarzenia!", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onImageUploadFailure(String errorMessage) {
                Log.e("Firebase", "Image upload failed: " + errorMessage);
            }
        });
    }

    private void clearAllEditTextFocus() {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null && currentFocus instanceof EditText) {
            currentFocus.clearFocus();
        }
    }

    interface OnImageUploadListener {
        void onImageUploadSuccess(String downloadUrl);

        void onImageUploadFailure(String errorMessage);
    }
}