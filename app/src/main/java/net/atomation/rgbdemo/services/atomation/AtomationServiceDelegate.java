package net.atomation.rgbdemo.services.atomation;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import net.atomation.rgbdemo.App;
import net.atomation.rgbdemo.interfaces.IAppScanListener;
import net.atomation.rgbdemo.services.IServiceCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible to represent AtomationService to the rest of the App
 * Created by eyal on 09/01/2017.
 */

public final class AtomationServiceDelegate {

    private static final String TAG = AtomationServiceDelegate.class.getSimpleName();

    private final Object mLock = new Object();
    private final List<IServiceCommand<AtomationService>> commandsQueue = new ArrayList<>();

    private AtomationService mService;
    private boolean mBound = false;

    public static AtomationServiceDelegate getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private AtomationServiceDelegate(Context context) {
        Context appContext = context.getApplicationContext();
        Intent intent = new Intent(appContext, AtomationService.class);

        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.i(TAG, "onServiceConnected");
                AtomationService.LocalBinder binder = (AtomationService.LocalBinder) iBinder;
                synchronized (mLock) {
                    mService = binder.getService();
                    mBound = true;
                    mService.execute(new ArrayList<>(commandsQueue));
                    commandsQueue.clear();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.i(TAG, "onServiceDisconnected");
                synchronized (mLock) {
                    mService = null;
                    mBound = false;
                }
            }
        };

        Log.d(TAG, "AtomationServiceDelegate: sending bind request");
        appContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void startScan() {
        Log.d(TAG, "startScan() called");
        IServiceCommand<AtomationService> command = new AtomationServiceCommands.StartScanCommand();
        executeCommand(command);
    }

    public void stopScan() {
        Log.d(TAG, "stopScan() called");
        IServiceCommand<AtomationService> command = new AtomationServiceCommands.StopScanCommand();
        executeCommand(command);
    }

    public void addScanListener(IAppScanListener listener) {
        Log.d(TAG, "addScanListener() called with: listener = [" + listener + "]");
        IServiceCommand<AtomationService> command = new AtomationServiceCommands.AddScanListenerCommand(listener);
        executeCommand(command);
    }

    public void removeScanListener(IAppScanListener listener) {
        Log.d(TAG, "removeScanListener() called with: listener = [" + listener + "]");
        IServiceCommand<AtomationService> command = new AtomationServiceCommands.RemoveScanListenerCommand(listener);
        executeCommand(command);
    }

    private void executeCommand(IServiceCommand<AtomationService> command) {
        Log.d(TAG, "executeCommand() called with: command = [" + command + "]");
        synchronized (mLock) {
            if (mBound) {
                Log.d(TAG, "executeCommand: service bound. executing - " + command);
                mService.execute(command);
            } else {
                Log.d(TAG, "executeCommand: service not bound. adding to queue - " + command);
                commandsQueue.add(command);
            }
        }
    }

    private static final class InstanceHolder {
        private static final AtomationServiceDelegate INSTANCE = new AtomationServiceDelegate(App.getAppContext());
    }
}
