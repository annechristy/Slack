package com.example.annie.slackproject;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by annie on 11/16/15.
 */
public class DataSaver {

    Context ctx;

    public DataSaver(Context ctx) {
        this.ctx = ctx;
    }

    public void writeFile(String filename, String writeStr) {
        try{
            FileOutputStream fout = ctx.openFileOutput(filename, ctx.MODE_PRIVATE);
            try{
                fout.write(writeStr.getBytes());
                fout.close();
            } catch (IOException i) {
                i.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void writeImageFile(String filename, Bitmap image) {

        try{
            FileOutputStream fout = ctx.openFileOutput(filename, ctx.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 90, fout);
            fout.close();
        } catch (IOException i) {
            i.printStackTrace();
        }

    }


}
