package com.bcilab.tremorapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageViewActivity extends AppCompatActivity {// * 이미지 클릭시 이미지 창

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        final ImageView result_image = findViewById(R.id.result_image);
        result_image.post(new Runnable() {
            @Override
            public void run() {
                Glide.with(ImageViewActivity.this).load(path)
                        .into(result_image);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.image_view, menu);

        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.close :
            {
                onBackPressed();
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
