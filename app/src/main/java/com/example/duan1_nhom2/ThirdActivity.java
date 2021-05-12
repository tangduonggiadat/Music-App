package com.example.duan1_nhom2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class ThirdActivity extends AppCompatActivity implements DialogAddSong.ProgressBarListener{
    Button btnChooseFile, btnBack, btnUpload;
    ImageView btnPlayAndPause;
    TextView txtStartDuration, txtEndDuration;
    EditText txtFileName;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    Handler myHandler = new Handler();
    ProgressBar progressBar;
    DialogAddSong dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        findView();
        seekBar.setClickable(false);
        btnPlayAndPause.setClickable(false);
        btnChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseAudioFile();
            }
        });
        btnPlayAndPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeAndPauseAudio();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null){
                    dialog.setListener(ThirdActivity.this);
                    dialog.show(getSupportFragmentManager(), "Tag");
                }else {
                    Toast.makeText(ThirdActivity.this, "Dialog is null!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void findView(){
        btnUpload = findViewById(R.id.btnUpload);
        btnBack = findViewById(R.id.btnBack);
        btnChooseFile = findViewById(R.id.btnChooseFile);
        btnPlayAndPause = findViewById(R.id.btnPlayAndPause);
        txtStartDuration = findViewById(R.id.txtStartDuration);
        txtEndDuration = findViewById(R.id.txtEndDuration);
        txtFileName = findViewById(R.id.txtFileName);
        seekBar = findViewById(R.id.seekBar);
        progressBar = findViewById(R.id.progressBar);
    }
    private void chooseAudioFile(){
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            final Uri uri = data.getData();
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer = MediaPlayer.create(this, uri);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                releaseMedia();
                            }
                        });
                        txtEndDuration.setText(changeTimeFormat(mediaPlayer.getDuration()));
                        txtFileName.setText(getFileName(uri));
                        seekBar.setMax(mediaPlayer.getDuration());
                        seekBar.setProgress(0);
                        btnPlayAndPause.setClickable(true);
                        dialog = new DialogAddSong(uri, changeTimeFormat(mediaPlayer.getDuration()), getFileName(uri));
                    }
                });
                mediaPlayer.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    private void releaseMedia(){
        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            btnPlayAndPause.setImageResource(R.drawable.ic_play_arrow);
            Toast.makeText(this, "Media is released!", Toast.LENGTH_SHORT).show();
        }
    }
    private void resumeAndPauseAudio(){
        if (mediaPlayer != null){
            if (mediaPlayer.isPlaying()){
                btnPlayAndPause.setImageResource(R.drawable.ic_play_arrow);
                mediaPlayer.pause();
            }else {
                btnPlayAndPause.setImageResource(R.drawable.ic_pause);
                mediaPlayer.start();
                myHandler.postDelayed(UpdateSongTime, 100);
            }
        }
    }
    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            int currentTime = mediaPlayer.getCurrentPosition();
            seekBar.setProgress(currentTime);
            myHandler.postDelayed(this, 100);
        }
    };
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    private String changeTimeFormat(int endDuration){
        int totalMin = Math.abs((endDuration/60000));
        int totalSecond = (endDuration%60000)/1000;
        if (totalSecond<10){
            return totalMin + ":0" + totalSecond;
        }else {
            return totalMin + ":" + totalSecond;
        }
    }

    @Override
    public void updateProgressBar(int progress) {
        progressBar.setProgress(progress);
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
}
