package com.netwokz.unwiredbridge.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by Steve on 9/9/13.
 */
public class Dialog {
    public static void error(Activity activity, int messageId) {
        error(activity, activity.getString(messageId));
    }

    public static void error(final Activity activity, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                activity.finish();
            }
        });
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle(android.R.string.dialog_alert_title);
        builder.show();
    }

    public static void question(final Activity activity, int messageId, int okId, DialogInterface.OnClickListener okListener, int noId, DialogInterface.OnClickListener noListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getString(messageId));
        builder.setCancelable(false);
        builder.setPositiveButton(activity.getString(okId), okListener);
        builder.setNegativeButton(activity.getString(noId), noListener);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle(android.R.string.dialog_alert_title);
        builder.show();
    }
}
