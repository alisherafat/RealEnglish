package ir.realenglish.app.custom;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import ir.realenglish.app.R;
import ir.realenglish.app.app.Config;
import ir.realenglish.app.utils.Utils;

public class RecordFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LOG_TAG = "myapp";

    private FloatingActionButton mRecordButton = null;

    private TextView mRecordingPrompt;
    private int mRecordPromptCount = 0;

    private boolean mustStart = true;

    private Chronometer mChronometer = null;
    long timeWhenPaused = 0; //stores time when user clicks pause button

    private String file = null;
    private String mFilePath = null;

    private MediaRecorder recorder = null;


    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;
    private static final SimpleDateFormat mTimerFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

    private OnRecordFinishListener listener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Record_Fragment.
     */
    public RecordFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View recordView = inflater.inflate(R.layout.fragment_record, container, false);

        mChronometer = (Chronometer) recordView.findViewById(R.id.chronometer);
        //update recording prompt text
        mRecordingPrompt = (TextView) recordView.findViewById(R.id.recording_status_text);

        mRecordButton = (FloatingActionButton) recordView.findViewById(R.id.btnRecord);
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(mustStart);
                mustStart = !mustStart;
            }
        });

        return recordView;
    }

    @Override
    public void onDestroy() {
        if (recorder != null) {
            stopRecording();
        }
        super.onDestroy();
    }

    // Recording Start/Stop
    private void onRecord(boolean start) {
        if (start) {
            // start recording
            mRecordButton.setImageResource(R.drawable.ic_media_stop);
            startRecording();
            //start Chronometer
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
            mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if (mRecordPromptCount == 0) {
                        mRecordingPrompt.setText("Recording.");
                    } else if (mRecordPromptCount == 1) {
                        mRecordingPrompt.setText("Recording..");
                    } else if (mRecordPromptCount == 2) {
                        mRecordingPrompt.setText("Recording...");
                        mRecordPromptCount = -1;
                    }
                    mRecordPromptCount++;
                }
            });
            //keep screen on while recording
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            mRecordingPrompt.setText("Recording.");
            mRecordPromptCount++;
        } else {
            //stop recording
            mRecordButton.setImageResource(R.drawable.ic_mic_white_36dp);
            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPaused = 0;
            mRecordingPrompt.setText(getString(R.string.record_prompt));
            stopRecording();
            //allow the screen to turn off again once recording is finished
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    public void startRecording() {

        file = Config.DIR_RECORD + File.separator + Utils.generateRandomString(6) + ".mp3";

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(file);
        try {
            if (recorder == null) {
                Utils.log("null");
                return;
            }
            recorder.prepare();
            recorder.start();
            Utils.log("started");
            mStartingTimeMillis = System.currentTimeMillis();
        } catch (Exception e) {
            Log.e(LOG_TAG, "prepare() failed");
            exitWithFailure();
        }
    }

    public void stopRecording() {
        mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
        try {
            Utils.log("stopped");
            recorder.stop();
            recorder.release();
            recorder = null;
            listener.onRecordFinish(file);
        } catch (Exception e) {
            exitWithFailure();
        }
    }

    private void exitWithFailure() {
        new File(file).delete();
        listener.onRecordFinish(null);
    }

    public interface OnRecordFinishListener {
        void onRecordFinish(String path);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RecordFragment.OnRecordFinishListener) {
            listener = (RecordFragment.OnRecordFinishListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecordFinishListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}