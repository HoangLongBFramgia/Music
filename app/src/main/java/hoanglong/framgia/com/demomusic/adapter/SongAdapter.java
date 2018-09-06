package hoanglong.framgia.com.demomusic.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import hoanglong.framgia.com.demomusic.R;
import hoanglong.framgia.com.demomusic.model.Song;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {


    private ArrayList<Song> songArrayList;
    private View.OnClickListener onClickListener;

    public SongAdapter(ArrayList<Song> songArrayList) {
        this.songArrayList = songArrayList;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public SongAdapter.SongViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song, viewGroup, false);
        itemView.setOnClickListener(onClickListener);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.SongViewHolder songViewHolder, int i) {
        Song song = songArrayList.get(i);
        songViewHolder.ivAvatar.setImageResource(song.getImageSong());
        songViewHolder.tvTitle.setText(song.getNameSong());
        songViewHolder.tvAuthor.setText(song.getAuthor());
        songViewHolder.view.setTag(song);
    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }

    class SongViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvAuthor;
        ImageView ivAvatar;
        View view;

        SongViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            view = itemView;
        }
    }
}
