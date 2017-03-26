package net.atomation.rgbdemo.services.atomation;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;

import net.atomation.atomationsdk.api.Constants;
import net.atomation.atomationsdk.api.ErrorFactory;
import net.atomation.atomationsdk.api.IScanListener;
import net.atomation.atomationsdk.api.IScanRecord;
import net.atomation.atomationsdk.ble.AtomationScanManager;
import net.atomation.rgbdemo.R;
import net.atomation.rgbdemo.beans.AdvertisingData;
import net.atomation.rgbdemo.interfaces.IAppScanListener;
import net.atomation.rgbdemo.interfaces.IFunction;
import net.atomation.rgbdemo.managers.DeviceManager;
import net.atomation.rgbdemo.utils.AppException;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class responsible for managing a BLE scan for the entire app
 * Created by eyal on 09/01/2017.
 */

/* package */ final class ServiceScanHelper {

    private static final String TAG = ServiceScanHelper.class.getSimpleName();

    private final IScanListener mAtomationScanListener = new AtomationScanListener();

    private final Object mLock = new Object();
    private final List<IAppScanListener> mListeners = new ArrayList<>();

    /* package */ ServiceScanHelper() {}

    /* package */ void addScanListener(IAppScanListener listener) {
        Log.d(TAG, "addScanListener() called with: listener = [" + listener + "]");
        synchronized (mLock) {
            if (!mListeners.contains(listener)) {
                Log.d(TAG, "addScanListener: adding scan listener - " + listener);
                mListeners.add(listener);
            }
        }
    }

    /* package */ void removeScanListener(IAppScanListener listener) {
        Log.d(TAG, "removeScanListener() called with: listener = [" + listener + "]");
        synchronized (mLock) {
            if (mListeners.contains(listener)) {
                Log.d(TAG, "removeScanListener: removing scan listener - " + listener);
                mListeners.remove(listener);
            }
        }
    }

    /* package */ void startScan() {
        Log.d(TAG, "startScan() called");
        AtomationScanManager.startScanning(mAtomationScanListener);
    }

    /* package */ void stopScan() {
        Log.d(TAG, "stopScan() called");
        AtomationScanManager.stopScanning();
    }

    private void notifyListeners(IFunction<IAppScanListener, Void> notifyFunc) {
        Log.d(TAG, "notifyListeners() called with: notifyFunc = [" + notifyFunc + "]");

        List<IAppScanListener> listeners = new ArrayList<>();
        synchronized (mLock) {
            listeners.addAll(mListeners);
        }

        Log.d(TAG, "notifyListeners: notifying listeners");
        for (IAppScanListener listener : listeners) {
            notifyFunc.apply(listener);
        }
    }

    private final class AtomationScanListener implements IScanListener {

        @Override
        public void onDeviceFound(final String deviceAddress, final String deviceName, final int rssi, final IScanRecord scanRecord) {
            Log.d(TAG, "onDeviceFound() called with: deviceAddress = [" + deviceAddress + "], deviceName = [" + deviceName + "], rssi = [" + rssi + "], scanRecord = [" + scanRecord + "]");

            IFunction<IAppScanListener, Void> func = new IFunction<IAppScanListener, Void>() {
                @Nullable
                @Override
                public Void apply(@Nullable IAppScanListener listener) {
                    assert listener != null;
                    AdvertisingData advertisingData = new AdvertisingData(deviceAddress, deviceName, rssi, scanRecord);
                    DeviceManager.getInstance().addOrUpdateDevice(advertisingData);
                    return null;
                }
            };

            notifyListeners(func);
        }

        @Override
        public void onScanStatus(final boolean isScanning) {
            Log.d(TAG, "onScanStatus() called with: isScanning = [" + isScanning + "]");

            IFunction<IAppScanListener, Void> func = new IFunction<IAppScanListener, Void>() {
                @Nullable
                @Override
                public Void apply(@Nullable IAppScanListener listener) {
                    assert listener != null;
                    listener.onScanStatus(isScanning);
                    return null;
                }
            };

            notifyListeners(func);
        }

        @Override
        public void onScanError(final ErrorFactory.AtomationError errorCode) {
            Log.d(TAG, "onScanError() called with: errorCode = [" + errorCode + "]");

            IFunction<IAppScanListener, Void> func = new IFunction<IAppScanListener, Void>() {

                @Nullable
                @Override
                @SuppressLint("SwitchIntDef")
                public Void apply(@Nullable IAppScanListener listener) {
                    assert listener != null;

                    @AppException.Type int errorType;
                    @StringRes int errorMsgResId;
                    switch (errorCode.getErrorCode()) {
                        case Constants.ErrorCodes.ERROR_BLE_OFF:
                            errorType = AppException.Type.BLE_OFF;
                            errorMsgResId = R.string.error_bluetooth_off;
                            break;
                        case Constants.ErrorCodes.INTERNAL_ERROR:
                            errorType = AppException.Type.INTERNAL;
                            errorMsgResId = R.string.error_internal_error;
                            break;
                        default:
                            errorType = AppException.Type.GENERAL;
                            errorMsgResId = 0;
                            break;
                    }

                    listener.onScanError(new AppException(errorType, errorMsgResId));
                    return null;
                }
            };

            notifyListeners(func);
        }
    }
}
