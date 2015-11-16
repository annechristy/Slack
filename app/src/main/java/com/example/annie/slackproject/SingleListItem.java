package com.example.annie.slackproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class SingleListItem extends Activity{

    ImageView imageView;
    String imageURL;
    TextView usernameView;
    TextView realnameView;
    TextView titleView;

    int id;
    //Bitmap bitmap;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.single_list_item);


        usernameView = (TextView) findViewById(R.id.username);
        realnameView = (TextView) findViewById(R.id.realname);
        titleView = (TextView) findViewById(R.id.title);
        imageView = (ImageView) findViewById(R.id.image);







        Intent i = getIntent();
       id = (int) i.getSerializableExtra("memberid");
        HashMap<String, String> profileMap = (HashMap<String, String>) i.getSerializableExtra("profile");

        boolean network = (boolean) i.getSerializableExtra("network");

        //Bitmap image = fileList();
        String username = profileMap.get("name");
        String real_name = profileMap.get("real_name");
        String title = profileMap.get("title");
        imageURL = profileMap.get("image_192");

        if(network == false) {
            loadImageFromStorage(id+"_image");
        }
        else {


            new ImageDownloaderLocal().execute(imageURL);
        }

        usernameView.setText(username);
        realnameView.setText(real_name);
        titleView.setText(title);

    }

    private void loadImageFromStorage(String filename)
    {

        try {
            File f=new File(filename+".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imageView.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }


    public class ImageDownloaderLocal extends AsyncTask<String, Void, Bitmap> {

        ImageView image = (ImageView) findViewById(R.id.image);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Image progress dialog

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

            imageView.setImageBitmap(result);
        }
    }

}
