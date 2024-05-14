package com.example.whizzapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.whizzapp.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class Homepage extends AppCompatActivity {
    TextView textViewWelcome;
    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private MaterialCardView hamburgerIcon;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ImageView logoutIcon;
    private Class<?> currentActivityClass;
    private MaterialCardView navButtonClose, homeButton, schoolScheduleButton, mapButton, todoButton, eventsButton, helpButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        currentActivityClass = getClass();

        mAuth = FirebaseAuth.getInstance();

        textViewWelcome = findViewById(R.id.welcome_message);
        drawerLayout = findViewById(R.id.drawerLayout);
        hamburgerIcon = findViewById(R.id.nav_button);
        logoutIcon = findViewById(R.id.logoutIcon);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //MENU ITEMS//
        navButtonClose = findViewById(R.id.navButtonClose);
        homeButton = findViewById(R.id.homeButton);
        schoolScheduleButton = findViewById(R.id.schoolScheduleButton);
        mapButton = findViewById(R.id.mapButton);
        todoButton = findViewById(R.id.todoButton);
        eventsButton = findViewById(R.id.eventsButton);
        helpButton = findViewById(R.id.helpButton);
        //MENU ITEMS//

        String userId = mAuth.getCurrentUser().getUid();

        if (mAuth.getCurrentUser() != null) {
            // Pobierz imię użytkownika z Firestore na podstawie UID
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(userId);

            docRef.get().addOnCompleteListener(documentTask -> {
                if (documentTask.isSuccessful() && documentTask.getResult() != null) {
                    DocumentSnapshot document = documentTask.getResult();
                    if (document.exists()) {
                        String userName = document.getString("name");
                        textViewWelcome.setText("Witaj, " + userName + "!");
                    }
                }
            });
        }

        menuHandler();
    }

    private void logoutUser() {
        mAuth.signOut();
        Toast.makeText(Homepage.this, "Wylogowano pomyślnie!", Toast.LENGTH_SHORT).show();

        // Przekieruj użytkownika do ekranu logowania
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void menuHandler() {
        hamburgerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                } else {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });

        navButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentActivityClass != Homepage.class) {
                    Intent intent = new Intent(getApplicationContext(), Homepage.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });

        /*schoolScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentActivityClass != SchoolSchedule.class) {
                    Intent intent = new Intent(getApplicationContext(), SchoolSchedule.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });*/

        /*mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentActivityClass != NavigationMap.class) {
                    Intent intent = new Intent(getApplicationContext(), NavigationMap.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });*/

        /*todoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentActivityClass != ToDoList.class) {
                    Intent intent = new Intent(getApplicationContext(), ToDoList.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });*/

        /*eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentActivityClass != Events.class) {
                    Intent intent = new Intent(getApplicationContext(), Events.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });*/

        /*helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentActivityClass != HelpReport.class) {
                    Intent intent = new Intent(getApplicationContext(), HelpReport.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });*/

        logoutIcon.setOnClickListener(v -> logoutUser());
    }
}