package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Message;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

public interface MessageService {

    String SERVICE_ENDPOINT = "https://gist.githubusercontent.com";

    @GET("/albertogiunta/c40a1471d3cc141c9185393945a83912/raw/")
    Observable<List<Message>> getAllMessages();

}
