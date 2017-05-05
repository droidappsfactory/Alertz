package com.droidappsfactory.alertz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.droidappsfactory.alertz.util.LogHelper;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Rishi on 05-05-2017.
 */

public class SignUpActivity extends AppCompatActivity {

    LogHelper logHelper = new LogHelper(SignUpActivity.class.getSimpleName());

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }
}
