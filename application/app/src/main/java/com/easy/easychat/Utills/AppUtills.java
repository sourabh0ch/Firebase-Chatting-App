package com.easy.easychat.Utills;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import com.easy.easychat.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class AppUtills extends Application {

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getImage(FirebaseStorage storage, StorageReference storageRef, String uid, final Context context){
        final String[] url = {""};
        storageRef.child(CommonConstants.USER_PROFILE_STORAGE + "/" + uid + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                url[0] = uri.toString();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //Toast.makeText(context, "Failed to load the image", Toast.LENGTH_SHORT).show();
            }
        });
        return String.valueOf(url);
    }
}
