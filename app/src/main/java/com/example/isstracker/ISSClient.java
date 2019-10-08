package com.example.isstracker;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.ToJson;

import java.io.IOException;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import rx.Observable;

class ISSClient {

    private static final String API_BASE_URL = "http://api.open-notify.org/";

    private static ISSClient sISSClient;
    private ISSService mISSService;

    private ISSClient() {
        Moshi moshi = new Moshi.Builder().add(getJsonAdapter()).build();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build();

        mISSService = retrofit.create(ISSService.class);
    }

    static ISSClient get() {
        if (sISSClient == null) {
            sISSClient = new ISSClient();
        }

        return sISSClient;
    }

    private JsonAdapter<Coordinate> getJsonAdapter() {
        JsonAdapter<Coordinate> adapter = new JsonAdapter<Coordinate>() {
            @Override
            @FromJson
            public Coordinate fromJson(JsonReader reader) throws IOException {
                Coordinate coordinate = new Coordinate();
                reader.beginObject();
                while(reader.hasNext()) {
                    String name = reader.nextName();

                    switch (name) {
                        case "timestamp":
                            coordinate.setTimeStamp(reader.nextLong());
                            break;
                        case "iss_position":
                            setLatAndLong(coordinate, reader);
                            break;
                        default:
                            reader.skipValue();
                            break;
                    }
                }
                reader.endObject();

                return coordinate;
            }

            private Coordinate setLatAndLong(
                    Coordinate coordinate, JsonReader reader) throws IOException{
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();

                    if (name.equals("longitude")) {
                        coordinate.setLongitude(Double.parseDouble(reader.nextString()));
                    }
                    else if (name.equals("latitude")) {
                        coordinate.setLatitude(Double.parseDouble(reader.nextString()));
                    }
                    else {
                        reader.skipValue();
                    }
                }
                reader.endObject();

                return coordinate;
            }

            @Override
            @ToJson
            public void toJson(JsonWriter writer, Coordinate value) throws IOException { }
        };

        return adapter;
    }

    Observable<Coordinate> getCurrentCoordinates() {
        return mISSService.getCoordinates();
    }
}
