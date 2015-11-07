package com.yuyuyzl.recomposition;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private String ansString="",ansCategory;

    private byte[] thumbnail;
    private void provideAnswer(){
        Intent intent=new Intent(this,AnswerActivity.class);
        //ansString="建议你试着把人物的面部放到黄金分割线处以获取更加生动的效果。";


        intent.setData(this.getIntent().getData());
        intent.putExtra("AnsString", ansString);
        intent.putExtra("thumbnail",thumbnail);
        intent.putExtra("Category",ansCategory);
        startActivity(intent);
    }

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
        //Gson gson = new Gson();



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


                float scoremax=0;
                for(int i=0;i<visionAnalyzeResult.categories.size();i++){
                    if (visionAnalyzeResult.categories.get(i).score>scoremax){
                        category=visionAnalyzeResult.categories.get(i).name;
                        scoremax=visionAnalyzeResult.categories.get(i).score;
                    }
                }

                if (!category.endsWith("_"))subcategory =category.split("_")[1];
                category=category.split("_")[0];
                ansCategory=" "+categoryHelper.toFirstUppercase(category)+(subcategory==null?"":", "+categoryHelper.toFirstUppercase(subcategory));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //mTextStatus.setText("Detected category: "+category+"\n"+(subcategory==null?"":("Subcategory: "+subcategory+"\n"))+gson.toJson(visionAnalyzeResult.categories));
                        //mTextStatus.setText("Detected category: " + category + "\n" + (subcategory == null ? "" : ("Subcategory: " + subcategory + "\n")));
                        //mTextStatus.setText("Calculating area of interests...");
                        mTextStatus.setText(ansCategory);
                    }
                });

                inputStream.reset();
                thumbnail=client.getThumbnail(200, 200, true, inputStream);
                //thumbnail = BitmapFactory.decodeByteArray(thumbnailarray, 0, thumbnailarray.length);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextStatus.setText("Organizing my opinion...");
                        //mTextStatus.setText(String.valueOf(thumbnail.length));
                    }
                });

                switch (category) {
                    case "abstract":
                        ansString+="    我有点看不清楚，这是一幅抽象画吗？\n";
                        break;
                    case "outdoors":
                    case "building":
                    case "dark":
                    case "trans":
                    case "sky":
                        ansString+="    把分割画面的长直线放在三等分线上有奇效。\n";
                    case "people":
                    case "food":
                    case "animal":
                    case "plant":
                    case "text":
                    default:
                        ansString+="    如果可行，把被摄主体放在三等分线上可以使画面更生动活泼。\n";

                        provideAnswer();
                        break;
                }


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
