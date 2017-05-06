package com.droidappsfactory.alertz;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Rishi on 05-05-2017.
 */

public class SignUpActivity extends AppCompatActivity {

    LogHelper logHelper = new LogHelper(SignUpActivity.class.getSimpleName());

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    Context context;
    EditText et_username,et_passowrd;
    Button bt_register;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        init();
        initAction();
    }

    private void initAction() {
        bt_register.setOnClickListener(new View.OnClickListener() {
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

                progressDialog.setMessage("Registering new user");
                progressDialog.show();

                mAuth.createUserWithEmailAndPassword(userName,passWord).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(context,"User registration successful.",Toast.LENGTH_SHORT).show();
                        }else {

                            Toast.makeText(context,"User registration failed.",Toast.LENGTH_SHORT).show();
                        }


                    }
                });

            }
        });
    }

    private void init() {
        context = SignUpActivity.this;
        progressDialog = new ProgressDialog(context);
        et_username = (EditText)findViewById(R.id.et_username);
        et_passowrd = (EditText)findViewById(R.id.et_pwd);
        bt_register = (Button)findViewById(R.id.bt_register);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    logHelper.printLog("onAuthStateChanged:signed_in:" + user.getUid());
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
