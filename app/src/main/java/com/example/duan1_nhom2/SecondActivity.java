package com.example.duan1_nhom2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duan1_nhom2.AdapterClass.SongInPlaylist_rvAdapter;
import com.example.duan1_nhom2.Model.Nhac;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity implements SongInPlaylist_rvAdapter.UpdateSongTime {
    TextView txtTenNhac, txtTenNgheSi, txtTheLoai, txtThoiLuong, txtURL, txtReceivedUsername, txtReceivedEmail, txtCurrentTime, txtEndTime;
    Button btnFirstSong, btnSecondSong, btnThirdSong, btnNext, btnLogout, btnFourthSong;
    ImageView btnPlayAndPause2, ivProfilePicture, ivExpand, ivCollapse, ivPlayAndPause, ivSkipToPrevious, ivSkipToNext, ivShuffle, ivLooping;
    MediaPlayer mediaPlayer;
    SeekBar seekBar2, seekBar3;
    BottomSheetBehavior mBottomSheetBehavior;
    View bottomSheet;
    ArrayList<Nhac> dsn = new ArrayList<>();
    RecyclerView rvPlaylist;
    SongInPlaylist_rvAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    Handler myHandler = new Handler();
    int tracker1 = 0;
    int tracker2 = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        findView();
        onReceivedUserProfile();
        adapter = new SongInPlaylist_rvAdapter(getBaseContext(), dsn);
        layoutManager = new LinearLayoutManager(getBaseContext());
        rvPlaylist.setAdapter(adapter);
        rvPlaylist.setLayoutManager(layoutManager);
        adapter.setUpdateSongTimeListener(this);
        mBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED){
                    mBottomSheetBehavior.setDraggable(true);
                }else if (newState == BottomSheetBehavior.STATE_EXPANDED){
                    mBottomSheetBehavior.setDraggable(false);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        seekBar2.setClickable(false);
        btnPlayAndPause2.setClickable(false);
        ivPlayAndPause.setClickable(false);
        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                adapter.onSeekbarProgressChanged(seekBar.getProgress());
            }
        });
        btnPlayAndPause2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeAndPauseAudio();
            }
        });
        btnFirstSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queueMusic("Cho Nhau Lối Đi Riêng");
            }
        });
        btnSecondSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queueMusic("When The Shadow Reveals You");
            }
        });
        btnThirdSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queueMusic("Make A Stand");
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SecondActivity.this, ThirdActivity.class));
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
        ivExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        ivCollapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        ivSkipToNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.skipToNext();
                ivPlayAndPause.setImageResource(R.drawable.ic_pause);
            }
        });
        ivSkipToPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.skipToPrevious();
                ivPlayAndPause.setImageResource(R.drawable.ic_pause);
            }
        });
        ivPlayAndPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.playAndPauseMusic()){
                    ivPlayAndPause.setImageResource(R.drawable.ic_play_arrow);
                }else{
                    ivPlayAndPause.setImageResource(R.drawable.ic_pause);
                }
            }
        });
        ivShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tracker1%2==0){
                    ivShuffle.setImageResource(R.drawable.ic_shuffle_red);
                    adapter.setShuffling(true);
                }else {
                    ivShuffle.setImageResource(R.drawable.ic_shuffle);
                    adapter.setShuffling(false);
                }
                tracker1 += 1;
            }
        });
        ivLooping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tracker2==3){
                    ivLooping.setImageResource(R.drawable.ic_repeat_one);
                    adapter.setLoopingOne(true);
                    tracker2 = 1;
                } else if (tracker2==2){
                    ivLooping.setImageResource(R.drawable.ic_repeat_red);
                    adapter.setLooping(true);
                    adapter.setLoopingOne(false);
                    tracker2 = 3;
                }else {
                    ivLooping.setImageResource(R.drawable.ic_repeat);
                    adapter.setLooping(false);
                    adapter.setLoopingOne(false);
                    tracker2 = 2;
                }
            }
        });
        btnFourthSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queueMusic("Matsuyoi Hime");
            }
        });
    }
    private void queueMusic(final String songName){
        FirebaseDatabase data = FirebaseDatabase.getInstance();
        DatabaseReference myRef = data.getReference().child("Nhac");
        Query query = myRef.orderByChild("TenNhac").equalTo(songName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot i: snapshot.getChildren()){
                        Nhac nhac = i.getValue(Nhac.class);
                        dsn.add(nhac);
                        adapter.updatePlaylist(dsn);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void fetchAudioFileFromFirebase(final String songName) {
        mediaPlayer = new MediaPlayer();
        FirebaseDatabase data = FirebaseDatabase.getInstance();
        DatabaseReference myRef = data.getReference().child("Nhac");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot i : snapshot.getChildren()) {
                    Nhac nhac = i.getValue(Nhac.class);
                    if (nhac.getTenNhac().equals(songName)) {
                        txtTenNhac.setText(nhac.getTenNhac());
                        txtTenNgheSi.setText(nhac.getTenNgheSi());
                        txtTheLoai.setText(nhac.getTheLoai());
                        txtThoiLuong.setText(nhac.getThoiLuong());
                        txtURL.setText(nhac.getURL());
                        try {
                            mediaPlayer.setDataSource(nhac.getURL());
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            releaseMedia();
                                        }
                                    });
                                    seekBar2.setMax(mediaPlayer.getDuration());
                                    seekBar2.setProgress(0);
                                    btnPlayAndPause2.setClickable(true);
                                }
                            });
                            mediaPlayer.prepareAsync();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void resumeAndPauseAudio(){
        if (mediaPlayer != null){
            if (mediaPlayer.isPlaying()){
                btnPlayAndPause2.setImageResource(R.drawable.ic_play_arrow);
                mediaPlayer.pause();
            }else {
                btnPlayAndPause2.setImageResource(R.drawable.ic_pause);
                mediaPlayer.start();
                myHandler.postDelayed(UpdateSongTime, 100);
            }
        }
    }
    Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            int currentTime = mediaPlayer.getCurrentPosition();
            seekBar2.setProgress(currentTime);
            myHandler.postDelayed(this, 100);
        }
    };
    private void onReceivedUserProfile(){
        Bundle bundle = getIntent().getBundleExtra("UserProfile");
        if (bundle != null){
            int loginAs = bundle.getInt("LoginAs");
            if (loginAs == 1){
                txtReceivedEmail.setText(bundle.getString("Email"));
            }else{
                txtReceivedUsername.setText(bundle.getString("Username"));
            }
            if (bundle.getString("PhotoURL") != null){
                Picasso.with(this).load(bundle.getString("PhotoURL")).into(ivProfilePicture);
            }
        }
    }
    private void logOut(){
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(SecondActivity.this, LoginActivity.class));
        }
    }
    private void findView() {
        txtTenNgheSi = findViewById(R.id.txtTenNgheSi);
        txtTenNhac = findViewById(R.id.txtTenNhac);
        txtTheLoai = findViewById(R.id.txtTheLoai);
        txtThoiLuong = findViewById(R.id.txtThoiLuong);
        txtURL = findViewById(R.id.txtURL);
        btnFirstSong = findViewById(R.id.btnFirstSong);
        btnSecondSong = findViewById(R.id.btnSecondSong);
        btnThirdSong = findViewById(R.id.btnThirdSong);
        btnNext = findViewById(R.id.btnNext);
        btnPlayAndPause2 = findViewById(R.id.btnPlayAndPause2);
        seekBar2 = findViewById(R.id.seekBar2);
        txtReceivedUsername = findViewById(R.id.txtReceivedUsername);
        txtReceivedEmail = findViewById(R.id.txtReceivedUserEmail);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        btnLogout = findViewById(R.id.btnLogOut);
        ivExpand = findViewById(R.id.ivExpand);
        ivCollapse = findViewById(R.id.ivCollapse);
        bottomSheet = findViewById(R.id.mBottomSheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        rvPlaylist = findViewById(R.id.rvPlaylist);
        ivPlayAndPause = findViewById(R.id.ivPlayAndPause);
        ivShuffle = findViewById(R.id.ivShuffle);
        ivLooping = findViewById(R.id.ivLooping);
        ivSkipToNext = findViewById(R.id.ivSkipToNext);
        ivSkipToPrevious = findViewById(R.id.ivSkipToPrevious);
        btnFourthSong = findViewById(R.id.btnFourthSong);
        seekBar3 = findViewById(R.id.seekBar3);
        txtCurrentTime = findViewById(R.id.txtCurrentTime);
        txtEndTime = findViewById(R.id.txtEndTime);
    }

    private void releaseMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            btnPlayAndPause2.setImageResource(R.drawable.ic_play_arrow);
            Toast.makeText(this, "Media is released!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateTimeTextView(String currentTime) {
        txtCurrentTime.setText(currentTime);
    }

    @Override
    public void setEndTime(int seekbarTime,String endTime) {
        txtEndTime.setText(endTime);
        seekBar3.setMax(seekbarTime);
        ivPlayAndPause.setClickable(true);
        ivPlayAndPause.setImageResource(R.drawable.ic_pause);
    }

    @Override
    public void updateSeekBar(int progress) {
        seekBar3.setProgress(progress);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        myHandler.removeCallbacks(UpdateSongTime);
        releaseMedia();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myHandler.removeCallbacks(UpdateSongTime);
        releaseMedia();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            FirebaseAuth.getInstance().signOut();
        }
        adapter.releaseMedia();
    }
}
