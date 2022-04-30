package com.federicoberon.alarme.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

public class CustomMediaPlayer extends MediaPlayer  {
    private static CustomMediaPlayer Instance;
    MediaPlayer mediaPlayer;
    private Context mContext;
    private String mUri;
    private int mVolume;

    private CustomMediaPlayer(){
        super();
    }


    public static CustomMediaPlayer getMediaPlayerInstance() {
        if (Instance == null) {
            return Instance = new CustomMediaPlayer();
        }
        return Instance;
    }

    public void playAudioFile() {
        playAudioFile(mContext, mUri, mVolume);
    }

    public void playAudioFile(Context context, String uri, int volume) {
        this.mContext = context;
        this.mUri = uri;
        this.mVolume = volume;
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.reset();
            mediaPlayer.setWakeMode(context, AudioManager.MODE_RINGTONE);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.setDataSource(context, Uri.parse(uri));
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
        } catch (IOException e) {
            Log.w("MIO", "<<< ERROR 1 >>>");
            e.printStackTrace();
            Uri defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context
                    , RingtoneManager.TYPE_RINGTONE);
            playAudioFile(context, defaultRingtoneUri.toString(), volume);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopAudioFile() {
        if (mediaPlayer != null) {
            while(mediaPlayer.isPlaying())
                mediaPlayer.stop();
        }
    }

}
