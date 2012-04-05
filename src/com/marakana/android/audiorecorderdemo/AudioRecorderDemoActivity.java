
package com.marakana.android.audiorecorderdemo;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class AudioRecorderDemoActivity extends Activity implements OnCompletionListener {
    private static final String TAG = "AudioRecorderDemoActivity";

    private static final String OUT_FILE_NAME = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/audio-recorder-output.m4a";

    private ImageButton recordButton;

    private ImageButton stopButton;

    private ImageButton playButton;

    private ImageButton deleteButton;

    private MediaRecorder mediaRecorder = null;

    private MediaPlayer mediaPlayer = null;

    private File file;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        super.setContentView(R.layout.main);
        this.recordButton = (ImageButton)super.findViewById(R.id.recordButton);
        this.stopButton = (ImageButton)super.findViewById(R.id.stopButton);
        this.playButton = (ImageButton)super.findViewById(R.id.playButton);
        this.deleteButton = (ImageButton)super.findViewById(R.id.deleteButton);
        this.file = new File(OUT_FILE_NAME);
        this.setButtonsEnabled(true, false, this.file.exists());
    }

    private void setButtonsEnabled(boolean record, boolean stop, boolean playAndDelete) {
        this.recordButton.setEnabled(record);
        this.stopButton.setEnabled(stop);
        this.playButton.setEnabled(playAndDelete);
        this.deleteButton.setEnabled(playAndDelete);
    }

    public void record(View v) {
        Log.d(TAG, "record");
        this.mediaRecorder = new MediaRecorder();
        this.mediaRecorder.setAudioChannels(1);
        this.mediaRecorder.setAudioSamplingRate(44100);
        this.mediaRecorder.setAudioEncodingBitRate(64000);
        this.mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        this.mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        this.mediaRecorder.setOutputFile(this.file.getAbsolutePath());
        this.mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        try {
            this.mediaRecorder.prepare();
            this.mediaRecorder.start();

            // update the buttons
            this.setButtonsEnabled(false, true, false);
        } catch (IOException e) {
            Log.e(TAG, "Failed to record()", e);
        }
    }

    public void play(View v) {
        Log.d(TAG, "play()");
        if (this.file.exists()) {
            this.mediaPlayer = new MediaPlayer();
            try {
                this.mediaPlayer.setDataSource(this.file.getAbsolutePath());
                this.mediaPlayer.prepare();
                this.mediaPlayer.setOnCompletionListener(this);
                this.mediaPlayer.start();

                // update the buttons
                this.setButtonsEnabled(false, true, false);
            } catch (IOException e) {
                Log.e(TAG, "Failed to play()", e);
            }
        } else {
            this.playButton.setEnabled(false);
        }
    }

    public void stop(View v) {
        Log.d(TAG, "stop()");
        if (this.mediaPlayer != null) {
            // stop/release the media player
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        } else if (this.mediaRecorder != null) {
            // stop/release the media recorder
            this.mediaRecorder.stop();
            this.mediaRecorder.release();
            this.mediaRecorder = null;
        }
        // update the buttons
        this.setButtonsEnabled(true, false, this.file.exists());
    }

    public void delete(View v) {
        Log.d(TAG, "delete()");
        this.file.delete();
        // update the buttons
        this.setButtonsEnabled(true, false, this.file.exists());
    }

    @Override
    public void onPause() {
        super.onPause();
        this.stop(null);
    }

    // called when the playback is done
    public void onCompletion(MediaPlayer mp) {
        this.stop(null);
    }
}
