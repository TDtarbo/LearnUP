package com.webeedesign.learnup.ui.calendar.weekly_calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.webeedesign.learnup.ui.CurrentUser;
import com.webeedesign.learnup.ui.database.DatabaseHelper;
import com.webeedesign.learnup.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class WeeklyCalendarFragment extends Fragment {

    @SuppressLint("ConstantLocale")
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE\ndd\nMMM", Locale.getDefault());

    private static final String[] timeslots = createTimeslots();

    private TableLayout table01, table02;
    private ScrollView scrollView;
    private int daysOffset;
    Calendar calendar = Calendar.getInstance();
    private final int currentWeekNumber = calendar.get(Calendar.WEEK_OF_YEAR);
    private int weekOfYear = currentWeekNumber;
    private final int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
    private final int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

    private CurrentUser currentUser;

    public WeeklyCalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        HashMap<String, Integer> weeklyData = DatabaseHelper.getDataForWeek(currentWeekNumber, getContext());


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weekly_calendar, container, false);
        table01 = view.findViewById(R.id.table01);
        table02 = view.findViewById(R.id.table02);
        scrollView = view.findViewById(R.id.tableScroll);

        daysOffset = 0;

        addDateRow();
        for (int i = 0; i < timeslots.length; i++) {
            addTimeslotRow(i, weeklyData);
        }
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        view.setOnTouchListener(new OnSwipeTouchListener(getContext()));

        //Auto Scroll to the current hour
        table02.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Wait until the layout has been drawn before getting the height
                int height = Math.round(table02.getChildAt(currentHour).getTop());

                scrollView.post(() -> scrollView.smoothScrollTo(0, height));

                table02.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        return view;
    }

    //Generate timeslots array
    private static String[] createTimeslots() {

        String[] timeslots = new String[24];
        int index = 0;
        for (int hour = 0; hour < 12; hour++) {
            if (hour == 0) {
                timeslots[index] = "12\nam";
            } else {
                timeslots[index] = Integer.toString(hour) + "\nam";
            }
            index++;
        }
        for (int hour = 12; hour < 24; hour++) {
            if (hour == 12) {
                timeslots[index] = "12\npm";
            } else {
                timeslots[index] = Integer.toString(hour - 12) + "\npm";
            }
            index++;
        }
        return timeslots;
    }


    //Add current week to the table header
    private void addDateRow() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.add(Calendar.DAY_OF_WEEK, daysOffset);
        TableRow row = new TableRow(getContext());
        row.setGravity(Gravity.CENTER_VERTICAL);
        TextView emptyTextView = new TextView(getContext());
        emptyTextView.setWidth(dpToPx(40));
        row.addView(emptyTextView);
        for (int i = 0; i < 7; i++) {
            TextView textView = new TextView(getContext());
            String dateString = dateFormat.format(calendar.getTime());
            SpannableStringBuilder ssb = new SpannableStringBuilder(dateString);

            // get the start and end indices of the "dd" part in the date string
            int start = dateString.indexOf("\n") + 1;
            int end = start + 2;

            // set the text size of the "dd" part
            ssb.setSpan(new AbsoluteSizeSpan(dpToPx(20)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(ssb);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.parseColor("#727272"));
            int padding = dpToPx(8);
            textView.setPadding(padding, padding, padding, padding);

            // Check if the current date is the same as the date being displayed
            String today = dateFormat.format(new Date());

            if (ssb.toString().equals(today)) {
                // Set the background color to blue for today's date
                textView.setBackgroundColor(Color.parseColor("#ffffff"));
                textView.setTextColor(Color.parseColor("#106CC0"));
            }

            row.addView(textView);
            calendar.add(Calendar.DAY_OF_WEEK, 1);
        }
        table01.addView(row);
    }


    //Populate the table cells
    @SuppressLint("SetTextI18n")
    private void addTimeslotRow(int index, HashMap<String, Integer> myMap) {
        int minHeight = dpToPx(100);
        TableRow row = new TableRow(getContext());
        TextView timeTextView = new TextView(getContext());
        timeTextView.setText(timeslots[index]);
        timeTextView.setGravity(Gravity.CENTER);
        timeTextView.setMinWidth(20);
        timeTextView.setTextSize(12);
        timeTextView.setHeight(minHeight);


        //make current hour bold
        if (index == currentHour){
            timeTextView.setTextColor(Color.parseColor("#106CC0"));
            timeTextView.setTypeface(timeTextView.getTypeface(),Typeface.BOLD);
        }else{
            timeTextView.setTextColor(Color.parseColor("#727272"));
        }

        row.addView(timeTextView);

        // Generate table rows

        for (int i = 0; i < 7; i++) {
            CardView cardView = new CardView(requireContext());

            // generate a unique ID for the cell
            String cellId = (timeslots[index].replace("\n", "")) +"_" + i;
            cardView.setId(View.generateViewId());

            //cell customization
            int paddingInline = dpToPx(8);
            int paddingBlock = dpToPx(8);
            cardView.setContentPadding(paddingInline, paddingBlock, paddingInline, paddingBlock);
            TextView textView = new TextView(getContext());
            textView.setText("");
            textView.setGravity(Gravity.CENTER);
            cardView.addView(textView);
            row.addView(cardView);
            textView.setMinimumHeight(minHeight);
            textView.setRotation(-70);



            // customize the cell based on its position in the table
            if (myMap.containsKey(cellId)) {
                int category = myMap.get(cellId);
                textView.setTextColor(Color.parseColor("#ffffff"));
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setMaxWidth(0);
                textView.setMinimumHeight(minHeight);
                textView.setTextSize(10);
                timeTextView.setTypeface(timeTextView.getTypeface(),Typeface.BOLD);


                switch (category){
                    case 0:
                        cardView.setCardBackgroundColor(Color.parseColor("#8B0000"));
                        textView.setText("Exam");
                        break;

                    case 1:
                        cardView.setCardBackgroundColor(Color.parseColor("#4CAF50"));
                        textView.setText("Assignment");

                        break;

                    case 2:
                        cardView.setCardBackgroundColor(Color.parseColor("#9C27B0"));
                        textView.setText("Lab");
                        break;

                    case 3:
                        cardView.setCardBackgroundColor(Color.parseColor("#ED9C24"));
                        textView.setText("Workshop");
                        break;

                    case 4:
                        cardView.setCardBackgroundColor(Color.parseColor("#FF5722"));
                        textView.setText("Project");
                        break;

                    case 5:
                        cardView.setCardBackgroundColor(Color.parseColor("#2196F3"));
                        textView.setText("Lecture");
                        break;

                    case 6:
                        cardView.setCardBackgroundColor(Color.parseColor("#212121"));
                        textView.setText("Other");
                        break;

                    default:
                       break;
                }

                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setMaxLines(1);

            } else if(i == dayOfWeek && currentWeekNumber == weekOfYear){
                cardView.setCardBackgroundColor(Color.parseColor("#edf2ff"));
            }else{
                cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
            }
        }
        // Add the finalized row to the table02
        table02.addView(row);

    }


    //Generate next week data for the tables
    private void showNextWeek() {

        daysOffset += 7;
        weekOfYear += 1;

        HashMap<String, Integer> weeklyData = DatabaseHelper.getDataForWeek(weekOfYear, getContext());

        table01.removeViews(0, table01.getChildCount());
        table02.removeViews(0, table02.getChildCount());
        addDateRow();

        for (int i = 0; i < timeslots.length; i++) {
            addTimeslotRow(i, weeklyData);
        }

        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        table02.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Wait until the layout has been drawn before getting the height
                int height = Math.round(table02.getChildAt(currentHour).getTop());

                scrollView.post(() -> scrollView.smoothScrollTo(0, height));

                table02.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            }
        });
    }

    //Generate previous week data for the tables
    private void showPreviousWeek() {

        daysOffset -= 7;
        weekOfYear -= 1;

        HashMap<String, Integer> weeklyData = DatabaseHelper.getDataForWeek(weekOfYear, getContext());


        table01.removeViews(0, table01.getChildCount());
        table02.removeViews(0, table02.getChildCount());
        addDateRow();
        for (int i = 0; i < timeslots.length; i++) {
            addTimeslotRow(i, weeklyData);
        }

        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        table02.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Wait until the layout has been drawn before getting the height
                int height = Math.round(table02.getChildAt(currentHour).getTop());
                Log.d("MyFragment", "ScrollView height: " + height);

                scrollView.post(() -> scrollView.smoothScrollTo(0, height));

                table02.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    //Swipe listener to navigate between next and previous weeks
    private class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context context) {
            gestureDetector = new GestureDetector(context, new GestureListener());
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffX = e2.getX() - e1.getX();
                    float diffY = e2.getY() - e1.getY();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                showPreviousWeek();
                            } else {
                                showNextWeek();
                            }
                            result = true;
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }
    }

    //dp to pixel converter
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
