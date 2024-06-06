package com.example.whizzapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    MaterialCardView loginButton;
    MaterialCardView loginFrame;
    MaterialCardView passwordFrame;
    MaterialCardView registerButton;
    private EditText editTextEmail, editTextPassword;
    private FirebaseAuth mAuth;
    TextView resetPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance(); //Polaczenie z baza, przyda sie do logowania

        registerButton = findViewById(R.id.register_button);
        editTextEmail = findViewById(R.id.emailEditText);
        editTextPassword = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        loginFrame = findViewById(R.id.inputframe_login);
        passwordFrame = findViewById(R.id.inputframe_password);
        resetPassword = findViewById(R.id.password_reminder_button);

        //LOGOWANIE AUTOMATYCZNE JEZELI UZYTKOWNIK JEST ZALOGOWANY

        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified()) {
            startActivity(new Intent(getApplicationContext(), Homepage.class)); //TUTAJ ZMIENIC NA STRONE GLOWNA
            finish();
            return;
        }

        //LOGOWANIE AUTOMATYCZNE JEZELI UZYTKOWNIK JEST ZALOGOWANY


        setFocusChangeListenerForCard(loginFrame, R.id.emailEditText);
        setFocusChangeListenerForCard(passwordFrame, R.id.passwordEditText);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Registration.class);
                startActivity(intent);
                finish();
            }
        });

        loginButton.setOnClickListener(v -> loginUser());


        String text = "Nie pamiętasz hasła? Kliknij!";
        SpannableString spannableString = new SpannableString(text);

        // Color for the part "Kliknij!"
        int start = text.indexOf("Kliknij");
        int end = start + "Kliknij".length();
        int primaryColor = getResources().getColor(R.color.primary);

        spannableString.setSpan(new ForegroundColorSpan(primaryColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Optionally underline the entire text or a part of it
        // spannableString.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        resetPassword.setText(spannableString);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ResetPassword.class);
                startActivity(intent);
                finish();
            }
        });
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

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class); // TUTAJ ZMIENIC NA STRONE GLOWNA, PO UDANYM LOGOWANIU
                            intent.putExtra("userId", user.getUid());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Potwierdź swój adres e-mail, aby się zalogować.", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Logowanie nieudane! Spróbuj ponownie.", Toast.LENGTH_SHORT).show();
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

    private void clearAllEditTextFocus() {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null && currentFocus instanceof EditText) {
            currentFocus.clearFocus();
        }
    }
}