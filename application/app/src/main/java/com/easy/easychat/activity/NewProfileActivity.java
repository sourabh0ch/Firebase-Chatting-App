package com.easy.easychat.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.palette.graphics.Palette;

import com.easy.easychat.R;
import com.easy.easychat.Utills.AppUtills;
import com.easy.easychat.Utills.CommonConstants;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

public class NewProfileActivity extends AppCompatActivity {
    private TextView userName, tvStatus, tvMobileNo, tvHeader;
    private CoordinatorLayout coordinatorLayout;
    private AppBarLayout appBarLayout;
    private Context context;
    private ImageView toolbarImage;
    private String name, id, userImg, mobileNo, profileStatus;
    private ProgressBar progressBar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);
        inflateToolbar();
        initView();
        initOnClickListener();
    }

    private void inflateToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initView() {
        context = NewProfileActivity.this;
        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
        Drawable draw = getResources().getDrawable(R.drawable.custom_progress_bar);
        // set the drawable as progress drawable
        progressBar.setProgressDrawable(draw);
        progressBar.setVisibility(View.INVISIBLE);
        final Display dWidth = getWindowManager().getDefaultDisplay();
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cordinator_layout);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        appBarLayout.post(new Runnable() {
            @Override
            public void run() {
                int heightPx = dWidth.getWidth() * 1 / 3;
                setAppBarOffset(heightPx);
            }
        });
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        collapsingToolbarLayout.setTitleEnabled(true);

        toolbarImage = (ImageView) findViewById(R.id.toolbar_image);
        toolbarImage.getLayoutParams().height = dWidth.getWidth();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.circle_image_group);

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int mutedColor = palette.getMutedColor(getResources().getColor(R.color.colorPrimary));
                collapsingToolbarLayout.setContentScrimColor(mutedColor);
            }
        });

        userName = (TextView) findViewById(R.id.userName);
        tvMobileNo = (TextView) findViewById(R.id.phoneNo);
        tvStatus = (TextView) findViewById(R.id.status);

        name = getIntent().getStringExtra(CommonConstants.USER_NAME);
        id = getIntent().getStringExtra(CommonConstants.UID);
        userImg = getIntent().getStringExtra(CommonConstants.THUMB_IMAGE);
        mobileNo = getIntent().getStringExtra(CommonConstants.MOBILE_NO);
        profileStatus = getIntent().getStringExtra(CommonConstants.PROFILE_STATUS);
        collapsingToolbarLayout.setTitle(name);
        userName.setText(name);
        tvStatus.setText(profileStatus);
        tvMobileNo.setText(mobileNo);

        loadImage(toolbarImage);
    }

    private void loadImage(ImageView toolbarImage) {
        progressBar.setVisibility(View.VISIBLE);
        Picasso.with(context).load(userImg).memoryPolicy(MemoryPolicy.NO_CACHE).fit().centerInside()
                .into(toolbarImage, new com.squareup.picasso.Callback() {
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
    }

    private void setAppBarOffset(int offsetPx) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        behavior.onNestedPreScroll(coordinatorLayout, appBarLayout, null, 0, offsetPx, new int[]{0, 0});
    }

    private void initOnClickListener(){
        toolbarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtills.getImage(context,userImg, name, view);
            }
        });
    }
}
