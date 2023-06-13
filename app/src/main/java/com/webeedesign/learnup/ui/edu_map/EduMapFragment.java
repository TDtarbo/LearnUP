package com.webeedesign.learnup.ui.edu_map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.webeedesign.learnup.R;

    public class EduMapFragment extends Fragment implements OnMapReadyCallback {

        private GoogleMap mMap;
        private View rootView;
        private static final int LOCATION_PERMISSION_CODE = 101;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


            rootView = inflater.inflate(R.layout.fragment_edu_map, container, false);

            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            FloatingActionButton schoolsBtn = rootView.findViewById(R.id.schools);
            FloatingActionButton librariesBtn = rootView.findViewById(R.id.libraries);
            FloatingActionButton bookstoresBtn = rootView.findViewById(R.id.bookstores);
            FloatingActionButton restaurantsBtn = rootView.findViewById(R.id.restaurants);
            FloatingActionButton closeBtn = rootView.findViewById(R.id.close);

            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMap.clear();
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style));
                    closeBtn.setVisibility(View.GONE);
                    schoolsBtn.setVisibility(View.VISIBLE);
                    librariesBtn.setVisibility(View.VISIBLE);
                    bookstoresBtn.setVisibility(View.VISIBLE);
                    restaurantsBtn.setVisibility(View.VISIBLE);

                    mMap.getMyLocation();
                    // Get the user's location
                    Location myLocation = mMap.getMyLocation();

                    // Create a LatLng object from the user's location
                    LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

                    // Animate the camera to the user's location
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
            });

            schoolsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    schoolsBtn.setVisibility(View.GONE);
                    librariesBtn.setVisibility(View.VISIBLE);
                    bookstoresBtn.setVisibility(View.VISIBLE);
                    restaurantsBtn.setVisibility(View.VISIBLE);
                    closeBtn.setVisibility(View.VISIBLE);
                    mMap.clear();
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style_empty));
                    addMarker(new LatLng(6.992451309356518, 81.05447617093554), "School");
                    addMarker(new LatLng(6.988003083886815, 81.04629736574456), "School");
                }
            });

            librariesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    schoolsBtn.setVisibility(View.VISIBLE);
                    bookstoresBtn.setVisibility(View.VISIBLE);
                    restaurantsBtn.setVisibility(View.VISIBLE);
                    librariesBtn.setVisibility(View.GONE);
                    closeBtn.setVisibility(View.VISIBLE);
                    mMap.clear();
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style_empty));
                    addMarker(new LatLng(6.99563176459976, 81.05382634805736), "Library");
                    addMarker(new LatLng(6.990938917447859, 81.05295244832462), "Library");
                }
            });

            restaurantsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    schoolsBtn.setVisibility(View.VISIBLE);
                    bookstoresBtn.setVisibility(View.VISIBLE);
                    librariesBtn.setVisibility(View.VISIBLE);
                    restaurantsBtn.setVisibility(View.GONE);
                    closeBtn.setVisibility(View.VISIBLE);
                    mMap.clear();
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style_empty));
                    addMarker(new LatLng(6.990204960786668, 81.05192169479369), "Restaurant");
                    addMarker(new LatLng(6.991739596127017, 81.05447617093554), "Restaurant");
                    addMarker(new LatLng(6.987758430257652, 81.04764182687185), "Restaurant");
                    addMarker(new LatLng(6.994875574406686, 81.05149594877005), "Restaurant");
                    addMarker(new LatLng(6.990293925291839, 81.05510358612827), "Restaurant");
                    addMarker(new LatLng(6.990583059816705, 81.05239225618826), "Restaurant");
                }
            });

            bookstoresBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    schoolsBtn.setVisibility(View.VISIBLE);
                    restaurantsBtn.setVisibility(View.VISIBLE);
                    librariesBtn.setVisibility(View.VISIBLE);
                    bookstoresBtn.setVisibility(View.GONE);
                    closeBtn.setVisibility(View.VISIBLE);
                    mMap.clear();
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style_empty));
                    addMarker(new LatLng(6.989896701409752, 81.05640044085908), "Bookstore");
                    addMarker(new LatLng(6.993912961504448, 81.05721934525864), "Bookstore");
                    addMarker(new LatLng(6.992908899770352, 81.05047542666779), "Bookstore");
                    addMarker(new LatLng(6.97821128312117, 81.05052277427578), "Bookstore");
                    addMarker(new LatLng(6.985282405528273, 81.04708954689815), "Bookstore");
                    addMarker(new LatLng(6.990351397365473, 81.0507373508354), "Bookstore");
                }
            });


            return rootView;
        }

        private void addMarker(LatLng latLng, String title) {

            mMap.addMarker(new MarkerOptions().position(latLng).title(title));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f);
            mMap.animateCamera(cameraUpdate);
        }


        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;


            if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mMap.setMyLocationEnabled(true);
                LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16f);
                    mMap.animateCamera(cameraUpdate);
                }

            } else {
                requestLocationPermission();
            }
        }


        private void requestLocationPermission(){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }


        @SuppressWarnings("deprecation")
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == LOCATION_PERMISSION_CODE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);

                        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
                        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16f);
                            mMap.animateCamera(cameraUpdate);
                        }
                    }
                }
            }
        }
    }
