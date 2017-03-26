package net.atomation.rgbdemo.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import net.atomation.atomationsdk.api.ErrorFactory;
import net.atomation.atomationsdk.api.IConnectionStateListener;
import net.atomation.atomationsdk.api.IRgbAtom;
import net.atomation.atomationsdk.api.IWriteListener;
import net.atomation.atomationsdk.ble.AtomationCloudManager;
import net.atomation.atomationsdk.api.AtomationEvent;
import net.atomation.atomationsdk.ble.AtomationRgbAtomManager;
import net.atomation.rgbdemo.R;
import net.atomation.rgbdemo.activities.contents.DeviceActivityContent;
import net.atomation.rgbdemo.databinding.ActivityDeviceBinding;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import java.util.List;

public class DeviceActivity extends AppCompatActivity {

    private static final String TAG = DeviceActivity.class.getSimpleName();

    private static final String INTENT_EXTRA_MAC = "mac";
    private static final String EVENT_ID_CONNECTION_STATE = "connection state";
    private static final String EVENT_STATE_CONNECTED = "connected";
    private static final String EVENT_STATE_DISCONNECTED = "disconnected";
    private static final String EVENT_STATE_ERROR = "error";
    private static final String EVENT_ID_WRITE_LED_1 = "write led 1";
    private static final String EVENT_ID_WRITE_LED_2 = "write led 2";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ConnectionStatus.DISCONNECTED, ConnectionStatus.CONNECTING, ConnectionStatus.CONNECTED})
    public @interface ConnectionStatus {
        int DISCONNECTED = 0;
        int CONNECTING = 1;
        int CONNECTED = 2;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ColorIndex.RED, ColorIndex.GREEN, ColorIndex.BLUE})
    public @interface ColorIndex {
        int RED = 0;
        int GREEN = 1;
        int BLUE = 2;
    }

    ActivityDeviceBinding mBinding;
    private final DeviceActivityContent mContent = new DeviceActivityContent();
    private final IConnectionStateListener mConnectionStateListener = new ConnectionListener();
    private final IWriteListener mLedWriteListener = new LedWriteListener();
    private IRgbAtom mAtom;


    public static Intent createIntent(Context context, String address) {
        Log.d(TAG, "createIntent() called with: context = [" + context + "]");
        return new Intent(context, DeviceActivity.class)
                .putExtra(INTENT_EXTRA_MAC, address);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_device);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras == null || !extras.containsKey(INTENT_EXTRA_MAC)){
            throw new IllegalArgumentException("mac is null");
        }

        mContent.setAddress(extras.getString(INTENT_EXTRA_MAC));
        mContent.setConnectionState(ConnectionStatus.DISCONNECTED);
        mBinding.setContent(mContent);
        mBinding.setEventHandler(this);

        mAtom = AtomationRgbAtomManager.getAtom(mContent.getAddress());
        if (mAtom == null) {
            mAtom = AtomationRgbAtomManager.createAtom(mContent.getAddress());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mContent.getConnectionState() != ConnectionStatus.DISCONNECTED) {
            mAtom.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        connectDevice();
    }

    private void connectDevice(){
        Log.d(TAG, "connectDevice() called for - " + mContent.getAddress());
        mAtom.connect(mConnectionStateListener);
        mContent.setConnectionState(ConnectionStatus.CONNECTING);
    }

    public void onConnectionButtonClicked(){
        Log.i(TAG, "onConnectionButtonClicked");
        if (mContent.getConnectionState() == ConnectionStatus.CONNECTED){
            Log.d(TAG, "onConnectionButtonClicked: disconnecting from atom - " + mContent.getAddress());
            mAtom.disconnect();
        } else {
            connectDevice();
        }
    }

    public void onSetLed1Clicked(){
        Log.d(TAG, "onSetLed1Clicked() called");

        String [] errMsg = {null, null, null};
        int [] colors = {0x00, 0x00, 0x00};
        boolean valid = true;

        valid &= isValidNumber(mBinding.edtRed1.getText().toString(), errMsg, colors, ColorIndex.RED);
        valid &= isValidNumber(mBinding.edtGreen1.getText().toString(), errMsg, colors,ColorIndex.GREEN);
        valid &= isValidNumber(mBinding.edtBlue1.getText().toString(), errMsg, colors, ColorIndex.BLUE);

        if (valid) {
            Log.d(TAG, "onSetLed1Clicked: writing RGB");
            mAtom.writeRGB(IRgbAtom.AtomationLedId.LED_1,
                    colors[ColorIndex.RED],
                    colors[ColorIndex.GREEN],
                    colors[ColorIndex.BLUE],
                    mLedWriteListener);
            sendWriteLedEvent(EVENT_ID_WRITE_LED_1, colors);
        }

        mContent.setLed1Error(errMsg);
    }

    public void onSetLed2Clicked(){
        Log.d(TAG, "onSetLed2Clicked() called");

        String [] errMsg = {null, null, null};
        int [] colors = {0x00, 0x00, 0x00};
        boolean valid = true;

        valid &= isValidNumber(mBinding.edtRed2.getText().toString(), errMsg, colors, ColorIndex.RED);
        valid &= isValidNumber(mBinding.edtGreen2.getText().toString(), errMsg, colors, ColorIndex.GREEN);
        valid &= isValidNumber(mBinding.edtBlue2.getText().toString(), errMsg, colors, ColorIndex.BLUE);

        if (valid) {
            Log.d(TAG, "onSetLed1Clicked: writing RGB");
            mAtom.writeRGB(IRgbAtom.AtomationLedId.LED_2, colors[ColorIndex.RED],
                    colors[ColorIndex.GREEN],
                    colors[ColorIndex.BLUE],
                    mLedWriteListener);
            sendWriteLedEvent(EVENT_ID_WRITE_LED_2, colors);
        }

        mContent.setLed2Error(errMsg);
    }

    private void sendWriteLedEvent(String ledEventId, int[] rgb){
        Log.d(TAG, "sendWriteLedEvent() called with: ledEventId = [" + ledEventId + "], rgb = [" + rgb + "]");
        List<String> colorsStr = new ArrayList<>();
        colorsStr.add(String.valueOf(rgb[ColorIndex.RED]));
        colorsStr.add(String.valueOf(rgb[ColorIndex.GREEN]));
        colorsStr.add(String.valueOf(rgb[ColorIndex.BLUE]));
        AtomationCloudManager.sendEvent(new AtomationEvent(ledEventId, colorsStr ,mContent.getAddress(), System.currentTimeMillis()));
    }

    private boolean isValidNumber(String strNumber, String [] errMsg, int [] colors, int index){
        Log.d(TAG, "isValidNumber() called with: strNumber = [" + strNumber + "], errMsg = [" + errMsg + "], colors = [" + colors + "], index = [" + index + "]");
        int number = 0;
        boolean valid = true;
        String errStr = null;

        try{
            number = Integer.parseInt(strNumber);
        } catch (NumberFormatException e){
            Log.e(TAG, "isValidNumber: couldn't parse number", e);
            valid = false;
            errStr = getString(R.string.error_is_empty);
        }

        if (number < IRgbAtom.RGB_MINIMUM_VALUE || number > IRgbAtom.RGB_MAXIMUM_VALUE) {
            Log.d(TAG, "isValidNumber: number out of range");
            valid = false;
            errStr = getString(R.string.error_not_in_range);
        }

        errMsg[index] = errStr;
        colors[index] = number;

        Log.d(TAG, "isValidNumber() returned: " + valid);
        return valid;
    }

    private final class ConnectionListener implements IConnectionStateListener {
        @Override
        public void onDeviceConnected() {
            Log.d(TAG, "onDeviceConnected() called");
            mContent.setConnectionState(ConnectionStatus.CONNECTED);
            sendConnectionEvent(EVENT_STATE_CONNECTED);
        }

        @Override
        public void onDeviceDisconnect() {
            Log.d(TAG, "onDeviceDisconnect() called");
            mContent.setConnectionState(ConnectionStatus.DISCONNECTED);
            sendConnectionEvent(EVENT_STATE_DISCONNECTED);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(DeviceActivity.this, getString(R.string.disconnection_msg),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onConnectionError(ErrorFactory.AtomationError error) {
            Log.d(TAG, "onConnectionError() called with: error = [" + error + "]");
            mContent.setConnectionState(ConnectionStatus.DISCONNECTED);
            sendConnectionEvent(EVENT_STATE_ERROR, String.valueOf(error));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(DeviceActivity.this, getString(R.string.error_connection),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendConnectionEvent(String state) {
        sendConnectionEvent(state, null);
    }

    private void sendConnectionEvent(String state, String error) {
        Log.d(TAG, "sendConnectionEvent() called with: state = [" + state + "]");
        List<String> eventData = new ArrayList<>();
        eventData.add(state);

        if (error != null) {
            eventData.add(error);
        }

        AtomationCloudManager.sendEvent(new AtomationEvent(EVENT_ID_CONNECTION_STATE, eventData, mContent.getAddress(), System.currentTimeMillis()));
    }

    private class LedWriteListener implements IWriteListener{

        @Override
        public void onWriteExecuted() {
            Log.d(TAG, "LedWriteListener onWriteExecuted() called");
        }

        @Override
        public void onWriteError(ErrorFactory.AtomationError atomationError) {
            Log.d(TAG, "onWriteError() called with: atomationError = [" + atomationError + "]");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(DeviceActivity.this, getString(R.string
                            .error_writing_failed), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
