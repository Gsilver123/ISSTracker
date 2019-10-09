package com.example.isstracker;

import android.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CoordinateViewModel extends ViewModel {

    private MutableLiveData<Pair<Double, Double>> mCoordinateLiveData;

    MutableLiveData<Pair<Double, Double>> getCurrentLatAndLong() {
        if (mCoordinateLiveData == null) {
            mCoordinateLiveData = new MutableLiveData<>();
        }
        return mCoordinateLiveData;
    }

    public CoordinateViewModel() { }
}
