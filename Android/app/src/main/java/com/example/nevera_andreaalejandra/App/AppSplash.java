package com.example.nevera_andreaalejandra.App;

import android.app.Application;
import android.os.SystemClock;

public class AppSplash extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SystemClock.sleep(1000);
    }
}
