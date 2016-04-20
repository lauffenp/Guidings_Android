package com.stoicdesign.philiplauffenburger.guidings;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VideoRecorderActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    static final int REQUEST_VIDEO_CAPTURE = 1;
    protected String[] PermissionsLocation =
    {Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 3;
    double latitude, longitude;
    private GoogleApiClient mGoogleApiClient = null;
    private Location mLastLocation = null;
    List<String> keyWordArray = new ArrayList<String>();
    List<String> autoCompleteKeyWordArrayStrings = new ArrayList<String>();
    private Map<String, Integer> autoCompleteKeyWordArray = new HashMap<>();
    ParseUser currentUser;
    TextView mLatitudeText;
    TextView mLongitudeText,txtuser;
    EditText description, hashtags, videoState;
    Array keywords;
    Context context;
    String descriptionString,hashtagsString,hashtagsString1,hashtagsString2,hashtagsString3;
    Bitmap thumb;
    TableLayout scrollTable;
    TableRow scrollTableRow;
    Boolean videoLarge=false;
    String realUri;
   Uri uri;
    boolean videoTaken;

    ImageButton videostate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) this.getApplication()).setVideoTaken(false);
        setContentView(R.layout.activity_video_recorder);


                ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

        Toast.makeText(getApplicationContext(),"Hey, remember, videos look better when you turn your phone to landscape! ", Toast.LENGTH_LONG).show();

        // Retrieve current user from Parse.com

        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Annotation");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ArrayList locations = new ArrayList();
                    for (ParseObject point : objects) {

                        List<String> pointArray = point.getList("keywords");
                        for (int i = 0; i < pointArray.size(); i++) {
                            Integer value = autoCompleteKeyWordArray.get(pointArray.get(i));
                            if (value == null) {
                                autoCompleteKeyWordArray.put(pointArray.get(i), 1);
                            } else {
                                autoCompleteKeyWordArray.put(pointArray.get(i), value + 1);
                            }
                        }

                    }

                }


                autoCompleteKeyWordArray = MapUtil.sortByValue(autoCompleteKeyWordArray);
                Set<String> keywordArraySet = autoCompleteKeyWordArray.keySet();
                Iterator iterator = keywordArraySet.iterator();
                while (iterator.hasNext()) {
                    autoCompleteKeyWordArrayStrings.add(iterator.next().toString());
                }
//                                       Log.v("autoCom", autoCompleteKeyWordArrayStrings.toString());

            }
        });


        // Convert currentUser into String
        String struser = currentUser.getUsername();





        Toolbar toolbar = (Toolbar) findViewById(R.id.video_toolbar);
        setSupportActionBar(toolbar);

        ImageButton buttonRecord = (ImageButton) findViewById(R.id.video_record_button);
        buttonRecord.setOnClickListener(this);

        Button buttonAdd = (Button) findViewById(R.id.add_button);
        buttonAdd.setOnClickListener(this);
//        mLatitudeText = (TextView) findViewById((R.id.latitude_text));
//        mLongitudeText = (TextView) findViewById((R.id.longitude_text));

        toolbar = (Toolbar) findViewById(R.id.video_toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                  .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (!videoTaken) {
            videoTaken=true;
            dispatchTakeVideoIntent();
        }


    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        if (id ==R.id.action_map) {
            startActivity(new Intent(this, MapsActivity.class));
        }
        if (id ==R.id.action_user) {
            Intent i = new Intent(this, UserActivity.class);
            i.putExtra("user", ParseUser.getCurrentUser().getUsername());
            i.putExtra("userId", ParseUser.getCurrentUser().getObjectId());

            startActivity(i);
        }
        if (id==R.id.action_post) {
            preDispatchVideo();

        }

        return super.onOptionsItemSelected(item);
    }

    private void buildArrayAdapter(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, autoCompleteKeyWordArrayStrings);
        MultiAutoCompleteTextView textView = (MultiAutoCompleteTextView)
                findViewById(R.id.hash_text1);
        textView.setDropDownBackgroundDrawable(new ColorDrawable(Color.parseColor("#2F4F4F")));

        textView.setAdapter(adapter);
        textView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
    }
    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

