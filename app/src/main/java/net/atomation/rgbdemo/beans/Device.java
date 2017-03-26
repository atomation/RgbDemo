package net.atomation.rgbdemo.beans;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;

import net.atomation.rgbdemo.BR;

/**
 * Class representing a single remote device
 * Created by eyal on 09/01/2017.
 */

public class Device extends BaseObservable {

    @NonNull
    private final String mMac;

    @NonNull
    private String mName;

    private int mRssi;
    private double mBatteryVoltage;
    // TODO: 09/01/2017 add batteryPercent

    public Device(@NonNull String mac, @NonNull String name) {
        this.mMac = mac;
        this.mName = name;
    }

    @Bindable
    @NonNull
    public String getMac() {
        return mMac;
    }

    @Bindable
    @NonNull
    public String getName() {
        return mName;
    }

    public void setName(@NonNull String name) {
        this.mName = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public int getRssi() {
        return mRssi;
    }

    public void setRssi(int rssi) {
        this.mRssi = rssi;
        notifyPropertyChanged(BR.rssi);
    }

    @Bindable
    public double getBatteryVoltage() {
        return mBatteryVoltage;
    }

    public void setBatteryVoltage(double batteryVoltage) {
        this.mBatteryVoltage = batteryVoltage;
        notifyPropertyChanged(BR.batteryVoltage);
    }

    @Override
    public String toString() {
        return "Device{" +
                "mMac='" + mMac + '\'' +
                ", mName='" + mName + '\'' +
                ", mRssi=" + mRssi +
                ", mBatteryVoltage=" + mBatteryVoltage +
                "} " + super.toString();
    }
}
