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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Events extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private MaterialCardView hamburgerIcon;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ImageView logoutIcon;
    private Class<?> currentActivityClass;
    private TextView welcomeText;
    private MaterialCardView addButton;
    private int i = 0;
    private MaterialCardView navButtonClose, homeButton, schoolScheduleButton, mapButton, todoButton, eventsButton, helpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        currentActivityClass = getClass();

        mAuth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.drawerLayout);
        hamburgerIcon = findViewById(R.id.nav_button);
        logoutIcon = findViewById(R.id.logoutIcon);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        addButton = findViewById(R.id.addButton);
        welcomeText = findViewById(R.id.welcomeText);
        todoButton = findViewById(R.id.todoButton);

        //MENU ITEMS//
        navButtonClose = findViewById(R.id.navButtonClose);
        homeButton = findViewById(R.id.homeButton);
        schoolScheduleButton = findViewById(R.id.schoolScheduleButton);
        mapButton = findViewById(R.id.mapButton);
        todoButton = findViewById(R.id.todoButton);
        eventsButton = findViewById(R.id.eventsButton);
        helpButton = findViewById(R.id.helpButton);
        //MENU ITEMS//

        printEvents();
        menuHandler();
    }

    private void logoutUser() {
        mAuth.signOut();
        Toast.makeText(Events.this, "Wylogowano pomyślnie!", Toast.LENGTH_SHORT).show();

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
                } else {
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

        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentActivityClass != Events.class) {
                    Intent intent = new Intent(getApplicationContext(), Events.class);
                    startActivity(intent);
                    finish();
                } else {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });

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

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentActivityClass != AddEvent.class) {
                    Intent intent = new Intent(getApplicationContext(), AddEvent.class);
                    startActivity(intent);
                    finish();
                } else {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });


        logoutIcon.setOnClickListener(v -> logoutUser());
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
                .whereEqualTo("Approved",true)
                .orderBy("CreatedAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
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


                            //*******Event Container*******
                            MaterialCardView eventContainer = new MaterialCardView(Events.this);
                            eventContainer.setStrokeColor(getColor(R.color.secondary_background));
                            eventContainer.setCardBackgroundColor(getColor(R.color.secondary_background));
                            eventContainer.setRadius(convertDpToPixel(20, getApplicationContext()));
                            eventContainer.setStrokeWidth(convertDpToPixel(2, getApplicationContext()));
                            eventContainer.setStrokeColor(getColor(R.color.primary));

                            ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(convertDpToPixel(322, getApplicationContext()), convertDpToPixel(178, getApplicationContext()));

                            marginLayoutParams.setMargins((screenWidth / 2) - (convertDpToPixel(322, getApplicationContext()) / 2), convertDpToPixel(100 + (i * 178) + (i * 20), getApplicationContext()), 0, (i * 20));
                            eventContainer.setLayoutParams(marginLayoutParams);

                            FrameLayout parentLayout = findViewById(R.id.mainContent);
                            //*******Event Container*******

                            //*******Title*******
                            TextView title = new TextView(Events.this);
                            title.setText(data.get("Title").toString());
                            Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.open_sans_bold);
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
                            TextView description = new TextView(Events.this);
                            description.setText(data.get("Description").toString());
                            Typeface descripionTypeface = ResourcesCompat.getFont(getApplicationContext(), R.font.open_sans);
                            description.setTypeface(descripionTypeface);
                            description.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
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

                            //*******Author*******
                            String authorText = "Opublikował: " + data.get("Name").toString() + " " + data.get("Surname").toString();
                            TextView AuthorTextField = new TextView(Events.this);
                            AuthorTextField.setText(authorText);
                            Typeface AuthorTypeface = ResourcesCompat.getFont(getApplicationContext(), R.font.open_sans);
                            AuthorTextField.setTypeface(AuthorTypeface);
                            AuthorTextField.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                            AuthorTextField.setTextColor(getColor(R.color.black));
                            convertDpToPixel(ViewGroup.LayoutParams.WRAP_CONTENT, getApplicationContext());

                            ViewGroup.MarginLayoutParams AuthorLayoutParams = new ViewGroup.MarginLayoutParams(convertDpToPixel(ViewGroup.LayoutParams.WRAP_CONTENT, getApplicationContext()), convertDpToPixel(ViewGroup.LayoutParams.WRAP_CONTENT, getApplicationContext()));
                            AuthorLayoutParams.setMargins(convertDpToPixel(20, getApplicationContext()), convertDpToPixel(110, getApplicationContext()), 0, 0);
                            AuthorTextField.setLayoutParams(AuthorLayoutParams);

                            eventContainer.addView(AuthorTextField);
                            //*******Author*******


                            //*******Photo*******
                            MaterialCardView imageHolder = new MaterialCardView(Events.this);
                            ViewGroup.MarginLayoutParams photoViewParams = new ViewGroup.MarginLayoutParams(convertDpToPixel(90, getApplicationContext()), convertDpToPixel(87, getApplicationContext()));
                            photoViewParams.setMargins(convertDpToPixel(210, getApplicationContext()), convertDpToPixel(25, getApplicationContext()), 0, 0);
                            imageHolder.setRadius(convertDpToPixel(20,getApplicationContext()));
                            imageHolder.setStrokeWidth(0);
                            imageHolder.setLayoutParams(photoViewParams);


                            ImageView photoView = new ImageView(Events.this);
                            photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            imageHolder.addView(photoView);

                            Picasso.get().load(data.get("PhotoUrl").toString()).into(photoView);


                            eventContainer.addView(imageHolder);
                            //*******Photo*******

                            //*******Attendance Counter*******
                            MaterialCardView attendanceCounter = new MaterialCardView(Events.this);
                            attendanceCounter.setCardBackgroundColor(Color.parseColor("#8B0D02"));
                            attendanceCounter.setStrokeColor(getColor(R.color.primary));
                            attendanceCounter.setRadius(convertDpToPixel(4, getApplicationContext()));
                            ViewGroup.MarginLayoutParams attendanceCounterLayoutParams = new ViewGroup.MarginLayoutParams(convertDpToPixel(180, getApplicationContext()), convertDpToPixel(30, getApplicationContext()));
                            attendanceCounterLayoutParams.setMargins(convertDpToPixel(120, getApplicationContext()), convertDpToPixel(130, getApplicationContext()), 0, 0);
                            attendanceCounter.setLayoutParams(attendanceCounterLayoutParams);

                            TextView attendanceCounterButtonText = new TextView(Events.this);
                            String attendanceText = "Liczba chętnych: " + data.get("Attendance").toString() + "/" + data.get("MaxAttendance").toString();
                            attendanceCounterButtonText.setText(attendanceText);
                            attendanceCounterButtonText.setTextColor(getColor(R.color.white));
                            attendanceCounterButtonText.setTypeface(typeface);
                            attendanceCounterButtonText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                            attendanceCounter.addView(attendanceCounterButtonText);
                            attendanceCounterButtonText.setGravity(Gravity.CENTER);

                            eventContainer.addView(attendanceCounter);
                            //*******Attendance Counter*******

                            DocumentReference docRef = document.getReference();
                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            CollectionReference participantsRef = docRef.collection("Participants");

                            //*******joinButton*******
                            MaterialCardView joinButton = new MaterialCardView(Events.this);

                            TextView buttonText = new TextView(Events.this);

                            participantsRef.whereEqualTo("UserId", currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (!task.getResult().isEmpty()) {
                                            joinButton.setCardBackgroundColor(getColor(R.color.third_background));
                                            buttonText.setTextColor(getColor(R.color.black));
                                            String buttonDescriptionText = "Dołączono";
                                            buttonText.setText(buttonDescriptionText);
                                        }else{
                                            joinButton.setCardBackgroundColor(getColor(R.color.primary));
                                            buttonText.setTextColor(getColor(R.color.white));
                                            String buttonDescriptionText = "Zapisz się";
                                            buttonText.setText(buttonDescriptionText);
                                        }
                                    }
                                }
                            });

                            buttonText.setTypeface(typeface);
                            buttonText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                            joinButton.addView(buttonText);
                            buttonText.setGravity(Gravity.CENTER);

                            joinButton.setRadius(convertDpToPixel(4, getApplicationContext()));
                            ViewGroup.MarginLayoutParams joinButtonLayoutParams = new ViewGroup.MarginLayoutParams(convertDpToPixel(90, getApplicationContext()), convertDpToPixel(30, getApplicationContext()));
                            joinButtonLayoutParams.setMargins(convertDpToPixel(20, getApplicationContext()), convertDpToPixel(130, getApplicationContext()), 0, 0);
                            joinButton.setLayoutParams(joinButtonLayoutParams);
                            joinButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    participantsRef.whereEqualTo("UserId", currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                if (!task.getResult().isEmpty()) {
                                                    Log.d("Firestore", "User is already enlisted to this event");
                                                    Toast.makeText(Events.this, "Użytkownik już jest uczestnikiem wydarzenia!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Map<String, Object> signedEventData = new HashMap<>();
                                                    signedEventData.put("UserId", currentUser.getUid());

                                                    participantsRef.add(signedEventData);

                                                    Log.d("Firestore", "User enlisted to event");

                                                    long eventAttendanceCount;

                                                    try {
                                                        eventAttendanceCount = Long.parseLong(data.get("Attendance").toString());
                                                    } catch (NumberFormatException e) {
                                                        Log.e("JoinButton", "Invalid attendance count format", e);
                                                        Toast.makeText(Events.this, "Invalid attendance count format", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }

                                                    if (eventAttendanceCount >= Long.parseLong(data.get("MaxAttendance").toString())) {
                                                        Toast.makeText(Events.this, "Wydarzenie jest pełne", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        eventAttendanceCount++;
                                                        data.put("Attendance", String.valueOf(eventAttendanceCount));

                                                        docRef.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                String attendanceText = "Liczba chętnych: " + data.get("Attendance").toString() + "/" + data.get("MaxAttendance").toString();
                                                                attendanceCounterButtonText.setText(attendanceText);
                                                                joinButton.setCardBackgroundColor(getColor(R.color.third_background));
                                                                buttonText.setTextColor(getColor(R.color.black));
                                                                String buttonDescriptionText = "Dołączono";
                                                                buttonText.setText(buttonDescriptionText);
                                                                Toast.makeText(Events.this, "Dołączono do wydarzenia!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.e("JoinButton", "Error updating attendance count", e);
                                                                Toast.makeText(Events.this, "Nie udało dołączyć się do wydarzenia. Spróbuj ponownie", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        }

                                    });
                                }
                            });



                            eventContainer.addView(joinButton);
                            //*******joinButton*******


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
}