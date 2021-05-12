package com.example.duan1_nhom2.AdapterClass;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1_nhom2.DAOClass.DAO_Nhac;
import com.example.duan1_nhom2.Model.Nhac;
import com.example.duan1_nhom2.R;

import java.util.ArrayList;
import java.util.HashMap;

public class SongInPlaylist_rvAdapter extends RecyclerView.Adapter<SongInPlaylist_rvAdapter.SongInPlaylist_ViewHolder> {
    private Context context;
    private ArrayList<Nhac> dsn;
    private ArrayList<Integer> positionNumber = new ArrayList<>();
    private HashMap<Integer, TextView> textViews = new HashMap<>();
    private int playPosition = -1;
    private boolean isShuffling = false;
    private boolean isLooping = false;
    private boolean isLoopingOne = false;
    private MediaPlayer mediaPlayer;
    private Handler myHandler = new Handler();
    public SongInPlaylist_rvAdapter(Context context, ArrayList<Nhac> dsn) {
        this.context = context;
        this.dsn = dsn;
    }

    public interface UpdateSongTime {
        void updateTimeTextView(String currentTime);
        void setEndTime(int seekbarTime,String endTime);
        void updateSeekBar(int progress);
    }

    private UpdateSongTime listener;

    @NonNull
    @Override
    public SongInPlaylist_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongInPlaylist_ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_playlist_dangnghe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SongInPlaylist_ViewHolder holder, final int position) {
        final Nhac nhac = dsn.get(position);
        textViews.put(position, holder.txtTenBaiHat);
        holder.txtTenBaiHat.setText(nhac.getTenNhac());
        holder.txtTenNgheSi.setText(nhac.getTenNgheSi());
        holder.txtThoiLuong.setText(nhac.getThoiLuong());
        holder.txtTenBaiHat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releaseMedia();
                playPosition = position;
                if (positionNumber.size() > 0) {
                    positionNumber.clear();
                }
                Log.d("Position:", String.valueOf(playPosition));
                triggerAutoPlay(playPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dsn.size();
    }

    static class SongInPlaylist_ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTenBaiHat, txtTenNgheSi, txtThoiLuong;

        SongInPlaylist_ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTenBaiHat = itemView.findViewById(R.id.txtTenBaiHat);
            txtTenNgheSi = itemView.findViewById(R.id.txtTenNgheSi);
            txtThoiLuong = itemView.findViewById(R.id.txtThoiLuong);
        }
    }

    public void setUpdateSongTimeListener(UpdateSongTime listener){
        this.listener = listener;
    }

    public void updatePlaylist(ArrayList<Nhac> dsn) {
        this.dsn = dsn;
        notifyDataSetChanged();
    }

    public void releaseMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
    public void onSeekbarProgressChanged(int progress){
        if (mediaPlayer != null){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.seekTo(progress);
            }
        }
    }
    public boolean playAndPauseMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                return true;
            } else {
                mediaPlayer.start();
                myHandler.postDelayed(UpdateSongTime, 500);
                return false;
            }
        }
        return false;
    }

    public void skipToPrevious() {
        releaseMedia();
        if (isShuffling){
            while (true){
                int newPlayPosition = (int) (100.0 * Math.random() % dsn.size());
                if (playPosition != newPlayPosition) {
                    playPosition = newPlayPosition;
                    break;
                }
            }
            triggerAutoPlay(playPosition);
        } else if (playPosition > 0) {
            playPosition -= 1;
            triggerAutoPlay(playPosition);
        }else {
            playPosition = dsn.size() - 1;
            triggerAutoPlay(playPosition);
        }
    }

    public void skipToNext() {
        releaseMedia();
        if (isShuffling){
            while (true){
                int newPlayPosition = (int) (100.0 * Math.random() % dsn.size());
                if (playPosition != newPlayPosition) {
                    playPosition = newPlayPosition;
                    break;
                }
            }
            triggerAutoPlay(playPosition);
        } else if (playPosition < dsn.size()-1){
            playPosition += 1;
            triggerAutoPlay(playPosition);
        }else {
            playPosition = 0;
            triggerAutoPlay(playPosition);
        }
    }

    public void setLoopingOne(boolean isLoopingOne) {
        this.isLoopingOne = isLoopingOne;
    }

    public void setShuffling(boolean isShuffling) {
        this.isShuffling = isShuffling;
        if (positionNumber.size() > 0) {
            positionNumber.clear();
        }
    }

    public void setLooping(boolean isLooping) {
        this.isLooping = isLooping;
    }

    private void triggerAutoPlay(final int position) {
        for (Integer i: textViews.keySet()){
            if (i == position){
                textViews.get(i).setTextColor(Color.CYAN);
            }else {
                textViews.get(i).setTextColor(Color.WHITE);
            }
        }
        Nhac nhac = dsn.get(position);
        if (nhac.getURL() != null) {
            mediaPlayer = DAO_Nhac.createMediaPlayer(nhac);
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                listener.setEndTime(mp.getDuration(),changeTimeFormat(mp.getDuration()));
                myHandler.postDelayed(UpdateSongTime, 500);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                textViews.get(position).setTextColor(Color.WHITE);
                if (isLoopingOne) {
                    triggerAutoPlay(playPosition);
                } else if (isShuffling && isLooping) {
                    while (true){
                        int newPlayPosition = (int) (100.0 * Math.random() % dsn.size());
                        if (playPosition != newPlayPosition) {
                            playPosition = newPlayPosition;
                            break;
                        }
                    }
                    triggerAutoPlay(playPosition);
                } else if (isShuffling) {
                    while (positionNumber.size() != dsn.size()) {
                        playPosition = (int) (100.0 * Math.random() % dsn.size());
                        if (!positionNumber.contains(playPosition)) {
                            positionNumber.add(playPosition);
                            Log.d("Shuffle: ", positionNumber.toString());
                            triggerAutoPlay(playPosition);
                            break;
                        }
                    }
                } else if (isLooping) {
                    if (playPosition < dsn.size() - 1) {
                        playPosition += 1;
                        triggerAutoPlay(playPosition);
                    } else {
                        playPosition = 0;
                        triggerAutoPlay(playPosition);
                    }
                } else {
                    if (playPosition < dsn.size() - 1) {
                        playPosition += 1;
                        triggerAutoPlay(playPosition);
                    } else {
                        releaseMedia();
                        Log.d("Mediaplayer: ", "Finished list, Media is released!");
                    }
                }
                myHandler.removeCallbacks(UpdateSongTime);
            }
        });
        mediaPlayer.prepareAsync();
    }
    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            int progress = mediaPlayer.getCurrentPosition();
            listener.updateSeekBar(progress);
            listener.updateTimeTextView(changeTimeFormat(progress));
            myHandler.postDelayed(this, 500);
        }
    };
    private String changeTimeFormat(int endDuration){
        int totalMin = Math.abs((endDuration/60000));
        int totalSecond = (endDuration%60000)/1000;
        if (totalSecond<10){
            return totalMin + ":0" + totalSecond;
        }else {
            return totalMin + ":" + totalSecond;
        }
    }
}
