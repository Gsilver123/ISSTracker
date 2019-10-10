package com.example.isstracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static int MY_PERMISSIONS_INTERNET;

    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private Button mViewOnMapButton;

    private CoordinateViewModel mCoordinateModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViewVariables();
        mViewOnMapButton.setOnClickListener(this);
        mCoordinateModel = ViewModelProviders.of(this).get(CoordinateViewModel.class);

        ISSClient.get().setViewModel(mCoordinateModel);
        setObserver();
        maybeRequestPermissionsAndStartObserving(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        ISSClient.get().unSubscribeSubscription();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_INTERNET) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ISSClient.get().observeCurrentCoordinates();
            }
        }
    }

    private void initializeViewVariables() {
        mLatitudeTextView = findViewById(R.id.latitude_position_text_view);
        mLongitudeTextView = findViewById(R.id.longitude_position_text_view);
        mViewOnMapButton = findViewById(R.id.view_on_map_btn);
    }

    static void maybeRequestPermissionsAndStartObserving(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "I made it through");
            ActivityCompat.requestPermissions(activity,
                    new String[]{ Manifest.permission.INTERNET },
                    MY_PERMISSIONS_INTERNET);
        }
        else {
            ISSClient.get().observeCurrentCoordinates();
        }
    }

    private void setObserver() {
        final Observer<Pair<Double, Double>> coordinateObserver =
                new Observer<Pair<Double, Double>>() {
                    @Override
                    public void onChanged(Pair<Double, Double> doubleDoublePair) {
                        mLatitudeTextView.setText(String.valueOf(doubleDoublePair.first));
                        mLongitudeTextView.setText(String.valueOf(doubleDoublePair.second));
                    }
                };

        mCoordinateModel.getCurrentLatAndLong().observe(this, coordinateObserver);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.view_on_map_btn) {
            Intent intent = new Intent(this, MapFragment.class);
            startActivity(intent);
            finish();
        }
    }
}
