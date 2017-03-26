package net.atomation.rgbdemo.utils;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import net.atomation.rgbdemo.App;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Basic Exception thrown/returned in the app
 * Created by eyal on 09/01/2017.
 */

public class AppException extends Exception {

    // TODO: 11/01/2017 decide how to organize the various error codes/messages
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            Type.GENERAL,
            Type.INTERNAL,
            Type.BLE_OFF,
            Type.TOKEN_INVALID,
            Type.USER_NAME_OR_PASSWORD_INCORRECT,
            Type.RECOVERY_EMAIL_INCORRECT,
            Type.RECOVERY_EMAIL_INVALID,
            Type.NETWORK_UNAVAILABLE,
            Type.INTERNET_ERROR,
            Type.USER_ALREADY_EXISTS,
            Type.PASSWORD_INVALID,
    })
    public @interface Type {
        // general
        int GENERAL = 1;
        int INTERNAL = 2;
        int BLE_OFF = 3;
        int TOKEN_INVALID = 4;

        // network
        int NETWORK_UNAVAILABLE = 11;
        int INTERNET_ERROR = 12;

        // user management
        int USER_NAME_OR_PASSWORD_INCORRECT = 21;
        int RECOVERY_EMAIL_INCORRECT = 22;
        int RECOVERY_EMAIL_INVALID = 23;
        int USER_ALREADY_EXISTS = 24;
        int PASSWORD_INVALID = 25;
    }

    private final int errorCode;
    private final @NonNull String errorMsg;
    private final @StringRes int errorMsgResId;

    public AppException(@Type int errorCode, @StringRes int errorMsgResId) {
        this(errorCode, errorMsgResId, null);
    }

    public AppException(@Type int errorCode, @StringRes int errorMsgResId, @Nullable String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsgResId = errorMsgResId;
        this.errorMsg = errorMsg != null ? errorMsg : "";
    }

    public @Type int getErrorCode() {
        return errorCode;
    }

    public @NonNull String getErrorMsg() {
        String retVal = errorMsg;
        if (errorMsgResId != 0) {
            retVal = App.getAppContext().getString(errorMsgResId);
        }

        return retVal;
    }

    @Override
    public String toString() {
        return "AppException{" +
                "errorCode=" + errorCode +
                ", errorMsg='" + errorMsg + '\'' +
                ", errorMsgResId=" + errorMsgResId +
                "} " + super.toString();
    }
}
