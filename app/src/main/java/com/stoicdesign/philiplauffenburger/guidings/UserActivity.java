package com.stoicdesign.philiplauffenburger.guidings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UserActivity extends AppCompatActivity implements KeywordAddDialogFragment.KeywordAddDialogListener {


    // Declare Variable
    TableLayout table;
    TableRow keywordRow, linkRow;
    MenuItem logout;
    ImageView profilePic;
    ParseUser currentUser, viewedUser;
    String user, cruser, linkUrl;
    String userId;
    CharSequence keywordArrayChar[];
    TextView txtuser, txtvideos, txtUrl;
    boolean userCheck = false;
    Context _this;
    Intent i;
    String txtUrlString;
    private Map<String, Integer> keyWordArray = new HashMap<>();
    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from singleitemview.xml
        setContentView(R.layout.activity_user);
        _this = this;
//        user = getIntent().getExtras().get("user").toString();


        userId = getIntent().getExtras().get("userId").toString();


        Toolbar toolbar = (Toolbar) findViewById(R.id.user_toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        // Retrieve current user from Parse.com
        currentUser = ParseUser.getCurrentUser();


        // Convert currentUser into String
        cruser = currentUser.getObjectId().toString();

        // Locate TextView in welcome.xml
        txtuser = (TextView) findViewById(R.id.txtuser);
        txtvideos = (TextView) findViewById(R.id.users_videos_text);
        linkRow = (TableRow) findViewById(R.id.user_link);
        ImageView megaphone = (ImageView) findViewById(R.id.link_icon);
        profilePic = (ImageView) findViewById(R.id.profile_picture);

        ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("objectId", userId);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    //List contain object with specific user id.

                    viewedUser = objects.get(0);

                    buildUserProfile();

                } else {
                    // error
                }
            }
        });
    }

    public void buildUserProfile() {
        Object userName = viewedUser.get("nickname");
        if (userName == null) {
            userName = viewedUser.getUsername();

        }
        user = userName.toString();

        txtuser.setText(user);

        txtUrl = new TextView(this);
        txtUrlString = viewedUser.getString("homepageUrl");
        ParseFile parsePic = viewedUser.getParseFile("profilePicture");
        if (parsePic != null) {
            parsePic.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        profilePic.setImageBitmap(bmp);
                    } else {
                        Log.e("ProfilePicError", e.getMessage());
                    }
                }
            });
        }


        if (cruser.equals(userId)) {
            userCheck = true;
            ((App) this.getApplication()).setViewOwnProfile(true);

            txtvideos.setText("Your Recent Guidings:");
            profilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,
                            "Select Picture"), SELECT_PICTURE);
                }
            });
            if (txtUrlString == null) {
                txtUrl.setTextColor(Color.parseColor("#2F4F4F"));

                txtUrl.setText("Link to your personal/favorite website!");


            } else {
                txtUrl.setTextColor(Color.parseColor("#2F4F4F"));
                txtUrl.setText(txtUrlString);
                txtUrl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!txtUrlString.startsWith("http://") && !txtUrlString.startsWith("https://")) {
                            String txtUrlStringmod = "http://" + txtUrlString;
                            Uri uri = Uri.parse(txtUrlStringmod);
                            i = new Intent(Intent.ACTION_VIEW, uri);

                        } else {
                            Uri uri = Uri.parse(txtUrlString);
                            i = new Intent(Intent.ACTION_VIEW, uri);

                        }
                        try {
                            startActivity(i);
                        } catch (Exception e) {
                            Log.e("ERROR", e.getMessage());
                            Toast.makeText(_this, "OOPS, looks like a bad url", Toast.LENGTH_SHORT).show();

                        }


                    }
                });
            }


        } else {
            ((App) this.getApplication()).setViewOwnProfile(false);

            if (txtUrlString == null) {
                txtUrl.setTextColor(Color.parseColor("#2F4F4F"));

                txtUrl.setText(user + " hasn't set their personal website yet");

            } else {
                txtUrl.setTextColor(Color.parseColor("#2F4F4F"));
                txtUrl.setText(txtUrlString);
                txtUrl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!txtUrlString.startsWith("http://") && !txtUrlString.startsWith("https://")) {
                            String txtUrlStringmod = "http://" + txtUrlString;
                            Uri uri = Uri.parse(txtUrlStringmod);
                            i = new Intent(Intent.ACTION_VIEW, uri);

                        } else {
                            Uri uri = Uri.parse(txtUrlString);
                            i = new Intent(Intent.ACTION_VIEW, uri);

                        }
                        try {
                            startActivity(i);
                        } catch (Exception e) {
                            Log.e("ERROR", e.getMessage());
                            Toast.makeText(_this, "OOPS, looks like a bad url", Toast.LENGTH_SHORT).show();

                        }


                    }
                });
            }

            //find user whose profile is being viewe
        }
        linkRow.addView(txtUrl, 1);


        final TableRow keywordRow = (TableRow) findViewById(R.id.user_keywords);


        final List<String> keywordsList = viewedUser.getList("subscribedKeywords");
        if (keywordsList != null) {
            Iterator it = keywordsList.iterator();
            int i = 0;
            while (it.hasNext()) {
                final int index = i;
                final String newKeywordString = it.next().toString();
                TextView newKeyword = (TextView) getLayoutInflater().inflate(R.layout.keyword_text_view, null);
                newKeyword.setText(newKeywordString);
                Log.v("KEYWORD STRING", newKeywordString);
                final Collection<String> keywordArrayforRemoval = Arrays.asList(newKeywordString);
                if (userCheck) {
                    newKeyword.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            keywordRow.removeView(v);

                            keywordsList.remove(index);
                            viewedUser.removeAll("subscribedKeywords", keywordArrayforRemoval);
                            viewedUser.saveInBackground();

                        }
                    });
                }
                GradientDrawable gdDefault = new GradientDrawable();
                gdDefault.setColor(Color.parseColor("#eeeeee"));
                gdDefault.setCornerRadius(100);
                gdDefault.setStroke(5, Color.parseColor("#000000"));
                newKeyword.setPadding(20, 20, 20, 20);
                newKeyword.setBackground(gdDefault);
                keywordRow.addView(newKeyword, i + 1);
                i++;
            }


            // Set the currentUser String into TextView

        } else {
            TextView newKeyword = (TextView) getLayoutInflater().inflate(R.layout.keyword_text_view, null);
            newKeyword.setText("No Keywords Set Yet");
            GradientDrawable gdDefault = new GradientDrawable();
            gdDefault.setColor(Color.parseColor("#eeeeee"));
            gdDefault.setCornerRadius(100);
            gdDefault.setStroke(5, Color.parseColor("#000000"));
            newKeyword.setPadding(20, 20, 20, 20);
            newKeyword.setBackground(gdDefault);
            keywordRow.addView(newKeyword, 1);
        }
        loadVideoSlide();


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        if (id == R.id.action_video) {
            startActivity(new Intent(this, VideoRecorderActivity.class));
        }
        if (id == R.id.action_map) {
            startActivity(new Intent(this, MapsActivity.class));
        }
        if (id == R.id.menu_logout) {
            ParseUser.logOut();
            Intent logoutI = new Intent(this, LoginSignupActivity.class);
            startActivity(logoutI);
        }
        if (id == R.id.action_user) {

            Intent i = new Intent(this, UserActivity.class);
//            i.putExtra("user", ParseUser.getCurrentUser().getString("nickname"));
            i.putExtra("userId", ParseUser.getCurrentUser().getObjectId());
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadVideoSlide() {
//        final String stuser = ParseUser.getCurrentUser().getObjectId();
//         Log.v("STUUSER", stuser);
        final List<VideoSlideViewModel> viewModels = new ArrayList<VideoSlideViewModel>();
        final ListView table = (ListView) findViewById(R.id.user_video_table);
//        final TableRow row = new TableRow(this);
//        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//        table.addView(row);
        final Context _this = this;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Annotation");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
//                    Array<>
                    for (ParseObject point : objects) {
                        String type = null;
                        String extension = MimeTypeMap.getFileExtensionFromUrl(point.getParseFile("videoFile").getUrl());
                        if (extension != null) {
                            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                        }
                        if (!"video/quicktime".equals(type)) {
//                        Log.v("USER", point.getParseUser("uploadedBy").getObjectId());
                            if (point.getParseUser("uploadedBy").getObjectId().equals(userId)) {

                                VideoSlideViewModel newSlide = new VideoSlideViewModel(point.get("description").toString(), point.getParseFile("thumbnail").getUrl(), point.getObjectId());
                                //maybe dumb method of putting the last first
                                viewModels.add(0, newSlide);
                            }
                            List<String> pointArray = point.getList("keywords");
                            if (pointArray != null) {
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
                    }
                    keyWordArray.put("NONE", 1000);
                    keyWordArray = MapUtil.sortByValue(keyWordArray);

                    keywordArrayChar = keyWordArray.keySet().toArray(new CharSequence[0]);
                    VideoAdapter adapter = new VideoAdapter(_this, viewModels);
                    table.setAdapter(adapter);
                }
            }
        });


    }

    public void onKeywordClick(View v) {
        if (userCheck) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            DialogFragment newFragment = KeywordAddDialogFragment.newInstance(keywordArrayChar);
            newFragment.show(ft, "keywordAdd");
        }
    }

    public void onSelectKeyword(String keyword) {
        Log.v("keyword selecteD:", keyword);
        if (keyword.equals("NONE")) {
            return;
        } else {
            viewedUser.add("subscribedKeywords", keyword);
            viewedUser.saveInBackground();


//            TextView newKeyword = new TextView(this);
            TextView newKeyword = (TextView) getLayoutInflater().inflate(R.layout.keyword_text_view, null);
            final Collection<String> keywordArrayforRemoval = Arrays.asList(keyword);
            final TableRow keywordRow = (TableRow) findViewById(R.id.user_keywords);

            newKeyword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    keywordRow.removeView(v);

                    viewedUser.removeAll("subscribedKeywords", keywordArrayforRemoval);
                    viewedUser.saveInBackground();

                }
            });

            newKeyword.setText(keyword);
            GradientDrawable gdDefault = new GradientDrawable();
            gdDefault.setColor(Color.parseColor("#eeeeee"));
            gdDefault.setCornerRadius(100);
            gdDefault.setStroke(5, Color.parseColor("#000000"));
            newKeyword.setPadding(20, 20, 20, 20);
            newKeyword.setBackground(gdDefault);
            keywordRow.addView(newKeyword, 1);

        }
    }

    public void onLinkClick(View v) {

        if (userCheck) {
//    public void onMegaphoneClick(){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter the url of your personal/favorite webpage");

// Set up the input
            final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
            builder.setView(input);

// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    linkUrl = input.getText().toString();
                    txtUrl.setText(linkUrl);
                    viewedUser.put("homepageUrl", linkUrl);
                    viewedUser.saveInBackground();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    }

    public void onNameClick(View v) {
        if (userCheck) {
//    public void onMegaphoneClick(){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter your nickname!");

// Set up the input
            final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            builder.setView(input);

// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String nickname = input.getText().toString();
                    txtuser.setText(nickname);
                    user = nickname;
                    viewedUser.put("nickname", nickname);
                    viewedUser.saveInBackground();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImage = data.getData();

                    selectedImagePath = getPath(selectedImage);
                if (selectedImagePath== null){
                    Toast.makeText(UserActivity.this, "Selected Image file path can not be located. Make sure the image is present on your local storage.", Toast.LENGTH_LONG).show();
                    return;
                }

//                Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);
                Bitmap bitmap = null;
                try {
                    bitmap = getBitmapFromUri(selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                // Compress image to lower quality scale 1 - 100
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                }
                byte[] image = stream.toByteArray();
                if (image.length < 10485760) {
                    // Create the ParseFile
                    ParseFile file = new ParseFile("profilePicture", image);
                    // Upload the image into Parse Cloud
                    file.saveInBackground();
                    profilePic.setImageBitmap(bitmap);


                    viewedUser.put("profilePicture", file);
                    viewedUser.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                return;
                            } else {
                                Log.e("ERROR", e.toString());
                            }
                        }
                    });

                    Toast.makeText(UserActivity.this, "Your Profile Picture has been updated", Toast.LENGTH_SHORT).show();


                    // Show a simple toast message
                } else {
                    Toast.makeText(UserActivity.this, "Profile Picture is too large. Please compress and Try again", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        String res = null;

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try{
        cursor = getContentResolver().query(uri, projection, null, null, null);
    } catch (Exception e){
        Log.e("Error", e.getMessage());
        return null;
        }
        if (cursor.moveToFirst()) {
            ;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        if (res != null) {
            Log.v("CURSOR", res);

            return res;
        } else {
            return uri.getPath();
        }
    }

    private byte[] readInFile(String path) throws IOException {
        // TODO Auto-generated method stub
        byte[] data = null;
        File file = new File(path);
        InputStream input_stream = new BufferedInputStream(new FileInputStream(
                file));
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        data = new byte[16384]; // 16K
        int bytes_read;
        while ((bytes_read = input_stream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytes_read);
        }
        input_stream.close();
        return buffer.toByteArray();

    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }


}
