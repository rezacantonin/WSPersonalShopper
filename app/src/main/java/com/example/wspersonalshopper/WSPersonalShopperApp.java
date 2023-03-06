package com.example.wspersonalshopper;

import android.app.Application;

/**
 * Created by derohimat on 19/08/2016.
 */
public class WSPersonalShopperApp extends Application {

    public static boolean isInLockMode;

    public static boolean isInLockMode() {
        return isInLockMode;
    }

    public static void setIsInLockMode(boolean isInLockMode) {
        WSPersonalShopperApp.isInLockMode = isInLockMode;
    }
}
