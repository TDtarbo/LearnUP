package com.webeedesign.learnup.ui.todo_list;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.webeedesign.learnup.ui.all_activities.TaskInfo;
import com.webeedesign.learnup.ui.database.DatabaseHelper;
import com.webeedesign.learnup.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ToDoListFragment extends Fragment {
    private PopupWindow popupWindow;

    private LinkedHashMap<Integer, String[]> listNames, listItems;

    private LinearLayout cards , task, buttonLayout;

    private DatabaseHelper LearnUPdb;

    private String today;

    private int cardId;

    private Button addBtn, deleteBtn;

    private ScrollView cardContainer, noEvent;

    private HashMap <Integer, Boolean> isExpanded;

    private final HashMap <Integer, Integer> spanValues = new HashMap<>();

    private final HashMap <Integer, Integer> newSpanValues = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_do_list, container, false);
        FloatingActionButton addList = view.findViewById(R.id.addList);
        LearnUPdb = new DatabaseHelper(getContext());

        addList.setOnClickListener(view1 -> openAddTaskListNamePopUp());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        today = dateFormat.format(currentDate);

        cardContainer = view.findViewById(R.id.card_container);
        cards = view.findViewById(R.id.cards);
        noEvent = view.findViewById(R.id.no_event);

        listNames = DatabaseHelper.getTodoLists(getContext());

        if (listNames.size() > 0){
            noEvent.setVisibility(View.GONE);
            cardContainer.setVisibility(View.VISIBLE);
        }

        isExpanded = new HashMap<>();
        createCardViews();

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void createCardViews() {

        for (Map.Entry<Integer, String[]> entry : listNames.entrySet()) {
            String[] data = entry.getValue();

            Integer key = entry.getKey();
            String listName = data[0];
            String dateCreated = data[1];

            int[] progressInfo = DatabaseHelper.getProgressInfo(getContext() , key);

            int totalItems = progressInfo[0];
            int checkedItemCount = progressInfo[1];

            int percentageValue;

            if (totalItems > 0) {
                percentageValue = (int) Math.round((double) checkedItemCount / totalItems * 100);
            } else {
                percentageValue = 0;
            }


            CardView cardView = new CardView(requireContext());
            CardView.LayoutParams cardViewParams = new CardView.LayoutParams(
                    CardView.LayoutParams.MATCH_PARENT,
                    CardView.LayoutParams.WRAP_CONTENT
            );
            cardViewParams.setMargins(16, 16, 16, 50);
            cardView.setLayoutParams(cardViewParams);

            cardView.setId(key);
            cardView.setCardElevation(8);
            cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
            cardView.setRadius(20);
            cardView.setBackgroundResource(R.drawable.cardview_border);

            RelativeLayout relativeLayout = new RelativeLayout(requireContext());
            RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            relativeLayout.setLayoutParams(relativeLayoutParams);
            relativeLayout.setPadding(50, 30, 50, 30);

            LinearLayout linearLayout = new LinearLayout(requireContext());
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setId(View.generateViewId());


            ProgressBar progressBar = new ProgressBar(requireContext(), null, android.R.attr.progressBarStyleHorizontal);
            progressBar.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.9f // This sets the weight of the ProgressBar to 90% of the parent's width
            ));

            progressBar.setProgress(percentageValue);
            progressBar.setId(View.generateViewId());

            TextView name = new TextView(requireContext());
            name.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            ));
            name.setText(listName);
            name.setTextSize(20);
            name.setMaxLines(1);
            name.setTypeface(null, Typeface.BOLD);
            name.setId(View.generateViewId());
            name.setTextColor(Color.parseColor("#000000"));

            TextView date = new TextView(requireContext());
            date.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            ));
            date.setText("Created on:\n"+dateCreated);
            date.setTextSize(10);
            date.setId(View.generateViewId());

            TextView percentage = new TextView(requireContext());
            percentage.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.1f // This sets the weight of the TextView to 20% of the parent's width
            ));
            percentage.setText(percentageValue + "%");
            percentage.setTextSize(10);
            percentage.setGravity(Gravity.END);
            percentage.setId(View.generateViewId());


            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) name.getLayoutParams();
            params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params1.addRule(RelativeLayout.ALIGN_PARENT_START);
            params1.setMargins(0, 0, 0, 50);
            name.setLayoutParams(params1);

            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) date.getLayoutParams();
            params2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params2.addRule(RelativeLayout.ALIGN_PARENT_END);
            params2.setMargins(0, 0, 0, 20);
            date.setLayoutParams(params2);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.BELOW, date.getId());
            params.setMargins(0, 25, 0, 0);
            linearLayout.setLayoutParams(params);


            linearLayout.addView(progressBar);
            linearLayout.addView(percentage);
            relativeLayout.addView(name);
            relativeLayout.addView(date);
            relativeLayout.addView(linearLayout);

            cardView.addView(relativeLayout);

            isExpanded.put(key, false);

            //Card Onclick Listener
            cardView.setOnClickListener(v -> cardSpan(isExpanded, linearLayout, relativeLayout, cardView, progressBar, percentage));

            //Add views to CardView
            cards.addView(cardView);
        }
    }


    //New List popup Window
    private void openAddTaskListNamePopUp(){

        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.add_list_popup, null);

        // create the popup window
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // add animation to the popup window
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_bottom);
        popupView.startAnimation(animation);

        TextView listName = popupView.findViewById(R.id.listName);
        Button addBtn = popupView.findViewById(R.id.addBtn);
        Button cancelBtn = popupView.findViewById(R.id.cancelBtn);

        //Add new List
        addBtn.setOnClickListener(view -> {
            boolean isInserted;
            try {
                isInserted = LearnUPdb.insertIntoTodoLists(listName.getText().toString(), today);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            if (isInserted){
                Toast.makeText(getContext(), "Task Added!", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
                listNames = DatabaseHelper.getTodoLists(getContext());
                cards.removeAllViews();
                createCardViews();

            }else{
                Toast.makeText(getContext(), "Failed to Add!", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }

            listNames = DatabaseHelper.getTodoLists(getContext());

            if (listNames.size() > 0){
                noEvent.setVisibility(View.GONE);
                cardContainer.setVisibility(View.VISIBLE);
            }
        });

        //NewList cancel button
        cancelBtn.setOnClickListener(view -> {
            Animation slideOutBottom = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_bottom);
            popupView.startAnimation(slideOutBottom);
            slideOutBottom.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation slideOutBottom) {}

                @Override
                public void onAnimationEnd(Animation slideOutBottom) {
                    popupWindow.dismiss();
                }

                @Override
                public void onAnimationRepeat(Animation slideOutBottom) {}
            });
        });


        // show the popup window
        View parent = requireView().getRootView();
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);

    }


    //Add Task popup Window
    @SuppressLint("SetTextI18n")
    private void openAddTaskListItemPopUp(
            LinearLayout linearLayout,
            RelativeLayout relativeLayout,
            CardView cardView ,
            Button addBtn,
            ProgressBar progressBar,
            TextView percentage){

        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.add_list_item_popup, null);

        // create the popup window
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // add animation to the popup window
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_bottom);
        popupView.startAnimation(animation);

        TextView itemName = popupView.findViewById(R.id.listName);
        Button addTaskBtn = popupView.findViewById(R.id.addBtn);
        Button cancelBtn = popupView.findViewById(R.id.cancelBtn);

        //Add task item button handler
        addTaskBtn.setOnClickListener(view -> {
            boolean isInserted;
            try {
                isInserted = LearnUPdb.insertIntoTodos(itemName.getText().toString(), cardId, 0);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            if (isInserted){

                //Refresh the card layout
                task.removeAllViews();
                relativeLayout.removeView(addBtn);
                relativeLayout.removeView(task);

                Toast.makeText(getContext(), "Task Added!", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
                listItems = DatabaseHelper.getTodoItems(getContext() , cardId);

                LinearLayout buttonLayout = new LinearLayout(getContext());
                buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                buttonLayout.setLayoutParams(buttonLayoutParams);
                buttonLayout.setId(View.generateViewId());

                RelativeLayout.LayoutParams buttonLayoutLayoutParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                buttonLayoutLayoutParams.addRule(RelativeLayout.BELOW, linearLayout.getId());
                buttonLayoutLayoutParams.setMargins(0, 50, 0, 0);
                buttonLayout.setLayoutParams(buttonLayoutLayoutParams);

// Create the "Add a task" button
                Button newAddBtn = new Button(getContext());
                newAddBtn.setText("Add a task");
                newAddBtn.setTextSize(12);
                newAddBtn.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                newAddBtn.setId(View.generateViewId());

                newAddBtn.setOnClickListener(view1 -> openAddTaskListItemPopUp(linearLayout, relativeLayout, cardView, newAddBtn, progressBar, percentage));

// Create the "Delete list" button
                Button newDeleteBtn = new Button(getContext());
                newDeleteBtn.setText("Delete list");
                newDeleteBtn.setTextSize(12);
                newDeleteBtn.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                newDeleteBtn.setId(View.generateViewId());

                newDeleteBtn.setOnClickListener(view1 -> deleteTaskList(cardId));

                task = new LinearLayout(requireContext());
                task.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                task.setOrientation(LinearLayout.VERTICAL);
                task.setId(View.generateViewId());

                listItems = DatabaseHelper.getTodoItems(getContext() , cardId);

                if (listItems.size() > 0){

                    for (Map.Entry<Integer, String[]> entry : listItems.entrySet()) {
                        String[] data = entry.getValue();

                        Integer key = entry.getKey();
                        String itemName1 = data[0];
                        String status = data[1];

                        boolean isChecked = "1".equals(status);

                        CheckBox checkBox = new CheckBox(requireContext());
                        checkBox.setText(itemName1);
                        checkBox.setChecked(isChecked);

                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.addRule(RelativeLayout.BELOW, newAddBtn.getId());
                        params.setMargins(0, 25, 0, 0);
                        task.setLayoutParams(params);

                        //Update progressbar and progress percentage
                        int[] progressInfo = DatabaseHelper.getProgressInfo(getContext() , cardId);

                        int totalItems = progressInfo[0];
                        int checkedItemCount = progressInfo[1];

                        int percentageValue;

                        if (totalItems > 0) {
                            percentageValue = (int) Math.round((double) checkedItemCount / totalItems * 100);
                        } else {
                            percentageValue = 0;
                        }

                        progressBar.setProgress(percentageValue);
                        percentage.setText(percentageValue + "%");

                        //Set new Checkbox event Listener
                        checkBox.setOnCheckedChangeListener((buttonView, newCheckBoxIsChecked) -> {

                            if (newCheckBoxIsChecked) {
                                boolean newDataIsInserted;
                                newDataIsInserted = LearnUPdb.updateCheckBox(key, 1);

                                if (newDataIsInserted){

                                    int[] progressInfo1 = DatabaseHelper.getProgressInfo(getContext() , cardId);

                                    int totalItems1 = progressInfo1[0];
                                    int checkedItemCount1 = progressInfo1[1];

                                    int percentageValue1;

                                    if (totalItems1 > 0) {
                                        percentageValue1 = (int) Math.round((double) checkedItemCount1 / totalItems1 * 100);
                                    } else {
                                        percentageValue1 = 0;
                                    }

                                    progressBar.setProgress(percentageValue1);
                                    percentage.setText(percentageValue1 + "%");

                                }else{
                                    Toast.makeText(getContext(), "Error occur white updating the task", Toast.LENGTH_SHORT).show();
                                }


                            } else {
                                boolean newDataIsInserted;
                                newDataIsInserted = LearnUPdb.updateCheckBox(key, 0);

                                if (newDataIsInserted){
                                    int[] progressInfo1 = DatabaseHelper.getProgressInfo(getContext() , cardId);

                                    int totalItems1 = progressInfo1[0];
                                    int checkedItemCount1 = progressInfo1[1];

                                    int percentageValue1;

                                    if (totalItems1 > 0) {
                                        percentageValue1 = (int) Math.round((double) checkedItemCount1 / totalItems1 * 100);
                                    } else {
                                        percentageValue1 = 0;
                                    }

                                    progressBar.setProgress(percentageValue1);
                                    percentage.setText(percentageValue1 + "%");

                                }else{
                                    Toast.makeText(getContext(), "Error occur white updating the task", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        //Add the checkbox to the layout
                        task.addView(checkBox);

                    }
                }

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                params.addRule(RelativeLayout.BELOW, buttonLayout.getId());
                params.setMargins(0, 25, 0, 0);
                task.setLayoutParams(params);

                //add updated layout
                relativeLayout.addView(task);
                buttonLayout.addView(newAddBtn);
                buttonLayout.addView(newDeleteBtn);

// Add the button layout to the parent layout
                relativeLayout.addView(buttonLayout);

                // Measure the height of the new content
                task.measure(
                        View.MeasureSpec.makeMeasureSpec(cardView.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                //add new height
                newSpanValues.put(cardId, task.getMeasuredHeight() + 210);

                // Animate the CardView to expand to the new height
                ValueAnimator animator = ValueAnimator.ofInt(cardView.getHeight(), cardView.getHeight() + newSpanValues.get(cardId) - spanValues.get(cardId));
                animator.setDuration(300);

                //Animate the card view span
                animator.addUpdateListener(valueAnimator -> {
                    int value = (int) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
                    layoutParams.height = value;
                    cardView.setLayoutParams(layoutParams);
                });

                spanValues.put(cardId, newSpanValues.get(cardId));
                animator.start();


            }else{
                Toast.makeText(getContext(), "Failed to Add!", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });

        //cancel item btn handler
        cancelBtn.setOnClickListener(view -> {
            Animation slideOutBottom = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_bottom);
            popupView.startAnimation(slideOutBottom);
            slideOutBottom.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation slideOutBottom) {}

                @Override
                public void onAnimationEnd(Animation slideOutBottom) {
                    popupWindow.dismiss();
                }

                @Override
                public void onAnimationRepeat(Animation slideOutBottom) {}
            });
        });

        // show the popup window
        View parent = requireView().getRootView();
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);

    }





    //Card span Handler
    @SuppressLint("SetTextI18n")
    private void cardSpan(
            HashMap <Integer, Boolean> isExpanded,
            LinearLayout linearLayout,
            RelativeLayout relativeLayout,
            CardView cardView,
            ProgressBar progressBar,
            TextView percentage ){


        //get selected card viewId
        cardId = cardView.getId();

        //Generate the layout
        if (Boolean.FALSE.equals(isExpanded.get(cardView.getId()))) {

            buttonLayout = new LinearLayout(getContext());
            buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            buttonLayout.setLayoutParams(buttonLayoutParams);
            buttonLayout.setId(View.generateViewId());

            RelativeLayout.LayoutParams buttonLayoutLayoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            buttonLayoutLayoutParams.addRule(RelativeLayout.BELOW, linearLayout.getId());
            buttonLayoutLayoutParams.setMargins(0, 50, 0, 0);
            buttonLayout.setLayoutParams(buttonLayoutLayoutParams);

// Create the "Add a task" button
            addBtn = new Button(getContext());
            addBtn.setText("Add a task");
            addBtn.setTextSize(12);
            addBtn.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

// Create the "Delete list" button
            deleteBtn = new Button(getContext());
            deleteBtn.setText("Delete list");
            deleteBtn.setTextSize(12);
            deleteBtn.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            deleteBtn.setOnClickListener(view -> deleteTaskList(cardId));


            task = new LinearLayout(requireContext());
            task.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            task.setOrientation(LinearLayout.VERTICAL);
            task.setId(View.generateViewId());


            //Navigate to add new Task
            addBtn.setOnClickListener(view -> openAddTaskListItemPopUp(linearLayout, relativeLayout, cardView, addBtn, progressBar, percentage));

            //Call database function to get Items
            listItems = DatabaseHelper.getTodoItems(getContext() , cardId);

            if (listItems.size() > 0){

                for (Map.Entry<Integer, String[]> entry : listItems.entrySet()) {
                    String[] data = entry.getValue();

                    Integer key = entry.getKey();
                    String itemName = data[0];
                    String status = data[1];

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
                    checkBox.setText(itemName);
                    checkBox.setChecked(isChecked);

                    //set checkbox Event Listeners
                    checkBox.setOnCheckedChangeListener((buttonView, isChecked1) -> {
                        if (isChecked1) {
                            boolean isInserted;
                            isInserted = LearnUPdb.updateCheckBox(key, 1);

                            if (isInserted){

                                int[] progressInfo = DatabaseHelper.getProgressInfo(getContext() , cardId);

                                int totalItems = progressInfo[0];
                                int checkedItemCount = progressInfo[1];

                                int percentageValue;

                                if (totalItems > 0) {
                                    percentageValue = (int) Math.round((double) checkedItemCount / totalItems * 100);
                                } else {
                                    percentageValue = 0;
                                }

                                progressBar.setProgress(percentageValue);
                                percentage.setText(percentageValue + "%");

                            }else{
                                Toast.makeText(getContext(), "Error occur white updating the task", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            boolean isInserted;
                            isInserted = LearnUPdb.updateCheckBox(key, 0);

                            if (isInserted){
                                int[] progressInfo = DatabaseHelper.getProgressInfo(getContext() , cardId);

                                int totalItems = progressInfo[0];
                                int checkedItemCount = progressInfo[1];

                                int percentageValue;

                                if (totalItems > 0) {
                                    percentageValue = (int) Math.round((double) checkedItemCount / totalItems * 100);
                                } else {
                                    percentageValue = 0;
                                }

                                progressBar.setProgress(percentageValue);
                                percentage.setText(percentageValue + "%");

                            }else{
                                Toast.makeText(getContext(), "Error occur white updating the task", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.addRule(RelativeLayout.BELOW, buttonLayout.getId());
                    params.setMargins(0, 25, 0, 0);
                    task.setLayoutParams(params);

                    task.addView(checkBox);

                }
            }

            //Create finalized layout
            relativeLayout.addView(task);
            // Add the buttons to the linear layout
            buttonLayout.addView(addBtn);
            buttonLayout.addView(deleteBtn);

// Add the button layout to the parent layout
            relativeLayout.addView(buttonLayout);

            // Measure the height of the new content
            task.measure(View.MeasureSpec.makeMeasureSpec(cardView.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            spanValues.put(cardId,task.getMeasuredHeight() + 210);


            // Animate the CardView to expand to the new height
            ValueAnimator animator = ValueAnimator.ofInt(cardView.getHeight(), cardView.getHeight() + spanValues.get(cardId));
            animator.setDuration(300);


            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int value = (int) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
                    layoutParams.height = value;
                    cardView.setLayoutParams(layoutParams);
                }
            });
            animator.start();
            isExpanded.put(cardId,true);

        }else{

            task.measure(View.MeasureSpec.makeMeasureSpec(cardView.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            // Animate the CardView to shrink to the new height
            ValueAnimator animator = ValueAnimator.ofInt(cardView.getHeight(), cardView.getHeight() - spanValues.get(cardId));
            animator.setDuration(300);
            animator.addUpdateListener(valueAnimator -> {
                int value = (int) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
                layoutParams.height = value;
                cardView.setLayoutParams(layoutParams);
            });

            animator.start();
            relativeLayout.removeView(buttonLayout);
            task.removeAllViews();
            isExpanded.put(cardId,false);

        }


    }

    public void deleteTaskList(int cardId){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this task? This action cannot be undone.");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Integer deleteTodoList = LearnUPdb.deleteTodoList(String.valueOf(cardId));
                Integer deleteTodos = LearnUPdb.deleteTodoItems(String.valueOf(cardId));

                if (deleteTodoList>0 || deleteTodos>0) {
                    Toast.makeText(getContext(), "ToDo list Deleted", Toast.LENGTH_LONG).show();
                    listNames = DatabaseHelper.getTodoLists(getContext());
                    cards.removeAllViews();
                    createCardViews();
                    if (cards.getChildCount() <= 0){
                        noEvent.setVisibility(View.VISIBLE);
                        cardContainer.setVisibility(View.GONE);
                    }else {
                        noEvent.setVisibility(View.GONE);
                        cardContainer.setVisibility(View.VISIBLE);
                    }
                }else {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
                }

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
}
