package com.webeedesign.learnup.ui.dashboard;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.webeedesign.learnup.ui.TaskReminderReceiver;
import com.webeedesign.learnup.ui.database.DatabaseHelper;
import com.webeedesign.learnup.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddTask extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {

    private Spinner categoryList;
    private TextView time_lb, validator;

    private Button addBtn, cancelBtn;
    EditText title, description, date, time, location, link;
    private int year, month, day, hour, minute;
    ;
    private DatabaseHelper LearnUPdb;

    private static final String CHANNEL_ID = "task_reminder_channel";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        LearnUPdb = new DatabaseHelper(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        categoryList = findViewById(R.id.category_list);
        validator = findViewById(R.id.spinner_validate);
        time_lb = findViewById(R.id.time_lb);
        addBtn = findViewById(R.id.addBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        categoryList.setOnItemSelectedListener(this);

        String[] categories = getResources().getStringArray(R.array.categories);

        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categoryList.setAdapter(adapter);

        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        location = findViewById(R.id.location);
        link = findViewById(R.id.link);

        initDate();

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelBtn();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                inputValidator();
            }

        });

        createNotificationChannel();


    }
    private void cancelBtn() {
        finish();
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
                        // Set the selected time
                        hour = hourOfDay;
                        minute = minuteOfHour;

                        // Update the time EditText
                        updateTimeEditText();
                    }
                },
                hour,
                minute,
                DateFormat.is24HourFormat(this)
        );
        timePickerDialog.show();
    }

    @SuppressLint("DefaultLocale")
    private void updateTimeEditText() {
        // Update the time EditText with the selected time
        EditText timeEditText = findViewById(R.id.time);
        timeEditText.setText(String.format("%02d:%02d", hour, minute));
        time.setError(null);
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                year,
                month,
                day);
        datePickerDialog.show();
    }


    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = month;
        this.day = dayOfMonth;


        String selectedDate = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
        date.setText(selectedDate);
        date.setError(null);
    }

    private void initDate() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }


    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        String value = adapterView.getItemAtPosition(i).toString();

        if (!value.equals("Select Category")) {
            validator.setVisibility(View.GONE);
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    //validate inputs for prevent empty date insert
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void inputValidator() {

        Object input1 = categoryList.getSelectedItem();
        String input2 = title.getText().toString().trim();
        String input3 = date.getText().toString().trim();
        String input4 = time.getText().toString().trim();

        if (input1.equals("Select Category")) {
            validator.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
        } else if (input2.isEmpty()) {
            title.setError("Require filed");
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
        } else if (input3.isEmpty()) {
            date.setError("Require filed");
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
        } else if (input4.isEmpty()) {
            time.setError("Require filed");
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
        } else {
            // Display an error message if any input field is empty
            try {
                addData();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }


        }
    }


    //add data btn handler
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addData() throws ParseException {

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date formattedDate = dateFormat.parse(date.getText().toString());

        SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm");
        Date formattedTime = inputFormat.parse(time.getText().toString());
        SimpleDateFormat outputFormat = new SimpleDateFormat("ha");
        String taskTime = outputFormat.format(formattedTime).toLowerCase();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(formattedDate);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        String inputDay = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK) - 1);


        String cell = taskTime + "_" + inputDay;

        int category = 0;

        String selectedCategory = (String) categoryList.getSelectedItem();


        switch (selectedCategory) {
            case "Exam":
                category = 0;
                break;
            case "Assignment":
                category = 1;
                break;
            case "Lab":
                category = 2;
                break;
            case "Workshop":
                category = 3;
                break;
            case "Project":
                category = 4;
                break;
            case "Lecture":
                category = 5;
                break;
            case "Other":
                category = 6;
                break;
            default:
                break;
        }


        //calling insertDataToTaskTable method and passing arguments
        boolean isInserted;
        try {

            isInserted = LearnUPdb.insertDataToTaskTable(
                    cell,
                    category,
                    title.getText().toString(),
                    description.getText().toString(),
                    date.getText().toString(),
                    time.getText().toString(),
                    location.getText().toString(),
                    link.getText().toString(),
                    week);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        if (isInserted) {
            Toast.makeText(AddTask.this, "Task Added!", Toast.LENGTH_SHORT).show();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
                createNotification();
                scheduleNotification(selectedCategory, year, month, day, hour, minute);
            } else {
                Toast.makeText(AddTask.this, "Task Added! (Permission required for vibration)", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(AddTask.this, "Failed to Add!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Task Reminder Channel";
            String description = "Channel for task reminders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotification() {
        // Check if the required permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            // Handle the case where permission is not granted
            Toast.makeText(this, "Vibration permission not granted.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create an intent for the notification
        Intent intent = new Intent(this, AddTask.class);
        PendingIntent pendingIntent;

        long notificationId = System.currentTimeMillis(); // Generate unique notification ID

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Use FLAG_IMMUTABLE for Android S and above
            pendingIntent = PendingIntent.getBroadcast(this, (int) notificationId, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            // Use the deprecated PendingIntent.FLAG_UPDATE_CURRENT for older Android versions
            pendingIntent = PendingIntent.getBroadcast(this, (int) notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.raw.smallicon)
                .setContentTitle("Task Added!")
                .setContentText("Your task has been added successfully.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify((int) notificationId, builder.build());
    }


    private void scheduleNotification(String taskName, int year, int month, int day, int hour, int minute) {
        // Create an intent for the notification
        Intent intent = new Intent(this, TaskReminderReceiver.class);
        // Pass any extra data to the intent if needed
        intent.putExtra("TASK_NAME", taskName);

        PendingIntent pendingIntent;

        long notificationId = System.currentTimeMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Use FLAG_IMMUTABLE for Android S and above
            pendingIntent = PendingIntent.getBroadcast(this, (int) notificationId, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            // Use the deprecated PendingIntent.FLAG_UPDATE_CURRENT for older Android versions
            pendingIntent = PendingIntent.getBroadcast(this, (int) notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // Set the notification trigger time based on user input
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        calendar.add(Calendar.HOUR_OF_DAY, -1);
        // Get the trigger time in milliseconds
        long triggerTimeMillis = calendar.getTimeInMillis();

        // Get the AlarmManager service
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent);
        }
    }







}