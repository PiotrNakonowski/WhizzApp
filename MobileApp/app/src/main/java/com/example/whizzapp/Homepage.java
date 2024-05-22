package com.example.whizzapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whizzapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Homepage extends AppCompatActivity {
    TextView textViewWelcome;
    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private MaterialCardView hamburgerIcon;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ImageView logoutIcon;

    private int i;
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

        printEvents();
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

    private int convertDpToPixel(float dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    private void printEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference eventsCollectionRef = db.collection("events");

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        eventsCollectionRef
                .orderBy("CreatedAt", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                        for (DocumentSnapshot document : documents) {

                            Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.open_sans_bold);

                            //*******Title Section*******
                            MaterialCardView sectionTitleContainer = new MaterialCardView(Homepage.this);
                            sectionTitleContainer.setStrokeColor(getColor(R.color.secondary_background));
                            sectionTitleContainer.setCardBackgroundColor(getColor(R.color.secondary_background));
                            sectionTitleContainer.setRadius(convertDpToPixel(20, getApplicationContext()));
                            sectionTitleContainer.setStrokeWidth(convertDpToPixel(2, getApplicationContext()));

                            ViewGroup.MarginLayoutParams sectionTitleParams = new ViewGroup.MarginLayoutParams(convertDpToPixel(125, getApplicationContext()), convertDpToPixel(30, getApplicationContext()));

                            sectionTitleParams.setMargins((screenWidth / 2) - (convertDpToPixel(322, getApplicationContext()) / 2), convertDpToPixel(335 + (i * 178) + (i * 20), getApplicationContext()), 0, (i * 20));
                            sectionTitleContainer.setLayoutParams(sectionTitleParams);

                            TextView sectionTitle = new TextView(Homepage.this);
                            sectionTitle.setText("Wydarzenia");
                            sectionTitle.setGravity(Gravity.CENTER);
                            sectionTitle.setTypeface(typeface);
                            sectionTitle.setTypeface(typeface);
                            sectionTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                            sectionTitle.setTextColor(getColor(R.color.black));


                            sectionTitleContainer.addView(sectionTitle);

                            //*******Title Section*******
                            FrameLayout parentLayout = findViewById(R.id.mainContent);
                            parentLayout.addView(sectionTitleContainer);

                            String documentID = document.getId();
                            Map<String, Object> data = document.getData();
                            Log.d("Firestore", "ID dokumentu: " + documentID);
                            Log.d("Firestore", "Dane dokumentu: " + data);

                            //*******Event Container*******
                            MaterialCardView eventContainer = new MaterialCardView(Homepage.this);
                            eventContainer.setStrokeColor(getColor(R.color.secondary_background));
                            eventContainer.setCardBackgroundColor(getColor(R.color.secondary_background));
                            eventContainer.setRadius(convertDpToPixel(20, getApplicationContext()));
                            eventContainer.setStrokeWidth(convertDpToPixel(2, getApplicationContext()));

                            ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(convertDpToPixel(322, getApplicationContext()), convertDpToPixel(130, getApplicationContext()));

                            marginLayoutParams.setMargins((screenWidth / 2) - (convertDpToPixel(322, getApplicationContext()) / 2), convertDpToPixel(375 + (i * 178) + (i * 20), getApplicationContext()), 0, (i * 20));
                            eventContainer.setLayoutParams(marginLayoutParams);

                            //*******Event Container*******

                            //*******Title*******
                            TextView title = new TextView(Homepage.this);
                            title.setText(data.get("Title").toString());
                            title.setTypeface(typeface);
                            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                            title.setTextColor(getColor(R.color.black));
                            ViewGroup.MarginLayoutParams titleLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            titleLayoutParams.setMargins(convertDpToPixel(20, getApplicationContext()), convertDpToPixel(10, getApplicationContext()), 0, 0);
                            title.setLayoutParams(titleLayoutParams);
                            title.setGravity(Gravity.TOP);

                            eventContainer.addView(title);
                            //*******Title*******

                            //*******Description*******
                            TextView description = new TextView(Homepage.this);
                            description.setText(data.get("Description").toString());
                            Typeface descripionTypeface = ResourcesCompat.getFont(getApplicationContext(), R.font.open_sans);
                            description.setTypeface(descripionTypeface);
                            description.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                            description.setTextColor(getColor(R.color.black));
                            int specificWidthInDp = 180;
                            int specificWidthInPx = convertDpToPixel(specificWidthInDp, getApplicationContext());

                            int specificHeightInDp = 87;
                            int specificHeightInPx = convertDpToPixel(specificHeightInDp, getApplicationContext());

                            ViewGroup.MarginLayoutParams descriptionLayoutParams = new ViewGroup.MarginLayoutParams(specificWidthInPx, specificHeightInPx);
                            descriptionLayoutParams.setMargins(convertDpToPixel(20, getApplicationContext()), convertDpToPixel(35, getApplicationContext()), 0, 0);
                            description.setLayoutParams(descriptionLayoutParams);

                            eventContainer.addView(description);
                            //*******Description*******

                            //*******Photo*******
                            ImageView photoView = new ImageView(Homepage.this);
                            ViewGroup.MarginLayoutParams photoViewParams = new ViewGroup.MarginLayoutParams(convertDpToPixel(100, getApplicationContext()), convertDpToPixel(110, getApplicationContext()));
                            photoViewParams.setMargins(convertDpToPixel(210, getApplicationContext()), convertDpToPixel(15, getApplicationContext()), 0, 0);
                            photoView.setLayoutParams(photoViewParams);

                            Picasso.get().load(data.get("PhotoUrl").toString()).into(photoView);

                            eventContainer.addView(photoView);
                            //*******Photo*******


                            parentLayout.addView(eventContainer);
                            i++;
                        }
                    } else {
                        Log.d("Firestore", "Brak dokumentów w kolekcji.");
                    }
                } else {
                    Log.e("Firestore", "Błąd podczas pobierania dokumentów: " + task.getException().getMessage());
                }
            }
        });
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

        schoolScheduleButton.setOnClickListener(new View.OnClickListener() {
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
        });

       /* mapButton.setOnClickListener(new View.OnClickListener() {
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

       /* todoButton.setOnClickListener(new View.OnClickListener() {
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

        eventsButton.setOnClickListener(v -> {
            if (currentActivityClass != Events.class) {
                Intent newintent = new Intent(getApplicationContext(), Events.class);
                startActivity(newintent);
                finish();
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

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