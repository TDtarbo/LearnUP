package com.webeedesign.learnup.ui.registration;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.webeedesign.learnup.R;
import com.webeedesign.learnup.ui.database.DatabaseHelper;
import com.webeedesign.learnup.ui.login.LoginActivity;

import java.text.ParseException;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private DatabaseHelper LearnUPdb;
    private TextInputEditText username, password;
    private LinearLayout alertMessage , alert, content;
    private Button signUpBtn, goToLoginBtn;
    private TextInputLayout passwordLayout;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private TextView login;
    private String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        LearnUPdb = new DatabaseHelper(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        alertMessage = findViewById(R.id.alertMessage);
        signUpBtn = findViewById(R.id.signUpBtn);
        content = findViewById(R.id.content);
        alert = findViewById(R.id.alert);
        goToLoginBtn = findViewById(R.id.goToLoginBtn);
        passwordLayout = findViewById(R.id.passwordLayout);
        login = findViewById(R.id.login);
        radioGroup = findViewById(R.id.radio_group);

        animateContentBtn();


        signUpBtn.setOnClickListener(this::hideKeyBoard);
        login.setOnClickListener(view -> navigateToLoginPage());






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
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(SignUpActivity.this.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        try {
            validateSignUp();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    private void animateContentBtn(){
        content.setAlpha(0f);
        content.setTranslationY(100f);

        content.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setStartDelay(150)
                .start();

        content.setVisibility(View.VISIBLE);
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

                boolean isAccountCreated = LearnUPdb.insertIntoUsers(username.getText().toString(), password.getText().toString(), gender);

                if (isAccountCreated){
                    alertMessage.setVisibility(View.GONE);
                    openLoadingPopUp();

                }else {
                    Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show();
                }


            }
        }
    }



    private void navigateToLoginPage(){
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
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
        Animation animation = AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.slide_in_bottom);
        popupView.startAnimation(animation);

        View rootView = getWindow().getDecorView().getRootView();
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);

        // Dismiss the popup window after 5 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                popupWindow.dismiss();
                content.setVisibility(View.GONE);

                alert.setAlpha(0f);
                alert.setTranslationY(100f);

                alert.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(300)
                        .start();

                alert.setVisibility(View.VISIBLE);

                goToLoginBtn.setOnClickListener(view -> {

                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
            }
        }, 3000);
    }

}