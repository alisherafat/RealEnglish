package ir.realenglish.app.custom;

import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ir.realenglish.app.R;


public class AudioPlayer extends LinearLayout {
    public static final int READY = 1;
    public static final int DOWNLOADING = 2;
    private MediaPlayer mediaPlayer;
    private Handler myHandler = new Handler();
    private SeekBar seekbar;
    private TextView txtTime;
    private View playerView;
    private FloatingActionButton fabToggle;
    private String totalTime;
    int finalTime, startTime, iVolume, status;
    private boolean isPrepared, isPlaying;
    private OnClickListener togglePlayClickListerner;

    private boolean isFinished = false;

    public void setRemoteUrl(String url) throws IOException {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.reset();
        mediaPlayer.setDataSource(url);
        mediaPlayer.prepareAsync();
    }

    public void setLocalURI(Uri uri) throws IOException {
        mediaPlayer.setDataSource(getRealPathFromURI(getContext(), uri));
        mediaPlayer.prepare();
    }

    public void setLocalPath(String path) throws IOException {
        if (mediaPlayer == null) throw new NullPointerException();
        mediaPlayer.reset();
        mediaPlayer.setDataSource(path);
        mediaPlayer.prepare();
    }

    public void attachMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
        iniiMedaPlayer();
    }

    private void iniiMedaPlayer() {
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                finalTime = mediaPlayer.getDuration();
                totalTime = miliToReadable(finalTime);
                txtTime.setText(totalTime);
                seekbar.setMax(finalTime);
                // seekbar.setProgress(0);
                seekbar.setClickable(true);
                isPrepared = true;
                status = READY;
                if (seekbar.getProgress() > 0) {
                    mediaPlayer.seekTo(seekbar.getProgress());
                }
                togglePlayPause();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                isFinished = true;
                txtTime.setText(totalTime);
                seekbar.setProgress(0);
                fabToggle.setImageResource(R.mipmap.ic_action_playback_play);
            }
        });
    }

    public void setOnTogglePlayClickListener(OnClickListener listener) {
        this.togglePlayClickListerner = listener;
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    public void togglePlayPause() {
        if (!isPrepared) {
            txtTime.setText("Please wait...");
            return;
        }
        if (this.status != READY) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            // pause(500);
            fabToggle.setImageResource(R.mipmap.ic_action_playback_play);
        } else {
            isFinished = false;
            // play(100);
            mediaPlayer.start();
            isPlaying = true;
            fabToggle.setImageResource(R.mipmap.ic_action_playback_pause);
            handelPlayingMode();
        }
    }

    public void setStatus(int status) {
        this.status = status;
        fabToggle.setIndeterminate(false);
        switch (status) {
            case DOWNLOADING:
                fabToggle.setIndeterminate(true);
                break;
        }

    }


    private String miliToReadable(int miliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(miliseconds),
                TimeUnit.MILLISECONDS.toSeconds(miliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(miliseconds))
        );
    }

    private void handelPlayingMode() {
        if (isFinished) {
            return;
        }
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            startTime = mediaPlayer.getCurrentPosition();
            txtTime.setText(String.format("%s / %s", miliToReadable(startTime), totalTime));

            seekbar.setProgress(startTime);
            if (mediaPlayer != null && mediaPlayer.isPlaying())
                myHandler.postDelayed(UpdateSongTime, 200);
        }
    }


    public AudioPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public AudioPlayer(Context context) {
        super(context);
        initialize(context);
    }

    private void initialize(final Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        playerView = inflater.inflate(R.layout.stream_audio, this);
        final LinearLayout lytContent = (LinearLayout) playerView.findViewById(R.id.lytContent);
        fabToggle = (FloatingActionButton) lytContent.findViewById(R.id.myAudioPlayerFabTogglePlay);
        seekbar = (SeekBar) lytContent.findViewById(R.id.seekBar);
        txtTime = (TextView) lytContent.findViewById(R.id.txtTime);


        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (isPlaying)
                        mediaPlayer.seekTo(progress);
                    else seekBar.setProgress(progress);
                }
            }
        });
        seekbar.setClickable(false);
        fabToggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (togglePlayClickListerner != null) {
                    togglePlayClickListerner.onClick(v);
                }
            }
        });

    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            handelPlayingMode();
        }
    };

}