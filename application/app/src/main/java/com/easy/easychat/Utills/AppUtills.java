package com.easy.easychat.Utills;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;

import com.easy.easychat.R;
import com.easy.easychat.activity.HomeActivity;
import com.easy.easychat.activity.ImageViewActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class AppUtills  {

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void getImage(Context context,  String  thumbUrl, String name,  View view){
        try {

            Intent intent = new Intent(context, ImageViewActivity.class);
            intent.putExtra(CommonConstants.THUMB_IMAGE, thumbUrl);
            intent.putExtra(CommonConstants.USER_NAME, name);

            Pair<View, String> bodyPair = Pair.create(view, thumbUrl);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(((Activity)context), bodyPair);

            ActivityCompat.startActivity(context, intent, options.toBundle());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showAlert(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


}
