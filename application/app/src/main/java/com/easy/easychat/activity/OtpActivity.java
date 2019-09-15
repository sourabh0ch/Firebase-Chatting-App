package com.easy.easychat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easy.easychat.Utills.CommonConstants;
import com.easy.easychat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OtpActivity extends AppCompatActivity implements TextWatcher {
    private Context context;
    private TextView secureMobNo;
    private String mobileNo,verificationCode;
    private Button btnOtpSubmit;
    private EditText et1,et2,et3,et4,et5,et6;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        initView();
        initOnClicklistener();

    }

    private void initView(){
        context = OtpActivity.this;
        et1 = (EditText) findViewById(R.id.editText1);
        et2 = (EditText) findViewById(R.id.editText2);
        et3 = (EditText) findViewById(R.id.editText3);
        et4 = (EditText) findViewById(R.id.editText4);
        et5 = (EditText) findViewById(R.id.editText5);
        et6 = (EditText) findViewById(R.id.editText6);
        auth = FirebaseAuth.getInstance();
        verificationCode = getIntent().getStringExtra(CommonConstants.OTP);
        secureMobNo = (TextView) findViewById(R.id.mobNo);
        mobileNo = getIntent().getStringExtra(CommonConstants.MOBILE_NO);
        secureMobNo.setText(mobileNo);
        btnOtpSubmit = (Button) findViewById(R.id.btn_otp_submit);
    }

    private void initOnClicklistener(){
        btnOtpSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String pin1 = et1.getText().toString();
                    String pin2 = et2.getText().toString();
                    String pin3 = et3.getText().toString();
                    String pin4 = et4.getText().toString();
                    String pin5 = et5.getText().toString();
                    String pin6 = et6.getText().toString();
                    if (pin1.isEmpty() || pin2.isEmpty() || pin3.isEmpty() || pin4.isEmpty()) {
                        if (pin1.isEmpty()) {
                            Toast.makeText(context, "This field Can't be empty", Toast.LENGTH_SHORT).show();
                        }
                        if (pin2.isEmpty()) {
                            Toast.makeText(context, "This field Can't be empty", Toast.LENGTH_SHORT).show();
                        }
                        if (pin3.isEmpty()) {
                            Toast.makeText(context, "This field Can't be empty", Toast.LENGTH_SHORT).show();
                        }
                        if (pin4.isEmpty()) {
                            Toast.makeText(context, "This field Can't be empty", Toast.LENGTH_SHORT).show();
                        }
                        if (pin5.isEmpty()) {
                            Toast.makeText(context, "This field Can't be empty", Toast.LENGTH_SHORT).show();
                        }
                        if (pin6.isEmpty()) {
                            Toast.makeText(context, "This field Can't be empty", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String otpCode = pin1 + pin2 + pin3 + pin4 + pin5 + pin6;

                        verifyOtp(otpCode);
                    }

                } catch (Throwable t) {
                    throw t;
                }
            }
        });
        et1.addTextChangedListener(this);
        et2.addTextChangedListener(this);
        et3.addTextChangedListener(this);
        et4.addTextChangedListener(this);
        et5.addTextChangedListener(this);
        et6.addTextChangedListener(this);
    }

    private void verifyOtp(String otp){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);
        siginWithCredential(credential);

    }

    private void siginWithCredential(PhoneAuthCredential credential){
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                try{
                    if (task.isSuccessful()){
                        FirebaseUser user = auth.getCurrentUser();
                        if(user.getDisplayName()==null || user.getEmail()==null){
                            Intent intent = new Intent(OtpActivity.this, SignupActivity.class);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(OtpActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }

                    }else{
                        task.getException();
                        Toast.makeText(context, "Verification failed", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Exception ex = task.getException();
                    Log.d("ex",ex.toString());
                }

            }
        });
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (count == 1) {
            if (et1.getText().length() == 1) {
                et1.clearFocus();
                et2.requestFocus();
            }
            if (et2.getText().length() == 1) {
                et2.clearFocus();
                et3.requestFocus();
            }
            if (et3.getText().length() == 1) {
                et3.clearFocus();
                et4.requestFocus();
            }
            if (et4.getText().length() == 1) {
                et4.clearFocus();
                et5.requestFocus();
            }
            if (et5.getText().length() == 1) {
                et5.clearFocus();
                et6.requestFocus();
            }
            if (et6.getText().length() == 1) {
                et6.clearFocus();
                btnOtpSubmit.requestFocus();
            }
        }
    }

}
