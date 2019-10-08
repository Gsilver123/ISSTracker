package com.example.isstracker;

class Coordinate {

    private long mTimeStamp;
    private double mLatitude;
    private double mLongitude;

    @SuppressWarnings("unused")
    Coordinate() { }

    long getTimeStamp() {
        return mTimeStamp;
    }

    void setTimeStamp(long timeStamp) {
        mTimeStamp = timeStamp;
    }

    double getLatitude() {
        return mLatitude;
    }

    void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    double getLongitude() {
        return mLongitude;
    }

    void setLongitude(double longitude) {
        mLongitude = longitude;
    }
}
