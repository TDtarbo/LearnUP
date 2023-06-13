package com.webeedesign.learnup.ui.voice_notes;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.webeedesign.learnup.R;
import com.webeedesign.learnup.ui.dashboard.AddTask;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class VoiceNotesFragment extends Fragment {

    private ImageView recBtn;
    private Chronometer recordDuration;
    private GifImageView voice;

    private Button recListBtn;

    private boolean recording = false;
    private static final int RECORD_AUDIO_PERMISSION_CODE = 1;

    private MediaRecorder mediaRecorder;
    private String fileNameInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_voice_notes, container, false);

        recBtn = view.findViewById(R.id.recBtn);
        recListBtn = view.findViewById(R.id.recList);
        recordDuration = view.findViewById(R.id.recordDuration);
        voice = view.findViewById(R.id.voiceGif);

        recBtn.setOnClickListener(view1 -> recBtnHandler());
        recListBtn.setOnClickListener(view12 -> {
            Intent intent = new Intent(getContext(), MyRecordings.class);
            startActivity(intent);
        });

        return view;
    }

    private void requestMicrophonePermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.RECORD_AUDIO},
                RECORD_AUDIO_PERMISSION_CODE);
    }

    private boolean checkMicrophonePermission() {
        int result = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                recBtnHandler();
            } else {
                // Permission denied
                // Handle the scenario where the user denied microphone permission
            }
        }
    }


    public void recBtnHandler() {
        if (checkMicrophonePermission()) {
            if (!recording) {
                // Start recording
                recordDuration.setBase(SystemClock.elapsedRealtime());
                recordDuration.start();
                recBtn.setImageResource(R.raw.mic);
                recording = true;

                recordDuration.animate()
                        .translationY(300f)
                        .setDuration(300)
                        .scaleX(0.3f)
                        .scaleY(0.3f)
                        .start();

                voice.setVisibility(View.VISIBLE);

                recListBtn.animate()
                        .alpha(0)
                        .setDuration(300)
                        .scaleX(0f)
                        .scaleY(0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                recListBtn.setVisibility(View.GONE);
                            }
                        })
                        .start();

                final MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.play);
                mediaPlayer.start();

                startRecording();

            } else {
                // Stop recording
                recordDuration.setBase(SystemClock.elapsedRealtime());
                recordDuration.stop();
                recBtn.setImageResource(R.raw.mic_gray);
                recording = false;

                recordDuration.animate()
                        .translationY(-300f)
                        .scaleX(1)
                        .scaleY(1)
                        .setDuration(300)
                        .start();

                voice.setVisibility(View.GONE);

                recListBtn.animate()
                        .alpha(1)
                        .setDuration(300)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                recListBtn.setVisibility(View.VISIBLE);
                            }
                        })
                        .start();

                final MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.stop);
                mediaPlayer.start();

                stopRecording();
            }
        } else {
            requestMicrophonePermission();
        }
    }



    private void startRecording() {
        // Create the file to store the recorded audio
        File audioFile = createAudioFile();
        if (audioFile == null) {
            return;
        }

        try {
            // Initialize the MediaRecorder
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(audioFile.getAbsolutePath());

            // Start recording
            mediaRecorder.prepare();
            mediaRecorder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;

            showSaveFileDialog();

        }
    }

    private File audioFile;

    private File createAudioFile() {
        // Generate a unique file name for the audio file
        String fileName = "temp.mp3";

        // Get the directory for storing the audio file in private app storage
        File directory = getActivity().getExternalFilesDir(Environment.DIRECTORY_MUSIC);

        if (directory != null) {
            // Create the file in the directory
            audioFile = new File(directory, fileName);
            audioFile.getAbsolutePath();
            return audioFile;
        } else {
            return null;
        }
    }

    private void showSaveFileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Save Recording");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(
                50, 20, 50, 20);

        // Create an EditText for entering the file name
        final EditText fileNameEditText = new EditText(requireContext());
        fileNameEditText.setHint("Enter File Name");



        layout.addView(fileNameEditText, layoutParams);

        builder.setView(layout);


        // Set the positive button
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fileNameInput = fileNameEditText.getText().toString();

                if (!fileNameInput.isEmpty()) {
                    File audioFile = createAudioFile();

                    if (audioFile != null) {
                        // Generate the new file name based on user input
                        String newFileName = fileNameInput + ".mp3";

                        // Create a File object for the new file
                        File newFile = new File(audioFile.getParentFile(), newFileName);

                        // Handle file name conflicts
                        int count = 1;
                        while (newFile.exists()) {
                            String incrementedFileName = fileNameInput + " (" + count + ").mp3";
                            newFile = new File(audioFile.getParentFile(), incrementedFileName);
                            count++;
                        }

                        // Rename the audio file
                        boolean renamed = audioFile.renameTo(newFile);

                        if (renamed) {
                            Toast.makeText(getContext(), "File saved!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to save the file", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error saving the file", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "File name cannot be empty", Toast.LENGTH_SHORT).show();

                    // Keep the dialog open by overriding the behavior
                    try {
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete the file
                if (audioFile != null && audioFile.exists()) {
                    audioFile.delete();

                }
                // Cancel the recording or perform any other actions
            }
        });


        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }



}