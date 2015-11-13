package com.example.annie.slackproject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ListActivity {

    private ProgressDialog pDialog;

    // URL to get contacts JSON
    //private static String url = "http://api.androidhive.info/contacts/";

    // JSON Node names
    private static final String TAG_MEMBERS = "members";
    private static final String TAG_USERNAME = "name";
    private static final String TAG_REAL_NAME = "real_name";
    private static final String TAG_TITLE = "title";
    private static final String TAG_PICTURE = "image_24";
    private static final String TAG_PROFILE = "profile";


    // contacts JSONArray
    JSONArray members = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> memberList;

   /* @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactList = new ArrayList<HashMap<String, String>>();

        ListView lv = getListView();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        memberList = new ArrayList<HashMap<String, String>>();

        ListView lv = getListView();

        new GetAPIInfo().execute();

        // listening to single list item on click
       /* lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // selected item
                String product = ((TextView) view).getText().toString();

                // Launching new Activity on selecting single List Item
                Intent i = new Intent(getApplicationContext(), SingleListItem.class);
                // sending data to new activity
                i.putExtra("product", product);
                startActivity(i);

            }
        });*/

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // selected item
                String product = ((TextView) view).getText().toString();

                // Launching new Activity on selecting single List Item
                Intent i = new Intent(getApplicationContext(), SingleListItem.class);
                // sending data to new activity
                i.putExtra("product", product);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class GetAPIInfo extends AsyncTask<Void, Void, String> {
        private Exception exception;

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //TextView responseView = (TextView) findViewById(R.id.responseView);

        protected void onPreExecute() {

            progressBar.setVisibility(View.VISIBLE);
            //responseView.setText("Response View Text");
        }

        protected String doInBackground(Void... urls) {

            //String email = findViewById(R.id.emailText).getText().toString();
            // Do some validation here
            String jsonStr = null;

            try {
                URL url = new URL("https://slack.com/api/users.list?token=xoxp-5048173296-5048346304-5180362684-7b3865");
                //URL url = new URL("http://api.androidhive.info/contacts/");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
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

                // Getting JSON Array node
                members = jsonObject.getJSONArray(TAG_MEMBERS);

                // looping through All Contacts
                for (int i = 0; i < members.length(); i++) {
                    JSONObject c = members.getJSONObject(i);

                    String username = c.getString(TAG_USERNAME);
                    String real_name = c.getString(TAG_REAL_NAME);

                    // Phone node is JSON Object
                    JSONObject profile = c.getJSONObject(TAG_PROFILE);
                    String title = profile.getString(TAG_TITLE);
                    String picture = profile.getString(TAG_PICTURE);

                    // tmp hashmap for single contact
                    HashMap<String, String> member = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    member.put(TAG_USERNAME, username);
                    member.put(TAG_REAL_NAME, real_name);
                    member.put(TAG_TITLE, title);
                    member.put(TAG_PICTURE, picture);

                    if(memberList == null) {
                        System.out.println("A: CONTACT LIST IS NULL!!");
                        return;
                    }

                    if(member == null) {
                        System.out.println("CONTACT IS NULL!!");
                        return;
                    }

                    // adding contact to contact list
                    memberList.add(member);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(memberList == null) {
                System.out.println("B: CONTACT LIST IS NULL!!");
                return;
            }

            // Put parsed JSON into the ListView
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, memberList,
                    R.layout.list_item, new String[] { TAG_REAL_NAME, TAG_USERNAME,
                                    TAG_TITLE, TAG_PICTURE}, new int[] { R.id.name,
                                    R.id.email, R.id.mobile});

            setListAdapter(adapter);


            //responseView.setText(response);

        }
    }


}
