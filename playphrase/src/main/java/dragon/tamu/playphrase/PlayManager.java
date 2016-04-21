package dragon.tamu.playphrase;

import android.media.MediaPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PlayManager implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private MediaPlayer mp;
    private boolean repeat, paused;
    private int curPosition;
    private List<String> phraseFiles;
    private MainActivity.OnPausePlayClickListener listener;
    private MainActivity.OnStopPlayClickListener stopListener;

    public PlayManager() {
        repeat = false;
        paused = false;
    }

    public boolean toggleRepeat() {
        repeat = !repeat;
        return repeat;
    }

    public void playPhrase(Phrase p, ArrayList<String> languages) {

        if (mp != null) {
            mp.stop();
            mp.reset();
            if (paused && listener != null)
                listener.OnPausePlayClick();
        } else {
            if (listener != null)
                listener.OnPausePlayClick();
            mp = new MediaPlayer();
            mp.setOnPreparedListener(this);
            mp.setOnCompletionListener(this);
        }
        phraseFiles = new LinkedList<>();
        if (languages.isEmpty()) {
            for (String s : p.phraseLanguages.keySet()) {
                phraseFiles.add(p.phraseLanguages.get(s));
            }
        } else {
            for (int i = 0; i < languages.size(); i++) {
                if (p.phraseLanguages.containsKey(languages.get(i)))
                    phraseFiles.add(p.phraseLanguages.get(languages.get(i)));
            }
        }
        curPosition = 0;
        playQueue();

    }

    public boolean isPlaying() {
        return mp != null && mp.isPlaying();
    }

    public void stopPhrase() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
            if (listener != null && !paused)
                listener.OnPausePlayClick();
            paused = false;

        }
        if (stopListener != null) {
            stopListener.onStopPlayClick();
        }
    }

    public void setOnPausePlayClickListener(MainActivity.OnPausePlayClickListener listener) {
        this.listener = listener;
    }

    public void setOnStopPlayClickListener(MainActivity.OnStopPlayClickListener listener) {
        stopListener = listener;
    }

    private void playQueue() {
        paused = false;
        if (curPosition >= phraseFiles.size() && !repeat) {
            stopPhrase();
        } else {
            // mp.release();
            try {
                mp.setDataSource(phraseFiles.get(curPosition));
                mp.prepare();
                mp.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            curPosition++;
        }
    }

    public void pausePlayer() {
        if (mp != null && !paused && mp.isPlaying()) {
            paused = true;
            mp.pause();
            if (listener != null)
                listener.OnPausePlayClick();
        }
    }

    public void resumePlayer() {
        if (paused) {
            paused = false;
            mp.start();
        } else if (mp != null) {
            curPosition = 0;
            mp.reset();
            playQueue();
        } else {
            curPosition = 0;
            mp = new MediaPlayer();
            mp.setOnPreparedListener(this);
            mp.setOnCompletionListener(this);
            playQueue();
        }
        if (listener != null)
            listener.OnPausePlayClick();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mp != null) {
            mp.stop();
            mp.reset();
            paused = false;
        }
        if (curPosition >= phraseFiles.size() && repeat) {
            curPosition = 0;
        }
        playQueue();
    }
}