package com.stoicdesign.philiplauffenburger.guidings;


import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

//public class MapsActivity extends AppCompatActivity implements  OnMapReadyCallback, GoogleMap.OnMarkerClickListener,  GoogleMap.OnInfoWindowClickListener, GoogleApiClient.OnConnectionFailedListener {
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMapClickListener {
    static boolean videoTaken = false;
    private ClusterManager<MyItem> mClusterManager;
    private GoogleMap mMap;
    Context _this;
    private Map<String, ParseObject> annotation = new HashMap<String, ParseObject>();
    private Map<String, Integer> keyWordArray = new HashMap<>();
    private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private String userName, time, id, title, newDate, userId;
    private VideoView mVideoView;
    private String keyword;
    private View mymarkerview;
    RelativeLayout keywordSearch;
    FloatingActionButton searchFab;
    FrameLayout mapHolder;
    CardView videoCard;
    View infoWindowView;
    Marker marker;
    Marker lastClickedMarker;
    Number likes;
    PlaceAutocompleteFragment autocompleteFragment;
    //    private String user;
    private GoogleApiClient mGoogleApiClient;
    //    @Override
    Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _this = this;
        setContentView(R.layout.activity_map);

        mapHolder = (FrameLayout) findViewById(R.id.map_holder);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        toolbar = (Toolbar) findViewById(R.id.map_tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        searchFab = (FloatingActionButton) findViewById(R.id.search_fab);

        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        ft.hide(autocompleteFragment);
        ft.commit();
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                LatLngBounds latlngbnds = place.getViewport();
                Log.i("PLACE", "Place: " + place.getLatLng() + latlngbnds);
                if (latlngbnds != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latlngbnds, 0));
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 17));

                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("ERROR", "An error occurred: " + status);
            }
        });

//        FloatingActionButton fab = new FloatingActionButton(this);
//
//        fab.setImageResource(R.drawable.app_icon_3_13);
//
//        mapHolder.addView(fab);

        mapFragment.getMapAsync(this);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if (id == R.id.action_search) {

            keywordSearch = (RelativeLayout) findViewById(R.id.keyword_search);
            if (keywordSearch.getVisibility() == View.GONE) {
                keywordSearch.setVisibility(View.VISIBLE);
            } else {
                keywordSearch.setVisibility(View.GONE);
            }

        }
        if (id == R.id.action_video) {
            startActivity(new Intent(this, VideoRecorderActivity.class));
        }
        if (id == R.id.action_user) {

            Intent i = new Intent(this, UserActivity.class);
//            i.putExtra("user", ParseUser.getCurrentUser().getString("nickname"));
            i.putExtra("userId", ParseUser.getCurrentUser().getObjectId());
            startActivity(i);
        }
        if (id == R.id.action_reload) {
            mapHolder.removeView(videoCard);
            searchFab.setVisibility(View.VISIBLE);
            clusterHandler("Show All");
        }

        return super.onOptionsItemSelected(item);
    }


    public void onRecordVideoFABClick (View v) {
        startActivity(new Intent(this, VideoRecorderActivity.class));

    }


    public void playAnnotationVideo(String id) throws ParseException {

        ParseObject parseAnnotation = annotation.get(id);

        title = parseAnnotation.get("description").toString();
        String parseUserCursor = parseAnnotation.getParseUser("uploadedBy").getObjectId();


        Date timeDate = parseAnnotation.getCreatedAt();
        newDate = DateUtility.getTimeAgo(timeDate, this);

//        DateFormat formatter = new SimpleDateFormat("EEE, MMM d, ''yy");
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(timeDate);
//        String time = formatter.format(cal.getTime());

//        String time =cal.getDisplayName(Calendar.DATE,Calendar.SHORT, Locale.ENGLISH);
        String keywords = parseAnnotation.get("keywords").toString();
        Number likes = parseAnnotation.getNumber("numberOfLikes");
        ParseFile videoFile = parseAnnotation.getParseFile("videoFile");
        String uri = videoFile.getUrl();
        final Intent playIntent = new Intent(MapsActivity.this, VideoWatchActivity.class);

        playIntent.putExtra("title", title);

        playIntent.putExtra("time", newDate);
        playIntent.putExtra("keywords", keywords);
        playIntent.putExtra("uri", uri);
        playIntent.putExtra("annoId", id);
        final String annoId = id;
        if (likes == null) {
            likes = 0;
        }
        playIntent.putExtra("likes", likes.toString());


        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.getInBackground(parseUserCursor, new GetCallback<ParseUser>() {
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    userName = object.getString("nickname");
                    if (userName == null) {
                        userName = object.getUsername();
                    }
                    String userId = object.getObjectId();
                    boolean likeTruth = false;
                    List<String> likedArray = object.getList("likedGuides");
                    if (likedArray != null) {
                        if (likedArray.size() > 1) {
                            Iterator it = likedArray.iterator();
                            while (it.hasNext()) {
                                if (it.next().toString().equals(annoId)) {
                                    likeTruth = true;
                                    break;
                                }
                            }
                        } else {
                            Log.v("LASize", String.valueOf(likedArray.get(0)));

                            if (likedArray.get(0).equals(annoId)) {
                                likeTruth = true;
                            }
                        }

                    }

                    Log.v("likeTruth", String.valueOf(likeTruth));
                    playIntent.putExtra("likeTruth", likeTruth);
//                    playIntent.putExtra("user", userName);
                    playIntent.putExtra("userId", userId);
                    startActivity(playIntent);
                } else {
                    Log.e("PARSE ERROR", e.getMessage());
//                    playIntent.putExtra("user", "Anonymous");
                    playIntent.putExtra("userId", "None");
                    startActivity(playIntent);
                }
            }
        });

    }

    public View getInfoContents(Marker marker) {
        return null;
//            return mContents;
    }
