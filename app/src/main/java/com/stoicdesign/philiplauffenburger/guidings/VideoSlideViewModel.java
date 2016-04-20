package com.stoicdesign.philiplauffenburger.guidings;

import android.app.Activity;
import android.content.Context;

/**
 * Created by philiplauffenburger on 2/26/16.
 */
 public class VideoSlideViewModel  {
    private String text;
    private String imageUrl;
    private String id;
    private Context context;

    public VideoSlideViewModel(String text, String imageUrl, String id) {
        this.text = text;
        this.imageUrl = imageUrl;
        this.id = id;
    }

    public String getText() {
        return text;
    }
    public String getId() {
        return id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }




}
