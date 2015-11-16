package com.example.annie.slackproject;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by annie on 11/16/15.
 */
public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
    String image_url;
    String filename;
    Context ctx;
    Bitmap image;

    public ImageDownloader(String url, String filename, Context ctx) {
        this.image_url = url;
        this.filename = filename;
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Do nothing.
    }

    @Override
    protected Bitmap doInBackground(String... url) {
       // String image_url = url[0];
        Bitmap bitmap = null;
        try {
            // Download the image from the url.
            InputStream input = new java.net.URL(image_url).openStream();
            // Decode the image.
            bitmap = BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        image = bitmap;
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        // Save the image to a file here once I'm sure I'm actually getting an image.
        //DataSaver imageSaver = new DataSaver(ctx);
        //imageSaver.writeImageFile(filename, result);

        saveToInternalStorage(result, this.filename);

    }

    public Bitmap getImage() {
        return image;
    }

    private void saveToInternalStorage(Bitmap bitmapImage, String filename){
        ContextWrapper cw = new ContextWrapper(ctx);
        // path to /data/data/yourapp/app_data/imageDir
       // File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        //File mypath=new File(directory, filename+".jpg");
        File myfile = new File(filename+".jpg");

        FileOutputStream fos;
        try {

            fos = new FileOutputStream(myfile);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //return directory.getAbsolutePath();
    }


}