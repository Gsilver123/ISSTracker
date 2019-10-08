package com.example.isstracker;

import retrofit2.http.GET;
import rx.Observable;

public interface ISSService {
    @GET("/iss-now.json")
    Observable<Coordinate> getCoordinates();
}
