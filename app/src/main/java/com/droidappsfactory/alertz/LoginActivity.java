package com.droidappsfactory.alertz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.droidappsfactory.alertz.util.LogHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Rishi on 05-05-2017.
 */

public class LoginActivity extends AppCompatActivity {

    LogHelper logHelper = new LogHelper(LoginActivity.class.getSimpleName());


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    ProgressDialog progressDialog;
    Context context;
    EditText et_username,et_passowrd;
    Button bt_login;
    TextView tv_signup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        initActions();

    }

    private void initActions() {
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = et_username.getText().toString().trim();
                String passWord = et_passowrd.getText().toString().trim();
                if(TextUtils.isEmpty(userName)){
                    Toast.makeText(context,"Please enter username",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(passWord)){
                    Toast.makeText(context,"Please enter password",Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.setMessage("Logging in ...");
                progressDialog.show();

                mAuth.signInWithEmailAndPassword(userName,passWord).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(context,"User logged in",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context,AlertzActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0,0);
                        }else {

                            Toast.makeText(context,"Please check your credentials",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.move_right_in_activity,R.anim.move_left_out_activity);
            }
        });
    }

    private void init() {
        context = LoginActivity.this;
        progressDialog = new ProgressDialog(this);
        et_username = (EditText)findViewById(R.id.et_username);
        et_passowrd = (EditText)findViewById(R.id.et_pwd);
        bt_login = (Button)findViewById(R.id.btn_login);
        tv_signup = (TextView)findViewById(R.id.tv_signup);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Toast.makeText(context,"User logged in",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context,AlertzActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // User is signed out
                    logHelper.printLog("onAuthStateChanged:signed_out");
                }

            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
