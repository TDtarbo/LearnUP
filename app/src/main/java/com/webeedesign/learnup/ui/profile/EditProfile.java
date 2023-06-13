package com.webeedesign.learnup.ui.profile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.webeedesign.learnup.ui.CurrentUser;
import com.webeedesign.learnup.ui.dashboard.DashboardFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.webeedesign.learnup.R;
import com.webeedesign.learnup.ui.dashboard.DashboardFragment;
import com.webeedesign.learnup.ui.database.DatabaseHelper;
import com.webeedesign.learnup.ui.login.LoginActivity;

import java.text.ParseException;
import java.util.HashMap;

public class EditProfile extends AppCompatActivity {

    private DatabaseHelper LearnUPdb;
    private TextInputEditText username, password;

    private LinearLayout alertMessage;

    private Button updateProfileBtn;

    private TextInputLayout passwordLayout;
    private RadioGroup radioGroup;

    private RadioButton radioButton;
    private String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        LearnUPdb = new DatabaseHelper(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        updateProfileBtn = findViewById(R.id.updateProfileBtn);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        alertMessage = findViewById(R.id.alertMessage);
        passwordLayout = findViewById(R.id.passwordLayout);
        radioGroup = findViewById(R.id.radio_group);


        updateProfileBtn.setOnClickListener(this::hideKeyBoard);



        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setGender(checkedId);
            }
        });


    }

    private void setGender(int checkedId){
        radioButton = findViewById(checkedId);
        gender = radioButton.getText().toString();
    }

    private void hideKeyBoard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(EditProfile.this.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        try {
            validateSignUp();
            CurrentUser.getInstance().setUserName(username.getText().toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    private void validateSignUp() throws ParseException {

        alertMessage.animate()
                .alpha(0f)
                .translationY(-100f)
                .setDuration(300)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        alertMessage.setVisibility(View.GONE);
                    }
                })
                .start();

        username.setError(null);
        password.setError(null);

        if (gender == null){
            Toast.makeText(this, "Select your gender", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(username.getText().toString())) {
            username.setError("Please enter a username");
        }
        // Check if the password field is empty
        else if(TextUtils.isEmpty(password.getText().toString())) {

            username.setError(null);
            ;
            passwordLayout.setPasswordVisibilityToggleEnabled(false);
            password.setError("Please enter a password");

            password.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Do nothing
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Do nothing
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().isEmpty()) {
                        passwordLayout.setPasswordVisibilityToggleEnabled(false);
                    } else {
                        password.setError(null);
                        passwordLayout.setPasswordVisibilityToggleEnabled(true);
                    }
                }
            });

        } else if (!(password.getText().toString().length() == 8)) {

            passwordLayout.setError("Password must be 8 characters long");

            password.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Do nothing
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    passwordLayout.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });// Show error message


        }else {

            passwordLayout.setError(null);

            boolean isUserExist = LearnUPdb.isUserNameExist(username.getText().toString());

            if (isUserExist){

                alertMessage.setAlpha(0f);
                alertMessage.setTranslationY(-100f);

                alertMessage.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(300)
                        .start();

                alertMessage.setVisibility(View.VISIBLE);

            }else {

                boolean isUpdated = LearnUPdb.updateProfileData(username.getText().toString(), password.getText().toString(), gender);

                if (isUpdated){
                    alertMessage.setVisibility(View.GONE);
                    openLoadingPopUp();

                }else {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }


    private void openLoadingPopUp(){

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.signup_loading_popup, null);

        // create the popup window
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(false);

        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // add animation to the popup window
        Animation animation = AnimationUtils.loadAnimation(EditProfile.this, R.anim.slide_in_bottom);
        popupView.startAnimation(animation);

        View rootView = getWindow().getDecorView().getRootView();
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);

        // Dismiss the popup window after 5 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                popupWindow.dismiss();

                Toast.makeText(EditProfile.this, "Profile updated", Toast.LENGTH_SHORT).show();
                finish();
            }

        }, 3000);

    }

}