package com.example.whizzapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
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

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public class Homepage extends AppCompatActivity {
    TextView textViewWelcome;
    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private MaterialCardView hamburgerIcon;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ImageView logoutIcon;
    private View loadingScreen;
    private static final long SPLASH_DELAY = 2000;
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
        loadingScreen = findViewById(R.id.loadingScreen);
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

        printSchedule();
        printEvents();
        printTasks();
        menuHandler();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingScreen.setVisibility(View.GONE);
                drawerLayout.setVisibility(View.VISIBLE);
            }
        }, SPLASH_DELAY);
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

    private String convertDayOfWeekToString(DayOfWeek day) {
        if (day == DayOfWeek.MONDAY) {
            return "poniedziałek";
        }
        if (day == DayOfWeek.TUESDAY) {
            return "wtorek";
        }
        if (day == DayOfWeek.WEDNESDAY) {
            return "środa";
        }
        if (day == DayOfWeek.THURSDAY) {
            return "czwartek";
        }
        if (day == DayOfWeek.FRIDAY) {
            return "piątek";
        }
        if (day == DayOfWeek.SATURDAY) {
            return "sobota";
        }
        if (day == DayOfWeek.SUNDAY) {
            return "niedziela";
        }
        return null;
    }

    int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        float density = getResources().getDisplayMetrics().density;
        return (int) (screenWidth / density);
    }

    private boolean isImageAvailable(String dayOfWeek) {
        String userID = mAuth.getCurrentUser().getUid();
        File userDir = new File(getFilesDir(), userID);
        if (!userDir.exists()) {
            return false;
        }

        File imageFile = new File(userDir, dayOfWeek + ".jpg");
        return imageFile.exists();
    }

    private MaterialCardView createTaskHolder() {
        MaterialCardView taskContainer = new MaterialCardView(this);
        taskContainer.setRadius(convertDpToPixel(20, getApplicationContext()));
        taskContainer.setStrokeWidth(convertDpToPixel(2, getApplicationContext()));
        taskContainer.setCardBackgroundColor(getColor(R.color.primary));
        taskContainer.setStrokeColor(getColor(R.color.primary));
        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(
                convertDpToPixel(getScreenWidth() - 90, getApplicationContext()),
                convertDpToPixel(28, getApplicationContext())
        );
        marginLayoutParams.setMargins(convertDpToPixel(20, getApplicationContext()), convertDpToPixel(18, getApplicationContext()), 0, 0);
        taskContainer.setLayoutParams(marginLayoutParams);
        taskContainer.setVisibility(View.GONE);
        return taskContainer;
    }

    private void printSchedule() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        FrameLayout parentLayout = findViewById(R.id.mainContent);
        String userID = mAuth.getCurrentUser().getUid();

        MaterialCardView nameContainer = new MaterialCardView(Homepage.this);
        nameContainer.setStrokeColor(getColor(R.color.primary));
        nameContainer.setCardBackgroundColor(getColor(R.color.secondary_background));
        nameContainer.setRadius(convertDpToPixel(20, getApplicationContext()));
        nameContainer.setStrokeWidth(convertDpToPixel(2, getApplicationContext()));

        ViewGroup.MarginLayoutParams nameContainerLayout = new ViewGroup.MarginLayoutParams(
                convertDpToPixel(200, getApplicationContext()),
                convertDpToPixel(33, getApplicationContext())
        );

        nameContainerLayout.setMargins(convertDpToPixel(24, getApplicationContext()), convertDpToPixel(110, getApplicationContext()), 0, 0);
        nameContainer.setLayoutParams(nameContainerLayout);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        linearLayout.setLayoutParams(linearLayoutParams);

        TextView scheduleName = new TextView(Homepage.this);
        scheduleName.setTextColor(getColor(R.color.black));
        FrameLayout.LayoutParams textLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        textLayoutParams.gravity = Gravity.CENTER;
        scheduleName.setLayoutParams(textLayoutParams);
        scheduleName.setGravity(Gravity.CENTER);
        scheduleName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.open_sans);
        scheduleName.setTypeface(typeface);
        scheduleName.setText("Plan lekcji - ");

        linearLayout.addView(scheduleName);

        TextView dayOfSchedule = new TextView(Homepage.this);
        dayOfSchedule.setTextColor(getColor(R.color.black));
        dayOfSchedule.setLayoutParams(textLayoutParams);
        dayOfSchedule.setGravity(Gravity.CENTER);
        nameContainer.setStrokeColor(getColor(R.color.primary));
        dayOfSchedule.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        Typeface bold = ResourcesCompat.getFont(getApplicationContext(), R.font.open_sans_bold);
        dayOfSchedule.setTypeface(bold);
        dayOfSchedule.setText(convertDayOfWeekToString(dayOfWeek));

        linearLayout.addView(dayOfSchedule);

        nameContainer.addView(linearLayout);

        parentLayout.addView(nameContainer);

        MaterialCardView scheduleContainer = new MaterialCardView(this);
        scheduleContainer.setStrokeColor(getColor(R.color.primary));
        scheduleContainer.setCardBackgroundColor(getColor(R.color.secondary_background));
        scheduleContainer.setRadius(convertDpToPixel(20, getApplicationContext()));
        scheduleContainer.setStrokeWidth(convertDpToPixel(2, getApplicationContext()));

        ViewGroup.MarginLayoutParams scheduleContainerLayout = new ViewGroup.MarginLayoutParams(
                convertDpToPixel(getScreenWidth() - 48, getApplicationContext()),
                convertDpToPixel(159, getApplicationContext())
        );
        scheduleContainerLayout.setMargins(convertDpToPixel(24, getApplicationContext()), convertDpToPixel(162, getApplicationContext()), 0, 0);
        scheduleContainer.setLayoutParams(scheduleContainerLayout);

        if (isImageAvailable(dayOfWeek.name().toLowerCase())) {

            MaterialCardView secondImageHolder = new MaterialCardView(this);
            secondImageHolder.setRadius(convertDpToPixel(20, getApplicationContext()));
            secondImageHolder.setStrokeWidth(0);
            ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                    convertDpToPixel(getScreenWidth() - 48, getApplicationContext()),
                    convertDpToPixel(159, getApplicationContext())
            );
            secondImageHolder.setLayoutParams(layoutParams);

            ImageView scheduleImage = new ImageView(this);

            scheduleImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

            ImageView biggerScheduleImage = new ImageView(this);
            ViewGroup.LayoutParams biggerLayoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            biggerScheduleImage.setLayoutParams(biggerLayoutParams);
            biggerScheduleImage.setVisibility(View.GONE);

            File userDir = new File(getFilesDir(), userID);
            File imageFile = new File(userDir, dayOfWeek.name().toLowerCase() + ".jpg");
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

            scheduleImage.setImageBitmap(bitmap);
            biggerScheduleImage.setImageBitmap(bitmap);

            secondImageHolder.addView(scheduleImage);

            scheduleContainer.addView(secondImageHolder);
            ((FrameLayout.LayoutParams) secondImageHolder.getLayoutParams()).gravity = Gravity.CENTER;
            parentLayout.addView(scheduleContainer);
            parentLayout.addView(biggerScheduleImage);

            scheduleImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Homepage.this);
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
        } else {
            LinearLayout linearLayoutError = new LinearLayout(this);
            linearLayoutError.setOrientation(LinearLayout.HORIZONTAL);
            linearLayoutError.setGravity(Gravity.CENTER);

            linearLayoutError.setLayoutParams(linearLayoutParams);
            TextView errorText = new TextView(Homepage.this);
            errorText.setTextColor(getColor(R.color.black));
            errorText.setLayoutParams(textLayoutParams);
            errorText.setGravity(Gravity.CENTER);
            errorText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            errorText.setTypeface(bold);
            errorText.setText("Brak zdjęcia planu lekcji!");
            linearLayoutError.addView(errorText);
            scheduleContainer.addView(linearLayoutError);
            parentLayout.addView(scheduleContainer);
        }
    }

    private void printEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference eventsCollectionRef = db.collection("events");


        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);


        eventsCollectionRef
                .whereEqualTo("Approved",true)
                .orderBy("CreatedAt", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.open_sans_bold);

                        FrameLayout parentLayout = findViewById(R.id.mainContent);

                        //*******Section Title*******
                        ViewGroup.MarginLayoutParams sectionTitleParams = getSectionTitleParams();
                        TextView sectionTitle = getSectionTitleTextView(typeface);

                        MaterialCardView sectionTitleContainer = getSectionTitleContainer(sectionTitleParams, sectionTitle);

                        parentLayout.addView(sectionTitleContainer);
                        //*******Section Title*******

                        //*******Event Container*******
                        MaterialCardView eventContainer = getMaterialCardView();
                        ViewGroup.MarginLayoutParams marginLayoutParams = getEventContainerLayoutParams();

                        eventContainer.setLayoutParams(marginLayoutParams);
                        //*******Event Container*******
                        if(querySnapshot.isEmpty()){
                            FrameLayout.LayoutParams textLayoutParams = getTextLayoutParams();

                            LinearLayout linearLayoutError = new LinearLayout(Homepage.this);
                            linearLayoutError.setOrientation(LinearLayout.HORIZONTAL);
                            linearLayoutError.setGravity(Gravity.CENTER);

                            TextView errorText = getErrorText(textLayoutParams, typeface);
                            String alert = "Brak nadchodzących wydarzeń!";
                            errorText.setText(alert);
                            linearLayoutError.addView(errorText);
                            eventContainer.addView(linearLayoutError);

                            parentLayout.addView(eventContainer);
                            Log.d("Firestore", "Brak dokumentów w kolekcji events.");
                            return;
                        }

                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                        String documentID = document.getId();
                        Map<String, Object> data = document.getData();
                        Log.d("Firestore", "ID dokumentu: " + documentID);
                        Log.d("Firestore", "Dane dokumentu: " + data);


                        //*******Title*******
                        TextView title = getTitleTextView(data, typeface);

                        eventContainer.addView(title);
                        //*******Title*******

                        //*******Description*******
                        Typeface descripionTypeface = ResourcesCompat.getFont(getApplicationContext(), R.font.open_sans);

                        ViewGroup.MarginLayoutParams eventDescriptionLayoutParams = getEventDescriptionLayoutParams();
                        String eventDescriptionContent = data.get("Description").toString();
                        TextView eventDescription = getTextView(eventDescriptionContent, descripionTypeface, 10);
                        eventDescription.setLayoutParams(eventDescriptionLayoutParams);

                        eventContainer.addView(eventDescription);
                        //*******Description*******

                        //*******Author*******
                        String authorText = "Opublikował: " + data.get("Name").toString() + " " + data.get("Surname").toString();
                        Typeface AuthorTypeface = ResourcesCompat.getFont(getApplicationContext(), R.font.open_sans);
                        TextView AuthorTextField = getTextView(authorText, AuthorTypeface, 10);

                        ViewGroup.MarginLayoutParams AuthorLayoutParams = new ViewGroup.MarginLayoutParams(
                                convertDpToPixel(ViewGroup.LayoutParams.WRAP_CONTENT, getApplicationContext()),
                                convertDpToPixel(ViewGroup.LayoutParams.WRAP_CONTENT, getApplicationContext())
                        );
                        AuthorLayoutParams.setMargins(convertDpToPixel(22, getApplicationContext()), convertDpToPixel(127, getApplicationContext()), 0, 0);

                        AuthorTextField.setLayoutParams(AuthorLayoutParams);
                        eventContainer.addView(AuthorTextField);
                        //*******Author*******

                        //*******Photo*******
                        MaterialCardView imageHolder = getImageHolder();

                        ImageView photoView = new ImageView(Homepage.this);
                        photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageHolder.addView(photoView);

                        Picasso.get()
                                .load(data.get("PhotoUrl").toString())
                                .resize(800,600)
                                .into(photoView);

                        eventContainer.addView(imageHolder);
                        ((FrameLayout.LayoutParams) imageHolder.getLayoutParams()).gravity = Gravity.END;
                        //*******Photo*******


                        parentLayout.addView(eventContainer);

                    } else {
                        Log.d("Firestore", "Brak dokumentów w kolekcji.");
                    }
                } else {
                    Log.e("Firestore", "Błąd podczas pobierania dokumentów: " + task.getException().getMessage());
                }
            }

        });
    }

    /**
     * Prepares TextView with parameters
     *
     * @param text text to be set into TextView
     * @param typeface typeface to be set into TextView
     * @param size font size to be set into TextView
     * @return TextView with text, font and font-size set
     */
    @NonNull
    private TextView getTextView(String text, Typeface typeface, int size) {
        TextView textView = new TextView(Homepage.this);
        textView.setText(text);
        textView.setTypeface(typeface);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        textView.setTextColor(getColor(R.color.black));
        return textView;
    }

    /**
     * Prepares MaterialCardView for image
     * @return prepared MaterialCardView with parameters
     */
    @NonNull
    private MaterialCardView getImageHolder() {

        MaterialCardView imageHolder = new MaterialCardView(Homepage.this);
        ViewGroup.MarginLayoutParams photoViewParams = new ViewGroup.MarginLayoutParams(convertDpToPixel(100, getApplicationContext()), convertDpToPixel(100, getApplicationContext()));
        photoViewParams.setMargins(0, convertDpToPixel(30, getApplicationContext()), convertDpToPixel(22, getApplicationContext()), 0);
        imageHolder.setRadius(convertDpToPixel(20,getApplicationContext()));
        imageHolder.setStrokeWidth(0);
        imageHolder.setLayoutParams(photoViewParams);
        return imageHolder;
    }

    /**
     * Prepares parameters for EventDescription in Events section
     * @return Layout parameters for EventDescription
     */
    @NonNull
    private ViewGroup.MarginLayoutParams getEventDescriptionLayoutParams() {
        int specificWidthInDp = 150;
        int specificWidthInPx = convertDpToPixel(specificWidthInDp, getApplicationContext());
        int specificHeightInDp = 80;
        int specificHeightInPx = convertDpToPixel(specificHeightInDp, getApplicationContext());

        ViewGroup.MarginLayoutParams descriptionLayoutParams = new ViewGroup.MarginLayoutParams(specificWidthInPx, specificHeightInPx);
        descriptionLayoutParams.setMargins(convertDpToPixel(22, getApplicationContext()), convertDpToPixel(45, getApplicationContext()), 0, 0);
        return descriptionLayoutParams;
    }


    /**
     * Prepares Layout parameters for Text
     * @return prepared layout parameters
     */
    @NonNull
    private static FrameLayout.LayoutParams getTextLayoutParams() {
        FrameLayout.LayoutParams textLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        textLayoutParams.gravity = Gravity.CENTER;
        return textLayoutParams;
    }

    /**
     * Prepares error TextView with parameters
     * @param textLayoutParams parameters to be set into TextView
     * @param typeface typeface to be used in errorText
     * @return prepared TextView with parameters
     */
    @NonNull
    private TextView getErrorText(FrameLayout.LayoutParams textLayoutParams, Typeface typeface) {
        TextView errorText = new TextView(Homepage.this);
        errorText.setTextColor(getColor(R.color.black));
        errorText.setLayoutParams(textLayoutParams);
        errorText.setGravity(Gravity.CENTER);
        errorText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        errorText.setTypeface(typeface);
        return errorText;
    }

    /**
     * Prepares title TextView for title in events section
     * @param data data from events collection document
     * @param typeface typeface to be used
     * @return prepared titleTextView
     */
    @NonNull
    private TextView getTitleTextView(Map<String, Object> data, Typeface typeface) {
        String title = data.get("Title").toString();
        TextView titleTextView = getTextView(title, typeface, 18);
        ViewGroup.MarginLayoutParams titleLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleLayoutParams.setMargins(convertDpToPixel(22, getApplicationContext()), convertDpToPixel(12, getApplicationContext()), 0, 0);
        titleTextView.setLayoutParams(titleLayoutParams);
        titleTextView.setGravity(Gravity.TOP);
        return titleTextView;
    }

    /**
     * Prepares layout params for events MaterialCardView section
     * @return prepared layout params
     */
    @NonNull
    private ViewGroup.MarginLayoutParams getEventContainerLayoutParams() {
        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(
                convertDpToPixel(getScreenWidth() - 48, getApplicationContext()),
                convertDpToPixel(159, getApplicationContext())
        );
        marginLayoutParams.setMargins(convertDpToPixel(24, getApplicationContext()), convertDpToPixel(400, getApplicationContext()), 0, 0);
        return marginLayoutParams;
    }

    /**
     * Sets parameters to MaterialCardView
     * @return prepared MaterialCardView with parameters
     */
    @NonNull
    private MaterialCardView getMaterialCardView() {
        MaterialCardView materialCardView = new MaterialCardView(Homepage.this);
        materialCardView.setCardBackgroundColor(getColor(R.color.secondary_background));
        materialCardView.setRadius(convertDpToPixel(20, getApplicationContext()));
        materialCardView.setStrokeWidth(convertDpToPixel(2, getApplicationContext()));
        materialCardView.setStrokeColor(getColor(R.color.primary));
        return materialCardView;
    }

    /**
     * prepares parameters for titleSection
     * @return prepared layout parameters with parameters
     */
    @NonNull
    private ViewGroup.MarginLayoutParams getSectionTitleParams() {
        ViewGroup.MarginLayoutParams sectionTitleParams = new ViewGroup.MarginLayoutParams(
                convertDpToPixel(100, getApplicationContext()),
                convertDpToPixel(33, getApplicationContext())
        );
        sectionTitleParams.setMargins(convertDpToPixel(24, getApplicationContext()), convertDpToPixel(348, getApplicationContext()), 24, 0);
        return sectionTitleParams;
    }

    /**
     * Prepares container for section title
     * @param sectionTitleParams layout parameters
     * @param sectionTitle text to be set into MaterialCardView
     * @return prepared MaterialCardView
     */
    @NonNull
    private MaterialCardView getSectionTitleContainer(ViewGroup.MarginLayoutParams sectionTitleParams, TextView sectionTitle) {
        MaterialCardView sectionTitleContainer = getMaterialCardView();
        sectionTitleContainer.setLayoutParams(sectionTitleParams);
        sectionTitleContainer.addView(sectionTitle);
        return sectionTitleContainer;
    }

    /**
     * Prepares TextView for event section title
     * @param typeface to be used
     * @return prepared TextView
     */
    @NonNull
    private TextView getSectionTitleTextView(Typeface typeface) {
        TextView sectionTitle = new TextView(Homepage.this);
        sectionTitle.setText("Wydarzenia");
        sectionTitle.setGravity(Gravity.CENTER);
        sectionTitle.setTypeface(typeface);
        sectionTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        sectionTitle.setTextColor(getColor(R.color.black));
        return sectionTitle;
    }

    private void printTasks() {
        FrameLayout parentLayout = findViewById(R.id.mainContent);
        String userID = mAuth.getCurrentUser().getUid();

        MaterialCardView nameContainer = new MaterialCardView(Homepage.this);
        nameContainer.setStrokeColor(getColor(R.color.secondary_background));
        nameContainer.setCardBackgroundColor(getColor(R.color.secondary_background));
        nameContainer.setRadius(convertDpToPixel(20, getApplicationContext()));
        nameContainer.setStrokeWidth(convertDpToPixel(2, getApplicationContext()));
        nameContainer.setStrokeColor(getColor(R.color.primary));

        ViewGroup.MarginLayoutParams nameContainerLayout = new ViewGroup.MarginLayoutParams(
                convertDpToPixel(100, getApplicationContext()),
                convertDpToPixel(33, getApplicationContext())
        );

        nameContainerLayout.setMargins(convertDpToPixel(24, getApplicationContext()), convertDpToPixel(587, getApplicationContext()), 0, 0);
        nameContainer.setLayoutParams(nameContainerLayout);

        TextView toDoText = new TextView(Homepage.this);
        toDoText.setTextColor(getColor(R.color.black));
        FrameLayout.LayoutParams textLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        textLayoutParams.gravity = Gravity.CENTER;
        toDoText.setLayoutParams(textLayoutParams);
        toDoText.setGravity(Gravity.CENTER);
        toDoText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.open_sans_bold);
        toDoText.setTypeface(typeface);
        toDoText.setText("Lista TO DO");

        nameContainer.addView(toDoText);
        parentLayout.addView(nameContainer);

        MaterialCardView tasksContainer = new MaterialCardView(this);
        tasksContainer.setStrokeColor(getColor(R.color.secondary_background));
        tasksContainer.setCardBackgroundColor(getColor(R.color.secondary_background));
        tasksContainer.setRadius(convertDpToPixel(20, getApplicationContext()));
        tasksContainer.setStrokeWidth(convertDpToPixel(2, getApplicationContext()));
        tasksContainer.setStrokeColor(getColor(R.color.primary));

        ViewGroup.MarginLayoutParams tasksContainerLayout = new ViewGroup.MarginLayoutParams(
                    convertDpToPixel(getScreenWidth() - 48, getApplicationContext()),
                    convertDpToPixel(200, getApplicationContext())
        );
        tasksContainerLayout.setMargins(convertDpToPixel(24, getApplicationContext()), convertDpToPixel(638, getApplicationContext()), 0, convertDpToPixel(10, getApplicationContext()));
        tasksContainer.setLayoutParams(tasksContainerLayout);

        LinearLayout linearLayoutError = new LinearLayout(this);
        linearLayoutError.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutError.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        linearLayoutError.setLayoutParams(linearLayoutParams);

        LinearLayout linearLayoutTasks = new LinearLayout(this);
        linearLayoutTasks.setOrientation(LinearLayout.VERTICAL);
        linearLayoutTasks.setGravity(Gravity.TOP);
        linearLayoutTasks.setLayoutParams(linearLayoutParams);
        linearLayoutTasks.setVisibility(View.GONE);

        TextView errorText = new TextView(Homepage.this);
        errorText.setTextColor(getColor(R.color.black));
        errorText.setLayoutParams(textLayoutParams);
        errorText.setGravity(Gravity.CENTER);
        errorText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        Typeface bold = ResourcesCompat.getFont(getApplicationContext(), R.font.open_sans_bold);
        errorText.setTypeface(bold);
        errorText.setText("Nie masz żadnych zadań.");
        linearLayoutError.addView(errorText);

        MaterialCardView taskContainer0 = createTaskHolder();
        MaterialCardView taskContainer1 = createTaskHolder();
        MaterialCardView taskContainer2 = createTaskHolder();
        MaterialCardView taskContainer3 = createTaskHolder();

        linearLayoutTasks.addView(taskContainer0);
        linearLayoutTasks.addView(taskContainer1);
        linearLayoutTasks.addView(taskContainer2);
        linearLayoutTasks.addView(taskContainer3);

        tasksContainer.addView(linearLayoutError);
        tasksContainer.addView(linearLayoutTasks);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference tasksCollectionRef = db.collection("todo").document(userID).collection("tasks");

        final int[] i = {0}; //Licznik na ilość pobranych tasków

        tasksCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                        for (DocumentSnapshot document : documents) {
                            Map<String, Object> data = document.getData();

                            TextView title = new TextView(Homepage.this);
                            title.setText(data.get("Title").toString());
                            Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.open_sans_bold);
                            title.setTypeface(typeface);
                            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                            title.setTextColor(getColor(R.color.white));
                            ViewGroup.MarginLayoutParams titleLayoutParams = new ViewGroup.MarginLayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            );
                            titleLayoutParams.setMargins(convertDpToPixel(12, getApplicationContext()), convertDpToPixel(5, getApplicationContext()), 0, 0);
                            title.setLayoutParams(titleLayoutParams);

                            if (i[0] == 0) {
                                taskContainer0.addView(title);
                                taskContainer0.setVisibility(View.VISIBLE);
                            }
                            if (i[0] == 1) {
                                taskContainer1.addView(title);
                                taskContainer1.setVisibility(View.VISIBLE);
                            }
                            if (i[0] == 2) {
                                taskContainer2.addView(title);
                                taskContainer2.setVisibility(View.VISIBLE);
                            }
                            if (i[0] == 3) {
                                taskContainer3.addView(title);
                                taskContainer3.setVisibility(View.VISIBLE);
                            }

                            i[0]++;

                            if (i[0] == 4) {
                                break;
                            }
                        }
                    }
                }
                if (i[0] > 0) {
                    errorText.setVisibility(View.GONE);
                    linearLayoutError.setVisibility(View.GONE);
                    linearLayoutTasks.setVisibility(View.VISIBLE);
                }
                parentLayout.addView(tasksContainer);
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

        eventsButton.setOnClickListener(v -> {
            if (currentActivityClass != Events.class) {
                Intent newintent = new Intent(getApplicationContext(), Events.class);
                startActivity(newintent);
                finish();
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
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
        });

        logoutIcon.setOnClickListener(v -> logoutUser());
    }

}