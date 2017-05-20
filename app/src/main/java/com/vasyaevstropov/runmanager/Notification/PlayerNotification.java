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
import android.support.annotation.BoolRes;
import android.support.annotation.RequiresApi;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.vasyaevstropov.runmanager.Activities.MediaPlayerActivity;
import com.vasyaevstropov.runmanager.DB.Preferences;
import com.vasyaevstropov.runmanager.Fragments.MusicFragment;
import com.vasyaevstropov.runmanager.MainActivity;
import com.vasyaevstropov.runmanager.Models.MediaContent;
import com.vasyaevstropov.runmanager.R;
import com.vasyaevstropov.runmanager.Services.MusicService;

import static android.content.Context.NOTIFICATION_SERVICE;


public class PlayerNotification extends Notification {

    private static final String ACTION_TOGGLE_PLAYBACK = "com.vasyaevstropov.runmanager.TOGGLE_PLAYBACK";
    private static final String ACTION_PREV = "com.vasyaevstropov.runmanager.PREV";
    private static final String ACTION_NEXT = "com.vasyaevstropov.runmanager.NEXT";

    RemoteViews contentView;
    private Context ctx;
    private NotificationManager notificationManager;
    private MediaContent mediaContent;
    Boolean isPlaing;

    public PlayerNotification(Context context, MediaContent mediaContent, Boolean isPlaying) {
        super();
        ctx = context;
        this.mediaContent = mediaContent;
        this.isPlaing = isPlaying;


        if (Build.VERSION.SDK_INT >= 21) {
            Toast.makeText(context, Build.VERSION.SDK_INT + "", Toast.LENGTH_LONG).show();
            buildNotify21(ctx);
        } else {
            Toast.makeText(context, Build.VERSION.SDK_INT + "", Toast.LENGTH_LONG).show();
            buldNotify15(ctx);
        }

    }

    private void buldNotify15(Context ctx) {
        notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(ctx);
        builder.setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle("Track title")
                .setContentText("Artist - album")
                .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.side_nav_bar))
                .build();

        Notification notification = builder.getNotification();
        notification.when = System.currentTimeMillis();

        contentView = new RemoteViews(ctx.getPackageName(), R.layout.notification_player); // Создаем экземпляр RemoteViews указывая использовать разметку нашего уведомления

        contentView.setImageViewResource(R.id.imgBtnNext, android.R.drawable.ic_media_next);
        contentView.setImageViewResource(R.id.imgBtnPlay, android.R.drawable.ic_media_play);
        contentView.setImageViewResource(R.id.imgBtnPrevious, android.R.drawable.ic_media_previous);

        contentView.setOnClickPendingIntent(R.id.imgBtnPlay, retreivePlaybackAction(1, ctx)); // для обработки нажатия
        contentView.setOnClickPendingIntent(R.id.imgBtnNext, retreivePlaybackAction(2, ctx));
        contentView.setOnClickPendingIntent(R.id.imgBtnPrevious, retreivePlaybackAction(3, ctx));
        contentView.setOnClickPendingIntent(R.id.layoutMediaPlayer, retreivePlaybackAction(4, ctx));

        //  setListeners(contentView);
        notification.contentView = contentView; // Присваиваем contentView нашему уведомлению
        notification.flags |= Notification.FLAG_ONGOING_EVENT; // не убираем уведомление

        CharSequence tickerText = "Ticker Text";
        notification.tickerText = tickerText;
        notification.icon = R.drawable.ic_menu_send;

        notificationManager.notify(548853, notification);


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void buildNotify21(Context context) {
        final Bitmap artwork = BitmapFactory.decodeResource(context.getResources(), R.drawable.common_google_signin_btn_icon_dark);

        // Create a new MediaSession
        final MediaSession mediaSession = new MediaSession(context, "debug tag");
        // Update the current metadata
        mediaSession.setMetadata(new MediaMetadata.Builder()
                .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, artwork)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, mediaContent.getArtist())
                .putString(MediaMetadata.METADATA_KEY_ALBUM, " ")
                .putString(MediaMetadata.METADATA_KEY_TITLE, mediaContent.getTitle())
                .build());

        mediaSession.setActive(true);

        mediaSession.setCallback(new MediaSession.Callback() {
            public void onPause() {
                super.onPause();
            }

            public void onPlay() {
                super.onPlay();
            }

            public void onStop() {
                super.onStop();
            }
        });
        // Indicate you want to receive transport controls via your Callback
        mediaSession.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);


        final Notification noti = new Notification.Builder(context)

                .setShowWhen(false)

                .setStyle(new Notification.MediaStyle()

                        .setMediaSession(mediaSession.getSessionToken())

                        .setShowActionsInCompactView(1, 2)) //0 - prev, 1 - play, 2 - next

                .setColor(ctx.getResources().getColor(R.color.colorAccent))

                .setLargeIcon(artwork)
                .setSmallIcon(android.R.drawable.ic_media_play)

                .setContentText(mediaContent.getArtist())
                .setContentInfo("")
                .setContentTitle(mediaContent.getTitle())
                // Add some playback controls
                .addAction(android.R.drawable.ic_media_previous, "prev", retreivePlaybackAction(3, ctx))
                .addAction(isPlaing ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play,
                        "pause",
                        retreivePlaybackAction(1, ctx))
                .addAction(android.R.drawable.ic_media_next, "next", retreivePlaybackAction(2, ctx))

                .setContentIntent(retreivePlaybackAction(4, ctx))

                .build();

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
            case 4:
                action = new Intent(ctx, MediaPlayerActivity.class);
                action.putExtra("Music", true);
                pendingIntent = PendingIntent.getActivity(context, 4, action, PendingIntent.FLAG_UPDATE_CURRENT);
                return pendingIntent;
            default:
                break;
        }
        return null;
    }

}
