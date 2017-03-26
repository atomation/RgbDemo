package net.atomation.rgbdemo.interfaces;

import net.atomation.rgbdemo.utils.AppException;

/**
 * Listener to BLE scan status events
 * Created by eyal on 09/01/2017.
 */

public interface IAppScanListener {
    void onScanStatus(boolean isScanning);

    void onScanError(AppException e);
}
