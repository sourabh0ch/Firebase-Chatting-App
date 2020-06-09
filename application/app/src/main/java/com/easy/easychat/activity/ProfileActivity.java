package com.easy.easychat.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.easy.easychat.R;
import com.easy.easychat.Utills.CommonConstants;
import com.google.android.material.appbar.AppBarLayout;

public class ProfileActivity extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;
    AppBarLayout appBarLayout;
    private TextView userName,status, mobileNo, tvHeader;
    private ImageView ivLogOut;
    private String name, id;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
        initView();
        inflateToollbar();


    }

    private void inflateToollbar(){
        try{
            Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
            tvHeader = (TextView)mToolbar.findViewById(R.id.header);
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
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initView(){
        try{
            //userName = (TextView)findViewById(R.id.userName);
            mobileNo = (TextView)findViewById(R.id.phoneNo);
            status = (TextView)findViewById(R.id.status);

             name = getIntent().getStringExtra(CommonConstants.USER_NAME);
             id = getIntent().getStringExtra(CommonConstants.UID);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