//

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        //UNCOMMENT ALL BELOW TO GET BACK TO NORMAL
//        mMap.setInfoWindowAdapter(new CustomInfoWindow());
//        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            // here to request the missing permissions, and then overriding
//               public void onRequestPermissionsResult(int requestCode, String[] per,
//                                                      int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            clusterHandler(keyword);
            return;
        }

        mMap.setMyLocationEnabled(true);
        clusterHandler(keyword);
        //should decide better place to orient the map
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(63, 10)));
    }

    public void clusterHandler(final String keywordFilter) {
//        if (mMap==null){
//            onMapReady(mgoogleMap);
//        }
//        if (keyWordArray !=null) {
        keyWordArray.clear();
//        }
        mMap.clear();
        setUpClusterer();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Annotation");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ArrayList locations = new ArrayList();
                    for (ParseObject point : objects) {
                        String type = null;
                        String extension = MimeTypeMap.getFileExtensionFromUrl(point.getParseFile("videoFile").getUrl());
                        if (extension != null) {
                            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                        }

                        List<String> pointArray = point.getList("keywords");

                        if (!"video/quicktime".equals(type)) {

                            if (keywordFilter == null || keywordFilter.equals("Show All")) {
                                //location code
                                ParseGeoPoint location = point.getParseGeoPoint("location");

                                double lat = location.getLatitude();
                                double lng = location.getLongitude();
                                LatLng latlng = new LatLng(lat, lng);
                                MyItem newItem = new MyItem(lat, lng, point, point.getObjectId(), point.get("description").toString(), point.get("keywords").toString());
                                mClusterManager.setRenderer(new MyClusterRendering(getApplicationContext(), mMap, mClusterManager));
                                mClusterManager.addItem(newItem);


                                annotation.put(point.getObjectId(), point);

                                //KeyWordArray assembly code

                                for (int i = 0; i < pointArray.size(); i++) {
                                    Integer value = keyWordArray.get(pointArray.get(i));
                                    if (value == null) {
                                        keyWordArray.put(pointArray.get(i), 1);
                                    } else {
                                        keyWordArray.put(pointArray.get(i), value + 1);
                                    }
//                            }

                                }
                                //sort keyword array

//                        }
                            } else {
//                                keyWordArray.clear();
//                                Log.v("KEYWORDARRAY", keyWordArray.toString());
                                boolean matchFound = false; // dont be so eager that there is a match
                                for (String each : pointArray) {
                                    if (each.equals(keywordFilter)) {
                                        matchFound = true;
                                        break;
                                        // break; // optionally, you may break out
                                    }
                                }

                                if (matchFound) { // no match found?
                                    ParseGeoPoint location = point.getParseGeoPoint("location");

                                    double lat = location.getLatitude();
                                    double lng = location.getLongitude();
                                    LatLng latlng = new LatLng(lat, lng);
                                    MyItem newItem = new MyItem(lat, lng, point, point.getObjectId(), point.get("description").toString(), point.get("keywords").toString());
                                    mClusterManager.setRenderer(new MyClusterRendering(getApplicationContext(), mMap, mClusterManager));
                                    mClusterManager.addItem(newItem);


                                    annotation.put(point.getObjectId(), point);

                                    //KeyWordArray assembly code

                                    for (int i = 0; i < pointArray.size(); i++) {
                                        Integer value = keyWordArray.get(pointArray.get(i));
                                        if (value == null) {
                                            keyWordArray.put(pointArray.get(i), 1);
                                        } else {
                                            keyWordArray.put(pointArray.get(i), value + 1);
                                        }
                                    }

                                }

                            }
                            keyWordArray.put("Show All", 1000);
                            keyWordArray = MapUtil.sortByValue(keyWordArray);
//                            Log.v("KEYWORDARRAY", keyWordArray.toString());
                        }

                    }

                    keywordScrollGenerator(keyWordArray);
                } else {
                    Log.e("Error", e.toString());
                }
            }


        });
    }


    private void keywordScrollGenerator(Map<String, Integer> array) {

        final List<String> list = new ArrayList<String>(array.keySet());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, list);
        AutoCompleteTextView keywordAutoComplete = (AutoCompleteTextView) findViewById(R.id.auto_complete_keywords_search);
        keywordAutoComplete.setDropDownBackgroundDrawable(new ColorDrawable(Color.parseColor("#2F4F4F")));

        keywordAutoComplete.setAdapter(adapter);
        keywordAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String keywordSelected = parent.getItemAtPosition(position).toString();
                Log.v("keyword Slected", keywordSelected);
                clusterHandler(keywordSelected);
            }


        });
        TableLayout scrollTable = (TableLayout) findViewById(R.id.keywords_table);
        scrollTable.removeAllViews();
        TableRow scrollTableRow = new TableRow(this);
        scrollTableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        scrollTable.addView(scrollTableRow);
        Set set = array.keySet();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {

            String text = iterator.next().toString();
            TextView newKeyword = (TextView) getLayoutInflater().inflate(R.layout.keyword_text_view, null);
            GradientDrawable gdDefault = new GradientDrawable();
            gdDefault.setColor(Color.parseColor("#ffffff"));
            gdDefault.setCornerRadius(30);
            gdDefault.setStroke(5, Color.parseColor("#000000"));

            newKeyword.setBackground(gdDefault);
            newKeyword.setText(text);
//            Stringkeyword=text;
            newKeyword.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    TextView b = (TextView) v;
                    String text = b.getText().toString();

                    clusterHandler(text);
                }
            });
            scrollTableRow.addView(newKeyword);


        }


    }


    private void setUpClusterer() {
        // Declare a variable for the cluster manager.


        // Position the map.
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MyItem>(this, mMap);
//        final Collection<Marker> markerList = mClusterManager.getMarkerCollection().getMarkers();
        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                marker.hideInfoWindow();
            }
        });
        mMap.setOnCameraChangeListener(mClusterManager);

        mClusterManager
                .setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
                    @Override
                    public boolean onClusterClick(Cluster<MyItem> cluster) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(cluster.getPosition()));
                        mMap.moveCamera(CameraUpdateFactory.zoomIn());
                        return true;
                    }
                });

        mMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {

            @Override
            public void onInfoWindowClose(Marker marker) {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.guidings_blue_3_13));
                marker.hideInfoWindow();
            }
        });
