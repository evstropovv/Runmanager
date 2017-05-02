package com.vasyaevstropov.runmanager.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.vasyaevstropov.runmanager.Activities.MediaPlayerActivity;
import com.vasyaevstropov.runmanager.MainActivity;
import com.vasyaevstropov.runmanager.R;

/**
 * Created by Вася on 01.05.2017.
 */

public class PlayerNotification extends Notification {

    private Context ctx;
    private NotificationManager notificationManager;

    public PlayerNotification(Context context) {
        super();
        ctx = context;
        notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(ctx);

        Notification notification = builder.getNotification();
        notification.when = System.currentTimeMillis();

        RemoteViews contentView = new RemoteViews(ctx.getPackageName(), R.layout.notification_player);
        contentView.setImageViewResource(R.id.imgBtnPlay, android.R.drawable.ic_media_play);
        contentView.setImageViewResource(R.id.imgBtnPrevious, android.R.drawable.ic_media_previous);
        contentView.setImageViewResource(R.id.imgBtnNext, android.R.drawable.ic_media_next);

        setListeners(contentView);
        notification.contentView = contentView;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        CharSequence tickerText = "Ticker Text";
        notification.tickerText = tickerText;
        notification.icon = R.drawable.ic_menu_send;

        notificationManager.notify(548853, notification);
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
