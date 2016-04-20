package com.stoicdesign.philiplauffenburger.guidings;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

/**
 * Created by philiplauffenburger on 1/21/16.
 */
public class App extends Application {
 private boolean videoTaken, viewOwnProfile;

        @Override
        public void onCreate() {
            super.onCreate();
            FontsOverride.setDefaultFont(this, "DEFAULT", "Roboto-Regular.ttf");
            FontsOverride.setDefaultFont(this, "MONOSPACE", "Roboto-Bold.ttf");
            FontsOverride.setDefaultFont(this, "SERIF", "Roboto-Italic.ttf");
            FontsOverride.setDefaultFont(this, "SANS_SERIF", "Roboto-Light.ttf");
            Parse.initialize(this,"Eim8wu6P6uLIyvqKWMaDgLUXF6pBGJLoY11ZNyOy","4b20rzM6zyZ5jCFvHoV5mYY0lAf2x0dGKDJ8fMFZ"); // Your Application ID and Client Key are defined elsewhere
            ParseUser.enableAutomaticUser();



            ParseInstallation.getCurrentInstallation().saveInBackground();

        }
    public boolean getVideoTaken(){
        return videoTaken;
    }

    public void setVideoTaken(boolean videoTaken){
        this.videoTaken = videoTaken;
    }

    public boolean getViewOwnProfile(){
        return viewOwnProfile;
    }
    public void setViewOwnProfile(boolean viewOwnProfile){
        this.viewOwnProfile = viewOwnProfile;
    }



}


