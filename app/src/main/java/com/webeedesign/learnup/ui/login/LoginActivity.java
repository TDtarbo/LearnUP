package com.webeedesign.learnup.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.webeedesign.learnup.R;
import com.webeedesign.learnup.ui.CurrentUser;
import com.webeedesign.learnup.ui.database.DatabaseHelper;
import com.webeedesign.learnup.ui.navigation_drawer.NavigationDrawerActivity;
import com.webeedesign.learnup.ui.registration.SignUpActivity;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private DatabaseHelper LearnUPdb;
    private TextInputEditText username, password;
    private LinearLayout alertMessage;
    private Button loginBtn;
    private TextInputLayout passwordLayout;
    private TextView signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LearnUPdb = new DatabaseHelper(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        alertMessage = findViewById(R.id.alertMessage);
        loginBtn = findViewById(R.id.loginBtn);
        passwordLayout = findViewById(R.id.passwordLayout);
        signUp = findViewById(R.id.signUp);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToSignUpPage();
            }
        });

        animateLoginBtn();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateLogin();
            }
        });
    }

    private void animateLoginBtn(){
        loginBtn.setAlpha(0f);
        loginBtn.setTranslationY(100f);

        loginBtn.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setStartDelay(500)
                .start();

        loginBtn.setVisibility(View.VISIBLE);
    }

    private void validateLogin(){

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

        if (TextUtils.isEmpty(username.getText().toString())) {
            username.setError("Please enter a username");
        }
// Check if the password field is empty
        else if(TextUtils.isEmpty(password.getText().toString())) {

            username.setError(null);;
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
                    }else {
                        password.setError(null);
                        passwordLayout.setPasswordVisibilityToggleEnabled(true);
                    }
                }
            });

        }else {

            int isUserValid = LearnUPdb.isUserValid(username.getText().toString(), password.getText().toString());

            if (isUserValid > 0){
                alertMessage.setVisibility(View.GONE);

                CurrentUser.getInstance().setUserId(isUserValid);
                CurrentUser.getInstance().setUserName(username.getText().toString());

                Intent intent = new Intent(LoginActivity.this, NavigationDrawerActivity.class);
                startActivity(intent);
                finish();

            }else {
                alertMessage.setAlpha(0f);
                alertMessage.setTranslationY(-100f);

                alertMessage.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(300)
                        .start();

                alertMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    private void navigateToSignUpPage(){
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }
}