package com.stoicdesign.philiplauffenburger.guidings;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.parse.ParseObject;

/**
 * Created by philiplauffenburger on 1/29/16.
 */
public class MyItem implements ClusterItem {
    private final LatLng mPosition;
    private final ParseObject mPoint;
    private String mId, mTime;
    BitmapDescriptor icon;
    String title;
    String snippet;
//    public MyItem(double lat, double lng, ParseObject point, String id, BitmapDescriptor ic, String tit, String sni) {
    public MyItem(double lat, double lng, ParseObject point, String id, String description, String keywords) {
        mPosition = new LatLng(lat, lng);
        mPoint = point;
        mId = id;
//        icon = ic;
        title = description;
        snippet = keywords;

//        JSONArray objectItem = new JSONArray();
    }
//    public onItemClick(myItem)
    @Override
    public LatLng getPosition() {
        return mPosition;
    }
    public String getId() {
        return mId;
    }
    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        snippet = (mId+snippet);
        return snippet;
    }
    public void setId(String id) {
    this.mId =id;
    }



}
