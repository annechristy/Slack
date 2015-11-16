package com.example.annie.slackproject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ListActivity {
    // URL that accesses the Slack API with authentication token.
    String urlStr = "https://slack.com/api/users.list?token=xoxp-5048173296-5048346304-5180362684-7b3865";

    int numSavedAttributes = 3;

    // JSON Node names
    private static final String TAG_MEMBERS = "members";
    private static final String TAG_USERNAME = "name";
    private static final String TAG_REAL_NAME = "real_name";
    private static final String TAG_TITLE = "title";
    private static final String TAG_PICTURE = "image_192";
    private static final String TAG_PROFILE = "profile";

    // contacts JSONArray
    JSONArray members = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> memberList;

    boolean network = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        memberList = new ArrayList<HashMap<String, String>>();
        TextView textView = (TextView) findViewById(R.id.myText);
        textView.setText("Slack");

        ListView lv = getListView();


        // Check whether there is a network connection.
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            new GetAPIInfo().execute();
        } else {
            network = false;
            // Access saved data from last time.
            int numFiles = fileList().length;
            int numUsers = numFiles/numSavedAttributes;

            String username = "username not found";
            String title = "title not found";
            String real_name = "real name not found";
            Bitmap image = null;

            for(int i=0; i < numUsers; i++) {
                try{
                    // Get the username file.
                    //FileInputStream fin = openFileInput(fileList()[numSavedAttributes*(i-1)]);
                    FileInputStream fin_username = openFileInput(i+"_username");
                    FileInputStream fin_realname = openFileInput(i+"_realname");
                    FileInputStream fin_title = openFileInput(i+"_title");
                    //FileInputStream fin_image = openFileInput(i+"image");
                    try {
                        BufferedReader bufferedReader_username = new BufferedReader(new InputStreamReader(fin_username));
                        BufferedReader bufferedReader_realname = new BufferedReader(new InputStreamReader(fin_realname));
                        BufferedReader bufferedReader_title = new BufferedReader(new InputStreamReader(fin_title));

                        StringBuilder stringBuilder_username = new StringBuilder();
                        String line_username;
                        while ((line_username = bufferedReader_username.readLine()) != null) {
                            stringBuilder_username.append(line_username);
                        }
                        bufferedReader_username.close();
                        username = stringBuilder_username.toString();

                        StringBuilder stringBuilder_realname = new StringBuilder();
                        String line_realname;
                        while ((line_realname = bufferedReader_realname.readLine()) != null) {
                            stringBuilder_realname.append(line_realname);
                        }
                        bufferedReader_realname.close();
                        real_name = stringBuilder_realname.toString();

                        StringBuilder stringBuilder_title = new StringBuilder();
                        String line_title;
                        while ((line_title = bufferedReader_title.readLine()) != null) {
                            stringBuilder_title.append(line_title);
                        }
                        bufferedReader_title.close();
                        title = stringBuilder_title.toString();


                    } catch (IOException j) {
                        j.printStackTrace();
                    }




                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                // Store information about a single member in a HashMap.
                HashMap<String, String> member = new HashMap<String, String>();
                // Add member information to the member hashmap.
                member.put(TAG_USERNAME, username);
                member.put(TAG_REAL_NAME, real_name);
                member.put(TAG_TITLE, title);
                //member.put(TAG_PICTURE, picture);


                // Add the member to the member list.
                memberList.add(member);

            }


            // Put parsed JSON into the ListView.
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, memberList,
                    R.layout.list_item, new String[] {TAG_REAL_NAME, TAG_TITLE},
                    new int[] { R.id.name, R.id.subTitle });

            // Put the name and title into the scrolling listview object.
            setListAdapter(adapter);
        }



        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the relevant member as a HashMap
                HashMap<String, String> selectedMember = memberList.get(position);
;
                // Launching new Activity on selecting single List Item
                Intent i = new Intent(getApplicationContext(), SingleListItem.class);
                // sending data to new activity
                i.putExtra("profile", selectedMember);
                i.putExtra("network", network);
                i.putExtra("memberid", position);

                // Open the new activity that shows the user's profile.
                startActivity(i);
            }
        });


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

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class GetAPIInfo extends AsyncTask<Void, Void, String> {

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        protected void onPreExecute() {

            progressBar.setVisibility(View.VISIBLE);

        }

        protected String doInBackground(Void... urls) {

            String jsonStr = null;

            try {
                URL url = new URL(urlStr);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    // Read in the JSON information
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    jsonStr = stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }

            return jsonStr;
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);

            if(response != null) try {
                JSONObject jsonObject = new JSONObject(response);

                // Get the JSON Array node.
                members = jsonObject.getJSONArray(TAG_MEMBERS);

                // Loop through all the users in the user list.
                for (int i = 0; i < members.length(); i++) {
                    JSONObject c = members.getJSONObject(i);

                    String username = c.getString(TAG_USERNAME);
                    String real_name = c.getString(TAG_REAL_NAME);
                    // There is a nested JSON object, profile, that contains info we want.
                    JSONObject profile = c.getJSONObject(TAG_PROFILE);
                    String title = profile.getString(TAG_TITLE);
                    String picture = profile.getString(TAG_PICTURE);


                    DataSaver dataSaver = new DataSaver(getApplicationContext());

                    dataSaver.writeFile(i+"_username", username);
                    dataSaver.writeFile(i+"_realname", real_name);
                    dataSaver.writeFile(i+"_title", title);
                    // Save the image too.
                    ImageDownloader imageDownloader = new ImageDownloader(picture, i+"_image", getApplicationContext());
                    imageDownloader.execute();

                    // Store information about a single member in a HashMap.
                    HashMap<String, String> member = new HashMap<String, String>();

                    // Add member information to the member hashmap.
                    member.put(TAG_USERNAME, username);
                    member.put(TAG_REAL_NAME, real_name);
                    member.put(TAG_TITLE, title);
                    member.put(TAG_PICTURE, picture);
                    // Add the member to the member list.
                    memberList.add(member);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Put parsed JSON into the ListView.
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, memberList,
                    R.layout.list_item, new String[] {TAG_REAL_NAME, TAG_TITLE},
                    new int[] { R.id.name, R.id.subTitle });

            // Put the name and title into the scrolling listview object.
            setListAdapter(adapter);
        }

    }

}
