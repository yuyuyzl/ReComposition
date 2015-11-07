package com.yuyuyzl.recomposition;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class AnswerActivity extends Activity {
    private TextView textCategory,textMain;
    private ImageView imageThumbnail,imagePicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        textCategory= (TextView) findViewById(R.id.textCategory);
        textMain= (TextView) findViewById(R.id.textMain);
        imagePicked=(ImageView)findViewById(R.id.imagePicked);
        imageThumbnail=(ImageView)findViewById(R.id.imageThumbnail);
        textCategory.setText(this.getIntent().getStringExtra("Category"));
        textMain.setText(this.getIntent().getStringExtra("AnsString"));
        byte[] thumbnail=this.getIntent().getByteArrayExtra("thumbnail");
        imageThumbnail.setImageBitmap(BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.length));

        Bitmap bitmap=ImageHelper.loadSizeLimitedBitmapFromUri(this.getIntent().getData(),this.getContentResolver());
        Canvas canvas=new Canvas(bitmap);
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setARGB(255,128,128,128);
        canvas.drawLine(bitmap.getWidth() * 0.666F, 0, bitmap.getWidth() * 0.666F, bitmap.getHeight(), paint);
        canvas.drawLine(bitmap.getWidth()*0.333F,0,bitmap.getWidth() *0.333F,bitmap.getHeight(),paint);
        canvas.drawLine(0,bitmap.getHeight()*0.666F,bitmap.getWidth(),bitmap.getHeight()*0.666F,paint);
        canvas.drawLine(0,bitmap.getHeight()*0.333F,bitmap.getWidth(),bitmap.getHeight()*0.333F,paint);
        imagePicked.setImageBitmap(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_answer, menu);
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
}
