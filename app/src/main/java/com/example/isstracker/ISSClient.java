package com.example.isstracker;

import android.util.Log;
import android.util.Pair;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.ToJson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

class ISSClient {

    private static final String TAG = ISSClient.class.getSimpleName();
    private static final String API_BASE_URL = "http://api.open-notify.org/";

    private static ISSClient sISSClient;
    private ISSService mISSService;

    private Subscription mSubscription;

    private CoordinateViewModel mCoordinateModel;

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

            private void setLatAndLong(
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
            }

            @Override
            @ToJson
            public void toJson(JsonWriter writer, Coordinate value) throws IOException { }
        };

        return adapter;
    }

    void setViewModel(CoordinateViewModel coordinateViewModel) {
        mCoordinateModel = coordinateViewModel;
    }

    void observeCurrentCoordinates() {
        if (mCoordinateModel == null) {
            return;
        }

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
                        mCoordinateModel.getCurrentLatAndLong().setValue(
                                new Pair<>(coordinate.getLatitude(), coordinate.getLongitude()));
                    }
                });
    }

    void unSubscribeSubscription() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    private Observable<Coordinate> getCurrentCoordinates() {
        return mISSService.getCoordinates();
    }
}
