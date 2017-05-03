package com.vasyaevstropov.runmanager.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.vasyaevstropov.runmanager.Activities.MediaPlayerActivity;
import com.vasyaevstropov.runmanager.MainActivity;
import com.vasyaevstropov.runmanager.R;
import com.vasyaevstropov.runmanager.Services.MusicService;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Вася on 01.05.2017.
 */

public class PlayerNotification extends Notification {

    private static final String ACTION_TOGGLE_PLAYBACK = "com.vasyaevstropov.runmanager.TOGGLE_PLAYBACK";
    private static final String ACTION_PREV = "com.vasyaevstropov.runmanager.PREV";
    private static final String ACTION_NEXT = "com.vasyaevstropov.runmanager.NEXT";

    private Context ctx;
    private NotificationManager notificationManager;

    public PlayerNotification(Context context) {
        super();
        ctx = context;


        if ( Build.VERSION.SDK_INT >= 21 ) {
            Toast.makeText(context, Build.VERSION.SDK_INT+"", Toast.LENGTH_LONG).show();
            buildNotify21(ctx);
        } else{
            Toast.makeText(context, Build.VERSION.SDK_INT+"", Toast.LENGTH_LONG).show();
            buldNotify15(ctx);
        }




    }

    private void buldNotify15(Context ctx) {
        notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(ctx);
        builder.setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle("Track title")
                .setContentText("Artist - album")

                .build();


        Notification notification = builder.getNotification();
        notification.when = System.currentTimeMillis();



      //  setListeners(contentView);
        notification.contentView = contentView;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        CharSequence tickerText = "Ticker Text";
        notification.tickerText = tickerText;
        notification.icon = R.drawable.ic_menu_send;

           notificationManager.notify(548853, notification);

    }

    private void buildNotify21(Context context) {
        final Bitmap artwork = BitmapFactory.decodeResource(context.getResources(), R.drawable.common_google_signin_btn_icon_dark);


        // Create a new MediaSession
        final MediaSession mediaSession = new MediaSession(context, "debug tag");
        // Update the current metadata

        mediaSession.setMetadata(new MediaMetadata.Builder()
                .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, artwork)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, "Pink Floyd")
                .putString(MediaMetadata.METADATA_KEY_ALBUM, "Dark Side of the Moon")
                .putString(MediaMetadata.METADATA_KEY_TITLE, "The Great Gig in the Sky")
                .build());
        // Indicate you're ready to receive media commands
        mediaSession.setActive(true);
        // Attach a new Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSession.Callback() {

            // Implement your callbacks

        });
        // Indicate you want to receive transport controls via your Callback
        mediaSession.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Create a new Notification
        final Notification noti = new Notification.Builder(context)
                // Hide the timestamp
                .setShowWhen(false)
                // Set the Notification style
                .setStyle(new Notification.MediaStyle()
                        // Attach our MediaSession token
                        .setMediaSession(mediaSession.getSessionToken())
                        // Show our playback controls in the compat view
                        .setShowActionsInCompactView(0, 1, 2))
                // Set the Notification color
                .setColor(0xFFDB4437)
                // Set the large and small icons
                .setLargeIcon(artwork)
                .setSmallIcon(android.R.drawable.ic_media_play)
                // Set Notification content information
                .setContentText("Pink Floyd")
                .setContentInfo("Dark Side of the Moon")
                .setContentTitle("The Great Gig in the Sky")
                // Add some playback controls
//                .addAction(android.R.drawable.ic_media_previous, "prev", retreivePlaybackAction(3, ctx))
//                .addAction(android.R.drawable.ic_media_pause, "pause", retreivePlaybackAction(1, ctx))
//                .addAction(android.R.drawable.ic_media_next, "next", retreivePlaybackAction(2, ctx))
                .build();

        // Do something with your TransportControls
        final MediaController.TransportControls controls = mediaSession.getController().getTransportControls();

        ((NotificationManager) context.getSystemService(NOTIFICATION_SERVICE)).notify(1, noti);


    }

    private PendingIntent retreivePlaybackAction(int which, Context context) {
        Intent action;
        PendingIntent pendingIntent;
        final ComponentName serviceName = new ComponentName(context, MusicService.class);
        switch (which) {
            case 1:
                // Play and pause
                action = new Intent(ACTION_TOGGLE_PLAYBACK);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(context, 1, action, 0);
                return pendingIntent;
            case 2:
                // Skip tracks
                action = new Intent(ACTION_NEXT);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(context, 2, action, 0);
                return pendingIntent;
            case 3:
                // Previous tracks
                action = new Intent(ACTION_PREV);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(context, 3, action, 0);
                return pendingIntent;
            default:
                break;
        }
        return null;
    }


    private void setListeners(RemoteViews contentView) {
        Intent intentPrevious = new Intent(ctx, MediaPlayerActivity.class);
        intentPrevious.putExtra("DO", "previous");
        PendingIntent pendPrevious = PendingIntent.getService(ctx, 0, intentPrevious, 0);
        contentView.setOnClickPendingIntent(R.id.imgBtnPrevious, pendPrevious);

        Intent intentPlayStop = new Intent(ctx, MediaPlayerActivity.class);
        intentPlayStop.putExtra("DO", "playstop");
        PendingIntent pendPlayStop = PendingIntent.getService(ctx, 1, intentPlayStop, 0);
        contentView.setOnClickPendingIntent(R.id.imgBtnPlay, pendPlayStop);

        Intent intentNext = new Intent(ctx, MediaPlayerActivity.class);
        intentNext.putExtra("DO", "next");
        PendingIntent pendNext = PendingIntent.getService(ctx, 2, intentNext, 0);
        contentView.setOnClickPendingIntent(R.id.imgBtnPrevious, pendNext);
    }
}
