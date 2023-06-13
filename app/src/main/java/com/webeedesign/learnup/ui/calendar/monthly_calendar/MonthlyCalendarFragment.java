package com.webeedesign.learnup.ui.calendar.monthly_calendar;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarPickerView;
import com.webeedesign.learnup.ui.database.DatabaseHelper;
import com.webeedesign.learnup.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MonthlyCalendarFragment extends Fragment {

    private TextView header;

    private boolean hideOverDue = false;

    private ScrollView noEvent, cardContainer;

    private LinearLayout cards;

    private HashMap<Integer, String[]> monthlyData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_monthly_calendar, container, false);

        monthlyData = DatabaseHelper.getDataForMonth(getContext());
        cardContainer = view.findViewById(R.id.card_container);
        cards = view.findViewById(R.id.cards);

        CalendarPickerView calendarView = view.findViewById(R.id.calendar_view);

        header = view.findViewById(R.id.date);
        noEvent = view.findViewById(R.id.no_event);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        String todayString = sdf.format(new Date());
        header.setText(todayString);

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date thisMonth = calendar.getTime();

        calendar.add(Calendar.MONTH, 2);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date nextMonth = calendar.getTime();

        calendarView.init(thisMonth, nextMonth);

        Date today = new Date();
        calendarView.scrollToDate(today);

        List<String> datesToHighlight = getDatesToHighlight();

        SimpleDateFormat listDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


        if(datesToHighlight.contains(listDateFormat.format(today))) {
            noEvent.setVisibility(View.GONE);
            cardContainer.setVisibility(View.VISIBLE);
            createCardViews(listDateFormat.format(today));
        }

        CalendarCellDecorator decorator = (cellView, date) -> {
            String formattedDate = listDateFormat.format(date);
            cellView.getDayOfMonthTextView().setTextSize(14);

            if (DateUtils.isToday(date.getTime())) {
                cellView.setBackgroundColor(Color.parseColor("#c42f18"));
            } else if (datesToHighlight.contains(formattedDate)) {
                cellView.setBackgroundColor(Color.parseColor("#7a0669"));
                cellView.getDayOfMonthTextView().setTextColor(Color.parseColor("#ffffff"));
            }
        };

        calendarView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                String formattedDate = listDateFormat.format(date);

                String selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);
                header.setText(selectedDate);

                if(!datesToHighlight.contains(formattedDate)){
                    noEvent.setVisibility(View.VISIBLE);
                    cardContainer.setVisibility(View.GONE);
                }else {
                    noEvent.setVisibility(View.GONE);
                    createCardViews(listDateFormat.format(date));
                    cardContainer.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onDateUnselected(Date date) {
            }
        });
        calendarView.setDecorators(Collections.singletonList(decorator));
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<String> getDatesToHighlight() {

        List<String> formattedDates = new ArrayList<>();

        for (Map.Entry<Integer, String[]> entry : monthlyData.entrySet()) {
            String[] data = entry.getValue();
            formattedDates.add(data[4]);
        }

        return formattedDates;
    }

    @SuppressLint("SetTextI18n")
    public void createCardViews(String day) {
        cards.removeAllViews();

        for (Map.Entry<Integer, String[]> entry : monthlyData.entrySet()) {
            String[] data = entry.getValue();
            String date = data[4];

            if (date.equals(day)) {

                java.util.Calendar today = java.util.Calendar.getInstance();
                today.setTimeInMillis(System.currentTimeMillis());

                String category = data[1];
                date = data[4];
                String time = data[5];
                String title = data[2];
                String description = data[3];
                String due = date + " \n" + time;
                String venue = date + " " + time;

                if (category.equals("all_category")){
                    continue;
                }

                ViewGroup colorView = new FrameLayout(requireContext()); // Create a View for color
                colorView.setLayoutParams(new ViewGroup.LayoutParams(
                        220, // Set the desired width of the color view
                        ViewGroup.LayoutParams.MATCH_PARENT
                ));

                TextView timeLeft = new TextView(requireContext());
                timeLeft.setGravity(Gravity.CENTER);
                timeLeft.setTextColor(Color.parseColor("#ffffff"));
                timeLeft.setTextSize(12);// Set gravity to center
                colorView.addView(timeLeft);


                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                String currentDatetimeString = sdf.format(new Date());

                try {
                    // Parse the target date and time
                    Date currentDatetime = sdf.parse(currentDatetimeString);
                    Date targetDatetime = sdf.parse(venue);

                    // Create calendar instances for the current datetime and target datetime
                    java.util.Calendar currentCalendar = java.util.Calendar.getInstance();
                    currentCalendar.setTime(currentDatetime);

                    java.util.Calendar targetCalendar = java.util.Calendar.getInstance();
                    targetCalendar.setTime(targetDatetime);

                    // Calculate the time difference in milliseconds
                    long timeDifference = targetCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis();

                    // Convert the time difference to day
                    long daysDifference = timeDifference / (24 * 60 * 60 * 1000);



                    if (daysDifference >= 1) {
                        // Calculate hours difference
                        long hoursDifference = (timeDifference % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);

                        // Set the text to display days and hours
                        timeLeft.setText(daysDifference + "d " + hoursDifference + "h");
                    }
                    // Check if daysDifference is less than 1 day
                    else {
                        // Calculate hours and minutes difference
                        long hoursDifference = timeDifference / (60 * 60 * 1000);
                        long minutesDifference = (timeDifference % (60 * 60 * 1000)) / (60 * 1000);

                        // Check if hoursDifference is less than 1 hour
                        if (minutesDifference < 0 || hoursDifference < 0) {
                            timeLeft.setText("Over due");
                            if (hideOverDue){
                                continue;
                            }
                        }else if (hoursDifference < 1) {
                            // Set the text to display minutes only
                            timeLeft.setText(minutesDifference + "min");
                        } else {
                            // Set the text to display hours and minutes
                            timeLeft.setText(hoursDifference + "h " + minutesDifference + "min");
                        }
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                CardView cardView = new CardView(requireContext());
                CardView.LayoutParams cardViewParams = new CardView.LayoutParams(
                        CardView.LayoutParams.MATCH_PARENT,
                        CardView.LayoutParams.WRAP_CONTENT
                );
                cardViewParams.setMargins(16, 16, 16, 16);
                cardView.setLayoutParams(cardViewParams);

                cardView.setCardElevation(8);
                cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
                cardView.setRadius(50);
                cardView.setBackgroundResource(R.drawable.cardview_border);

                LinearLayout linearLayout = new LinearLayout(requireContext());
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);


                RelativeLayout relativeLayout = new RelativeLayout(requireContext());
                RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                relativeLayout.setLayoutParams(relativeLayoutParams);
                relativeLayout.setPadding(50, 30, 50, 30);


                TextView textView1 = new TextView(requireContext());
                textView1.setLayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                ));

                switch (category){
                    case "0":
                        colorView.setBackgroundColor(Color.parseColor("#8B0000"));
                        textView1.setText("Exam");
                        break;

                    case "1":
                        colorView.setBackgroundColor(Color.parseColor("#4CAF50"));
                        textView1.setText("Assignment");

                        break;

                    case "2":
                        colorView.setBackgroundColor(Color.parseColor("#9C27B0"));
                        textView1.setText("Lab");
                        break;

                    case "3":
                        colorView.setBackgroundColor(Color.parseColor("#ED9C24"));
                        textView1.setText("Workshop");
                        break;

                    case "4":
                        colorView.setBackgroundColor(Color.parseColor("#FF5722"));
                        textView1.setText("Project");
                        break;

                    case "5":
                        colorView.setBackgroundColor(Color.parseColor("#2196F3"));
                        textView1.setText("Lecture");
                        break;

                    case "6":
                        colorView.setBackgroundColor(Color.parseColor("#212121"));
                        textView1.setText("Other");
                        break;

                    default:
                        break;
                }

                textView1.setTextSize(15);
                textView1.setId(View.generateViewId());


                TextView textView2 = new TextView(requireContext());
                textView2.setLayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                ));
                textView2.setText(due);
                textView2.setTextSize(10);
                textView2.setId(View.generateViewId());

                TextView textView3 = new TextView(requireContext());
                textView3.setLayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                ));
                textView3.setText(title);
                textView3.setTextSize(20);
                textView3.setTypeface(null, Typeface.BOLD);
                textView3.setId(View.generateViewId());
                textView3.setTextColor(Color.parseColor("#000000"));

                TextView textView4 = new TextView(requireContext());
                textView4.setLayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                ));
                textView4.setText(description);
                textView4.setTextSize(12);
                textView4.setId(View.generateViewId());

                RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) textView1.getLayoutParams();
                params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params1.addRule(RelativeLayout.ALIGN_PARENT_START);
                params1.setMargins(0, 0, 0, 50);
                textView1.setLayoutParams(params1);

                RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) textView2.getLayoutParams();
                params2.addRule(RelativeLayout.ALIGN_PARENT_END);
                params2.setMargins(160, 0, 0, 20);
                textView2.setLayoutParams(params2);

                RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) textView3.getLayoutParams();
                params3.addRule(RelativeLayout.BELOW, textView2.getId());
                params3.addRule(RelativeLayout.ALIGN_PARENT_START);
                params3.setMargins(0, 16, 0, 0);
                textView3.setLayoutParams(params3);

                RelativeLayout.LayoutParams params4 = (RelativeLayout.LayoutParams) textView4.getLayoutParams();
                params4.addRule(RelativeLayout.BELOW, textView3.getId());
                params4.addRule(RelativeLayout.ALIGN_PARENT_START);
                textView4.setLayoutParams(params4);

                relativeLayout.addView(textView1);
                relativeLayout.addView(textView2);
                relativeLayout.addView(textView3);
                relativeLayout.addView(textView4);

                linearLayout.addView(colorView);
                linearLayout.addView(relativeLayout);
                cardView.addView(linearLayout);

                cards.addView(cardView);

            }
        }
    }
}


