package com.stoicdesign.philiplauffenburger.guidings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philiplauffenburger on 2/26/16.
 */
public class VideoAdapter extends BaseAdapter {

    final List<VideoSlideViewModel> viewModels;

    private final Context context;
    private final LayoutInflater inflater;

    public VideoAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.viewModels = new ArrayList<VideoSlideViewModel>();
    }

    public VideoAdapter(Context context, List<VideoSlideViewModel> viewModels) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.viewModels = viewModels;
    }

    public List<VideoSlideViewModel> viewmodels() {
        return this.viewModels;
    }

    @Override
    public int getCount() {
        return this.viewModels.size();
    }

    @Override
    public VideoSlideViewModel getItem(int position) {
        return this.viewModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        // We only need to implement this if we have multiple rows with a different layout. All your rows use the same layout so we can just return 0.
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // We get the view model for this position
        final VideoSlideViewModel viewModel = getItem(position);

        VideoRow row;
        // If the convertView is null we need to create it
        if(convertView == null) {
            convertView = this.inflater.inflate(VideoRow.LAYOUT, parent, false);

            // In that case we also need to create a new row and attach it to the newly created View
            row = new VideoRow(this.context, convertView);
            convertView.setTag(row);
        }

        // After that we get the row associated with this View and bind the view model to it
        row = (VideoRow) convertView.getTag();
        row.bind(viewModel);


        return convertView;

    }
}
