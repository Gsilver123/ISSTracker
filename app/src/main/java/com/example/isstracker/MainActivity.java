package com.example.isstracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private Button mViewOnMapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLatitudeTextView = findViewById(R.id.latitude_position_text_view);
        mLongitudeTextView = findViewById(R.id.longitude_position_text_view);
        mViewOnMapButton = findViewById(R.id.view_on_map_btn);
    }
}
