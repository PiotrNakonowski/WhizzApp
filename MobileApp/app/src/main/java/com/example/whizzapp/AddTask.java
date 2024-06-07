package com.example.whizzapp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddTask extends AppCompatActivity {
    private MaterialCardView inputFrameTaskTitle;
    private EditText taskTitle;
    private MaterialCardView inputFrameTaskDescription;
    private EditText taskDescription;
    private ImageView backIcon;
    private MaterialCardView addTaskButton;
    private MaterialCardView dateSelectButton;
    private MaterialCardView inputFrameDate;
    private TextView expireDate;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        inputFrameTaskTitle = findViewById(R.id.inputframeTaskTitle);
        inputFrameTaskDescription = findViewById(R.id.inputframeTaskDescription);
        backIcon = findViewById(R.id.back_icon);
        addTaskButton = findViewById(R.id.sendButton);
        dateSelectButton = findViewById(R.id.expireDateButton);
        expireDate = findViewById(R.id.expireDate);
        taskTitle = findViewById(R.id.taskTitle);
        taskDescription = findViewById(R.id.taskDescription);
        inputFrameDate = findViewById(R.id.inputframeExpireDate);
        mAuth = FirebaseAuth.getInstance();

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ToDoList.class);
                startActivity(intent);
                finish();
            }
        });

        dateSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddTask.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String selectedDate = dayOfMonth + "." + (month + 1) + "." + year + "r.";
                                expireDate.setText(selectedDate);
                                expireDate.setTextColor(getResources().getColor(R.color.black));
                            }
                        }, year, month, day);

                datePickerDialog.show();
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getApplicationContext(), ToDoList.class);
                startActivity(intent);
                finish();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);

        setFocusChangeListenerForCard(inputFrameTaskTitle, R.id.taskTitle);
        setFocusChangeListenerForCard(inputFrameTaskDescription, R.id.taskDescription);

        addTaskButton.setOnClickListener(v -> addTask());
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

    private void checkExactAlarmPermissionAndScheduleNotification(String taskName, long expireDateMillis) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                requestExactAlarmPermission();
                return;
            }
        }
        scheduleNotification(taskName, expireDateMillis);
    }

    private void requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            String taskName = taskTitle.getText().toString().trim();
            String expireDateString = expireDate.getText().toString().trim();
            expireDateString = expireDateString.replace("r.", "").trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy");
            LocalDate expireDate = LocalDate.parse(expireDateString, formatter);
            ZonedDateTime expireDateTime = expireDate.atStartOfDay(ZoneId.systemDefault());
            long expireDateMillis = expireDateTime.toInstant().toEpochMilli();
            checkExactAlarmPermissionAndScheduleNotification(taskName, expireDateMillis);
        }
    }

    private void scheduleNotification(String taskName, long expireDateMillis) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TaskNotificationReceiver.class);
        intent.putExtra("taskName", taskName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        long triggerTime = System.currentTimeMillis() + 10 * 1000;

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    private void addTask() {
        String title = taskTitle.getText().toString().trim();
        String description = taskDescription.getText().toString().trim();
        String date = expireDate.getText().toString().trim();
        String dateWithoutR = date.replace("r.", "").trim();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy");
        LocalDate expire = LocalDate.parse(dateWithoutR, formatter);

        ZonedDateTime expireDateTime = expire.atStartOfDay(ZoneId.systemDefault());

        long expireDateMillis = expireDateTime.toInstant().toEpochMilli();

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

        if (title.isEmpty()) {
            taskTitle.setError("Pole Tytuł jest wymagane!");
            taskTitle.requestFocus();
            inputFrameTaskTitle.setStrokeColor(getResources().getColor(R.color.error));
            return;
        }

        if (description.isEmpty()) {
            taskDescription.setError("Pole Opis jest wymagane!");
            taskDescription.requestFocus();
            inputFrameTaskDescription.setStrokeColor(getResources().getColor(R.color.error));
            return;
        }

        if (date.equals("Data zakończenia")) {
            expireDate.requestFocus();
            inputFrameDate.setStrokeColor(getResources().getColor(R.color.error));
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        CollectionReference tasksCollectionRef = db.collection("todo").document(userID).collection("tasks");

        Map<String, Object> taskData = new HashMap<>();
        taskData.put("Title", title);
        taskData.put("Description", description);
        taskData.put("ExpireDate", date);

        tasksCollectionRef.add(taskData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddTask.this, "Nowe zadanie zostało utworzone!", Toast.LENGTH_SHORT).show();
                        Log.d("Firestore", "Dane zostały pomyślnie dodane do bazy danych");
                        Intent intent = new Intent(getApplicationContext(), ToDoList.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Błąd podczas dodawania danych do bazy danych: " + e.getMessage());
                        Toast.makeText(AddTask.this, "Wystąpił błąd podczad dodawania zadania!", Toast.LENGTH_SHORT).show();
                    }
                });

        scheduleNotification(title, expireDateMillis);
        return;
    }

    private void clearAllEditTextFocus() {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null && currentFocus instanceof EditText) {
            currentFocus.clearFocus();
        }
    }
}