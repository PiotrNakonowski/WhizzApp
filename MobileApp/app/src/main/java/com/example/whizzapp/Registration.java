package com.example.whizzapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class Registration extends AppCompatActivity {
    ImageView backIcon;
    private EditText editTextName, editTextSurname, editTextEmail, editTextPassword, editTextConfirmPassword;
    MaterialCardView firstnameFrame, secondnameFrame, passwordFrame, emailFrame, passwordRepeatFrame;
    MaterialCardView regButton;
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

        setFocusChangeListenerForCard(firstnameFrame, R.id.first_name_edit_text);
        setFocusChangeListenerForCard(secondnameFrame, R.id.second_name_edit_text);
        setFocusChangeListenerForCard(emailFrame, R.id.email_edit_text);
        setFocusChangeListenerForCard(passwordFrame, R.id.password_edit_text);
        setFocusChangeListenerForCard(passwordRepeatFrame, R.id.password_confirm_edit_text);

        regButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser()
    {
        String name = editTextName.getText().toString().trim();
        String surname = editTextSurname.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

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

        if (name.isEmpty()) {
            editTextName.setError("Pole Imię jest wymagane!");
            editTextName.requestFocus();
            return;
        }

        if (surname.isEmpty()) {
            editTextSurname.setError("Pole Nazwisko jest wymagane!");
            editTextSurname.requestFocus();
            return;
        }

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

        if (password.length() < 6) {
            editTextPassword.setError("Hasło musi mieć co najmniej 6 znaków!");
            editTextPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Hasła nie są identyczne!");
            editTextConfirmPassword.requestFocus();
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