package com.base.lib.googleservices;

import android.app.Activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.games.GamesActivityResultCodes;

/** ripped from BaseGameUtils
 * @see <a href="https://developers.google.com/games/services/downloads/">https://developers.google.com/games/services/downloads/</a> */
class GameHelperUtils {

    static String activityResponseCodeToString(int respCode) {
        switch (respCode) {
            case Activity.RESULT_OK:
                return "RESULT_OK";
            case Activity.RESULT_CANCELED:
                return "RESULT_CANCELED";
            case GamesActivityResultCodes.RESULT_APP_MISCONFIGURED:
                return "RESULT_APP_MISCONFIGURED";
            case GamesActivityResultCodes.RESULT_LEFT_ROOM:
                return "RESULT_LEFT_ROOM";
            case GamesActivityResultCodes.RESULT_LICENSE_FAILED:
                return "RESULT_LICENSE_FAILED";
            case GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED:
                return "RESULT_RECONNECT_REQUIRED";
            case GamesActivityResultCodes.RESULT_SIGN_IN_FAILED:
                return "SIGN_IN_FAILED";
            default:
                return String.valueOf(respCode);
        }
    }

    static String errorCodeToString(int errorCode) {
        switch (errorCode) {
            case ConnectionResult.DEVELOPER_ERROR:
                return "DEVELOPER_ERROR(" + errorCode + ")";
            case ConnectionResult.INTERNAL_ERROR:
                return "INTERNAL_ERROR(" + errorCode + ")";
            case ConnectionResult.INVALID_ACCOUNT:
                return "INVALID_ACCOUNT(" + errorCode + ")";
            case ConnectionResult.LICENSE_CHECK_FAILED:
                return "LICENSE_CHECK_FAILED(" + errorCode + ")";
            case ConnectionResult.NETWORK_ERROR:
                return "NETWORK_ERROR(" + errorCode + ")";
            case ConnectionResult.RESOLUTION_REQUIRED:
                return "RESOLUTION_REQUIRED(" + errorCode + ")";
            case ConnectionResult.SERVICE_DISABLED:
                return "SERVICE_DISABLED(" + errorCode + ")";
            case ConnectionResult.SERVICE_INVALID:
                return "SERVICE_INVALID(" + errorCode + ")";
            case ConnectionResult.SERVICE_MISSING:
                return "SERVICE_MISSING(" + errorCode + ")";
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                return "SERVICE_VERSION_UPDATE_REQUIRED(" + errorCode + ")";
            case ConnectionResult.SIGN_IN_REQUIRED:
                return "SIGN_IN_REQUIRED(" + errorCode + ")";
            case ConnectionResult.SUCCESS:
                return "SUCCESS(" + errorCode + ")";
            default:
                return "Unknown error code " + errorCode;
        }
    }


}
