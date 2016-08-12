package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.NotificationData;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.SolutionList;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by albertogiunta on 18/06/16.
 */
public interface JourneyService {

    String SERVICE_ENDPOINT = "http:///46.101.130.226:8080";

    //INSTANT
    @GET("/departure/{departureStationId}/arrival/{arrivalStationId}/instant")
    Observable<List<SolutionList.Solution>> getJourneyInstant(@Path("departureStationId") String departureId,
                                                              @Path("arrivalStationId") String arrivalId,
                                                              @Query("preemptive") boolean preemptive);


    // GET BEFORE TIME
    // http://46.101.130.226:8080/departure/5066/arrival/7104/look-forward?start-from=1463202600
    @GET("/departure/{departureStationId}/arrival/{arrivalStationId}/look-behind")
    Observable<List<SolutionList.Solution>> getJourneyBeforeTime(@Path("departureStationId") String departureId,
                                                                 @Path("arrivalStationId") String arrivalId,
                                                                 @Query("end-at") long time,
                                                                 @Query("include-delays") boolean includeDelays,
                                                                 @Query("preemptive") boolean preemptive);

    // GET AFTER TIME
    @GET("/departure/{departureStationId}/arrival/{arrivalStationId}/look-forward")
    Observable<List<SolutionList.Solution>> getJourneyAfterTime(@Path("departureStationId") String departureId,
                                                                @Path("arrivalStationId") String arrivalId,
                                                                @Query("start-from") long time,
                                                                @Query("include-delays") boolean includeDelays,
                                                                @Query("preemptive") boolean preemptive);


    @GET("/station/{trainDepartureStationId}/train/{trainId}/header")
    Observable<NotificationData> getNotificationData(@Path("trainDepartureStationId") String trainDepartureStationId,
                                                     @Path("trainId") String trainId);

    @GET("/train/{trainId}/header")
    Observable<NotificationData> getNotificationData(@Path("trainId") String trainId);

}