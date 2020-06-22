package com.easy.easychat.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easy.easychat.R;
import com.easy.easychat.Utills.AppUtills;
import com.easy.easychat.Utills.CommonConstants;
import com.easy.easychat.Utills.SharedPrefrenceUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
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
    private EditText etEmail, etPwd;
    private Button btnLogin;
    private TextView tvNewUser;
    private FirebaseAuth auth;
    private DatabaseReference mdaDatabase;
    private ProgressDialog progressDialog;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_email_and_pwd);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        appPermissions();
        proceed();
    }

    private void appPermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            boolean status = true;
            if(ContextCompat.checkSelfPermission(LoginWithEmailAndPassword.this, CommonConstants.PERMISSION_READ_PHONE)!= PackageManager.PERMISSION_GRANTED){
                status = false;
            }
            if(ContextCompat.checkSelfPermission(LoginWithEmailAndPassword.this, CommonConstants.PERMISSION_WRITE)!= PackageManager.PERMISSION_GRANTED){
                status = false;
            }
            if(!status){
                requestPermissions(new String[]{CommonConstants.PERMISSION_READ_PHONE, CommonConstants.PERMISSION_WRITE}, CommonConstants.PERMISSION_REQ_CODE);
            }
            else{
                proceed();
            }
        }else{
            proceed();
        }
    }

    private void proceed() {
        if (SharedPrefrenceUtil.isLogIn(this)) {
            SharedPreferences pref = getSharedPreferences("pendingnoti", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("pendingnoti", "yes");
            editor.commit();

            Intent contentIntent = new Intent(LoginWithEmailAndPassword.this,
                    HomeActivity.class);
            contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(contentIntent);
            finish();
        } else {
            setContentView(R.layout.activity_login_with_email_and_pwd);
            initView();
            initOnClickListener();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CommonConstants.PERMISSION_REQ_CODE:
                boolean read = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean writeStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(read && writeStorage){
//                        EasyLogger.configureLogger(ExternalStorage.getSdCardPath()
//                                + CommonConstants.APP_FOLDER_NAME + "/"
//                                + CommonConstants.APP_LOG_FILE_NAME);
                        proceed();
                    }else {
                        Log.d("out","else");
                        showMessageOKCancel();
                    }
                }
                break;
        }
    }
    private void showMessageOKCancel() {
        final Dialog dialog = new Dialog(LoginWithEmailAndPassword.this, R.style.TransparentProgressDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_app_permissions);

        Button btnCancel =(Button) dialog.findViewById(R.id.btnCancel);
        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
        ImageView ivClose = (ImageView) dialog.findViewById(R.id.ivClose);
        TextView tvMsg = (TextView) dialog.findViewById(R.id.tvMsg);
        tvMsg.setText("You need to give both permissions.");

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appPermissions();
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                AppUtills.showAlert(LoginWithEmailAndPassword.this, "Sorry! We can not operate properly without permissions.");
                LoginWithEmailAndPassword.this.finish();
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                AppUtills.showAlert(LoginWithEmailAndPassword.this, "Sorry! We can not operate properly without permissions.");
                LoginWithEmailAndPassword.this.finish();
            }
        });
        dialog.show();
    }


    private void initView() {
        etEmail = (EditText) findViewById(R.id.et_email);
        etPwd = this.<EditText>findViewById(R.id.et_pwd);
        btnLogin = (Button) findViewById(R.id.btn_login);
        tvNewUser = (TextView) findViewById(R.id.tv_register_user);
        progressDialog = new ProgressDialog(this);
        etPwd.setTransformationMethod(new PasswordTransformationMethod());
        auth = FirebaseAuth.getInstance();
        mdaDatabase = FirebaseDatabase.getInstance().getReference().child(CommonConstants.USER_AUTH);
    }

    private void initOnClickListener() {
        tvNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginWithEmailAndPassword.this, SignupActivity.class);
                startActivity(i);
                // throw new RuntimeException("Test Crash"); // Force a crash
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtills.hideKeyboardFrom(LoginWithEmailAndPassword.this, v);
                final String email = etEmail.getText().toString();
                final String pwd = etPwd.getText().toString();

                if (email.isEmpty()) {
                    Toast.makeText(LoginWithEmailAndPassword.this, "please enter your email", Toast.LENGTH_SHORT).show();
                } else if (pwd.isEmpty()) {
                    Toast.makeText(LoginWithEmailAndPassword.this, "please enter your password", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setTitle("Loging in");
                    progressDialog.setMessage("Please wait while you are loging in..");
                    progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();
                    etEmail.getText().clear();
                    etPwd.getText().clear();
                    loginService(email, pwd);
                }
            }
        });
    }

    private void loginService(String email, String pwd) {
        auth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    String uid = user.getUid();
                    String tokenId = FirebaseInstanceId.getInstance().getToken();
                    Map usermap = new HashMap<>();
                    usermap.put(CommonConstants.TOKEN_ID, tokenId);
                    usermap.put(CommonConstants.STATUS, "true");
                    mdaDatabase.child(uid).updateChildren(usermap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                Toast.makeText(LoginWithEmailAndPassword.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(LoginWithEmailAndPassword.this, HomeActivity.class);
                                startActivity(i);
                                SharedPrefrenceUtil.setIsLogIn(LoginWithEmailAndPassword.this, true);
                                progressDialog.dismiss();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(LoginWithEmailAndPassword.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginWithEmailAndPassword.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
