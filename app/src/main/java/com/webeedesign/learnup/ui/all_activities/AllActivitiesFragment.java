package com.webeedesign.learnup.ui.all_activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.webeedesign.learnup.ui.database.DatabaseHelper;
import com.webeedesign.learnup.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class AllActivitiesFragment extends Fragment {
    private ScrollView noEvent, cardContainer;

    private LinearLayout cards;
    private HashMap<Integer, String[]> allData;

    private boolean hideOverDue = false;

    private String selectedValue = "0";
    private TextView taskCount;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_activities, container, false);

        allData = DatabaseHelper.getDataForMonth(getContext());
        cardContainer = view.findViewById(R.id.card_container);
        cards = view.findViewById(R.id.cards);
        noEvent = view.findViewById(R.id.no_event);
        taskCount = view.findViewById(R.id.taskCount);
        Spinner sortList = view.findViewById(R.id.category_list);

        CompoundButton.OnCheckedChangeListener hideOverDueListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hideOverDue = isChecked;
                createCardViews();
                getChildPresence();
            }
        };

        Switch switchHideOverDue = view.findViewById(R.id.hideOverDue);
        switchHideOverDue.setOnCheckedChangeListener(hideOverDueListener);

        String[] sortItems = getResources().getStringArray(R.array.sortList);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,sortItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sortList.setAdapter(adapter);

        sortList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedValue = String.valueOf(position);
                createCardViews();
                getChildPresence();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        if (allData.size() > 0){
            noEvent.setVisibility(View.GONE);
            cardContainer.setVisibility(View.VISIBLE);
        }

        getTaskCount();
        createCardViews();

        return view;


    }

    @SuppressLint("SetTextI18n")
    public void createCardViews() {
        cards.removeAllViews();
        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(System.currentTimeMillis());

        for (Map.Entry<Integer, String[]> entry : allData.entrySet()) {
            String[] data = entry.getValue();

            Integer key = entry.getKey();
            String category = data[1];
            String date = data[4];
            String time = data[5];
            String title = data[2];
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
                assert currentDatetime != null;
                currentCalendar.setTime(currentDatetime);

                Calendar targetCalendar = Calendar.getInstance();
                assert targetDatetime != null;
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

            switch (category){
                case "0":
                    if(!(selectedValue.equals("0") || selectedValue.equals("1"))){
                        continue;
                    }
                    colorView.setBackgroundColor(Color.parseColor("#8B0000"));
                    textView1.setText("Exam");
                    break;

                case "1":
                    if(!(selectedValue.equals("0") || selectedValue.equals("2"))){
                        continue;
                    }
                    colorView.setBackgroundColor(Color.parseColor("#4CAF50"));
                    textView1.setText("Assignment");
                    break;

                case "2":
                    if(!(selectedValue.equals("0") || selectedValue.equals("3"))){
                        continue;
                    }
                    colorView.setBackgroundColor(Color.parseColor("#9C27B0"));
                    textView1.setText("Lab");
                    break;

                case "3":
                    if(!(selectedValue.equals("0") || selectedValue.equals("4"))){
                        continue;
                    }
                    colorView.setBackgroundColor(Color.parseColor("#ED9C24"));
                    textView1.setText("Workshop");
                    break;

                case "4":
                    if(!(selectedValue.equals("0") || selectedValue.equals("5"))){
                        continue;
                    }
                    colorView.setBackgroundColor(Color.parseColor("#FF5722"));
                    textView1.setText("Project");
                    break;

                case "5":
                    if(!(selectedValue.equals("0") || selectedValue.equals("6"))){
                        continue;
                    }
                    colorView.setBackgroundColor(Color.parseColor("#2196F3"));
                    textView1.setText("Lecture");
                    break;

                case "6":
                    if(!(selectedValue.equals("0") || selectedValue.equals("7"))){
                        continue;
                    }
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
            cards.addView(cardView);
        }
    }

    @SuppressLint("SetTextI18n")
    private void getChildPresence(){
        if (cards.getChildCount() > 0) {
            noEvent.setVisibility(View.GONE);
            cardContainer.setVisibility(View.VISIBLE);
        } else {
            noEvent.setVisibility(View.VISIBLE);
            cardContainer.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void getTaskCount(){
        if (cards.getChildCount() > 0) {
            taskCount.setVisibility(View.VISIBLE);
            taskCount.setText("Total tasks: " + cards.getChildCount() );
        } else {
            taskCount.setVisibility(View.GONE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        allData = DatabaseHelper.getDataForMonth(getContext());
        createCardViews();
        getTaskCount();
        getChildPresence();

    }

}