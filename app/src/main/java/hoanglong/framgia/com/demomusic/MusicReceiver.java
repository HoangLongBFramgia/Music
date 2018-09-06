package hoanglong.framgia.com.demomusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import hoanglong.framgia.com.demomusic.service.SongService;

public class MusicReceiver extends BroadcastReceiver {

    SongService songService;
    private Intent playIntent;
    private boolean musicBound = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (Constant.ACTION_NEXT.equals(action)) {
            Toast.makeText(context, "next music", Toast.LENGTH_SHORT).show();
        } else if (Constant.ACTION_PLAY.equals(action)) {
            Toast.makeText(context, "play music", Toast.LENGTH_SHORT).show();
        } else if (Constant.ACTION_PREVIOUS.equals(action)) {
            Toast.makeText(context, "pause music", Toast.LENGTH_SHORT).show();
        }
    }
}