//    }
//        mMap.setInfoWindowAdapter(new CustomWindowAdapter(this.getLayoutInflater(),
//                imageStringMapMarker, getApplicationContext()));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            //
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (mapHolder.getChildCount() > 2) {
                    mapHolder.removeView(videoCard);
                    searchFab.setVisibility(View.VISIBLE);
                if (lastClickedMarker != null) {
                    lastClickedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.guidings_blue_3_13));

                }
                }
                if (marker.getTitle() != null) {
                    lastClickedMarker = marker;
//                    CardView videoCard = new CardView(_this);
                    searchFab.setVisibility(View.INVISIBLE);

                    videoCard = (CardView) getLayoutInflater().inflate(R.layout.video_watch_card, null);
//                    findViewById(R.id.video_card_view);

                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 850);
                    videoCard.setLayoutParams(lp);
                    videoCard.bringToFront();
                    lp.gravity = Gravity.CENTER;
                    String fullSnip = marker.getSnippet();
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.guidings_yellow_3_13));

                    ParseObject point = annotation.get(fullSnip.substring(0, 10));
                    String keywords = fullSnip.substring(10);
                    id = fullSnip.substring(0, 10);
                    int lengthStrings = keywords.length();
                    keywords = keywords.substring(1, lengthStrings - 1);
                    TextView title = ((TextView) videoCard.findViewById(R.id.video_title));
