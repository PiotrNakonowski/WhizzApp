package com.example.whizzapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {
    ImageView backIcon;
    private EditText editTextName, editTextSurname, editTextEmail, editTextPassword, editTextConfirmPassword;
    MaterialCardView firstnameFrame, secondnameFrame, passwordFrame, emailFrame, passwordRepeatFrame;
    MaterialCardView regButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CheckBox privacyBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        backIcon = findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextName = findViewById(R.id.first_name_edit_text);
        editTextSurname = findViewById(R.id.second_name_edit_text);
        editTextEmail = findViewById(R.id.email_edit_text);
        editTextPassword = findViewById(R.id.password_edit_text);
        editTextConfirmPassword = findViewById(R.id.password_confirm_edit_text);
        regButton = findViewById(R.id.reg_button);
        firstnameFrame = findViewById(R.id.input_frame_firstname);
        secondnameFrame = findViewById(R.id.inputframe_secondname);
        passwordFrame = findViewById(R.id.inputframe_password);
        emailFrame = findViewById(R.id.inputframe_email);
        passwordRepeatFrame = findViewById(R.id.inputframe_password_repeat);
        privacyBox = findViewById(R.id.privacyBox);

        setFocusChangeListenerForCard(firstnameFrame, R.id.first_name_edit_text);
        setFocusChangeListenerForCard(secondnameFrame, R.id.second_name_edit_text);
        setFocusChangeListenerForCard(emailFrame, R.id.email_edit_text);
        setFocusChangeListenerForCard(passwordFrame, R.id.password_edit_text);
        setFocusChangeListenerForCard(passwordRepeatFrame, R.id.password_confirm_edit_text);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);

        regButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = editTextName.getText().toString().trim();
        String surname = editTextSurname.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        clearAllEditTextFocus();

        if (name.isEmpty()) {
            editTextName.setError("Pole Imię jest wymagane!");
            editTextName.requestFocus();
            firstnameFrame.setStrokeColor(getColor(R.color.error));
            return;
        }

        if (surname.isEmpty()) {
            editTextSurname.setError("Pole Nazwisko jest wymagane!");
            editTextSurname.requestFocus();
            secondnameFrame.setStrokeColor(getColor(R.color.error));
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Pole E-mail jest wymagane!");
            editTextEmail.requestFocus();
            emailFrame.setStrokeColor(getColor(R.color.error));
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Wprowadź poprawny adres e-mail!");
            editTextEmail.requestFocus();
            emailFrame.setStrokeColor(getColor(R.color.error));
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Pole Hasło jest wymagane!");
            editTextPassword.requestFocus();
            passwordFrame.setStrokeColor(getColor(R.color.error));
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Hasło musi mieć co najmniej 6 znaków!");
            editTextPassword.requestFocus();
            passwordFrame.setStrokeColor(getColor(R.color.error));
            return;
        }

        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Hasła nie są identyczne!");
            editTextConfirmPassword.requestFocus();
            passwordRepeatFrame.setStrokeColor(getColor(R.color.error));
            return;
        }

        if (!privacyBox.isChecked()) {
            privacyBox.setError("Musisz zaakceptować przetwarzanie danych osobowych!");
            Toast.makeText(this, "Musisz zaakceptować przetwarzanie danych osobowych!", Toast.LENGTH_SHORT).show();
            privacyBox.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("name", name);
                        userData.put("surname", surname);
                        userData.put("email", email);
                        userData.put("createdAt", FieldValue.serverTimestamp());
                        userData.put("isAdmin", false);

                        db.collection("users")
                                .document(mAuth.getCurrentUser().getUid())
                                .set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    sendEmailVerification();
                                    Toast.makeText(Registration.this, "Rejestracja udana!", Toast.LENGTH_SHORT).show();
                                    Log.d("Registration", "Dane zostały zapisane do Firestore.");
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(Registration.this, "Błąd podczas dodawania danych do Firestore", Toast.LENGTH_SHORT).show();
                                    Log.e("Registration", "Błąd podczas zapisu do Firestore.", e);
                                });
                    } else {
                        Toast.makeText(Registration.this, "Rejestracja nieudana! Spróbuj ponownie.", Toast.LENGTH_SHORT).show();
                        Log.e("Registration", "Błąd podczas tworzenia użytkownika Firebase.", task.getException());
                    }
                });

    }

    private void sendEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Registration.this, "Wiadomość z weryfikacją adresu e-mail została wysłana.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Registration.this, "Nie udało się wysłać wiadomości z weryfikacją e-mail.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
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

    private void clearAllEditTextFocus() {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null && currentFocus instanceof EditText) {
            currentFocus.clearFocus();
        }
    }
}