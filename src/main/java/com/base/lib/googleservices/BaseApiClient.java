package com.base.lib.googleservices;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.plus.Plus;

/**
 *
 */
public class BaseApiClient implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener { //todo reproduce listeners

    private static final String TAG = "BaseApiClient";

    public static final int REQUEST_RESOLVE_ERROR = 9001;
    public static final int REQUEST_ACHIEVEMENTS = 9002;
    public static final int REQUEST_LEADERBORDS = 9003;

    private static final String DIALOG_ERROR = "dialog_error";

    private static BaseApiClient instance;

    private static GoogleApiClient client;
    private static boolean resolvingError = false;
    private static boolean userCenceled = false;
    private static String errToast = null;

    private Runnable onConnectedAction;

    private final BaseActivity activity;

    private BaseApiClient(BaseActivity activity) { //todo game/drive/etc. options
        this.activity = activity;

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(activity);
        builder.addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this);

        client = builder.build();

        int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (code != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(code, activity, 0);
        } else {
            Base.logV(TAG, "Google Play Service is available");
        }
    }

    public static void init(Base base, boolean connectAnyways) {

        if (instance == null) {
            instance = new BaseApiClient(base.activity);
        }

        if (base.isConnected() || connectAnyways) {
            instance.connect();
        }
    }

    public static BaseApiClient getInstance() {

        return instance;
    }

    public static GoogleApiClient getClient() {

        return client;
    }

    public static boolean isConnected() {

        return client.isConnected();
    }

    public static void signIn() {

        instance.connect();
    }

    public static void signIn(Runnable onConnectedAction) {

        instance.setOnConnectedAction(onConnectedAction);
        instance.connect();
    }

    public static void signOut() {

        instance.disconnect();
    }

    public void connect() {

        if (!client.isConnected() && !client.isConnecting()) {
            Base.logV(TAG, "try to connect");
            client.connect();
        }
    }

    public void reconnect() {

        if (!client.isConnected()) {
            client.connect();
        } else {
            client.reconnect();
        }
    }

    public void disconnect() {

        if (client.isConnected()) {
            Base.logV(TAG, "disconnect");
            client.disconnect();
        }
    }

    public void setOnConnectedAction(final Runnable action) {

        if (onConnectedAction == null) {
            onConnectedAction = action;
        } else {
            final Runnable temp = onConnectedAction;
            onConnectedAction = new Runnable() {
                @Override
                public void run() {
                    temp.run();
                    action.run();
                }
            };
        }
    }

    public void runAction(Runnable action) {
        if (client.isConnected()) {
            action.run();
        } else {
            setOnConnectedAction(action);
        }
    }

    public boolean userNotLogIn() {

        return userCenceled;
    }

    public boolean userConnectable(Base base) {

        return !userCenceled && base.isConnected();
    }

    public static void setConnectionErrorToast(String errorToast) {

        errToast = errorToast;
    }

    @Override
    public void onConnected(Bundle bundle) {

        Base.logV(TAG, "connected");
        resolvingError = false;
        if (onConnectedAction != null) {
            onConnectedAction.run();
            onConnectedAction = null;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

        Base.logV(TAG, "suspended " + i);
        disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Base.logV(TAG, "connection failed");
        Base.logE(TAG, GameHelperUtils.errorCodeToString(connectionResult.getErrorCode()));

        if (resolvingError) {
            return;
        }

        resolvingError = true;

        if (connectionResult.hasResolution()) {
            try {
                Base.logV(TAG, "solving problem " + REQUEST_RESOLVE_ERROR);
                connectionResult.startResolutionForResult(activity, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                connect();
            }
        } else {
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_RESOLVE_ERROR) {
            resolvingError = false;
            if (resultCode == Activity.RESULT_OK || resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
                Base.logV(TAG, "reconnecting");
                userCenceled = false;
                reconnect();
            } else {
                onConnectedAction = null;
                Base.logE(TAG, requestCode + " solving failed at " + GameHelperUtils.activityResponseCodeToString(resultCode));
                if (resultCode == GamesActivityResultCodes.RESULT_APP_MISCONFIGURED) {
                    //Base.logI(TAG, getSHA1CertFingerprint()); //TODO LOG FINGERPRINT
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    userCenceled = true;
                    Base.logV(TAG, "user cenceled to log in");
                } else if (resultCode == GamesActivityResultCodes.RESULT_SIGN_IN_FAILED) {
                    if (errToast != null) {
                        //Base.toast(errToast); //TODO
                    }
                }
            }
        } else if (requestCode == REQUEST_ACHIEVEMENTS || requestCode == REQUEST_LEADERBORDS) {
            if (resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
                Base.logV(TAG, "disconnected by user");
                userCenceled = true;
                disconnect();
            }
        }

    }

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(activity.getFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        resolvingError = false;
    }

    public void setClient(GoogleApiClient client) {
        this.client = client;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode, this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            instance.onDialogDismissed();
        }
    }

}