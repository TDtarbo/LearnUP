package com.webeedesign.learnup.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.webeedesign.learnup.R;
import com.webeedesign.learnup.ui.CurrentUser;
import com.webeedesign.learnup.ui.all_activities.TaskInfo;
import com.webeedesign.learnup.ui.database.DatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DashboardFragment extends Fragment {


    private int userId;

    private DatabaseHelper LearnUPdb;

    private TextView user, taskAvailability, taskCount;

    private ImageView greetingImage;

    private String formattedDate;


    private HashMap<Integer, String[]> todayData;
    private LinearLayout todayCardContainer, upComingCardContainer, noEventsToday, noUpComingEvents;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        LearnUPdb = new DatabaseHelper(getContext());
        getTodayDate();
        todayData = DatabaseHelper.getDataForNextEightDays(getContext());

        FloatingActionButton addTask = rootView.findViewById(R.id.add_event);
        greetingImage = rootView.findViewById(R.id.greetingImage);
        user = rootView.findViewById(R.id.greeting);
        todayCardContainer = rootView.findViewById(R.id.todayCardContainer);
        upComingCardContainer = rootView.findViewById(R.id.upComingCardContainer);
        taskAvailability = rootView.findViewById(R.id.taskAvailability);
        taskCount = rootView.findViewById(R.id.taskCount);
        noEventsToday = rootView.findViewById(R.id.noEventsToday);
        noUpComingEvents = rootView.findViewById(R.id.noUpComingEvents);

        userId = CurrentUser.getInstance().getUserId();
        user.setText(generateGreeting() +", " + CurrentUser.getInstance().getUserName());


        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddTask.class);
                startActivity(intent);
            }
        });

        createCardViews();

        return rootView;
    }




    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String generateGreeting() {
        LocalTime currentTime = LocalTime.now();

        if (currentTime.isAfter(LocalTime.of(5, 0)) && currentTime.isBefore(LocalTime.of(12, 0))) {
            greetingImage.setImageResource(R.raw.greeting_morning);
            greetingImage.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(400)
                    .setStartDelay(500)
                    .start();
            return "Good morning!";
        } else if (currentTime.isAfter(LocalTime.of(12, 0)) && currentTime.isBefore(LocalTime.of(18, 0))) {
            greetingImage.setImageResource(R.raw.greeting_afternoon);
            greetingImage.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(400)
                    .setStartDelay(500)
                    .start();
            return "Good afternoon!";
        } else {
            greetingImage.setImageResource(R.raw.greeting_evening);
            greetingImage.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(400)
                    .setStartDelay(500)
                    .start();
            return "Good evening!";

        }
    }

    public void getTodayDate(){

            Date currentDate = new Date();
            // Define the desired date format
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            // Format the current date
            formattedDate = dateFormat.format(currentDate);
    }

    @SuppressLint("SetTextI18n")
    public void createCardViews() {
        todayCardContainer.removeAllViews();
        upComingCardContainer.removeAllViews();

        taskAvailability.setText("You don't have tasks Today");
        taskCount.setVisibility(View.GONE);
        if (todayData.size() > 0){


            Calendar today = Calendar.getInstance();
            today.setTimeInMillis(System.currentTimeMillis());

            for (Map.Entry<Integer, String[]> entry : todayData.entrySet()) {
                String[] data = entry.getValue();

                if (todayData.size() > 0) {

                }
                Integer key = entry.getKey();
                String category = data[1];
                String date = data[4];
                String time = data[5];
                String title = data[2];
                String status = data[9];
                String description = data[3];
                String due = date + " \n" + time;
                String venue = date + " " + time;


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


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                String currentDatetimeString = sdf.format(new Date());

                try {
                    // Parse the target date and time
                    Date currentDatetime = sdf.parse(currentDatetimeString);
                    Date targetDatetime = sdf.parse(venue);

                    // Create calendar instances for the current datetime and target datetime
                    Calendar currentCalendar = Calendar.getInstance();
                    currentCalendar.setTime(currentDatetime);

                    Calendar targetCalendar = Calendar.getInstance();
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
                        } else if (hoursDifference < 1) {
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
                cardViewParams.setMargins(0, 10, 0, 10);
                cardView.setLayoutParams(cardViewParams);

                cardView.setId(key);
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

                switch (category) {
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
                textView3.setMaxLines(1);
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
                textView4.setMaxLines(1);
                textView4.setId(View.generateViewId());

                boolean isChecked = false;

                switch (status){
                    case "0":
                        isChecked = false;
                        break;
                    case "1":
                        isChecked = true;
                    default:
                        break;
                }

                CheckBox checkBox = new CheckBox(requireContext());
                checkBox.setLayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                ));
                checkBox.setTextSize(12);
                checkBox.setId(View.generateViewId());
                checkBox.setChecked(isChecked);

                checkBox.setOnCheckedChangeListener((buttonView, isChecked1) -> {

                    boolean isInserted;

                    if(status.equals("0")){
                        isInserted = LearnUPdb.updateDashboardCheckBox(key, 1);
                    }else {
                        isInserted = LearnUPdb.updateDashboardCheckBox(key, 0);
                    }


                });

                RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) textView1.getLayoutParams();
                params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params1.addRule(RelativeLayout.ALIGN_PARENT_START);
                params1.setMargins(0, 0, 0, 50);
                textView1.setLayoutParams(params1);

                RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) textView2.getLayoutParams();
                params2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params2.addRule(RelativeLayout.ALIGN_PARENT_END);
                params2.setMargins(0, 0, 0, 20);
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

                RelativeLayout.LayoutParams params5 = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                params5.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params5.addRule(RelativeLayout.ALIGN_PARENT_END);
                params5.setMargins(0, 0, 0, 50);
                checkBox.setLayoutParams(params5);


                relativeLayout.addView(textView1);
                relativeLayout.addView(textView2);
                relativeLayout.addView(textView3);
                relativeLayout.addView(textView4);
                relativeLayout.addView(checkBox);

                linearLayout.addView(colorView);
                linearLayout.addView(relativeLayout);
                cardView.addView(linearLayout);

                cardView.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), TaskInfo.class);

                    String location = data[6];
                    String link = data[7];

                    intent.putExtra("cardId", v.getId());
                    intent.putExtra("category", Integer.parseInt(category));
                    intent.putExtra("date", date);
                    intent.putExtra("time", time);
                    intent.putExtra("title", title);
                    intent.putExtra("description", description);
                    intent.putExtra("location", location);
                    intent.putExtra("link", link);

                    startActivity(intent);
                });

                if (formattedDate.equals(date)) {
                    todayCardContainer.addView(cardView);
                } else {
                    upComingCardContainer.addView(cardView);
                }

                if (todayCardContainer.getChildCount() <= 0) {
                    taskAvailability.setText("You don't have tasks Today");
                    taskCount.setVisibility(View.GONE);
                    noEventsToday.setVisibility(View.VISIBLE);
                } else {
                    taskAvailability.setText("You've got");
                    taskCount.setVisibility(View.VISIBLE);

                    if (todayCardContainer.getChildCount() == 1) {
                        taskCount.setText(todayCardContainer.getChildCount() + " Task today");
                    } else {
                        taskCount.setText(todayCardContainer.getChildCount() + " Tasks today");
                    }
                    noEventsToday.setVisibility(View.GONE);
                }

                if (upComingCardContainer.getChildCount() <= 0) {
                    noUpComingEvents.setVisibility(View.VISIBLE);
                } else {
                    noUpComingEvents.setVisibility(View.GONE);
                }

            }

        }else{

            noEventsToday.setVisibility(View.VISIBLE);
            noUpComingEvents.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        todayData = DatabaseHelper.getDataForNextEightDays(getContext());
        createCardViews();
    }



}