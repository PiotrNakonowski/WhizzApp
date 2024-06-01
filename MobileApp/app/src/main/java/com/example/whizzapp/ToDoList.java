package com.example.whizzapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;


public class ToDoList extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private MaterialCardView hamburgerIcon;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ImageView logoutIcon;
    private Class<?> currentActivityClass;
    private MaterialCardView navButtonClose, homeButton, schoolScheduleButton, mapButton, todoButton, eventsButton, helpButton;
    private MaterialCardView addButton;
    private TextView welcomeText;
    private int previousHeight = 0;
    private int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        currentActivityClass = getClass();

        mAuth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.drawerLayout);
        hamburgerIcon = findViewById(R.id.nav_button);
        logoutIcon = findViewById(R.id.logoutIcon);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        addButton = findViewById(R.id.addButton);
        welcomeText = findViewById(R.id.welcomeText);
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

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddTask.class);
                startActivity(intent);
                finish();
            }
        });

        menuHandler();

        printTasks();
    }

    private void logoutUser() {
        mAuth.signOut();
        Toast.makeText(ToDoList.this, "Wylogowano pomyślnie!", Toast.LENGTH_SHORT).show();

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

        todoButton.setOnClickListener(new View.OnClickListener() {
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
        });

        eventsButton.setOnClickListener(new View.OnClickListener() {
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

    private int convertDpToPixel(float dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    private void printTasks() {
        String userID = mAuth.getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference tasksCollectionRef = db.collection("todo").document(userID).collection("tasks");

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        float density = getResources().getDisplayMetrics().density;
        int screenWidthInDp = (int) (screenWidth / density);

        tasksCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    final int[] top = {100};
                    if (querySnapshot != null) {
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                        for (DocumentSnapshot document : documents) {
                            if (welcomeText.getVisibility() == View.VISIBLE) {
                                welcomeText.setVisibility(View.GONE);
                            }
                            String documentID = document.getId();
                            Map<String, Object> data = document.getData();
                            Log.d("Firestore", "ID dokumentu: " + documentID);
                            Log.d("Firestore", "Dane dokumentu: " + data);

                            MaterialCardView taskContainer = new MaterialCardView(ToDoList.this);
                            taskContainer.setStrokeColor(getColor(R.color.secondary_background));
                            taskContainer.setCardBackgroundColor(getColor(R.color.secondary_background));
                            taskContainer.setRadius(convertDpToPixel(20, getApplicationContext()));
                            taskContainer.setStrokeWidth(convertDpToPixel(2, getApplicationContext()));
                            taskContainer.setStrokeColor(getColor(R.color.primary));

                            ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(
                                    convertDpToPixel(322, getApplicationContext()),
                                    convertDpToPixel(200, getApplicationContext())
                            );

                            marginLayoutParams.setMargins((screenWidth / 2) - (convertDpToPixel(322, getApplicationContext()) / 2), convertDpToPixel(top[0], getApplicationContext()), 0, 0);
                            taskContainer.setLayoutParams(marginLayoutParams);

                            FrameLayout parentLayout = findViewById(R.id.mainContent);

                            TextView title = new TextView(ToDoList.this);
                            title.setText(data.get("Title").toString());
                            Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.open_sans_bold);
                            title.setTypeface(typeface);
                            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                            title.setTextColor(getColor(R.color.black));
                            ViewGroup.MarginLayoutParams titleLayoutParams = new ViewGroup.MarginLayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            );
                            titleLayoutParams.setMargins(convertDpToPixel(22, getApplicationContext()), convertDpToPixel(10, getApplicationContext()), 0, 0);
                            title.setLayoutParams(titleLayoutParams);
                            title.setGravity(Gravity.TOP);

                            taskContainer.addView(title);

                            ImageView clockImage = new ImageView(ToDoList.this);
                            clockImage.setImageResource(R.drawable.clock);
                            ViewGroup.MarginLayoutParams clockLayoutParams = new ViewGroup.MarginLayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            );
                            clockLayoutParams.setMargins(convertDpToPixel(22, getApplicationContext()), convertDpToPixel(40, getApplicationContext()), 0, 0);
                            clockImage.setLayoutParams(clockLayoutParams);

                            taskContainer.addView(clockImage);

                            TextView expireDate = new TextView(ToDoList.this);
                            expireDate.setText(data.get("ExpireDate").toString());
                            expireDate.setTypeface(typeface);
                            expireDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                            expireDate.setTextColor(getColor(R.color.black));
                            ViewGroup.MarginLayoutParams dateLayoutParams = new ViewGroup.MarginLayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            );
                            dateLayoutParams.setMargins(convertDpToPixel(57, getApplicationContext()), convertDpToPixel(40, getApplicationContext()), 0, 0);
                            expireDate.setLayoutParams(dateLayoutParams);
                            expireDate.setGravity(Gravity.TOP);

                            taskContainer.addView(expireDate);

                            MaterialCardView deleteButton = new MaterialCardView(ToDoList.this);
                            deleteButton.setCardBackgroundColor(getColor(R.color.primary));
                            deleteButton.setStrokeColor(getColor(R.color.primary));
                            deleteButton.setRadius(convertDpToPixel(4, getApplicationContext()));
                            ViewGroup.MarginLayoutParams deleteButtonLayoutParams = new ViewGroup.MarginLayoutParams(
                                    convertDpToPixel(109, getApplicationContext()),
                                    convertDpToPixel(36, getApplicationContext())
                            );
                            deleteButtonLayoutParams.setMargins(convertDpToPixel(199, getApplicationContext()), convertDpToPixel(22, getApplicationContext()), 0, 0);
                            deleteButton.setLayoutParams(deleteButtonLayoutParams);
                            deleteButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DocumentReference documentRefToDelete = db.collection("todo").document(userID).collection("tasks").document(documentID);

                                    documentRefToDelete.delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d("Firestore", "Zadanie zostało pomyślnie usunięte z bazy danych.");
                                                    Intent intent = new Intent(getApplicationContext(), ToDoList.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("Firestore", "Bład podczas usuwania zadania z bazy danych: " + e.getMessage());
                                                }
                                            });
                                }
                            });

                            TextView buttonText = new TextView(ToDoList.this);
                            buttonText.setText("Zakończ");
                            buttonText.setTextColor(getColor(R.color.white));
                            buttonText.setTypeface(typeface);
                            buttonText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                            deleteButton.addView(buttonText);
                            buttonText.setGravity(Gravity.CENTER);

                            taskContainer.addView(deleteButton);

                            TextView description = new TextView(ToDoList.this);
                            description.setText(data.get("Description").toString());
                            Typeface descripionTypeface = ResourcesCompat.getFont(getApplicationContext(), R.font.open_sans);
                            description.setTypeface(descripionTypeface);
                            description.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                            description.setTextColor(getColor(R.color.black));
                            description.setPadding(0, 0, convertDpToPixel(16, getApplicationContext()), convertDpToPixel(10, getApplicationContext()));
                            ViewGroup.MarginLayoutParams descriptionLayoutParams = new ViewGroup.MarginLayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            );
                            descriptionLayoutParams.setMargins(convertDpToPixel(22, getApplicationContext()), convertDpToPixel(76, getApplicationContext()), 0, convertDpToPixel(10, getApplicationContext()));
                            description.setLayoutParams(descriptionLayoutParams);

                            taskContainer.addView(description);

                            Log.d("ToDoList", "Height: " + top[0]);

                            parentLayout.addView(taskContainer);

                            top[0] += 220;

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
}