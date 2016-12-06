package com.jaus.albertogiunta.justintrain_oraritreni.utils.helpers;

import com.google.gson.Gson;

import android.content.Context;

import com.jaus.albertogiunta.justintrain_oraritreni.data.ServerConfig;

import java.util.List;

public class ServerConfigsHelper {

    public static String getAPIEndpoint(Context context) {
        return new Gson().fromJson(SharedPreferencesHelper.getSharedPreferenceObject(context, "serverConfig"), ServerConfig.class).getAddress();
    }

    public static void setAPIEndpoint(Context context, List<ServerConfig> list) {
        ServerConfig config = list.get(0);
        SharedPreferencesHelper.setSharedPreferenceObject(context, "serverConfig", new Gson().toJson(config));
    }

}
