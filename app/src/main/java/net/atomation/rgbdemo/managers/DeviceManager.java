package net.atomation.rgbdemo.managers;

import android.support.annotation.Nullable;
import android.util.Log;

import net.atomation.rgbdemo.beans.AdvertisingData;
import net.atomation.rgbdemo.beans.Device;
import net.atomation.rgbdemo.interfaces.IOnDeviceUpdatedListener;
import net.atomation.rgbdemo.utils.AppExecutors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager class responsible for creating, updating and providing remote devices to the rest of the app
 * Created by eyal on 09/01/2017.
 */

public final class DeviceManager {

    private static final String TAG = DeviceManager.class.getSimpleName();

    private final Object mDevicesLock = new Object();
    private final Object mListenersLock = new Object();

    private final Map<String, Device> mDevices = new HashMap<>();
    private final List<IOnDeviceUpdatedListener> mDeviceUpdateListeners = new ArrayList<>();

    public static DeviceManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private DeviceManager() {}

    public @Nullable Device getDevice(String macAddress) {
        Log.d(TAG, "getDevice() called with: macAddress = [" + macAddress + "]");
        synchronized (mDevices) {
            return mDevices.get(macAddress);
        }
    }

    public void addOnDeviceUpdatedListener(IOnDeviceUpdatedListener listener) {
        Log.d(TAG, "addOnDeviceUpdatedListener() called with: listener = [" + listener + "]");
        synchronized (mListenersLock) {
            if (!mDeviceUpdateListeners.contains(listener)) {
                Log.d(TAG, "addOnDeviceUpdatedListener: adding onDeviceUpdateListener - " + listener);
                mDeviceUpdateListeners.add(listener);
            }
        }
    }

    public void removeOnDeviceUpdatedListener(IOnDeviceUpdatedListener listener) {
        Log.d(TAG, "removeOnDeviceUpdatedListener() called with: listener = [" + listener + "]");
        synchronized (mListenersLock) {
            if (mDeviceUpdateListeners.contains(listener)) {
                Log.d(TAG, "removeOnDeviceUpdatedListener: removing onDeviceUpdatedListener - " + listener);
                mDeviceUpdateListeners.remove(listener);
            }
        }
    }

    public void addOrUpdateDevice(AdvertisingData advertisingData) {
        Log.d(TAG, "addOrUpdateDevice() called with: advertisingData = [" + advertisingData + "]");

        synchronized (mDevicesLock) {
            String macAddress = advertisingData.getMac();
            Device device = mDevices.get(macAddress);
            if (device == null) {
                device = new Device(macAddress, advertisingData.getName());
                mDevices.put(macAddress, device);
            }

            updateDevice(device, advertisingData);
        }
    }

    private void updateDevice(Device device, AdvertisingData advertisingData) {
        Log.d(TAG, "updateDevice() called with: device = [" + device + "], advertisingData = [" + advertisingData + "]");
        device.setName(advertisingData.getName());
        device.setRssi(advertisingData.getRssi());
        device.setBatteryVoltage(advertisingData.getBatteryVoltage());

        notifyDeviceUpdatedListeners(device);
    }

    private void notifyDeviceUpdatedListeners(final Device device) {
        Log.d(TAG, "notifyDeviceUpdatedListeners() called with: device = [" + device + "]");
        List<IOnDeviceUpdatedListener> listeners = new ArrayList<>();
        synchronized (mListenersLock) {
            listeners.addAll(mDeviceUpdateListeners);
        }

        for (final IOnDeviceUpdatedListener listener : listeners) {
            AppExecutors.QUEUE_EXECUTOR.submit(new Runnable() {
                @Override
                public void run() {
                    listener.onDeviceUpdated(device);
                }
            });
        }
    }

    private static final class InstanceHolder {
        private static final DeviceManager INSTANCE = new DeviceManager();
    }
}
