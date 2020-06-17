package com.easy.easychat.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.easy.easychat.R;
import com.easy.easychat.Utills.CommonConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity {
    private EditText etEmail, etUserName, etMobileNo, etProfileStatus;
    private Button btnUpdate;
    private TextView tvHeader;
    private ImageView ivLogOut;
    private String id, email, name, mobileNo, profileStatus, status, pwd, tokenId, thumbUrl;
    private DatabaseReference mUserDatabase;
    private Context context;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_profile);
        initView();
        inflateToollbar();
        initOnClickListener();
    }

    private void inflateToollbar() {
        try {
            Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
            tvHeader = (TextView) mToolbar.findViewById(R.id.header);
            tvHeader.setText(name);
            //tvHeader.setText("Profile");
            ivLogOut = (ImageView) mToolbar.findViewById(R.id.ivLogOut);
            ivLogOut.setVisibility(View.GONE);
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initView() {
        context = UpdateProfileActivity.this;
        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
        Drawable draw = getResources().getDrawable(R.drawable.custom_progress_bar);
        // set the drawable as progress drawable
        progressBar.setProgressDrawable(draw);
        progressBar.setVisibility(View.INVISIBLE);
        etEmail = (EditText) findViewById(R.id.et_update_email);
        etMobileNo = (EditText) findViewById(R.id.et_update_mobileNo);
        etUserName = (EditText) findViewById(R.id.et_update_username);
        etProfileStatus = (EditText) findViewById(R.id.et_update_profile_status);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child(CommonConstants.USERS);
        id = getIntent().getStringExtra(CommonConstants.UID);
        email = getIntent().getStringExtra(CommonConstants.EMAIL);
        mobileNo = getIntent().getStringExtra(CommonConstants.MOBILE_NO);
        profileStatus = getIntent().getStringExtra(CommonConstants.PROFILE_STATUS);
        name = getIntent().getStringExtra(CommonConstants.USER_NAME);
        status = getIntent().getStringExtra(CommonConstants.STATUS);
        tokenId = getIntent().getStringExtra(CommonConstants.TOKEN_ID);
        thumbUrl = getIntent().getStringExtra(CommonConstants.THUMB_IMAGE);
        pwd = getIntent().getStringExtra(CommonConstants.PASSWORD);
        setValueToField(email, mobileNo, profileStatus, name);
    }

    private void initOnClickListener() {
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                try {
                    Map user = new HashMap();
                    user.put(CommonConstants.UID, id);
                    user.put(CommonConstants.TOKEN_ID, tokenId);
                    user.put(CommonConstants.EMAIL, etEmail.getText().toString());
                    user.put(CommonConstants.PASSWORD, pwd);
                    user.put(CommonConstants.MOBILE_NO, etMobileNo.getText().toString());
                    user.put(CommonConstants.USER_NAME, etUserName.getText().toString());
                    user.put(CommonConstants.PROFILE_STATUS, etProfileStatus.getText().toString());
                    user.put(CommonConstants.THUMB_IMAGE, thumbUrl);
                    user.put(CommonConstants.STATUS, status);

                    mUserDatabase.child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                UpdateProfileActivity.this.finish();
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void setValueToField(String email, String mobileNo, String profileStatus, String name) {
        if (!TextUtils.isEmpty(email)) {
            etEmail.setText(email);
        }

        if (!TextUtils.isEmpty(mobileNo)) {
            etMobileNo.setText(mobileNo);
        }

        if (!TextUtils.isEmpty(profileStatus)) {
            etProfileStatus.setText(profileStatus);
        }

        if (!TextUtils.isEmpty(name)) {
            etUserName.setText(name);
        }
    }
}
