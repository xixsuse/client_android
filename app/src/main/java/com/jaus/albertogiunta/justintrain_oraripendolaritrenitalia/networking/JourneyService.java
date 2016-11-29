package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Journey;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.TrainHeader;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface JourneyService {

//    String SERVICE_ENDPOINT = "http:///46.101.130.226:8081";

    //INSTANT
    @GET("/departure/{departureStationId}/arrival/{arrivalStationId}/instant")
    Observable<Journey> getJourneyInstant(@Path("departureStationId") String departureId,
                                          @Path("arrivalStationId") String arrivalId,
                                          @Query("preemptive") boolean preemptive);


    // GET BEFORE TIME
    // http://46.101.130.226:8080/departure/5066/arrival/7104/look-forward?start-from=1463202600
    @GET("/departure/{departureStationId}/arrival/{arrivalStationId}/look-behind")
    Observable<Journey> getJourneyBeforeTime(@Path("departureStationId") String departureId,
                                             @Path("arrivalStationId") String arrivalId,
                                             @Query("end-at") String time,
                                             @Query("include-delays") boolean includeDelays,
                                             @Query("preemptive") boolean preemptive);

    // GET AFTER TIME
    @GET("/departure/{departureStationId}/arrival/{arrivalStationId}/look-ahead")
    Observable<Journey> getJourneyAfterTime(@Path("departureStationId") String departureId,
                                            @Path("arrivalStationId") String arrivalId,
                                            @Query("start-from") String time,
                                            @Query("include-delays") boolean includeDelays,
                                            @Query("preemptive") boolean preemptive);

    @GET("/departure/{departureStationId}/arrival/{arrivalStationId}/train/{trainId}/station/{trainDepartureStationId}")
    Observable<TrainHeader> getDelay(@Path("departureStationId") String departureStationId,
                                     @Path("arrivalStationId") String arrivalStationId,
                                     @Path("trainId") String trainId,
                                     @Path("trainDepartureStationId") String trainDepartureStationId);

    @GET("/departure/{departureStationId}/arrival/{arrivalStationId}/train/{trainId}")
    Observable<TrainHeader> getDelay(@Path("departureStationId") String departureStationId,
                                     @Path("arrivalStationId") String arrivalStationId,
                                     @Path("trainId") String trainId);

}