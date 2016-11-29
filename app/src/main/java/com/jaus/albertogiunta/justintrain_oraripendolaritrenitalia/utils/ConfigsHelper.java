package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils;

import com.google.gson.Gson;

import android.content.Context;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.ServerConfig;

import java.util.List;

public class ConfigsHelper {

    public static String getAPIEndpoint(Context context) {
        return new Gson().fromJson(SharedPrefsHelper.getSharedPreferenceObject(context, "serverConfig"), ServerConfig.class).getAddress();
    }

    public static void setAPIEndpoint(Context context, List<ServerConfig> list) {
        ServerConfig config = list.get(0);
        SharedPrefsHelper.setSharedPreferenceObject(context, "serverConfig", new Gson().toJson(config));
    }

}
