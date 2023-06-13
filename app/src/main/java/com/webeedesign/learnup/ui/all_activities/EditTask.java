package com.webeedesign.learnup.ui.all_activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
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

import com.webeedesign.learnup.ui.database.DatabaseHelper;
import com.webeedesign.learnup.R;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditTask extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {

    private Spinner categoryList;
    private TextView time_lb, validator;

    private Button updateBtn;
    EditText title, description, date, time, location, link;
    private int year, month, day, hour, minute;;

    private int fetchedCardId;
    private int fetchedCategory;
    private String fetchedDate;
    private String fetchedTime;
    private String fetchedTitle;
    private String fetchedDescription;
    private String fetchedLocation;
    private String fetchedLink;

    DatabaseHelper LearnUPdb;

    private boolean isChanged = false;

    private int category;

    private boolean isSkipped = false;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        LearnUPdb = new DatabaseHelper(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        categoryList = findViewById(R.id.category_list);
        validator = findViewById(R.id.spinner_validate);
        time_lb = findViewById(R.id.time_lb);
        updateBtn = findViewById(R.id.updateBtn);
        updateBtn.setEnabled(false);

        categoryList.setOnItemSelectedListener(this);

        String[] categories = getResources().getStringArray(R.array.categories);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categoryList.setAdapter(adapter);


        Intent editTask = getIntent();

        // Extract the clickedCardId values from the intent
        fetchedCardId = editTask.getIntExtra("cardId", 0);
        fetchedCategory = editTask.getIntExtra("category", 0);
        fetchedDate = editTask.getStringExtra("date");
        fetchedTime = editTask.getStringExtra("time");
        fetchedTitle = editTask.getStringExtra("title");
        fetchedDescription = editTask.getStringExtra("description");
        fetchedLocation = editTask.getStringExtra("location");
        fetchedLink = editTask.getStringExtra("link");

        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        location = findViewById(R.id.location);
        link = findViewById(R.id.link);

        title.setText(fetchedTitle);
        description.setText(fetchedDescription);
        date.setText(fetchedDate);
        time.setText(fetchedTime);
        location.setText(fetchedLocation);
        link.setText(fetchedLink);
        categoryList.setSelection(fetchedCategory + 1);

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

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                inputValidator();
            }
        });

        textChangedListener();

    }

    private void textChangedListener(){

            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // Do nothing
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(!isChanged){
                        updateBtn.setEnabled(true);
                        isChanged = true;
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    // Do nothing
                }
            };


            title.addTextChangedListener(textWatcher);
            description.addTextChangedListener(textWatcher);
            date.addTextChangedListener(textWatcher);
            time.addTextChangedListener(textWatcher);
            location.addTextChangedListener(textWatcher);
            link.addTextChangedListener(textWatcher);


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
        int intValue = adapterView.getSelectedItemPosition();

        if (!value.equals("Select Category")){
            validator.setVisibility(View.GONE);
        }

        if (!isSkipped){
            isSkipped = true;
        }else{
            if (!(intValue - 1 == fetchedCategory)){
                isChanged = true;
                updateBtn.setEnabled(true);
            }else{
                isChanged = false;
                updateBtn.setEnabled(false);
            }
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void inputValidator(){

        Object input1 = categoryList.getSelectedItem();
        String input2 = title.getText().toString().trim();
        String input3 = date.getText().toString().trim();
        String input4 = time.getText().toString().trim();

        if (input1.equals("Select Category")){
            validator.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
        } else if (input2.isEmpty()){
            title.setError("Require filed");
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
        } else if (input3.isEmpty()){
            date.setError("Require filed");
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
        } else if (input4.isEmpty()){
            time.setError("Require filed");
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
        } else {
            // Display an error message if any input field is empty
            try {
               updateData();
           } catch (ParseException e) {
               throw new RuntimeException(e);
           }


        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateData() throws ParseException {

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date formattedDate = dateFormat.parse(date.getText().toString());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm");
        Date formattedTime = inputFormat.parse(time.getText().toString());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("ha");
        String taskTime = outputFormat.format(formattedTime).toLowerCase();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(formattedDate);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK) -1);

        String cell = taskTime + "_" + day;

        getCategory();

        //calling insertData method and passing arguments
        boolean isInserted;
        isInserted = LearnUPdb.updateTaskData(
                fetchedCardId,
                cell,
                category,
                title.getText().toString(),
                description.getText().toString(),
                date.getText().toString(),
                time.getText().toString(),
                location.getText().toString(),
                link.getText().toString(),
                week);

        if (isInserted){

            Toast.makeText(EditTask.this, "Task updated!", Toast.LENGTH_SHORT).show();
            passUpdatedData();
            finish();

        }else{
            Toast.makeText(EditTask.this, "Error occur white updating the task", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void onBackPressed() {
        getCategory();
        passDefaultData();
    }

    private void passUpdatedData(){


        Intent editTask = new Intent(EditTask.this, TaskInfo.class);

        editTask.putExtra("cardId", fetchedCardId);
        editTask.putExtra("category", category);
        Log.d("hoe", "navigateToTaskInfo: " + category);
        editTask.putExtra("date", date.getText().toString());
        editTask.putExtra("time", time.getText().toString());
        editTask.putExtra("title", title.getText().toString());
        editTask.putExtra("description", description.getText().toString());
        editTask.putExtra("location", location.getText().toString());
        editTask.putExtra("link", link.getText().toString());

        startActivity(editTask);
        finish();
    }

    private void passDefaultData(){


        Intent editTask = new Intent(EditTask.this, TaskInfo.class);

        editTask.putExtra("cardId", fetchedCardId);
        editTask.putExtra("category", fetchedCategory);
        editTask.putExtra("date", fetchedDate);
        editTask.putExtra("time", fetchedTime);
        editTask.putExtra("title", fetchedTitle);
        editTask.putExtra("description", fetchedDescription);
        editTask.putExtra("location", fetchedLocation);
        editTask.putExtra("link", fetchedLink);

        if(isChanged){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Changes are not saved!");
            builder.setMessage("Do you want to save them?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        updateData();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startActivity(editTask);
                    finish();
                    dialog.dismiss();
                }
            });

            builder.setNeutralButton("Continue Editing", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            // Create and show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }else{
            startActivity(editTask);
            finish();
        }


    }



    private void getCategory(){
        switch ((String) categoryList.getSelectedItem()){
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
    }





}