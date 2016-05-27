package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia;

import android.app.Application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.internal.IOException;

/**
 * Created by albertogiunta on 27/05/16.
 */
public class MyApplication extends Application {

    private static MyApplication context;

    public MyApplication getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
//        JodaTimeAndroid.init(this);

        copyBundledRealmFile(this.getResources().openRawResource(R.raw.station), Realm.DEFAULT_REALM_NAME);
        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.deleteRealm(config);
        Realm.setDefaultConfiguration(config);
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
