package com.webeedesign.learnup.ui.note_capture;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.webeedesign.learnup.R;

import java.io.File;

public class ImageActivity extends AppCompatActivity {

    private Uri imageUri;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private SubsamplingScaleImageView imageView;

    private boolean isSensorRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);


        // Get the image URL from the Intent
        imageUri = Uri.parse(getIntent().getStringExtra("image_url"));

        // Initialize the ImageView using SubsamplingScaleImageView library
        imageView = findViewById(R.id.image_view);
        imageView.setImage(ImageSource.uri(imageUri));
        imageView.setMaxScale(10);

        // Initialize the accelerometer sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    public void confirm(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete the image?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteImage();
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

    public void deleteImage() {
        // Delete the image from the file system
        File file = new File(imageUri.getPath());
        if (file.delete()) {
            Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            // If there is an error while deleting the file, show a toast message
            Toast.makeText(this, "Failed to delete image", Toast.LENGTH_SHORT).show();
        }
    }


    private final SensorEventListener sensorEventListener = new SensorEventListener() {

        int previousOrientation = -1;
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float[] values = sensorEvent.values;

            int orientation = 0;

            if (Math.abs(values[0]) > Math.abs(values[1])) {

                if (values[0] > 0) {
                    orientation = 1; // Portrait mode
                } else {
                    orientation = 3; // Upside down portrait mode
                }
            } else {
                if (values[1] > 0) {
                    orientation = 0; // Landscape mode (left)
                } else {
                    orientation = 2; // Landscape mode (right)
                }
            }
            if (orientation != previousOrientation) {
                rotateImage(orientation);
                previousOrientation = orientation;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private void registerSensorListener() {
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensor != null) {
            sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void unregisterSensorListener() {
        sensorManager.unregisterListener(sensorEventListener);
    }

    private void rotateImage(int orientation) {
        SubsamplingScaleImageView imageView = findViewById(R.id.image_view);
        int degrees = 1;
        switch (orientation) {
            case 0:
                degrees = 0;
                break;
            case 1:
                degrees = 90;
                break;
            case 2:
                degrees = 180;
                break;
            case 3:
                degrees = 270;
                break;
        }
        imageView.setOrientation(degrees);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isSensorRegistered) {
            registerSensorListener();
            isSensorRegistered = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterSensorListener();
        isSensorRegistered = false;
    }
}
