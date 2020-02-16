package com.easy.easychat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easy.easychat.R;
import com.easy.easychat.Utills.AppUtills;
import com.easy.easychat.Utills.CommonConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class LoginWithEmailAndPassword extends AppCompatActivity {
   private EditText etEmail,etPwd;
   private Button btnLogin;
   private TextView tvNewUser;
   private FirebaseAuth auth;
   private DatabaseReference mdaDatabase;
   private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_email_and_pwd);
        initView();
        initOnClickListener();
    }

    private void initView(){
        etEmail = (EditText)findViewById(R.id.et_email);
        etPwd  = (EditText)findViewById(R.id.et_pwd);
        btnLogin = (Button)findViewById(R.id.btn_login);
        tvNewUser = (TextView)findViewById(R.id.tv_register_user);
        progressDialog = new ProgressDialog(this);
        etPwd.setTransformationMethod(new PasswordTransformationMethod());
        auth = FirebaseAuth.getInstance();
        mdaDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void initOnClickListener(){
        tvNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginWithEmailAndPassword.this,SignupActivity.class);
                startActivity(i);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtills.hideKeyboardFrom(LoginWithEmailAndPassword.this,v);
               final  String email = etEmail.getText().toString();
               final  String pwd = etPwd.getText().toString();

               if (email.isEmpty()){
                   Toast.makeText(LoginWithEmailAndPassword.this, "please enter your email", Toast.LENGTH_SHORT).show();
               }else if (pwd.isEmpty()){
                   Toast.makeText(LoginWithEmailAndPassword.this, "please enter your password", Toast.LENGTH_SHORT).show();
               }else {
                   progressDialog.setTitle("Loging in");
                   progressDialog.setMessage("Please wait while you are loging in..");
                   progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
                   progressDialog.show();
                   etEmail.getText().clear();
                   etPwd.getText().clear();
                   loginService(email,pwd);
               }
            }
        });
    }
    
    private void loginService(String email, String pwd){
        auth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = auth.getCurrentUser();
                    String uid = user.getUid();
                    String tokenId = FirebaseInstanceId.getInstance().getToken();
                    Map usermap = new HashMap<>();
                    usermap.put(CommonConstants.TOKEN_ID,tokenId);
                    usermap.put(CommonConstants.STATUS,"true");
                    mdaDatabase.child(uid).updateChildren(usermap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null){
                                Toast.makeText(LoginWithEmailAndPassword.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(LoginWithEmailAndPassword.this,HomeActivity.class);
                                startActivity(i);
                                progressDialog.dismiss();
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(LoginWithEmailAndPassword.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(LoginWithEmailAndPassword.this, "Wrong Credential", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
