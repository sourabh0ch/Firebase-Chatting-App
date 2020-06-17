package com.easy.easychat.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.easy.easychat.R;
import com.easy.easychat.Utills.CommonConstants;
import com.squareup.picasso.Picasso;

public class ImageViewActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView title;
    private String my_title, imgPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        imageView = findViewById(R.id.imageView);
        //title = findViewById(R.id.title);

        imgPath = getIntent().getExtras().getString(CommonConstants.THUMB_IMAGE);
        my_title = getIntent().getExtras().getString(CommonConstants.USER_NAME);

//        if (!TextUtils.isEmpty(my_title)) {
//            title.setText(my_title);
//        }

        if (!TextUtils.isEmpty(imgPath)) {

            ViewCompat.setTransitionName(imageView, imgPath);
            Picasso.with(this).load(imgPath)
                    .into(imageView);

        } else {
            ViewCompat.setTransitionName(imageView, "NotAvailable");
            Picasso.with(this).load(R.drawable.circle_image_group)
                    .into(imageView);
        }

    }
}
