package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking;

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

    // http://46.101.130.226:8080/departure/5066/arrival/7104/look-forward?start-from=1463202600
    @GET("/departure/{departureStationId}/arrival/{arrivalStationId}/look-forward")
    Observable<List<SolutionList.Solution>> getJourneySolutions(@Path("departureStationId") String departureId,
                                                      @Path("arrivalStationId") String arrivalId,
                                                      @Query("start-from") long time, @Query("include-delays") boolean includeDelays);

}
