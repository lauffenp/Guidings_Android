package com.stoicdesign.philiplauffenburger.guidings;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageButton;

import com.parse.ParseAnonymousUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

/**
 * Created by philiplauffenburger on 1/21/16.
 */
public class MainActivity extends AppCompatActivity  {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private Toolbar toolbar;
    ImageButton button, mapButton, userButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

// Determine whether the current user is an anonymous user
        if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            // If user is anonymous, send the user to LoginSignupActivity.class
            Intent intent = new Intent(MainActivity.this,
                    LoginSignupActivity.class);
            startActivity(intent);
            finish();
        } else {
            // If current user is NOT anonymous user
            // Get current user data from Parse.com
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {

                onLoggedIn();
            } else {
                // Send user to LoginSignupActivity.class
                Intent intent = new Intent(MainActivity.this,
                        LoginSignupActivity.class);
                startActivity(intent);
                finish();
            }
        }










    }

    //MAY NEED THIS!!!!

    public void onLoggedIn() {
//        ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        String id = ParseUser.getCurrentUser().getObjectId();
       ParseInstallation installation =  ParseInstallation.getCurrentInstallation();
        installation.put("userID",id);
        installation.saveInBackground();
//
//
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
    }




//        setContentView(R.layout.activity_main);
//        button = (ImageButton) findViewById(R.id.video_Button);
//        button.setOnClickListener(this);
//        mapButton = (ImageButton) findViewById(R.id.map_Button);
//        mapButton.setOnClickListener(this);
//        userButton = (ImageButton) findViewById(R.id.user_button);
//        userButton.setOnClickListener(this);
//
//    }
//
//
//    public void onClick(View v) {
//
//        switch (v.getId()) {
//            case R.id.video_Button: {
//                Intent i = new Intent(this, VideoRecorderActivity.class);
//                startActivity(i);
//                break;
//            }
//            case R.id.user_button: {
//                Intent i = new Intent(this, UserActivity.class);
//                startActivity(i);
//                break;
//            }
//            case R.id.map_Button: {
//                Intent i = new Intent(this, MapsActivity.class);
//                startActivity(i);
//                break;
//            }
//
//
//        }
//    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.v("Permissions_Length", permissions.toString());
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;

            }
        }
    }
}

