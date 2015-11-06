package com.yuyuyzl.recomposition;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.microsoft.projectoxford.vision.VisionServiceClient;

public class AnalyzeActivity extends Activity {
    private Uri mImageUri;
    private Bitmap mBitmap;
    private TextView mTextStatus;
    private VisionServiceClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        mTextStatus=(TextView)findViewById(R.id.textStatus);
        mImageUri=this.getIntent().getData();



        new doAnalyze().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_analyze, menu);
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
    private class doAnalyze extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextStatus.setText("Compressing Image...");
                }
            });

            mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                    mImageUri, getContentResolver());
            if (mBitmap != null) {
                // Show the image on screen.
                //ImageView imageView = (ImageView) findViewById(R.id.selectedImage);
                //imageView.setImageBitmap(mBitmap);

                // Add detection log.
                Log.d("AnalyzeActivity", "Image: " + mImageUri + " resized to " + mBitmap.getWidth()
                        + "x" + mBitmap.getHeight());
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextStatus.setText("Connecting to Project Oxford...");
                }
            });
            return null;
        }
    }
}