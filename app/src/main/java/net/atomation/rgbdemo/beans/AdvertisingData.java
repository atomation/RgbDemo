package net.atomation.rgbdemo.beans;

import android.support.annotation.IntDef;
import android.util.Log;

import net.atomation.atomationsdk.api.IScanRecord;
import net.atomation.rgbdemo.utils.Utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteOrder;

/**
 * Class represents all the processed data received from an advertising event of a particular device
 * Created by eyal on 09/01/2017.
 */

public final class AdvertisingData {

    private static final String TAG = AdvertisingData.class.getSimpleName();

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ManufacturerDataIndex.BATTERY_LSB, ManufacturerDataIndex.BATTERY_MSB})
    private @interface ManufacturerDataIndex {
        // TODO: 09/01/2017 put correct index values here. take from Koby's ReadMes
        int BATTERY_LSB = 0;
        int BATTERY_MSB = 1;
    }

    private final String name;
    private final String mac;
    private final int rssi;
    private final double batteryVoltage;
    // TODO: 09/01/2017 add batteryPercent (device specific code. maybe not here)

    public AdvertisingData(String deviceAddress, String deviceName, int rssi, IScanRecord scanRecord) {
        name = deviceName != null ? deviceName : scanRecord.getLocalName();
        mac = deviceAddress;
        this.rssi = rssi;

        byte[] manufacturerDataBytes = scanRecord.get(IScanRecord.MANUFACTURER_DATA_ID);
        batteryVoltage = parseBattery(manufacturerDataBytes);
    }

    public String getName() {
        return name;
    }

    public String getMac() {
        return mac;
    }

    public int getRssi() {
        return rssi;
    }

    public double getBatteryVoltage() {
        return batteryVoltage;
    }

    private double parseBattery(byte[] manufecturerDataBytes) {
        double batteryVoltage = 0;

		if (manufecturerDataBytes != null && manufecturerDataBytes.length > 0) {
			try {
				int rawBattery = Utils.bytesToInt(new byte[]{manufecturerDataBytes[ManufacturerDataIndex.BATTERY_LSB], manufecturerDataBytes[ManufacturerDataIndex.BATTERY_MSB]}, ByteOrder.LITTLE_ENDIAN);

				batteryVoltage = Utils.convertRawBatteryToVoltage(rawBattery);
			} catch (Exception e) {
				Log.e(TAG, "parseBattery: couldn't parse battery value", e);
			}
		}

        return batteryVoltage;
    }

    @Override
    public String toString() {
        return "AdvertisingData{" +
                "name='" + name + '\'' +
                ", mac='" + mac + '\'' +
                ", rssi=" + rssi +
                ", batteryVoltage=" + batteryVoltage +
                '}';
    }
}