//                    TextView snippet = ((TextView)videoCard.findViewById(R.id.snippet));

                    TableRow keywordRow = (TableRow) videoCard.findViewById(R.id.keyword_table_row_video);
                    TextView likeCount = ((TextView) videoCard.findViewById(R.id.like_count_watch));
                    TextView date = ((TextView) videoCard.findViewById(R.id.upload_time));
                    TextView user = ((TextView) videoCard.findViewById(R.id.uploaded_by));
                    ImageView thumbnail = ((ImageView) videoCard.findViewById(R.id.thumbnail));
                    thumbnail.bringToFront();
                    likes = point.getNumber("numberOfLikes");
                    time = DateUtility.getTimeAgo(point.getCreatedAt(), _this);
                    final String uri = point.getParseFile("thumbnail").getUrl();
                    ArrayList<String> keywordArray = new ArrayList<String>(Arrays.asList(keywords.split(", ")));
                    Iterator it = keywordArray.iterator();
                    int i = 0;
                    while (it.hasNext()) {

                        String text = it.next().toString();
                        TextView newKeyword = (TextView) getLayoutInflater().inflate(R.layout.keyword_text_view, null);
                        newKeyword.setText(text);
                        GradientDrawable gdDefault = new GradientDrawable();
                        gdDefault.setColor(Color.parseColor("#eeeeee"));
                        gdDefault.setCornerRadius(100);
                        gdDefault.setStroke(5, Color.parseColor("#000000"));
                        newKeyword.setPadding(20, 20, 20, 20);
                        newKeyword.setBackground(gdDefault);
                        keywordRow.addView(newKeyword, i);
                        i++;


                    }
                    userName = null;


                    try {
                        userName = point.getParseUser("uploadedBy").fetchIfNeeded().getString("nickname");
                        userId = point.getParseUser("uploadedBy").fetchIfNeeded().getObjectId();

                        if (userName == null) {
                            userName = point.getParseUser("uploadedBy").fetchIfNeeded().getUsername();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    user.setText(userName);

                    user.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(_this, UserActivity.class);
                            i.putExtra("userId", userId);
                            startActivity(i);
                        }
                    });
                    ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                    if (likes == null) {
                        likes = 0;
                    }
                    likeCount.setText(likes.toString());
                    date.setText(time);
                    title.setText(marker.getTitle());

                    Picasso.with(_this).load(uri).fit().into(thumbnail);


                    mapHolder.addView(videoCard);


//                    Intent i = new Intent(_this, videoPlayerCard.class);
//                    startActivity(i);
                    return true;
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                    mMap.moveCamera(CameraUpdateFactory.zoomIn());
                    return true;
                }
            }
        });
    }


    @Override
    public void onMapClick(LatLng latLng) {
//        if (marker !=null){
        if (mapHolder.getChildCount() > 2) {
            mapHolder.removeView(videoCard);
            searchFab.setVisibility(View.VISIBLE);

            lastClickedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.guidings_blue_3_13));
