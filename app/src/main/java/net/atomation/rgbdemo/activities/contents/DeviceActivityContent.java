package net.atomation.rgbdemo.activities.contents;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import net.atomation.rgbdemo.BR;
import net.atomation.rgbdemo.activities.DeviceActivity;

/**
 * Content class for ScanActivity
 * Created by eyal on 09/01/2017.
 */

public final class DeviceActivityContent extends BaseObservable {

    private @DeviceActivity.ConnectionStatus int connectionState = DeviceActivity.ConnectionStatus.DISCONNECTED;
    private String address;
    private String errRed1;
    private String errRed2;
    private String errGreen1;
    private String errGreen2;
    private String errBlue1;
    private String errBlue2;

    @Bindable
    public @DeviceActivity.ConnectionStatus int getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(@DeviceActivity.ConnectionStatus int newConnectionState) {
        this.connectionState = newConnectionState;
        notifyPropertyChanged(BR.connectionState);
    }

    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);
    }

    @Bindable
    public String getErrBlue2() {
        return errBlue2;
    }

    public void setErrBlue2(String errBlue2){
        this.errBlue2 = errBlue2;
        notifyPropertyChanged(BR.errBlue2);
    }

    @Bindable
    public String getErrBlue1() {
        return errBlue1;
    }

    public void setErrBlue1(String errBlue1) {
        this.errBlue1 = errBlue1;
        notifyPropertyChanged(BR.errBlue1);
    }

    @Bindable
    public String getErrGreen2() {
        return errGreen2;
    }

    public void setErrGreen2(String errGreen2) {
        this.errGreen2 = errGreen2;
        notifyPropertyChanged(BR.errGreen2);
    }

    @Bindable
    public String getErrGreen1() {
        return errGreen1;
    }

    public void setErrGreen1(String errGreen1) {
        this.errGreen1 = errGreen1;
        notifyPropertyChanged(BR.errGreen1);
    }

    @Bindable
    public String getErrRed2() {
        return errRed2;
    }

    public void setErrRed2(String errRed2) {
        this.errRed2 = errRed2;
        notifyPropertyChanged(BR.errRed2);
    }

    @Bindable
    public String getErrRed1() {
        return errRed1;
    }

    public void setErrRed1(String errRed1) {
        this.errRed1 = errRed1;
        notifyPropertyChanged(BR.errRed1);
    }

    public void setLed1Error(String [] errMsg){
        setErrRed1(errMsg[DeviceActivity.ColorIndex.RED]);
        setErrGreen1(errMsg[DeviceActivity.ColorIndex.GREEN]);
        setErrBlue1(errMsg[DeviceActivity.ColorIndex.BLUE]);
    }

    public void setLed2Error(String [] errMsg){
        setErrRed2(errMsg[DeviceActivity.ColorIndex.RED]);
        setErrGreen2(errMsg[DeviceActivity.ColorIndex.GREEN]);
        setErrBlue2(errMsg[DeviceActivity.ColorIndex.BLUE]);
    }
}
