package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Train;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface TrainService {

//    String SERVICE_ENDPOINT = "http:///46.101.130.226:8081";

    @GET("/train/{trainId}")
    Observable<Train> getTrainDetails(@Path("trainId") String trainId);

}
