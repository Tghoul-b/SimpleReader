package com.project.reader.ui.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.example.reader.R;
import com.project.reader.ui.widget.test.factory.PicturesPageFactory;
import com.project.reader.ui.widget.test.view.CoverPageView;

public class testActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        CoverPageView coverPageView=findViewById(R.id.test_view);
        int []picturesId={R.drawable.ic_aboutapp,R.drawable.ic_arrow_dropdown};
        PicturesPageFactory factory=new PicturesPageFactory(this,picturesId);
        coverPageView.setPageFactory(factory);
    }
}