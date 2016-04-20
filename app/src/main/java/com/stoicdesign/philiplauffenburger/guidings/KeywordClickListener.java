package com.stoicdesign.philiplauffenburger.guidings;

import android.app.Activity;
import android.util.Log;
import android.view.View;

/**
 * Created by philiplauffenburger on 2/17/16.
 */
public class KeywordClickListener implements View.OnClickListener {

    String keyword;
    Activity a;

    public KeywordClickListener(Activity a, String keyword) {
        this.keyword = keyword;
        this.a = a;
    }

    @Override
    public void onClick(View v) {
        Log.v("KEYWORDCLICKLISTENER", keyword);

    }
}
