package com.subject.segmenttation;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.subject.segmentation.MySegmentation;

public class MainActivity extends AppCompatActivity  {

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btn = findViewById(R.id.pickkimage);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   openImagePicker();

            }
        });

        //progress dialog
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        //your uri
        int drawableId = R.drawable.shus;
        Uri uri = Utils.getUriFromDrawable(MainActivity.this, drawableId);

        //Subject Segmentation
        MySegmentation.SubjectSegment(MainActivity.this, uri, new MySegmentation.SegmentationCallback() {
            @Override
            public void onSegmentationComplete(Bitmap bitmap, int timeElapsed, String error) {
                if (bitmap != null) {
                    // Handle the segmented bitmap

                    ImageView imageView = findViewById(R.id.output_image);
                    imageView.setImageBitmap(bitmap);
                    progressDialog.dismiss();
                    Log.d("onSegmentation","bitmap : "+bitmap);
                } else {
                    // Handle the error

                    Log.d("onSegmentation","error : "+error);
                }
                // Handle the time elapsed
                Log.d("Time Elapsed", "Time: " + timeElapsed + "ms");
            }
        });
    }


}