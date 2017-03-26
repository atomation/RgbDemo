package net.atomation.rgbdemo.services;

import android.app.Service;
import android.support.annotation.WorkerThread;

/**
 * API for all service commands
 * Created by eyal on 09/01/2017.
 */
@WorkerThread
public
/* package */ interface IServiceCommand<T extends Service> {
    void execute(T service);
}
