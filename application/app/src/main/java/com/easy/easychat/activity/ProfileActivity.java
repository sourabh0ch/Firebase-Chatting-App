package com.easy.easychat.activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
    private TextView userName, status, mobileNo, tvHeader;
    private ImageView ivLogOut, ivcamera;
    private String name, id, userImg;
    private StorageReference mountainsRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Context context;
    private CircleImageView ivUserImage;


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
            mobileNo = (TextView) findViewById(R.id.phoneNo);
            status = (TextView) findViewById(R.id.status);
            ivUserImage = (CircleImageView) findViewById(R.id.user_image);
            ivcamera = (ImageView) findViewById(R.id.camera);
            ivcamera.setVisibility(View.GONE);
            name = getIntent().getStringExtra(CommonConstants.USER_NAME);
            id = getIntent().getStringExtra(CommonConstants.UID);
            userImg = getIntent().getStringExtra(CommonConstants.THUMB_IMAGE);
            userName.setText(name);
            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReference();
            Picasso.with(context).load(userImg).fit().centerInside()
                    .placeholder(R.drawable.circle_image_group).into(ivUserImage);
            getUserProfile();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getUserProfile() {

        storageRef.child(CommonConstants.USER_PROFILE_STORAGE + "/" + id + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                // Got the download URL for 'users/me/profile.png'
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(context, "Failed to load the image", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
