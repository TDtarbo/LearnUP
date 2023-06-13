package com.webeedesign.learnup.ui.voice_notes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.webeedesign.learnup.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MyRecordings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recordings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        LinearLayout parentLayout = findViewById(R.id.parentLayout);
        ArrayList<File> savedFilesList = getSavedAudioFiles();

        if (savedFilesList.isEmpty()){
            TextView textView = new TextView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            textView.setLayoutParams(layoutParams);
            textView.setText("No recordings");
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(0,50,0,0);

            parentLayout.addView(textView);

        }else{
            createCardViews(parentLayout,savedFilesList);
        }


    }

    public void createCardViews(LinearLayout parentLayout, ArrayList<File> savedFilesList){
        for (File audioFile : savedFilesList) {
            // Create a new card view for each audio file
            CardView cardView = new CardView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(20, 12, 20, 12);
            cardView.setLayoutParams(layoutParams);

            // Set card view properties
            cardView.setCardBackgroundColor(Color.WHITE);
            cardView.setRadius(30);
            cardView.setCardElevation(10);
            cardView.setMinimumHeight(150);

            // Create a horizontal LinearLayout to hold the file name and delete button
            LinearLayout fileLayout = new LinearLayout(this);
            fileLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams fileLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            fileLayout.setLayoutParams(fileLayoutParams);

            // Create a TextView for the file name
            TextView fileNameTextView = new TextView(this);
            LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f);
            fileNameTextView.setLayoutParams(textViewParams);
            String fileName = audioFile.getName();
            int extensionIndex = fileName.lastIndexOf(".");
            if (extensionIndex > 0) {
                fileName = fileName.substring(0, extensionIndex);
            }
            fileNameTextView.setText(fileName);
            fileNameTextView.setTextSize(18);
            fileNameTextView.setPadding(30, 25, 25, 25);


            ImageButton deleteButton = new ImageButton(this);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            deleteButton.setLayoutParams(buttonParams);
            deleteButton.setImageResource(R.drawable.baseline_delete_24_gray); // Set the trash icon
            deleteButton.setBackground(null); // Remove the background to make it transparent
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteFileDialog(audioFile, cardView);
                }
            });

            // Add the TextView and delete button to the file layout
            fileLayout.addView(fileNameTextView);
            fileLayout.addView(deleteButton);

            // Add the file layout to the card view
            cardView.addView(fileLayout);

            // Handle click event to play the audio file
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playAudioFile(audioFile);
                }
            });

            // Add the card view to the parent layout
            parentLayout.addView(cardView);
        }
    }

    private void showDeleteFileDialog(File audioFile, CardView cardView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete File");
        builder.setMessage("Are you sure you want to delete this file?");

        // Set the positive button
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete the audio file
                boolean deleted = audioFile.delete();

                if (deleted) {
                    // Remove the card view from the parent layout
                    LinearLayout parentLayout = findViewById(R.id.parentLayout);
                    parentLayout.removeView(cardView);

                    Toast.makeText(getApplicationContext(), "File deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to delete the file", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set the negative button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cancel the deletion or perform any other actions
            }
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private ArrayList<File> getSavedAudioFiles() {
        ArrayList<File> savedFilesList = new ArrayList<>();

        // Get the directory for storing the audio files in private app storage
        File directory = getExternalFilesDir(Environment.DIRECTORY_MUSIC);

        // Check if the directory exists
        if (directory != null && directory.exists()) {
            // Get all files in the directory
            File[] files = directory.listFiles();

            // Iterate over the files and add audio files to the list
            if (files != null) {
                for (File file : files) {
                    // Add audio files to the list (if needed, you can add additional checks for file extensions)
                    savedFilesList.add(file);
                }
            }
        }

        return savedFilesList;
    }

    private void playAudioFile(File audioFile) {
        // Generate a content URI for the audio file
        Uri contentUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", audioFile);

        // Create an intent with the action VIEW
        Intent intent = new Intent(Intent.ACTION_VIEW);

        // Set the data and MIME type of the intent
        intent.setDataAndType(contentUri, "audio/*");

        // Set the flags to grant read access to the URI
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Check if there's an app available to handle the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the activity with the intent
            startActivity(intent);
        } else {
            // No app available to handle the intent
            Toast.makeText(this, "No app available to play audio file", Toast.LENGTH_SHORT).show();
        }
    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}