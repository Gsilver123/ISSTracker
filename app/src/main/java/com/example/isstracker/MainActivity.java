package com.example.isstracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static int MY_PERMISSIONS_INTERNET;

    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private Button mViewOnMapButton;

    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViewVariables();
        mViewOnMapButton.setOnClickListener(this);

        requestPermissions();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_INTERNET) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentCoordinates();
            }
        }
    }

    private void initializeViewVariables() {
        mLatitudeTextView = findViewById(R.id.latitude_position_text_view);
        mLongitudeTextView = findViewById(R.id.longitude_position_text_view);
        mViewOnMapButton = findViewById(R.id.view_on_map_btn);
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "I made it through");
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.INTERNET },
                    MY_PERMISSIONS_INTERNET);
        }
        else {
            getCurrentCoordinates();
        }
    }

    private void getCurrentCoordinates() {
        mSubscription = ISSClient.get()
                .getCurrentCoordinates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {
                    @Override
                    public Observable<?> call(Observable<? extends Void> observable) {
                        return observable.delay(2, TimeUnit.SECONDS);
                    }
                })
                .subscribe(new Observer<Coordinate>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "Task completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "Error " + e.getLocalizedMessage() + " was thrown");
                    }

                    @Override
                    public void onNext(Coordinate coordinate) {
                        assert coordinate != null;
                        mLatitudeTextView.setText(String.valueOf(coordinate.getLatitude()));
                        mLongitudeTextView.setText(String.valueOf(coordinate.getLongitude()));
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.view_on_map_btn) {
            Intent intent = new Intent(this, MapFragment.class);
            startActivity(intent);
        }
    }
}
