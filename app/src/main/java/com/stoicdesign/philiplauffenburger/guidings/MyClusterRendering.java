package com.stoicdesign.philiplauffenburger.guidings;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by philiplauffenburger on 1/31/16.
 */
public class MyClusterRendering extends DefaultClusterRenderer<MyItem> {

    public MyClusterRendering(Context context, GoogleMap map,
                       ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
    }


    protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {

//        markerOptions.id(item.getId());
        markerOptions.snippet(item.getSnippet());
        markerOptions.title(item.getTitle());

        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.guidings_blue_3_13));
//        markerOptions.infoWindowAnchor(3,0);
        super.onBeforeClusterItemRendered(item, markerOptions);
    }
}
