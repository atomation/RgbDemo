package net.atomation.rgbdemo.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import net.atomation.rgbdemo.R;
import net.atomation.rgbdemo.activities.adapters.ScanAdapter;
import net.atomation.rgbdemo.activities.contents.ScanActivityContent;
import net.atomation.rgbdemo.beans.Device;
import net.atomation.rgbdemo.databinding.ActivityScanBinding;
import net.atomation.rgbdemo.interfaces.IAppScanListener;
import net.atomation.rgbdemo.interfaces.IOnDeviceUpdatedListener;
import net.atomation.rgbdemo.managers.DeviceManager;
import net.atomation.rgbdemo.services.atomation.AtomationServiceDelegate;
import net.atomation.rgbdemo.utils.AppException;
import net.atomation.rgbdemo.utils.AppExecutors;

import java.util.concurrent.TimeUnit;

public class ScanActivity extends AppCompatActivity {

    private static final String TAG = ScanActivity.class.getSimpleName();

    private static final int SWIPE_TO_REFRESH_TIMEOUT_SECONDS = 5;

    private final ScanActivityContent mContent = new ScanActivityContent();
    private final IAppScanListener mScanListener = new ScanListener();
    private final IOnDeviceUpdatedListener mDeviceUpdatedListener = new OnDeviceUpdatedListener();

    private ScanAdapter mScanAdapter;

    public static Intent createIntent(Context context) {
        Log.d(TAG, "createIntent() called with: context = [" + context + "]");
        return new Intent(context, ScanActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
        ActivityScanBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_scan);
        binding.setContent(mContent);

        mScanAdapter = new ScanAdapter(this);
        binding.rvDevices.setLayoutManager(new LinearLayoutManager(this));
        binding.rvDevices.setAdapter(mScanAdapter);

        binding.srlDevicesWrapper.setOnRefreshListener(new ScanRefreshListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");

        DeviceManager deviceManager = DeviceManager.getInstance();
        deviceManager.addOnDeviceUpdatedListener(mDeviceUpdatedListener);

        AtomationServiceDelegate delegate = AtomationServiceDelegate.getInstance();
        delegate.addScanListener(mScanListener);
        delegate.startScan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
        AtomationServiceDelegate delegate = AtomationServiceDelegate.getInstance();
        delegate.stopScan();
        delegate.removeScanListener(mScanListener);

        DeviceManager deviceManager = DeviceManager.getInstance();
        deviceManager.removeOnDeviceUpdatedListener(mDeviceUpdatedListener);

        mScanAdapter.clear();
    }

    public void onDeviceSelected(Device device) {
        Log.i(TAG, "onDeviceSelected() called with: device = [" + device + "]");
        startActivity(DeviceActivity.createIntent(this, device.getMac()));
    }

    private final class ScanListener implements IAppScanListener {

        @Override
        public void onScanStatus(boolean isScanning) {
            Log.d(TAG, "onScanStatus() called with: isScanning = [" + isScanning + "]");
            mContent.setScanning(isScanning);
        }

        @Override
        public void onScanError(AppException e) {
            Log.d(TAG, "onScanError() called with: e = [" + e + "]");
            mContent.setScanning(false);
        }
    }

    private final class OnDeviceUpdatedListener implements IOnDeviceUpdatedListener {

        @Override
        public void onDeviceUpdated(final Device device) {
            Log.d(TAG, "onDeviceUpdated() called with: device = [" + device + "]");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onDeviceUpdated: adding device to adapter - " + device);
                    mScanAdapter.addDevice(device);
                    mContent.setRefreshing(false);
                }
            });
        }
    }

    private final class ScanRefreshListener implements SwipeRefreshLayout.OnRefreshListener{

        @Override
        public void onRefresh() {
            Log.d(TAG, "onRefresh() called");
            mContent.setRefreshing(true);
            mScanAdapter.clear();

            AppExecutors.SCHEDULED_QUEUE_EXECUTOR.schedule(new Runnable() {
                @Override
                public void run() {
                    ScanActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "onRefresh: refresh UI timeout task executed");
                            if (mContent.isRefreshing()) {
                                Log.i(TAG, "onRefresh: refresh UI timeout. disabling icon...");
                                mContent.setRefreshing(false);
                            }
                        }
                    });
                }
            }, SWIPE_TO_REFRESH_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }
    }
}
