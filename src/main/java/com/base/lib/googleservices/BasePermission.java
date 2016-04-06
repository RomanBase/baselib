package com.base.lib.googleservices;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import com.base.lib.engine.Base;

import java.util.ArrayList;
import java.util.List;

public class BasePermission {

    private final Context context;
    private String[] requiredPermissions;

    public BasePermission(Context context, String[] requestedPermisions) {

        this.context = context;
        requiredPermissions = checkPermissions(context, requestedPermisions);
    }

    /**
     * @return true if any requested permission is not granted
     */
    public boolean isDialogRequired() {

        return requiredPermissions != null;
    }

    /**
     * @see {BasePermission.requestDialog(Context, String...)}
     */
    public void requestRequiredPermissions() {

        if (requiredPermissions != null) {
            requestDialog(context, requiredPermissions);
        }
    }

    public String[] getRequiredPermissions() {
        return requiredPermissions;
    }

    public void setRequiredPermissions(String[] requiredPermissions) {
        this.requiredPermissions = requiredPermissions;
    }


    /**
     * @return true if device runs Android M+
     */
    public static boolean isRequired() {

        return Build.VERSION.SDK_INT > 22;
    }

    /**
     * @param permission required permission
     * @return true if permission is granted
     */
    public static boolean isAvailable(Context context, String permission) {

        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * @param permission required permission
     * @return true if additional explaination is required
     */
    public static boolean isDialogRequired(Context context, String permission) {

        return ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission);
    }

    /**
     * request permmisions (granted or forbiden permissions are exluded from dialog)
     * <p>
     * requestCode is GlobalCode.PERMMISSION_REQUEST to match with a result reported to onRequestPermissionsResult callback.
     * </p>
     *
     * @param permissions array of required permissions
     */
    public static void requestDialog(Context context, String... permissions) {

        requestDialog(context, 100, permissions);
    }

    /**
     * request permmisions (granted or forbiden permissions are exluded from dialog)
     *
     * @param requestCode 8-bit int to match with a result reported to onRequestPermissionsResult callback.
     * @param permissions array of required permissions
     */
    public static void requestDialog(Context context, int requestCode, String... permissions) {

        if (permissions == null) {
            return;
        }

        ActivityCompat.requestPermissions((Activity) context, permissions, requestCode);
    }

    /**
     * @param permissions array of permissions to check
     * @return array of required permissions
     */
    public static String[] checkPermissions(Context context, String... permissions) {

        if (permissions == null) {
            return null;
        }

        List<String> required = new ArrayList<>();

        for (String permission : permissions) {
            if (!isAvailable(context, permission)) {
                required.add(permission);
                Base.logE(permission + " is not granted");
            } else {
                Base.logD(permission + " is granted");
            }
        }

        if (required.size() == 0) {
            return null;
        } else {
            String[] out = new String[required.size()];
            out = required.toArray(out);

            return out;
        }
    }
}
