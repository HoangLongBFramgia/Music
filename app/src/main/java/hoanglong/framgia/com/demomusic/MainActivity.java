package hoanglong.framgia.com.demomusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import hoanglong.framgia.com.demomusic.adapter.SongAdapter;
import hoanglong.framgia.com.demomusic.model.Song;
import hoanglong.framgia.com.demomusic.service.SongService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SongService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;
    private ArrayList<Song> songList;


    private int[] songImages = {R.drawable.viyeulanho, R.drawable.lactroi, R.drawable.dungnhuthoiquen, R.drawable.nguoilaoi};
    private int[] songs = {R.raw.viyeulanho, R.raw.lactroi, R.raw.dungnhuthoiquen, R.raw.nguoilaoi};
    private String[] songsTitle = {"Vì yêu là nhớ", "Lạc trôi", "Đừng như thói quen", "Người lạ ơi"};
    private String[] songsAuthor = {"Han Sara", "Sơn Tùng MTP", "Jayki & Sara", "Karik & Orange"};

    ImageView ivPlay, ivNext, ivPre;
    ImageView ivSong;
    RecyclerView rvSong;
    TextView tvTitle;
    TextView tvAuthor;
    ConstraintLayout constraintLayout;
    SeekBar seekBar;

    private double startTime = 0;
    private Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setView();
        setData();
        setListener();
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        SongAdapter songAdapter = new SongAdapter(songList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvSong.setNestedScrollingEnabled(false);
        rvSong.setHasFixedSize(true);
        rvSong.setLayoutManager(layoutManager);
        rvSong.setAdapter(songAdapter);

        songAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playMusic(view);
            }
        });
    }

    private void playMusic(View view) {
        Song song = (Song) view.getTag();
        int posn = rvSong.getChildAdapterPosition(view);

        musicSrv.setSong(posn);
        ivPlay.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
        musicSrv.playSong();
//        musicSrv.updateNotifications();

        //Update view Mini Player
        updateMiniPlayer(song);

        //Setup SeekBar
        seekBar.setMax(musicSrv.getDuration());
        seekBar.setProgress((int) startTime);
        myHandler.postDelayed(UpdateSongTime, 100);
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            if(musicSrv!=null) {
                startTime = musicSrv.getPosn();
                seekBar.setProgress((int) startTime);
                myHandler.postDelayed(this, 100);
            }
        }
    };

    private void updateMiniPlayer(Song song) {
        constraintLayout.setVisibility(View.VISIBLE);
        ivSong.setImageResource(song.getImageSong());
        tvTitle.setText(song.getNameSong());
        tvAuthor.setText(song.getAuthor());
    }

    private void setListener() {
        ivPlay.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        ivPre.setOnClickListener(this);
    }

    private void setData() {
        songList = new ArrayList<>();
        for (int i = 0; i < songImages.length; i++) {
            Song song = new Song(songs[i], songsTitle[i], songImages[i], songsAuthor[i]);
            songList.add(song);
        }
    }

    private void setView() {
        ivPlay = findViewById(R.id.iv_play);
        ivNext = findViewById(R.id.iv_next);
        ivPre = findViewById(R.id.iv_previous);
        ivSong = findViewById(R.id.iv_song);
        tvTitle = findViewById(R.id.tv_title);
        tvAuthor = findViewById(R.id.tv_author);
        rvSong = findViewById(R.id.rv_list);
        constraintLayout = findViewById(R.id.constraint);
        seekBar = findViewById(R.id.seekBar);

        //hide mini player
        constraintLayout.setVisibility(View.GONE);

        //init service
        musicSrv = new SongService();
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SongService.SongBinder binder = (SongService.SongBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setSongs(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, SongService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicSrv = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_play:
                if (isPlaying()) {
                    ivPlay.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                    musicSrv.pauseSong();
                } else {
                    ivPlay.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                    musicSrv.playSong();
                }
                break;
            case R.id.iv_next:
                musicSrv.nextSong();
                updateMiniPlayer(songList.get(musicSrv.getSongPosn()));
                break;
            case R.id.iv_previous:
                musicSrv.previousSong();
                updateMiniPlayer(songList.get(musicSrv.getSongPosn()));
                break;
        }
    }

    boolean isPlaying() {
        return musicSrv.isPlaying();
    }
}
