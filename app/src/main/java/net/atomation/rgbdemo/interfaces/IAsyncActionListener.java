package net.atomation.rgbdemo.interfaces;

import net.atomation.rgbdemo.utils.AppException;

/**
 * API for general Async actions listeners
 * Created by eyal on 11/01/2017.
 */

public interface IAsyncActionListener {
    void onSuccess();

    void onError(AppException error);
}
