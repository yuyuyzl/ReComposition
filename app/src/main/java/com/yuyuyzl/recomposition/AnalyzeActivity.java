package com.yuyuyzl.recomposition;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalyzeResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

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
        String category, subcategory;
        Gson gson = new Gson();
        @Override

        protected String doInBackground(String... params) {

            try {
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
                        mTextStatus.setText("Initializing Project Oxford...");
                      }
                });

                if (client == null) {
                    client = new VisionServiceRestClient(getString(R.string.subscription_key));
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      mTextStatus.setText("Analyzing Image...");
                    }
                });


                String[] features = {"All"};

              // Put the image into an input stream for detection.
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

                final AnalyzeResult visionAnalyzeResult = client.analyzeImage(inputStream, features);


                int scoremax=0;
                for(int i=0;i<visionAnalyzeResult.categories.size();i++){
                    if (visionAnalyzeResult.categories.get(i).score>scoremax)category=visionAnalyzeResult.categories.get(i).name;
                }

                if (!category.endsWith("_"))subcategory =category.split("_")[1];
                category=category.split("_")[0];

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextStatus.setText("Detected category: "+category+"\n"+"Subcategory: "+subcategory+"\n"+gson.toJson(visionAnalyzeResult.categories));
                    }
                });


            } catch (final Exception e){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextStatus.setText(e.getLocalizedMessage());

                        Log.e("ERROR-ANALYZING",e.getLocalizedMessage());
                        for(StackTraceElement ei:e.getStackTrace()) {

                            Log.e("ERROR-ANALYZING", ei.toString());
                        }
                    }
                });
            }




            return null;
        }
    }
}
