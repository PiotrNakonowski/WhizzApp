package com.example.whizzapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddEvent extends AppCompatActivity {
    ImageView backIcon;
    private MaterialCardView inputFrameEventTitle;
    private EditText eventTitle;
    private FirebaseAuth mAuth;
    private MaterialCardView inputFrameEventDescription;
    private EditText eventDescription;
    private MaterialCardView addEventButton;
    private MaterialCardView addPhotoButton;
    private TextView addPhotoText;
    private MaterialCardView inputFramePhoto;
    private MaterialCardView inputFrameMaxAttendanceNumberInput;
    private EditText maxAttendanceNumberInput;
    private Uri uri;
    private String fileName;

    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        backIcon = findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Events.class);
                startActivity(intent);
                finish();
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getApplicationContext(), Events.class);
                startActivity(intent);
                finish();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);


        inputFrameEventTitle = findViewById(R.id.inputframeEventTitle);
        inputFrameEventDescription = findViewById(R.id.inputframeEventDescription);
        addEventButton = findViewById(R.id.sendButton);
        addPhotoButton = findViewById(R.id.addPhotoButton);
        addPhotoText = findViewById(R.id.addPhotoText);
        inputFramePhoto = findViewById(R.id.inputframePhoto);
        eventTitle = findViewById(R.id.eventTitle);
        eventDescription = findViewById(R.id.eventDescription);
        inputFrameMaxAttendanceNumberInput = findViewById(R.id.maxAttendanceNumber);
        maxAttendanceNumberInput = findViewById(R.id.maxAttendanceNumberText);

        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMediaLauncher.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
            }
        });

        addEventButton.setOnClickListener(v -> addEvent());
        setFocusChangeListenerForCard(inputFrameEventTitle, R.id.eventTitle);
        setFocusChangeListenerForCard(inputFrameEventDescription, R.id.eventDescription);
        setFocusChangeListenerForCard(inputFrameMaxAttendanceNumberInput, R.id.maxAttendanceNumberText);
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

    private void setFocusChangeListenerForCard(MaterialCardView cardView, int editTextId) {
        EditText editText = findViewById(editTextId);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    cardView.setStrokeColor(Color.parseColor("#D87648"));
                } else {
                    cardView.setStrokeColor(Color.parseColor("#B7B7B7"));
                }
            }
        });
    }

    protected void sendImages(Uri uri, long fileName, OnImageUploadListener listener) {
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

        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }


        boolean error = false;

        long maxAttendanceNumber = 0;
        try {
            maxAttendanceNumber = Long.parseLong(maxAttendanceNumberInput.getText().toString().trim());
        } catch (NumberFormatException e) {
            maxAttendanceNumberInput.setError("Zły format liczby!");
            maxAttendanceNumberInput.requestFocus();
            inputFrameMaxAttendanceNumberInput.setStrokeColor(getResources().getColor(R.color.error));
            error = true;
        }

        if (!error) {
            inputFrameMaxAttendanceNumberInput.setStrokeColor(getResources().getColor(R.color.input_color));
        }

        long attendance = 0;

        if (title.isEmpty()) {
            eventTitle.setError("Pole Tytuł jest wymagane!");
            eventTitle.requestFocus();
            inputFrameEventTitle.setStrokeColor(getResources().getColor(R.color.error));
            error = true;
        } else {
            inputFrameEventTitle.setStrokeColor(getResources().getColor(R.color.input_color));
        }

        if (description.isEmpty()) {
            eventDescription.setError("Pole Opis jest wymagane!");
            eventDescription.requestFocus();
            inputFrameEventDescription.setStrokeColor(getResources().getColor(R.color.error));
            error = true;
        } else {
            inputFrameEventDescription.setStrokeColor(getResources().getColor(R.color.input_color));
        }

        if (uri == null) {
            addPhotoText.setError("Zdjęcie jest wymagane!");
            addPhotoText.requestFocus();
            inputFramePhoto.setStrokeColor(getResources().getColor(R.color.error));
            error = true;
        } else {
            addPhotoText.setError(null);
            inputFramePhoto.setStrokeColor(getResources().getColor(R.color.input_color));
        }

        if (error) {
            return;
        }

        long finalMaxAttendanceNumber = maxAttendanceNumber;
        Random random = new Random();
        long imgId = random.nextLong();
        sendImages(uri, imgId, new OnImageUploadListener() {
            @Override
            public void onImageUploadSuccess(String downloadUrl) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference eventCollectionRef = db.collection("events");

                Log.d("Firebase", "Image upload successful");

                mAuth = FirebaseAuth.getInstance();

                String userId = mAuth.getCurrentUser().getUid();
                DocumentReference docRef = db.collection("users").document(userId);

                docRef.get().addOnCompleteListener(documentTask -> {
                    if (documentTask.isSuccessful() && documentTask.getResult() != null) {
                        DocumentSnapshot document = documentTask.getResult();
                        if (document.exists()) {
                            Map<String, Object> eventData = new HashMap<>();

                            String name = document.getString("name");
                            String surname = document.getString("surname");

                            eventData.put("Name", name);
                            eventData.put("Surname", surname);
                            eventData.put("Title", title);
                            eventData.put("Description", description);
                            eventData.put("PhotoUrl", downloadUrl);
                            eventData.put("Attendance", attendance);
                            eventData.put("MaxAttendance", finalMaxAttendanceNumber);
                            eventData.put("CreatedAt", FieldValue.serverTimestamp());
                            eventData.put("Approved", false);
                            eventData.put("Owner", userId);

                            eventCollectionRef.add(eventData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(AddEvent.this, "Wydarzenie utworzone. Po weryfikacji wydarzenie stanie się widoczne", Toast.LENGTH_LONG).show();
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
                    }
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
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
    }

    interface OnImageUploadListener {
        void onImageUploadSuccess(String downloadUrl);

        void onImageUploadFailure(String errorMessage);
    }
}