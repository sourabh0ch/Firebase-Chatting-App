package com.easy.easychat.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.palette.graphics.Palette;

import com.easy.easychat.R;
import com.easy.easychat.Utills.CommonConstants;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class ProfileActivity extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;
    AppBarLayout appBarLayout;
    private TextView userName,status, mobileNo, tvHeader;
    private ImageView ivLogOut;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
        inflateToollbar();
        initView();

    }

    private void inflateToollbar(){
        try{
            Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
            tvHeader = (TextView)mToolbar.findViewById(R.id.header);

            tvHeader.setText("Profile");
            ivLogOut = (ImageView) mToolbar.findViewById(R.id.ivLogOut);
            ivLogOut.setVisibility(View.GONE);
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }

            userName = (TextView)findViewById(R.id.userName);
            mobileNo = (TextView)findViewById(R.id.phoneNo);
            status = (TextView)findViewById(R.id.status);

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setAppBarOffset(int offsetPx) {
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
//        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
//        behavior.onNestedPreScroll(coordinatorLayout, appBarLayout, null, 0, offsetPx, new int[]{0, 0});
    }

    private void initView(){
        try{
            String name = getIntent().getStringExtra(CommonConstants.USER_NAME);
            String id = getIntent().getStringExtra(CommonConstants.UID);
            userName.setText(name);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
