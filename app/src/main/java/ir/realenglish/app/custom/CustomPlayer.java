package ir.realenglish.app.custom;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import ir.realenglish.app.R;
import ir.realenglish.app.utils.Utils;


public class CustomPlayer extends LinearLayout {

    private Button btnClose;
    private MediaPlayer mediaPlayer;
    //private double finalTime = 0;
    int finalTime, startTime, iVolume;
    private Handler myHandler = new Handler();
    public SeekBar seekbar;
    private TextView txtCurrentTime, txtTotalTime, txtMusicName;
    public View playerView;
    public ImageView imgPlayPause;
    private boolean isPrepared;
    private boolean isFinished = false;
    private String totalTime;


    private final static int INT_VOLUME_MAX = 100;
    private final static int INT_VOLUME_MIN = 0;
    private final static float FLOAT_VOLUME_MAX = 1;
    private final static float FLOAT_VOLUME_MIN = 0;

    public void setPath(String path, String name) {
        isPrepared = false;
        iniiMedaPlayer();
        setupViews();
        mediaPlayer.reset();
        try {
            txtMusicName.setText(name);
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPause() {
        if (mediaPlayer == null) return;
        if (isPrepared && mediaPlayer.isPlaying()) {
            togglePlayPause();
        }
    }

    public void onDestroy() {
        release();
    }

    private void iniiMedaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isPrepared = true;
                finalTime = mediaPlayer.getDuration();
                totalTime = miliToReadable(finalTime);
                txtTotalTime.setText(totalTime);
                seekbar.setMax(finalTime);
                seekbar.setClickable(true);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                isFinished = true;
                seekbar.setProgress(0);
                imgPlayPause.setImageResource(R.mipmap.play);
                txtCurrentTime.setText(miliToReadable(0));
            }
        });
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public CustomPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializer(context);
    }

    public CustomPlayer(Context context) {
        super(context);
        initializer(context);
    }

    private void initializer(final Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        playerView = inflater.inflate(R.layout.player, this);
        final LinearLayout lytContent = (LinearLayout) playerView.findViewById(R.id.lytContent);
        btnClose = (Button) lytContent.findViewById(R.id.btnclose);
        imgPlayPause = (ImageView) lytContent.findViewById(R.id.imgPlayPause);
        txtCurrentTime = (TextView) lytContent.findViewById(R.id.txtCurrentTime);
        txtTotalTime = (TextView) lytContent.findViewById(R.id.txtTotalTime);
        txtMusicName = (TextView) lytContent.findViewById(R.id.txtMusicName);
        seekbar = (SeekBar) lytContent.findViewById(R.id.seekBar);

        setupViews();
    }

    private void setupViews() {
        txtCurrentTime.setText(miliToReadable(0));
        seekbar.setProgress(0);
        imgPlayPause.setImageResource(R.mipmap.play);
        seekbar.setClickable(false);
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
                    mediaPlayer.seekTo(progress);
                }
            }
        });

        imgPlayPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });

        btnClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                playerView.setVisibility(GONE);
                mediaPlayer.release();
                mediaPlayer = null;
            }
        });
    }

    private void handelPlayingMode() {
        if (isFinished) {
            return;
        }

        if (mediaPlayer == null) return;

        if (mediaPlayer.isPlaying()) {
            startTime = mediaPlayer.getCurrentPosition();
            txtCurrentTime.setText(String.format("%s", miliToReadable(startTime)));

            seekbar.setProgress(startTime);
            myHandler.postDelayed(UpdateSongTime, 200);
        }
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            handelPlayingMode();
        }
    };

    public void togglePlayPause() {
        if (!isPrepared) {
            Utils.toast("Please wait...");
            return;
        }

        if (mediaPlayer.isPlaying()) {
            imgPlayPause.setImageResource(R.mipmap.play);
            pause(550);
        } else {
            imgPlayPause.setImageResource(R.mipmap.pause);
            play(150);
            isFinished = false;
            handelPlayingMode();
        }
    }

    private String miliToReadable(int miliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(miliseconds),
                TimeUnit.MILLISECONDS.toSeconds(miliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(miliseconds))
        );
    }

    public void play(int fadeDuration) {
        if (mediaPlayer == null) return;


        if (fadeDuration > 0)
            iVolume = INT_VOLUME_MIN;
        else
            iVolume = INT_VOLUME_MAX;
        updateVolume(0);

        if (!mediaPlayer.isPlaying()) mediaPlayer.start();

        if (fadeDuration > 0) {
            final Timer timer = new Timer(true);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    updateVolume(1);
                    if (iVolume == INT_VOLUME_MAX) {
                        timer.cancel();
                        timer.purge();
                    }
                }
            };

            int delay = fadeDuration / INT_VOLUME_MAX;
            if (delay == 0) delay = 1;

            timer.schedule(timerTask, delay, delay);
        }
    }

    public void pause(int fadeDuration) {
        if (mediaPlayer == null) return;

        if (fadeDuration > 0)
            iVolume = INT_VOLUME_MAX;
        else
            iVolume = INT_VOLUME_MIN;

        updateVolume(0);

        if (fadeDuration > 0) {
            final Timer timer = new Timer(true);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    updateVolume(-1);
                    if (iVolume == INT_VOLUME_MIN) {
                        //Pause music
                        if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                        timer.cancel();
                        timer.purge();
                    }
                }
            };

            int delay = fadeDuration / INT_VOLUME_MAX;
            if (delay == 0) delay = 1;

            timer.schedule(timerTask, delay, delay);
        }
    }

    private void updateVolume(int change) {
        if (mediaPlayer == null) return;
        iVolume = iVolume + change;

        if (iVolume < INT_VOLUME_MIN)
            iVolume = INT_VOLUME_MIN;
        else if (iVolume > INT_VOLUME_MAX)
            iVolume = INT_VOLUME_MAX;

        float fVolume = 1 - ((float) Math.log(INT_VOLUME_MAX - iVolume) / (float) Math.log(INT_VOLUME_MAX));

        if (fVolume < FLOAT_VOLUME_MIN)
            fVolume = FLOAT_VOLUME_MIN;
        else if (fVolume > FLOAT_VOLUME_MAX)
            fVolume = FLOAT_VOLUME_MAX;

        mediaPlayer.setVolume(fVolume, fVolume);
    }

}