package net.atomation.rgbdemo.services.atomation;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import net.atomation.rgbdemo.interfaces.IAppScanListener;
import net.atomation.rgbdemo.services.IServiceCommand;
import net.atomation.rgbdemo.utils.AppExecutors;

import java.util.List;

/**
 * Service responsible for all atomation related operations in the app. every command will be executed on a different worker thread
 */
public class AtomationService extends Service {

    private static final String TAG = AtomationService.class.getSimpleName();

    private final LocalBinder mBinder = new LocalBinder();
    private final ServiceScanHelper mScanHelper = new ServiceScanHelper();

    public AtomationService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() called");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() called with: intent = [" + intent + "]");
        return mBinder;
    }

    /* package */ void execute(List<IServiceCommand<AtomationService>> commandsQueue) {
        Log.d(TAG, "executing commands queue");
        for (IServiceCommand<AtomationService> command : commandsQueue) {
            execute(command);
        }
    }

    /* package */ void execute(final IServiceCommand<AtomationService> command) {
        Log.d(TAG, "scheduling command to execute - " + command);
        AppExecutors.QUEUE_EXECUTOR.submit(new Runnable() {
            @Override
            public void run() {
                // TODO: 09/01/2017 what if AtomationService is unbounded when the thread executes?
                Log.d(TAG, "execute: executing " + command);
                command.execute(AtomationService.this);
            }
        });
    }

    /* package */ void addScanListener(IAppScanListener listener) {
        Log.d(TAG, "addScanListener() called with: listener = [" + listener + "]");
        mScanHelper.addScanListener(listener);
    }

    /* package */ void removeScanListener(IAppScanListener listener) {
        Log.d(TAG, "removeScanListener() called with: listener = [" + listener + "]");
        mScanHelper.removeScanListener(listener);
    }

    /* package */ void startScan() {
        Log.d(TAG, "startScan() called");
        // TODO: 09/01/2017 move the service to foreground
        mScanHelper.startScan();
    }

    /* package */ void stopScan() {
        Log.d(TAG, "stopScan() called");
        // TODO: 09/01/2017 remove service from foreground
        mScanHelper.stopScan();
    }

    // TODO: 09/01/2017 add service commands

    /* package */ class LocalBinder extends Binder {
        /* package */ @NonNull AtomationService getService() {
            return AtomationService.this;
        }
    }
}
