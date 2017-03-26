package net.atomation.rgbdemo.activities.contents;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import net.atomation.rgbdemo.BR;

/**
 * Content class for ScanActivity
 * Created by eyal on 09/01/2017.
 */

public final class ScanActivityContent extends BaseObservable {

    private boolean isScanning;
    private boolean isRefreshing;

    @Bindable
    public boolean isScanning() {
        return isScanning;
    }

    public void setScanning(boolean scanning) {
        isScanning = scanning;
        notifyPropertyChanged(BR.scanning);
    }

    @Bindable
    public boolean isRefreshing() {
        return isRefreshing;
    }

    public void setRefreshing(boolean refreshing) {
        isRefreshing = refreshing;
        notifyPropertyChanged(BR.refreshing);
    }
}
