package net.atomation.rgbdemo.interfaces;

import android.support.annotation.Nullable;

/**
 * Utility Functor API
 * Created by eyal on 09/01/2017.
 */

public interface IFunction<Parameter, ReturnVal> {
    @Nullable ReturnVal apply(@Nullable Parameter parameter);
}
