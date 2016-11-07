package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.stetho.Stetho;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.internal.IOException;

public class MyApplication extends Application {

    private static MyApplication context;

    public MyApplication getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        JodaTimeAndroid.init(this);
        Stetho.initializeWithDefaults(this);
        Realm.init(this);
        copyBundledRealmFile(this.getResources().openRawResource(R.raw.station), Realm.DEFAULT_REALM_NAME);

//        copyBundledRealmFile(this.getResources().openRawResource(R.raw.station), Realm.DEFAULT_REALM_NAME);
//        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
//        Realm.setDefaultConfiguration(realmConfiguration);
//        RealmConfiguration config = new RealmConfiguration.Builder()
//                .name(Realm.DEFAULT_REALM_NAME)
//                .assetFile("station.realm")
//                .deleteRealmIfMigrationNeeded()
//                .build();
//        Realm.setDefaultConfiguration(config);
//        if (copyBundledRealmFile(this.getResources().openRawResource(R.raw.station), Realm.DEFAULT_REALM_NAME) != null) {
//            RealmConfiguration config = new RealmConfiguration.Builder(this)
//                    .deleteRealmIfMigrationNeeded()
//                    .build();
//            Realm.deleteRealm(config);
//            Realm.setDefaultConfiguration(config);
//        }
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    private String copyBundledRealmFile(InputStream inputStream, String outFileName) {
        try {
            File file = new File(this.getFilesDir(), outFileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            outputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
