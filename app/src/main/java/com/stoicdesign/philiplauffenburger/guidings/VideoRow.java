package com.stoicdesign.philiplauffenburger.guidings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by philiplauffenburger on 2/26/16.
 */
public class VideoRow {
    // This is a reference to the layout we defined above
    public static final int LAYOUT = R.layout.video_slide;

    private final Context context;
    private final TextView textView,  numLikes, date;
    private final TableRow keywordRow;
    private final ImageView imageView;
    private final Button delButton;
    String userName, annoId, time;
    View convertView;

    public VideoRow(Context context, View convertView) {
        this.convertView = convertView;
        this.context = context;
        this.imageView = (ImageView) convertView.findViewById(R.id.imageView);
        this.textView = (TextView) convertView.findViewById(R.id.textView);
        this.keywordRow = (TableRow) convertView.findViewById(R.id.keywords_row_video_slide);
        this.date =  (TextView) convertView.findViewById(R.id.date_table_row_video);
        this.numLikes =  (TextView) convertView.findViewById(R.id.liked_video_row);
        this.delButton = (Button) convertView.findViewById(R.id.delete_button);

    }

    public void bind(final VideoSlideViewModel videoSlideViewModel) {
        this.textView.setText(videoSlideViewModel.getText());
        ParseQuery query = new ParseQuery("Annotation");
        ParseObject parseAnnotation = null;
        try {
             parseAnnotation = query.get(videoSlideViewModel.getId());
        } catch (ParseException e) {
            e.printStackTrace();
        }
         annoId = videoSlideViewModel.getId();
        final String title = parseAnnotation.get("description").toString();
        Number likesnumber = parseAnnotation.getNumber("numberOfLikes");
if (likesnumber==null){
    numLikes.setText("0");
} else {
    numLikes.setText(likesnumber.toString());
}

        String likes = "0";
        if (likesnumber != null){
            likes=likesnumber.toString();
        }
        final String parseUserCursor = parseAnnotation.getParseUser("uploadedBy").getObjectId();



        time = DateUtility.getTimeAgo(parseAnnotation.getCreatedAt(), this.context);
        if (time==null){
            time=parseAnnotation.getCreatedAt().toString();
        }
        this.date.setText(time);
         final String keywords = parseAnnotation.get("keywords").toString();
        int lengthStrings = keywords.length();
        String keywordscut = keywords.substring(1,lengthStrings-1);
        ArrayList<String> keywordArray = new ArrayList<String>(Arrays.asList(keywordscut.split(", ")));
        Iterator it =keywordArray.iterator();
        int i= 0;
        while (it.hasNext()){

            String text= it.next().toString();
//            ViewGroup view = (ViewGroup) findViewById(android.R.id.content);

//            TextView newKeyword = (TextView) getLayoutInflater().inflate(R.layout.keyword_text_view,view);
            TextView newKeyword = new TextView(this.context);
            newKeyword.setText(text);
            newKeyword.setTextColor(Color.parseColor("#2F4F4F"));
            GradientDrawable gdDefault = new GradientDrawable();
            gdDefault.setColor(Color.parseColor("#eeeeee"));
            gdDefault.setCornerRadius(100);
            gdDefault.setStroke(5, Color.parseColor("#000000"));
            newKeyword.setPadding(20, 20, 20, 20);
            newKeyword.setBackground(gdDefault);
            keywordRow.addView(newKeyword, i);
            i++;


        }


        Picasso.with(this.context).load(videoSlideViewModel.getImageUrl()).into(this.imageView);

        final ParseObject parseAnnotationVid = parseAnnotation;
        final String likesVid = likes;

        this.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                ParseFile videoFile = parseAnnotationVid.getParseFile("videoFile");
                String uri = videoFile.getUrl();
                final Intent playIntent = new Intent(context,VideoWatchActivity.class);




                playIntent.putExtra("title", title);
                playIntent.putExtra("likes", likesVid);
                playIntent.putExtra("annoId",annoId);
                playIntent.putExtra("time", time);
                playIntent.putExtra("keywords", keywords);
                playIntent.putExtra("uri", uri);




                ParseQuery<ParseUser> userQuery = ParseUser.getQuery();

                userQuery.getInBackground(parseUserCursor, new GetCallback<ParseUser>() {
                    public void done(ParseUser object, ParseException e) {
                        if (e ==null) {
                            userName = object.getString("nickname");
                            if (userName==null) {
                                userName = object.getUsername();
                            }
                            String userId = object.getObjectId();
                            boolean likeTruth=false;
                            List<String> likedArray = object.getList("likedGuides");
                            if (likedArray != null) {
                                Log.v("LASize", String.valueOf(likedArray.size()));
                                if (likedArray.size() > 1) {
                                    Iterator it = likedArray.iterator();
                                    while (it.hasNext()) {
                                        if (it.next().toString().equals(annoId)) {
                                            likeTruth = true;
                                            break;
                                        }
                                    }
                                } else{
                                    Log.v("LASize",String.valueOf(likedArray.get(0)));

                                    if (likedArray.get(0).equals(annoId)){
                                        likeTruth = true;
                                    }
                                }

                            }
//                            String userName = object.getUsername();
//                            String userId = object.getObjectId();
//                            Log.v("USER", userName);
//                            playIntent.putExtra("user", userName);
                            playIntent.putExtra("userId", parseUserCursor);
                            playIntent.putExtra("likeTruth", likeTruth);
                            context.startActivity(playIntent);
                        } else {
                            Log.e("PARSE ERROR", e.getMessage());
                            playIntent.putExtra("user", "Anonymous");
                            playIntent.putExtra("userId", "None");
                            context.startActivity(playIntent);
                        }
                    }
                });
            }
        });

        //delete activity code
        Activity activity = (Activity) context;
       boolean viewOwnProfile =  ((App) activity.getApplication()).getViewOwnProfile();
        if (viewOwnProfile){
            this.delButton.setVisibility(View.VISIBLE);
            delButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure you want to delete this Guidings?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            });
        }
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Annotation");
                    query.getInBackground(annoId, new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {
                            object.deleteInBackground();
                                Toast.makeText(context,"Your Guidings was Deleted", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(context, UserActivity.class);
                                i.putExtra("userId", ParseUser.getCurrentUser().getObjectId());
                                context.startActivity(i);


                            } else {
                                // something went wrong
                            }
                        }
                    });
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };
}
