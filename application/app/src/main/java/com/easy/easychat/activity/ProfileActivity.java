package com.easy.easychat.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.easy.easychat.R;
import com.easy.easychat.Utills.CommonConstants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;
    AppBarLayout appBarLayout;
    private TextView userName, tvStatus, tvMobileNo, tvHeader;
    private ImageView ivLogOut, ivcamera;
    private String name, id, userImg, mobileNo, profileStatus;
    private StorageReference mountainsRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Context context;
    private CircleImageView ivUserImage;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
        initView();
        inflateToollbar();
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
        try {
            context = ProfileActivity.this;
            userName = (TextView) findViewById(R.id.userName);
            tvMobileNo = (TextView) findViewById(R.id.phoneNo);
            tvStatus = (TextView) findViewById(R.id.status);
            ivUserImage = (CircleImageView) findViewById(R.id.user_image);
            ivcamera = (ImageView) findViewById(R.id.camera);
            ivcamera.setVisibility(View.GONE);
            progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
            Drawable draw = getResources().getDrawable(R.drawable.custom_progress_bar);
            // set the drawable as progress drawable
            progressBar.setProgressDrawable(draw);
            progressBar.setVisibility(View.INVISIBLE);
            name = getIntent().getStringExtra(CommonConstants.USER_NAME);
            id = getIntent().getStringExtra(CommonConstants.UID);
            userImg = getIntent().getStringExtra(CommonConstants.THUMB_IMAGE);
            mobileNo = getIntent().getStringExtra(CommonConstants.MOBILE_NO);
            profileStatus = getIntent().getStringExtra(CommonConstants.PROFILE_STATUS);
            userName.setText(name);
            tvStatus.setText(profileStatus);
            tvMobileNo.setText(mobileNo);
            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReference();
            progressBar.setVisibility(View.VISIBLE);
            Picasso.with(context).load(userImg).memoryPolicy(MemoryPolicy.NO_CACHE).fit().centerInside()
                    .into(ivUserImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            progressBar.setVisibility(View.GONE);
                            //do something when there is picture loading error
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
