package com.easy.easychat.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.easy.easychat.Utills.AppUtills;
import com.easy.easychat.Utills.CommonConstants;
import com.easy.easychat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private EditText etUsername, etEmail;
    private TextInputEditText etPassword;
    private Button btnSignUp;
    private Context context;
    private String mobileNo;
    private FirebaseAuth auth;
    private DatabaseReference mDatabaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        intiView();
        initOnClickListener();
    }

    private void intiView() {
        context = SignupActivity.this;
        progressDialog = new ProgressDialog(this);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (TextInputEditText) findViewById(R.id.etPassword);
        btnSignUp = (Button) findViewById(R.id.btnsignup);
        etPassword.setTransformationMethod(new PasswordTransformationMethod());
        auth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(CommonConstants.USER);
        mDatabaseReference.keepSynced(true);
    }

    private void initOnClickListener() {

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AppUtills.hideKeyboardFrom(SignupActivity.this,view);
                    String name = etUsername.getText().toString();
                    String email = etEmail.getText().toString();
                    String pwd = etPassword.getText().toString();

                    if (name.isEmpty()) {
                        Toast.makeText(SignupActivity.this, "please fill the name field", Toast.LENGTH_SHORT).show();
                    } else if (email.isEmpty()) {
                        Toast.makeText(SignupActivity.this, "please fill the email field", Toast.LENGTH_SHORT).show();
                    } else if (pwd.isEmpty()) {
                        Toast.makeText(SignupActivity.this, "please fill the email field", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.setTitle("Signing in");
                        progressDialog.setMessage("Please wait while Signing in");
                        progressDialog.setCancelable(false);
                        progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
                        progressDialog.show();
                        etUsername.getText().clear();
                        etEmail.getText().clear();
                        etPassword.getText().clear();
                        signUpServiceCall(name, email,pwd);
                    }
                } catch (Throwable t) {
                    throw t;
                }

            }
        });
    }

    private void signUpServiceCall(final String name, final String email,final String pwd) {
        auth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                FirebaseUser user = auth.getCurrentUser();
                String uid = user.getUid();
                String tokenId = FirebaseInstanceId.getInstance().getToken();
                Map<String, String> usermap = new HashMap<>();
                usermap.put(CommonConstants.UID, uid);
                usermap.put(CommonConstants.TOKEN_ID, tokenId);
                usermap.put(CommonConstants.USER_NAME, name);
                usermap.put(CommonConstants.EMAIL, email);
                usermap.put(CommonConstants.PASSWORD, pwd);
                usermap.put(CommonConstants.STATUS, "true");
                usermap.put(CommonConstants.USER_IMAGE, "");

                mDatabaseReference.child(uid).setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "New User is created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignupActivity.this, HomeActivity.class);

                            //----REMOVING THE LOGIN ACTIVITY FROM THE QUEUE----
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            progressDialog.dismiss();
                        } else {
                            progressDialog.setMessage(task.getException().getMessage());
                            progressDialog.dismiss();
                            Toast.makeText(SignupActivity.this, "YOUR NAME IS NOT REGISTERED... MAKE NEW ACCOUNT-- ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}
