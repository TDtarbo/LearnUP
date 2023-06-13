package com.webeedesign.learnup.ui.all_activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.webeedesign.learnup.ui.database.DatabaseHelper;
import com.webeedesign.learnup.R;

public class TaskInfo extends AppCompatActivity {

    private int fetchedCardId;
    private int fetchedCategory;
    private String fetchedDate;
    private String fetchedTime;
    private String fetchedTitle;
    private String fetchedDescription;
    private String fetchedLocation;
    private String fetchedLink;
    private DatabaseHelper LearnUPdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);
        LearnUPdb = new DatabaseHelper(this);

        Intent intent = getIntent();

        // Extract the clickedCardId values from the intent
        fetchedCardId = intent.getIntExtra("cardId", 0);
        fetchedCategory = intent.getIntExtra("category", 2);
        fetchedDate = intent.getStringExtra("date");
        fetchedTime = intent.getStringExtra("time");
        fetchedTitle = intent.getStringExtra("title");
        fetchedDescription = intent.getStringExtra("description");
        fetchedLocation = intent.getStringExtra("location");
        fetchedLink = intent.getStringExtra("link");

        String categoryString = "";

        TextView title, category, due, description, noDescription, location, link;

        title = findViewById(R.id.title);
        category = findViewById(R.id.category);
        due = findViewById(R.id.due);
        description = findViewById(R.id.description);
        noDescription = findViewById(R.id.noDescription);
        location = findViewById(R.id.location);
        link = findViewById(R.id.link);

        ImageView locationIcon, linkIcon;

        locationIcon = findViewById(R.id.locationIcon);
        linkIcon = findViewById(R.id.linkIcon);

        switch (fetchedCategory){
            case 0:
                categoryString = "Exam";
                category.setBackgroundColor(Color.parseColor("#8B0000"));
                break;
            case 1:
                categoryString = "Assignment";
                category.setBackgroundColor(Color.parseColor("#4CAF50"));
                break;
            case 2:
                categoryString = "Lab";
                category.setBackgroundColor(Color.parseColor("#9C27B0"));
                break;
            case 3:
                categoryString = "Workshop";
                category.setBackgroundColor(Color.parseColor("#ED9C24"));
                break;
            case 4:
                categoryString = "Project";
                category.setBackgroundColor(Color.parseColor("#FF5722"));
                break;
            case 5:
                categoryString = "Lecture";
                category.setBackgroundColor(Color.parseColor("#2196F3"));
                break;
            case 6:
                categoryString = "Other";
                category.setBackgroundColor(Color.parseColor("#212121"));
                break;
            default:
                break;
        }

        title.setText(fetchedTitle);
        category.setText(categoryString);
        due.setText("Due on " + fetchedDate + " at, " + fetchedTime);

        if (fetchedDescription.isEmpty()){
            noDescription.setVisibility(View.VISIBLE);
        }else{
            noDescription.setVisibility(View.GONE);
            description.setVisibility(View.VISIBLE);
            description.setText(fetchedDescription);
        }

        if (fetchedLocation.isEmpty()){
            locationIcon.setImageResource(R.drawable.baseline_location_off_24);
            location.setText("Location information are not available  for this task");
            location.setTextSize(12);
            location.setTextColor(Color.parseColor("#959595"));
            location.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        }else{
            location.setText(fetchedLocation);
        }

        if (fetchedLink.isEmpty()){
            linkIcon.setImageResource(R.drawable.baseline_link_off_24);
            link.setText("link(s) not available for this task");
            link.setTextSize(12);
            link.setTextColor(Color.parseColor("#959595"));
        }else{
            link.setText(fetchedLink);
        }

        FloatingActionButton editBtn  = findViewById(R.id.editBtn);
        FloatingActionButton deleteBtn  = findViewById(R.id.deleteBtn);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditTask();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { deleteBtn();}
        });
    }

    public void openEditTask(){

        Intent editTask = new Intent(TaskInfo.this, EditTask.class);
        editTask.putExtra("cardId", fetchedCardId);
        editTask.putExtra("category", fetchedCategory);
        editTask.putExtra("date", fetchedDate);
        editTask.putExtra("time", fetchedTime);
        editTask.putExtra("title", fetchedTitle);
        editTask.putExtra("description", fetchedDescription);
        editTask.putExtra("location", fetchedLocation);
        editTask.putExtra("link", fetchedLink);

        startActivity(editTask);
        finish();
    }

    private void deleteBtn(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this task? This action cannot be undone.");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Integer delete_data = LearnUPdb.deleteTaskTableData(String.valueOf(fetchedCardId));

                if (delete_data>0) {
                    Toast.makeText(TaskInfo.this, "Task Deleted", Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog box
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}