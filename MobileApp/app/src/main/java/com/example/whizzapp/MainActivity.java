package com.example.whizzapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {
    MaterialCardView login_button;
    MaterialCardView login_frame;
    MaterialCardView password_frame;
    MaterialCardView registerButton;
    private EditText editTextEmail, editTextPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerButton = findViewById(R.id.register_button);

        editTextEmail = findViewById(R.id.emailEditText);
        editTextPassword = findViewById(R.id.passwordEditText);
        login_button = findViewById(R.id.loginButton);
        login_frame = findViewById(R.id.inputframe_login);
        password_frame = findViewById(R.id.inputframe_password);

        setFocusChangeListenerForCard(login_frame, R.id.emailEditText);
        setFocusChangeListenerForCard(password_frame, R.id.passwordEditText);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Registration.class);
                startActivity(intent);
                finish();
            }
        });

        login_button.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        clearAllEditTextFocus();

        if (email.isEmpty()) {
            editTextEmail.setError("Pole E-mail jest wymagane!");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Wprowadź poprawny adres e-mail!");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Pole Hasło jest wymagane!");
            editTextPassword.requestFocus();
            return;
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