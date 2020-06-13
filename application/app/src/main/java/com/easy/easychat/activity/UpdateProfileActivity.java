package com.easy.easychat.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.easy.easychat.R;
import com.easy.easychat.Utills.CommonConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity {
    private EditText etEmail, etUserName, etMobileNo, etProfileStatus;
    private Button btnUpdate;
    private String id,email,name, mobileNo,profileStatus;
    private DatabaseReference mUserDatabase;
    private Context context;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_profile);
        initView();
        initOnClickListener();
    }

    private void initView() {
        context = UpdateProfileActivity.this;
        etEmail = (EditText) findViewById(R.id.et_update_email);
        etMobileNo = (EditText) findViewById(R.id.et_update_mobileNo);
        etUserName = (EditText) findViewById(R.id.et_update_username);
        etProfileStatus = (EditText) findViewById(R.id.et_update_profile_status);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        mUserDatabase = FirebaseDatabase.getInstance().getReference();
        id = getIntent().getStringExtra(CommonConstants.UID);
        email = getIntent().getStringExtra(CommonConstants.EMAIL);
        mobileNo = getIntent().getStringExtra(CommonConstants.MOBILE_NO);
        profileStatus = getIntent().getStringExtra(CommonConstants.PROFILE_STATUS);
        name = getIntent().getStringExtra(CommonConstants.USER_NAME);
        setValueToField(email,mobileNo, profileStatus, name);
    }

    private void initOnClickListener() {
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("name", etUserName.getText().toString());
                try{
                    Map user = new HashMap();
                    user.put(CommonConstants.USER_NAME,etUserName.getText().toString());
                    user.put(CommonConstants.EMAIL,etEmail.getText().toString());
                    user.put(CommonConstants.MOBILE_NO,etMobileNo.getText().toString());
                    user.put(CommonConstants.PROFILE_STATUS,etProfileStatus.getText().toString());

                    mUserDatabase.child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
//                    DatabaseReference ref = mUserDatabase.child(id).child(CommonConstants.USER_NAME);
//                    ref.setValue(etUserName.getText().toString());
//                    mUserDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                            //dataSnapshot.getRef().child(CommonConstants.EMAIL).setValue(etEmail.getText().toString());
//                            dataSnapshot.getRef().child(CommonConstants.USER_NAME).setValue(etUserName.getText().toString());
//                            //dataSnapshot.getRef().child(CommonConstants.PROFILE_STATUS).setValue(etProfileStatus.getText().toString());
//                            dataSnapshot.getRef().child(CommonConstants.MOBILE_NO).setValue(etMobileNo.getText().toString());
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                            Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    private void setValueToField(String email, String mobileNo, String profileStatus, String name) {
        if (!TextUtils.isEmpty(email)){
            etEmail.setText(email);
        }

        if (!TextUtils.isEmpty(mobileNo)){
            etMobileNo.setText(mobileNo);
        }

        if (!TextUtils.isEmpty(profileStatus)){
            etProfileStatus.setText(profileStatus);
        }

        if (!TextUtils.isEmpty(name)){
            etUserName.setText(name);
        }
    }
}
