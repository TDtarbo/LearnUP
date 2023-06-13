package com.webeedesign.learnup.ui.calendar;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.webeedesign.learnup.R;
import com.webeedesign.learnup.ui.calendar.monthly_calendar.MonthlyCalendarFragment;
import com.webeedesign.learnup.ui.calendar.weekly_calendar.WeeklyCalendarFragment;

public class CalendarFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        return view;
    }
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.calendar_container, new WeeklyCalendarFragment())
                    .commit();
        }
    }
    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment;
        switch (item.getItemId()) {
            case R.id.navigation_item2:
                selectedFragment = new MonthlyCalendarFragment();
                break;
            default:
                selectedFragment = new WeeklyCalendarFragment();
        }
        getChildFragmentManager().beginTransaction()
                .replace(R.id.calendar_container, selectedFragment)
                .commit();
        return true;
    }

}