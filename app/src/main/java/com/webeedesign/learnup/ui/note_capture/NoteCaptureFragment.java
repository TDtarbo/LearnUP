package com.webeedesign.learnup.ui.note_capture;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.webeedesign.learnup.R;
import com.webeedesign.learnup.ui.CurrentUser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class NoteCaptureFragment extends Fragment {

    private File photoFile;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 2;
    private static final int REQUEST_STORAGE_PERMISSION = 3;

    private String prefix;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_note_capture, container, false);

        FloatingActionButton captureButton = rootView.findViewById(R.id.img_capture);
        prefix = CurrentUser.getInstance().getUserName() + "_IMG_";

        //Onclick for the floating camera button
        captureButton.setOnClickListener(v -> {
            if (checkPermissions()) {
                openPictureIntent();
            } else {

                //Calling requestPermissions method
                requestPermissions();
            }
        });

        return rootView;
    }


    //Methods for and getting permissions
    private boolean checkPermissions() {
        int cameraPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        int storagePermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return cameraPermission == PackageManager.PERMISSION_GRANTED &&
                storagePermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE },
                REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION || requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openPictureIntent();
            }
        }
    }



    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Create recycler view for the images and initialize grid layout with 3 columns
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        //initialize private Pictures app directory
        File directory = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (directory != null) {
            File[] files = directory.listFiles();


            if (files != null) {
                //Create array for image files
                ArrayList<File> imageList = new ArrayList<>();
                //assigning images to the imageList file array
                for (File file : files) {
                    imageList.add(file);
                }

                //Crate array for each image Uri
                ArrayList<Uri> imageUriList = new ArrayList<>();
                for (File imageFile : imageList) {
                    Uri imageUri = Uri.fromFile(imageFile);
                    imageUriList.add(imageUri);
                }

                Context context = getContext();

                if (context != null) {
                    //setting the adaptor
                    ImageAdapter adapter = new ImageAdapter(context, imageUriList, prefix);
                    recyclerView.setAdapter(adapter);
                }

            }
        }

    }

    private void openPictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            try {
                //create image file
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Handle the exception
            }
            if (photoFile != null) {
                //store image file
                Uri photoURI = FileProvider.getUriForFile(getContext(),"com.webeedesign.learnup.fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    private File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String imageFileName = prefix + timeStamp;
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg", storageDir);

        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {

            //Handle camera results
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getActivity(), "Image captured successfully!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {

                //deleting the empty file that created to assign the image file
                if (photoFile != null && photoFile.exists() && photoFile.length() == 0) {
                    photoFile.delete();
                }
                Toast.makeText(getActivity(), "Image capture canceled.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Failed to capture image.", Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        restartFragment();
    }

    //Calling to onViewCreated to update recycler view
    public void restartFragment(){
        onViewCreated(getView(), null);
    }



}
