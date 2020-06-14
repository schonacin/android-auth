package com.blue_unicorn.android_auth_lib.android;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.blue_unicorn.android_auth_lib.R;
import com.blue_unicorn.android_auth_lib.android.authentication.AuthInfo;
import com.blue_unicorn.android_auth_lib.android.constants.AuthenticatorAPIMethod;
import com.blue_unicorn.android_auth_lib.android.constants.IntentAction;
import com.blue_unicorn.android_auth_lib.android.constants.NotificationID;

public class NotificationHandler {

    private Context context;
    private Class mainActivity;

    private static final String REQUEST_CHANNEL_ID = "FIDO_REQUESTS";
    private static final String NOTIFY_CHANNEL_ID = "FIDO_NOTIFICATIONS";

    public NotificationHandler(Context context) {
        this.context = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createFidoChannels();
        }
    }

    public void setMainActivity(Class mainActivity) {
        this.mainActivity = mainActivity;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createFidoChannels() {
        {
            CharSequence name = "FIDO_REQUEST_CHANNEL";
            String description = "Channel for FIDO requests";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(REQUEST_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        {
            CharSequence name = "FIDO_NOTIFY_CHANNEL";
            String description = "Channel for FIDO notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFY_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private NotificationCompat.Builder buildNotification(String channelId) {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            builder = new NotificationCompat.Builder(context, channelId);
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        return builder
                .setSmallIcon(R.drawable.ic_launcher_foreground);
    }

    private String getMessage(@AuthenticatorAPIMethod int method, boolean success) {
        String appendix;
        if (success) appendix = " successful.";
        else appendix = " failed.";
        switch (method) {
            case AuthenticatorAPIMethod.MAKE_CREDENTIAL:
                return "Registration " + appendix;
            case AuthenticatorAPIMethod.GET_ASSERTION:
                return "Authentication" + appendix;
            default:
                return null;
        }
    }

    public void notify(@AuthenticatorAPIMethod int method, boolean success) {
        String msg = getMessage(method, success);
        if (msg == null) return;

        Intent resultIntent = new Intent(context, FidoAuthService.class);
        NotificationCompat.Builder builder =
                buildNotification(NOTIFY_CHANNEL_ID)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setContentTitle(msg);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(mainActivity);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.cancel(NotificationID.NOTIFY);
        notificationManagerCompat.notify(NotificationID.NOTIFY, builder.build());
    }

    public void requestApproval(AuthInfo authInfo, boolean authenticationRequired) {
        Intent approve = new Intent(context, mainActivity);
        Intent decline = new Intent(context, mainActivity);
        if (authenticationRequired) {
            approve.setAction(IntentAction.CTAP_APPROVE_NOTIFICATION_FOR_AUTHENTICATION);
        } else {
            approve.setAction(IntentAction.CTAP_APPROVE_NOTIFICATION);
        }
        decline.setAction(IntentAction.CTAP_DECLINE_NOTIFICATION);

        PendingIntent approveIntent = PendingIntent.getActivity(context, 0, approve, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent declineIntent = PendingIntent.getActivity(context, 0, decline, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action.Builder approveBuilder = new NotificationCompat.Action.Builder(
                R.drawable.ic_launcher_background, "Once", approveIntent);
        NotificationCompat.Builder builder =
                buildNotification(REQUEST_CHANNEL_ID)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setContentTitle(authInfo.getTitle())
                        .setContentText("Relying party: " + authInfo.getRp() + "\n"
                                + "Username: " + authInfo.getUser())
                        .setDeleteIntent(declineIntent)
                        .addAction(R.drawable.ic_launcher_background, "Approve", approveIntent)
                        .setTimeoutAfter(20000);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.cancel(NotificationID.REQUEST);
        notificationManagerCompat.notify(NotificationID.REQUEST, builder.build());
    }

    public void closeNotification(@NotificationID int id) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.cancel(id);
    }

    public void closeAllNotifications() {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.cancel(NotificationID.REQUEST);
        notificationManagerCompat.cancel(NotificationID.NOTIFY);
    }

}
