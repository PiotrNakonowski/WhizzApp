package com.example.whizzapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.util.ArrayList;

public class SchoolSchedule extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private MaterialCardView hamburgerIcon;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ImageView logoutIcon;
    private Class<?> currentActivityClass;
    private MaterialCardView navButtonClose, homeButton, schoolScheduleButton, mapButton, todoButton, eventsButton, helpButton;
    private MaterialCardView addButton;
    private TextView welcomeText;
    private String userID;
    private int i = 0;
    private ArrayList<String> namesOfFiles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_schedule);

        currentActivityClass = getClass();
        namesOfFiles = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        drawerLayout = findViewById(R.id.drawerLayout);
        hamburgerIcon = findViewById(R.id.nav_button);
        addButton = findViewById(R.id.addButton);
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

        welcomeText = findViewById(R.id.welcomeText);

        String[] weekDays = {"monday", "tuesday", "wednesday", "thursday", "friday"};
        File userDir = new File(getFilesDir(), userID);

        if (userDir.exists() && userDir.isDirectory()) {
            File[] files = userDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        if (fileName.endsWith(".jpg")) {
                            String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
                            namesOfFiles.add(fileNameWithoutExtension);
                            Log.d("MainActivity", "Nazwa pliku: " + fileNameWithoutExtension);
                        }
                    }
                }
            }

            if (!namesOfFiles.isEmpty()) {
                ViewGroup parent = (ViewGroup) welcomeText.getParent();
                parent.removeView(welcomeText);
            }

            printImages(weekDays, namesOfFiles);
        } else {
            Log.e("MainActivity", "Katalog użytkownika nie istnieje lub nie jest katalogiem.");
        }

        menuHandler();
    }

    private int convertDpToPixel(float dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    private void printImages(String[] weekDays, ArrayList<String> namesOfFiles) {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        float density = getResources().getDisplayMetrics().density;
        int screenWidthInDp = (int) (screenWidth / density);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        File userDir = new File(getFilesDir(), userID);

        for (String day : weekDays) {
            if (namesOfFiles.contains(day)) {
                File imagePath = new File(userDir, day + ".jpg");
                if (imagePath.exists()) {
                    MaterialCardView cardView = new MaterialCardView(this);

                    cardView.setRadius(convertDpToPixel(20, this));
                    cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.secondary_background));
                    cardView.setStrokeWidth(convertDpToPixel(2, getApplicationContext()));
                    cardView.setStrokeColor(getColor(R.color.primary));

                    ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(
                            convertDpToPixel(186, this),
                            convertDpToPixel(32, this)
                    );
                    marginLayoutParams.setMargins(convertDpToPixel(20, this), convertDpToPixel(100 + (i * 181) + (i * 32), this), 0, 0);
                    cardView.setLayoutParams(marginLayoutParams);

                    TextView textView = new TextView(this);
                    textView.setTextColor(getResources().getColor(R.color.black));
                    float textSizeInSp = 16;
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeInSp);
                    ViewGroup.LayoutParams textLayoutParams = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    );
                    textView.setLayoutParams(textLayoutParams);
                    textView.setText("Plan na " + convertDayToPolish(day));
                    textView.setGravity(Gravity.CENTER);
                    Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.open_sans_bold);
                    textView.setTypeface(typeface);

                    cardView.addView(textView);

                    ViewGroup parentLayout = findViewById(R.id.mainContent);
                    parentLayout.addView(cardView);

                    MaterialCardView imageCardView = new MaterialCardView(this);

                    imageCardView.setRadius(convertDpToPixel(20, this));
                    imageCardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.secondary_background));
                    imageCardView.setStrokeWidth(convertDpToPixel(2, getApplicationContext()));
                    imageCardView.setStrokeColor(getColor(R.color.primary));
                    ViewGroup.MarginLayoutParams secondMarginLayoutParams = new ViewGroup.MarginLayoutParams(
                            convertDpToPixel(screenWidthInDp - 40, this),
                            convertDpToPixel(159, this)
                    );
                    secondMarginLayoutParams.setMargins(convertDpToPixel(20, this), convertDpToPixel(143 + (i * 54 + (i * 159)), this), 0, convertDpToPixel(10, getApplicationContext()));
                    imageCardView.setLayoutParams(secondMarginLayoutParams);

                    ImageView scheduleImage = new ImageView(this);
                    ViewGroup.LayoutParams imageLayoutParams = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    );
                    scheduleImage.setLayoutParams(imageLayoutParams);
                    scheduleImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    ImageView biggerScheduleImage = new ImageView(this);
                    ViewGroup.LayoutParams biggerLayoutParams = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    biggerScheduleImage.setLayoutParams(biggerLayoutParams);
                    biggerScheduleImage.setVisibility(View.GONE);

                    try {
                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath.getAbsolutePath());
                        scheduleImage.setImageBitmap(bitmap);
                        biggerScheduleImage.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        Log.e("SchoolSchedule", "Błąd podczas pobierania zdjęcia z lokalnej pamięci.", e);
                    }

                    imageCardView.addView(scheduleImage);
                    ((FrameLayout.LayoutParams) scheduleImage.getLayoutParams()).gravity = Gravity.CENTER;
                    parentLayout.addView(imageCardView);
                    parentLayout.addView(biggerScheduleImage);

                    scheduleImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SchoolSchedule.this);
                            LayoutInflater inflater = getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.activity_bigger_image, null);
                            ImageView expandedImageView = dialogView.findViewById(R.id.expandedImageView);
                            BitmapDrawable drawable = (BitmapDrawable) scheduleImage.getDrawable();
                            expandedImageView.setImageBitmap(drawable.getBitmap());
                            builder.setView(dialogView);

                            builder.setPositiveButton("Zamknij", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog dialog = builder.create();

                            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialogInterface) {
                                    Button button = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                                    button.setBackgroundColor(getResources().getColor(R.color.secondary_background));
                                    button.setTextColor(getResources().getColor(R.color.primary));
                                    Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.open_sans_bold);
                                    button.setTypeface(typeface);
                                }
                            });

                            dialog.show();
                        }
                    });

                    i++;
                }
            }
        }
    }

    private String convertDayToPolish(String day) {
        switch (day) {
            case "monday":
                return "poniedziałek";
            case "tuesday":
                return "wtorek";
            case "wednesday":
                return "środę";
            case "thursday":
                return "czwartek";
            case "friday":
                return "piątek";
        }
        return null;
    }

    private void logoutUser() {
        mAuth.signOut();
        Toast.makeText(SchoolSchedule.this, "Wylogowano pomyślnie!", Toast.LENGTH_SHORT).show();

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

        mapButton.setOnClickListener(new View.OnClickListener() {
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

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddSchoolSchedule.class);
                startActivity(intent);
                finish();
            }
        });

        logoutIcon.setOnClickListener(v -> logoutUser());
    }
}