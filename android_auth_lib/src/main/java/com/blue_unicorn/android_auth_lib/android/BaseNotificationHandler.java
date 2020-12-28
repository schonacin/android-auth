package com.blue_unicorn.android_auth_lib.android;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.blue_unicorn.android_auth_lib.R;
import com.blue_unicorn.android_auth_lib.android.authentication.AuthInfo;
import com.blue_unicorn.android_auth_lib.android.constants.AuthenticatorAPIMethod;
import com.blue_unicorn.android_auth_lib.android.constants.IntentAction;
import com.blue_unicorn.android_auth_lib.android.constants.IntentExtra;
import com.blue_unicorn.android_auth_lib.android.constants.NotificationID;

import timber.log.Timber;

public class BaseNotificationHandler implements NotificationHandler {

    private Context context;
    private Class mainActivity;

    private static final String REQUEST_CHANNEL_ID = "FIDO_REQUESTS";
    private static final String NOTIFY_CHANNEL_ID = "FIDO_NOTIFICATIONS";
    private static final String SERVICE_RUNNING_CHANNEL_ID = "FIDO_SERVICE_RUNNING";


    public BaseNotificationHandler(Context context, Class activityClass) {
        this.context = context;
        this.mainActivity = activityClass;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createFidoChannels();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createFidoChannels() {
        Timber.d("Creating Notification Channels...");
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

        {
            CharSequence name = "FIDO_SERVICE_RUNNING_CHANNEL";
            String description = "Channel showing that the service is running";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(SERVICE_RUNNING_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private NotificationCompat.Builder buildNotification(String channelId) {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
                return "Operation " + appendix;
        }
    }

    public void notifyFailure() {
        notify("Something went wrong!");
    }

    public void notifyResult(@NonNull AuthInfo authInfo, boolean success) {
        String message = getMessage(authInfo.getMethod(), success);
        notify(message);
    }

    public void notify(@NonNull String message) {
        Timber.d("Displaying Notification to show message \"%s\"", message);
        Intent resultIntent = new Intent(context, FidoAuthService.class);
        NotificationCompat.Builder builder =
                buildNotification(NOTIFY_CHANNEL_ID)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setContentTitle(message);

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
        requestApproval(authInfo, authenticationRequired, null);
    }

    public void requestApproval(AuthInfo authInfo, boolean authenticationRequired, @Nullable Integer freshness) {
        Timber.d("Displaying Notification to request User Approval");
        Intent approve = new Intent(context, mainActivity);
        Intent decline = new Intent(context, mainActivity);
        if (authenticationRequired) {
            approve.setAction(IntentAction.CTAP_APPROVE_NOTIFICATION_FOR_AUTHENTICATION);
        } else {
            approve.setAction(IntentAction.CTAP_APPROVE_NOTIFICATION);
        }
        decline.setAction(IntentAction.CTAP_DECLINE_NOTIFICATION);

        if (freshness != null) {
            approve.putExtra(IntentExtra.AUTHENTICATION_FRESHNESS, freshness);
        }

        PendingIntent approveIntent = PendingIntent.getActivity(context, 0, approve, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent declineIntent = PendingIntent.getActivity(context, 0, decline, PendingIntent.FLAG_UPDATE_CURRENT);

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

    public void showServiceActiveNotification(Context context) {
        Timber.d("Displaying Notification that FIDO Service is active");
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, mainActivity), 0);

        String text = "Fido Advertising Service is Running.";

        NotificationCompat.Builder builder =
                buildNotification(SERVICE_RUNNING_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setTicker(text)
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle("FIDO BLE")
                        .setContentText(text)
                        .setContentIntent(contentIntent)
                        .setOngoing(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.cancel(NotificationID.SHOW_SERVICE);
        notificationManagerCompat.notify(NotificationID.SHOW_SERVICE, builder.build());
    }

    public void closeNotification(@NotificationID int id) {
        Timber.d("Closing Notification with id %i", id);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.cancel(id);
    }

    public void closeAllNotifications() {
        Timber.d("Closing all Notifications");
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.cancel(NotificationID.REQUEST);
        notificationManagerCompat.cancel(NotificationID.NOTIFY);
        notificationManagerCompat.cancel(NotificationID.SHOW_SERVICE);
    }

}