//        }
        }
    }


    public class MarkerCallback implements Callback {
        Marker marker = null;

        MarkerCallback(Marker marker) {
            this.marker = marker;
        }

        @Override
        public void onError() {
            Log.e(getClass().getSimpleName(), "Error loading thumbnail!");
        }

        @Override
        public void onSuccess() {
            if (marker != null && marker.isInfoWindowShown()) {
//                Log.v("onSuccess", "onsuccess called");
                marker.hideInfoWindow();

                marker.showInfoWindow();
            }
        }
    }

    public void onInfoWindowClick(Marker marker) {
        String fullSnip = marker.getSnippet();
        id = fullSnip.substring(0, 10);
//        Log.v("onIWC_ID", id);
        try {
            playAnnotationVideo(id);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void onConnectionFailed(ConnectionResult result) {
        Log.e("Connection Error", result.getErrorMessage());
    }


    public void onSearchCloseClick(View view) {
        mapHolder.removeView(videoCard);
        searchFab.setVisibility(View.VISIBLE);

        keywordSearch.setVisibility(View.GONE);
        clusterHandler("Show All");
    }

    public void thumbnailClickListener(View view) {

        final Fragment frag = new VideoWatchFragment();
        final Bundle args = new Bundle();


        ParseObject parseAnnotation = annotation.get(id);

        title = parseAnnotation.get("description").toString();
        String parseUserCursor = parseAnnotation.getParseUser("uploadedBy").getObjectId();


        Date timeDate = parseAnnotation.getCreatedAt();
        newDate = DateUtility.getTimeAgo(timeDate, this);

//        DateFormat formatter = new SimpleDateFormat("EEE, MMM d, ''yy");
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(timeDate);
//        String time = formatter.format(cal.getTime());

//        String time =cal.getDisplayName(Calendar.DATE,Calendar.SHORT, Locale.ENGLISH);
        String keywords = parseAnnotation.get("keywords").toString();
        Number likes = parseAnnotation.getNumber("numberOfLikes");
        ParseFile videoFile = parseAnnotation.getParseFile("videoFile");
        String uri = videoFile.getUrl();

        args.putString("title", title);
        args.putString("time", newDate);
        args.putString("keywords", keywords);
        args.putString("uri", uri);
        args.putString("annoId", id);
        final String annoId = id;
        if (likes == null) {
            likes = 0;
        }
        args.putInt("likes", likes.intValue());


        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.getInBackground(parseUserCursor, new GetCallback<ParseUser>() {
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    userName = object.getString("nickname");
                    if (userName == null) {
                        userName = object.getUsername();
                    }
                    String userId = object.getObjectId();
                    boolean likeTruth = false;
                    List<String> likedArray = object.getList("likedGuides");
                    if (likedArray != null) {
                        if (likedArray.size() > 1) {
                            Iterator it = likedArray.iterator();
                            while (it.hasNext()) {
                                if (it.next().toString().equals(annoId)) {
                                    likeTruth = true;
                                    break;
                                }
                            }
                        } else {

                            if (likedArray.get(0).equals(annoId)) {
                                likeTruth = true;
                            }
                        }

                    }

                    args.putBoolean("likeTruth", likeTruth);
//                    playIntent.putExtra("user", userName);
                    args.putString("userId", userId);
                    frag.setArguments(args);
                    getFragmentManager().beginTransaction()
                            .add(R.id.video_card_view, frag)
                            .commit();
                } else {
                    Log.e("PARSE ERROR", e.getMessage());
//                    playIntent.putExtra("user", "Anonymous");
                    args.putString("userId", "None");
                    frag.setArguments(args);
                    getFragmentManager().beginTransaction()
                            .add(R.id.video_card_view, frag)
                            .commit();
                }
            }

            ;
        });


    }

    public void onPlaceSearch(View v) {
        if (autocompleteFragment.isHidden()) {
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction()
                    .show(autocompleteFragment)
                    .commit();
        } else {
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction()
                    .hide(autocompleteFragment)
                    .commit();
        }
    }


}