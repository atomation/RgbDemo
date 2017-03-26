package net.atomation.rgbdemo.interfaces;

import android.support.annotation.WorkerThread;

import net.atomation.rgbdemo.beans.Device;

/**
 * API listening to device updated events
 * Created by eyal on 09/01/2017.
 */

@WorkerThread
public interface IOnDeviceUpdatedListener {
    void onDeviceUpdated(Device device);
}
