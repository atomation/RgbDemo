package net.atomation.rgbdemo;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import net.atomation.atomationsdk.api.ErrorFactory;
import net.atomation.atomationsdk.api.ISdkInitializationListener;
import net.atomation.atomationsdk.ble.AtomationSdk;

/**
 * class responsible for providing application context to the rest of the app
 * Created by eyal on 09/01/2017.
 */

public class App extends Application {

    private static final String TAG = App.class.getSimpleName();

    private static App sInstance;

    public static Context getAppContext() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() called. initializing app context...");
        sInstance = this;
        AtomationSdk.init(getApplicationContext(), new ISdkInitializationListener() {
            @Override
            public void onInitialized() {
                Log.d(TAG, "onInitialized() called");
            }

            @Override
            public void onError(ErrorFactory.AtomationError error) {
                Log.d(TAG, "onError() called with: error = [" + error + "]");
            }
        });
    }
}
