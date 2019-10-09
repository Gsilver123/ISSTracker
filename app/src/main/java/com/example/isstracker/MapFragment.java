package com.example.isstracker;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends AppCompatActivity implements OnMapReadyCallback {

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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Bitmap bitmapIcon = ((BitmapDrawable) getDrawable(R.drawable.iss_image)).getBitmap();
        bitmapIcon = Bitmap.createScaledBitmap(bitmapIcon, 100, 100, false);

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker")
                .icon(BitmapDescriptorFactory.fromBitmap(bitmapIcon)));
        Log.d("Map", "Made it");
    }
}
