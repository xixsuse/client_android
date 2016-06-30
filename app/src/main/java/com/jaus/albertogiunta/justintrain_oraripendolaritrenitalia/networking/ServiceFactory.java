package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by albertogiunta on 18/06/16.
 */
public class ServiceFactory {

    /**
     * Creates a retrofit service from an arbitrary class (clazz)
     *
     * @param clazz    Java interface of the retrofit service
     * @param endPoint REST endpoint url
     * @return retrofit service with defined endpoint
     */
    public static <T> T createRetrofitService(final Class<T> clazz, final String endPoint) {
        final Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(endPoint)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        T service = restAdapter.create(clazz);

        return service;
    }

}
