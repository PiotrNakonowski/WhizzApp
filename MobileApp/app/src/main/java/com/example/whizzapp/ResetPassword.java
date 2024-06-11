package com.example.whizzapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {
    ImageView imageView;
    private FirebaseAuth mAuth;
    private MaterialCardView inputFrameEmail;
    private EditText editTextEmail;
    private MaterialCardView resetPasswordButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();

        inputFrameEmail = findViewById(R.id.inputframe_email);
        editTextEmail = findViewById(R.id.email_edit_text);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        imageView = findViewById(R.id.back_icon);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);

        setFocusChangeListenerForCard(inputFrameEmail, R.id.email_edit_text);

        resetPasswordButton.setOnClickListener(v -> resetPassword());
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

    private void resetPassword() {
        String email = editTextEmail.getText().toString().trim();

        try
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        clearAllEditTextFocus();

        if (email.isEmpty()) {
            editTextEmail.setError("Wprowadź adres e-mail");
            editTextEmail.requestFocus();
            inputFrameEmail.setStrokeColor(getColor(R.color.error));
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Wprowadź poprawny adres e-mail!");
            editTextEmail.requestFocus();
            inputFrameEmail.setStrokeColor(getColor(R.color.error));
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(findViewById(android.R.id.content), "Jeżeli wprowadzony adres e-mail jest powiązany z kontem, to wiadomość została wysłana.", Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), "Nie udało się wysłać linku do zresetowania hasła.", Snackbar.LENGTH_LONG).show();
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