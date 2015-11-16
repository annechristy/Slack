package com.example.annie.slackproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.HashMap;

public class SingleListItem extends Activity{

    ImageView imageView;
    String imageURL;
    TextView usernameView;
    TextView realnameView;
    TextView titleView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.single_list_item);

        System.out.println("SINGLE LIST ITEM CLASS ACCESSED");

        usernameView = (TextView) findViewById(R.id.username);
        realnameView = (TextView) findViewById(R.id.realname);
        titleView = (TextView) findViewById(R.id.title);
        imageView = (ImageView) findViewById(R.id.image);


        Intent i = getIntent();
        // getting attached intent data
       // String testString = i.getStringExtra("member_name");
        HashMap<String, String> profileMap = (HashMap<String, String>) i.getSerializableExtra("profile");
        String username = profileMap.get("name");
        String real_name = profileMap.get("real_name");
        String title = profileMap.get("title");
        imageURL = profileMap.get("image_192"); //<--- This should be an actual image and not a string once I figure that out.

        new ImageDownloader().execute(imageURL);

        // displaying selected product name
        usernameView.setText(username);
        realnameView.setText(real_name);
        titleView.setText(title);

    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        ImageView image = (ImageView) findViewById(R.id.image);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Could create a progress dialog here if I chose to.
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            String image_url = url[0];
            Bitmap bitmap = null;
            try {
                // Download the image from the url.
                InputStream input = new java.net.URL(image_url).openStream();
                // Decode the image.
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Save the image to a file here once I'm sure I'm actually getting an image.

            imageView.setImageBitmap(result);
        }
    }
}
