package net.atomation.rgbdemo.activities.adapters;

import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.atomation.rgbdemo.activities.ScanActivity;
import net.atomation.rgbdemo.beans.Device;
import net.atomation.rgbdemo.databinding.ViewScanDeviceListItemBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class to be used in the scanned devices RecycleView in ScanActivity
 * Created by eyal on 09/01/2017.
 */

public final class ScanAdapter extends RecyclerView.Adapter<ScanAdapter.ViewHolder> {

    private static final String TAG = ScanAdapter.class.getSimpleName();

    private final ScanActivity mActivity;
    private final List<Device> mDevices = new ArrayList<>();

    public ScanAdapter(ScanActivity activity) {
        Log.d(TAG, "ScanAdapter() called with: activity = [" + activity + "]");
        mActivity = activity;
    }

    @Override
    public ScanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder() called with: parent = [" + parent + "], viewType = [" + viewType + "]");
        ViewScanDeviceListItemBinding binding = ViewScanDeviceListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ScanAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder() called with: holder = [" + holder + "], position = [" + position + "]");
        holder.mBinding.setDevice(mDevices.get(position));
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    @UiThread
    public void addDevice(Device device) {
        Log.d(TAG, "addDevice() called with: device = [" + device + "]");
        if (!mDevices.contains(device)) {
            Log.i(TAG, "addDevice: adding device to scan list - " + device);
            mDevices.add(device);
            notifyItemInserted(mDevices.size() - 1);
        }
    }

    @UiThread
    public void clear() {
        Log.d(TAG, "clear() called");
        Log.i(TAG, "clear: clearing scanned devices list");
        mDevices.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ViewScanDeviceListItemBinding mBinding;

        /* package */ ViewHolder(ViewScanDeviceListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.setViewHolder(this);
        }

        public void onDeviceSelected() {
            Device device = mBinding.getDevice();
            Log.d(TAG, "onDeviceSelected() called for " + device);
            mActivity.onDeviceSelected(device);
        }
    }
}
