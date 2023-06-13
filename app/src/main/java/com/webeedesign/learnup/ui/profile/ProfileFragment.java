package com.webeedesign.learnup.ui.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.webeedesign.learnup.R;
import com.webeedesign.learnup.ui.CurrentUser;
import com.webeedesign.learnup.ui.database.DatabaseHelper;

public class ProfileFragment extends Fragment {

    private ImageView profilePhoto;

    private TextView tasks, todos, user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());

        ImageView profileBackground = rootView.findViewById(R.id.profileBackground);
        profilePhoto = rootView.findViewById(R.id.profilePhoto);
        tasks = rootView.findViewById(R.id.tasks);
        todos = rootView.findViewById(R.id.todos);
        user = rootView.findViewById(R.id.user);
        Button editProfileBtn = rootView.findViewById(R.id.editProfileBtn);

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToEditProfile();
            }
        });

        profileBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
        setUserData();

        return rootView;

    }

    private void navigateToEditProfile(){
        Intent intent = new Intent(getContext(), EditProfile.class);
        startActivity(intent);
    }

    @SuppressLint("ResourceType")
    private void setUserData(){

        user.setText("Hi, " + CurrentUser.getInstance().getUserName());

        String gender = DatabaseHelper.getProfileGenderInfo(getContext());

        if (gender.equals("Male")){
            profilePhoto.setImageResource(R.raw.male);
        }else{
            profilePhoto.setImageResource(R.raw.female);
        }

        int[] todoCount = DatabaseHelper.getProfileTodoInfo(getContext());

        int totalTodos = todoCount[0];
        int checkedTodoCount = todoCount[1];

        int taskCount = DatabaseHelper.getProfileTaskInfo(getContext());

        if (totalTodos > 0){
            todos.setText(checkedTodoCount + "/" + totalTodos);
        }else {
            todos.setTextSize(12);
            todos.setText("No todos yet");
        }

        if (taskCount > 0){
            tasks.setText(String.valueOf(taskCount));
        }else {
            tasks.setTextSize(12);
            tasks.setText("No tasks yet");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUserData();
    }

}