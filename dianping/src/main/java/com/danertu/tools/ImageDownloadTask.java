package com.danertu.tools;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class ImageDownloadTask extends AsyncTask<Object, Object, Bitmap> {
    private ImageView imageView = null;

    @Override
    protected Bitmap doInBackground(Object... params) {
        Bitmap bmp = null;
        imageView = (ImageView) params[1];
        try {
            bmp = BitmapFactory.decodeStream(new URL((String) params[0]).openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmp;
    }

    protected void onPostExecute(Bitmap result) {
        imageView.setImageBitmap(result);
    }

    public void setViewImage(ImageView v, String value) {
        new ImageDownloadTask().execute(value, v);
    }
}
