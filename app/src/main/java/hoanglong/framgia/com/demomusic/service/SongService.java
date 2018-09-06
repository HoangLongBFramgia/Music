package hoanglong.framgia.com.demomusic.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import java.util.ArrayList;

import hoanglong.framgia.com.demomusic.Constant;
import hoanglong.framgia.com.demomusic.MainActivity;
import hoanglong.framgia.com.demomusic.MusicReceiver;
import hoanglong.framgia.com.demomusic.R;
import hoanglong.framgia.com.demomusic.model.Song;

public class SongService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private static final int NOTIFICATION_ID = 1;
    MediaPlayer player;
    private ArrayList<Song> songs;
    private int songPosn;
    private int resImageSong;
    private final IBinder songBind = new SongBinder();
    private String songTitle;

    //Notification
    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    RemoteViews notificationLayout;

    @Override
    public void onCreate() {
        super.onCreate();
        //init song position
        songPosn = 0;
        //create player
        player = new MediaPlayer();
        //init player
        initMediaPlayer();

    }

    private void initMediaPlayer() {
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listener
        player.setOnPreparedListener(this);
        player.setOnErrorListener(this);
        player.setOnCompletionListener(this);
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    //binder
    public class SongBinder extends Binder {
        public SongService getService() {
            return SongService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return songBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    public void playSong() {
        //reset player
        player.reset();
        //get song
        Song song = songs.get(songPosn);
        songTitle = song.getNameSong();
        resImageSong = song.getImageSong();
        //Create
        player = MediaPlayer.create(getApplicationContext(), song.getSong());
        player.start();
        notification();
        //updateNotification();
    }

    //set the song
    public void setSong(int songIndex) {
        songPosn = songIndex;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (player.getCurrentPosition() > 0) {
            mediaPlayer.reset();
            nextSong();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mediaPlayer.reset();
        return false;
    }

    public void notification() {
        //notification
        notificationLayout = new RemoteViews(getPackageName(), R.layout.custom_notify);
        notificationLayout.setImageViewResource(R.id.iv_song_notify, songs.get(songPosn).getImageSong());
        notificationLayout.setTextViewText(R.id.tv_title_notify, songs.get(songPosn).getNameSong());
        notificationLayout.setTextViewText(R.id.tv_author_notify, songs.get(songPosn).getAuthor());

        mBuilder = new NotificationCompat.Builder(getApplicationContext(), "notify_001");
        Intent ii = new Intent(getApplicationContext(), MainActivity.class);
        ii.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, ii, 0);

        mBuilder.setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(Notification.PRIORITY_MAX)
                .setStyle(new NotificationCompat.BigPictureStyle())
                .setAutoCancel(true).setOngoing(true).setContent(notificationLayout);


        mNotificationManager =
                (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }

        BroadcastReceiver mReceiver = new MusicReceiver();
        registerReceiver(mReceiver, new IntentFilter(Constant.ACTION_NEXT));
        registerReceiver(mReceiver, new IntentFilter(Constant.ACTION_PLAY));
        registerReceiver(mReceiver, new IntentFilter(Constant.ACTION_PREVIOUS));

        setClickNext(notificationLayout, getApplicationContext());
        setClickPlay(notificationLayout, getApplicationContext());
        setClickPrevious(notificationLayout, getApplicationContext());

        mNotificationManager.notify(0, mBuilder.build());
    }

    private void setClickPrevious(RemoteViews view, Context context) {
        Intent intent = new Intent(Constant.ACTION_PLAY);
        PendingIntent pPlay = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.iv_previous_notify, pPlay);
    }

    private void setClickPlay(RemoteViews view, Context context) {
        Intent intent = new Intent(Constant.ACTION_PLAY);
        PendingIntent pPlay = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.iv_previous_notify, pPlay);
    }

    private void setClickNext(RemoteViews view, Context context) {
        Intent intent = new Intent(Constant.ACTION_NEXT);
        PendingIntent pNext = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.iv_next_notify, pNext);
    }

    public void updateNotification() {
        if (isPlaying()) {
            notificationLayout.setImageViewResource(R.id.iv_play_notify, R.drawable.ic_play_circle_filled_black_24dp);
            mBuilder.setOngoing(false);
        } else {
            notificationLayout.setImageViewResource(R.id.iv_play_notify, R.drawable.ic_pause_circle_filled_black_24dp);
            mBuilder.setOngoing(true);
        }
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
    }

    public int getPosn() {
        return player.getCurrentPosition();
    }

    public int getSongPosn() {
        return songPosn;
    }

    public int getDuration() {
        return player.getDuration();
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public void pauseSong() {
        updateNotification();
        player.pause();
    }

    public void previousSong() {
        songPosn--;
        if (songPosn < 0) {
            songPosn = songs.size() - 1;
        }
        playSong();
    }

    public void nextSong() {
        songPosn++;
        if (songPosn >= songs.size()) {
            songPosn = 0;
        }
        playSong();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }
}