//        Log.v("ID_TAG", "should take video");
        uri = null;
//        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,20);
            takeVideoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 10983040L);

            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
//        }

        // Perform action on click
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {

            uri = data.getData();


            ImageButton videostate = (ImageButton) findViewById(R.id.video_record_button);




            realUri = getPathFromMediaUri(this, uri);
            File f = new File(Uri.parse(realUri).getPath());
            long s = f.length();
         if (s >= (10485760)){
                Toast.makeText(VideoRecorderActivity.this, "This Video is too large. Please record another one", Toast.LENGTH_SHORT).show();
//                Intent i = new Intent(this,videoCompress.class);
//                startActivityForResult(i,);
//
                videoLarge = true;
//                LoadJNI vk = new LoadJNI();
//                try {
//                    String workFolder=  getApplicationContext().getFilesDir().getAbsolutePath()+"/";
//                    //String[] complexCommand = {"ffmpeg","-i", "/sdcard/videokit/in.mp4"};
//                    String commandStr = "ffmpeg -i " + realUri+ " -b 150k "+ realUri.split("_")[0]+realUri.split("_")[1]+"b.mp4";
//                    vk.run(GeneralUtils.utilConvertToComplex(commandStr), workFolder, getApplicationContext());
//                    Log.i("test", "ffmpeg4android finished successfully");
//                } catch (Throwable e) {
//                    Log.e("test", "vk run exception.", e);
//                    try {
//                        String workFolder=  getApplicationContext().getFilesDir().getAbsolutePath()+"/";
//                        //String[] complexCommand = {"ffmpeg","-i", "/sdcard/videokit/in.mp4"};
//                        String commandStr = "ffmpeg -i " + uri+ " -b 150k " + uri+".mp4";
//                        vk.run(GeneralUtils.utilConvertToComplex(commandStr), workFolder, getApplicationContext());
//                        Log.i("test", "ffmpeg4android finished successfully");
//                    } catch (Throwable ee) {
//                        Log.e("test", "vk run exception2", ee);
//
//                    }
//                }

            } else {
                videoLarge = false;
            }

//            //This is the thumbnail creation code
            thumb = ThumbnailUtils.createVideoThumbnail(realUri,
                    MediaStore.Images.Thumbnails.MINI_KIND);

//            BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), thumb);
            videostate.setImageBitmap(thumb);
            buildArrayAdapter();



        }

    }





    public void dispatchPostVideo() {


//            //This is the thumbnail creation code
//        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(realUri,
//                MediaStore.Images.Thumbnails.MINI_KIND);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        thumb.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        // get byte array here
        byte[] byteThumb = stream.toByteArray();

//
        //thumbnail byte array is ready, now for video:
        byte[] byteArray = new byte[0];
        try {
            byteArray = readBytes(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Log.v("BYTES_THUMB", byteThumb.toString());
//        StringBuilder hashtagsbuilder = new StringBuilder();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        final ParseFile thumbnail = new ParseFile("thumbnail" + timeStamp + ".jpg", byteThumb, "image/png");
        thumbnail.saveInBackground();
        final ParseFile video = new ParseFile("video" + timeStamp + ".mp4", byteArray, "video/mp4");
//        try {
//            byte[] videoSize = video.getData();
//            Log.v("Byte[]",String.valueOf(videoSize.length));
//            Log.v("Byte[]toS",videoSize.toString());
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

            video.saveInBackground();


                    ParseObject newAnnotation = new ParseObject("Annotation");
                    newAnnotation.put("description", descriptionString);
                    ParseGeoPoint location = new ParseGeoPoint(latitude, longitude);
                    newAnnotation.put("location", location);
                    newAnnotation.put("uploadedBy", ParseUser.getCurrentUser());

        for (String s:keyWordArray){
//            hashtagsbuilder.append("#"+s+",");
            newAnnotation.add("keywords", s);
        }
//        String hashtags = hashtagsbuilder.toString();

//                    newAnnotation.put("hashtags", hashtags);
                    newAnnotation.put("videoFile", video);
                    newAnnotation.put("thumbnail", thumbnail);
                    newAnnotation.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            if (e == null) {

                                return;
                            } else {
                                Log.e("ERROR", e.toString());
                            }
                        }
                    });

        Toast.makeText(VideoRecorderActivity.this, "Your Guidings has been posted! Thanks!", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.video_record_button: {
                dispatchTakeVideoIntent();
                break;
            }
            case R.id.add_button:{
                hashtags = (AutoCompleteTextView) this.findViewById(R.id.hash_text1);

                if (TextUtils.isEmpty(hashtags.getText().toString())){
                    Toast.makeText(this, "Write a keyword and then press this button!",Toast.LENGTH_SHORT).show();
                    break;
                }
                String[] newkeywordArray = hashtags.getText().toString().split(", ");
                for (String s:newkeywordArray) {
                    keyWordArray.add(s);
//                Log.v("KWA",keyWordArray.toString());
                }


                TableLayout scrollTable=(TableLayout) findViewById(R.id.record_keywords);
                scrollTable.removeAllViews();
                final TableRow scrollTableRow= new TableRow(this);
                scrollTableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                scrollTable.addView(scrollTableRow);

                Iterator iterator = keyWordArray.iterator();
                hashtags.setText("");
                int i=0;
                while (iterator.hasNext()) {
                    final int index = i;
                    String text= iterator.next().toString();
                    TextView newKeyword = (TextView)getLayoutInflater().inflate(R.layout.keyword_text_view, null);


                    GradientDrawable gdDefault = new GradientDrawable();
                    gdDefault.setColor(Color.parseColor("#eeeeee"));
                    gdDefault.setCornerRadius(100);

                    gdDefault.setStroke(5, Color.parseColor("#000000"));
                    newKeyword.setPadding(5,5,5,5);
                    newKeyword.setBackground(gdDefault);
                    newKeyword.setText(text);

                    newKeyword.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            keyWordArray.remove(index);
                            scrollTableRow.removeView(v);
                        }
                    });
                    scrollTableRow.addView(newKeyword, i);
                     i++;

                }

                break;

            }



        }
    }
    private void preDispatchVideo(){
        description = (EditText) this.findViewById(R.id.description_text);


        videoState = (EditText) this.findViewById(R.id.video_state);
        if (TextUtils.isEmpty(description.getText().toString()) || (keyWordArray.size() == 0)) {
            Toast.makeText(this, "Please supply the description and at least one keyword", Toast.LENGTH_SHORT).show();
                return;
        } if (uri==null) {
            Toast.makeText(this, "Please record a video (Red VC)", Toast.LENGTH_SHORT).show();
            return;
        } if (videoLarge){
            Toast.makeText(this, "The recorded video is too large. Please take another one.", Toast.LENGTH_SHORT).show();
        return;
        }
//        } else {
//            Log.v("NOT RETURN", "didn't return");
            descriptionString = description.getText().toString();
            Log.v("keywordArray", Integer.toString(keyWordArray.size()));
            dispatchPostVideo();

    }
    private File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), "guidings");

        /**Create the storage directory if it does not exist*/
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        /**Create a media file name*/
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
//        Log.v("MEDIA_FILE", timeStamp);
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public byte[] readBytes(Uri uri) throws IOException {
        // this dynamically extends to take the bytes you read
        InputStream inputStream = getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    public String getPathFromMediaUri(Context context, Uri uri) {
        String result = null;

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        int col = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
        if (col >= 0 && cursor.moveToFirst())
            result = cursor.getString(col);
//        result="file://"+result;
        cursor.close();

        return result;
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
//        Log.v("Permissions_Length", permissions.toString());
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                }
                else {

//                     permission denied, boo! Disable the
//                     functionality that depends on this permission.
                }
                return;
            }
    case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
//                Log.v("GRANT_RESULTS",grantResults.toString());
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
            } case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Thanks! Start your Guidings now!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnected(Bundle bundle) {
//        Log.v("GOOGLE CONNECT", "got in");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
//            Log.v("FAILED CONNECT","SOmething didn't let you in");
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        Log.v("CONNECT SUCCESS", "you got in");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
//            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
//            Log.v("LOCATIONLAT", Double.toString(latitude));
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


}
