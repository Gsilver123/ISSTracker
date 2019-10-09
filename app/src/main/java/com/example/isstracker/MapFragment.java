package com.example.isstracker;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends AppCompatActivity implements OnMapReadyCallback {

    private CoordinateViewModel mCoordinatedModel;
    private Marker mISSMarker;
    private GoogleMap mGoogleMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            transaction.replace(R.id.map, mapFragment);
        }

        mapFragment.getMapAsync(this);

        mCoordinatedModel = ViewModelProviders.of(this).get(CoordinateViewModel.class);

        ISSClient.get().setViewModel(mCoordinatedModel);
        setObserver();
    }

    private void setObserver() {
        final Observer<Pair<Double, Double>> coordinateObserver =
                new Observer<Pair<Double, Double>>() {
            @Override
            public void onChanged(Pair<Double, Double> latAndLong) {
                LatLng currentPosition = new LatLng(latAndLong.first, latAndLong.second);

                mISSMarker.setPosition(currentPosition);
                mISSMarker.setVisible(true);

                if (mGoogleMap != null) {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(currentPosition)
                            .zoom(4)
                            .build();

                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        };

        mCoordinatedModel.getCurrentLatAndLong().observe(this, coordinateObserver);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Bitmap bitmapIcon = ((BitmapDrawable) getDrawable(R.drawable.iss_image)).getBitmap();
        bitmapIcon = Bitmap.createScaledBitmap(bitmapIcon, 100, 100, false);

        mGoogleMap = googleMap;
        mISSMarker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("ISS")
                .snippet("International Space Station Location")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmapIcon)));
        Log.d("Map", "Made it");
    }
}
