package com.easy.easychat.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;

import com.easy.easychat.R;
import com.easy.easychat.Utills.CommonConstants;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

public class ImageViewActivity extends AppCompatActivity implements View.OnTouchListener {
    private ImageView imageView, ivLogOut;
    private TextView title, tvHeader;
    private String my_title, imgPath;
    private RelativeLayout rlImageViewer;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        initView();
        inflateToolBar();
    }

    private void inflateToolBar() {
        try {
            Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
            mToolbar.setBackgroundColor(getResources().getColor(R.color.black));
            tvHeader = (TextView) mToolbar.findViewById(R.id.header);
            tvHeader.setText(my_title);
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

    private void initView() {
        imageView = findViewById(R.id.imageView);
        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
        Drawable draw = getResources().getDrawable(R.drawable.custom_progress_bar);
        progressBar.setProgressDrawable(draw);
        progressBar.setVisibility(View.INVISIBLE);
        rlImageViewer = (RelativeLayout) findViewById(R.id.rlImageViewer);
        imgPath = getIntent().getExtras().getString(CommonConstants.THUMB_IMAGE);
        my_title = getIntent().getExtras().getString(CommonConstants.USER_NAME);
        showImage();
        slideUpOrDownTheImage();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showImage() {
        if (!TextUtils.isEmpty(imgPath)) {
            progressBar.setVisibility(View.VISIBLE);
            ViewCompat.setTransitionName(imageView, imgPath);
            Picasso.with(this).load(imgPath).memoryPolicy(MemoryPolicy.NO_CACHE).fit().centerInside()
                    .into(imageView, new com.squareup.picasso.Callback() {
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

        } else {
            progressBar.setVisibility(View.GONE);
            ViewCompat.setTransitionName(imageView, "NotAvailable");
            Picasso.with(this).load(R.drawable.circle_image_group)
                    .into(imageView);
        }
    }

    private void slideUpOrDownTheImage() {
        imageView.setOnTouchListener(this);
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                break;
            }
            case MotionEvent.ACTION_UP: {
                // Here u can write code which is executed after the user release the touch on the screen
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                // Here u can write code which is executed when user move the finger on the screen
                Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_down);
                // Start animation
                imageView.startAnimation(slide_down);
                ActivityCompat.finishAfterTransition(ImageViewActivity.this);
                break;
            }
        }
        return true;
    }
}
