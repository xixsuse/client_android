package com.jaus.albertogiunta.justintrain_oraritreni.utils.helpers;

import com.google.gson.Gson;

import android.content.Context;

import com.jaus.albertogiunta.justintrain_oraritreni.data.ServerConfig;

import static com.jaus.albertogiunta.justintrain_oraritreni.utils.INTENT_CONST.I_SERVER_CONFIG;

public class ServerConfigsHelper {

    public static String getAPIEndpoint(Context context) {
        return new Gson().fromJson(SharedPreferencesHelper.getSharedPreferenceObject(context, "serverConfig"), ServerConfig.class).getAddress();
    }

    public static void setAPIEndpoint(Context context, ServerConfig serverConfig) {
//        ServerConfig config = list.get(0);
        SharedPreferencesHelper.setSharedPreferenceObject(context, I_SERVER_CONFIG, new Gson().toJson(serverConfig));
    }

}
