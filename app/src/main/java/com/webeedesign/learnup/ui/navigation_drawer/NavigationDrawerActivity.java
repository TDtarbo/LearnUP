package com.webeedesign.learnup.ui.navigation_drawer;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.webeedesign.learnup.R;
import com.webeedesign.learnup.databinding.ActivityNavigationDrawerBinding;
import com.webeedesign.learnup.ui.all_activities.EditTask;
import com.webeedesign.learnup.ui.login.LoginActivity;

public class NavigationDrawerActivity extends AppCompatActivity {

    private ClipData.Item logout;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNavigationDrawerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavigationDrawerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarNavigationDrawer.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        Menu menu = navigationView.getMenu();
        MenuItem logoutItem = menu.findItem(R.id.nav_logout);

        // Set the navigation graph for the NavController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation_drawer);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Define the top-level destinations and openable layout for the AppBarConfiguration
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard, R.id.nav_calendar, R.id.nav_all_activities, R.id.nav_todo_list, R.id.nav_edumap, R.id.nav_voice_notes, R.id.nav_note_capture, R.id.nav_profile)
                .setOpenableLayout(drawer)
                .build();

        // Set up the ActionBar with the NavController and AppBarConfiguration
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        logoutItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                AlertDialog.Builder builder = new AlertDialog.Builder(NavigationDrawerActivity.this);
                builder.setMessage("Are you sure you want to log out?");
                builder.setPositiveButton("Log out", (dialog, id) -> {

                    Toast.makeText(NavigationDrawerActivity.this, "Logged out successfully ", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(NavigationDrawerActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked the "Cancel" button, so dismiss the dialog box
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation_drawer);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
